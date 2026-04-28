package io.github.markovolimango.logo;

import io.github.markovolimango.logo.lsp.LogoLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

public class Main {
    public static void main(String[] args) {
        LogoLanguageServer server = new LogoLanguageServer();

        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);
        server.connect(launcher.getRemoteProxy());
        launcher.startListening();
    }
}