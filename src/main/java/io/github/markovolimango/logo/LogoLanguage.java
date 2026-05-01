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
            entry("forward", ProcInfo.of(Arity.fixed(1), Returns.VOID, "dist")),
            entry("fd", ProcInfo.of(Arity.fixed(1), Returns.VOID, "dist")),
            entry("back", ProcInfo.of(Arity.fixed(1), Returns.VOID, "dist")),
            entry("bk", ProcInfo.of(Arity.fixed(1), Returns.VOID, "dist")),
            entry("left", ProcInfo.of(Arity.fixed(1), Returns.VOID, "angle")),
            entry("lt", ProcInfo.of(Arity.fixed(1), Returns.VOID, "angle")),
            entry("right", ProcInfo.of(Arity.fixed(1), Returns.VOID, "angle")),
            entry("rt", ProcInfo.of(Arity.fixed(1), Returns.VOID, "angle")),
            entry("home", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("setx", ProcInfo.of(Arity.fixed(1), Returns.VOID, "x")),
            entry("sety", ProcInfo.of(Arity.fixed(1), Returns.VOID, "y")),
            entry("setxy", ProcInfo.of(Arity.fixed(2), Returns.VOID, "x y")),
            entry("setpos", ProcInfo.of(Arity.fixed(1), Returns.VOID, "pos")),
            entry("setheading", ProcInfo.of(Arity.fixed(1), Returns.VOID, "angle")),
            entry("seth", ProcInfo.of(Arity.fixed(1), Returns.VOID, "angle")),
            entry("arc", ProcInfo.of(Arity.fixed(2), Returns.VOID, "angle radius")),
            entry("ellipse", ProcInfo.of(Arity.fixed(2), Returns.VOID, "width height")),

            // Turtle motion queries (value)
            entry("pos", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("xcor", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("ycor", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("heading", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("towards", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "pos")),
            entry("scrunch", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("bounds", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),

            // Turtle and window control (void)
            entry("showturtle", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("st", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("hideturtle", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("ht", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("clean", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("clearscreen", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("cs", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("fill", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("filled", ProcInfo.of(Arity.fixed(2), Returns.VOID, "fillcolor statements")),
            entry("label", ProcInfo.of(Arity.fixed(1), Returns.VOID, "expr")),
            entry("setlabelheight", ProcInfo.of(Arity.fixed(1), Returns.VOID, "height")),
            entry("setlabelfont", ProcInfo.of(Arity.fixed(1), Returns.VOID, "font")),
            entry("wrap", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("window", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("fence", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("setscrunch", ProcInfo.of(Arity.fixed(2), Returns.VOID, "xscale yscale")),
            entry("setturtle", ProcInfo.of(Arity.fixed(1), Returns.VOID, "id")),
            entry("ask", ProcInfo.of(Arity.fixed(2), Returns.VOID, "turtle statements")),
            entry("clearturtles", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),

            // Turtle and window queries (value)
            entry("shownp", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("shown?", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("turtlemode", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("labelsize", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("labelfont", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("turtle", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("turtles", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),

            // Pen and background control (void)
            entry("pendown", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("pd", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("penup", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("pu", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("penpaint", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("ppt", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("penerase", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("pe", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("penreverse", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("px", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("setpencolor", ProcInfo.of(Arity.fixed(1), Returns.VOID, "color")),
            entry("setcolor", ProcInfo.of(Arity.fixed(1), Returns.VOID, "color")),
            entry("setpensize", ProcInfo.of(Arity.fixed(1), Returns.VOID, "width")),
            entry("setwidth", ProcInfo.of(Arity.fixed(1), Returns.VOID, "width")),
            entry("setpalette", ProcInfo.of(Arity.fixed(2), Returns.VOID, "index color")),
            entry("setbackground", ProcInfo.of(Arity.fixed(1), Returns.VOID, "color")),
            entry("setscreencolor", ProcInfo.of(Arity.fixed(1), Returns.VOID, "color")),
            entry("setsc", ProcInfo.of(Arity.fixed(1), Returns.VOID, "color")),
            entry("changeshape", ProcInfo.of(Arity.fixed(1), Returns.VOID, "shape")),
            entry("csh", ProcInfo.of(Arity.fixed(1), Returns.VOID, "shape")),

            // Pen queries (value)
            entry("pendownp", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("pendown?", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("penmode", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("pencolor", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("pc", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("palette", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "index")),
            entry("pensize", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("background", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("bg", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("getscreencolor", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("getsc", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),

            // Bitmap operations (void)
            entry("bitcut", ProcInfo.of(Arity.fixed(2), Returns.VOID, "width height")),
            entry("bitpaste", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),

            // Mouse/touch queries (value)
            entry("mousepos", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("clickpos", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("buttonp", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("button?", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("button", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("touches", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),

            // Procedure definition / reflection (value)
            entry("min", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "procname")),
            entry("text", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "procname")),
            entry("copydef", ProcInfo.of(Arity.fixed(2), Returns.VOID, "newname oldname")),
            entry("arity", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "procname")),

            // Variable definition (void/value)
            entry("make", ProcInfo.of(Arity.fixed(2), Returns.VOID, "varname expr")),
            entry("name", ProcInfo.of(Arity.fixed(2), Returns.VOID, "expr varname")),
            entry("localmake", ProcInfo.of(Arity.fixed(2), Returns.VOID, "varname expr")),
            entry("local", ProcInfo.of(Arity.variadic(1), Returns.VOID, "varname ...")),
            entry("thing", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "varname")),
            entry("global", ProcInfo.of(Arity.fixed(1), Returns.VOID, "varname")),

            // Property lists
            entry("pprop", ProcInfo.of(Arity.fixed(3), Returns.VOID, "plistname propname value")),
            entry("gprop", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "plistname propname")),
            entry("remprop", ProcInfo.of(Arity.fixed(2), Returns.VOID, "plistname propname")),
            entry("plist", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "plistname")),

            // Workspace predicates (value)
            entry("procedurep", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("procedure?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("primitivep", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("primitive?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("definedp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("defined?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("namep", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("name?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("plistp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("plist?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),

            // Workspace queries (value)
            entry("contents", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("buried", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("procedures", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("primitives", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("globals", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("names", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("plists", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("namelist", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "varnames")),
            entry("pllist", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "plnames")),

            // Workspace control (void)
            entry("erase", ProcInfo.of(Arity.fixed(1), Returns.VOID, "contentslist")),
            entry("erall", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("erps", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("erns", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("erpls", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("ern", ProcInfo.of(Arity.fixed(1), Returns.VOID, "varname")),
            entry("epl", ProcInfo.of(Arity.fixed(1), Returns.VOID, "plistname")),
            entry("bury", ProcInfo.of(Arity.fixed(1), Returns.VOID, "contentslist")),
            entry("buryall", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("buryname", ProcInfo.of(Arity.fixed(1), Returns.VOID, "varname")),
            entry("unbury", ProcInfo.of(Arity.fixed(1), Returns.VOID, "contentslist")),
            entry("unburyall", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("unburyname", ProcInfo.of(Arity.fixed(1), Returns.VOID, "varname")),
            entry("buriedp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),
            entry("buried?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "name")),

            // Control structures
            entry("run", ProcInfo.of(Arity.fixed(1), Returns.EITHER, "statements")),
            entry("runresult", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "statements")),
            entry("repeat", ProcInfo.of(Arity.fixed(2), Returns.VOID, "count statements")),
            entry("forever", ProcInfo.of(Arity.fixed(1), Returns.VOID, "statements")),
            entry("repcount", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("#", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("if", ProcInfo.of(Arity.fixed(2), Returns.EITHER, "expr statements")),
            entry("ifelse", ProcInfo.of(Arity.fixed(3), Returns.EITHER, "expr trueStatements falseStatements")),
            entry("test", ProcInfo.of(Arity.fixed(1), Returns.VOID, "expr")),
            entry("iftrue", ProcInfo.of(Arity.fixed(1), Returns.EITHER, "statements")),
            entry("ift", ProcInfo.of(Arity.fixed(1), Returns.EITHER, "statements")),
            entry("iffalse", ProcInfo.of(Arity.fixed(1), Returns.EITHER, "statements")),
            entry("iff", ProcInfo.of(Arity.fixed(1), Returns.EITHER, "statements")),
            entry("stop", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("output", ProcInfo.of(Arity.fixed(1), Returns.VOID, "expr")),
            entry("op", ProcInfo.of(Arity.fixed(1), Returns.VOID, "expr")),
            entry("catch", ProcInfo.of(Arity.fixed(2), Returns.EITHER, "tag statements")),
            entry("throw", ProcInfo.of(Arity.capped(1, 2), Returns.VOID, "tag [value]")),
            entry("error", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("wait", ProcInfo.of(Arity.fixed(1), Returns.VOID, "time")),
            entry("bye", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry(".maybeoutput", ProcInfo.of(Arity.fixed(1), Returns.EITHER, "expr")),
            entry("ignore", ProcInfo.of(Arity.fixed(1), Returns.VOID, "expr")),
            entry("for", ProcInfo.of(Arity.fixed(2), Returns.VOID, "controllist statements")),
            entry("dotimes", ProcInfo.of(Arity.fixed(2), Returns.VOID, "controllist statements")),
            entry("do.while", ProcInfo.of(Arity.fixed(2), Returns.VOID, "statements expr")),
            entry("while", ProcInfo.of(Arity.fixed(2), Returns.VOID, "expr statements")),
            entry("do.until", ProcInfo.of(Arity.fixed(2), Returns.VOID, "statements expr")),
            entry("until", ProcInfo.of(Arity.fixed(2), Returns.VOID, "expr statements")),
            entry("case", ProcInfo.of(Arity.fixed(2), Returns.EITHER, "expr clauses")),
            entry("cond", ProcInfo.of(Arity.fixed(1), Returns.EITHER, "clauses")),

            // Template-based iteration
            entry("apply", ProcInfo.of(Arity.fixed(2), Returns.EITHER, "template inputs")),
            entry("invoke", ProcInfo.of(Arity.variadic(2), Returns.EITHER, "template input ...")),
            entry("foreach", ProcInfo.of(Arity.fixed(2), Returns.VOID, "template data")),
            entry("map", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "template data")),
            entry("filter", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "template data")),
            entry("find", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "template data")),
            entry("reduce", ProcInfo.of(Arity.capped(2, 3), Returns.VALUE, "template data [initial]")),
            entry("crossmap", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "template data ...")),

            // Communication
            entry("print", ProcInfo.of(Arity.variadic(1), Returns.VOID, "thing ...")),
            entry("pr", ProcInfo.of(Arity.variadic(1), Returns.VOID, "thing ...")),
            entry("type", ProcInfo.of(Arity.variadic(1), Returns.VOID, "thing ...")),
            entry("show", ProcInfo.of(Arity.variadic(1), Returns.VOID, "thing ...")),
            entry("readlist", ProcInfo.of(Arity.capped(0, 1), Returns.VALUE, "[prompt]")),
            entry("readword", ProcInfo.of(Arity.capped(0, 1), Returns.VALUE, "[prompt]")),
            entry("cleartext", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("ct", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("settextcolor", ProcInfo.of(Arity.fixed(1), Returns.VOID, "color")),
            entry("textcolor", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("increasefont", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("decreasefont", ProcInfo.of(Arity.fixed(0), Returns.VOID, "")),
            entry("settextsize", ProcInfo.of(Arity.fixed(1), Returns.VOID, "size")),
            entry("textsize", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("setfont", ProcInfo.of(Arity.fixed(1), Returns.VOID, "font")),
            entry("font", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),

            // Data structure constructors (value)
            entry("word", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "word1 word2 ...")),
            entry("list", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "thing1 thing2 ...")),
            entry("sentence", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "thing1 thing2 ...")),
            entry("se", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "thing1 thing2 ...")),
            entry("fput", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing list")),
            entry("lput", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing list")),
            entry("array", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE, "size [origin]")),
            entry("mdarray", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE, "sizelist [origin]")),
            entry("listtoarray", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE, "list [origin]")),
            entry("arraytolist", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "array")),
            entry("combine", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing1 thing2")),
            entry("reverse", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("gensym", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),

            // Data selectors (value)
            entry("first", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("last", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("firsts", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("butfirst", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("bf", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("butlast", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("bl", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("butfirsts", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("bfs", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("item", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "index list")),
            entry("mditem", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "indexlist array")),
            entry("pick", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("remove", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing list")),
            entry("remdup", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "list")),
            entry("quoted", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("split", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing list")),

            // Data mutators (void)
            entry("setitem", ProcInfo.of(Arity.fixed(3), Returns.VOID, "index array value")),
            entry("mdsetitem", ProcInfo.of(Arity.fixed(3), Returns.VOID, "indexlist array value")),
            entry(".setfirst", ProcInfo.of(Arity.fixed(2), Returns.VOID, "list value")),
            entry(".setbf", ProcInfo.of(Arity.fixed(2), Returns.VOID, "list tail")),
            entry(".setitem", ProcInfo.of(Arity.fixed(3), Returns.VOID, "index array value")),
            entry("push", ProcInfo.of(Arity.fixed(2), Returns.VOID, "stackname thing")),
            entry("pop", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "stackname")),
            entry("queue", ProcInfo.of(Arity.fixed(2), Returns.VOID, "queuename thing")),
            entry("dequeue", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "queuename")),

            // Predicates (value)
            entry("wordp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("word?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("listp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("list?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("arrayp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("array?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("numberp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("number?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("emptyp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("empty?", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("equalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "expr1 expr2")),
            entry("equal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "expr1 expr2")),
            entry("notequalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "expr1 expr2")),
            entry("notequal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "expr1 expr2")),
            entry("beforep", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "word1 word2")),
            entry("before?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "word1 word2")),
            entry("substringp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing1 thing2")),
            entry("substring?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing1 thing2")),
            entry("memberp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing list")),
            entry("member?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing list")),
            entry(".eq", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "expr1 expr2")),

            // Queries (value)
            entry("count", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "thing")),
            entry("ascii", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "char")),
            entry("char", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "code")),
            entry("member", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "thing list")),
            entry("uppercase", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "word")),
            entry("lowercase", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "word")),
            entry("standout", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "word")),
            entry("parse", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "word")),
            entry("runparse", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "word")),

            // Arithmetic (value)
            entry("sum", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "num1 num2 ...")),
            entry("difference", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("product", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "num1 num2 ...")),
            entry("quotient", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE, "[num1] num2")),
            entry("remainder", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("modulo", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("minus", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("abs", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("int", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("round", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("sqrt", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("exp", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("log10", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("ln", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("arctan", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE, "num [denom]")),
            entry("sin", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "angle")),
            entry("cos", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "angle")),
            entry("tan", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "angle")),
            entry("radarctan", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE, "num [denom]")),
            entry("radsin", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "angle")),
            entry("radcos", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "angle")),
            entry("radtan", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "angle")),
            entry("iseq", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "from to")),
            entry("rseq", ProcInfo.of(Arity.fixed(3), Returns.VALUE, "from to count")),
            entry("random", ProcInfo.of(Arity.capped(1, 2), Returns.VALUE, "num [end]")),
            entry("rerandom", ProcInfo.of(Arity.capped(0, 1), Returns.VOID, "[seed]")),
            entry("form", ProcInfo.of(Arity.fixed(3), Returns.VALUE, "num width precision")),
            entry("power", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "base exp")),

            // Bitwise (value)
            entry("bitand", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "num1 num2 ...")),
            entry("bitor", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "num1 num2 ...")),
            entry("bitxor", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "num1 num2 ...")),
            entry("bitnot", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "num")),
            entry("ashift", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num count")),
            entry("lshift", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num count")),

            // Logical (value)
            entry("true", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("false", ProcInfo.of(Arity.fixed(0), Returns.VALUE, "")),
            entry("and", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "expr1 expr2 ...")),
            entry("or", ProcInfo.of(Arity.variadic(2), Returns.VALUE, "expr1 expr2 ...")),
            entry("xor", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "expr1 expr2")),
            entry("not", ProcInfo.of(Arity.fixed(1), Returns.VALUE, "expr")),

            // Numeric predicates (value)
            entry("lessp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("less?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("greaterp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("greater?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("lessequalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("lessequal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("greaterequalp", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2")),
            entry("greaterequal?", ProcInfo.of(Arity.fixed(2), Returns.VALUE, "num1 num2"))
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

    public static Returns getReturns(String procName) {
        ProcInfo info = BUILTIN_PROCS.get(procName.toLowerCase());
        return info != null ? info.returns : null;
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

    public static String getDescription(String procName) {
        ProcInfo info = BUILTIN_PROCS.get(procName.toLowerCase());
        return info != null ? info.description() : null;
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

    public record ProcInfo(Arity arity, Returns returns, String description) {
        public static ProcInfo of(Arity arity, Returns returns) {
            return new ProcInfo(arity, returns, "");
        }

        public static ProcInfo of(Arity arity, Returns returns, String description) {
            return new ProcInfo(arity, returns, description);
        }
    }
}