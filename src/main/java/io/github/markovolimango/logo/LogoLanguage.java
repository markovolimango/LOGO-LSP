package io.github.markovolimango.logo;

import io.github.markovolimango.logo.lexer.Token;

import java.util.Map;

import static java.util.Map.entry;

public final class LogoLanguage {
    private static final Map<String, Token.Type> KEYWORDS = Map.ofEntries(
            Map.entry("to", Token.Type.TO),
            Map.entry("end", Token.Type.END),
            Map.entry("define", Token.Type.DEFINE),

            Map.entry("make", Token.Type.MAKE),
            Map.entry("localmake", Token.Type.LOCALMAKE),
            Map.entry("name", Token.Type.NAME),

            Map.entry("output", Token.Type.OUTPUT),
            Map.entry("op", Token.Type.OUTPUT),

            Map.entry("and", Token.Type.OPERATOR),
            Map.entry("or", Token.Type.OPERATOR),
            Map.entry("not", Token.Type.OPERATOR)
    );

    private static final Map<String, Integer> BUILTIN_ARITY = Map.<String, Integer>ofEntries(
            // Turtle motion
            entry("forward", 1), entry("fd", 1),
            entry("back", 1), entry("bk", 1),
            entry("left", 1), entry("lt", 1),
            entry("right", 1), entry("rt", 1),
            entry("home", 0),
            entry("setx", 1),
            entry("sety", 1),
            entry("setxy", 2),
            entry("setpos", 1), // setpos [x y]  — takes a list
            entry("setheading", 1), entry("seth", 1),
            entry("arc", 2),
            entry("ellipse", 2),

            // Turtle motion queries
            entry("pos", 0),
            entry("xcor", 0),
            entry("ycor", 0),
            entry("heading", 0),
            entry("towards", 1), // towards [x y]

            // Turtle and window control
            entry("showturtle", 0), entry("st", 0),
            entry("hideturtle", 0), entry("ht", 0),
            entry("clean", 0),
            entry("clearscreen", 0), entry("cs", 0),
            entry("fill", 0),
            entry("filled", 2), // filled color [statements]
            entry("label", 1),
            entry("setlabelheight", 1),
            entry("wrap", 0),
            entry("window", 0),
            entry("fence", 0),

            // Turtle and window queries
            entry("shownp", 0), entry("shown?", 0),
            entry("labelsize", 0),

            // Pen and background control
            entry("penup", 0), entry("pu", 0),
            entry("pendown", 0), entry("pd", 0),
            entry("setcolor", 1), entry("setpencolor", 1),
            entry("setwidth", 1), entry("setpensize", 1),
            entry("changeshape", 1), entry("csh", 1),

            // Pen queries
            entry("pendownp", 0), entry("pendown?", 0),
            entry("pencolor", 0), entry("pc", 0),
            entry("pensize", 0),

            // Procedure definition / reflection
            entry("def", 1),

            // Variable definition
            entry("make", 2),
            entry("name", 2),
            entry("localmake", 2),
            entry("thing", 1),

            // Control structures
            entry("repeat", 2), // repeat N [stmts]
            entry("for", 2), // for [ctrl] [stmts]
            entry("repcount", 0),
            entry("if", 2), // if expr [stmts]
            entry("ifelse", 3), // ifelse expr [t] [f]
            entry("test", 1),
            entry("iftrue", 1),
            entry("iffalse", 1),
            entry("wait", 1),
            entry("bye", 0),
            entry("dotimes", 2), // dotimes [var n] [stmts]
            entry("do.while", 2), // do.while [stmts] expr
            entry("while", 2), // while [expr] [stmts]
            entry("do.until", 2), // do.until [stmts] [expr]
            entry("until", 2), // until [expr] [stmts]

            // Output / print
            entry("print", 1),
            entry("show", 1),
            entry("output", 1),

            // Lists
            entry("list", -1), // variadic
            entry("first", 1),
            entry("butfirst", 1),
            entry("last", 1),
            entry("butlast", 1),
            entry("item", 2),
            entry("pick", 1),

            // Math
            entry("sum", 2),
            entry("minus", 2),
            entry("random", 1),
            entry("modulo", 2),
            entry("power", 2),

            // Receivers
            entry("readword", 0), // optional prompt handled via ()
            entry("readlist", 0),

            // Predicates
            entry("word", 1), entry("word?", 1),
            entry("listp", 1), entry("list?", 1),
            entry("arrayp", 1), entry("array?", 1),
            entry("numberp", 1), entry("number?", 1),
            entry("emptyp", 1), entry("empty?", 1),
            entry("equalp", 2), entry("equal?", 2),
            entry("notequalp", 2), entry("notequal?", 2),
            entry("beforep", 1), entry("before?", 1),
            entry("substringp", 2), entry("substring?", 2)
    );

    private static final Map<String, int[]> INFIX_BP = Map.ofEntries(
            entry("or", new int[]{10, 11}),
            entry("and", new int[]{20, 21}),
            entry("=", new int[]{40, 41}),
            entry("<>", new int[]{40, 41}),
            entry("<", new int[]{40, 41}),
            entry(">", new int[]{40, 41}),
            entry("<=", new int[]{40, 41}),
            entry(">=", new int[]{40, 41}),
            entry("+", new int[]{50, 51}),
            entry("-", new int[]{50, 51}),
            entry("*", new int[]{60, 61}),
            entry("/", new int[]{60, 61}),
            entry("%", new int[]{60, 61})
    );

    private static final Map<String, Integer> PREFIX_BP = Map.of(
            "not", 30,   // lower than comparisons, higher than logical
            "-", 70    // unary minus binds very tight
    );

    public static Token.Type getTokenType(String keyword) {
        return KEYWORDS.get(keyword.toLowerCase());
    }

    public static Integer getArity(String procName) {
        return BUILTIN_ARITY.get(procName.toLowerCase());
    }

    public static int[] getInfixBP(String operator) {
        return INFIX_BP.get(operator.toLowerCase());
    }

    public static Integer getPrefixBP(String operator) {
        return PREFIX_BP.get(operator.toLowerCase());
    }

    public static boolean isBuiltin(String procName) {
        return BUILTIN_ARITY.containsKey(procName.toLowerCase());
    }
}
