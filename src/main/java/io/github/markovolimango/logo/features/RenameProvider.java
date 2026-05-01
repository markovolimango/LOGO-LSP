package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.analysis.Symbol;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.TextEdit;

import java.util.ArrayList;
import java.util.List;

public class RenameProvider {
    public static List<TextEdit> getRenameEdits(DocumentState state, Pos pos, String newName) {
        var lexer = new Lexer(state.getLine(pos.line()));
        var token = lexer.getTokenAt(new Pos(0, pos.col()));

        if (token.text().isBlank()) return null;

        Symbol sym = switch (token.type()) {
            case VARREF -> state.getSymTable().getVarDef(token.text(), pos);
            case PROC -> {
                if (lexer.isDefineParam(token))
                    yield state.getSymTable().getVarDef(token.text(), pos);
                yield state.getSymTable().getProcDef(token.text(), pos);
            }
            case WORD -> {
                int i = lexer.getIndexFromPos(new Pos(0, pos.col()));
                var tokens = lexer.getTokens();
                if ((i > 0 &&
                        (tokens.get(i - 1).type() == Token.Type.MAKE || tokens.get(i - 1).type() == Token.Type.LOCALMAKE))
                        || (i > 1 && tokens.get(i - 1).type() == Token.Type.NAME))
                    yield state.getSymTable().getVarDef(tokens.get(i).text(), pos);
                if (i > 0 && tokens.get(i - 1).type() == Token.Type.DEFINE)
                    yield state.getSymTable().getProcDef(tokens.get(i).text(), pos);
                yield null;
            }
            default -> null;
        };
        if (sym == null) return null;
        List<TextEdit> edits = new ArrayList<>();
        for (Pos[] ref : sym.getReferences())
            edits.add(new TextEdit(LspConverter.toRange(getTextStart(state, ref[0]), ref[1]), newName));
        return edits;
    }

    private static Pos getTextStart(DocumentState state, Pos start) {
        return switch (state.getCharAt(start)) {
            case ':', '"' -> start.nextCol();
            default -> start;
        };
    }
}
