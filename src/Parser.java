public class Parser extends ParserBase{
    public Parser(LexerUnit.Lexer lexer) throws Exception{
        super(lexer);
    }

    /// Program := StatementList
    public ASTNodes.StatementNode mainProgram() throws Exception{
        var res = statementList();
        requires(LexerUnit.TokenType.EOF);
        return res;
    }

    /// StatementList := Statement (';' Statement)*
    public ASTNodes.StatementNode statementList() throws Exception{
        var res = new ASTNodes.StatementListNode();
        res.add(statement());
        requires(LexerUnit.TokenType.EOF);
        while(isMatch(LexerUnit.TokenType.SEMICOLON)){
            res.add(statement());
        }
        return res;
    }

    /// id = expr
    /// id += expr
    /// id(exprlist)
    /// if expr then stat [else stat]
    /// while expr do stat
    /// { statlist }
    public ASTNodes.StatementNode statement() throws Exception{
        var position = currentToken.position;
        check(LexerUnit.TokenType.ID, LexerUnit.TokenType.IF, LexerUnit.TokenType.WHILE, LexerUnit.TokenType.LEFT_BRACE);
        if(isMatch(LexerUnit.TokenType.IF)){
            requires(LexerUnit.TokenType.LEFT_PAREN);
            var condition = expr();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            var thenstat = statement();
            var elsestat = (isMatch(LexerUnit.TokenType.ELSE)) ? statement() : null;
            return new ASTNodes.IfNode(condition, thenstat, elsestat, position);
        }

        else if(isMatch(LexerUnit.TokenType.WHILE)){
            requires(LexerUnit.TokenType.LEFT_PAREN);
            var condition = expr();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            var statement = statement();
            return new ASTNodes.WhileNode(condition, statement, position);
        }

        else if(isMatch(LexerUnit.TokenType.LEFT_BRACE)){
            var statement = statementList();
            requires(LexerUnit.TokenType.RIGHT_BRACE);
            statement.setPos(position);
            return statement;
        }

        else{
            var id = ident();
            if(isMatch(LexerUnit.TokenType.ASSIGN)){
                var expr = expr();
                return new ASTNodes.AssignNode(id, expr, position);
            }

            if(isMatch(LexerUnit.TokenType.ASSIGNPLUS)){
                var expr = expr();
                return new ASTNodes.AssignPlusNode(id, expr, position);
            }

            else if(isMatch(LexerUnit.TokenType.LEFT_PAREN)){
                var exprlist = exprList();
                requires(LexerUnit.TokenType.RIGHT_PAREN);
                return new ASTNodes.ProcCallNode(id, exprlist, position);
            }

            else ExpectedError(LexerUnit.TokenType.ASSIGNPLUS, LexerUnit.TokenType.ASSIGN, LexerUnit.TokenType.LEFT_PAREN);
        }
        return null;
    }

    public ASTNodes.ExprNode expr() throws Exception{
        var expr = comp();
        while(at(LexerUnit.TokenType.LESS, LexerUnit.TokenType.GREATER,
                LexerUnit.TokenType.GREATEREQUAL, LexerUnit.TokenType.LESSEQUAL,
                LexerUnit.TokenType.EQUAL, LexerUnit.TokenType.NOTEQUAL)){
            var op = advance();
            var right = comp();
            expr = new ASTNodes.BinOpNode(expr, right, op.value.toString(), expr.position);
        }
        return expr;
    }

    public ASTNodes.IdNode ident() throws Exception{
        var id = requires(LexerUnit.TokenType.ID);
        return new ASTNodes.IdNode(id.value.toString(), id.position);
    }

    public ASTNodes.ExprListNode exprList() throws Exception{
        var exprList = new ASTNodes.ExprListNode();
        exprList.add(expr());
        while(isMatch(LexerUnit.TokenType.COMMA)){
            exprList.add(expr());
        }
        return exprList;
    }

    public ASTNodes.ExprNode comp() throws Exception{
        var expr = term();
        while(at(LexerUnit.TokenType.PLUS, LexerUnit.TokenType.MINUS, LexerUnit.TokenType.OR)){
            var op = advance();
            var right = term();
            expr = new ASTNodes.BinOpNode(expr, right, op.value.toString(), expr.position);
        }
        return expr;
    }

    public ASTNodes.ExprNode term() throws Exception{
        var expr = factor();
        while(at(LexerUnit.TokenType.MULTIPLE, LexerUnit.TokenType.DIVIDE, LexerUnit.TokenType.AND)){
            var op = advance();
            var right = factor();
            expr = new ASTNodes.BinOpNode(expr, right, op.value.toString(), expr.position);
        }
        return expr;
    }

    public ASTNodes.ExprNode factor() throws Exception{
        var position = currentToken.position;
        if(at(LexerUnit.TokenType.INT))
            return new ASTNodes.IntNode((int)advance().value, position);

        else if(at(LexerUnit.TokenType.DOUBLELITERAL))
            return new ASTNodes.DoubleNode((double)advance().value, position);

        else if(at(LexerUnit.TokenType.LEFT_PAREN)){
            var res = expr();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            return res;
        }

        else if(at(LexerUnit.TokenType.ID)){
            var id = ident();
            if(at(LexerUnit.TokenType.LEFT_PAREN)){
                var exprlst = exprList();
                var res = new ASTNodes.FuncCallNode(id, exprlst, position);
                requires(LexerUnit.TokenType.RIGHT_PAREN);
                return res;
            }
            return id;
        }

        else CompilerException.syntaxError("Exception INT or '(' or id but " + peekToken().type.toString() + " found.", peekToken().position);
        return null;
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
