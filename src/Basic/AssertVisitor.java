package Basic;

import ExceptionLogic.CompilerException;

public class AssertVisitor implements ASTNodes.IVisitorP {
    private final String context;

    public AssertVisitor(String context) {
        this.context = context;
    }

    public AssertVisitor() {
        this("AST Validation");
    }

    @Override
    public void visitNode(ASTNodes.Node node) throws Exception {
        assert node != null : "Node cannot be null";
    }

    @Override
    public void visitExprNode(ASTNodes.ExprNode node) throws Exception {
        assert node != null : "ExprNode cannot be null";
        assert node.position != null : "ExprNode position cannot be null";
        assert node.position.line > 0 : "Invalid line number: " + node.position.line;
        assert node.position.column >= 0 : "Invalid column number: " + node.position.column;
    }

    @Override
    public void visitStatementNode(ASTNodes.StatementNode node) throws Exception {
        assert node != null : "StatementNode cannot be null";
        assert node.position != null : "StatementNode position cannot be null";
        assert node.position.line > 0 : "Invalid line number: " + node.position.line;
        assert node.position.column >= 0 : "Invalid column number: " + node.position.column;
    }

    @Override
    public void visitBinOp(ASTNodes.BinOpNode node) throws Exception {
        assert node != null : "BinOpNode cannot be null";
        assert node.left != null : "BinOpNode left operand cannot be null";
        assert node.right != null : "BinOpNode right operand cannot be null";
        assert node.op != null : "BinOpNode operator cannot be null";

        node.left.visitP(this);
        node.right.visitP(this);
    }

    @Override
    public void visitStatementList(ASTNodes.StatementListNode node) throws Exception {
        assert node != null : "StatementListNode cannot be null";
        assert node.statements != null : "StatementList statements cannot be null";

        for (var stmt : node.statements) {
            assert stmt != null : "Statement in StatementList cannot be null";
            stmt.visitP(this);
        }
    }

    @Override
    public void visitExprList(ASTNodes.ExprListNode node) throws Exception {
        assert node != null : "ExprListNode cannot be null";
        assert node.lst != null : "ExprList list cannot be null";

        for (var expr : node.lst) {
            assert expr != null : "Expression in ExprList cannot be null";
            expr.visitP(this);
        }
    }

    @Override
    public void visitInt(ASTNodes.IntNode node) throws Exception {
        assert node != null : "IntNode cannot be null";
        // Дополнительные проверки для IntNode если нужно
    }

    @Override
    public void visitDouble(ASTNodes.DoubleNode node) throws Exception {
        assert node != null : "DoubleNode cannot be null";
        // Дополнительные проверки для DoubleNode если нужно
    }

    @Override
    public void visitId(ASTNodes.IdNode node) throws Exception {
        assert node != null : "IdNode cannot be null";
        assert node.name != null : "IdNode name cannot be null";
        assert !node.name.trim().isEmpty() : "IdNode name cannot be empty";
    }

    @Override
    public void visitAssign(ASTNodes.AssignNode node) throws Exception {
        assert node != null : "AssignNode cannot be null";
        assert node.id != null : "AssignNode id cannot be null";
        assert node.expr != null : "AssignNode expr cannot be null";

        node.id.visitP(this);
        node.expr.visitP(this);
    }

    @Override
    public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception {
        assert node != null : "AssignOperationNode cannot be null";
        assert node.id != null : "AssignOperationNode id cannot be null";
        assert node.expr != null : "AssignOperationNode expr cannot be null";

        node.id.visitP(this);
        node.expr.visitP(this);
    }

    @Override
    public void visitIf(ASTNodes.IfNode node) throws Exception {
        assert node != null : "IfNode cannot be null";
        assert node.cond != null : "IfNode condition cannot be null";
        assert node.then != null : "IfNode then branch cannot be null";

        node.cond.visitP(this);
        node.then.visitP(this);
        if (node.elseif != null) {
            node.elseif.visitP(this);
        }
    }

    @Override
    public void visitFor(ASTNodes.ForNode node) throws Exception {
        assert node != null : "ForNode cannot be null";
        assert node.start != null : "ForNode start cannot be null";
        assert node.condition != null : "ForNode condition cannot be null";
        assert node.increment != null : "ForNode increment cannot be null";
        assert node.body != null : "ForNode body cannot be null";

        node.start.visitP(this);
        node.condition.visitP(this);
        node.increment.visitP(this);
        node.body.visitP(this);
    }

    @Override
    public void visitWhile(ASTNodes.WhileNode node) throws Exception {
        assert node != null : "WhileNode cannot be null";
        assert node.cond != null : "WhileNode condition cannot be null";
        assert node.stat != null : "WhileNode statement cannot be null";

        node.cond.visitP(this);
        node.stat.visitP(this);
    }

    @Override
    public void visitProcCall(ASTNodes.ProcCallNode node) throws Exception {
        assert node != null : "ProcCallNode cannot be null";
        assert node.name != null : "ProcCallNode name cannot be null";
        assert node.pars != null : "ProcCallNode parameters cannot be null";

        node.name.visitP(this);
        node.pars.visitP(this);
    }

    @Override
    public void visitFuncCall(ASTNodes.FuncCallNode node) throws Exception {
        assert node != null : "FuncCallNode cannot be null";
        assert node.name != null : "FuncCallNode name cannot be null";
        assert node.pars != null : "FuncCallNode parameters cannot be null";

        node.name.visitP(this);
        node.pars.visitP(this);
    }
}