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

public class Main {
//    public static void firstTest() throws Exception {
//        var lex  = new Basic.LexerUnit.Lexer("a = 35; if a > 40 then a = 40 else { a = 0; print(a * 223 + 10) }");
//        Basic.LexerUnit.Token token;
//        do {
//            token = lex.nextToken();
//            System.out.println(token.type + ": " + token.value);
//        } while (token.type != Basic.LexerUnit.TokenType.EOF);
//    }

//    public static void secondTest() throws Exception{
//        String text = "i = 1; sum = 0; n = 100000000;" +
//                "while (i<100000000) do {sum += 1/i; i += 1 };" +
//                "Print(sum);" +
//                "if(i == 1) then { Print(sum) }"
//                +"else { Print(52) }";
//
//        var lex = new Basic.LexerUnit.Lexer(text);
//        try {
//            var par = new Basic.Parser(lex);
//            var progr = par.mainProgram();
//            System.out.println(progr);
//        }
//        catch (ExceptionLogic.CompilerException.LexerException e) {
//            ExceptionLogic.CompilerException.outputError("Lexer error:", e, lex.getLines());
//        }
//        catch (ExceptionLogic.CompilerException.SyntaxException e) {
//            ExceptionLogic.CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
    public static void thirdTest() throws Exception{
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

    public static void fourthTest() throws Exception{
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

    public static void fifthTest() throws Exception{
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
//
//    public static void sixthTest() throws Exception{
//        var text = "i = sqrt(1); sum = 0.0; while i<100000000 do {sum += 1/i; i += 1}; print(sum)";
//        var lex = new Basic.LexerUnit.Lexer(text);
//        try{
//            var par = new Basic.Parser(lex);
//            var progr = par.mainProgram();
//            var semanticCheck = new SemanticCheckLogic.SemanticCheck();
//            progr.visitP(semanticCheck);
//        }
//        catch (ExceptionLogic.CompilerException.LexerException e) {
//            ExceptionLogic.CompilerException.outputError("Lexer error:", e, lex.getLines());
//        }
//        catch (ExceptionLogic.CompilerException.SyntaxException e) {
//            ExceptionLogic.CompilerException.outputError("Basic.Parser error:", e, lex.getLines());
//        }
//        catch (ExceptionLogic.CompilerException.SemanticException e){
//            ExceptionLogic.CompilerException.outputError("Semantic error:", e, lex.getLines());
//        }
//    }

    public static void main(String[] args) throws Exception {
        //secondTest();

        System.out.println("======> Без Interpret.InterpretTree <======");
        thirdTest();

        System.out.println("\n");
        System.out.println("======> Что-то похожее на с# <======");
        fourthTest();
        System.out.println("\n");
        System.out.println("======> Pascal + Python <======");
        fifthTest();

        System.out.println();
        //sixthTest();
    }
}
