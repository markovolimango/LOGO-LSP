package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.analysis.features.SemanticTokensProvider;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.parser.ParseError;
import org.eclipse.lsp4j.*;

import java.util.ArrayList;
import java.util.List;

public final class LspConverter {
    public static Position toPosition(Pos pos) {
        return new Position(pos.line(), pos.col());
    }

    public static Range toRange(Pos start, Pos end) {
        return new Range(toPosition(start), toPosition(end));
    }

    public static Diagnostic toDiagnostic(ParseError error) {
        Range range = toRange(error.start(), error.end());
        return new Diagnostic(range, error.message(), DiagnosticSeverity.Error, "logo");
    }

    public static Pos fromPosition(Position position) {
        return new Pos(position.getLine(), position.getCharacter());
    }

    public static SemanticTokens toSemanticTokens(List<SemanticTokensProvider.SemanticToken> tokens) {
        List<Integer> data = new ArrayList<>();
        int prevLine = 0;
        int prevCol = 0;

        for (var st : tokens) {
            Token t = st.token();
            int line = t.start().line();
            int col = t.start().col();
            int length = t.end().col() - t.start().col();

            data.add(line - prevLine);           // deltaLine
            data.add(line == prevLine ? col - prevCol : col); // deltaCol
            data.add(length);
            data.add(st.semanticType());
            data.add(0);                         // no modifiers for now

            prevLine = line;
            prevCol = col;
        }
        return new SemanticTokens(data);
    }
}
