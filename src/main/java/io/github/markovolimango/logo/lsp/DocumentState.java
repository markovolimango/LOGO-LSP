package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.analysis.SymbolTable;
import io.github.markovolimango.logo.analysis.SymbolTableBuilder;
import io.github.markovolimango.logo.ast.Node;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.parser.ParseError;
import io.github.markovolimango.logo.parser.Parser;

import java.util.List;

public class DocumentState {
    private final String uri;
    private final String[] lines;
    private final Node.Program ast;
    private final List<ParseError> errors;
    private final SymbolTable symTable;

    public DocumentState(String uri, String text) {
        this.uri = uri;
        this.lines = text.split("\n", -1);
        var tokens = new Lexer(text).tokenize();
        Parser parser = new Parser(tokens);
        this.ast = parser.parseProgram();
        this.errors = parser.getErrors();
        this.symTable = new SymbolTableBuilder().build(ast);
    }

    public String getUri() {
        return uri;
    }

    public String getLine(int line) {
        return lines[line];
    }

    public Node.Program getAst() {
        return ast;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public SymbolTable getSymTable() {
        return symTable;
    }
}
