package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.features.SemanticTokensProvider;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LogoLanguageServer implements LanguageServer, LanguageClientAware {
    private final LogoTextDocumentService textDocumentService = new LogoTextDocumentService();
    private final LogoWorkspaceService workspaceService = new LogoWorkspaceService();
    private LanguageClient client;

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        textDocumentService.setClient(client); // pass it down
    }

    public LanguageClient getClient() {
        return client;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();

        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);

        capabilities.setFoldingRangeProvider(true);

        capabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions(false, false));

        capabilities.setDefinitionProvider(true);
        capabilities.setDeclarationProvider(true);
        capabilities.setReferencesProvider(true);
        capabilities.setRenameProvider(new RenameOptions(true));
        capabilities.setDocumentSymbolProvider(true);

        SemanticTokensLegend legend = new SemanticTokensLegend(
                SemanticTokensProvider.TOKEN_TYPES,
                SemanticTokensProvider.TOKEN_MODIFIERS
        );
        SemanticTokensWithRegistrationOptions options = new SemanticTokensWithRegistrationOptions(legend);
        options.setFull(true);
        capabilities.setSemanticTokensProvider(options);

        CompletionOptions completionOptions = new CompletionOptions();
        completionOptions.setTriggerCharacters(List.of(":"));
        capabilities.setCompletionProvider(completionOptions);

        capabilities.setHoverProvider(true);

        InitializeResult result = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
