package io.github.markovolimango.logo.parser;

public class ASTWalker {
    public void walk(Node node) {
        switch (node) {
            case Node.Program p -> p.body().forEach(this::walk);
            case Node.ToStmt d -> d.body().forEach(this::walk);
            case Node.DefineStmt d -> {
                walk(d.name());
                walk(d.params());
                walk(d.body());
            }
            case Node.ProcCall c -> c.args().forEach(this::walk);
            case Node.MakeStmt m -> {
                walk(m.name());
                walk(m.value());
            }
            case Node.LocalMakeStmt l -> {
                walk(l.name());
                walk(l.value());
            }
            case Node.OutputStmt o -> walk(o.value());
            case Node.InfixExpr e -> {
                walk(e.left());
                walk(e.right());
            }
            case Node.PrefixExpr e -> walk(e.operand());
            case Node.Block b -> b.body().forEach(this::walk);
            case Node.Number n -> {
            }
            case Node.Word w -> {
            }
            case Node.VarRef v -> {
            }
        }
    }
}
