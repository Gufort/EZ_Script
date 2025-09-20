import java.util.regex.Pattern;

public class LexerBase {
    public String code;//Код программы, который анализируется в конструкторе
    public int line = 1;//Текущая строка
    public int column = 0;//Текущий столбец
    public int currentPosition = 0;//Текущая позиция
    public int start = 0;//Стартовая позиция токена
    public boolean atEoln = false;//Сервисное поле для метода NextChar, флаг конца строки

    public Position getCurrentPosition() { return new Position(line, column); }
    public boolean isAtEnd() {return currentPosition >= code.length(); }
    public char peekChar(){ return isAtEnd() ? '\0' : code.charAt(currentPosition); }
    public void startToken(){ start = currentPosition; }
    public boolean isWhitespace(char ch){  return ch == '\r' || ch == '\7' || ch == ' ' || ch == '\n' || ch == '\t'; }
    public String getTokenText(){ return code.substring(start, currentPosition); }
    public void skipWhitespace(){
        while(isWhitespace(peekChar()) && !isAtEnd())
            nextChar();
    }

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

    public String readNumber(){
        startToken();
        while(Character.isDigit(peekChar()))
            nextChar();
        if(peekChar() == '.' && Character.isDigit(peekNextChar())){
            nextChar();
            while(Character.isDigit(peekChar()))
                nextChar();
        }
        return getTokenText();
    }

    public String readIdentifier(){
        startToken();
        if(isAlpha(peekChar())){
            nextChar();
            while(isAlphaNumeric(peekChar()))
                nextChar();
        }
        return getTokenText();
    }

    public String readString() throws Exception{
        var quoteChar = '"';
        startToken();
        if(isMatch(quoteChar)){
            while(peekChar() != quoteChar && !isAtEnd())
                nextChar();
            if(!isMatch(quoteChar))
                CompilerException.lexerError("Незавершенная строковая константа", getCurrentPosition());
        }
        return getTokenText();
    }

    public LexerBase(String code) {
        this.code = code;
    }
}