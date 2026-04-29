package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.features.CompletionProvider;
import io.github.markovolimango.logo.features.DefinitionProvider;
import io.github.markovolimango.logo.features.SemanticTokensProvider;
import io.github.markovolimango.logo.lexer.Pos;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class LogoTextDocumentService implements TextDocumentService {
    private final Map<String, DocumentState> documents = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> pendingReparses = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private LanguageClient client;

    public void setClient(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        documents.put(uri, new DocumentState(uri, text));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getContentChanges().getFirst().getText();

        ScheduledFuture<?> pending = pendingReparses.get(uri);
        if (pending != null) pending.cancel(true);
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            documents.put(uri, new DocumentState(uri, text));
            System.err.println("mare");
        }, 150, TimeUnit.MILLISECONDS);
        pendingReparses.put(uri, future);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
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
        return CompletableFuture.supplyAsync(() -> {
            return Either.forLeft(CompletionProvider.getCompletion(state, cursor));
        });
    }
}
