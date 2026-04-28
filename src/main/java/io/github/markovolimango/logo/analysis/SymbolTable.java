package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Pos;

public class SymbolTable {
    private final Scope globalScope;

    public SymbolTable(Scope globalScope) {
        this.globalScope = globalScope;
    }

    public Symbol get(String name, Pos start) {
        Scope current = globalScope;
        boolean digging = true;

        while (digging) {
            Scope childScope = current.getChildren().stream()
                    .filter(child -> start.isAfter(child.getStart()) && child.getEnd().isAfter(start))
                    .findFirst()
                    .orElse(null);

            if (childScope != null) {
                current = childScope;
            } else {
                digging = false;
            }
        }

        return current.getDefinition(name, start);
    }
}