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
        Token token = Lexer.recoverTokenAt(state.getLine(pos.line()), pos.col());
        Symbol sym = switch (token.type()) {
            case VARREF -> state.getSymTable().getVarDef(token.text(), pos);
            case PROC -> state.getSymTable().getProcDef(token.text(), pos);
            default -> null;
        };
        if (sym == null) return null;
        List<TextEdit> edits = new ArrayList<>();
        edits.add(new TextEdit(LspConverter.toRange(getTextStart(state, sym.getStart()), sym.getEnd()), newName));
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
