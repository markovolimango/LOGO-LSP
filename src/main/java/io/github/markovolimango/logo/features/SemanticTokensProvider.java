package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.LogoLanguage;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.SemanticTokens;

import java.util.ArrayList;
import java.util.List;

public final class SemanticTokensProvider {
    // Token type indices - must match the legend declared in ServerCapabilities
    public static final int TYPE_KEYWORD = 0;
    public static final int TYPE_VARIABLE = 1;
    public static final int TYPE_FUNCTION = 2;
    public static final int TYPE_OPERATOR = 3;
    public static final int TYPE_NUMBER = 4;
    public static final int TYPE_STRING = 5;
    public static final int TYPE_COMMENT = 6;
    public static final List<String> TOKEN_TYPES = List.of("keyword", "variable", "function", "operator", "number", "string", "comment");

    public static final int MOD_NONE = 0;
    public static final int MOD_DECLARATION = 1;  // 1
    public static final int MOD_DEFAULT_LIB = 1 << 1;  // 2

    public static final List<String> TOKEN_MODIFIERS = List.of("declaration", "defaultLibrary");

    public static SemanticTokens getSemanticTokens(DocumentState state) {
        List<Integer> result = new ArrayList<>();
        int prevLine = 0, prevCol = 0;
        for (Token t : state.getTokens()) {
            int line = t.start().line(), col = t.start().col();

            Integer semanticType = switch (t.type()) {
                case NUMBER -> TYPE_NUMBER;
                case WORD -> TYPE_STRING;
                case PROC, DEFINE, MAKE, LOCALMAKE, NAME, OUTPUT -> TYPE_FUNCTION;
                case TO, END -> TYPE_KEYWORD;
                case VARREF -> TYPE_VARIABLE;
                case OPERATOR -> TYPE_OPERATOR;
                case LBRACKET, RBRACKET, LPAREN, RPAREN -> TYPE_OPERATOR;
                case COMMENT -> TYPE_COMMENT;
                case EOF -> null;
            };
            Integer smeanticModifier = switch (t.type()) {
                case PROC, DEFINE, MAKE, LOCALMAKE, NAME, OUTPUT ->
                        LogoLanguage.isBuiltin(t.text()) ? MOD_NONE : MOD_DEFAULT_LIB;
                default -> MOD_NONE;
            };

            result.add(line - prevLine);
            result.add(line == prevLine ? col - prevCol : col);
            result.add(t.end().col() - t.start().col());
            result.add(semanticType);
            result.add(smeanticModifier);
        }
        return new SemanticTokens(result);
    }
}