package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.SemanticTokens;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SemanticTokensProviderTest {

    @Test
    void semanticTokens_keywordsAndBuiltins() {
        String source = "to myproc\n  forward 100\nend";
        DocumentState state = new DocumentState("file:///test.logo", source);
        SemanticTokens tokens = SemanticTokensProvider.getSemanticTokens(state);
        List<Integer> data = tokens.getData();

        // to (keyword)
        // myproc (proc def)
        // forward (builtin proc call)
        // 100 (number)
        // end (keyword)

        assertFalse(data.isEmpty());
        // Each token has 5 integers
        assertTrue(data.size() >= 25);

        // Check "to" (line 0, col 0, len 2, type KEYWORD=0, mod NONE=0)
        assertEquals(0, data.get(0)); // delta line
        assertEquals(0, data.get(1)); // delta col
        assertEquals(2, data.get(2)); // length
        assertEquals(SemanticTokensProvider.TYPE_KEYWORD, data.get(3));

        // Check "myproc" (line 0, col 3, len 6, type FUNCTION=2, mod DECLARATION=1)
        assertEquals(0, data.get(5)); // delta line
        assertEquals(3, data.get(6)); // delta col (3 - 0)
        assertEquals(6, data.get(7)); // length
        assertEquals(SemanticTokensProvider.TYPE_FUNCTION, data.get(8));
        assertEquals(SemanticTokensProvider.MOD_DECLARATION, data.get(9));

        // Check "forward" (line 1, col 2, len 7, type FUNCTION=2, mod DEFAULT_LIB=2)
        // line 1, col 2. prev was line 0, col 3.
        assertEquals(1, data.get(10)); // delta line
        assertEquals(2, data.get(11)); // delta col (starts at 2)
        assertEquals(7, data.get(12)); // length
        assertEquals(SemanticTokensProvider.TYPE_FUNCTION, data.get(13));
        assertEquals(SemanticTokensProvider.MOD_DEFAULT_LIB, data.get(14));
    }

    @Test
    void semanticTokens_variables() {
        String source = "make \"x 10\nprint :x";
        DocumentState state = new DocumentState("file:///test.logo", source);
        SemanticTokens tokens = SemanticTokensProvider.getSemanticTokens(state);
        List<Integer> data = tokens.getData();

        // make (builtin function)
        // "x (string/word)
        // 10 (number)
        // print (builtin function)
        // :x (variable)

        // Find :x. It's at line 1, col 6.
        // Actually :x token starts at x, which is col 7.
        // Wait, Lexer.java: addToken(Token.Type.VARREF, startOffs + 1);
        // p r i n t _ : x
        // 0 1 2 3 4 5 6 7
        // So it's at col 7.

        boolean foundVar = false;
        for (int i = 0; i < data.size(); i += 5) {
            if (data.get(i + 3) == SemanticTokensProvider.TYPE_VARIABLE) {
                foundVar = true;
                assertEquals(2, data.get(i + 2)); // length of ":x" is 2
                break;
            }
        }
        assertTrue(foundVar);
    }
}
