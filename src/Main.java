import java.io.IOException;
public class Main {
    public static void main(String[] args) throws Exception {
        var lex  = new LexerUnit.Lexer("a = 35; if a > 40 then a = 40 else { a = 0; print(a * 223 + 10) }");
        LexerUnit.Token token;
        do {
            token = lex.nextToken();
            System.out.println(token.type + ": " + token.value);
        } while (token.type != LexerUnit.TokenType.EOF);
    }
}
