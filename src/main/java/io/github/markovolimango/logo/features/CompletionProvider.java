package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lexer.Pos;
import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

import java.util.ArrayList;
import java.util.List;

public class CompletionProvider {
    public static List<CompletionItem> getCompletion(DocumentState state, Pos pos) {
        List<CompletionItem> items = new ArrayList<>();

        CompletionItem toItem = new CompletionItem("to");
        toItem.setKind(CompletionItemKind.Snippet);
        toItem.setInsertTextFormat(InsertTextFormat.Snippet);
        toItem.setInsertText("to ${1:name}\n  $0\nend");
        toItem.setDetail("Define a procedure");
        toItem.setDocumentation("Defines a new named procedure");
        items.add(toItem);

        if (state != null) {
            state.getSymTable().getVarNames(pos).stream()
                    .distinct()
                    .forEach(name -> {
                        CompletionItem item = new CompletionItem(":" + name);
                        item.setKind(CompletionItemKind.Variable);
                        item.setDetail("Variable");
                        items.add(item);
                    });

            state.getSymTable().getProcNames(pos).stream()
                    .distinct()
                    .forEach(name -> {
                        CompletionItem item = new CompletionItem(name);
                        item.setKind(CompletionItemKind.Function);
                        item.setDetail("Procedure");
                        items.add(item);
                    });
        }


        return items;
    }
}
