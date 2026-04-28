package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Pos;

public record Symbol(
        String name,
        Pos start,
        Pos end
) {
}
