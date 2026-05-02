package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.LogoLanguage;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;

public final class HoverProvider {
    public static Hover getHover(DocumentState state, Pos pos) {
        var linePos = new Pos(0, pos.col());
        var lexer = new Lexer(state.getLine(pos.line()));
        var token = lexer.getTokenAt(linePos);

        return switch (token.type()) {
            case VARREF -> {
                var sym = state.getSymTable().getVarDef(token.text(), pos);
                if (sym != null)
                    yield new Hover(
                            new MarkupContent("markdown",
                                    "Definition (line " + (sym.getStart().line() + 1) + "):\n" +
                                            "```\n" + state.getLine(sym.getStart().line()) + "\n```"
                            ),
                            LspConverter.toRange(sym.getStart(), sym.getEnd())
                    );
                yield null;
            }
            case PROC -> {
                var descr = LogoLanguage.getDescription(token.text());
                if (descr != null)
                    yield new Hover(
                            new MarkupContent("markdown", "built-in, " + getProcHover(descr))
                    );
                var sym = state.getSymTable().getProcDef(token.text(), pos);
                if (sym != null)
                    yield new Hover(
                            new MarkupContent("markdown", getProcHover(sym.getDescription())),
                            LspConverter.toRange(sym.getStart(), sym.getEnd())
                    );
                yield null;
            }
            default -> null;
        };
    }

    private static String getProcHover(String args) {
        return args.isBlank() ? "no args" : "args: `" + args + "`";
    }
}
