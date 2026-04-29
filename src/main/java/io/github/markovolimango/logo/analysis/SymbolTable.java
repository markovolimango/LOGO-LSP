package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Pos;

import java.util.List;

public class SymbolTable {
    private final Scope globalScope;

    public SymbolTable(Scope globalScope) {
        this.globalScope = globalScope;
    }

    public Symbol.Proc getProcDef(String name, Pos start) {
        return globalScope.getProcDef(name, start);
    }

    public List<String> getProcNames(Pos start) {
        return globalScope.getAllProcNames(start);
    }

    private Scope findDeepestScope(Pos start) {
        Scope current = globalScope;
        boolean digging = true;
        while (digging) {
            Scope childScope = current.getChildren().stream()
                    .filter(child -> start.isAfter(child.getStart()) && child.getEnd().isAfter(start))
                    .findFirst()
                    .orElse(null);
            if (childScope != null)
                current = childScope;
            else
                digging = false;
        }
        return current;
    }

    public Symbol.Var getVarDef(String name, Pos start) {
        return findDeepestScope(start).getVarDef(name, start);
    }

    public List<String> getVarNames(Pos start) {
        return findDeepestScope(start).getAllVarNames(start);
    }
}