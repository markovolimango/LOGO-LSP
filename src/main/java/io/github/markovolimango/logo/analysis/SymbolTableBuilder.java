package io.github.markovolimango.logo.analysis;

import io.github.markovolimango.logo.lexer.Token;
import io.github.markovolimango.logo.parser.AstWalker;
import io.github.markovolimango.logo.parser.Node;

import java.util.Stack;

public class SymbolTableBuilder extends AstWalker {
    private final Stack<Scope> scopeStack = new Stack<>();
    private Scope globalScope;

    @Override
    public void walk(Node node) {
        switch (node) {
            case Node.ToStmt n -> {
                globalScope.addDefinition(new Symbol.Proc(n.name()));
                scopeStack.push(new Scope(currentScope(), n.start(), n.end()));
                for (Token param : n.params())
                    currentScope().addDefinition(new Symbol.Var(param));
                n.body().forEach(this::walk);
                scopeStack.pop();
            }
            case Node.DefineStmt n -> {
                if (n.name() instanceof Node.Word) {
                    Token nameToken = ((Node.Word) n.name()).value();
                    globalScope.addDefinition(new Symbol.Proc(nameToken));
                }
                scopeStack.push(new Scope(currentScope(), n.start(), n.end()));
                for (Node param : n.params().body()) {
                    if (param instanceof Node.Word) {
                        Token paramToken = ((Node.Word) param).value();
                        currentScope().addDefinition(new Symbol.Var(paramToken));
                    } else if (param instanceof Node.ProcCall) {
                        Token paramToken = ((Node.ProcCall) param).name();
                        currentScope().addDefinition(new Symbol.Var(paramToken));
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
                    globalScope.addDefinition(new Symbol.Var(nameToken));
                }
            }
            case Node.LocalMakeStmt n -> {
                super.walk(n.value());
                if (n.name() instanceof Node.Word) {
                    Token nameToken = ((Node.Word) n.name()).value();
                    currentScope().addDefinition(new Symbol.Var(nameToken));
                }
            }
            case Node.ProcCall p -> {
                var sym = globalScope.getProcDef(p.name().text(), p.name().start());
                if (sym != null) sym.addReference(p.start(), p.end());
                super.walk(p);
            }
            case Node.VarRef v -> {
                var sym = currentScope().getVarDef(v.name().text(), v.name().start());
                if (sym != null) sym.addReference(v.start(), v.end());
                super.walk(v);
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
