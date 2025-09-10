public class ASTNodesMethods {
    public ASTNodes.BinOpNode Bin(ASTNodes.ExprNode left, ASTNodes.ExprNode right, String operator) {
        return new ASTNodes.BinOpNode(left, right, operator);
    }

    public ASTNodes.AssignNode Assign(ASTNodes.IdNode id, ASTNodes.ExprNode expr){
        return new ASTNodes.AssignNode(id, expr);
    }

    public ASTNodes.IdNode Id(String name){
        return new ASTNodes.IdNode(name);
    }

    public ASTNodes.DoubleNode Num(double value){
        return new ASTNodes.DoubleNode(value);
    }

    public ASTNodes.IfNode Iff(ASTNodes.ExprNode expr, ASTNodes.StatementNode iff, ASTNodes.StatementNode elseif){
        return new ASTNodes.IfNode(expr, iff, elseif);
    }

    public ASTNodes.WhileNode While(ASTNodes.ExprNode expr, ASTNodes.StatementNode state){
        return new ASTNodes.WhileNode(expr, state);
    }

    public ASTNodes.StatementListNode Stl(ASTNodes.StatementNode... statements){
        var res = new ASTNodes.StatementListNode();
        for(ASTNodes.StatementNode s : statements){
            res.add(s);
        }
        return res;
    }

   public ASTNodes.ExprListNode Expr(ASTNodes.ExprNode... expr){
        var res = new ASTNodes.ExprListNode();
        for(ASTNodes.ExprNode e : expr){
            res.add(e);
        }
        return res;
   }

   public ASTNodes.ProcCallNode ProcCall(ASTNodes.IdNode id, ASTNodes.ExprListNode exprList){
        return new ASTNodes.ProcCallNode(id, exprList);
   }
}
