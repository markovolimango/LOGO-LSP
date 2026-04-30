package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import io.github.markovolimango.logo.parser.AstWalker;
import io.github.markovolimango.logo.parser.Node;
import org.eclipse.lsp4j.Diagnostic;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticProvider {
    public static List<Diagnostic> getDiagnostics(DocumentState state) {
        List<Diagnostic> diagnostics = new ArrayList<>();
        var errors = state.getErrors();
        for (var error : errors)
            diagnostics.add(new Diagnostic(
                    LspConverter.toRange(error.start(), error.end()),
                    error.message()
            ));
        var errorFinder = new ErrorFinder(state);
        errorFinder.walk(state.getAst());
        diagnostics.addAll(errorFinder.getErrors());
        return diagnostics;
    }

    private static class ErrorFinder extends AstWalker {
        List<Diagnostic> errors = new ArrayList<>();
        DocumentState state;

        public ErrorFinder(DocumentState state) {
            this.state = state;
        }

        @Override
        public void walk(Node node) {
            if (node instanceof Node.VarRef(Token name, Pos start, Pos end)) {
                if (state.getSymTable().getVarDef(name.text(), start) == null)
                    errors.add(new Diagnostic(
                            LspConverter.toRange(start, end),
                            "Undefined variable: " + name.text())
                    );
            } else super.walk(node);
        }

        public List<Diagnostic> getErrors() {
            return errors;
        }
    }
}
