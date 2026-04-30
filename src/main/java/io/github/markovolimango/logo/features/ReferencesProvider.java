package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.Location;

import java.util.ArrayList;
import java.util.List;

public class ReferencesProvider {
    public static List<Location> findReferences(DocumentState state, Pos pos) {
        List<Location> locations = new ArrayList<>();
        String line = state.getLine(pos.line());
        int start = pos.col(), end = pos.col();

        while (start > 0 && Lexer.isNotDelimiter(line.charAt(start - 1)) && line.charAt(start - 1) != ':')
            start--;
        while (end < line.length() && Lexer.isNotDelimiter(line.charAt(end)))
            end++;

        boolean isVar = start - 1 >= 0 && line.charAt(start - 1) == ':';
        String name = (start == end) ? null : line.substring(start, end);
        if (name == null) return null;
        var symTable = state.getSymTable();
        var refs = isVar ? symTable.getVarRefs(name, pos) : symTable.getProcRefs(name, pos);
        for (var ref : refs) {
            locations.add(new Location(state.getUri(), LspConverter.toRange(ref[0], ref[1])));
        }
        return locations;
    }
}
