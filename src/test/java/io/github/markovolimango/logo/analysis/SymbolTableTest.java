package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTableTest {

    private SymbolTable buildTable(String source) {
        var tokens = new Lexer(source).getTokens();
        var program = new Parser(tokens).parseProgram();
        return new SymbolTableBuilder().build(program);
    }

    @Test
    void make_addsGlobalVariable() {
        SymbolTable table = buildTable("make \"x 10\nforward :x");
        // Variable 'x' is defined at "make \"x 10"
        // We look for it at "forward :x"
        Symbol.Var var = table.getVarDef("x", new Pos(1, 8));
        assertNotNull(var);
        assertEquals("x", var.getName());
    }

    @Test
    void localmake_addsLocalVariable() {
        String source = "to myproc\n  localmake \"y 20\n  print :y\nend\nprint :y";
        SymbolTable table = buildTable(source);

        // Inside myproc, y should be visible
        assertNotNull(table.getVarDef("y", new Pos(2, 8)));

        // Outside myproc, y should NOT be visible
        assertNull(table.getVarDef("y", new Pos(4, 6)));
    }

    @Test
    void toStmt_addsProcedureAndParams() {
        String source = "to square :side\n  repeat 4 [fd :side rt 90]\nend";
        SymbolTable table = buildTable(source);

        // Procedure 'square' should be in global scope
        assertNotNull(table.getProcDef("square", new Pos(4, 0)));

        // Param 'side' should be visible inside the procedure
        assertNotNull(table.getVarDef("side", new Pos(1, 15)));
    }

    @Test
    void scoping_innerShadowsOuter() {
        String source = "make \"x 1\nto myproc\n  localmake \"x 2\n  print :x\nend";
        SymbolTable table = buildTable(source);

        // Inside myproc, x should be the local one (2)
        Symbol.Var var = table.getVarDef("x", new Pos(3, 8));
        assertNotNull(var);
        // The local 'x' starts at line 2
        assertEquals(2, var.getStart().line());
    }

    @Test
    void define_addsProcedureAndParams() {
        // define "inc [[n] [output :n + 1]]
        String source = "define \"inc [[n] [output :n + 1]]";
        SymbolTable table = buildTable(source);

        assertNotNull(table.getProcDef("inc", new Pos(1, 0)));
        // 'n' should be visible in the body block
        // The body starts after [[n] 
        assertNotNull(table.getVarDef("n", new Pos(0, 25)));
    }

    @Test
    void getVarNames_returnsAvailableVariables() {
        String source = "make \"a 1\nmake \"b 2\nto p :c\n  localmake \"d 4\nend";
        SymbolTable table = buildTable(source);

        // At the end of p
        List<String> names = table.getAllVars(new Pos(3, 16))
                .stream().map(Symbol::getName).toList();
        assertTrue(names.contains("a"));
        assertTrue(names.contains("b"));
        assertTrue(names.contains("c"));
        assertTrue(names.contains("d"));
    }

    @Test
    void sequentialDefinitions_laterSeeEarlier() {
        String source = "make \"a 1\nmake \"b :a + 1";
        SymbolTable table = buildTable(source);

        assertNotNull(table.getVarDef("a", new Pos(1, 9)));
    }

    @Test
    void procedure_notVisibleBeforeDefinition() {
        String source = "mysquare 50\nto mysquare :s\n  fd :s\nend";
        SymbolTable table = buildTable(source);

        // LOGO procedures are usually global and visible everywhere if they are parsed first,
        // but our SymbolTableBuilder walks in order. 
        // Actually SymbolTableBuilder adds ToStmt name to globalScope BEFORE walking body.
        // However, it adds it as it encounters it.

        // Let's check if it is visible at line 0
        assertNull(table.getProcDef("mysquare", new Pos(0, 5)));
    }
}
