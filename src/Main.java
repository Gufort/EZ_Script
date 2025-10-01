import java.io.IOException;

public class Main {
//    public static void firstTest() throws Exception {
//        var lex  = new LexerUnit.Lexer("a = 35; if a > 40 then a = 40 else { a = 0; print(a * 223 + 10) }");
//        LexerUnit.Token token;
//        do {
//            token = lex.nextToken();
//            System.out.println(token.type + ": " + token.value);
//        } while (token.type != LexerUnit.TokenType.EOF);
//    }

//    public static void secondTest() throws Exception{
//        String text = "i = 1; sum = 0; n = 100000000;" +
//                "while (i<100000000) do {sum += 1/i; i += 1 };" +
//                "Print(sum);" +
//                "if(i == 1) then { Print(sum) }"
//                +"else { Print(52) }";
//
//        var lex = new LexerUnit.Lexer(text);
//        try {
//            var par = new Parser(lex);
//            var progr = par.mainProgram();
//            System.out.println(progr);
//        }
//        catch (CompilerException.LexerException e) {
//            CompilerException.outputError("Lexer error:", e, lex.getLines());
//        }
//        catch (CompilerException.SyntaxException e) {
//            CompilerException.outputError("Parser error:", e, lex.getLines());
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void thirdTest() throws Exception{
//        String text = "i = 1; sum = 0; n = 100000000;" +
//                "while (i<100000000) do {sum += 1/i; i += 1} ;" +
//                "Print(sum);" +
//                "if (i == 1) then { Print(sum) }"
//                +"else { Print(52) }";
//
//        var lex = new LexerUnit.Lexer(text);
//        try {
//            var par = new Parser(lex);
//            var progr = par.mainProgram();
//
//            var start = System.currentTimeMillis();
//            var end = System.currentTimeMillis();
//
//            System.out.println(progr + "\n" + (end - start)/1000 + " c");
//        }
//        catch (CompilerException.LexerException e) {
//            CompilerException.outputError("Lexer error:", e, lex.getLines());
//        }
//        catch (CompilerException.SyntaxException e) {
//            CompilerException.outputError("Parser error:", e, lex.getLines());
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
    public static void fourthTest() throws Exception{
        String text = "i = 2.0; sum = 0.0; n = 100000000;" +
                "Print(sum);" +
                "if(i == 1) then { Print(sum) }"
                +"else { Print(52) }";
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
            CompilerException.outputError("Parser error:", e, lex.getLines());
        }
    }

    public static void fifthTest() throws Exception{
        String text = "i = 1; sum = 0; n = 100000000;" +
                "Print(sum);" +
                "if(i == 1) then { Print(sum) }"
                +"else { Print(52) }";
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
            CompilerException.outputError("Parser error:", e, lex.getLines());
        }
    }
//
//    public static void sixthTest() throws Exception{
//        var text = "i = sqrt(1); sum = 0.0; while i<100000000 do {sum += 1/i; i += 1}; print(sum)";
//        var lex = new LexerUnit.Lexer(text);
//        try{
//            var par = new Parser(lex);
//            var progr = par.mainProgram();
//            var semanticCheck = new SemanticCheck();
//            progr.visitP(semanticCheck);
//        }
//        catch (CompilerException.LexerException e) {
//            CompilerException.outputError("Lexer error:", e, lex.getLines());
//        }
//        catch (CompilerException.SyntaxException e) {
//            CompilerException.outputError("Parser error:", e, lex.getLines());
//        }
//        catch (CompilerException.SemanticException e){
//            CompilerException.outputError("Semantic error:", e, lex.getLines());
//        }
//    }

    public static void main(String[] args) throws Exception {
        //secondTest();
        //thirdTest();

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
