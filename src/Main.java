import Basic.*;
import ExceptionLogic.CompilerException;
import Interpret.ConvertASTToInterpretTreeVisitor;
import Interpret.InterpretTree;
import PrettyPrinters.PrettyPrinterFirst;
import PrettyPrinters.PrettyPrinterSecond;
import SemanticCheckLogic.SemanticCheck;

import java.math.BigInteger;
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

    public static void secondTest() throws Exception{
        String text = "i = 0; sum = 0; n = 100000000;" +
                "while (i<n) do {sum += 1; i += 1} ;" +
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
        String text = "i = 0; sum = 0.0; n = 100000000;" +
                "while (i<n) do {sum = sum + 1.0; i += 1} ;" +
                "print(sum);" +
                "if (i == 1) then { print(sum) }" +
                "else { print(52) };" +
                "for(d = 0; d < 5; d += 1) do { sum += 1.0 };" +
                "print(sum)";

        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new SemanticCheck());
            InterpretTree.StatementNodeI rooti = (InterpretTree.StatementNodeI)progr.visit(new ConvertASTToInterpretTreeVisitor());
            var pp = new PrettyPrinterSecond();
            var start = System.nanoTime();
            rooti.execute();
            var end = System.nanoTime();
            System.out.println(progr.visit(pp));
            long duration = (end - start) / 1_000_000;
            System.out.println("Время выполнения: " + duration + " мс");
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
    }

    public static void fourthTest() throws Exception{
        String text = "i = 0; sum = 0.0; n = 100000000;" +
                "while (i<n) do {sum = sum + 1.0; i += 1} ;" +
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
            var generator = new ThreeAddressVisitor();
            progr.visitP(new SemanticCheck());
            progr.visitP(generator);
            generator.Stop();
            var start = System.nanoTime();
            SimpleVirtualMachine.startProgram(generator.getCode());
            var end = System.nanoTime();
            long duration = (end - start) / 1_000_000;
            System.out.println("Время выполнения: " + duration + " мс");
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

    public static void seventhTest() throws Exception{
        String text = "i = 1; sum = 0; n = 100000000; bigInt = 1234bi; " +
            "while(i < n) do { bigInt += 1; i += 1 }; " +
            "print(bigInt); bigInt *= 2bi; print(n)";

        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new SemanticCheck());
            InterpretTree.StatementNodeI rooti = (InterpretTree.StatementNodeI)progr.visit(new ConvertASTToInterpretTreeVisitor());
            var pp = new PrettyPrinterSecond();
            var start = System.nanoTime();
            rooti.execute();
            var end = System.nanoTime();
            long duration = (end - start) / 1_000_000;
            System.out.println(progr.visit(pp));
            System.out.println("Время выполнения: " + duration + " мс");
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
    }

    public static void eighthTest() throws Exception{
        String text = "i = 1; sum = 0; n = 1000000; bigInt = 1234bi; "+
                "while(i < n) do {bigInt = bigInt + 1bi; i += 1 }; "+
                "print(bigInt); bigInt *= 2bi; print(bigInt)";
        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            var generator = new ThreeAddressVisitor();
            progr.visitP(new SemanticCheck());
            progr.visitP(generator);
            generator.Stop();
            var start = System.nanoTime();
            SimpleVirtualMachine.startProgram(generator.getCode());
            var end = System.nanoTime();
            long duration = (end - start) / 1_000_000;
            System.out.println("Время выполнения: " + duration + " мс");
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Parser error:", e, lex.getLines());
        }
    }

    public static void ninethTest() throws Exception{
        String text = "arr = [1,2,3,4]; arr[2] = 2";
        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new SemanticCheck());
            var pp = new PrettyPrinterFirst();
            System.out.println(progr.visit(pp));
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Parser error:", e, lex.getLines());
        }
    }

    private static BigInteger testLoop() {
        int i = 0;
        BigInteger sum = new BigInteger("0");
        int n = 10000000;
        while(i < n) {
            sum.add(sum.multiply(BigInteger.valueOf(i)));
            i++;
        }
        return sum;
    }

    public static void main(String[] args) throws Exception {
        ninethTest();
    }
}

//        fourthTest();
//        System.out.println("_________________");
//        thirdTest();
//        System.out.println("_________________");
//        seventhTest();
//
//
//
//        long startTime = System.nanoTime();
//        var result = testLoop();
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime) / 1_000_000;
//
//        System.out.println("Результат: " + result);
//        System.out.println("Время выполнения: " + duration + " мс");