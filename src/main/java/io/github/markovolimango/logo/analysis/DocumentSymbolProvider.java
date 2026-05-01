package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DocumentSymbolProvider {
    public static List<DocumentSymbol> getSymbols(DocumentState state) {
        var res = new ArrayList<DocumentSymbol>();
        var vars = state.getSymTable().getAllVars(state.getTokens().getLast().start());
        for (var var : vars)
            res.add(new DocumentSymbol(
                    var.getName(),
                    SymbolKind.Variable,
                    LspConverter.toRange(var.getStart(), var.getEnd()),
                    LspConverter.toRange(var.getStart(), var.getEnd())
            ));
        for (var scope : state.getSymTable().getGlobalScope().getChildren())
            res.add(getScopeSymbol(scope));
        res.sort(Comparator.comparing(s -> s.getRange().getStart(), (p1, p2) -> {
            if (p1.getLine() != p2.getLine()) {
                return Integer.compare(p1.getLine(), p2.getLine());
            }
            return Integer.compare(p1.getCharacter(), p2.getCharacter());
        }));
        return res;
    }

    public static DocumentSymbol getScopeSymbol(Scope scope) {
        var vars = new ArrayList<DocumentSymbol>();
        for (var var : scope.getLocalVars()) {
            vars.add(new DocumentSymbol(
                    var.getName(),
                    SymbolKind.Variable,
                    LspConverter.toRange(var.getStart(), var.getEnd()),
                    LspConverter.toRange(var.getStart(), var.getEnd())
            ));
        }
        for (var child : scope.getChildren())
            vars.add(getScopeSymbol(child));
        var ds = new DocumentSymbol(
                scope.getName(),
                SymbolKind.Function,
                LspConverter.toRange(scope.getStart(), scope.getEnd()),
                LspConverter.toRange(scope.getStart(), scope.getEnd())
        );
        ds.setChildren(vars);
        return ds;
    }
}
