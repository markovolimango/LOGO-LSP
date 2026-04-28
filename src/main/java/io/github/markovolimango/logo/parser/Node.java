package io.github.markovolimango.logo.parser;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;

import java.util.List;

public sealed interface Node {
    Pos start();

    Pos end();

    record Program(List<Node> body, Pos start, Pos end) implements Node {
    }

    record Number(Token value, Pos start, Pos end) implements Node {
    }

    record Word(Token value, Pos start, Pos end) implements Node {
    }

    record Block(List<Node> body, Pos start, Pos end) implements Node {
    }

    record VarRef(Token name, Pos start, Pos end) implements Node {
    }

    record ToStmt(Token name, List<Token> params, List<Node> body, Pos start, Pos end) implements Node {
    }

    record DefineStmt(Node name, Block params, Block body, Pos start, Pos end) implements Node {
    }

    record ProcCall(Token name, List<Node> args, Pos start, Pos end) implements Node {
    }

    record LocalMakeStmt(Node name, Node value, Pos start, Pos end) implements Node {
    }

    record MakeStmt(Node name, Node value, Pos start, Pos end) implements Node {
    }

    record OutputStmt(Node value, Pos start, Pos end) implements Node {
    }

    record PrefixExpr(Token op, Node operand, Pos start, Pos end) implements Node {
    }

    record InfixExpr(Token op, Node left, Node right, Pos start, Pos end) implements Node {
    }

}
