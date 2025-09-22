public class AutoVisitorUnit implements ASTNodes.IVisitorP {
    @Override public void visitNode(ASTNodes.Node node){}
    @Override public void visitExprNode(ASTNodes.ExprNode node){}
    @Override public void visitStatementNode(ASTNodes.StatementNode node){}
    @Override public void visitInt(ASTNodes.IntNode node){}
    @Override public void visitDouble(ASTNodes.DoubleNode node){}
    @Override public void visitId(ASTNodes.IdNode node){}
    @Override public void visitBinOp(ASTNodes.BinOpNode node){
        node.left.visitP(this);
        node.right.visitP(this);
    }
    @Override public void visitStatementList(ASTNodes.StatementListNode stl){
        for(var curr: stl.statements)
            curr.visitP(this);
    }
    @Override public void visitExprList(ASTNodes.ExprListNode el){
        for(var curr: el.lst)
            curr.visitP(this);
    }
    @Override public void visitAssign(ASTNodes.AssignNode node){
        node.expr.visitP(this);
        node.id.visitP(this);
    }
    @Override public void visitAssignPlus(ASTNodes.AssignPlusNode node){
        node.expr.visitP(this);
        node.id.visitP(this);
    }
    @Override public void visitIf(ASTNodes.IfNode node){
        node.cond.visitP(this);
        node.then.visitP(this);
        if(node.elseif != null)
            node.elseif.visitP(this);
    }
    @Override public void visitWhile(ASTNodes.WhileNode node){
        node.cond.visitP(this);
        node.stat.visitP(this);
    }
    @Override public void visitProcCall(ASTNodes.ProcCallNode node){
        node.pars.visitP(this);
    }
    @Override public void visitFuncCall(ASTNodes.FuncCallNode node){
        node.pars.visitP(this);
    }
}
