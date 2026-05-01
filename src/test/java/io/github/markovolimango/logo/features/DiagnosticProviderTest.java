package io.github.markovolimango.logo.features;

import io.github.markovolimango.logo.lsp.DocumentState;
import org.eclipse.lsp4j.Diagnostic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticProviderTest {

    @Test
    void undefinedVariable_reported() {
        String source = "print :x";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined variable: x")));
    }

    @Test
    void definedVariable_notReported() {
        String source = "make \"x 10\nprint :x";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertFalse(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined variable: x")));
    }

    @Test
    void undefinedProcedure_reported() {
        String source = "unknownproc 1 2";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined procedure: unknownproc")));
    }

    @Test
    void definedProcedure_notReported() {
        String source = "to myproc\nend\nmyproc";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertFalse(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined procedure: myproc")));
    }

    @Test
    void builtinProcedure_notReported() {
        String source = "forward 100";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertFalse(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined procedure")));
    }

    @Test
    void unusedReturnValue_reported() {
        String source = "pos"; // pos returns a value
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Unused return value")));
    }

    @Test
    void expectedExpression_missing_reported() {
        String source = "forward pos"; // forward expects a value, pos returns a value (this is OK)
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);
        assertFalse(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Expected expression")));

        source = "forward fd 10"; // forward expects a value, fd returns VOID
        state = new DocumentState("file:///test.logo", source);
        diagnostics = DiagnosticProvider.getDiagnostics(state);
        assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Expected expression")));
    }

    @Test
    void procedureParameters_notReportedAsUndefined() {
        String source = "to myproc :a :b\n print :a\n print :b\nend";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertFalse(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined variable")));
    }

    @Test
    void scoping_localVariable_notReportedInside() {
        String source = "to myproc\n localmake \"l 5\n print :l\nend";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertFalse(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined variable: l")));
    }

    @Test
    void scoping_localVariable_reportedOutside() {
        String source = "to myproc\n localmake \"l 5\nend\nprint :l";
        DocumentState state = new DocumentState("file:///test.logo", source);
        List<Diagnostic> diagnostics = DiagnosticProvider.getDiagnostics(state);

        assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("Undefined variable: l")));
    }
}
