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
    private final String name;

    public Scope(Scope parent, Pos start, Pos end, String name) {
        this.parent = parent;
        if (parent != null)
            parent.children.add(this);
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addDefinition(Symbol symbol) {
        symbols.putIfAbsent(symbol.getName(), new ArrayList<>());
        symbols.get(symbol.getName()).add(symbol);
    }

    private Symbol getDef(String name, Pos start, Class<? extends Symbol> type) {
        var list = symbols.get(name);
        if (list != null)
            for (Symbol symbol : list.reversed())
                if (symbol.getClass() == type && !start.isBefore(symbol.getStart()))
                    return symbol;
        return parent != null ? parent.getDef(name, start, type) : null;
    }

    public Symbol.Var getVarDef(String name, Pos start) {
        return (Symbol.Var) getDef(name, start, Symbol.Var.class);
    }

    public Symbol.Proc getProcDef(String name, Pos start) {
        return (Symbol.Proc) getDef(name, start, Symbol.Proc.class);
    }

    public List<Symbol> getLocalSymbols(Class<? extends Symbol> type) {
        var res = new ArrayList<Symbol>();
        symbols.forEach((_, symbols) -> {
            for (Symbol symbol : symbols)
                if (symbol.getClass() == type)
                    res.add(symbol);
        });
        return res;
    }

    public List<Symbol> getAllSymbols(Pos start, Class<? extends Symbol> type) {
        var res = parent != null ? parent.getAllSymbols(start, type) : new ArrayList<Symbol>();
        symbols.forEach((_, symbols) -> {
            for (Symbol symbol : symbols)
                if (symbol.getClass() == type && start.isAfter(symbol.getEnd()))
                    res.add(symbol);
        });
        return res;
    }

    public List<Symbol> getLocalVars() {
        return getLocalSymbols(Symbol.Var.class);
    }

    public List<Symbol> getAllVars(Pos start) {
        return getAllSymbols(start, Symbol.Var.class);
    }

    public List<Symbol> getAllProcs(Pos start) {
        return getAllSymbols(start, Symbol.Proc.class);
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
