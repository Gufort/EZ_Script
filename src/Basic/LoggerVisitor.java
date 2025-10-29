package Basic;

import java.util.logging.Logger;

public class LoggerVisitor implements ASTNodes.IVisitorP {
    private final Logger logger;
    private int indent = 0; // уровень вложенности

    public LoggerVisitor(Logger logger) {
        this.logger = logger;
    }

    private String getIndent() {
        return "  ".repeat(indent);
    }


    @Override public void visitNode(ASTNodes.Node node) throws Exception {
        logger.info(getIndent() + "Visiting node at " + node.position.toString());
    }

    @Override public void visitExprNode(ASTNodes.ExprNode node) throws Exception{
        logger.info(getIndent() + "Visiting ExprNode at " + node.position.toString());
    }

    @Override public void visitStatementNode(ASTNodes.StatementNode node) throws Exception {
        logger.info(getIndent() + "Visiting StatementNode at " + node.position.toString());
    }

    @Override public void visitBinOp(ASTNodes.BinOpNode node) throws Exception {
        logger.info(getIndent() + "Visiting BinOpNode");
        indent++;
        node.left.visitP(this);
        node.right.visitP(this);
        indent--;
    }

    @Override public void visitStatementList(ASTNodes.StatementListNode node) throws Exception {
        logger.info(getIndent() + "Visiting StatementList");
        indent++;
        for(var curr: node.statements)
            curr.visitP(this);
        indent--;
    }

    @Override public void visitExprList(ASTNodes.ExprListNode node) throws Exception {
        logger.info(getIndent() + "Visiting ExprList");
        indent++;
        for(var curr: node.lst)
            curr.visitP(this);
        indent--;
    }

    @Override public void visitInt(ASTNodes.IntNode node) throws Exception {
        logger.info(getIndent() + "Visiting IntNode at " + node.value + " at " + node.position.toString());
    }

    @Override public void visitDouble(ASTNodes.DoubleNode node) throws Exception {
        logger.info(getIndent() + "Visiting DoubleNode " + node.value + " at " + node.position.toString());
    }

    @Override public void visitBigInt(ASTNodes.BigIntNode node) throws Exception {
        logger.info(getIndent() + "Visiting BigIntNode " + node.value + " at " + node.position.toString());
    }

    @Override public void visitId(ASTNodes.IdNode node) throws Exception {
        logger.info(getIndent() + "Visiting IdNode " + node.name + " at " + node.position.toString());
    }

    @Override public void visitAssign(ASTNodes.AssignNode node) throws Exception {
        logger.info(getIndent() + "Visiting AssignNode");
        indent++;
        node.id.visitP(this);
        node.expr.visitP(this);
        indent--;
    }

    @Override public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception {
        logger.info(getIndent() + "Visiting AssignOperationNode " + node.op + "= ");
        indent++;
        node.id.visitP(this);
        node.expr.visitP(this);
        indent--;
    }

    @Override public void visitIf(ASTNodes.IfNode node) throws Exception {
        logger.info(getIndent() + "Visiting IfNode at " + node.position.toString());
        indent++;
        node.cond.visitP(this);
        node.then.visitP(this);
        if(node.elseif != null)
            node.elseif.visitP(this);
        indent--;
    }

    @Override public void visitWhile(ASTNodes.WhileNode node) throws Exception {
        logger.info(getIndent() + "Visiting WhileNode at " + node.position.toString());
        indent++;
        node.cond.visitP(this);
        node.stat.visitP(this);
        indent--;
    }

    @Override public void visitFor(ASTNodes.ForNode node) throws Exception {
        logger.info(getIndent() + "Visiting ForNode at " + node.position.toString());
        indent++;
        node.start.visitP(this);
        node.condition.visitP(this);
        node.increment.visitP(this);
        node.body.visitP(this);
        indent--;
    }

    @Override public void visitProcCall(ASTNodes.ProcCallNode node) throws Exception {
        logger.info(getIndent() + "Visiting ProcCallNode " + node.name.name + " at " + node.position.toString());
        indent++;
        node.pars.visitP(this);
        indent--;
    }

    @Override public void visitFuncCall(ASTNodes.FuncCallNode node) throws Exception {
        logger.info(getIndent() + "Visiting FuncCallNode " + node.name.name + " at " + node.position.toString());
        indent++;
        node.pars.visitP(this);
        indent--;
    }
}
