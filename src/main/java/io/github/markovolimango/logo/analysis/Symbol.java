package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public sealed abstract class Symbol {
    private final String name;
    private final Pos start, end;
    private final List<Pos[]> references;

    public Symbol(Token token) {
        this.name = token.text();
        this.start = token.start();
        this.end = token.end();
        this.references = new ArrayList<>();
        this.references.add(new Pos[]{start, end});
    }

    public void addReference(Pos start, Pos end) {
        references.add(new Pos[]{start, end});
    }

    public String getName() {
        return name;
    }

    public Pos getStart() {
        return start;
    }

    public Pos getEnd() {
        return end;
    }

    public List<Pos[]> getReferences() {
        return references;
    }

    public static final class Var extends Symbol {
        public Var(Token token) {
            super(token);
        }
    }

    public static final class Proc extends Symbol {
        private final List<String> requiredParams = new ArrayList<>();
        private final List<String> optionalParams = new ArrayList<>();

        public Proc(Token token) {
            super(token);
        }

        public void addRequiredParam(String param) {
            requiredParams.add(param);
        }

        public void addOptionalParam(String param) {
            optionalParams.add(param);
        }

        public String getDescription() {
            StringBuilder sb = new StringBuilder();
            for (String param : requiredParams) {
                sb.append(param);
                sb.append(" ");
            }
            for (String param : optionalParams) {
                sb.append("[").append(param).append("] ");
                sb.append(" ");
            }
            return sb.toString();
        }

    }
}
