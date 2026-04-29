package io.github.markovolimango.logo.analysis.features;

import io.github.markovolimango.logo.ast.Node;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;

import java.util.ArrayList;
import java.util.List;

public final class SemanticTokensProvider {
    // Token type indices — must match the legend declared in ServerCapabilities
    public static final int TYPE_KEYWORD = 0;
    public static final int TYPE_VARIABLE = 1;
    public static final int TYPE_FUNCTION = 2;
    public static final int TYPE_OPERATOR = 3;
    public static final int TYPE_NUMBER = 4;
    public static final int TYPE_STRING = 5;
    public static final int TYPE_COMMENT = 6;
    public static final List<String> TOKEN_TYPES = List.of(
            "keyword", "variable", "function", "operator", "number", "string", "comment"
    );

    private SemanticTokensProvider() {
    }

    public static List<SemanticToken> getTokens(DocumentState state) {
        List<SemanticToken> result = new ArrayList<>();
        collectFromNode(state.getAst(), result);
        result.sort((a, b) -> {
            Pos pa = a.token().start();
            Pos pb = b.token().start();
            if (pa.equals(pb)) return 0;
            return pa.isAfter(pb) ? 1 : -1;
        });
        return result;
    }

    private static void collectFromNode(Node node, List<SemanticToken> out) {
        switch (node) {
            case Node.Program(var body, _, _) -> body.forEach(n -> collectFromNode(n, out));

            case Node.ToStmt(var name, var params, var body, _, _) -> {
                out.add(kw(name));                          // "to" — actually need the TO token
                out.add(new SemanticToken(name, TYPE_FUNCTION));
                params.forEach(p -> out.add(new SemanticToken(p, TYPE_VARIABLE)));
                body.forEach(n -> collectFromNode(n, out));
            }

            case Node.DefineStmt(var name, var params, var body, _, _) -> {
                // name is a Node (expression), params/body are Blocks
                collectFromNode(name, out);
                collectFromNode(params, out);
                collectFromNode(body, out);
            }

            case Node.ProcCall(var name, var args, _, _) -> {
                out.add(new SemanticToken(name, TYPE_FUNCTION));
                args.forEach(a -> collectFromNode(a, out));
            }

            case Node.MakeStmt(var name, var value, _, _) -> {
                collectFromNode(name, out);
                collectFromNode(value, out);
            }

            case Node.LocalMakeStmt(var name, var value, _, _) -> {
                collectFromNode(name, out);
                collectFromNode(value, out);
            }

            case Node.OutputStmt(var value, _, _) -> collectFromNode(value, out);

            case Node.VarRef(var name, _, _) -> out.add(new SemanticToken(name, TYPE_VARIABLE));

            case Node.Number(var value, _, _) -> out.add(new SemanticToken(value, TYPE_NUMBER));

            case Node.Word(var value, _, _) -> out.add(new SemanticToken(value, TYPE_STRING));

            case Node.InfixExpr(var op, var left, var right, _, _) -> {
                collectFromNode(left, out);
                out.add(new SemanticToken(op, TYPE_OPERATOR));
                collectFromNode(right, out);
            }

            case Node.PrefixExpr(var op, var operand, _, _) -> {
                out.add(new SemanticToken(op, TYPE_OPERATOR));
                collectFromNode(operand, out);
            }

            case Node.Block(var body, _, _) -> body.forEach(n -> collectFromNode(n, out));
        }
    }

    private static SemanticToken kw(Token t) {
        return new SemanticToken(t, TYPE_KEYWORD);
    }

    public record SemanticToken(Token token, int semanticType) {
    }
}