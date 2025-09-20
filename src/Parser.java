public class Parser extends ParserBase {
    public Parser(LexerUnit.Lexer lexer) throws Exception {
        super(lexer);
    }

    /// Program := StatementList
    public ASTNodes.StatementNode mainProgram() throws Exception {
        current = 0;
        var res = statementList();
        requires(LexerUnit.TokenType.EOF);
        return res;
    }

    /// StatementList := Statement (';' Statement)*
    public ASTNodes.StatementNode statementList() throws Exception {
        var res = new ASTNodes.StatementListNode();
        res.add(statement());
        while (isMatch(LexerUnit.TokenType.SEMICOLON)) {
            res.add(statement());
        }
        return res;
    }

    public ASTNodes.StatementNode ifStatement() throws Exception {
        var pos = CurrentToken().position;
        requires(LexerUnit.TokenType.IF);
        var cond = expr();
        requires(LexerUnit.TokenType.THEN);
        var thenStatement = statement();
        var elseStatement = isMatch(LexerUnit.TokenType.ELSE) ? statement() : null;
        return new ASTNodes.IfNode(cond, thenStatement, elseStatement, pos);
    }

    public ASTNodes.StatementNode whileStatement() throws Exception {
        var pos = CurrentToken().position;
        requires(LexerUnit.TokenType.WHILE);
        var cond = expr();
        requires(LexerUnit.TokenType.DO);
        var statement = statement();
        return new ASTNodes.WhileNode(cond, statement, pos);
    }

    public ASTNodes.StatementNode blockStatement() throws Exception {
        var pos = CurrentToken().position;
        requires(LexerUnit.TokenType.LEFT_BRACE);
        var stl = statementList();
        requires(LexerUnit.TokenType.RIGHT_BRACE);
        stl.position = pos;
        return stl;
    }

    /// id = expr
    /// id += expr
    /// id(exprlist)
    /// if expr then stat [else stat]
    /// while expr do stat
    /// { statlist }
    public ASTNodes.StatementNode statement() throws Exception {
        var pos = CurrentToken().position;
        if (at(LexerUnit.TokenType.IF))
            return ifStatement();
        else if (at(LexerUnit.TokenType.WHILE))
            return whileStatement();
        else if (at(LexerUnit.TokenType.LEFT_BRACE))
            return blockStatement();
        else if (at(LexerUnit.TokenType.ID)) {
            var id = ident();
            if (at(LexerUnit.TokenType.ASSIGNPLUS, LexerUnit.TokenType.ASSIGN))
                return parseAssignment(id, pos);
            else if (at(LexerUnit.TokenType.LEFT_PAREN))
                return parseProcedureCall(id, pos);
            else
                ExpectedError(LexerUnit.TokenType.ASSIGN, LexerUnit.TokenType.LEFT_PAREN);
        } else
            ExpectedError(LexerUnit.TokenType.ID, LexerUnit.TokenType.IF, LexerUnit.TokenType.WHILE, LexerUnit.TokenType.LEFT_BRACE);
        return null;
    }

    public ASTNodes.ExprNode expr() throws Exception {
        var expr = comp();
        while (at(LexerUnit.TokenType.LESS, LexerUnit.TokenType.GREATER,
                LexerUnit.TokenType.GREATEREQUAL, LexerUnit.TokenType.LESSEQUAL,
                LexerUnit.TokenType.EQUAL, LexerUnit.TokenType.NOTEQUAL)) {
            var op = nextLexem();
            var right = comp();
            expr = createBinaryOperation(expr, right, op);
        }
        return expr;
    }

    public ASTNodes.IdNode ident() throws Exception {
        var id = requires(LexerUnit.TokenType.ID);
        return new ASTNodes.IdNode(id.value.toString(), id.position);
    }

    public ASTNodes.ExprListNode exprList() throws Exception {
        var exprList = new ASTNodes.ExprListNode();
        exprList.add(expr());
        while (isMatch(LexerUnit.TokenType.COMMA)) {
            exprList.add(expr());
        }
        return exprList;
    }

    public ASTNodes.ExprNode comp() throws Exception {
        if (isAtEnd()) return null;
        var expr = term();
        while (at(LexerUnit.TokenType.PLUS, LexerUnit.TokenType.MINUS, LexerUnit.TokenType.OR)) {
            var op = nextLexem();
            var right = term();
            expr = createBinaryOperation(expr, right, op);
        }
        return expr;
    }

    public ASTNodes.ExprNode term() throws Exception {
        var expr = factor();
        while (at(LexerUnit.TokenType.MULTIPLE, LexerUnit.TokenType.DIVIDE, LexerUnit.TokenType.AND)) {
            var op = nextLexem();
            var right = factor();
            expr = createBinaryOperation(expr, right, op);
        }
        return expr;
    }

    public ASTNodes.ExprNode factor() throws Exception {
        var position = currentToken.position;

        if (currentToken == null || isAtEnd()) {
            CompilerException.syntaxError("Unexpected end of file", position);
            return null;
        }

        if (at(LexerUnit.TokenType.INT))
            return new ASTNodes.IntNode(Integer.parseInt(nextLexem().value.toString()), position);

        else if (at(LexerUnit.TokenType.DOUBLELITERAL))
            return new ASTNodes.DoubleNode(Double.parseDouble(nextLexem().value.toString()), position);

        else if (at(LexerUnit.TokenType.LEFT_PAREN)) {
            nextLexem();
            var res = expr();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            return res;
        }

        else if (at(LexerUnit.TokenType.ID)) {
            var id = ident();
            if (isMatch(LexerUnit.TokenType.LEFT_PAREN)) {
                var exprlst = exprList();
                var res = new ASTNodes.FuncCallNode(id, exprlst, position);
                requires(LexerUnit.TokenType.RIGHT_PAREN);
                return res;
            }
            return id;
        }

        else
            CompilerException.syntaxError("Exc" + peekToken().type.toString() + " найдено.", peekToken().position);
        return null;
    }

    private ASTNodes.StatementNode parseAssignment(ASTNodes.IdNode id, Position pos) throws Exception {
        if (isMatch(LexerUnit.TokenType.ASSIGN)) {
            var expr = expr();
            return new ASTNodes.AssignNode(id, expr, pos);
        } else if (isMatch(LexerUnit.TokenType.ASSIGNPLUS)) {
            var expr = expr();
            return new ASTNodes.AssignPlusNode(id, expr, pos);
        } else
            ExpectedError(LexerUnit.TokenType.ASSIGN, LexerUnit.TokenType.ASSIGNPLUS);
        return null;
    }

    private ASTNodes.StatementNode parseProcedureCall(ASTNodes.IdNode id, Position pos) throws Exception {
        requires(LexerUnit.TokenType.LEFT_PAREN);
        var expr = exprList();
        requires(LexerUnit.TokenType.RIGHT_PAREN);
        return new ASTNodes.ProcCallNode(id, expr, pos);
    }

    private ASTNodes.BinOpNode createBinaryOperation(ASTNodes.ExprNode left, ASTNodes.ExprNode right, TokenT<LexerUnit.TokenType> op) throws Exception {
        return new ASTNodes.BinOpNode(left, right, op.value.toString(), left.position);
    }
}
/*Program := StatementList
StatementList := Statement (';' Statement)*
Statement := Assign | ProcCall | IfStatement | WhileStatement
  | BlockStatement
Assign := Id ('=' | '+=' | '-=' | '*=' | '/=') Expr
ProcCall := Id '(' ExprList ')
FuncCall := Id '(' ExprList ')
WhileStatement := while Expr do Statement
IfStatement := if Expr then Statement [else Statement]
BlockStatement := '{' StatementList '}'
Expr := Comp (CompOp Comp)*
CompOp := '<' | '>' | '<=' | '>=' | '==' | '!='
Comp := Term (AddOp Term)*
AddOp := '+' | '-' | '||'
Term := Factor (MultOp Factor)*
MultOp := '*' | '/' | '&&'
Factor := IntNum | DoubleNum | FuncCall | '(' Expr ')
ExprList := Expr (',' Expr)* */
