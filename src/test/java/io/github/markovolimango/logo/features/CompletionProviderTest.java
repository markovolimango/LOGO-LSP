package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompletionProviderTest {

    @Test
    void completion_respectsScoping() {
        String source = """
                to myproc
                  localmake "localvar 5
                 :
                end
                """;
        DocumentState state = new DocumentState("file:///test.logo", source);

        // Outside myproc
        List<CompletionItem> items = CompletionProvider.getCompletion(state, new Pos(4, 0));
        assertFalse(items.stream().anyMatch(i -> i.getLabel().equals(":localvar")));

        // Inside myproc, line 2
        items = CompletionProvider.getCompletion(state, new Pos(2, 2));
        assertTrue(items.stream().anyMatch(i -> i.getLabel().equals(":localvar")));
    }
}
