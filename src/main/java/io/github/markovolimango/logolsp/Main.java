package io.github.markovolimango.logolsp;

import io.github.markovolimango.logolsp.lexer.Lexer;
import io.github.markovolimango.logolsp.lexer.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("mare");
        String source = """
                make "yoy 3
                
                To mare
                  localmake "yoy 4
                  print "mare.yoy:
                  print :yoy
                  op 2.
                  output 4
                end
                
                print "mare:
                print mare
                
                print "yoy:
                print :yoy
                
                if not TRUE [print "woa
                """;
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scan();
        for (Token token : tokens)
            System.out.println(token);
    }
}
