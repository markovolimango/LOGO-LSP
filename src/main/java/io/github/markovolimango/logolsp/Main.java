package io.github.markovolimango.logolsp;

import io.github.markovolimango.logolsp.lexer.Lexer;
import io.github.markovolimango.logolsp.lexer.Token;
import io.github.markovolimango.logolsp.parser.ExpressionParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("mare");
        String source = """
                (:a+:b)*(:c+:d)
                """;
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scan();
        for (Token token : tokens)
            System.out.println(token);
        var parser = new ExpressionParser(tokens);
        var expr = parser.parseExpr(0);
        System.out.println(expr.toString());
    }
}
