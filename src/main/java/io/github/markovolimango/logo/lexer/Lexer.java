package io.github.markovolimango.logo.lexer;

import io.github.markovolimango.logo.LogoLanguage;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private Pos currPos = new Pos(0, 0), startPos;
    private int currOffs = 0, startOffs;
    private boolean isInsideTo = false;

    public Lexer(String source) {
        this.source = source + "\0\0";
    }

    static private boolean isWordChar(char c) {
        return "\"[]() \t\n\r;\0".indexOf(c) == -1;
    }

    public static boolean isNotDelimiter(char c) {
        return "\"[]() \t\n\r\0+-*/%<>=;".indexOf(c) == -1;
    }

    private static boolean isNumber(String string) {
        int i = 0, len = string.length() + 1;

        char[] s = new char[len];
        string.getChars(0, len - 1, s, 0);
        s[len - 1] = '\0';

        while ('0' <= s[i] && s[i] <= '9') i++;
        if (s[i] == '.') i++;
        while ('0' <= s[i] && s[i] <= '9') i++;
        if (s[i] != '\0')
            return false;
        return s[i - 1] >= '0' && '9' >= s[i - 1];
    }

    public static Token recoverTokenAt(String line, int col) {
        var lineTokens = new Lexer(line).tokenize();
        for (var token : lineTokens)
            if (token.start().col() <= col && col <= token.end().col())
                return token;
        return new Token(Token.Type.EOF, "", new Pos(0, 0), new Pos(0, 0));
    }

    public static boolean isDefineParamAt(int i, List<Token> tokens) {
        if (tokens.getFirst().type() != Token.Type.DEFINE) return false;
        int j = 0;
        while (j < tokens.size() && tokens.get(j).type() != Token.Type.LBRACKET) j++;
        if (j >= tokens.size() - 2) return false;
        if (i == j + 1) return true;
        if (tokens.get(j + 1).type() != Token.Type.LBRACKET) return false;
        j++;
        while (j < tokens.size() && tokens.get(j).type() != Token.Type.RBRACKET)
            if (i == j++) return true;
        return false;
    }

    public List<Token> tokenize() {
        while (currOffs < source.length() - 2) {
            startPos = currPos;
            startOffs = currOffs;
            char c = consume();
            switch (c) {
                case ' ', '\t' -> {
                }
                case '\n' -> currPos = currPos.nextLine();
                case '\r' -> {
                    if (peek() != '\n') currPos = currPos.nextLine();
                }
                case '"' -> {
                    while (isWordChar(peek())) consume();
                    addToken(Token.Type.WORD, startOffs + 1);
                }
                case ':' -> {
                    while (isNotDelimiter(peek())) consume();
                    addToken(Token.Type.VARREF, startOffs + 1);
                }
                case ';' -> {
                    while (peek() != '\n' && peek() != '\r' && peek() != '\0') consume();
                    addToken(Token.Type.COMMENT, startOffs + 1);
                }
                case '[' -> addToken(Token.Type.LBRACKET);
                case ']' -> addToken(Token.Type.RBRACKET);
                case '(' -> addToken(Token.Type.LPAREN);
                case ')' -> addToken(Token.Type.RPAREN);
                case '+', '-', '*', '%', '/', '=' -> addToken(Token.Type.OPERATOR);
                case '>', '<' -> {
                    if (peek() == '=') consume();
                    addToken(Token.Type.OPERATOR);
                }
                default -> {
                    while (isNotDelimiter(peek())) consume();
                    String text = source.substring(startOffs, currOffs);
                    var type = LogoLanguage.getTokenType(text);
                    if (type != null && (type != Token.Type.TO || !isInsideTo)) {
                        addToken(type);
                        if (type == Token.Type.TO) isInsideTo = true;
                        else if (type == Token.Type.END) isInsideTo = false;
                    } else if (isNumber(text))
                        addToken(Token.Type.NUMBER);
                    else
                        addToken(Token.Type.PROC);
                }

            }
        }
        tokens.add(new Token(Token.Type.EOF, "", currPos.nextLine(), currPos.nextLine()));
        return tokens;
    }

    private char peek() {
        return source.charAt(currOffs);
    }

    private char consume() {
        char c = source.charAt(currOffs);
        currPos = currPos.nextCol();
        currOffs++;
        return c;
    }

    private void addToken(Token.Type type) {
        tokens.add(new Token(type, source.substring(startOffs, currOffs), startPos, currPos));
    }

    private void addToken(Token.Type type, int textStartOffs) {
        tokens.add(new Token(type, source.substring(textStartOffs, currOffs), startPos, currPos));
    }
}
