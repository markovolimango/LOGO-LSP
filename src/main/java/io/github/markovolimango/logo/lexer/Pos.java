package io.github.markovolimango.logo.lexer;

public record Pos(
        int line,
        int col
) {
    public Pos nextCol() {
        return new Pos(line, col + 1);
    }

    public Pos nextLine() {
        return new Pos(line + 1, 0);
    }

    public boolean isAfter(Pos other) {
        return line > other.line || (line == other.line && col > other.col);
    }
}
