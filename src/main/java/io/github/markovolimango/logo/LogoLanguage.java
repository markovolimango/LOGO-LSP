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

    private static final Map<String, ProcInfo> BUILTIN_PROCS = Map.<String, ProcInfo>ofEntries(
            // Turtle motion (void)
            entry("forward", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("fd", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("back", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("bk", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("left", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("lt", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("right", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("rt", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("home", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("setx", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("sety", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setxy", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("setpos", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setheading", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("seth", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("arc", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("ellipse", ProcInfo.of(Arity.fixed(2), Returns.VOID)),

            // Turtle motion queries (value)
            entry("pos", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("xcor", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("ycor", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("heading", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("towards", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("scrunch", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("bounds", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),

            // Turtle and window control (void)
            entry("showturtle", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("st", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("hideturtle", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("ht", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("clean", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("clearscreen", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("cs", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("fill", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("filled", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("label", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setlabelheight", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setlabelfont", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("wrap", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("window", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("fence", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("setscrunch", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("setturtle", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("ask", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("clearturtles", ProcInfo.of(Arity.fixed(0), Returns.VOID)),

            // Turtle and window queries (value)
            entry("shownp", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("shown?", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("turtlemode", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("labelsize", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("labelfont", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("turtle", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("turtles", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),

            // Pen and background control (void)
            entry("pendown", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("pd", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("penup", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("pu", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("penpaint", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("ppt", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("penerase", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("pe", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("penreverse", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("px", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("setpencolor", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setcolor", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setpensize", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setwidth", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setpalette", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("setbackground", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setscreencolor", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("setsc", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("changeshape", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("csh", ProcInfo.of(Arity.fixed(1), Returns.VOID)),

            // Pen queries (value)
            entry("pendownp", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("pendown?", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("penmode", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("pencolor", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("pc", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("palette", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("pensize", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("background", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("bg", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("getscreencolor", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("getsc", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),

            // Bitmap operations (void)
            entry("bitcut", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("bitpaste", ProcInfo.of(Arity.fixed(0), Returns.VOID)),

            // Mouse/touch queries (value)
            entry("mousepos", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("clickpos", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("buttonp", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("button?", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("button", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("touches", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),

            // Procedure definition / reflection (value)
            entry("min", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("text", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("copydef", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("arity", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Variable definition (void/value)
            entry("make", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("name", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("localmake", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("local", ProcInfo.of(Arity.variadic(1), Returns.VOID)),
            entry("thing", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("global", ProcInfo.of(Arity.fixed(1), Returns.VOID)),

            // Property lists
            entry("pprop", ProcInfo.of(Arity.fixed(3), Returns.VOID)),
            entry("gprop", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("remprop", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("plist", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Workspace predicates (value)
            entry("procedurep", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("procedure?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("primitivep", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("primitive?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("definedp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("defined?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("namep", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("name?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("plistp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("plist?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Workspace queries (value)
            entry("contents", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("buried", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("procedures", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("primitives", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("globals", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("names", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("plists", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("namelist", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("pllist", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Workspace control (void)
            entry("erase", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("erall", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("erps", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("erns", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("erpls", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("ern", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("epl", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("bury", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("buryall", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("buryname", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("unbury", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("unburyall", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("unburyname", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("buriedp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("buried?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Control structures
            entry("run", ProcInfo.of(Arity.fixed(1), Returns.EITHER)),
            entry("runresult", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("repeat", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("forever", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("repcount", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("#", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("if", ProcInfo.of(Arity.fixed(2), Returns.EITHER)),
            entry("ifelse", ProcInfo.of(Arity.fixed(3), Returns.EITHER)),
            entry("test", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("iftrue", ProcInfo.of(Arity.fixed(1), Returns.EITHER)),
            entry("ift", ProcInfo.of(Arity.fixed(1), Returns.EITHER)),
            entry("iffalse", ProcInfo.of(Arity.fixed(1), Returns.EITHER)),
            entry("iff", ProcInfo.of(Arity.fixed(1), Returns.EITHER)),
            entry("stop", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("output", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("op", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("catch", ProcInfo.of(Arity.fixed(2), Returns.EITHER)),
            entry("throw", ProcInfo.of(Arity.capped(1, 2), Returns.VOID)),
            entry("error", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("wait", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("bye", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry(".maybeoutput", ProcInfo.of(Arity.fixed(1), Returns.EITHER)),
            entry("ignore", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("for", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("dotimes", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("do.while", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("while", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("do.until", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("until", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("case", ProcInfo.of(Arity.fixed(2), Returns.EITHER)),
            entry("cond", ProcInfo.of(Arity.fixed(1), Returns.EITHER)),

            // Template-based iteration
            entry("apply", ProcInfo.of(Arity.fixed(2), Returns.EITHER)),
            entry("invoke", ProcInfo.of(Arity.variadic(2), Returns.EITHER)),
            entry("foreach", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("map", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("filter", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("find", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("reduce", ProcInfo.of(Arity.capped(2, 3), Returns.VALUE)),
            entry("crossmap", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),

            // Communication
            entry("print", ProcInfo.of(Arity.variadic(1), Returns.VOID)),
            entry("pr", ProcInfo.of(Arity.variadic(1), Returns.VOID)),
            entry("type", ProcInfo.of(Arity.variadic(1), Returns.VOID)),
            entry("show", ProcInfo.of(Arity.variadic(1), Returns.VOID)),
            entry("readlist", ProcInfo.of(Arity.capped(0, 1), Returns.VALUE)),
            entry("readword", ProcInfo.of(Arity.capped(0, 1), Returns.VALUE)),
            entry("cleartext", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("ct", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("settextcolor", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("textcolor", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("increasefont", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("decreasefont", ProcInfo.of(Arity.fixed(0), Returns.VOID)),
            entry("settextsize", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("textsize", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("setfont", ProcInfo.of(Arity.fixed(1), Returns.VOID)),
            entry("font", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),

            // Data structure constructors (value)
            entry("word", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("list", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("sentence", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("se", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("fput", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("lput", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("array", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE)),
            entry("mdarray", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE)),
            entry("listtoarray", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE)),
            entry("arraytolist", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("combine", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("reverse", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("gensym", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),

            // Data selectors (value)
            entry("first", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("last", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("firsts", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("butfirst", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("bf", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("butlast", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("bl", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("butfirsts", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("bfs", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("item", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("mditem", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("pick", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("remove", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("remdup", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("quoted", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("split", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),

            // Data mutators (void)
            entry("setitem", ProcInfo.of(Arity.fixed(3), Returns.VOID)),
            entry("mdsetitem", ProcInfo.of(Arity.fixed(3), Returns.VOID)),
            entry(".setfirst", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry(".setbf", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry(".setitem", ProcInfo.of(Arity.fixed(3), Returns.VOID)),
            entry("push", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("pop", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("queue", ProcInfo.of(Arity.fixed(2), Returns.VOID)),
            entry("dequeue", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Predicates (value)
            entry("wordp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("word?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("listp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("list?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("arrayp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("array?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("numberp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("number?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("emptyp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("empty?", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("equalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("equal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("notequalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("notequal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("beforep", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("before?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("substringp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("substring?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("memberp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("member?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry(".eq", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),

            // Queries (value)
            entry("count", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("ascii", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("char", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("member", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("uppercase", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("lowercase", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("standout", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("parse", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("runparse", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Arithmetic (value)
            entry("sum", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("difference", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("product", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("quotient", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE)),
            entry("remainder", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("modulo", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("minus", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("abs", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("int", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("round", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("sqrt", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("exp", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("log10", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("ln", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("arctan", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE)),
            entry("sin", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("cos", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("tan", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("radarctan", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE)),
            entry("radsin", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("radcos", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("radtan", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("iseq", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("rseq", ProcInfo.of(Arity.fixed(3), Returns.VALUE)),
            entry("random", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE)),
            entry("rerandom", ProcInfo.of(Arity.capped(0, 1), Returns.VOID)),
            entry("form", ProcInfo.of(Arity.fixed(3), Returns.VALUE)),
            entry("power", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),

            // Bitwise (value)
            entry("bitand", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("bitor", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("bitxor", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("bitnot", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),
            entry("ashift", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("lshift", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),

            // Logical (value)
            entry("true", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("false", ProcInfo.of(Arity.fixed(0), Returns.VALUE)),
            entry("and", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("or", ProcInfo.of(Arity.variadic(2), Returns.VALUE)),
            entry("xor", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("not", ProcInfo.of(Arity.fixed(1), Returns.VALUE)),

            // Numeric predicates (value)
            entry("lessp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("less?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("greaterp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("greater?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("lessequalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("lessequal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("greaterequalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE)),
            entry("greaterequal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE))
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
        ProcInfo info = BUILTIN_PROCS.get(procName.toLowerCase());
        return info != null ? info.arity() : null;
    }

    public static int[] getInfixBP(String operator) {
        return INFIX_BP.get(operator.toLowerCase());
    }

    public static Integer getPrefixBP(String operator) {
        return PREFIX_BP.get(operator.toLowerCase());
    }

    public static boolean isBuiltin(String procName) {
        String lower = procName.toLowerCase();
        return BUILTIN_PROCS.containsKey(lower) ||
                (KEYWORDS.containsKey(lower) && KEYWORDS.get(lower) != Token.Type.OPERATOR);
    }

    public enum Returns {
        /**
         * Always outputs a value — safe to use as an argument.
         */
        VALUE,
        /**
         * Never outputs a value — side-effects only.
         */
        VOID,
        /**
         * May or may not output a value depending on the body/arguments.
         */
        EITHER
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

    public record ProcInfo(Arity arity, Returns returns) {
        public static ProcInfo of(Arity arity, Returns returns) {
            return new ProcInfo(arity, returns);
        }
    }
}