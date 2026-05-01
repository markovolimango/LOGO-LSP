package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lsp.DocumentState;
import io.github.markovolimango.logo.lsp.LspConverter;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;

import java.util.ArrayList;
import java.util.List;

public class DocumentSymbolProvider {
    public static List<DocumentSymbol> getSymbols(DocumentState state) {
        var res = new ArrayList<DocumentSymbol>();
        var vars = state.getSymTable().getAllVars(state.getTokens().getLast().start());
        var procs = state.getSymTable().getAllProcs(state.getTokens().getLast().start());
        for (var var : vars)
            res.add(new DocumentSymbol(
                    var.getName(),
                    SymbolKind.Variable,
                    LspConverter.toRange(var.getStart(), var.getEnd()),
                    LspConverter.toRange(var.getStart(), var.getEnd())
            ));
        for (var proc : procs)
            res.add(new DocumentSymbol(
                    proc.getName(),
                    SymbolKind.Function,
                    LspConverter.toRange(proc.getStart(), proc.getEnd()),
                    LspConverter.toRange(proc.getStart(), proc.getEnd())
            ));
        return res;
    }
}
