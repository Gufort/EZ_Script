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
        TRUE, FALSE, IF, ELSE, WHILE, DO, THEN
    }

    public static  Dictionary<String, TokenType> KeyWords = new Hashtable<String, TokenType>();
    static {
        KeyWords.put("True", TokenType.TRUE);
        KeyWords.put("False", TokenType.FALSE);
        KeyWords.put("if", TokenType.IF);
        KeyWords.put("while", TokenType.WHILE);
        KeyWords.put("else", TokenType.ELSE);
        KeyWords.put("do", TokenType.DO);
        KeyWords.put("THEN", TokenType.THEN);
    }


    public static class Token extends TokenT<TokenType> {
        public Token(TokenType type, Position position, Object value) {
            super(type, position, value);
        }
    }

    public static class Lexer extends LexerBase  implements ILexer<TokenType> {
        @Override public boolean TokenTypeisEof(TokenType tt){ return tt.equals(TokenType.EOF); }
        @Override public String[] getLines(){ return code.split("\n"); }
        @Override public Token nextToken() throws Exception{
            skipWhitespace();;
            var pos = getCurrentPosition();
            if(isAtEnd())
                return new Token(TokenType.EOF, pos, "Eof");

            var ch = peekChar();

            if(Character.isDigit(ch))
                return scanNumber(pos);
            else if(isAlpha(ch))
                return scanIdentifier(pos);
            else if(ch == '"')
                return scanString(pos);
            else return scanSymbol(pos);
        }
        public Lexer(String code){ super(code); }

        private Token scanIdentifier(Position startPos){
            var id = readIdentifier();
            var tokenType = KeyWords.get(id);
            if(tokenType == null){
                tokenType = TokenType.ID;
            }
            return new Token(tokenType, startPos, id);
        }

        private Token scanString(Position startPos) throws Exception{
            var strWithQuotes = readString();
            var strValue = strWithQuotes.substring(1, strWithQuotes.length()-1);
            return new Token(TokenType.STRINGLITERAL, startPos, strValue);
        }

        private Token scanNumber(Position startPos) throws Exception{
            var number = readNumber();
            if(number.contains("."))
                return new Token(TokenType.DOUBLELITERAL, startPos, Double.parseDouble(number));
            return new Token(TokenType.INT, startPos, Integer.parseInt(number));
        }

        private Token scanSymbol(Position startPos) throws Exception{
            startToken();
            var ch = nextChar();

            switch(ch){
                case ',': return new Token(TokenType.COMMA, startPos, ',');
                case ';': return new Token(TokenType.SEMICOLON, startPos, ';');
                case '(': return new Token(TokenType.LEFT_PAREN, startPos, '(');
                case ')': return new Token(TokenType.RIGHT_PAREN, startPos, ')');
                case '{': return new Token(TokenType.LEFT_BRACE, startPos, '{');
                case '}': return new Token(TokenType.RIGHT_BRACE, startPos, '}');
                case '[': return new Token(TokenType.LEFT_BRACKET, startPos, '[');
                case ']': return new Token(TokenType.RIGHT_BRACKET, startPos, ']');
                case '.': return new Token(TokenType.DOT, startPos, '.');

                case '+': return new Token(isMatch('=') ? TokenType.ASSIGNPLUS: TokenType.PLUS, startPos, getTokenText());
                case '-': return new Token(isMatch('=') ? TokenType.ASSIGNMINUS: TokenType.MINUS, startPos, getTokenText());
                case '*': return new Token(isMatch('=') ? TokenType.ASSIGNMULTIPLE: TokenType.MULTIPLE, startPos, getTokenText());
                case '/':
                    if(isMatch('/')) {
                        while (peekChar() != '\n' && !isAtEnd())
                            nextChar();
                        return nextToken();
                    }
                    else return new Token(isMatch('=') ? TokenType.ASSIGNDIVIDE: TokenType.DIVIDE, startPos, getTokenText());

                case '!': return new Token(isMatch('=') ? TokenType.NOTEQUAL: TokenType.NOT, startPos, getTokenText());
                case '=': return new Token(isMatch('=') ? TokenType.EQUAL: TokenType.ASSIGN, startPos, getTokenText());
                case '>': return new Token(isMatch('>') ? TokenType.GREATEREQUAL: TokenType.GREATER, startPos, getTokenText());
                case '<': return new Token(isMatch('<') ? TokenType.LESSEQUAL: TokenType.LESS, startPos, getTokenText());
                case '&':
                    if(isMatch('&'))
                        return new Token(TokenType.AND, startPos, "&&");
                    else CompilerException.lexerError("Ожидается &", getCurrentPosition());
                case '|':
                    if(isMatch('|'))
                        return new Token(TokenType.OR, startPos, "||");
                    else CompilerException.lexerError("Ожидается ||", getCurrentPosition());

                default: CompilerException.lexerError("Неизвестный символ " + ch, startPos);
            }
            return null;
        }
    }
}
