package io.github.markovolimango.logolsp.lexer;

public record Position(
        int offs,
        int line,
        int col
) {
    public Position nextCol() {
        return new Position(offs + 1, line, col + 1);
    }

    public Position nextLine() {
        return new Position(offs, line + 1, 0);
    }
}
