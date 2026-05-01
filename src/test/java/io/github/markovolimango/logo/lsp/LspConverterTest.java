package io.github.markovolimango.logo.lsp;

import io.github.markovolimango.logo.lexer.Pos;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LspConverterTest {

    @Test
    void toPosition_correctlyConverts() {
        Pos pos = new Pos(10, 20);
        Position position = LspConverter.toPosition(pos);
        assertEquals(10, position.getLine());
        assertEquals(20, position.getCharacter());
    }

    @Test
    void toRange_correctlyConverts() {
        Pos start = new Pos(1, 2);
        Pos end = new Pos(3, 4);
        Range range = LspConverter.toRange(start, end);
        assertEquals(1, range.getStart().getLine());
        assertEquals(2, range.getStart().getCharacter());
        assertEquals(3, range.getEnd().getLine());
        assertEquals(4, range.getEnd().getCharacter());
    }

    @Test
    void fromPosition_correctlyConverts() {
        Position position = new Position(5, 6);
        Pos pos = LspConverter.fromPosition(position);
        assertEquals(5, pos.line());
        assertEquals(6, pos.col());
    }
}
