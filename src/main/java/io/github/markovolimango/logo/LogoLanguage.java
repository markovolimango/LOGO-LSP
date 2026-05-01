package io.github.markovolimango.logo;

import io.github.markovolimango.logo.lexer.Token;

import java.util.Map;

import static java.util.Map.entry;

public final class LogoLanguage {

    private static final Map<String, Token.Type> KEYWORDS = Map.ofEntries(
            entry("to", Token.Type.TO),
            entry("end", Token.Type.END),
            entry("define", Token.Type.DEFINE),
            entry("make", Token.Type.MAKE),
            entry("localmake", Token.Type.LOCALMAKE),
            entry("name", Token.Type.NAME),
            entry("output", Token.Type.OUTPUT),
            entry("op", Token.Type.OUTPUT),
            entry("and", Token.Type.OPERATOR),
            entry("or", Token.Type.OPERATOR),
            entry("not", Token.Type.OPERATOR)
    );
    private static final Map<String, Arity> BUILTIN_ARITY = Map.<String, Arity>ofEntries(
            // Turtle motion
            entry("forward", Arity.fixed(1)), entry("fd", Arity.fixed(1)),
            entry("back", Arity.fixed(1)), entry("bk", Arity.fixed(1)),
            entry("left", Arity.fixed(1)), entry("lt", Arity.fixed(1)),
            entry("right", Arity.fixed(1)), entry("rt", Arity.fixed(1)),
            entry("home", Arity.fixed(0)),
            entry("setx", Arity.fixed(1)),
            entry("sety", Arity.fixed(1)),
            entry("setxy", Arity.fixed(2)),
            entry("setpos", Arity.fixed(1)),
            entry("setheading", Arity.fixed(1)), entry("seth", Arity.fixed(1)),
            entry("arc", Arity.fixed(2)),
            entry("ellipse", Arity.fixed(2)),

            // Turtle motion queries
            entry("pos", Arity.fixed(0)),
            entry("xcor", Arity.fixed(0)),
            entry("ycor", Arity.fixed(0)),
            entry("heading", Arity.fixed(0)),
            entry("towards", Arity.fixed(1)),
            entry("scrunch", Arity.fixed(0)),
            entry("bounds", Arity.fixed(0)),

            // Turtle and window control
            entry("showturtle", Arity.fixed(0)), entry("st", Arity.fixed(0)),
            entry("hideturtle", Arity.fixed(0)), entry("ht", Arity.fixed(0)),
            entry("clean", Arity.fixed(0)),
            entry("clearscreen", Arity.fixed(0)), entry("cs", Arity.fixed(0)),
            entry("fill", Arity.fixed(0)),
            entry("filled", Arity.fixed(2)),
            entry("label", Arity.fixed(1)),
            entry("setlabelheight", Arity.fixed(1)),
            entry("setlabelfont", Arity.fixed(1)),
            entry("wrap", Arity.fixed(0)),
            entry("window", Arity.fixed(0)),
            entry("fence", Arity.fixed(0)),
            entry("setscrunch", Arity.fixed(2)),
            entry("setturtle", Arity.fixed(1)),
            entry("ask", Arity.fixed(2)),
            entry("clearturtles", Arity.fixed(0)),

            // Turtle and window queries
            entry("shownp", Arity.fixed(0)), entry("shown?", Arity.fixed(0)),
            entry("turtlemode", Arity.fixed(0)),
            entry("labelsize", Arity.fixed(0)),
            entry("labelfont", Arity.fixed(0)),
            entry("turtle", Arity.fixed(0)),
            entry("turtles", Arity.fixed(0)),

            // Pen and background control
            entry("pendown", Arity.fixed(0)), entry("pd", Arity.fixed(0)),
            entry("penup", Arity.fixed(0)), entry("pu", Arity.fixed(0)),
            entry("penpaint", Arity.fixed(0)), entry("ppt", Arity.fixed(0)),
            entry("penerase", Arity.fixed(0)), entry("pe", Arity.fixed(0)),
            entry("penreverse", Arity.fixed(0)), entry("px", Arity.fixed(0)),
            entry("setpencolor", Arity.fixed(1)), entry("setcolor", Arity.fixed(1)),
            entry("setpensize", Arity.fixed(1)), entry("setwidth", Arity.fixed(1)),
            entry("setpalette", Arity.fixed(2)),
            entry("setbackground", Arity.fixed(1)), entry("setscreencolor", Arity.fixed(1)), entry("setsc", Arity.fixed(1)),
            entry("changeshape", Arity.fixed(1)), entry("csh", Arity.fixed(1)),

            // Pen queries
            entry("pendownp", Arity.fixed(0)), entry("pendown?", Arity.fixed(0)),
            entry("penmode", Arity.fixed(0)),
            entry("pencolor", Arity.fixed(0)), entry("pc", Arity.fixed(0)),
            entry("palette", Arity.fixed(1)),
            entry("pensize", Arity.fixed(0)),
            entry("background", Arity.fixed(0)), entry("bg", Arity.fixed(0)),
            entry("getscreencolor", Arity.fixed(0)), entry("getsc", Arity.fixed(0)),

            // Bitmap operations
            entry("bitcut", Arity.fixed(2)),
            entry("bitpaste", Arity.fixed(0)),

            // Mouse/touch queries
            entry("mousepos", Arity.fixed(0)),
            entry("clickpos", Arity.fixed(0)),
            entry("buttonp", Arity.fixed(0)), entry("button?", Arity.fixed(0)),
            entry("button", Arity.fixed(0)),
            entry("touches", Arity.fixed(0)),

            // Procedure definition / reflection
            entry("min", Arity.fixed(1)),
            entry("text", Arity.fixed(1)),
            entry("copydef", Arity.fixed(2)),
            entry("arity", Arity.fixed(1)),

            // Variable definition
            entry("make", Arity.fixed(2)),
            entry("name", Arity.fixed(2)),
            entry("localmake", Arity.fixed(2)),
            entry("local", Arity.variadic(1)),
            entry("thing", Arity.fixed(1)),
            entry("global", Arity.fixed(1)),

            // Property lists
            entry("pprop", Arity.fixed(3)),
            entry("gprop", Arity.fixed(2)),
            entry("remprop", Arity.fixed(2)),
            entry("plist", Arity.fixed(1)),

            // Workspace predicates
            entry("procedurep", Arity.fixed(1)), entry("procedure?", Arity.fixed(1)),
            entry("primitivep", Arity.fixed(1)), entry("primitive?", Arity.fixed(1)),
            entry("definedp", Arity.fixed(1)), entry("defined?", Arity.fixed(1)),
            entry("namep", Arity.fixed(1)), entry("name?", Arity.fixed(1)),
            entry("plistp", Arity.fixed(1)), entry("plist?", Arity.fixed(1)),

            // Workspace queries
            entry("contents", Arity.fixed(0)),
            entry("buried", Arity.fixed(0)),
            entry("procedures", Arity.fixed(0)),
            entry("primitives", Arity.fixed(0)),
            entry("globals", Arity.fixed(0)),
            entry("names", Arity.fixed(0)),
            entry("plists", Arity.fixed(0)),
            entry("namelist", Arity.fixed(1)),
            entry("pllist", Arity.fixed(1)),

            // Workspace control
            entry("erase", Arity.fixed(1)),
            entry("erall", Arity.fixed(0)),
            entry("erps", Arity.fixed(0)),
            entry("erns", Arity.fixed(0)),
            entry("erpls", Arity.fixed(0)),
            entry("ern", Arity.fixed(1)),
            entry("epl", Arity.fixed(1)),
            entry("bury", Arity.fixed(1)),
            entry("buryall", Arity.fixed(0)),
            entry("buryname", Arity.fixed(1)),
            entry("unbury", Arity.fixed(1)),
            entry("unburyall", Arity.fixed(0)),
            entry("unburyname", Arity.fixed(1)),
            entry("buriedp", Arity.fixed(1)), entry("buried?", Arity.fixed(1)),

            // Control structures
            entry("run", Arity.fixed(1)),
            entry("runresult", Arity.fixed(1)),
            entry("repeat", Arity.fixed(2)),
            entry("forever", Arity.fixed(1)),
            entry("repcount", Arity.fixed(0)), entry("#", Arity.fixed(0)),
            entry("if", Arity.fixed(2)),
            entry("ifelse", Arity.fixed(3)),
            entry("test", Arity.fixed(1)),
            entry("iftrue", Arity.fixed(1)), entry("ift", Arity.fixed(1)),
            entry("iffalse", Arity.fixed(1)), entry("iff", Arity.fixed(1)),
            entry("stop", Arity.fixed(0)),
            entry("output", Arity.fixed(1)), entry("op", Arity.fixed(1)),
            entry("catch", Arity.fixed(2)),
            entry("throw", Arity.capped(1, 2)),
            entry("error", Arity.fixed(0)),
            entry("wait", Arity.fixed(1)),
            entry("bye", Arity.fixed(0)),
            entry(".maybeoutput", Arity.fixed(1)),
            entry("ignore", Arity.fixed(1)),
            entry("for", Arity.fixed(2)),
            entry("dotimes", Arity.fixed(2)),
            entry("do.while", Arity.fixed(2)),
            entry("while", Arity.fixed(2)),
            entry("do.until", Arity.fixed(2)),
            entry("until", Arity.fixed(2)),
            entry("case", Arity.fixed(2)),
            entry("cond", Arity.fixed(1)),

            // Template-based iteration
            entry("apply", Arity.fixed(2)),
            entry("invoke", Arity.variadic(2)),
            entry("foreach", Arity.fixed(2)),
            entry("map", Arity.fixed(2)),
            entry("filter", Arity.fixed(2)),
            entry("find", Arity.fixed(2)),
            entry("reduce", Arity.capped(2, 3)),
            entry("crossmap", Arity.variadic(2)),

            // Communication
            entry("print", Arity.variadic(1)), entry("pr", Arity.variadic(1)),
            entry("type", Arity.variadic(1)),
            entry("show", Arity.variadic(1)),
            entry("readlist", Arity.capped(0, 1)),
            entry("readword", Arity.capped(0, 1)),
            entry("cleartext", Arity.fixed(0)), entry("ct", Arity.fixed(0)),
            entry("settextcolor", Arity.fixed(1)),
            entry("textcolor", Arity.fixed(0)),
            entry("increasefont", Arity.fixed(0)),
            entry("decreasefont", Arity.fixed(0)),
            entry("settextsize", Arity.fixed(1)),
            entry("textsize", Arity.fixed(0)),
            entry("setfont", Arity.fixed(1)),
            entry("font", Arity.fixed(0)),

            // Data structure constructors
            entry("word", Arity.variadic(2)),
            entry("list", Arity.variadic(2)),
            entry("sentence", Arity.variadic(2)), entry("se", Arity.variadic(2)),
            entry("fput", Arity.fixed(2)),
            entry("lput", Arity.fixed(2)),
            entry("array", Arity.capped(1, 2)),
            entry("mdarray", Arity.capped(1, 2)),
            entry("listtoarray", Arity.capped(1, 2)),
            entry("arraytolist", Arity.fixed(1)),
            entry("combine", Arity.fixed(2)),
            entry("reverse", Arity.fixed(1)),
            entry("gensym", Arity.fixed(0)),

            // Data selectors
            entry("first", Arity.fixed(1)),
            entry("last", Arity.fixed(1)),
            entry("firsts", Arity.fixed(1)),
            entry("butfirst", Arity.fixed(1)), entry("bf", Arity.fixed(1)),
            entry("butlast", Arity.fixed(1)), entry("bl", Arity.fixed(1)),
            entry("butfirsts", Arity.fixed(1)), entry("bfs", Arity.fixed(1)),
            entry("item", Arity.fixed(2)),
            entry("mditem", Arity.fixed(2)),
            entry("pick", Arity.fixed(1)),
            entry("remove", Arity.fixed(2)),
            entry("remdup", Arity.fixed(1)),
            entry("quoted", Arity.fixed(1)),
            entry("split", Arity.fixed(2)),

            // Data mutators
            entry("setitem", Arity.fixed(3)),
            entry("mdsetitem", Arity.fixed(3)),
            entry(".setfirst", Arity.fixed(2)),
            entry(".setbf", Arity.fixed(2)),
            entry(".setitem", Arity.fixed(3)),
            entry("push", Arity.fixed(2)),
            entry("pop", Arity.fixed(1)),
            entry("queue", Arity.fixed(2)),
            entry("dequeue", Arity.fixed(1)),

            // Predicates
            entry("wordp", Arity.fixed(1)), entry("word?", Arity.fixed(1)),
            entry("listp", Arity.fixed(1)), entry("list?", Arity.fixed(1)),
            entry("arrayp", Arity.fixed(1)), entry("array?", Arity.fixed(1)),
            entry("numberp", Arity.fixed(1)), entry("number?", Arity.fixed(1)),
            entry("emptyp", Arity.fixed(1)), entry("empty?", Arity.fixed(1)),
            entry("equalp", Arity.fixed(2)), entry("equal?", Arity.fixed(2)),
            entry("notequalp", Arity.fixed(2)), entry("notequal?", Arity.fixed(2)),
            entry("beforep", Arity.fixed(2)), entry("before?", Arity.fixed(2)),
            entry("substringp", Arity.fixed(2)), entry("substring?", Arity.fixed(2)),
            entry("memberp", Arity.fixed(2)), entry("member?", Arity.fixed(2)),
            entry(".eq", Arity.fixed(2)),

            // Queries
            entry("count", Arity.fixed(1)),
            entry("ascii", Arity.fixed(1)),
            entry("char", Arity.fixed(1)),
            entry("member", Arity.fixed(2)),
            entry("uppercase", Arity.fixed(1)),
            entry("lowercase", Arity.fixed(1)),
            entry("standout", Arity.fixed(1)),
            entry("parse", Arity.fixed(1)),
            entry("runparse", Arity.fixed(1)),

            // Arithmetic
            entry("sum", Arity.variadic(2)),
            entry("difference", Arity.fixed(2)),
            entry("product", Arity.variadic(2)),
            entry("quotient", Arity.capped(1, 2)),
            entry("remainder", Arity.fixed(2)),
            entry("modulo", Arity.fixed(2)),
            entry("minus", Arity.fixed(1)),
            entry("abs", Arity.fixed(1)),
            entry("int", Arity.fixed(1)),
            entry("round", Arity.fixed(1)),
            entry("sqrt", Arity.fixed(1)),
            entry("exp", Arity.fixed(1)),
            entry("log10", Arity.fixed(1)),
            entry("ln", Arity.fixed(1)),
            entry("arctan", Arity.capped(1, 2)),
            entry("sin", Arity.fixed(1)),
            entry("cos", Arity.fixed(1)),
            entry("tan", Arity.fixed(1)),
            entry("radarctan", Arity.capped(1, 2)),
            entry("radsin", Arity.fixed(1)),
            entry("radcos", Arity.fixed(1)),
            entry("radtan", Arity.fixed(1)),
            entry("iseq", Arity.fixed(2)),
            entry("rseq", Arity.fixed(3)),
            entry("random", Arity.capped(1, 2)),
            entry("rerandom", Arity.capped(0, 1)),
            entry("form", Arity.fixed(3)),
            entry("power", Arity.fixed(2)),

            // Bitwise
            entry("bitand", Arity.variadic(2)),
            entry("bitor", Arity.variadic(2)),
            entry("bitxor", Arity.variadic(2)),
            entry("bitnot", Arity.fixed(1)),
            entry("ashift", Arity.fixed(2)),
            entry("lshift", Arity.fixed(2)),

            // Logical
            entry("true", Arity.fixed(0)),
            entry("false", Arity.fixed(0)),
            entry("and", Arity.variadic(2)),
            entry("or", Arity.variadic(2)),
            entry("xor", Arity.fixed(2)),
            entry("not", Arity.fixed(1)),

            // Numeric predicates
            entry("lessp", Arity.fixed(2)), entry("less?", Arity.fixed(2)),
            entry("greaterp", Arity.fixed(2)), entry("greater?", Arity.fixed(2)),
            entry("lessequalp", Arity.fixed(2)), entry("lessequal?", Arity.fixed(2)),
            entry("greaterequalp", Arity.fixed(2)), entry("greaterequal?", Arity.fixed(2))
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
            "not", 30,
            "-", 70
    );

    public static Token.Type getTokenType(String keyword) {
        return KEYWORDS.get(keyword.toLowerCase());
    }

    public static Arity getArity(String procName) {
        return BUILTIN_ARITY.get(procName.toLowerCase());
    }

    public static int[] getInfixBP(String operator) {
        return INFIX_BP.get(operator.toLowerCase());
    }

    public static Integer getPrefixBP(String operator) {
        return PREFIX_BP.get(operator.toLowerCase());
    }

    public static boolean isBuiltin(String procName) {
        String lower = procName.toLowerCase();
        return BUILTIN_ARITY.containsKey(lower) ||
                (KEYWORDS.containsKey(lower) && KEYWORDS.get(lower) != Token.Type.OPERATOR);
    }

    public record Arity(int min, int max) {
        public static Arity fixed(int n) {
            return new Arity(n, n);
        }

        public static Arity variadic(int min) {
            return new Arity(min, -1);
        }

        public static Arity capped(int min, int max) {
            return new Arity(min, max);
        }
    }
}