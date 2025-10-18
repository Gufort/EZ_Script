import Basic.ASTNodes;
import Basic.LexerUnit;
import Basic.Parser;
import ExceptionLogic.CompilerException;
import Interpret.ConvertASTToInterpretTreeVisitor;
import Interpret.InterpretTree;
import PrettyPrinters.PrettyPrinterFirst;
import PrettyPrinters.PrettyPrinterSecond;
import SemanticCheckLogic.SemanticCheck;
import VirtualMachine.*;

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
        String text = "i = 1; sum = 0; n = 100000000;" +
                "while (i<n) do {sum += 1/i; i += 1} ;" +
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
        String text = "i = 111; sum = 1; n = 100000000;" +
                "while (i<n) do {sum += 1/i; i += 1} ;" +
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

            // ТЕСТИРОВАНИЕ VIRTUAL MACHINE
            System.out.println("======> Virtual Machine Test <======");

            // Создаем визитор для генерации TAC
            ThreeAddressVisitor tacVisitor = new ThreeAddressVisitor();
            progr.visitP(tacVisitor);
            tacVisitor.Stop(); // Завершаем генерацию кода

            ArrayList<ThreeAddressCode> tacCode = tacVisitor.getCode();

            System.out.println("\nРезультат выполнения VM:");
            SimpleVirtualMachine.loadProgram(tacCode);
            SimpleVirtualMachine.run();

            System.out.println("\n" + "=".repeat(50));

            // Для сравнения запускаем старый интерпретатор
            System.out.println("======> Старый интерпретатор <======");
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

    public static void main(String[] args) throws Exception {
//        System.out.println("======> Без Interpret.InterpretTree <======");
//        firstTest();
//
//        System.out.println("\n");
//        System.out.println("======> Что-то похожее на с# <======");
//        secondTest();
//        System.out.println("\n");
//        System.out.println("======> Pascal + Python <======");
//        thirdTest();
//
//        System.out.println();
        fourthTest();
    }
}