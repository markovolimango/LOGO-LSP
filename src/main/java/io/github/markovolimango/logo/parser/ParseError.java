package io.github.markovolimango.logo.parser;

import io.github.markovolimango.logo.lexer.Pos;

public record ParseError(
        String message,
        Pos start,
        Pos end
) {
}
