# LOGO-LSP
LSP language server for Logo

## Features
- **Syntax highlighting** - semantic tokens for keywords, variables, functions, operators, numbers, strings and comments, with default library modifiers
- **Go to declaration/definiton/references** - scope-aware go to declaration/definiton/references for variables and procedures
- **Rename refactoring** - smart renaming of procedures and variables, accounts for multiple definitions of the same symbol
- **Diagnostics reporting** - errors for undefined procedures/variables, unexpected tokens, invalid arguments and more
- **Completion** - completion suggestions of procedures, local and global variables and built-in keywords, as well as `TO` procedure definition syntax (auto inserting `END`)

## Build and run
```bash
git clone https://github.com/markovolimango/LOGO-LSP.git
cd LOGO-LSP
./gradlew shadowJar
```
### Requirements
- Java 21 or higher

### Connect to the LSP4IJ plugin
1. Install the LSP4IJ plugin for IntelliJ IDEA
2. Settings → Languages & Frameworks → Language Servers → Add Language server
3. Command: java -jar /absolute/path/to/LOGO-LSP-9.4.0.jar
4. File association: *.logo

## Architecture
- Pipeline: source text -> tokens -> AST -> symbol table -> LSP responses
- Each stage is a separate package with no upward dependencies, lsp4j types are only used in the lsp package and as returns values in the features package.
- Project layout:
  ```
  src/main/java/io/github/markovolimango/logo/
  ├── lexer/               # Tokenisation: Lexer, Token, Pos
  ├── parser/              # AST: Node (sealed interface), Parser, AstWalker, AstPrinter
  ├── analysis/            # Symbol table: SymbolTableBuilder, SymbolTable, Scope, Symbol
  ├── features/            # One class per LSP feature, with static methods working with DocumentState
  ├── lsp/                 # LSP wiring: server, services, DocumentState, LspConverter
  ├── Main.java            # Entry point, just launches the server
  └── LogoLanguage.java    # Language constants (token types, built-in names)
  ```
## Key design decisions
**Pratt parser for expressions** - 
The parser uses a top-down operator precedence approach for expresson parsing. 
Each operator has a bidning power stored in LogoLanguage.

**No statement/expression distinction** - 
Every procedure call (including make, repeat, if) is parsed as an expression. 
In Logo procedures may or may not output a value depending on the execution path.
That's something that i feel shold be checked at runtime, not statically by an LSP server.

**TO and DEFINE as separate AST nodes** - 
'TO' procedure definition is the only Logo language construct that's not a procedure, so it's treated completely separately.
'DEFINE', on the other hand, is just a procedure, that can take any expression as an argument. If the name is not a literal, it's not registered in the symbol table.

**Scope tree with reverse-ordered definition lookup** - 
Each Scope sotres a list of Symbols in definition order to allow for redefinitions.
Lookup then iterates the list in reverse to find the last definition.
Variable resolution recursively walks up to the nearest definition, while all procedures are global.

**Features as the protocol boundary** - 
Each class in features/ takes Lexer/Parser/Analysis types (DocumentClass is just a collection of them), and returns lsp4j types.

**Debounced reparse on didChange** - 
Rather than rebuilding DocumentState on every keystroke, didChange schedules a reparse 150ms later.
If a new change occurs in that time, the old reparse is canceled.
