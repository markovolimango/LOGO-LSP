package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReferencesProviderTest {

    @Test
    void variableReferences_fromDefinition() {
        String source = "make \"x 10\nprint :x\nprint :x";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Pos of "x in make "x 10
        List<Location> refs = ReferencesProvider.findReferences(state, new Pos(0, 6));

        assertNotNull(refs);
        assertEquals(3, refs.size()); // definition + 2 uses
    }

    @Test
    void variableReferences_fromUse() {
        String source = "make \"x 10\nprint :x\nprint :x";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Pos of :x in first print
        List<Location> refs = ReferencesProvider.findReferences(state, new Pos(1, 7));

        assertNotNull(refs);
        assertEquals(3, refs.size());
    }

    @Test
    void procedureReferences_fromDefinition() {
        String source = "to myproc\nend\nmyproc\nmyproc";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Pos of myproc in to myproc
        List<Location> refs = ReferencesProvider.findReferences(state, new Pos(0, 3));

        assertNotNull(refs);
        assertEquals(3, refs.size()); // definition + 2 calls
    }

    @Test
    void procedureReferences_fromCall() {
        String source = "to myproc\nend\nmyproc\nmyproc";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Pos of myproc call
        List<Location> refs = ReferencesProvider.findReferences(state, new Pos(2, 0));

        assertNotNull(refs);
        assertEquals(3, refs.size());
    }

    @Test
    void scoping_localVariableReferences() {
        String source = "to p :a\n  print :a\nend\nprint :a";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // References for :a inside p (at to p :a)
        List<Location> refs = ReferencesProvider.findReferences(state, new Pos(0, 6));
        assertNotNull(refs);
        assertEquals(2, refs.size()); // param + use inside

        // References for :a outside p (it's undefined there, but let's see)
        List<Location> refsOutside = ReferencesProvider.findReferences(state, new Pos(3, 7));
        assertNull(refsOutside); // Should be null if undefined
    }
}
