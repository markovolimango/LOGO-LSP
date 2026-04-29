package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.analysis.Symbol;
import io.github.markovolimango.logo.analysis.features.DefinitionProvider;
import io.github.markovolimango.logo.analysis.features.SemanticTokensProvider;
import io.github.markovolimango.logo.lexer.Pos;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class LogoTextDocumentService implements TextDocumentService {
    private final Map<String, DocumentState> documents = new ConcurrentHashMap<>();
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
        documents.put(uri, new DocumentState(uri, text));
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
        if (state == null) return CompletableFuture.completedFuture(new SemanticTokens(List.of()));

        return CompletableFuture.supplyAsync(() ->
                LspConverter.toSemanticTokens(SemanticTokensProvider.getTokens(state))
        );
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        String uri = params.getTextDocument().getUri();
        Pos cursor = LspConverter.fromPosition(params.getPosition());
        DocumentState state = documents.get(uri);
        if (state == null) return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        return CompletableFuture.supplyAsync(() -> {
            Symbol sym = DefinitionProvider.findDefinition(state, cursor);
            if (sym == null) return Either.forLeft(List.of());
            Location loc = new Location(uri, LspConverter.toRange(sym.start(), sym.end()));
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
            Symbol sym = DefinitionProvider.findDefinition(state, cursor);
            if (sym == null) return Either.forLeft(List.of());
            Location loc = new Location(uri, LspConverter.toRange(sym.start(), sym.end()));
            return Either.forLeft(List.of(loc));
        });
    }
}
