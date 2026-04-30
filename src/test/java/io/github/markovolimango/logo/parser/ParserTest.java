package io.github.markovolimango.logo.parser;

import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private ParseResult parse(String source) {
        List<Token> tokens = new Lexer(source).getTokens();
        Parser parser = new Parser(tokens);
        Node.Program program = parser.parseProgram();
        return new ParseResult(program, parser);
    }

    /**
     * Convenience when only the Program node is needed.
     */
    private Node.Program parseProgram(String source) {
        return parse(source).program();
    }

    /**
     * Convenience when only errors are needed.
     */
    private Parser parseWithErrors(String source) {
        return parse(source).parser();
    }

    @Test
    void emptyProgram_doesNotThrow() {
        // Bug guard: body.getFirst() on an empty list must not throw
        assertDoesNotThrow(() -> parseProgram(""));
    }

    // =========================================================================
    // Program-level
    // =========================================================================

    @Test
    void emptyProgram_hasEmptyBody() {
        Node.Program program = parseProgram("");
        assertTrue(program.body().isEmpty());
    }

    @Test
    void singleStatement_bodyHasOneNode() {
        Node.Program program = parseProgram("forward 50");
        assertEquals(1, program.body().size());
    }

    @Test
    void multipleStatements_bodyHasCorrectCount() {
        Node.Program program = parseProgram("forward 50\nback 30\nleft 90");
        assertEquals(3, program.body().size());
    }

    @Test
    void numberLiteral_parsedAsNumber() {
        Node.Program program = parseProgram("42");
        assertInstanceOf(Node.Number.class, program.body().getFirst());
    }

    // =========================================================================
    // Numbers and words (atoms)
    // =========================================================================

    @Test
    void wordLiteral_parsedAsWord() {
        Node.Program program = parseProgram("\"hello");
        assertInstanceOf(Node.Word.class, program.body().getFirst());
    }

    @Test
    void varRef_parsedAsVarRef() {
        Node.Program program = parseProgram(":foo");
        assertInstanceOf(Node.VarRef.class, program.body().getFirst());
    }

    @Test
    void simpleAddition_parsedAsInfixExpr() {
        Node.Program program = parseProgram("3 + 4");
        assertInstanceOf(Node.InfixExpr.class, program.body().getFirst());
    }

    // =========================================================================
    // Infix expressions
    // =========================================================================

    @Test
    void infixExpr_hasCorrectOperator() {
        Node.Program program = parseProgram("3 + 4");
        Node.InfixExpr expr = (Node.InfixExpr) program.body().getFirst();
        assertEquals("+", expr.op().text());
    }

    @Test
    void infixExpr_leftAndRightAreNumbers() {
        Node.Program program = parseProgram("3 + 4");
        Node.InfixExpr expr = (Node.InfixExpr) program.body().getFirst();
        assertInstanceOf(Node.Number.class, expr.left());
        assertInstanceOf(Node.Number.class, expr.right());
    }

    @Test
    void operatorPrecedence_multiplyBindsTighterThanAdd() {
        // 2 + 3 * 4  should parse as  2 + (3 * 4)
        Node.Program program = parseProgram("2 + 3 * 4");
        Node.InfixExpr top = (Node.InfixExpr) program.body().getFirst();
        assertEquals("+", top.op().text());
        assertInstanceOf(Node.InfixExpr.class, top.right());
        assertEquals("*", ((Node.InfixExpr) top.right()).op().text());
    }

    @Test
    void operatorPrecedence_complex() {
        // 1 + 2 * 3 - 4 / 5  should be (1 + (2 * 3)) - (4 / 5)
        Node.Program program = parseProgram("1 + 2 * 3 - 4 / 5");
        Node.InfixExpr top = (Node.InfixExpr) program.body().getFirst();
        assertEquals("-", top.op().text());

        Node.InfixExpr left = (Node.InfixExpr) top.left();
        assertEquals("+", left.op().text());
        assertEquals("1", ((Node.Number) left.left()).value().text());
        assertEquals("*", ((Node.InfixExpr) left.right()).op().text());

        Node.InfixExpr right = (Node.InfixExpr) top.right();
        assertEquals("/", right.op().text());
    }

    @Test
    void parentheses_overridePrecedence() {
        // (2 + 3) * 4 — top operator must be *
        Node.Program program = parseProgram("(2 + 3) * 4");
        Node.InfixExpr top = (Node.InfixExpr) program.body().getFirst();
        assertEquals("*", top.op().text());
    }

    @Test
    void prefixMinus_parsedAsPrefixExpr() {
        Node.Program program = parseProgram("-5");
        assertInstanceOf(Node.PrefixExpr.class, program.body().getFirst());
    }

    @Test
    void prefixNot_parsedAsPrefixExpr() {
        Node.Program program = parseProgram("not 1");
        assertInstanceOf(Node.PrefixExpr.class, program.body().getFirst());
    }

    @Test
    void forwardCall_parsedAsProcCall() {
        Node.Program program = parseProgram("forward 50");
        assertInstanceOf(Node.ProcCall.class, program.body().getFirst());
    }

    // =========================================================================
    // ProcCall — built-in procedures
    // =========================================================================

    @Test
    void forwardCall_hasOneArgument() {
        Node.ProcCall call = (Node.ProcCall) parseProgram("forward 50").body().getFirst();
        assertEquals(1, call.args().size());
    }

    @Test
    void setxyCall_hasTwoArguments() {
        Node.ProcCall call = (Node.ProcCall) parseProgram("setxy 10 20").body().getFirst();
        assertEquals(2, call.args().size());
    }

    @Test
    void homeCall_hasZeroArguments() {
        Node.ProcCall call = (Node.ProcCall) parseProgram("home").body().getFirst();
        assertEquals(0, call.args().size());
    }

    @Test
    void ifelseCall_hasThreeArguments() {
        Node.ProcCall call = (Node.ProcCall) parseProgram("ifelse 1 > 0 [forward 10] [back 10]").body().getFirst();
        assertEquals(3, call.args().size());
    }

    @Test
    void listCall_variadic() {
        // (list 1 2 3 4)
        Node.Program program = parseProgram("(list 1 2 3 4)");
        Node.ProcCall call = (Node.ProcCall) program.body().getFirst();
        assertEquals("list", call.name().text());
        assertEquals(4, call.args().size());
    }

    @Test
    void repeatCall_bodyIsBlock() {
        Node.ProcCall call = (Node.ProcCall) parseProgram("repeat 4 [forward 50 right 90]").body().getFirst();
        assertInstanceOf(Node.Block.class, call.args().get(1));
    }

    @Test
    void nestedProcCall_parsedCorrectly() {
        // show sum 2 3  — sum is an argument to show
        Node.Program program = parseProgram("show sum 2 3");
        Node.ProcCall show = (Node.ProcCall) program.body().getFirst();
        assertEquals("show", show.name().text());
        assertInstanceOf(Node.ProcCall.class, show.args().getFirst());
    }

    @Test
    void emptyBlock_parsedSuccessfully() {
        Node.Program program = parseProgram("[]");
        assertInstanceOf(Node.Block.class, program.body().getFirst());
        assertTrue(((Node.Block) program.body().getFirst()).body().isEmpty());
    }

    // =========================================================================
    // Block
    // =========================================================================

    @Test
    void blockWithStatements_hasCorrectSize() {
        Node.Block block = (Node.Block) parseProgram("[forward 10 back 5]").body().getFirst();
        assertEquals(2, block.body().size());
    }

    @Test
    void makeStmt_parsedAsMakeStmt() {
        Node.Program program = parseProgram("make \"x 5");
        assertInstanceOf(Node.MakeStmt.class, program.body().getFirst());
    }

    // =========================================================================
    // MakeStmt / NameStmt
    // =========================================================================

    @Test
    void makeStmt_nameAndValueAreCorrect() {
        Node.MakeStmt stmt = (Node.MakeStmt) parseProgram("make \"x 5").body().getFirst();
        assertInstanceOf(Node.Word.class, stmt.name());
        assertInstanceOf(Node.Number.class, stmt.value());
    }

    @Test
    void nameStmt_hasReversedOperandOrder() {
        // name 5 "x  — value first, then name
        Node.MakeStmt stmt = (Node.MakeStmt) parseProgram("name 5 \"x").body().getFirst();
        assertInstanceOf(Node.Number.class, stmt.value());
        assertInstanceOf(Node.Word.class, stmt.name());
    }

    @Test
    void localMakeStmt_parsedAsLocalMakeStmt() {
        Node.Program program = parseProgram("localmake \"counter 0");
        assertInstanceOf(Node.LocalMakeStmt.class, program.body().getFirst());
    }

    @Test
    void outputStmt_parsedAsOutputStmt() {
        // output is only valid inside a procedure body, but the parser should still
        // parse it structurally
        Node.Program program = parseProgram("output :x");
        assertInstanceOf(Node.OutputStmt.class, program.body().getFirst());
    }

    // =========================================================================
    // OutputStmt
    // =========================================================================

    @Test
    void outputStmt_valueIsVarRef() {
        Node.OutputStmt stmt = (Node.OutputStmt) parseProgram("output :result").body().getFirst();
        assertInstanceOf(Node.VarRef.class, stmt.value());
    }

    @Test
    void toStmt_parsedAsToStmt() {
        Node.Program program = parseProgram("to square :n repeat 4 [forward :n right 90] end");
        assertInstanceOf(Node.ToStmt.class, program.body().getFirst());
    }

    // =========================================================================
    // ToStmt (procedure definition via "to ... end")
    // =========================================================================

    @Test
    void toStmt_nameIsCorrect() {
        Node.ToStmt stmt = (Node.ToStmt) parseProgram("to square :n repeat 4 [forward :n right 90] end").body().getFirst();
        assertEquals("square", stmt.name().text());
    }

    @Test
    void toStmt_paramsAreCaptured() {
        Node.ToStmt stmt = (Node.ToStmt) parseProgram("to square :n repeat 4 [forward :n right 90] end").body().getFirst();
        assertEquals(1, stmt.params().size());
        assertEquals("n", stmt.params().getFirst().text());
    }

    @Test
    void toStmt_bodyIsCorrectSize() {
        Node.ToStmt stmt = (Node.ToStmt) parseProgram("to square :n repeat 4 [forward :n right 90] end").body().getFirst();
        assertEquals(1, stmt.body().size());
    }

    @Test
    void toStmt_noParams_emptyParamList() {
        Node.ToStmt stmt = (Node.ToStmt) parseProgram("to greet print \"hello end").body().getFirst();
        assertTrue(stmt.params().isEmpty());
    }

    @Test
    void toStmt_multipleParams() {
        Node.ToStmt stmt = (Node.ToStmt) parseProgram("to add :a :b output :a + :b end").body().getFirst();
        assertEquals(2, stmt.params().size());
    }

    @Test
    void toStmt_calledAfterDefinition_noError() {
        // The parser must register arity from 'to', so a subsequent call is valid
        Parser parser = parseWithErrors("to square :n repeat 4 [forward :n right 90] end\nsquare 50");
        assertTrue(parser.getErrors().isEmpty(),
                "Expected no errors but got: " + parser.getErrors());
    }

    @Test
    void toStmt_missingEnd_addsError() {
        Parser parser = parseWithErrors("to square :n forward :n");
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    void defineStmt_parsedAsDefineStmt() {
        Node.Program program = parseProgram("define \"star [[n] [repeat 5 [forward :n right 144]]]");
        assertInstanceOf(Node.DefineStmt.class, program.body().getFirst());
    }

    // =========================================================================
    // DefineStmt
    // =========================================================================

    @Test
    void defineStmt_wrongBlockStructure_addsError() {
        // Only one inner list instead of two
        Parser parser = parseWithErrors("define \"bad [[n]]");
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    void procCall_startPositionIsNameToken() {
        Node.ProcCall call = (Node.ProcCall) parseProgram("forward 50").body().getFirst();
        // Column should be 0 (first char), line 0 or 1 depending on your Pos convention
        assertEquals(0, call.start().col());
    }

    // =========================================================================
    // Source positions
    // =========================================================================

    @Test
    void infixExpr_spansCoversBothOperands() {
        Node.InfixExpr expr = (Node.InfixExpr) parseProgram("3 + 4").body().getFirst();
        assertTrue(expr.start().col() <= expr.end().col());
    }

    // =========================================================================
    // Error recovery — parser continues after an error
    // =========================================================================

    @Test
    void recovery_missingArgumentDoesNotBlockNextStatement() {
        // [ forward ] back 10
        // If we put it in a block, maybe we can see it failing inside but continuing after
        Parser parser = new Parser(new Lexer("[ forward ] back 10").getTokens());
        Node.Program program = parser.parseProgram();

        boolean foundBack = program.body().stream()
                .anyMatch(n -> n instanceof Node.ProcCall pc && pc.name().text().equalsIgnoreCase("back"));
        assertTrue(foundBack);
    }

    @Test
    void comment_doesNotProduceNode() {
        // Assumes ; starts a line comment — adjust if your lexer uses a different convention
        Node.Program program = parseProgram("; this is a comment\nforward 50");
        new AstPrinter().print(program);
        assertEquals(1, program.body().size());
        assertInstanceOf(Node.ProcCall.class, program.body().getFirst());
    }

    // =========================================================================
    // Comments (should be silently skipped)
    // =========================================================================

    private record ParseResult(Node.Program program, Parser parser) {
        List<ParseError> errors() {
            return parser.getErrors();
        }
    }
}