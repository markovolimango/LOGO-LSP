package io.github.markovolimango.logolsp.lexer;

import java.util.Map;

public final class Keywords {
    public static final Map<String, Token.Type> map = Map.ofEntries(
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
}
