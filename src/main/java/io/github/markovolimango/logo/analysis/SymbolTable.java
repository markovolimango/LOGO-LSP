package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Pos;

import java.util.List;

public class SymbolTable {
    private final Scope globalScope;

    public SymbolTable(Scope globalScope) {
        this.globalScope = globalScope;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public Symbol.Proc getProcDef(String name, Pos start) {
        return globalScope.getProcDef(name, start);
    }

    public List<Symbol> getAllProcs(Pos start) {
        return globalScope.getAllProcs(start);
    }

    private Scope findDeepestScope(Pos start) {
        Scope current = globalScope;
        boolean digging = true;
        while (digging) {
            Scope childScope = current.getChildren().stream()
                    .filter(child -> start.isAfter(child.getStart()) && child.getEnd().isAfter(start))
                    .findFirst()
                    .orElse(null);
            if (childScope != null && childScope != current)
                current = childScope;
            else
                digging = false;
        }
        return current;
    }

    public Symbol.Var getVarDef(String name, Pos start) {
        return findDeepestScope(start).getVarDef(name, start);
    }

    public List<Symbol> getAllVars(Pos start) {
        return findDeepestScope(start).getAllVars(start);
    }

    public List<Pos[]> getVarRefs(String name, Pos start) {
        var def = getVarDef(name, start);
        if (def == null) return null;
        return def.getReferences();
    }

    public List<Pos[]> getProcRefs(String name, Pos start) {
        var def = getProcDef(name, start);
        if (def == null) return null;
        return def.getReferences();
    }
}