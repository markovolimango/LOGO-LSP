package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefinitionProviderTest {

    @Test
    void findDefinition_variable() {
        String source = "make \"x 10\nprint :x";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Position on 'x' in ':x'
        Location loc = DefinitionProvider.findDefinition(state, new Pos(1, 7));
        assertNotNull(loc);
        // The definition of 'x' is at line 0, column 5 (start of "x)
        assertEquals(0, loc.getRange().getStart().getLine());
        assertEquals(5, loc.getRange().getStart().getCharacter());
    }

    @Test
    void findDefinition_procedure() {
        String source = "to square :side\n  fd :side\nend\nsquare 50";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Position on 'square' in 'square 50'
        Location loc = DefinitionProvider.findDefinition(state, new Pos(3, 2));
        assertNotNull(loc);
        // Definition of 'square' is at line 0, column 3
        assertEquals(0, loc.getRange().getStart().getLine());
        assertEquals(3, loc.getRange().getStart().getCharacter());
    }

    @Test
    void findDefinition_localVariable() {
        String source = "to myproc :p\n  print :p\nend";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Position on 'p' in ':p'
        Location loc = DefinitionProvider.findDefinition(state, new Pos(1, 9));
        assertNotNull(loc);
        // Definition of 'p' is at line 0, column 10 (start of ':p' in 'to myproc :p')
        assertEquals(0, loc.getRange().getStart().getLine());
        assertEquals(10, loc.getRange().getStart().getCharacter());
    }

    @Test
    void findDefinition_nonExistent() {
        String source = "print :y";
        DocumentState state = new DocumentState("file:///test.logo", source);

        Location loc = DefinitionProvider.findDefinition(state, new Pos(0, 7));
        assertNull(loc);
    }
}
