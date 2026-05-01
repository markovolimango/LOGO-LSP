package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.TextEdit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RenameProviderTest {

    @Test
    void renameVariable_updatesAllOccurrences() {
        String source = "make \"x 10\nprint :x\nprint :x";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Rename x at "x
        List<TextEdit> edits = RenameProvider.getRenameEdits(state, new Pos(0, 6), "newVar");

        assertNotNull(edits);
        // Expecting 3 edits: one for "x and two for :x
        // Actually, with my change to Symbol, it might be 4 if RenameProvider still adds definition manually.
        // Let's check.
        assertEquals(3, edits.size(), "Should have 3 edits (1 definition + 2 uses)");

        for (TextEdit edit : edits) {
            assertEquals("newVar", edit.getNewText());
        }
    }

    @Test
    void renameProcedure_updatesAllOccurrences() {
        String source = "to myproc\nend\nmyproc\nmyproc";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Rename myproc
        List<TextEdit> edits = RenameProvider.getRenameEdits(state, new Pos(0, 3), "newProc");

        assertNotNull(edits);
        assertEquals(3, edits.size());
    }
}
