import java.util.Arrays;

public abstract class ParserBase {
    protected ILexer<LexerUnit.TokenType> lexer;
    protected TokenT<LexerUnit.TokenType> currentToken;
    protected int current = 0;

    public ParserBase(ILexer<LexerUnit.TokenType> lexer) throws Exception{
        this.lexer = lexer;
        nextLexem();
    }

    public TokenT<LexerUnit.TokenType> peekToken() { return currentToken; }
    public TokenT<LexerUnit.TokenType> CurrentToken() { return currentToken; }
    public boolean isAtEnd() { return peekToken().type == LexerUnit.TokenType.EOF; }

    //Вернуть текущий токен и перейти к следующему
    public TokenT<LexerUnit.TokenType> nextLexem() throws Exception {
        var tmp = currentToken;
        currentToken = lexer.nextToken();
        return tmp;
    }

    //Проверяем, что тип текущего токена совпадает с одним из данных типов
    public boolean at(LexerUnit.TokenType... types) {
        if (currentToken == null) return false;
        for (LexerUnit.TokenType type : types) {
            if (currentToken.type == type) return true;
        }
        return false;
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
            nextLexem();
            return true;
        }
        return false;
    }

    // Проверить на соответствие и вернуть токен или выбросить ошибку
    // В отличие от At в случае неуспеха бросает ошибку
    public TokenT<LexerUnit.TokenType> requires(LexerUnit.TokenType... types) throws Exception {
        if(at(types)){
            return nextLexem();
        }
        else ExpectedError(types);
        return null;
    }
}
