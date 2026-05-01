package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.analysis.DocumentSymbolProvider;
import io.github.markovolimango.logo.features.*;
import io.github.markovolimango.logo.lexer.Pos;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.messages.Either3;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class LogoTextDocumentService implements TextDocumentService {
    private final Map<String, DocumentState> documents = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> pendingReparses = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private LanguageClient client;

    public void setClient(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        var state = new DocumentState(uri, text);
        documents.put(uri, state);
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, DiagnosticProvider.getDiagnostics(state)));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getContentChanges().getFirst().getText();
        documents.get(uri).setText(text);

        ScheduledFuture<?> pending = pendingReparses.get(uri);
        if (pending != null) pending.cancel(true);
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            var state = new DocumentState(uri, text);
            documents.put(uri, state);
            client.publishDiagnostics(new PublishDiagnosticsParams(uri, DiagnosticProvider.getDiagnostics(state)));
        }, 150, TimeUnit.MILLISECONDS);
        pendingReparses.put(uri, future);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documents.remove(uri);
        ScheduledFuture<?> pending = pendingReparses.remove(uri);
        if (pending != null) pending.cancel(true);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        DocumentState state = documents.get(params.getTextDocument().getUri());
        if (state == null)
            return CompletableFuture.completedFuture(new SemanticTokens(List.of()));
        return CompletableFuture.supplyAsync(() -> SemanticTokensProvider.getSemanticTokens(state));
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        String uri = params.getTextDocument().getUri();
        Pos cursor = LspConverter.fromPosition(params.getPosition());
        DocumentState state = documents.get(uri);
        if (state == null) return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        return CompletableFuture.supplyAsync(() -> {
            Location loc = DefinitionProvider.findDefinition(state, cursor);
            if (loc == null) return Either.forLeft(List.of());
            return Either.forLeft(List.of(loc));
        });
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> declaration(DeclarationParams params) {
        String uri = params.getTextDocument().getUri();
        Pos cursor = LspConverter.fromPosition(params.getPosition());
        DocumentState state = documents.get(uri);
        if (state == null) return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        return CompletableFuture.supplyAsync(() -> {
            Location loc = DefinitionProvider.findDefinition(state, cursor);
            if (loc == null) return Either.forLeft(List.of());
            return Either.forLeft(List.of(loc));
        });
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        String uri = params.getTextDocument().getUri();
        DocumentState state = documents.get(uri);
        Pos cursor = LspConverter.fromPosition(params.getPosition());
        return CompletableFuture.supplyAsync(() -> Either.forLeft(CompletionProvider.getCompletion(state, cursor)));
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        DocumentState state = documents.get(params.getTextDocument().getUri());
        if (state == null) {
            return CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(List.of())));
        }
        return CompletableFuture.supplyAsync(() -> {
            List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);
            return new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(diagnostics));
        });
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        var state = documents.get(params.getTextDocument().getUri());
        var cursor = LspConverter.fromPosition(params.getPosition());
        if (state == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> ReferencesProvider.findReferences(state, cursor));
    }

    @Override
    public CompletableFuture<Either3<Range, PrepareRenameResult, PrepareRenameDefaultBehavior>> prepareRename(PrepareRenameParams params) {
        String uri = params.getTextDocument().getUri();
        Pos cursor = LspConverter.fromPosition(params.getPosition());
        DocumentState state = documents.get(uri);
        if (state == null) return null;
        return CompletableFuture.supplyAsync(() -> {
            Location loc = DefinitionProvider.findDefinition(state, cursor);
            if (loc == null) return null;
            return Either3.forFirst(loc.getRange());
        });
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        var state = documents.get(params.getTextDocument().getUri());
        var cursor = LspConverter.fromPosition(params.getPosition());
        if (state == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> {
            var edits = RenameProvider.getRenameEdits(state, cursor, params.getNewName());
            Map<String, List<TextEdit>> editMap = new HashMap<>();
            editMap.put(params.getTextDocument().getUri(), edits);
            return new WorkspaceEdit(editMap);
        });
    }

    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(DocumentSymbolParams params) {
        var syms = DocumentSymbolProvider.getSymbols(documents.get(params.getTextDocument().getUri()));
        return CompletableFuture.supplyAsync(() -> {
            var res = new ArrayList<Either<SymbolInformation, DocumentSymbol>>();
            for (var sym : syms)
                res.add(Either.forRight(sym));
            return res;
        });
    }

    @Override
    public CompletableFuture<List<FoldingRange>> foldingRange(FoldingRangeRequestParams params) {
        return CompletableFuture.supplyAsync(() -> FoldingRangeProvider.findFoldingRanges(documents.get(params.getTextDocument().getUri())));
    }
}
