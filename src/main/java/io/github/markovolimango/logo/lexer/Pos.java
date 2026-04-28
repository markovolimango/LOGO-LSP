package io.github.markovolimango.logo.lexer;

public record Pos(
        int offs,
        int line,
        int col
) {
    public Pos nextCol() {
        return new Pos(offs + 1, line, col + 1);
    }

    public Pos nextLine() {
        return new Pos(offs, line + 1, 0);
    }

    public boolean isAfter(Pos other) {
        return line > other.line || (line == other.line && col > other.col);
    }
}
