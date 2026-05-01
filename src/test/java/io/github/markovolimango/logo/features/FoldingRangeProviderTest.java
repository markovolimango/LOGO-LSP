package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.FoldingRange;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FoldingRangeProviderTest {

    @Test
    void toStmt_rangeProvided() {
        String source = "to myproc\n  print 1\nend";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<FoldingRange> ranges = FoldingRangeProvider.findFoldingRanges(state);

        assertEquals(1, ranges.size());
        assertEquals(0, ranges.getFirst().getStartLine());
        assertEquals(2, ranges.getFirst().getEndLine());
    }

    @Test
    void block_rangeProvided() {
        String source = "repeat 4 [\n  fd 100\n  rt 90\n]";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<FoldingRange> ranges = FoldingRangeProvider.findFoldingRanges(state);

        // One for the block
        assertTrue(ranges.stream().anyMatch(r -> r.getStartLine() == 0 && r.getEndLine() == 3));
    }

    @Test
    void nestedBlocks_rangesProvided() {
        String source = "repeat 4 [\n  repeat 4 [\n    fd 100\n  ]\n]";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<FoldingRange> ranges = FoldingRangeProvider.findFoldingRanges(state);

        assertEquals(2, ranges.size());
        assertTrue(ranges.stream().anyMatch(r -> r.getStartLine() == 0 && r.getEndLine() == 4));
        assertTrue(ranges.stream().anyMatch(r -> r.getStartLine() == 1 && r.getEndLine() == 3));
    }

    @Test
    void defineStmt_rangeProvided() {
        String source = "define \"myproc [\n  [a b]\n  [print :a + :b]\n]";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<FoldingRange> ranges = FoldingRangeProvider.findFoldingRanges(state);

        // One for DefineStmt, and likely some for blocks inside
        assertTrue(ranges.stream().anyMatch(r -> r.getStartLine() == 0 && r.getEndLine() == 3));
    }
}
