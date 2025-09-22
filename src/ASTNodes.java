import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public abstract class ASTNodes {

    public static Dictionary<String, Double> varValues = new Hashtable<String, Double>();

    public enum OperationType{opPlus, opMinus, opMultiply, opDivide,
    opEqual, opLess, opLessEqual, opGreater, opGreaterEqual, opNotEqual,
    opAnd, opOr, opNot, opBad};

    public Dictionary<OperationType, String> operationToStr = new Hashtable<OperationType, String>();

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
        T visitAssignPlus(AssignPlusNode ass);
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
        void visitAssignPlus(AssignPlusNode ass);
        void visitIf(IfNode ifn);
        void visitWhile(WhileNode whn);
        void visitProcCall(ProcCallNode p);
        void visitFuncCall(FuncCallNode f);
    }
    
    public static abstract class Node{
        public abstract <T> T visit(IVisitor<T> v);
        public abstract void visitP(IVisitorP v);
    }

    public static abstract class ExprNode extends Node{
        public Position position;
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitExprNode(this); };
        @Override
        public void visitP(IVisitorP v){ v.visitExprNode(this); }
    }

    public static abstract class StatementNode extends Node{
        public Position position;
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitStatementNode(this); };
        @Override
        public void visitP(IVisitorP v){ v.visitStatementNode(this); }
        public void setPos(Position pos){
            position = pos;
        }
    }

    public static class BinOpNode extends ExprNode{
        public ExprNode left;
        public ExprNode right;
        public OperationType op;

        public BinOpNode(ExprNode left, ExprNode right, OperationType op, Position position) {
            this.left = left;
            this.right = right;
            this.op = op;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v){return v.visitBinOp(this); };
        @Override
        public void visitP(IVisitorP v){ v.visitBinOp(this); }
        @Override
        public String toString() {
            return "(" + op + ",(" + left + "),(" + right + "))";
        }
    }

    public static class StatementListNode extends StatementNode{
        public ArrayList<StatementNode> statements = new ArrayList<StatementNode>();
        public void add(StatementNode statement){ statements.add(statement); }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitStatementList(this); };
        @Override
        public void visitP(IVisitorP v){ v.visitStatementList(this); }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < statements.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(statements.get(i).toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class ExprListNode extends Node{
        public ArrayList<ExprNode> lst = new ArrayList<ExprNode>();
        public void add(ExprNode expr){ lst.add(expr); }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitExprList(this); };
        @Override
        public void visitP(IVisitorP v){ v.visitExprList(this); }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < lst.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("(" + lst.get(i) + ")");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class IntNode extends ExprNode{
        public int value;
        public Position position;

        public IntNode(int value, Position position) {
            this.value = value;
            this.position = position;
        }

        public IntNode(int value) { this.value = value; }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitInt(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitInt(this); }

        @Override
        public String toString() { return String.valueOf(value); }
    }

    public static class DoubleNode extends ExprNode{
        public double value;
        public Position position;

        public DoubleNode(double value, Position position) {
            this.value = value;
            this.position = position;
        }

        public DoubleNode(double value) { this.value = value; }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitDouble(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitDouble(this); }

        @Override
        public String toString() { return String.valueOf(value); }
    }

    public static class IdNode extends ExprNode{
        public String name;
        public Position position;

        public IdNode(String name, Position position) {
            this.name = name;
            this.position = position;
        }

        public IdNode(String name) { this.name = name; }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitId(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitId(this); }

        @Override
        public String toString() { return name; }
    }

    public static class AssignNode extends StatementNode{
        public IdNode id;
        public ExprNode expr;

        public AssignNode(IdNode id, ExprNode expr, Position position) {
            this.id = id;
            this.expr = expr;
            this.position = position;
        }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitAssign(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitAssign(this); }

        @Override
        public String toString() {
            return "((" + expr + "),(" + id + "))";
        }
    }

    public static class AssignPlusNode extends StatementNode{
        public IdNode id;
        public ExprNode expr;

        public AssignPlusNode(IdNode id, ExprNode expr, Position position) {
            this.id = id;
            this.expr = expr;
            this.position = position;
        }


        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitAssignPlus(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitAssignPlus(this); }

        @Override
        public String toString() {
            return "((+=,(" + expr + "),(" + id + ")))";
        }
    }

    public static class IfNode extends StatementNode{
        public ExprNode cond;
        public StatementNode then;
        public StatementNode elseif;

        public IfNode(ExprNode cond, StatementNode then, StatementNode elseif, Position position) {
            this.cond = cond;
            this.then = then;
            this.elseif = elseif;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitIf(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitIf(this); }

        @Override
        public String toString() {
            if (elseif != null) {
                return "(if,(" + cond + "),(" + then + "),(" + elseif + "))";
            } else {
                return "(if,(" + cond + "),(" + then + "))";
            }
        }
    }

    public static class WhileNode extends StatementNode{
        public ExprNode cond;
        public StatementNode stat;

        public WhileNode(ExprNode cond, StatementNode stat, Position position) {
            this.cond = cond;
            this.stat = stat;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitWhile(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitWhile(this); }

        @Override
        public String toString() {
            return "((" + stat + "),(" + cond + "))";
        }
    }

    public static class ProcCallNode extends StatementNode{
        public IdNode name;
        public ExprListNode pars;

        public ProcCallNode(IdNode name, ExprListNode pars, Position position) {
            this.name = name;
            this.pars = pars;
            this.position = position;
        }


        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitProcCall(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitProcCall(this); }

        @Override
        public String toString() {
            return "((" + pars + "),(" + name + "))";
        }
    }

    public static class FuncCallNode extends ExprNode{
        public IdNode name;
        public ExprListNode pars;

        public FuncCallNode(IdNode name, ExprListNode pars, Position position) {
            this.name = name;
            this.pars = pars;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitFuncCall(this); }

        @Override
        public void visitP(IVisitorP v){ v.visitFuncCall(this); }

        @Override
        public String toString() { return "((" + pars + "),(" + name + "))"; }
    }
}
