package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompletionProviderTest {

    @Test
    void completion_includesToSnippet() {
        List<CompletionItem> items = CompletionProvider.getCompletion(null, new Pos(0, 0));
        assertTrue(items.stream().anyMatch(i -> i.getLabel().equals("to")));
    }

    @Test
    void completion_includesVariablesAndProcedures() {
        String source = "make \"myvar 10\nto myproc :param\n  \nend";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Position inside myproc
        List<CompletionItem> items = CompletionProvider.getCompletion(state, new Pos(2, 2));

        // Should include :myvar, :param, and myproc
        assertTrue(items.stream().anyMatch(i -> i.getLabel().equals(":myvar")));
        assertTrue(items.stream().anyMatch(i -> i.getLabel().equals(":param")));
        assertTrue(items.stream().anyMatch(i -> i.getLabel().equals("myproc")));
    }

    @Test
    void completion_respectsScoping() {
        String source = "to myproc\n  localmake \"localvar 5\n  \nend\n";
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Outside myproc
        List<CompletionItem> items = CompletionProvider.getCompletion(state, new Pos(4, 0));
        assertFalse(items.stream().anyMatch(i -> i.getLabel().equals(":localvar")));

        // Inside myproc, line 2
        items = CompletionProvider.getCompletion(state, new Pos(2, 2));
        assertTrue(items.stream().anyMatch(i -> i.getLabel().equals(":localvar")));
    }
}
