package io.github.markovolimango.logo.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private List<Token> lex(String source) {
        var tokens = new Lexer(source).scan();
        tokens.removeLast();
        return tokens;
    }

    private Token only(String source) {
        List<Token> tokens = lex(source);
        assertEquals(1, tokens.size(), "Expected exactly one token");
        return tokens.getFirst();
    }

    // -------------------------------------------------------------------------
    // Position
    // -------------------------------------------------------------------------

    @Test
    void position_nextCol_incrementsOffsAndCol() {
        Pos p = new Pos(5, 2, 3);
        Pos next = p.nextCol();
        assertEquals(6, next.offs());
        assertEquals(2, next.line());
        assertEquals(4, next.col());
    }

    @Test
    void position_nextLine_incrementsLineResetsCol() {
        Pos p = new Pos(5, 2, 7);
        Pos next = p.nextLine();
        assertEquals(5, next.offs());
        assertEquals(3, next.line());
        assertEquals(0, next.col());
    }

    // -------------------------------------------------------------------------
    // Numbers
    // -------------------------------------------------------------------------

    @Test
    void number_integer() {
        Token t = only("42");
        assertEquals(Token.Type.NUMBER, t.type());
        assertEquals("42", t.text());
    }

    @Test
    void number_float() {
        Token t = only("3.14");
        assertEquals(Token.Type.NUMBER, t.type());
        assertEquals("3.14", t.text());
    }

    @Test
    void number_dotAtStart() {
        Token t = only(".75");
        assertEquals(Token.Type.NUMBER, t.type());
        assertEquals(".75", t.text());
    }

    @ParameterizedTest
    @ValueSource(strings = {".", "1.", "1.2.3"})
    void number_invalidFormats_notNumber(String input) {
        // these should be lexed as PROC or ERROR, not NUMBER
        Token t = only(input);
        assertNotEquals(Token.Type.NUMBER, t.type());
    }

    // -------------------------------------------------------------------------
    // Words (quoted strings)
    // -------------------------------------------------------------------------

    @Test
    void word_stripsLeadingQuote() {
        Token t = only("\"hello");
        assertEquals(Token.Type.WORD, t.type());
        assertEquals("hello", t.text()); // stored without "
    }

    @Test
    void word_singleChar() {
        Token t = only("\"x");
        assertEquals(Token.Type.WORD, t.type());
        assertEquals("x", t.text());
    }

    @Test
    void word_stopsAtSpace() {
        List<Token> tokens = lex("\"foo bar");
        assertEquals(Token.Type.WORD, tokens.get(0).type());
        assertEquals("foo", tokens.get(0).text());
        assertEquals(Token.Type.PROC, tokens.get(1).type());
    }

    // -------------------------------------------------------------------------
    // Variable references
    // -------------------------------------------------------------------------

    @Test
    void varref_stripsLeadingColon() {
        Token t = only(":speed");
        assertEquals(Token.Type.VARREF, t.type());
        assertEquals("speed", t.text()); // stored without :
    }

    @Test
    void varref_stopsAtDelimiter() {
        List<Token> tokens = lex(":x]");
        assertEquals(Token.Type.VARREF, tokens.get(0).type());
        assertEquals("x", tokens.get(0).text());
        assertEquals(Token.Type.RBRACKET, tokens.get(1).type());
    }

    // -------------------------------------------------------------------------
    // Brackets and parens
    // -------------------------------------------------------------------------

    @Test
    void brackets_allFourTypes() {
        List<Token> tokens = lex("[]()");
        assertEquals(Token.Type.LBRACKET, tokens.get(0).type());
        assertEquals(Token.Type.RBRACKET, tokens.get(1).type());
        assertEquals(Token.Type.LPAREN, tokens.get(2).type());
        assertEquals(Token.Type.RPAREN, tokens.get(3).type());
    }

    // -------------------------------------------------------------------------
    // Operators
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {"+", "-", "*", "/", "%", "=", "<", ">", "<=", ">="})
    void operators_recognized(String op) {
        Token t = only(op);
        assertEquals(Token.Type.OPERATOR, t.type());
        assertEquals(op, t.text());
    }

    @Test
    void operator_lessThanOrEqual_twoChars() {
        Token t = only("<=");
        assertEquals(Token.Type.OPERATOR, t.type());
        assertEquals("<=", t.text());
    }

    @Test
    void operator_lessThan_standalone() {
        List<Token> tokens = lex("< 5");
        assertEquals(Token.Type.OPERATOR, tokens.getFirst().type());
        assertEquals("<", tokens.getFirst().text());
    }

    @Test
    void operators_and_or_not_areKeywordOperators() {
        for (String op : new String[]{"and", "or", "not"}) {
            Token t = only(op);
            assertEquals(Token.Type.OPERATOR, t.type(), op + " should be OPERATOR");
        }
    }

    // -------------------------------------------------------------------------
    // Keywords
    // -------------------------------------------------------------------------

    @Test
    void keyword_to() {
        assertEquals(Token.Type.TO, only("to").type());
    }

    @Test
    void keyword_end() {
        assertEquals(Token.Type.END, only("end").type());
    }

    @Test
    void keyword_define() {
        assertEquals(Token.Type.DEFINE, only("define").type());
    }

    @Test
    void keyword_make() {
        assertEquals(Token.Type.MAKE, only("make").type());
    }

    @Test
    void keyword_localmake() {
        assertEquals(Token.Type.LOCALMAKE, only("localmake").type());
    }

    @Test
    void keyword_name() {
        assertEquals(Token.Type.NAME, only("name").type());
    }

    @Test
    void keyword_output() {
        assertEquals(Token.Type.OUTPUT, only("output").type());
    }

    @Test
    void keyword_op_aliasForOutput() {
        assertEquals(Token.Type.OUTPUT, only("op").type());
    }

    @ParameterizedTest
    @ValueSource(strings = {"TO", "To", "END", "End", "MAKE", "Make", "OUTPUT", "Output"})
    void keywords_caseInsensitive(String input) {
        Token t = only(input);
        assertNotEquals(Token.Type.PROC, t.type(), input + " should be a keyword, not PROC");
    }

    // -------------------------------------------------------------------------
    // Procedures (unrecognized identifiers)
    // -------------------------------------------------------------------------

    @Test
    void proc_unknownIdentifier() {
        Token t = only("forward");
        assertEquals(Token.Type.PROC, t.type());
        assertEquals("forward", t.text());
    }

    @Test
    void proc_userDefinedName() {
        Token t = only("myproc");
        assertEquals(Token.Type.PROC, t.type());
    }

    // -------------------------------------------------------------------------
    // Comments
    // -------------------------------------------------------------------------

    @Test
    void comment_stripsLeadingSemicolon() {
        Token t = only("; this is a comment");
        assertEquals(Token.Type.COMMENT, t.type());
        assertEquals(" this is a comment", t.text()); // stored without ;
    }

    @Test
    void comment_stopsAtNewline() {
        List<Token> tokens = lex(";comment\nforward");
        assertEquals(Token.Type.COMMENT, tokens.getFirst().type());
        assertEquals("comment", tokens.getFirst().text());
        assertEquals(Token.Type.PROC, tokens.get(1).type());
    }

    @Test
    void comment_emptyComment() {
        Token t = only(";");
        assertEquals(Token.Type.COMMENT, t.type());
        assertEquals("", t.text());
    }

    // -------------------------------------------------------------------------
    // Whitespace handling
    // -------------------------------------------------------------------------

    @Test
    void whitespace_spacesAndTabsProduceNoTokens() {
        List<Token> tokens = lex("   \t  ");
        assertTrue(tokens.isEmpty());
    }

    @Test
    void whitespace_newlineProducesNoTokens() {
        List<Token> tokens = lex("\n\n");
        assertTrue(tokens.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Positions tracked correctly
    // -------------------------------------------------------------------------

    @Test
    void position_singleTokenStartsAtOrigin() {
        Token t = only("forward");
        assertEquals(0, t.start().offs());
        assertEquals(0, t.start().line());
        assertEquals(0, t.start().col());
    }

    @Test
    void position_secondTokenOnSameLine() {
        List<Token> tokens = lex("fd 50");
        Token second = tokens.get(1);
        assertEquals(0, second.start().line());
        assertEquals(3, second.start().col());
    }

    @Test
    void position_tokenOnSecondLine() {
        List<Token> tokens = lex("fd\n50");
        Token second = tokens.get(1);
        assertEquals(1, second.start().line());
        assertEquals(0, second.start().col());
    }

    @Test
    void position_endFollowsStart() {
        Token t = only("forward");
        assertTrue(t.end().offs() > t.start().offs());
        assertEquals(t.start().line(), t.end().line());
        assertEquals(t.start().col() + t.text().length(), t.end().col());
    }

    @Test
    void position_varrefStartPointsAtColon_endPointsPastName() {
        Token t = only(":speed");
        assertEquals(0, t.start().offs()); // start is at ':'
        assertEquals(6, t.end().offs());   // end is past 'speed'
        assertEquals("speed", t.text());   // but text excludes ':'
    }

    // -------------------------------------------------------------------------
    // Multi-token sequences (realistic Logo snippets)
    // -------------------------------------------------------------------------

    @Test
    void sequence_makeVariable() {
        // make "foo 5
        List<Token> tokens = lex("make \"foo 5");
        assertEquals(Token.Type.MAKE, tokens.get(0).type());
        assertEquals(Token.Type.WORD, tokens.get(1).type());
        assertEquals("foo", tokens.get(1).text());
        assertEquals(Token.Type.NUMBER, tokens.get(2).type());
        assertEquals("5", tokens.get(2).text());
    }

    @Test
    void sequence_repeatLoop() {
        // repeat 4 [ fd 50 rt 90 ]
        List<Token> tokens = lex("repeat 4 [ fd 50 rt 90 ]");
        assertEquals(Token.Type.PROC, tokens.get(0).type());
        assertEquals(Token.Type.NUMBER, tokens.get(1).type());
        assertEquals(Token.Type.LBRACKET, tokens.get(2).type());
        assertEquals(Token.Type.PROC, tokens.get(3).type()); // fd
        assertEquals(Token.Type.NUMBER, tokens.get(4).type());
        assertEquals(Token.Type.PROC, tokens.get(5).type()); // rt
        assertEquals(Token.Type.NUMBER, tokens.get(6).type());
        assertEquals(Token.Type.RBRACKET, tokens.get(7).type());
    }

    @Test
    void sequence_procedureDefinition() {
        // to square\n  repeat 4 [fd 50 rt 90]\nend
        List<Token> tokens = lex("to square\nrepeat 4 [fd 50 rt 90]\nend");
        assertEquals(Token.Type.TO, tokens.get(0).type());
        assertEquals(Token.Type.PROC, tokens.get(1).type());
        assertEquals("square", tokens.get(1).text());
        assertEquals(Token.Type.END, tokens.getLast().type());
    }

    @Test
    void sequence_varrefInExpression() {
        // make "x 5\nforward :x
        List<Token> tokens = lex("make \"x 5\nforward :x");
        Token varref = tokens.get(4);
        assertEquals(Token.Type.VARREF, varref.type());
        assertEquals("x", varref.text());
    }

    @Test
    void sequence_emptySource() {
        assertTrue(lex("").isEmpty());
    }

    // -------------------------------------------------------------------------
    // Delimiter and Operator Seperation (No Spaces)
    // -------------------------------------------------------------------------

    @Test
    void varref_stopsAtOperator() {
        // :a+ should be VARREF(a) and OPERATOR(+)
        List<Token> tokens = lex(":a+");
        assertEquals(2, tokens.size());

        assertEquals(Token.Type.VARREF, tokens.get(0).type());
        assertEquals("a", tokens.get(0).text());

        assertEquals(Token.Type.OPERATOR, tokens.get(1).type());
        assertEquals("+", tokens.get(1).text());
    }

    @Test
    void varref_stopsAtParenthesis() {
        // (:a) should be LPAREN, VARREF(a), RPAREN
        List<Token> tokens = lex("(:a)");
        assertEquals(3, tokens.size());

        assertEquals(Token.Type.LPAREN, tokens.get(0).type());

        assertEquals(Token.Type.VARREF, tokens.get(1).type());
        assertEquals("a", tokens.get(1).text());

        assertEquals(Token.Type.RPAREN, tokens.get(2).type());
    }

    @Test
    void complex_expression_no_spaces() {
        // (:a+:b)*(:c+:d)
        List<Token> tokens = lex("(:a+:b)*(:c+:d)");

        // Expected sequence of types:
        // ( :a + :b ) * ( :c + :d )
        Token.Type[] expectedTypes = {
                Token.Type.LPAREN, Token.Type.VARREF, Token.Type.OPERATOR,
                Token.Type.VARREF, Token.Type.RPAREN, Token.Type.OPERATOR,
                Token.Type.LPAREN, Token.Type.VARREF, Token.Type.OPERATOR,
                Token.Type.VARREF, Token.Type.RPAREN
        };

        assertEquals(expectedTypes.length, tokens.size(), "Token count mismatch");
        for (int i = 0; i < expectedTypes.length; i++) {
            assertEquals(expectedTypes[i], tokens.get(i).type(), "Mismatch at index " + i);
        }

        assertEquals("d", tokens.get(9).text(), "Variable 'd' should not include the closing parenthesis");
    }

    @Test
    void word_doesntStopsAtOperator() {
        // "foo+bar should be WORD(foo) and OPERATOR(+) and PROC(bar)
        List<Token> tokens = lex("\"foo+bar");
        assertEquals(1, tokens.size());
        assertEquals("foo+bar", tokens.getFirst().text());
    }

    @Test
    void varref_with_multiple_operators() {
        // :x*:y/:z
        List<Token> tokens = lex(":x*:y/:z");
        assertEquals(5, tokens.size());
        assertEquals("x", tokens.get(0).text());
        assertEquals("*", tokens.get(1).text());
        assertEquals("y", tokens.get(2).text());
        assertEquals("/", tokens.get(3).text());
        assertEquals("z", tokens.get(4).text());
    }
}