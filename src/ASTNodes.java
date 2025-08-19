import java.util.ArrayList;

public abstract class ASTNodes {
    interface IVisitor<T>{
        T visitNode(Node bin);
        T visitExprNode(ExprNode bin);
        T visitStatementNode(StatementNode bin);
        T visitBinOp(BinOpNode bin);
        T visitStatementList(StatementListNode stl);
        T visitExprList(ExprListNode exlist);
        T visitInt(IntNode n);
        T visitDouble(DoubleNode d);
        T visitId(IdNode id);
        T visitAssign(AssignNode ass);
        T visitIf(IfNode ifn);
        T visitWhile(WhileNode whl);
        T visitProcCall(ProcCallNode p);
        T visitFuncCall(FuncCallNode f);
    }

    interface IVisitorP{
        void visitNode(Node bin);
        void visitExprNode(ExprNode bin);
        void visitStatementNode(StatementNode bin);
        void visitBinOp(BinOpNode bin);
        void visitStatementList(StatementListNode stl);
        void visitExprList(ExprListNode exlist);
        void visitInt(IntNode n);
        void visitDouble(DoubleNode d);
        void visitId(IdNode id);
        void visitAssign(AssignNode ass);
        void visitIf(IfNode ifn);
        void visitWhile(WhileNode whn);
        void visitProcCall(ProcCallNode p);
        void visitFuncCall(FuncCallNode f);
    }
    
    public static abstract class Node{
        public abstract <T> T visit(IVisitor<T> v);
        public abstract void visit(IVisitorP v);
    }

    public static class ExprNode extends Node{
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitExprNode(this); };
        @Override
        public void visit(IVisitorP v){ v.visitExprNode(this); }
    }

    public static class StatementNode extends Node{
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitStatementNode(this); };
        @Override
        public void visit(IVisitorP v){ v.visitStatementNode(this); }
    }

    public static class BinOpNode extends ExprNode{
        public ExprNode left;
        public ExprNode right;
        public String op;

        public BinOpNode(ExprNode left, ExprNode right, String op){
            this.left = left;
            this.right = right;
            this.op = op;
        }

        @Override
        public <T> T visit(IVisitor<T> v){return v.visitBinOp(this); };
        @Override
        public void visit(IVisitorP v){ v.visitBinOp(this); }
    }

    public static class StatementListNode extends StatementNode{
        public ArrayList<StatementNode> statements = new ArrayList<StatementNode>();
        public void add(StatementNode statement){ statements.add(statement); }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitStatementList(this); };
        @Override
        public void visit(IVisitorP v){ v.visitStatementList(this); }
    }

    public static class ExprListNode extends Node{
        public ArrayList<ExprNode> lst = new ArrayList<ExprNode>();
        public void add(ExprNode expr){ lst.add(expr); }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitExprList(this); };
        @Override
        public void visit(IVisitorP v){ v.visitExprList(this); }
    }

    public static class IntNode extends ExprNode{
        public int value;
        public IntNode(int value) { this.value = value; }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitInt(this); }
        @Override
        public void visit(IVisitorP v){ v.visitInt(this); }
    }

    public static class DoubleNode extends ExprNode{
        public double value;
        public DoubleNode(double value) { this.value = value; }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitDouble(this); }
        @Override
        public void visit(IVisitorP v){ v.visitDouble(this); }
    }

    public static class IdNode extends ExprNode{
        public String name;
        public IdNode(String name) { this.name = name; }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitId(this); }
        @Override
        public void visit(IVisitorP v){ v.visitId(this); }
    }

    public static class AssignNode extends StatementNode{
        public IdNode id;
        public ExprNode expr;
        public AssignNode(IdNode id, ExprNode expr) {
            this.id = id;
            this.expr = expr;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitAssign(this); }
        @Override
        public void visit(IVisitorP v){ v.visitAssign(this); }
    }

    public static class IfNode extends StatementNode{
        public ExprNode cond;
        public ExprNode then;
        public ExprNode elseif;
        public IfNode(ExprNode cond, ExprNode then, ExprNode elseif) {
            this.cond = cond;
            this.then = then;
            this.elseif = elseif;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitIf(this); }
        @Override
        public void visit(IVisitorP v){ v.visitIf(this); }
    }

    public static class WhileNode extends StatementNode{
        public ExprNode cond;
        public ExprNode stat;
        public WhileNode(ExprNode cond, ExprNode stat) {
            this.cond = cond;
            this.stat = stat;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitWhile(this); }
        @Override
        public void visit(IVisitorP v){ v.visitWhile(this); }
    }

    public static class ProcCallNode extends StatementNode{
        public IdNode name;
        public ExprListNode pars;
        public ProcCallNode(IdNode name, ExprListNode pars) {
            this.name = name;
            this.pars = pars;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitProcCall(this); }
        @Override
        public void visit(IVisitorP v){ v.visitProcCall(this); }
    }

    public static class FuncCallNode extends ExprNode{
        public IdNode name;
        public ExprListNode pars;
        public FuncCallNode(IdNode name, ExprListNode pars) {
            this.name = name;
            this.pars = pars;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitFuncCall(this); }
        @Override
        public void visit(IVisitorP v){ v.visitFuncCall(this); }
    }
}
