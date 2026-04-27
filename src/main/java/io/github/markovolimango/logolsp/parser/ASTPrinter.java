package io.github.markovolimango.logolsp.parser;

public final class ASTPrinter {

    private ASTPrinter() {
    }

    public static String print(Node node) {
        StringBuilder sb = new StringBuilder();
        print(node, sb, 0);
        return sb.toString();
    }

    private static void print(Node node, StringBuilder sb, int indent) {
        String pad = "  ".repeat(indent);

        switch (node) {
            case Node.Program p -> {
                sb.append(pad).append("Program\n");
                for (Node child : p.body()) {
                    print(child, sb, indent + 1);
                }
            }

            case Node.Number n -> sb.append(pad)
                    .append("Number(").append(n.value().text()).append(")\n");

            case Node.Word w -> sb.append(pad)
                    .append("Word(\"").append(w.value().text()).append("\")\n");

            case Node.VarRef v -> sb.append(pad)
                    .append("VarRef(").append(v.name().text()).append(")\n");

            case Node.Block b -> {
                sb.append(pad).append("Block\n");
                for (Node child : b.body()) {
                    print(child, sb, indent + 1);
                }
            }

            case Node.ProcDef d -> {
                sb.append(pad)
                        .append("ProcDef(").append(d.name().text()).append(")\n");

                sb.append(pad).append("  Params:\n");
                for (Node param : d.params()) {
                    print(param, sb, indent + 2);
                }

                sb.append(pad).append("  Body:\n");
                for (Node stmt : d.body()) {
                    print(stmt, sb, indent + 2);
                }
            }

            case Node.ProcCall c -> {
                sb.append(pad)
                        .append("ProcCall(").append(c.name().text()).append(")\n");
                for (Node arg : c.args()) {
                    print(arg, sb, indent + 1);
                }
            }

            case Node.MakeStmt m -> {
                sb.append(pad).append("MakeStmt\n");
                sb.append(pad).append("  Name:\n");
                print(m.name(), sb, indent + 2);
                sb.append(pad).append("  Value:\n");
                print(m.value(), sb, indent + 2);
            }

            case Node.OutputStmt o -> {
                sb.append(pad).append("OutputStmt\n");
                print(o.value(), sb, indent + 1);
            }

            case Node.PrefixExpr p -> {
                sb.append(pad)
                        .append("Prefix(").append(p.op().text()).append(")\n");
                print(p.operand(), sb, indent + 1);
            }

            case Node.InfixExpr i -> {
                sb.append(pad)
                        .append("Infix(").append(i.op().text()).append(")\n");
                print(i.left(), sb, indent + 1);
                print(i.right(), sb, indent + 1);
            }
        }
    }
}