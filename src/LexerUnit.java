import javax.security.auth.login.LoginException;
import java.util.Dictionary;
import java.util.Hashtable;

public class LexerUnit {
    public enum TokenType {
        INT, DOUBLELITERAL, STRINGLITERAL, ID,
        PLUS, MINUS, MULTIPLE, DIVIDE, DOT,
        SEMICOLON, COMMA, LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, COLON, LEFT_BRACE, RIGHT_BRACE,
        ASSIGN, ASSIGNPLUS, ASSIGNMINUS, ASSIGNMULTIPLE, ASSIGNDIVIDE,
        EQUAL, LESS, GREATER, GREATEREQUAL, LESSEQUAL, NOTEQUAL,
        AND, OR, NOT,
        EOF,
        TRUE, FALSE, IF, ELSE, WHILE
    }

    public static  Dictionary<String, TokenType> KeyWords = new Hashtable<String, TokenType>();
    static {
        KeyWords.put("True", TokenType.TRUE);
        KeyWords.put("False", TokenType.FALSE);
        KeyWords.put("if", TokenType.IF);
        KeyWords.put("while", TokenType.WHILE);
        KeyWords.put("else", TokenType.ELSE);
    }

    public static class Token extends TokenBase {
        public TokenType type;
        public Token(TokenType type, Position position, Object value) {
            super(position, value);
            this.type = type;
        }
        public Token(TokenType type, Position position) {
            this(type, position, null);
        }
    }

    public static class Lexer extends LexerBase {
        private Token getIdentifier(Position startPosition) {
            while(isAlphaNumeric(peekChar()))
                nextChar();

            var value = code.substring(start, currentPosition);
            var type = TokenType.ID;

            if(KeyWords.get(value) != null)
                type = KeyWords.get(value);

            return new Token(type, startPosition, value);
        }

        private Token getString(Position startPosition) {
            while(peekChar() != '"' && !isAtEnd())
                nextChar();
            nextChar();
            var value = code.substring(start + 1, currentPosition - 1);
            return new Token(TokenType.STRINGLITERAL, startPosition, value);
        }

        private Token getNumber(Position startPosition) {
            while(Character.isDigit(peekChar()))
                nextChar();
            if(peekChar() == '.' && Character.isDigit(peekNextChar())){
                nextChar();
                while (Character.isDigit(peekChar()))
                    nextChar();
                var value = code.substring(start, currentPosition);
                double number = Double.parseDouble(value);
                return new Token(TokenType.DOUBLELITERAL, startPosition, number);
            }
            var value = code.substring(start, currentPosition);
            return new Token(TokenType.INT, startPosition, value);
        }

        public Token nextToken() throws Exception{
            var ch = nextChar();
            while(ch == (char)13 || ch == (char)10 || ch == (char)7 || ch == (char)' ')
                ch = nextChar();

            var position = getCurrentPosition();
            start = currentPosition - 1;
            switch (ch) {
                case (char)0:
                    return new Token(TokenType.EOF, position, "EOF");
                case (char)',':
                    return new Token(TokenType.COMMA, position, code.substring(start, currentPosition));
                case ')':
                    return new Token(TokenType.RIGHT_PAREN, position, code.substring(start, currentPosition));
                case '(':
                    return new Token(TokenType.LEFT_PAREN, position, code.substring(start, currentPosition));
                case '}':
                    return new Token(TokenType.RIGHT_BRACE, position, code.substring(start, currentPosition));
                case '{':
                    return new Token(TokenType.LEFT_BRACE, position, code.substring(start, currentPosition));
                case '[':
                    return new Token(TokenType.LEFT_BRACKET, position, code.substring(start, currentPosition));
                case ']':
                    return new Token(TokenType.RIGHT_BRACKET, position, code.substring(start, currentPosition));
                case '+':
                    return new Token(isMatch('=') ? TokenType.ASSIGNPLUS: TokenType.PLUS, position, code.substring(start, currentPosition));
                case '-':
                    return new Token(isMatch('=') ? TokenType.ASSIGNMINUS: TokenType.MINUS, position, code.substring(start, currentPosition));
                case '*':
                    return new Token(isMatch('=') ? TokenType.ASSIGNMULTIPLE: TokenType.MULTIPLE, position, code.substring(start, currentPosition));
                case '/':
                    if(isMatch('/'))
                        while (peekChar() != '\n' && !isAtEnd())
                            nextChar();
                    return new Token(isMatch('=') ? TokenType.ASSIGNDIVIDE: TokenType.DIVIDE, position, code.substring(start, currentPosition));
                case ':':
                    return new Token(TokenType.COLON, position, code.substring(start, currentPosition));
                case ';':
                    return new Token(TokenType.SEMICOLON, position, code.substring(start, currentPosition));
                case '=':
                    return new Token(isMatch('=') ? TokenType.EQUAL: TokenType.ASSIGN, position, code.substring(start, currentPosition));
                case '!':
                    return new Token(isMatch('=') ? TokenType.NOTEQUAL: TokenType.NOT, position, code.substring(start, currentPosition));
                case '>':
                    return new Token(isMatch('=') ? TokenType.GREATEREQUAL: TokenType.GREATER, position, code.substring(start, currentPosition));
                case '<':
                    return new Token(isMatch('=') ? TokenType.LESSEQUAL: TokenType.LESS, position, code.substring(start, currentPosition));
                case '&':
                    if(isMatch('&'))
                        return new Token(TokenType.AND, position, code.substring(start, currentPosition));
                    else throw new CompilerException.LexerException("Неверный символ " + peekChar() + " после &", getCurrentPosition());
                case '|':
                    if(isMatch('|'))
                        return new Token(TokenType.OR, position, code.substring(start, currentPosition));
                    else throw new CompilerException.LexerException("Неверный символ " + peekChar() + " после |", getCurrentPosition());
                case '"':
                    return getString(position);
                default:
                    if(Character.isDigit(ch))
                        return getNumber(position);
                    else if(isAlpha(ch))
                        return getIdentifier(position);
                    else throw new CompilerException.LexerException("Неизвестный символ" + ch +
                                " в позиции [" + position.line + position.column + "]", position);
            }
        }

        public Lexer(String code){ super(code); }
    }
}
