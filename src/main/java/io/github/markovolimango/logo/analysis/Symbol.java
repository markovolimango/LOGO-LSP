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
        public Proc(Token token) {
            super(token);
        }
    }
}
