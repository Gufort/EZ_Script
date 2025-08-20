//Текущий токен
public class TokenBase{
    Position position; //Позиция начала токена
    Object value;
    public TokenBase(Position position, Object value){
        this.position = position;
        this.value = value;
    }
}