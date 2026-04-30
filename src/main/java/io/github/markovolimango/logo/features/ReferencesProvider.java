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
        var tokens = new Lexer(state.getLine(pos.line())).tokenize();
        int i = 0;
        while (tokens.get(i).start().col() <= pos.col()) i++;
        i--;
        Token token = tokens.get(i);

        List<Location> locations = new ArrayList<>();
        var symTable = state.getSymTable();
        List<Pos[]> refs = switch (token.type()) {
            case VARREF -> symTable.getVarRefs(token.text(), pos);
            case PROC -> {
                if (Lexer.isDefineParamAt(i, tokens))
                    yield symTable.getVarRefs(token.text(), pos);
                yield symTable.getProcRefs(token.text(), pos);
            }
            case WORD -> {
                if ((i > 0 && (tokens.get(i - 1).type() == Token.Type.MAKE
                        || tokens.get(i - 1).type() == Token.Type.LOCALMAKE))
                        || (i > 1 && tokens.get(i - 1).type() == Token.Type.NAME))
                    yield symTable.getVarRefs(tokens.get(i).text(), pos);
                yield null;
            }
            default -> null;
        };
        if (refs == null) return null;
        for (var ref : refs)
            locations.add(new Location(state.getUri(), LspConverter.toRange(ref[0], ref[1])));
        return locations;
    }
}
