package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.ast.AstWalker;
import io.github.markovolimango.logo.ast.Node;

import java.util.Stack;

public class SymbolTableBuilder extends AstWalker {
    private final Stack<Scope> scopeStack = new Stack<>();
    private Scope globalScope;

    @Override
    public void walk(Node node) {
        switch (node) {
            case Node.ToStmt n -> {
                globalScope.addDefinition(new Symbol.Proc(n.name().text(), n.name().start(), n.name().end()));
                scopeStack.push(new Scope(currentScope(), n.start(), n.end()));
                for (Token param : n.params())
                    currentScope().addDefinition(new Symbol.Var(param.text(), param.start(), param.end()));
                super.walk(n);
                scopeStack.pop();
            }
            case Node.DefineStmt n -> {
                if (n.name() instanceof Node.Word) {
                    Token nameToken = ((Node.Word) n.name()).value();
                    globalScope.addDefinition(new Symbol.Proc(nameToken.text(), nameToken.start(), nameToken.end()));
                }
                scopeStack.push(new Scope(currentScope(), n.start(), n.end()));
                for (Node param : n.params().body()) {
                    if (param instanceof Node.Word) {
                        Token paramToken = ((Node.Word) param).value();
                        currentScope().addDefinition(new Symbol.Var(paramToken.text(), paramToken.start(), paramToken.end()));
                    } else if (param instanceof Node.ProcCall) {
                        Token paramToken = ((Node.ProcCall) param).name();
                        currentScope().addDefinition(new Symbol.Var(paramToken.text(), paramToken.start(), paramToken.end()));
                    } else {
                        super.walk(param);
                    }
                }
                super.walk(n.body());
                scopeStack.pop();
            }
            case Node.MakeStmt n -> {
                super.walk(n.value());
                if (n.name() instanceof Node.Word) {
                    Token nameToken = ((Node.Word) n.name()).value();
                    globalScope.addDefinition(new Symbol.Var(nameToken.text(), nameToken.start(), nameToken.end()));
                    currentScope().addDefinition(new Symbol.Var(nameToken.text(), nameToken.start(), nameToken.end()));
                }
            }
            case Node.LocalMakeStmt n -> {
                super.walk(n.value());
                if (n.name() instanceof Node.Word) {
                    Token nameToken = ((Node.Word) n.name()).value();
                    currentScope().addDefinition(new Symbol.Var(nameToken.text(), nameToken.start(), nameToken.end()));
                }
            }
            default -> super.walk(node);
        }
    }

    private Scope currentScope() {
        return scopeStack.peek();
    }

    public SymbolTable build(Node.Program program) {
        globalScope = new Scope(null, program.start(), program.end());
        scopeStack.push(globalScope);
        walk(program);
        return new SymbolTable(globalScope);
    }
}
