public class TokenT<TokenType>{
    public TokenType type;
    public Position position;
    public Object value;
    public TokenT(TokenType type, Position position, Object value){
        this.type = type;
        this.position = position;
        this.value = value;
    }
}
