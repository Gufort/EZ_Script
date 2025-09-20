public interface ILexer<TokenType>{
    String[] getLines();
    TokenT<TokenType> nextToken() throws Exception;
    boolean TokenTypeisEof(TokenType tt);
}
