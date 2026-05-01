package io.github.markovolimango.logo.parser;

import io.github.markovolimango.logo.LogoLanguage;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final List<Token> tokens;
    private final Map<String, LogoLanguage.Arity> userDefinedArity = new HashMap<>();
    private final List<ParseError> errors = new ArrayList<>();
    private int pos;
    private Token lastConsumed = new Token(Token.Type.EOF, "", new Pos(0, 0), new Pos(0, 0));

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public Node.Program parseProgram() {
        var body = new ArrayList<Node>();
        while (peek().type() != Token.Type.EOF) {
            int startPos = pos;
            body.add(parseExpr());
            if (pos == startPos) consume();
        }
        if (body.isEmpty())
            return new Node.Program(body, new Pos(0, 0), new Pos(0, 0));
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
        var requiredParams = new ArrayList<Token>();
        var optionalParams = new ArrayList<Token>();
        var body = new ArrayList<Node>();
        while (peek().type() == Token.Type.VARREF)
            requiredParams.add(consume());
        while (peek().type() == Token.Type.LBRACKET) {
            consume();
            var vr = expect(Token.Type.VARREF);
            expect(Token.Type.RBRACKET);
            optionalParams.add(vr);
        }
        int min = requiredParams.size(), max = min + optionalParams.size();
        userDefinedArity.put(name.text(), LogoLanguage.Arity.capped(min, max));
        while (peek().type() != Token.Type.EOF && peek().type() != Token.Type.END) {
            int startPos = pos;
            body.add(parseExpr());
            if (pos == startPos) consume();
        }
        Token endToken = expect(Token.Type.END);
        return new Node.ToStmt(name, requiredParams, body, toToken.start(), endToken.end());
    }

    // only called when we know the keyword is correct
    public Node.DefineStmt parseDefineStmt() {
        consume();
        Node name = parseExpr();
        Node.Block block = parseBlock();

        if (block.body().size() != 2) {
            errors.add(new ParseError("Expected list of length 2 in 'DEFINE'", block.start(), block.end()));
            return new Node.DefineStmt(name, new ArrayList<>(), new Node.Block(new ArrayList<>(), block.start(), block.end()), name.start(), block.end());
        }
        var params = parseDefineParams(block.body().getFirst());
        return new Node.DefineStmt(name, params, block.body().getLast(), name.start(), block.end());
    }

    public List<Node> parseDefineParams(Node node) {
        return switch (node) {
            case Node.ProcCall pc -> List.of(pc);
            case Node.Block b -> {
                List<Node> params = new ArrayList<>();
                for (Node n : b.body()) {
                    if (!(n instanceof Node.ProcCall))
                        errors.add(new ParseError("Invalid procedure parameter", n.start(), n.end()));
                    else if (!((Node.ProcCall) n).args().isEmpty())
                        errors.add(new ParseError("Invalid procedure parameter", n.start(), n.end()));
                    params.add(n);
                }
                yield params;
            }
            default -> {
                errors.add(new ParseError("Invalid procedure parameter", node.start(), node.end()));
                yield List.of(node);
            }
        };
    }

    public Node.ProcCall parseProcCall() {
        boolean isGreedy = lastConsumed.type() == Token.Type.LPAREN;
        Token name = consume();
        var arity = getProcArity(name.text());
        if (arity == null) {
            // errors.add(new ParseError("Undefined procedure: " + name.text(), name.start(), name.end()));
            arity = LogoLanguage.Arity.fixed(0);
        }
        var args = new ArrayList<Node>();
        if (isGreedy) {
            while (peek().type() != Token.Type.RPAREN && peek().type() != Token.Type.EOF)
                args.add(parseExpr());
            if (args.size() > arity.max() && arity.max() > -1)
                errors.add(new ParseError("Too many arguments for procedure: " + name.text(), name.start(), name.end()));
            if (args.size() < arity.min())
                errors.add(new ParseError("Too few arguments for procedure: " + name.text(), name.start(), name.end()));
        } else {
            for (int i = 0; i < arity.min(); i++)
                args.add(parseExpr());
        }
        return new Node.ProcCall(name, args, name.start(), args.isEmpty() ? name.end() : args.getLast().end());
    }

    public Node.Block parseBlock() {
        Token lbracket = expect(Token.Type.LBRACKET);
        var body = new ArrayList<Node>();
        while (peek().type() != Token.Type.RBRACKET && peek().type() != Token.Type.EOF) {
            int startPos = pos;
            body.add(parseExpr());
            if (pos == startPos) consume();
        }
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
            int[] bp = LogoLanguage.getInfixBP(op.text());
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
                Integer rbp = LogoLanguage.getPrefixBP(t.text());
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
        if (pos >= tokens.size()) {
            return getEofToken();
        }
        Token t = tokens.get(pos);
        while (t.type() == Token.Type.COMMENT) {
            pos++;
            if (pos >= tokens.size()) return getEofToken();
            t = tokens.get(pos);
        }
        return t;
    }

    private Token getEofToken() {
        if (tokens.isEmpty()) {
            return new Token(Token.Type.EOF, "", new Pos(0, 0), new Pos(0, 0));
        }
        Token last = tokens.getLast();
        if (last.type() == Token.Type.EOF) return last;
        return new Token(Token.Type.EOF, "", last.end(), last.end());
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

    private LogoLanguage.Arity getProcArity(String name) {
        var arity = LogoLanguage.getArity(name);
        if (arity == null) arity = userDefinedArity.get(name);
        return arity;
    }
}
