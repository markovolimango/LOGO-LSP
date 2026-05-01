package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentSymbolProviderTest {

    @Test
    void documentSymbols_proceduresAndVariables() {
        String source = "make \"g 1\nto myproc :p\n  localmake \"l 2\nend";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<DocumentSymbol> symbols = DocumentSymbolProvider.getSymbols(state);

        // Expecting:
        // 1. g (Variable)
        // 2. myproc (Function)
        //    - p (Variable)
        //    - l (Variable)

        assertNotNull(symbols);
        // g and myproc are top-level
        assertTrue(symbols.stream().anyMatch(s -> s.getName().equals("g") && s.getKind() == SymbolKind.Variable));

        DocumentSymbol myproc = symbols.stream()
                .filter(s -> s.getName().equals("myproc") && s.getKind() == SymbolKind.Function)
                .findFirst()
                .orElse(null);

        assertNotNull(myproc);
        assertNotNull(myproc.getChildren());
        assertEquals(2, myproc.getChildren().size());
        assertTrue(myproc.getChildren().stream().anyMatch(s -> s.getName().equals("p")));
        assertTrue(myproc.getChildren().stream().anyMatch(s -> s.getName().equals("l")));
    }
}
