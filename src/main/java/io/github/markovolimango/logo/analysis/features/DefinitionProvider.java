package io.github.markovolimango.logo.analysis.features;

import io.github.markovolimango.logo.analysis.Symbol;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;

public class DefinitionProvider {
    public static Symbol findDefinition(DocumentState state, Pos pos) {
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
        return isVar ? symTable.getVarDef(name, pos) : symTable.getProcDef(name, pos);
    }
}
