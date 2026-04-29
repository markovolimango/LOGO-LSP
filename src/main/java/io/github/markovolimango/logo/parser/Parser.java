package io.github.markovolimango.logo.parser;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Parser {
    private static final Map<String, Integer> BUILTIN_ARITY = Map.<String, Integer>ofEntries(
            // Turtle motion
            entry("forward", 1), entry("fd", 1),
            entry("back", 1), entry("bk", 1),
            entry("left", 1), entry("lt", 1),
            entry("right", 1), entry("rt", 1),
            entry("home", 0),
            entry("setx", 1),
            entry("sety", 1),
            entry("setxy", 2),
            entry("setpos", 1), // setpos [x y]  — takes a list
            entry("setheading", 1), entry("seth", 1),
            entry("arc", 2),
            entry("ellipse", 2),

            // Turtle motion queries
            entry("pos", 0),
            entry("xcor", 0),
            entry("ycor", 0),
            entry("heading", 0),
            entry("towards", 1), // towards [x y]

            // Turtle and window control
            entry("showturtle", 0), entry("st", 0),
            entry("hideturtle", 0), entry("ht", 0),
            entry("clean", 0),
            entry("clearscreen", 0), entry("cs", 0),
            entry("fill", 0),
            entry("filled", 2), // filled color [statements]
            entry("label", 1),
            entry("setlabelheight", 1),
            entry("wrap", 0),
            entry("window", 0),
            entry("fence", 0),

            // Turtle and window queries
            entry("shownp", 0), entry("shown?", 0),
            entry("labelsize", 0),

            // Pen and background control
            entry("penup", 0), entry("pu", 0),
            entry("pendown", 0), entry("pd", 0),
            entry("setcolor", 1), entry("setpencolor", 1),
            entry("setwidth", 1), entry("setpensize", 1),
            entry("changeshape", 1), entry("csh", 1),

            // Pen queries
            entry("pendownp", 0), entry("pendown?", 0),
            entry("pencolor", 0), entry("pc", 0),
            entry("pensize", 0),

            // Procedure definition / reflection
            entry("def", 1),

            // Variable definition
            entry("make", 2),
            entry("name", 2),
            entry("localmake", 2),
            entry("thing", 1),

            // Control structures
            entry("repeat", 2), // repeat N [stmts]
            entry("for", 2), // for [ctrl] [stmts]
            entry("repcount", 0),
            entry("if", 2), // if expr [stmts]
            entry("ifelse", 3), // ifelse expr [t] [f]
            entry("test", 1),
            entry("iftrue", 1),
            entry("iffalse", 1),
            entry("wait", 1),
            entry("bye", 0),
            entry("dotimes", 2), // dotimes [var n] [stmts]
            entry("do.while", 2), // do.while [stmts] expr
            entry("while", 2), // while [expr] [stmts]
            entry("do.until", 2), // do.until [stmts] [expr]
            entry("until", 2), // until [expr] [stmts]

            // Output / print
            entry("print", 1),
            entry("show", 1),
            entry("output", 1),

            // Lists
            entry("list", -1), // variadic
            entry("first", 1),
            entry("butfirst", 1),
            entry("last", 1),
            entry("butlast", 1),
            entry("item", 2),
            entry("pick", 1),

            // Math
            entry("sum", 2),
            entry("minus", 2),
            entry("random", 1),
            entry("modulo", 2),
            entry("power", 2),

            // Receivers
            entry("readword", 0), // optional prompt handled via ()
            entry("readlist", 0),

            // Predicates
            entry("word", 1), entry("word?", 1),
            entry("listp", 1), entry("list?", 1),
            entry("arrayp", 1), entry("array?", 1),
            entry("numberp", 1), entry("number?", 1),
            entry("emptyp", 1), entry("empty?", 1),
            entry("equalp", 2), entry("equal?", 2),
            entry("notequalp", 2), entry("notequal?", 2),
            entry("beforep", 1), entry("before?", 1),
            entry("substringp", 2), entry("substring?", 2)
    );

    private static final Map<String, int[]> INFIX_BP = Map.ofEntries(
            entry("or", new int[]{10, 11}),
            entry("and", new int[]{20, 21}),
            entry("=", new int[]{40, 41}),
            entry("<>", new int[]{40, 41}),
            entry("<", new int[]{40, 41}),
            entry(">", new int[]{40, 41}),
            entry("<=", new int[]{40, 41}),
            entry(">=", new int[]{40, 41}),
            entry("+", new int[]{50, 51}),
            entry("-", new int[]{50, 51}),
            entry("*", new int[]{60, 61}),
            entry("/", new int[]{60, 61}),
            entry("%", new int[]{60, 61})
    );

    private static final Map<String, Integer> PREFIX_BP = Map.of(
            "not", 30,   // lower than comparisons, higher than logical
            "-", 70    // unary minus binds very tight
    );

    private final List<Token> tokens;
    private final Map<String, Integer> userDefinedArity = new HashMap<>();
    private final List<ParseError> errors = new ArrayList<>();
    private int pos;
    private Token lastConsumed;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public Node.Program parseProgram() {
        var body = new ArrayList<Node>();
        while (peek().type() != Token.Type.EOF)
            body.add(parseExpr());
        if (body.isEmpty())
            return new Node.Program(body, new Pos(0, 0, 0), new Pos(0, 0, 0));
        return new Node.Program(body, body.getFirst().start(), body.getLast().end());
    }

    // only called when we know the keyword is correct
    public Node.MakeStmt parseMakeStmt() {
        Token keyword = consume();
        Node name, value;
        if (keyword.type() == Token.Type.MAKE) {
            name = parseExpr();
            value = parseExpr();
        } else {
            value = parseExpr();
            name = parseExpr();
        }
        return new Node.MakeStmt(name, value, keyword.start(), value.end());
    }

    // only called when we know the keyword is correct
    public Node parseLocalMakeStmt() {
        Token keyword = consume();
        Node name = parseExpr();
        Node value = parseExpr();
        return new Node.LocalMakeStmt(name, value, keyword.start(), value.end());
    }

    // only called when we know the keyword is correct
    public Node parseOutputStmt() {
        Token keyword = consume();
        var value = parseExpr();
        return new Node.OutputStmt(value, keyword.start(), value.end());
    }

    // only called when we know the keyword is correct
    public Node parseToStmt() {
        Token toToken = consume();
        Token name = consume();     // maybe check for errors here?
        List<Token> params = new ArrayList<>();
        List<Node> body = new ArrayList<>();
        while (peek().type() == Token.Type.VARREF)
            params.add(consume());
        userDefinedArity.put(name.text(), params.size());
        while (peek().type() != Token.Type.EOF && peek().type() != Token.Type.END)
            body.add(parseExpr());
        Token endToken = expect(Token.Type.END);
        return new Node.ToStmt(name, params, body, toToken.start(), endToken.end());
    }

    // only called when we know the keyword is correct
    public Node.DefineStmt parseDefineStmt() {
        consume();
        Node name = parseExpr();
        Node.Block block = parseBlock();

        Node.Block params = new Node.Block(new ArrayList<>(), block.start(), block.start());
        Node.Block body = new Node.Block(new ArrayList<>(), block.end(), block.end());
        if (block.body().size() != 2) {
            errors.add(new ParseError("Expected list of length 2 in 'DEFINE'", block.start(), block.end()));
        } else {
            if (block.body().getFirst() instanceof Node.Block)
                params = (Node.Block) block.body().getFirst();
            else
                errors.add(new ParseError("Expected list of arguments in 'DEFINE", block.body().getFirst().start(), block.body().getFirst().end()));
            if (block.body().getLast() instanceof Node.Block)
                body = (Node.Block) block.body().getLast();
            else
                errors.add(new ParseError("Expected list of body statements in 'DEFINE", block.body().getLast().start(), block.body().getLast().end()));
        }
        return new Node.DefineStmt(name, params, body, name.start(), block.end());
    }

    public Node.ProcCall parseProcCall() {
        Token name = consume();
        Integer arity = getProcArity(name.text());
        if (arity == null) {
            errors.add(new ParseError("Undefined procedure: " + name.text(), name.start(), name.end()));
            arity = 0;
        }
        var args = new ArrayList<Node>(arity);
        while (arity-- > 0)
            args.add(parseExpr());
        return new Node.ProcCall(name, args, name.start(), args.isEmpty() ? name.end() : args.getLast().end());
    }

    public Node.Block parseBlock() {
        Token lbracket = expect(Token.Type.LBRACKET);
        var body = new ArrayList<Node>();
        while (peek().type() != Token.Type.RBRACKET && peek().type() != Token.Type.EOF)
            body.add(parseExpr());
        Token rbracket = expect(Token.Type.RBRACKET);
        return new Node.Block(body, lbracket.start(), rbracket.end());
    }

    public Node parseExpr() {
        return parseExpr(0);
    }

    @SuppressWarnings("InfiniteRecursion")
    private Node parseExpr(int minBP) {
        Node left = parseNud();
        while (true) {
            Token op = peek();
            int[] bp = INFIX_BP.get(op.text());
            if (bp == null || bp[0] <= minBP) break;

            consume();
            Node right = parseExpr(bp[1]);
            left = new Node.InfixExpr(op, left, right, left.start(), right.end());
        }

        return left;
    }

    private Node parseNud() {
        Token t = peek();
        switch (t.type()) {
            case NUMBER -> {
                consume();
                return new Node.Number(t, t.start(), t.end());
            }
            case WORD -> {
                consume();
                return new Node.Word(t, t.start(), t.end());
            }
            case VARREF -> {
                consume();
                return new Node.VarRef(t, t.start(), t.end());
            }
            case LPAREN -> {
                consume();
                Node inner = parseExpr(0);
                expect(Token.Type.RPAREN);
                return inner;
            }
            case OPERATOR -> {
                consume();
                Integer rbp = PREFIX_BP.get(t.text());
                if (rbp == null) {
                    errors.add(new ParseError("Operator '" + t.text() + "' cannot appear in prefix position", t.start(), t.end()));
                    rbp = 70; // placeholder to keep on parsing
                }
                Node operand = parseExpr(rbp);
                return new Node.PrefixExpr(t, operand, t.start(), operand.end());
            }
            case PROC -> {
                return parseProcCall();
            }
            case LBRACKET -> {
                return parseBlock();
            }
            case MAKE, NAME -> {
                return parseMakeStmt();
            }
            case LOCALMAKE -> {
                return parseLocalMakeStmt();
            }
            case OUTPUT -> {
                return parseOutputStmt();
            }
            case TO -> {
                return parseToStmt();
            }
            case DEFINE -> {
                return parseDefineStmt();
            }
            default -> {
                errors.add(new ParseError("Unexpected token: " + t.text(), t.start(), t.end()));
                return new Node.Number(new Token(Token.Type.NUMBER, "0", t.start(), t.end()), t.start(), t.end());
            }
        }
    }

    private Token peek() {
        Token t = tokens.get(pos);
        while (t.type() == Token.Type.COMMENT)
            t = tokens.get(++pos);
        return t;
    }

    private Token consume() {
        Token t = peek();
        pos++;
        lastConsumed = t;
        return t;
    }

    private Token expect(Token.Type type) {
        if (peek().type() == type)
            return consume();
        errors.add(new ParseError("Expected " + type + ".", peek().start(), peek().end()));
        return new Token(type, "", lastConsumed.end(), lastConsumed.end());
    }

    private Integer getProcArity(String name) {
        var arity = BUILTIN_ARITY.get(name);
        if (arity == null) arity = userDefinedArity.get(name);
        return arity;
    }
}
