package io.github.markovolimango.logolsp;

import io.github.markovolimango.logolsp.lexer.Lexer;
import io.github.markovolimango.logolsp.lexer.Token;
import io.github.markovolimango.logolsp.parser.ASTPrinter;
import io.github.markovolimango.logolsp.parser.Parser;

import java.util.List;

public class Main {
    static void main() {
        String source = """
                to square :size
                  repeat 4 [
                    forward :size
                    right 90
                  ]
                end
                
                to spiral :n :len
                  if :n <= 0 [
                    output :len
                  ]
                  forward :len
                  right 20
                  spiral :n - 1 :len + 5
                end
                
                to main
                  make "x 10
                  make "y 20
                
                  print :x + :y * 2
                  print (:x + :y) * 2
                
                  square :x
                
                  make "result spiral 5 10
                  print :result
                
                  if :x > 5 [
                    make "x :x + 1
                    print :x
                  ]
                
                  repeat 3 [
                    make "y :y + :x
                    print :y
                  ]
                end
                
                main
                """;
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scan();
        var parser = new Parser(tokens);
        var ast = parser.parseProgram();
        System.out.println(ASTPrinter.print(ast));
    }
}
