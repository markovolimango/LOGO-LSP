package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.Location;

import java.util.ArrayList;
import java.util.List;

public class ReferencesProvider {
    public static List<Location> findReferences(DocumentState state, Pos pos) {
        Token token = Lexer.recoverTokenAt(state.getLine(pos.line()), pos.col());
        List<Location> locations = new ArrayList<>();
        var symTable = state.getSymTable();
        List<Pos[]> refs = switch (token.type()) {
            case VARREF -> symTable.getVarRefs(token.text(), pos);
            case PROC -> symTable.getProcRefs(token.text(), pos);
            default -> List.of();
        };
        for (var ref : refs)
            locations.add(new Location(state.getUri(), LspConverter.toRange(ref[0], ref[1])));
        return locations;
    }
}
