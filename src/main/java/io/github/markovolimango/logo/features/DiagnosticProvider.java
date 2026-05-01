package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.LogoLanguage;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import io.github.markovolimango.logo.parser.AstWalker;
import io.github.markovolimango.logo.parser.Node;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

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
        var checker = new ReturnValueChecker();
        checker.walk(state.getAst());
        diagnostics.addAll(checker.getErrors());
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

    public static class ReturnValueChecker {
        public List<Diagnostic> errors = new ArrayList<>();

        public List<Diagnostic> getErrors() {
            return errors;
        }

        public void walk(Node.Program program) {
            walk(program, Context.NEUTRAL);
        }

        private void walk(Node node, Context ctx) {
            switch (node) {
                case Node.Program p -> p.body().forEach(n -> walk(n, Context.STMT));

                case Node.ToStmt d -> d.body().forEach(n -> walk(n, Context.STMT));

                case Node.DefineStmt d -> {
                    if (ctx == Context.EXPR) {
                        error(d, "Expected expression");
                    }
                    walk(d.name(), Context.EXPR);
                    d.params().forEach(n -> walk(n, Context.EXPR));
                    walk(d.body(), Context.STMT);
                }

                case Node.ProcCall c -> {
                    var returns = LogoLanguage.getReturns(c.name().text());

                    if (ctx == Context.STMT && returns == LogoLanguage.Returns.VALUE) {
                        error(c, "Unused return value");
                    }
                    if (ctx == Context.EXPR && returns == LogoLanguage.Returns.VOID) {
                        error(c, "Expected expression");
                    }

                    c.args().forEach(n -> walk(n, Context.EXPR));
                }

                case Node.MakeStmt m -> {
                    if (ctx == Context.EXPR) error(m, "Expected expression");
                    walk(m.name(), Context.EXPR);
                    walk(m.value(), Context.EXPR);
                }

                case Node.LocalMakeStmt l -> {
                    if (ctx == Context.EXPR) error(l, "Expected expression");
                    walk(l.name(), Context.EXPR);
                    walk(l.value(), Context.EXPR);
                }

                case Node.OutputStmt o -> {
                    if (ctx == Context.EXPR) error(o, "Expected expression");
                    walk(o.value(), Context.EXPR);
                }

                case Node.InfixExpr e -> {
                    if (ctx == Context.STMT) error(e, "Unused value");
                    walk(e.left(), Context.EXPR);
                    walk(e.right(), Context.EXPR);
                }

                case Node.PrefixExpr e -> {
                    if (ctx == Context.STMT) error(e, "Unused value");
                    walk(e.operand(), Context.EXPR);
                }

                case Node.Block b -> b.body().forEach(n -> walk(n, Context.STMT));

                case Node.Number n -> {
                    if (ctx == Context.STMT) error(n, "Unused value");
                }

                case Node.Word w -> {
                    if (ctx == Context.STMT) error(w, "Unused value");
                }

                case Node.VarRef v -> {
                    if (ctx == Context.STMT) error(v, "Unused value");
                }
            }
        }

        private void error(Node n, String msg) {
            errors.add(new Diagnostic(
                    LspConverter.toRange(n.start(), n.end()),
                    msg,
                    DiagnosticSeverity.Error,
                    ""
            ));
        }

        private enum Context {
            STMT,
            EXPR,
            NEUTRAL
        }
    }
}
