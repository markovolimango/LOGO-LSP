package io.github.markovolimango.logolsp.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private Position curr = new Position(0, 0, 0);
    private Position start;

    public Lexer(String source) {
        this.source = source + "\0\0";
    }

    static private boolean isWordChar(char c) {
        return "\"[] \n\r\t;\0".indexOf(c) == -1;
    }

    private static boolean isDelimiter(char c) {
        return "\"[]() \t\n\r\0".indexOf(c) != -1;
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
        if (s[i - 1] < '0' || '9' < s[i - 1])
            return false;
        return true;
    }

    public List<Token> scan() {
        while (curr.offs() < source.length() - 2) {
            start = curr;
            char c = consume();
            switch (c) {
                case ' ', '\t' -> {
                }
                case '\n' -> curr = curr.nextLine();
                case '\r' -> {
                    if (peek() != '\n') curr = curr.nextLine();
                }
                case '"', ':' -> {
                    while (isWordChar(peek())) consume();
                    addToken(c == '"' ? Token.Type.WORD : Token.Type.VARREF, start.offs() + 1);
                }
                case ';' -> {
                    while (peek() != '\n' && peek() != '\r') consume();
                    addToken(Token.Type.COMMENT, start.offs() + 1);
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
                    while (!isDelimiter(peek())) consume();
                    String text = source.substring(start.offs(), curr.offs());
                    if (Keywords.map.containsKey(text.toLowerCase()))
                        addToken(Keywords.map.get(text.toLowerCase()));
                    else if (isNumber(text))
                        addToken(Token.Type.NUMBER);
                    else
                        addToken(Token.Type.PROC);
                }

            }
        }
        return tokens;
    }

    private char peek() {
        return source.charAt(curr.offs());
    }

    private char consume() {
        char c = source.charAt(curr.offs());
        curr = curr.nextCol();
        return c;
    }

    private void addToken(Token.Type type) {
        tokens.add(new Token(type, source.substring(start.offs(), curr.offs()), start, curr));
    }

    private void addToken(Token.Type type, int textStartOffs) {
        tokens.add(new Token(type, source.substring(textStartOffs, curr.offs()), start, curr));
    }
}
