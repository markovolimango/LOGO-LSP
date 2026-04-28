package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Pos;

public sealed interface Symbol {
    String name();

    Pos start();

    Pos end();

    record Var(String name, Pos start, Pos end) implements Symbol {
    }

    record Proc(String name, Pos start, Pos end) implements Symbol {
    }
}
