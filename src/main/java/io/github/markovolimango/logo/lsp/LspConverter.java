package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.parser.ParseError;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

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
}
