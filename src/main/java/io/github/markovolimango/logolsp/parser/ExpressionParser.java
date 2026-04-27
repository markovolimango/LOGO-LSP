package io.github.markovolimango.logolsp.parser;

import io.github.markovolimango.logolsp.lexer.Token;
import io.github.markovolimango.logolsp.lexer.Token.Type;

import java.util.List;
import java.util.Map;

public class ExpressionParser {
    private static final Map<String, int[]> INFIX_BP = Map.ofEntries(
            Map.entry("or", new int[]{10, 11}),
            Map.entry("and", new int[]{20, 21}),
            Map.entry("=", new int[]{40, 41}),
            Map.entry("<>", new int[]{40, 41}),
            Map.entry("<", new int[]{40, 41}),
            Map.entry(">", new int[]{40, 41}),
            Map.entry("<=", new int[]{40, 41}),
            Map.entry(">=", new int[]{40, 41}),
            Map.entry("+", new int[]{50, 51}),
            Map.entry("-", new int[]{50, 51}),
            Map.entry("*", new int[]{60, 61}),
            Map.entry("/", new int[]{60, 61}),
            Map.entry("%", new int[]{60, 61})
    );

    private static final Map<String, Integer> PREFIX_BP = Map.of(
            "not", 30,   // lower than comparisons, higher than logical
            "-", 70    // unary minus binds very tight
    );

    private final List<Token> tokens;
    private int pos;

    public ExpressionParser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public Node parseExpr(int minBP) {
        Node left = parseNud();

        while (true) {
            Token op = peek();
            if (op == null) break;

            int[] bp = infixBP(op);
            if (bp == null || bp[0] <= minBP) break;

            consume(); // eat the infix operator
            Node right = parseExpr(bp[1]);
            left = new Node.InfixExpr(op, left, right);
        }

        return left;
    }

    private Node parseNud() {
        Token t = consume();
        if (t == null) throw parseError("Unexpected end of input");

        return switch (t.type()) {
            case NUMBER -> new Node.Number(t, Double.parseDouble(t.text()));
            case WORD -> new Node.Word(t, t.text());
            case VARREF -> new Node.VarRef(t, t.text());

            case LPAREN -> {
                Node inner = parseExpr(0);
                expect(Type.RPAREN, ")");
                yield inner;
            }

            case OPERATOR -> {
                Integer rbp = PREFIX_BP.get(t.text());
                if (rbp == null) throw parseError("Operator '" + t.text() + "' cannot appear in prefix position");
                Node operand = parseExpr(rbp);
                yield new Node.PrefixExpr(t, operand);
            }

            // A PROC token in expression position becomes a ProcCall.
            // Argument count is unknown at parse time (LOGO is unary/variadic),
            // so we collect tokens until we hit something that cannot start an expr.
            case PROC -> {
                List<Node> args = new java.util.ArrayList<>();
                while (canStartExpr(peek())) {
                    args.add(parseExpr(0));
                }
                yield new Node.ProcCall(t, args);
            }

            default -> throw parseError("Unexpected token: " + t.text() + " (" + t.type() + ")");
        };
    }

    private int[] infixBP(Token t) {
        if (t.type() == Type.OPERATOR) return INFIX_BP.get(t.text());
        return null;
    }

    /**
     * Returns true if the token can be the start of an expression (nud position).
     */
    private boolean canStartExpr(Token t) {
        if (t == null) return false;
        return switch (t.type()) {
            case NUMBER, WORD, VARREF, LPAREN, PROC -> true;
            case OPERATOR -> PREFIX_BP.containsKey(t.text());
            default -> false;
        };
    }

    // ── Parser state ──────────────────────────────────────────────────────────

    private Token peek() {
        if (pos >= tokens.size()) return null;
        Token t = tokens.get(pos);
        // Skip comments transparently
        while (t != null && t.type() == Type.COMMENT) {
            pos++;
            t = pos < tokens.size() ? tokens.get(pos) : null;
        }
        return t;
    }

    private Token consume() {
        Token t = peek();
        if (t != null) pos++;
        return t;
    }

    private void expect(Type type, String text) {
        Token t = consume();
        if (t == null || t.type() != type) {
            throw parseError("Expected '" + text + "'");
        }
    }

    // ── Public entry point ────────────────────────────────────────────────────

    private RuntimeException parseError(String message) {
        Token cur = pos < tokens.size() ? tokens.get(pos) : null;
        String loc = cur != null
                ? " at " + cur.start()
                : " at end of input";
        return new IllegalStateException(message + loc);
    }
}
