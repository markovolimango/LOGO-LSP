package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.LogoLanguage;
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
        var errorFinder = new UndefinedRefFinder(state);
        errorFinder.walk(state.getAst());
        diagnostics.addAll(errorFinder.getErrors());
        return diagnostics;
    }

    private static class UndefinedRefFinder extends AstWalker {
        private final List<Diagnostic> errors = new ArrayList<>();
        private final DocumentState state;
        private boolean ignoreProcErrors = false;


        public UndefinedRefFinder(DocumentState state) {
            this.state = state;
        }

        @Override
        public void walk(Node node) {
            switch (node) {
                case Node.VarRef(Token name, Pos start, Pos end) -> {
                    if (state.getSymTable().getVarDef(name.text(), start) == null)
                        errors.add(new Diagnostic(
                                LspConverter.toRange(start, end),
                                "Undefined variable: " + name.text())
                        );
                }
                case Node.ProcCall pc -> {
                    var sym = state.getSymTable().getProcDef(pc.name().text(), pc.name().start());
                    boolean isBuiltin = LogoLanguage.isBuiltin(pc.name().text());
                    if (sym == null && !isBuiltin && !ignoreProcErrors)
                        errors.add(new Diagnostic(
                                LspConverter.toRange(pc.name().start(), pc.name().end()),
                                "Undefined procedure: " + pc.name().text())
                        );
                }
                case Node.DefineStmt ds -> {
                    walk(ds.name());
                    ignoreProcErrors = true;
                    for (var param : ds.params())
                        walk(param);
                    ignoreProcErrors = false;
                    walk(ds.body());
                }
                default -> super.walk(node);
            }
        }

        public List<Diagnostic> getErrors() {
            return errors;
        }
    }
}
