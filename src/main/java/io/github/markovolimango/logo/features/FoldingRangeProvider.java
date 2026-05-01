package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.parser.AstWalker;
import io.github.markovolimango.logo.parser.Node;
import org.eclipse.lsp4j.FoldingRange;

import java.util.ArrayList;
import java.util.List;

public final class FoldingRangeProvider {
    public static List<FoldingRange> findFoldingRanges(DocumentState state) {
        var finder = new FoldingRangeFinder();
        finder.walk(state.getAst());
        return finder.getRanges();
    }

    private static class FoldingRangeFinder extends AstWalker {
        private final List<FoldingRange> ranges = new ArrayList<>();

        public List<FoldingRange> getRanges() {
            return ranges;
        }

        @Override
        public void walk(Node node) {
            switch (node) {
                case Node.ToStmt t -> {
                    ranges.add(new FoldingRange(t.start().line(), t.end().line()));
                    super.walk(t);
                }
                case Node.Block b -> {
                    ranges.add(new FoldingRange(b.start().line(), b.end().line()));
                    super.walk(b);
                }
                case Node.DefineStmt d -> {
                    ranges.add(new FoldingRange(d.start().line(), d.end().line()));
                    super.walk(d);
                }
                default -> super.walk(node);
            }
        }
    }
}
