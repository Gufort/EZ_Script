import java.util.regex.Pattern;

public class LexerBase{
    public String code;//Код программы, который анализируется в конструкторе
    public int line = 1;//Текущая строка
    public int column = 0;//Текущий столбец
    public int currentPosition = 0;//Текущая позиция
    public int start = 0;//Стартовая позиция токена
    public boolean atEoln = false;//Сервисное поле для метода NextChar, флаг конца строки

    public Position getCurrentPosition() { return new Position(line, column); }
    public boolean isAtEnd() {return currentPosition >= code.length(); }
    public char peekChar(){ return isAtEnd() ? '\0' : code.charAt(currentPosition); }

    //Возвращает текущий символ и переходит к следующему
    public char nextChar(){
        char res = peekChar();
        if(atEoln){
            atEoln = false;
            line++;
            column = 0;
        }
        if(res == '\0') return res;
        if(res == '\n') atEoln = true;

        currentPosition++;
        column++;
        return res;
    }

    //Проверка совпадения с ожидаемым символом
    public boolean isMatch(char ch){
        if(peekChar() == ch) {
            nextChar();
            return true;
        }
        else return false;
    }

    //Вернуть следующий символ
    public char peekNextChar(){
        var pos = currentPosition + 1;
        if(pos > code.length()) return '\0';
        else return code.charAt(pos);
    }

    public static boolean isAlpha(char c) {
        return Pattern.matches( "[A-Za-zА-Яа-яёЁ_]", String.valueOf(c));
    }

    protected static boolean isAlphaNumeric(char c) {
        return isAlpha(c) || Character.isDigit(c);
    }

    //Разделить код на строки
    public String[] lines() { return code.split("\n"); }

    public LexerBase(String code) {
        this.code = code;
    }
}