package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.analysis.Symbol;
import io.github.markovolimango.logo.analysis.SymbolTable;
import io.github.markovolimango.logo.analysis.SymbolTableBuilder;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.ast.Node;
import io.github.markovolimango.logo.parser.Parser;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LogoTextDocumentService implements TextDocumentService {
    private final Map<String, String> documents = new HashMap<>();
    private LanguageClient client;
    private Node.Program ast;
    private SymbolTable symTable;

    public void setClient(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();

        documents.put(uri, text);
        validate(uri, text);

        var tokens = new Lexer(text).tokenize();
        ast = new Parser(tokens).parseProgram();
        symTable = new SymbolTableBuilder().build(ast);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();

        String text = params.getContentChanges().getFirst().getText();

        documents.put(uri, text);
        validate(uri, text);

        var tokens = new Lexer(text).tokenize();
        ast = new Parser(tokens).parseProgram();
        symTable = new SymbolTableBuilder().build(ast);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        return CompletableFuture.supplyAsync(() -> {
            String uri = params.getTextDocument().getUri();
            Position position = params.getPosition();
            Pos startPos = new Pos(0, position.getLine(), position.getCharacter());

            String text = documents.get(uri);
            if (text == null) return null;

            String[] lines = text.split("\n");
            if (position.getLine() >= lines.length) return null;

            String line = lines[position.getLine()];
            int col = position.getCharacter();

            // Simple regex or boundary check to find the word start/end
            // This is a naive implementation; adjust based on your language's syntax
            int start = col;
            while (start > 0 && Lexer.isNotDelimiter(line.charAt(start - 1)) && line.charAt(start - 1) != ':')
                start--;

            int end = col;
            while (end < line.length() && Lexer.isNotDelimiter(line.charAt(end)))
                end++;

            boolean isVar = start - 1 >= 0 && line.charAt(start - 1) == ':';

            String name = (start == end) ? null : line.substring(start, end);

            if (name == null) return Either.forLeft(new ArrayList<>());
            Symbol symbol = isVar ? symTable.getVarDef(name, startPos) : symTable.getProcDef(name, startPos);
            if (symbol != null) {
                Range range = new Range(new Position(symbol.start().line(), symbol.start().col()), new Position(symbol.end().line(), symbol.end().col()));
                return Either.forLeft(List.of(new Location(uri, range)));
            }

            return Either.forLeft(List.of());
        });
    }

    private void validate(String uri, String text) {
        List<Diagnostic> diagnostics = new ArrayList<>();
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
    }
}
