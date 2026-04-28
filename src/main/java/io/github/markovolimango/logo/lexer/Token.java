package io.github.markovolimango.logo.lexer;

public record Token(
        Type type,
        String text,
        Pos start,
        Pos end    // LSP ranges need both ends
) {
    public enum Type {
        NUMBER,                 // 42, 3.14 (negative numbers stored as operator and positive number tokens)
        WORD,                   // "hello (stored without ")

        PROC,                   // built-in and user-defined procedures

        TO, END, DEFINE,        // to end define
        MAKE, LOCALMAKE, NAME,  // make localmake name
        OUTPUT,                 // output

        VARREF,                 // :varname (stored without :)

        OPERATOR,               // + - * / = < > <= >= <> % and or not

        LBRACKET, RBRACKET,     // [ ]
        LPAREN, RPAREN,         // ( )

        COMMENT,                // ;comment (stored without ;)

        ERROR,                  // unknown tokens, for error recovery

        EOF,                    // sentinel for the end of the tokens list
    }
}

