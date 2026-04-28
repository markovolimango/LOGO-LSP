package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {
    private final Map<String, List<Symbol>> symbols = new HashMap<>();
    private final Scope parent;
    private final List<Scope> children = new ArrayList<>();
    private final Pos start, end;

    public Scope(Scope parent, Pos start, Pos end) {
        this.parent = parent;
        if (parent != null)
            parent.children.add(this);
        this.start = start;
        this.end = end;
    }

    public void addDefinition(Symbol symbol) {
        symbols.putIfAbsent(symbol.name(), new ArrayList<>());
        symbols.get(symbol.name()).add(symbol);
    }

    public Symbol.Var getVarDef(String name, Pos start) {
        var list = symbols.get(name);
        if (list != null)
            for (Symbol symbol : list.reversed())
                if (symbol instanceof Symbol.Var && start.isAfter(symbol.end()))
                    return (Symbol.Var) symbol;
        return parent != null ? parent.getVarDef(name, start) : null;
    }

    public Symbol.Proc getProcDef(String name, Pos start) {
        var list = symbols.get(name);
        if (list != null)
            for (Symbol symbol : list.reversed())
                if (symbol instanceof Symbol.Proc && start.isAfter(symbol.end()))
                    return (Symbol.Proc) symbol;
        return parent != null ? parent.getProcDef(name, start) : null;
    }

    public List<Scope> getChildren() {
        return children;
    }

    public Pos getStart() {
        return start;
    }

    public Pos getEnd() {
        return end;
    }
}
