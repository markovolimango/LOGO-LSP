package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.analysis.Symbol;
import io.github.markovolimango.logo.lexer.Lexer;
import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

import java.util.ArrayList;
import java.util.List;

public class CompletionProvider {
    public static List<CompletionItem> getCompletion(DocumentState state, Pos pos) {
        var lexer = new Lexer(state.getLine(pos.line()));
        var token = lexer.getTokenAt(new Pos(0, pos.col() - 1));

        List<CompletionItem> items = new ArrayList<>();

        if (token.text().startsWith("t")) {
            CompletionItem toItem = new CompletionItem("to");
            toItem.setKind(CompletionItemKind.Snippet);
            toItem.setInsertTextFormat(InsertTextFormat.Snippet);
            toItem.setInsertText("to ${1:name}\n  $0\nend");
            toItem.setDetail("Define a procedure");
            toItem.setDocumentation("Defines a new named procedure");
            items.add(toItem);
        }

        state.getSymTable().getAllVars(pos).stream()
                .map(Symbol::getName)
                .distinct()
                .forEach(name -> {
                    if (token.type() == Token.Type.VARREF) {
                        CompletionItem item = new CompletionItem(":" + name);
                        item.setKind(CompletionItemKind.Variable);
                        item.setDetail("Variable");
                        items.add(item);
                    }
                });

        state.getSymTable().getAllProcs(pos).stream()
                .map(Symbol::getName)
                .distinct()
                .forEach(name -> {
                    if (token.type() == Token.Type.PROC) {
                        CompletionItem item = new CompletionItem(name);
                        item.setKind(CompletionItemKind.Function);
                        item.setDetail("Procedure");
                        items.add(item);
                    }
                });


        return items;
    }
}
