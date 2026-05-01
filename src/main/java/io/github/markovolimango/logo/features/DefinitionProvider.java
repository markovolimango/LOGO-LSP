package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.analysis.Symbol;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.Location;

public final class DefinitionProvider {
    public static Location findDefinition(DocumentState state, Pos pos) {
        var linePos = new Pos(0, pos.col());
        var lexer = new Lexer(state.getLine(pos.line()));
        var tokens = lexer.getTokens();
        var i = lexer.getIndexFromPos(linePos);
        var token = lexer.getTokenAt(linePos);

        if (token.text().isBlank()) return null;

        var symTable = state.getSymTable();
        Symbol sym = switch (token.type()) {
            case VARREF -> symTable.getVarDef(token.text(), pos);
            case PROC -> symTable.getProcDef(token.text(), pos);
            case WORD -> {
                if ((i > 0 &&
                        (tokens.get(i - 1).type() == Token.Type.MAKE || tokens.get(i - 1).type() == Token.Type.LOCALMAKE))
                        || (i > 1 && tokens.get(i - 1).type() == Token.Type.NAME))
                    yield symTable.getVarDef(tokens.get(i).text(), pos);
                if (i > 0 && tokens.get(i - 1).type() == Token.Type.DEFINE)
                    yield symTable.getProcDef(tokens.get(i).text(), pos);
                yield null;
            }
            default -> null;
        };
        if (sym == null) return null;
        return new Location(state.getUri(), LspConverter.toRange(sym.getStart(), sym.getEnd()));
    }
}
