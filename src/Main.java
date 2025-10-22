import Basic.*;
import ExceptionLogic.CompilerException;
import Interpret.ConvertASTToInterpretTreeVisitor;
import Interpret.InterpretTree;
import PrettyPrinters.PrettyPrinterFirst;
import PrettyPrinters.PrettyPrinterSecond;
import SemanticCheckLogic.SemanticCheck;
import java.util.logging.Logger;
import VirtualMachine.*;
import com.sun.tools.attach.VirtualMachine;

import java.util.ArrayList;

public class Main {
    public static void firstTest() throws Exception{
        String text = "i = 1; sum = 0; n = 100000000;" +
                "while (i<100000000) do {sum += 1/i; i += 1} ;" +
                "Print(sum);" +
                "if (i == 1) then { Print(sum) }"
                +"else { Print(52) };"
                +"for(d = 0; d < 5; d += 1) do { Print(4) }";

        var lex = new LexerUnit.Lexer(text);
        try {
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new SemanticCheck());
            var start = System.currentTimeMillis();
            var end = System.currentTimeMillis();
            var pp = new PrettyPrinterFirst();
            System.out.println(progr.visit(pp));
            //System.out.println(progr + "\n" + (end - start)/1000 + " c");
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void secondTest() throws Exception{
        String text = "i = 1; sum = 0; n = 100000000;" +
                "while (i<n) do {sum += 1/i; i += 1} ;" +
                "Print(sum);" +
                "if (i == 1) then { Print(sum) }"
                +"else { Print(52) };"
                +"k = 100;"
                +"for(d = 0; d < k; d += 1) do { sum += 1 };"
                +"Print(sum)";
        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new SemanticCheck());
            InterpretTree.StatementNodeI rooti = (InterpretTree.StatementNodeI)progr.visit(new ConvertASTToInterpretTreeVisitor());
            var pp = new PrettyPrinterFirst();
            rooti.execute();
            System.out.println(progr.visit(pp));
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
    }

    public static void thirdTest() throws Exception{
        String text = "i = 1; sum = 123123; n = 100000000;" +
                "while (i<n) do {sum += 1; i += 1} ;" +
                "Print(sum);" +
                "if (i == 1) then { Print(sum) }"
                +"else { Print(52) };"
                +"k = 100;"
                +"for(d = 0; d < k; d += 1) do { sum += 1; sum += 1 };"
                +"Print(sum)";

        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new SemanticCheck());
            InterpretTree.StatementNodeI rooti = (InterpretTree.StatementNodeI)progr.visit(new ConvertASTToInterpretTreeVisitor());
            var pp = new PrettyPrinterSecond();
            rooti.execute();
            System.out.println(progr.visit(pp));
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
    }

    public static void fourthTest() throws Exception{
        String text = "i = 1; sum = 1; n = 100000;" +
                "while (i<n) do {sum += i; i += 1} ;" +
                "Print(sum);" +
                "if (i == 1) then { Print(sum) }"
                +"else { Print(52) };"
                +"k = 100;"
                +"for(d = 1; d < k; d += 1) do { sum += 1 };"
                +"Print(sum)";

        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            var generator = new ThreeAddressVisitor();
            progr.visitP(new SemanticCheck());
            progr.visitP(generator);
            generator.Stop();
            SimpleVirtualMachine.initialize();
            SimpleVirtualMachine.loadProgram(generator.getCode());
            SimpleVirtualMachine.run();
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
    }


    // LoggerVisitor Test
    public static void fifthTest() throws Exception{
        String text = "i = 1; sum = 0; n = 100000000;" +
                "while (i<100000000) do {sum += 1/i; i += 1} ;" +
                "Print(sum);" +
                "if (i == 1) then { Print(sum) }"
                +"else { Print(52) };"
                +"for(d = 0; d < 5; d += 1) do { Print(4) }";

        var lex = new LexerUnit.Lexer(text);
        try {
            Logger logger = Logger.getLogger(Main.class.getName());
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new LoggerVisitor(logger));
            var pp = new PrettyPrinterFirst();
            System.out.println(progr.visit(pp));
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // AssertVisitor Test
    public static void sixthTest() throws Exception{
        String text = "i = 1; sum = 0; n = 100000000;" +
                "while (i<n) do {sum += 1/i; i += 1} ;" +
                "Print(sum);" +
                "if (i == 1) then { Print(sum) }"
                +"else { Print(52) };"
                +"for(d = 0; d < 5; d += 1) do { Print(4) }";

        var lex = new LexerUnit.Lexer(text);
        try {
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new AssertVisitor());
            var pp = new PrettyPrinterSecond();
            System.out.println(progr.visit(pp));
            System.out.println("GOOD: all validation successfully");
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        thirdTest();
    }
}