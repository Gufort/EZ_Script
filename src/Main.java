import Basic.*;
import ExceptionLogic.CompilerException;
import Interpret.ConvertASTToInterpretTreeVisitor;
import Interpret.InterpretTree;
import Interpret.Memory;
import PrettyPrinters.PrettyPrinterFirst;
import PrettyPrinters.PrettyPrinterSecond;
import SemanticCheckLogic.SemanticCheck;

import java.io.*;
import java.math.BigInteger;
import java.util.Random;
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
            "while(i < n) do { bigInt = bigInt + 1bi; i += 1 }; " +
            "print(bigInt); bigInt *= 2bi; print(bigInt)";

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
        String text = "arr = [1,2,3,4]; print(arr)";
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

    public static void tenthTest() throws Exception {
        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("tests.txt"))) {
            String line;
            while ((line = br.readLine()) != null)
                text.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        text.append("dump()");

        var lex = new LexerUnit.Lexer(text.toString());
        try {
            var par = new Parser(lex);
            var progr = par.mainProgram();
            progr.visitP(new SemanticCheck());
            InterpretTree.StatementNodeI rooti = (InterpretTree.StatementNodeI) progr.visit(new ConvertASTToInterpretTreeVisitor());

            System.out.println("=== ВЫПОЛНЕНИЕ ПРОГРАММЫ ===");
            var start = System.nanoTime();
            rooti.execute();
            var end = System.nanoTime();

            System.out.println("\n=== Код ===");
            var pp = new PrettyPrinterSecond();
            System.out.println(progr.visit(pp));

            long duration = (end - start) / 1_000_000;
            System.out.println("\nВремя выполнения: " + duration + " мс");
        } catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        } catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
        }
    }

    public static void eleventhTest() throws Exception{
        String text = "int[] arr = new int[] {1,2,3,5656,4}; int[] arr = {123,2}; print(arr[1])";

        var lex = new LexerUnit.Lexer(text);
        try{
            var par = new Parser(lex);
            var progr = par.mainProgram();
            var generator = new ThreeAddressVisitor();
            progr.visitP(new SemanticCheck());
            progr.visitP(generator);
            generator.Stop();
            SimpleVirtualMachine.startProgram(generator.getCode());
            System.out.println(progr.visit(new PrettyPrinterSecond()));
        }
        catch (CompilerException.LexerException e) {
            CompilerException.outputError("Lexer error:", e, lex.getLines());
        }
        catch (CompilerException.SyntaxException e) {
            CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
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

    public static void generateArrays(String path){
        try (FileWriter fw = new FileWriter(path, false);
            BufferedWriter bw = new BufferedWriter(fw)) {
            Random random = new Random();

            int sizeOfMemory = 8192;
            int index = 0;
            while (true) {
                int size = random.nextInt(30) + 15;
                if(sizeOfMemory - size < 0) break;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(random.nextInt(1000) + 1);
                }
                index++;
                sizeOfMemory -= size;
                bw.write("int[] arr" + index + " = {" + sb + "};");
                bw.newLine();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void freeSomeArrays(String path){
        try (FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("dump();");
            bw.newLine();
            for(int i = 1; i < 250; i += 2){
                bw.write("free(arr" + i + ");");
                bw.newLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        //Memory.testMemoryManager(Memory.AllocationStrategy.FIRST_FIT);
        generateArrays("tests.txt");
        freeSomeArrays("tests.txt");
        tenthTest();
    }
}