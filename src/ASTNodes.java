import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public abstract class ASTNodes {

    public static Dictionary<String, Double> varValues = new Hashtable<String, Double>();

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
        public abstract void visit(IVisitorP v);
    }

    public static abstract class ExprNode extends Node{
        public Position position;
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitExprNode(this); };
        @Override
        public void visit(IVisitorP v){ v.visitExprNode(this); }

        public abstract double eval() throws Exception;
    }

    public static abstract class StatementNode extends Node{
        public Position position;
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitStatementNode(this); };
        @Override
        public void visit(IVisitorP v){ v.visitStatementNode(this); }
        public void setPos(Position pos){
            position = pos;
        }
        public abstract void execute() throws Exception;
    }

    public static class BinOpNode extends ExprNode{
        public ExprNode left;
        public ExprNode right;
        public String op;

        public BinOpNode(ExprNode left, ExprNode right, String op, Position position) {
            this.left = left;
            this.right = right;
            this.op = op;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v){return v.visitBinOp(this); };
        @Override
        public void visit(IVisitorP v){ v.visitBinOp(this); }

        @Override
        public double eval() throws Exception{
            var l = left.eval();
            var r = right.eval();
            switch (op){
                case "+":
                    return l + r;
                case "*":
                    return l * r;
                case "/":
                    return l / r;
                case "<":
                    return l < r ? 1 : 0;
                case "==":
                    return l == r ? 1 : 0;
                default:
                    throw new CompilerException.SemanticException(
                            "Unknown operator: '" + op + "'", this.position
                    );
            }
        }
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
        public void visit(IVisitorP v){ v.visitStatementList(this); }

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

        @Override
        public void execute() throws Exception{
            for (StatementNode statement : statements) {
                statement.execute();
            }
        }
    }

    public static class ExprListNode extends Node{
        public ArrayList<ExprNode> lst = new ArrayList<ExprNode>();
        public void add(ExprNode expr){ lst.add(expr); }
        @Override
        public <T> T visit(IVisitor<T> v){ return v.visitExprList(this); };
        @Override
        public void visit(IVisitorP v){ v.visitExprList(this); }
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
        public void visit(IVisitorP v){ v.visitInt(this); }

        @Override
        public String toString() { return String.valueOf(value); }

        @Override
        public double eval() throws Exception{
            return Integer.valueOf(value);
        }
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
        public void visit(IVisitorP v){ v.visitDouble(this); }

        @Override
        public String toString() { return String.valueOf(value); }

        @Override
        public double eval() throws Exception{
            return Double.valueOf(value);
        }
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
        public void visit(IVisitorP v){ v.visitId(this); }

        @Override
        public String toString() { return name; }

        @Override
        public double eval() throws Exception{
            return varValues.get(name);
        }
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
        public void visit(IVisitorP v){ v.visitAssign(this); }

        @Override
        public String toString() {
            return "((" + expr + "),(" + id + "))";
        }

        @Override
        public void execute() throws Exception{
            varValues.put(id.name, expr.eval());
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
        public void visit(IVisitorP v){ v.visitAssignPlus(this); }

        @Override
        public String toString() {
            return "((+=,(" + expr + "),(" + id + ")))";
        }

        @Override
        public void execute() throws Exception{
            var curr = varValues.get(id.name);
            varValues.put(id.name, curr + expr.eval());
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
        public void visit(IVisitorP v){ v.visitIf(this); }

        @Override
        public String toString() {
            if (elseif != null) {
                return "(if,(" + cond + "),(" + then + "),(" + elseif + "))";
            } else {
                return "(if,(" + cond + "),(" + then + "))";
            }
        }

        @Override
        public void execute() throws Exception{
            if(cond.eval() > 0)
                then.execute();
            else if(elseif != null)
                elseif.execute();
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
        public void visit(IVisitorP v){ v.visitWhile(this); }

        @Override
        public String toString() {
            return "((" + stat + "),(" + cond + "))";
        }

        @Override
        public void execute() throws Exception{
            while (cond.eval() > 0){
                stat.execute();
            }
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
        public void visit(IVisitorP v){ v.visitProcCall(this); }

        @Override
        public String toString() {
            return "((" + pars + "),(" + name + "))";
        }

        @Override
        public void execute() throws Exception{
            if(name.name.equals("print")) {
                System.out.println(pars.lst.get(0).eval());
            }
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
        public void visit(IVisitorP v){ v.visitFuncCall(this); }

        @Override
        public String toString() { return "((" + pars + "),(" + name + "))"; }

        @Override
        public double eval() throws Exception{
            return 0;
        }
    }
}
