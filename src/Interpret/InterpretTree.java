package Interpret;

import Pointers.BooleanPointer;
import Pointers.IntPointer;
import Pointers.RealPointer;

import java.util.ArrayList;
import java.util.List;

public class InterpretTree {
    public static abstract class NodeI {
    }

    public static abstract class ExprNodeI extends NodeI {
        public int evalInt() { return 0; }
        public double evalReal() { return 0.0; }
        public boolean evalBool() { return false; }
    }

    public static abstract class StatementNodeI extends NodeI {
        public void execute() {}
    }

    public static abstract class BinOpNodeI extends ExprNodeI {
        public ExprNodeI left;
        public ExprNodeI right;

        public BinOpNodeI(ExprNodeI left, ExprNodeI right) {
            this.left = left;
            this.right = right;
        }
    }

    public static class PlusII extends BinOpNodeI {
        public PlusII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public int evalInt() { return left.evalInt() + right.evalInt(); }
    }

    public static class PlusIR extends BinOpNodeI {
        public PlusIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() + right.evalReal(); }
    }

    public static class PlusRI extends BinOpNodeI {
        public PlusRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() + right.evalInt(); }
    }

    public static class PlusRR extends BinOpNodeI {
        public PlusRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() + right.evalReal(); }
    }

    public static class PlusIC extends BinOpNodeI {
        public int value;
        public PlusIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public int evalInt() { return left.evalInt() + value; }
    }

    public static class PlusRC extends BinOpNodeI {
        public double value;
        public PlusRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public double evalReal() { return left.evalReal() + value; }
    }

    // Аналогично для других операций
    public static class MinusII extends BinOpNodeI {
        public MinusII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public int evalInt() { return left.evalInt() - right.evalInt(); }
    }

    public static class MinusIR extends BinOpNodeI {
        public MinusIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() - right.evalReal(); }
    }

    public static class MinusRI extends BinOpNodeI {
        public MinusRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() - right.evalInt(); }
    }

    public static class MinusRR extends BinOpNodeI {
        public MinusRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() - right.evalReal(); }
    }

    public static class MultII extends BinOpNodeI {
        public MultII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public int evalInt() { return left.evalInt() * right.evalInt(); }
    }

    public static class MultIR extends BinOpNodeI {
        public MultIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() * right.evalReal(); }
    }

    public static class MultRI extends BinOpNodeI {
        public MultRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() * right.evalInt(); }
    }

    public static class MultRR extends BinOpNodeI {
        public MultRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() * right.evalReal(); }
    }

    public static class DivII extends BinOpNodeI {
        public DivII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return (double) left.evalInt() / right.evalInt(); }
    }

    public static class DivIR extends BinOpNodeI {
        public DivIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() / right.evalReal(); }
    }

    public static class DivRI extends BinOpNodeI {
        public DivRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() / right.evalInt(); }
    }

    public static class DivRR extends BinOpNodeI {
        public DivRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() / right.evalReal(); }
    }

    public static class LessII extends BinOpNodeI {
        public LessII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() < right.evalInt(); }
    }

    public static class LessIR extends BinOpNodeI {
        public LessIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() < right.evalReal(); }
    }

    public static class LessRI extends BinOpNodeI {
        public LessRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() < right.evalInt(); }
    }

    public static class LessRR extends BinOpNodeI {
        public LessRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() < right.evalReal(); }
    }

    public static class GreaterII extends BinOpNodeI {
        public GreaterII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() > right.evalInt(); }
    }

    public static class GreaterIR extends BinOpNodeI {
        public GreaterIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() > right.evalReal(); }
    }

    public static class GreaterRI extends BinOpNodeI {
        public GreaterRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() > right.evalInt(); }
    }

    public static class GreaterRR extends BinOpNodeI {
        public GreaterRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() > right.evalReal(); }
    }

    public static class LessEqII extends BinOpNodeI {
        public LessEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() <= right.evalInt(); }
    }

    public static class LessEqIR extends BinOpNodeI {
        public LessEqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() <= right.evalReal(); }
    }

    public static class LessEqRI extends BinOpNodeI {
        public LessEqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() <= right.evalInt(); }
    }

    public static class LessEqRR extends BinOpNodeI {
        public LessEqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() <= right.evalReal(); }
    }

    public static class GreaterEqII extends BinOpNodeI {
        public GreaterEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() >= right.evalInt(); }
    }

    public static class GreaterEqIR extends BinOpNodeI {
        public GreaterEqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() >= right.evalReal(); }
    }

    public static class GreaterEqRI extends BinOpNodeI {
        public GreaterEqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() >= right.evalInt(); }
    }

    public static class GreaterEqRR extends BinOpNodeI {
        public GreaterEqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() >= right.evalReal(); }
    }

    public static class EqII extends BinOpNodeI {
        public EqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() == right.evalInt(); }
    }

    public static class EqIR extends BinOpNodeI {
        public EqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() == right.evalReal(); }
    }

    public static class EqRI extends BinOpNodeI {
        public EqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() == right.evalInt(); }
    }

    public static class EqRR extends BinOpNodeI {
        public EqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() == right.evalReal(); }
    }

    public static class EqBB extends BinOpNodeI {
        public EqBB(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalBool() == right.evalBool(); }
    }

    public static class NotEqII extends BinOpNodeI {
        public NotEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() != right.evalInt(); }
    }

    public static class NotEqIR extends BinOpNodeI {
        public NotEqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() != right.evalReal(); }
    }

    public static class NotEqRI extends BinOpNodeI {
        public NotEqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() != right.evalInt(); }
    }

    public static class NotEqRR extends BinOpNodeI {
        public NotEqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() != right.evalReal(); }
    }

    public static class NotEqBB extends BinOpNodeI {
        public NotEqBB(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalBool() != right.evalBool(); }
    }

    // Контейнеры
    public static class StatementListNodeI extends StatementNodeI {
        public List<StatementNodeI> lst = new ArrayList<>();

        public void add(StatementNodeI st) { lst.add(st); }

        @Override
        public void execute() {
            for (StatementNodeI statement : lst) {
                statement.execute();
            }
        }
    }

    public static class ExprListNodeI extends NodeI {
        public ArrayList<ExprNodeI> lst = new ArrayList<>();

        public void add(ExprNodeI ex) { lst.add(ex); }
    }

    // Литералы
    public static class IntNodeI extends ExprNodeI {
        public int val;
        public IntNodeI(int value) { this.val = value; }
        @Override public int evalInt() { return val; }
    }

    public static class DoubleNodeI extends ExprNodeI {
        public double val;
        public DoubleNodeI(double value) { this.val = value; }
        @Override public double evalReal() { return val; }
    }

    // Идентификаторы
    public static class IdNodeI extends ExprNodeI {
        public IntPointer pi;
        public IdNodeI(IntPointer pi) { this.pi = pi; }
        @Override public int evalInt() { return pi.getValue(); }
    }

    public static class IdNodeR extends ExprNodeI {
        public RealPointer pr;
        public IdNodeR(RealPointer pr) { this.pr = pr; }
        @Override public double evalReal() { return pr.getValue(); }
    }

    public static class IdNodeB extends ExprNodeI {
        public BooleanPointer pb;
        public IdNodeB(BooleanPointer pb) { this.pb = pb; }
        @Override public boolean evalBool() { return pb.getValue(); }
    }

    public static class IdNodeFun extends ExprNodeI {
        public String name;
        public IdNodeFun(String name) { this.name = name; }
    }

    // Операторы присваивания
    public static class AssignIntNodeI extends StatementNodeI {
        public IntPointer pi;
        public ExprNodeI expr;

        public AssignIntNodeI(IntPointer pi, ExprNodeI expr) {
            this.pi = pi;
            this.expr = expr;
        }

        @Override
        public void execute() {
            pi.setValue(expr.evalInt());
        }
    }

    public static class AssignIntCNodeI extends StatementNodeI {
        public IntPointer pi;
        public int val;

        public AssignIntCNodeI(IntPointer pi, int val) {
            this.pi = pi;
            this.val = val;
        }

        @Override
        public void execute() {
            pi.setValue(val);
        }
    }

    public static class AssignRealNodeI extends StatementNodeI {
        public RealPointer pr;
        public ExprNodeI expr;

        public AssignRealNodeI(RealPointer pr, ExprNodeI expr) {
            this.pr = pr;
            this.expr = expr;
        }

        @Override
        public void execute() {
            pr.setValue(expr.evalReal());
        }
    }

    public static class AssignRealCNodeI extends StatementNodeI {
        public RealPointer pr;
        public double val;

        public AssignRealCNodeI(RealPointer pr, double val) {
            this.pr = pr;
            this.val = val;
        }

        @Override
        public void execute() {
            pr.setValue(val);
        }
    }

    public static class AssignRealIntCNodeI extends StatementNodeI {
        public RealPointer pr;
        public int val;

        public AssignRealIntCNodeI(RealPointer pr, int val) {
            this.pr = pr;
            this.val = val;
        }

        @Override
        public void execute() {
            pr.setValue((double) val);  // Явное приведение int к double
        }
    }

    public static class AssignBoolNodeI extends StatementNodeI {
        public BooleanPointer pb;
        public ExprNodeI expr;

        public AssignBoolNodeI(BooleanPointer pb, ExprNodeI expr) {
            this.pb = pb;
            this.expr = expr;
        }

        @Override
        public void execute() {
            pb.setValue(expr.evalBool());
        }
    }

    public static class AssignPlusIntNodeI extends AssignIntNodeI {
        public AssignPlusIntNodeI(IntPointer pi, ExprNodeI expr) {
            super(pi, expr);
        }

        @Override
        public void execute() {
            pi.setValue(pi.getValue() + expr.evalInt());
        }
    }

    public static class AssignPlusRealNodeI extends AssignRealNodeI {
        public AssignPlusRealNodeI(RealPointer pr, ExprNodeI expr) {
            super(pr, expr);
        }

        @Override
        public void execute() {
            pr.setValue(pr.getValue() + expr.evalReal());
        }
    }

    public static class AssignPlusIntCNodeI extends AssignIntCNodeI {
        public AssignPlusIntCNodeI(IntPointer pi, int val) {
            super(pi, val);
        }

        @Override
        public void execute() {
            pi.setValue(pi.getValue() + val);
        }
    }

    public static class AssignPlusRealCNodeI extends AssignRealCNodeI {
        public AssignPlusRealCNodeI(RealPointer pr, double val) {
            super(pr, val);
        }

        @Override
        public void execute() {
            pr.setValue(pr.getValue() + val);
        }
    }

    public static class AssignPlusRealIntCNodeI extends AssignRealIntCNodeI {
        public AssignPlusRealIntCNodeI(RealPointer pr, int val) {
            super(pr, val);
        }

        @Override
        public void execute() {
            pr.setValue(pr.getValue() + val);
        }
    }

    // Управляющие конструкции
    public static class IfNodeI extends StatementNodeI {
        public ExprNodeI condition;
        public StatementNodeI thenStat;
        public StatementNodeI elseStat;

        public IfNodeI(ExprNodeI condition, StatementNodeI thenStat, StatementNodeI elseStat) {
            this.condition = condition;
            this.thenStat = thenStat;
            this.elseStat = elseStat;
        }

        @Override
        public void execute() {
            if (condition.evalBool()) {
                thenStat.execute();
            } else if (elseStat != null) {
                elseStat.execute();
            }
        }
    }

    public static class WhileNodeI extends StatementNodeI {
        public ExprNodeI condition;
        public StatementNodeI stat;

        public WhileNodeI(ExprNodeI condition, StatementNodeI stat) {
            this.condition = condition;
            this.stat = stat;
        }

        @Override
        public void execute() {
            while (condition.evalBool()) {
                stat.execute();
            }
        }
    }

    public static class ForNodeI extends StatementNodeI {
        public ExprNodeI condition;
        public StatementNodeI body;
        public StatementNodeI start;
        public StatementNodeI increment;

        public ForNodeI(StatementNodeI start, ExprNodeI condition, StatementNodeI increment, StatementNodeI body) {
            this.start = start;
            this.condition = condition;
            this.body = body;
            this.increment = increment;
        }

        @Override
        public void execute() {
            start.execute();
            while(condition.evalBool()) {
                body.execute();
                increment.execute();
            }
        }
    }

    public static class ProcCallNodeI extends StatementNodeI {
        public String name;
        public ExprListNodeI pars;

        public ProcCallNodeI(String name, ExprListNodeI pars) {
            this.name = name;
            this.pars = pars;
        }

        @Override
        public void execute() {
            if ("Print".equals(name) && pars != null) {
                for (ExprNodeI expr : pars.lst) {
                    // Пробуем разные типы по порядку
                    try {
                        System.out.print(expr.evalInt());
                    } catch (Exception e1) {
                        try {
                            System.out.print(expr.evalReal());
                        } catch (Exception e2) {
                            try {
                                System.out.print(expr.evalBool());
                            } catch (Exception e3) {
                                System.out.print("?");
                            }
                        }
                    }
                    System.out.print(" ");
                }
                System.out.println();
            }
        }
    }
}