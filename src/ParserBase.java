import java.util.Arrays;

public class ParserBase {
    public LexerUnit.Lexer lexer;
    protected LexerUnit.Token currentToken;

    public ParserBase(LexerUnit.Lexer lexer) throws Exception{
        this.lexer = lexer;
        advance();
    }

    public LexerUnit.Token peekToken() { return currentToken; }
    public LexerUnit.Token CurrentToken() { return currentToken; }
    public boolean isAtEnd() { return peekToken().type == LexerUnit.TokenType.EOF; }

    //Вернуть текущий токен и перейти к следующему
    public LexerUnit.Token advance() throws Exception {
        var tmp = currentToken;
        currentToken = lexer.nextToken();
        return tmp;
    }

    //Проверяем, что тип текущего токена совпадает с одним из данных типов
    public boolean at(LexerUnit.TokenType... types) throws Exception {
        return Arrays.stream(types).anyMatch(t -> peekToken().type == t);
    }

    public void check(LexerUnit.TokenType... types) throws Exception {
        if(!at(types))
            ExpectedError(types);
    }

    public void ExpectedError(LexerUnit.TokenType... types) throws Exception {
        String expected = String.join(" или ", Arrays.stream(types).map(Enum::name).toArray(String[]::new));
        CompilerException.syntaxError(expected + " ожидалось, но " + peekToken().type.name() + " найдено", peekToken().position);
    }

    // Проверить, что тип текушего токена совпадает с одним из данных типов
    // В случае успеха перейти к следующему токену
    public boolean isMatch(LexerUnit.TokenType... types) throws Exception {
        if(at(types)){
            advance();
            return true;
        }
        return false;
    }

    // Проверить на соответствие и вернуть токен или выбросить ошибку
    // В отличие от At в случае неуспеха бросает ошибку
    public LexerUnit.Token requires(LexerUnit.TokenType... types) throws Exception {
        if(at(types)){
            return advance();
        }
        else ExpectedError(types);
        return null;
    }
}
