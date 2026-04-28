package io.github.markovolimango.logo;

import io.github.markovolimango.logo.analysis.SymbolTableBuilder;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.parser.ASTPrinter;
import io.github.markovolimango.logo.parser.Parser;

public class maretest {
    static void main(String[] args) {
        String text = """
                to mare
                    print 5
                end
                
                mare
                """;
        var tokens = new Lexer(text).scan();
        var ast = new Parser(tokens).parseProgram();
        new ASTPrinter().print(ast);
        var symTable = new SymbolTableBuilder().build(ast);
        System.out.println(symTable.get("mare", new Pos(0, 4, 0)));
    }
}
