package io.github.markovolimango.logo.parser;

import java.util.stream.Collectors;

public class AstPrinter extends AstWalker {
    private int indentLevel = 0;

    public void print(Node node) {
        walk(node);
        System.out.println();
    }

    @Override
    public void walk(Node node) {
        if (node == null) return;

        printIndent();

        switch (node) {
            case Node.Program p -> {
                System.out.println("(PROGRAM");
                indent(() -> p.body().forEach(this::walk));
                System.out.print(")");
            }
            case Node.ToStmt t -> {
                String params = t.params().stream().map(token -> ":" + token.text()).collect(Collectors.joining(" "));
                System.out.println("(TO " + t.name().text() + " [" + params + "]");
                indent(() -> t.body().forEach(this::walk));
                System.out.print(")");
            }
            case Node.DefineStmt d -> {
                System.out.println("(DEFINE");
                indent(() -> {
                    walk(d.name());
                    d.params().forEach(this::walk);
                    walk(d.body());
                });
                System.out.print(")");
            }
            case Node.ProcCall c -> {
                System.out.println("(CALL " + c.name().text());
                indent(() -> c.args().forEach(this::walk));
                System.out.print(")");
            }
            case Node.MakeStmt m -> {
                System.out.println("(MAKE");
                indent(() -> {
                    walk(m.name());
                    walk(m.value());
                });
                System.out.print(")");
            }
            case Node.LocalMakeStmt l -> {
                System.out.println("(LOCALMAKE");
                indent(() -> {
                    walk(l.name());
                    walk(l.value());
                });
                System.out.print(")");
            }
            case Node.OutputStmt o -> {
                System.out.println("(OUTPUT");
                indent(() -> walk(o.value()));
                System.out.print(")");
            }
            case Node.InfixExpr e -> {
                System.out.println("(INFIX " + e.op().text());
                indent(() -> {
                    walk(e.left());
                    walk(e.right());
                });
                System.out.print(")");
            }
            case Node.PrefixExpr e -> {
                System.out.println("(PREFIX " + e.op().text());
                indent(() -> walk(e.operand()));
                System.out.print(")");
            }
            case Node.Block b -> {
                System.out.println("(BLOCK");
                indent(() -> b.body().forEach(this::walk));
                System.out.print(")");
            }
            case Node.Number n -> System.out.print("NUMBER[" + n.value().text() + "]");
            case Node.Word w -> System.out.print("WORD[\"" + w.value().text() + "]");
            case Node.VarRef v -> System.out.print("VAR[:" + v.name().text() + "]");
        }

        // Only print newline for leaf nodes or the closing paren of a block
        // to keep the structure clean.
        if (isLeaf(node)) {
            System.out.println();
        }
    }

    private boolean isLeaf(Node node) {
        return node instanceof Node.Number || node instanceof Node.Word || node instanceof Node.VarRef;
    }

    private void indent(Runnable action) {
        indentLevel++;
        action.run();
        indentLevel--;
        System.out.println();
    }

    private void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("  ");
        }
    }
}