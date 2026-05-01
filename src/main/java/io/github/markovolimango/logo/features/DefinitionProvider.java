package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.analysis.Symbol;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.Location;

public class DefinitionProvider {
    public static Location findDefinition(DocumentState state, Pos pos) {
        Token token = new Lexer(state.getLine(pos.line())).getTokenAt(new Pos(0, pos.col()));
        if (token.text().isBlank()) return null;
        
        var symTable = state.getSymTable();
        Symbol sym = switch (token.type()) {
            case VARREF -> symTable.getVarDef(token.text(), pos);
            case PROC -> symTable.getProcDef(token.text(), pos);
            default -> null;
        };
        if (sym == null) return null;
        return new Location(state.getUri(), LspConverter.toRange(sym.getStart(), sym.getEnd()));
    }
}
