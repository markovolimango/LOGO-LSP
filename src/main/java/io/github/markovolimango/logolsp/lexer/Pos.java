package io.github.markovolimango.logolsp.lexer;

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
}
