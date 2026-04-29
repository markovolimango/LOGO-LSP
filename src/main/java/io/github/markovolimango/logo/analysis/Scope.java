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

    private Symbol getDef(String name, Pos start, Class<? extends Symbol> type) {
        var list = symbols.get(name);
        if (list != null)
            for (Symbol symbol : list.reversed())
                if (symbol.getClass() == type && start.isAfter(symbol.end()))
                    return symbol;
        return parent != null ? parent.getDef(name, start, type) : null;
    }

    public Symbol.Var getVarDef(String name, Pos start) {
        return (Symbol.Var) getDef(name, start, Symbol.Var.class);
    }

    public Symbol.Proc getProcDef(String name, Pos start) {
        return (Symbol.Proc) getDef(name, start, Symbol.Proc.class);
    }

    public List<String> getAllNames(Pos start, Class<? extends Symbol> type) {
        var res = parent != null ? parent.getAllNames(start, type) : new ArrayList<String>();
        symbols.forEach((name, symbol) -> {
            for (Symbol s : symbol)
                if (s.getClass() == type && start.isAfter(s.end()))
                    res.add(name);
        });
        return res;
    }

    public List<String> getAllVarNames(Pos start) {
        return getAllNames(start, Symbol.Var.class);
    }

    public List<String> getAllProcNames(Pos start) {
        return getAllNames(start, Symbol.Proc.class);
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
