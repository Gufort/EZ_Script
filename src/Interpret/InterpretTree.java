package Interpret;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class InterpretTree {
    public static abstract class NodeI {
    }

    public static abstract class ExprNodeI extends NodeI {
        public int evalInt() { return 0; }
        public double evalReal() { return 0.0; }
        public boolean evalBool() { return false; }
        public BigInteger evalBigInteger() { return new BigInteger("0"); }
        public int evalAddress() { return 0; }
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

    public static class BigIntegerNodeI extends ExprNodeI {
        public BigInteger val;
        public BigIntegerNodeI(BigInteger value) { this.val = value; }
        public BigIntegerNodeI(String value) { this.val = new BigInteger(value); }
        @Override public BigInteger evalBigInteger() { return val; }
    }

    public static class BooleanNodeI extends ExprNodeI {
        public boolean val;
        public BooleanNodeI(boolean value) { this.val = value; }
        @Override public boolean evalBool() { return val; }
    }

    public static class IdNodeI extends ExprNodeI {
        public int address;
        public Memory.DataType type;

        public IdNodeI(int address, Memory.DataType type) {
            this.address = address;
            this.type = type;
        }

        @Override public int evalInt() {
            if (type != Memory.DataType.INT) {
                throw new RuntimeException("Type mismatch: expected INT, got " + type);
            }
            return Memory.getInt(address);
        }

        @Override public double evalReal() {
            if (type != Memory.DataType.DOUBLE) {
                throw new RuntimeException("Type mismatch: expected DOUBLE, got " + type);
            }
            return Memory.getDouble(address);
        }

        @Override public boolean evalBool() {
            if (type != Memory.DataType.BOOLEAN) {
                throw new RuntimeException("Type mismatch: expected BOOLEAN, got " + type);
            }
            return Memory.getBoolean(address);
        }

        @Override public BigInteger evalBigInteger() {
            if (type != Memory.DataType.BIG_INTEGER) {
                throw new RuntimeException("Type mismatch: expected BIG_INTEGER, got " + type);
            }
            return Memory.getBigInteger(address);
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

    public static class PlusBIBI extends BinOpNodeI {
        public PlusBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().add(right.evalBigInteger());
        }
    }

    public static class PlusBIC extends BinOpNodeI {
        public BigInteger value;
        public PlusBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().add(value);
        }
    }


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

    public static class MinusIC extends BinOpNodeI {
        public int value;
        public MinusIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public int evalInt() { return left.evalInt() - value; }
    }

    public static class MinusRC extends BinOpNodeI {
        public double value;
        public MinusRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public double evalReal() { return left.evalReal() - value; }
    }

    public static class MinusBIBI extends BinOpNodeI {
        public MinusBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().subtract(right.evalBigInteger());
        }
    }

    public static class MinusBIC extends BinOpNodeI {
        public BigInteger value;
        public MinusBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().subtract(value);
        }
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

    public static class MultIC extends BinOpNodeI {
        public int value;
        public MultIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public int evalInt() { return left.evalInt() * value; }
    }

    public static class MultRC extends BinOpNodeI {
        public double value;
        public MultRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public double evalReal() { return left.evalReal() * value; }
    }

    public static class MultBIBI extends BinOpNodeI {
        public MultBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().multiply(right.evalBigInteger());
        }
    }

    public static class MultBIC extends BinOpNodeI {
        public BigInteger value;
        public MultBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().multiply(value);
        }
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

    public static class DivIC extends BinOpNodeI {
        public int value;
        public DivIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public double evalReal() { return left.evalInt() / (double) value; }
    }

    public static class DivRC extends BinOpNodeI {
        public double value;
        public DivRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public double evalReal() { return left.evalReal() / value; }
    }

    public static class DivBIBI extends BinOpNodeI {
        public DivBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().divide(right.evalBigInteger());
        }
    }

    public static class DivBIC extends BinOpNodeI {
        public BigInteger value;
        public DivBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().divide(value);
        }
    }


    public static class ModBIBI extends BinOpNodeI {
        public ModBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().mod(right.evalBigInteger());
        }
    }

    public static class ModBIC extends BinOpNodeI {
        public BigInteger value;
        public ModBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().mod(value);
        }
    }

    // Вставьте этот код в класс InterpretTree после других Assign-классов

    // ==================== СОСТАВНЫЕ ПРИСВАИВАНИЯ ДЛЯ BIGINTEGER ====================
    public static class AssignMinusBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMinusBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            BigInteger sub = expr.evalBigInteger();
            Memory.setBigInteger(address, current.subtract(sub));
        }
    }

    public static class AssignMultBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMultBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            BigInteger mul = expr.evalBigInteger();
            Memory.setBigInteger(address, current.multiply(mul));
        }
    }

    public static class AssignDivBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignDivBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            BigInteger div = expr.evalBigInteger();
            Memory.setBigInteger(address, current.divide(div));
        }
    }


    public static class AssignMinusBigIntegerCNodeI extends StatementNodeI {
        public int address;
        public BigInteger val;

        public AssignMinusBigIntegerCNodeI(int address, BigInteger val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.subtract(val));
        }
    }

    public static class AssignMultBigIntegerCNodeI extends StatementNodeI {
        public int address;
        public BigInteger val;

        public AssignMultBigIntegerCNodeI(int address, BigInteger val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.multiply(val));
        }
    }

    public static class AssignDivBigIntegerCNodeI extends StatementNodeI {
        public int address;
        public BigInteger val;

        public AssignDivBigIntegerCNodeI(int address, BigInteger val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.divide(val));
        }
    }

    public static class AssignMinusDoubleNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMinusDoubleNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            double sub = expr.evalReal();
            Memory.setDouble(address, current - sub);
        }
    }

    public static class AssignMinusDoubleCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignMinusDoubleCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            Memory.setDouble(address, current - val);
        }
    }

    public static class AssignMultDoubleNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMultDoubleNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            double mul = expr.evalReal();
            Memory.setDouble(address, current * mul);
        }
    }

    public static class AssignMultDoubleCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignMultDoubleCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            Memory.setDouble(address, current * val);
        }
    }

    public static class AssignBooleanCNodeI extends StatementNodeI {
        public int address;
        public boolean val;

        public AssignBooleanCNodeI(int address, boolean val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setBoolean(address, val);
        }
    }


    public static class AssignIntFromDoubleCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignIntFromDoubleCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setInt(address, (int) val);
        }
    }


    public static class ArrayAssignMinusBigIntegerNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignMinusBigIntegerNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            BigInteger current = Memory.getArrayElementBigInteger(arrAddr, idx);
            BigInteger sub = value.evalBigInteger();
            Memory.setArrayElementBigInteger(arrAddr, idx, current.subtract(sub));
        }
    }

    public static class ArrayAssignMultBigIntegerNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignMultBigIntegerNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            BigInteger current = Memory.getArrayElementBigInteger(arrAddr, idx);
            BigInteger mul = value.evalBigInteger();
            Memory.setArrayElementBigInteger(arrAddr, idx, current.multiply(mul));
        }
    }

    public static class ArrayAssignDivBigIntegerNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignDivBigIntegerNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            BigInteger current = Memory.getArrayElementBigInteger(arrAddr, idx);
            BigInteger div = value.evalBigInteger();
            Memory.setArrayElementBigInteger(arrAddr, idx, current.divide(div));
        }
    }


    public static class ArrayAssignMinusDoubleNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignMinusDoubleNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            double current = Memory.getArrayElementDouble(arrAddr, idx);
            double sub = value.evalReal();
            Memory.setArrayElementDouble(arrAddr, idx, current - sub);
        }
    }

    public static class ArrayAssignMultDoubleNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignMultDoubleNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            double current = Memory.getArrayElementDouble(arrAddr, idx);
            double mul = value.evalReal();
            Memory.setArrayElementDouble(arrAddr, idx, current * mul);
        }
    }

    public static class ArrayAssignDivDoubleNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignDivDoubleNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            double current = Memory.getArrayElementDouble(arrAddr, idx);
            double div = value.evalReal();
            Memory.setArrayElementDouble(arrAddr, idx, current / div);
        }
    }


    public static class ArrayAssignMinusIntNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignMinusIntNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            int current = Memory.getArrayElementInt(arrAddr, idx);
            int sub = value.evalInt();
            Memory.setArrayElementInt(arrAddr, idx, current - sub);
        }
    }

    public static class ArrayAssignMultIntNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignMultIntNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            int current = Memory.getArrayElementInt(arrAddr, idx);
            int mul = value.evalInt();
            Memory.setArrayElementInt(arrAddr, idx, current * mul);
        }
    }


    public static class LessII extends BinOpNodeI {
        public LessII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() < right.evalInt(); }
    }

    public static class LessIC extends BinOpNodeI {
        public int value;
        public LessIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalInt() < value; }
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

    public static class LessRC extends BinOpNodeI {
        public double value;
        public LessRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalReal() < value; }
    }

    public static class LessBIBI extends BinOpNodeI {
        public LessBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) < 0;
        }
    }

    public static class LessBIC extends BinOpNodeI {
        public BigInteger value;
        public LessBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(value) < 0;
        }
    }

    public static class GreaterII extends BinOpNodeI {
        public GreaterII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() > right.evalInt(); }
    }

    public static class GreaterIC extends BinOpNodeI {
        public int value;
        public GreaterIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalInt() > value; }
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

    public static class GreaterRC extends BinOpNodeI {
        public double value;
        public GreaterRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalReal() > value; }
    }

    public static class GreaterBIBI extends BinOpNodeI {
        public GreaterBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) > 0;
        }
    }

    public static class GreaterBIC extends BinOpNodeI {
        public BigInteger value;
        public GreaterBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(value) > 0;
        }
    }

    public static class LessEqII extends BinOpNodeI {
        public LessEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() <= right.evalInt(); }
    }

    public static class LessEqIC extends BinOpNodeI {
        public int value;
        public LessEqIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalInt() <= value; }
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

    public static class LessEqRC extends BinOpNodeI {
        public double value;
        public LessEqRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalReal() <= value; }
    }

    public static class LessEqBIBI extends BinOpNodeI {
        public LessEqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) <= 0;
        }
    }

    public static class LessEqBIC extends BinOpNodeI {
        public BigInteger value;
        public LessEqBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(value) <= 0;
        }
    }

    public static class GreaterEqII extends BinOpNodeI {
        public GreaterEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() >= right.evalInt(); }
    }

    public static class GreaterEqIC extends BinOpNodeI {
        public int value;
        public GreaterEqIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalInt() >= value; }
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

    public static class GreaterEqRC extends BinOpNodeI {
        public double value;
        public GreaterEqRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalReal() >= value; }
    }

    public static class GreaterEqBIBI extends BinOpNodeI {
        public GreaterEqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) >= 0;
        }
    }

    public static class GreaterEqBIC extends BinOpNodeI {
        public BigInteger value;
        public GreaterEqBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(value) >= 0;
        }
    }

    public static class EqII extends BinOpNodeI {
        public EqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() == right.evalInt(); }
    }

    public static class EqIC extends BinOpNodeI {
        public int value;
        public EqIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalInt() == value; }
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

    public static class EqRC extends BinOpNodeI {
        public double value;
        public EqRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalReal() == value; }
    }

    public static class EqBB extends BinOpNodeI {
        public EqBB(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalBool() == right.evalBool(); }
    }

    public static class EqBC extends BinOpNodeI {
        public boolean value;
        public EqBC(ExprNodeI left, boolean value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalBool() == value; }
    }

    public static class EqBIBI extends BinOpNodeI {
        public EqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().equals(right.evalBigInteger());
        }
    }

    public static class EqBIC extends BinOpNodeI {
        public BigInteger value;
        public EqBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() {
            return left.evalBigInteger().equals(value);
        }
    }

    public static class NotEqII extends BinOpNodeI {
        public NotEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() != right.evalInt(); }
    }

    public static class NotEqIC extends BinOpNodeI {
        public int value;
        public NotEqIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalInt() != value; }
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

    public static class NotEqRC extends BinOpNodeI {
        public double value;
        public NotEqRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalReal() != value; }
    }

    public static class NotEqBB extends BinOpNodeI {
        public NotEqBB(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalBool() != right.evalBool(); }
    }

    public static class NotEqBC extends BinOpNodeI {
        public boolean value;
        public NotEqBC(ExprNodeI left, boolean value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() { return left.evalBool() != value; }
    }

    public static class NotEqBIBI extends BinOpNodeI {
        public NotEqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return !left.evalBigInteger().equals(right.evalBigInteger());
        }
    }

    public static class NotEqBIC extends BinOpNodeI {
        public BigInteger value;
        public NotEqBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public boolean evalBool() {
            return !left.evalBigInteger().equals(value);
        }
    }


    public static class IntToDoubleNodeI extends ExprNodeI {
        public ExprNodeI expr;
        public IntToDoubleNodeI(ExprNodeI expr) { this.expr = expr; }
        @Override public double evalReal() { return (double) expr.evalInt(); }
    }

    public static class DoubleToIntNodeI extends ExprNodeI {
        public ExprNodeI expr;
        public DoubleToIntNodeI(ExprNodeI expr) { this.expr = expr; }
        @Override public int evalInt() { return (int) expr.evalReal(); }
    }

    public static class IntToBigIntegerNodeI extends ExprNodeI {
        public ExprNodeI expr;
        public IntToBigIntegerNodeI(ExprNodeI expr) { this.expr = expr; }
        @Override public BigInteger evalBigInteger() {
            return BigInteger.valueOf(expr.evalInt());
        }
    }

    public static class BigIntegerToIntNodeI extends ExprNodeI {
        public ExprNodeI expr;
        public BigIntegerToIntNodeI(ExprNodeI expr) { this.expr = expr; }
        @Override public int evalInt() {
            return expr.evalBigInteger().intValue();
        }
    }


    public static class ArrayAccessNodeI extends ExprNodeI {
        public ExprNodeI array;          // Адрес заголовка массива
        public ExprNodeI index;          // Индекс
        public Memory.DataType elementType; // Тип элементов

        public ArrayAccessNodeI(ExprNodeI array, ExprNodeI index, Memory.DataType elementType) {
            this.array = array;
            this.index = index;
            this.elementType = elementType;
        }

        @Override
        public int evalInt() {
            if (elementType != Memory.DataType.INT) {
                throw new RuntimeException("Type mismatch: expected INT, got " + elementType);
            }
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            return Memory.getArrayElementInt(arrAddr, idx);
        }

        @Override
        public double evalReal() {
            if (elementType != Memory.DataType.DOUBLE) {
                throw new RuntimeException("Type mismatch: expected DOUBLE, got " + elementType);
            }
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            return Memory.getArrayElementDouble(arrAddr, idx);
        }

        @Override
        public boolean evalBool() {
            if (elementType != Memory.DataType.BOOLEAN) {
                throw new RuntimeException("Type mismatch: expected BOOLEAN, got " + elementType);
            }
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            return Memory.getArrayElementBoolean(arrAddr, idx);
        }

        @Override
        public BigInteger evalBigInteger() {
            if (elementType != Memory.DataType.BIG_INTEGER) {
                throw new RuntimeException("Type mismatch: expected BIG_INTEGER, got " + elementType);
            }
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            return Memory.getArrayElementBigInteger(arrAddr, idx);
        }
    }

    public static class ArrayLiteralNodeI extends ExprNodeI {
        public ArrayList<ExprNodeI> elements;
        public Memory.DataType elementType;

        public ArrayLiteralNodeI(ArrayList<ExprNodeI> elements, Memory.DataType elementType) {
            this.elements = elements;
            this.elementType = elementType;
        }

        @Override
        public int evalInt() {
            int length = elements.size();
            int arrayAddress = Memory.allocateArray(elementType, length);

            for (int i = 0; i < length; i++) {
                int elementAddress = Memory.getArrayElementAddress(arrayAddress, i);
                ExprNodeI element = elements.get(i);

                switch (elementType) {
                    case INT:
                        Memory.setInt(elementAddress, element.evalInt());
                        break;
                    case DOUBLE:
                        Memory.setDouble(elementAddress, element.evalReal());
                        break;
                    case BOOLEAN:
                        Memory.setBoolean(elementAddress, element.evalBool());
                        break;
                    case BIG_INTEGER:
                        Memory.setBigInteger(elementAddress, element.evalBigInteger());
                        break;
                }
            }

            return arrayAddress;
        }
    }

    public static class ArrayDeclarationNodeI extends StatementNodeI {
        public int variableAddress;      // Адрес переменной, хранящей адрес массива
        public ExprNodeI size;           // Размер массива
        public ArrayList<ExprNodeI> initialElements;
        public Memory.DataType elementType;

        public ArrayDeclarationNodeI(int variableAddress, ExprNodeI size,
                                     ArrayList<ExprNodeI> initialElements,
                                     Memory.DataType elementType) {
            this.variableAddress = variableAddress;
            this.size = size;
            this.initialElements = initialElements;
            this.elementType = elementType;
        }

        @Override
        public void execute() {
            int arraySize;
            int arrayAddress;

            if (size != null) {
                arraySize = size.evalInt();
                arrayAddress = Memory.allocateArray(elementType, arraySize);

                if (initialElements != null && !initialElements.isEmpty()) {
                    int initSize = Math.min(arraySize, initialElements.size());
                    for (int i = 0; i < initSize; i++) {
                        int elementAddress = Memory.getArrayElementAddress(arrayAddress, i);
                        ExprNodeI element = initialElements.get(i);

                        switch (elementType) {
                            case INT:
                                Memory.setInt(elementAddress, element.evalInt());
                                break;
                            case DOUBLE:
                                Memory.setDouble(elementAddress, element.evalReal());
                                break;
                            case BOOLEAN:
                                Memory.setBoolean(elementAddress, element.evalBool());
                                break;
                            case BIG_INTEGER:
                                Memory.setBigInteger(elementAddress, element.evalBigInteger());
                                break;
                        }
                    }
                }
            } else if (initialElements != null && !initialElements.isEmpty()) {
                arraySize = initialElements.size();
                arrayAddress = Memory.allocateArray(elementType, arraySize);

                for (int i = 0; i < arraySize; i++) {
                    int elementAddress = Memory.getArrayElementAddress(arrayAddress, i);
                    ExprNodeI element = initialElements.get(i);

                    switch (elementType) {
                        case INT:
                            Memory.setInt(elementAddress, element.evalInt());
                            break;
                        case DOUBLE:
                            Memory.setDouble(elementAddress, element.evalReal());
                            break;
                        case BOOLEAN:
                            Memory.setBoolean(elementAddress, element.evalBool());
                            break;
                        case BIG_INTEGER:
                            Memory.setBigInteger(elementAddress, element.evalBigInteger());
                            break;
                    }
                }
            } else {
                throw new RuntimeException("Array declaration must have either size or initializer");
            }

            Memory.setInt(variableAddress, arrayAddress);
        }
    }

    public static class ArrayAssignIntNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignIntNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            int val = value.evalInt();
            Memory.setArrayElementInt(arrAddr, idx, val);
        }
    }

    public static class ArrayAssignDoubleNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignDoubleNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            double val = value.evalReal();
            Memory.setArrayElementDouble(arrAddr, idx, val);
        }
    }

    public static class ArrayAssignBooleanNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignBooleanNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            boolean val = value.evalBool();
            Memory.setArrayElementBoolean(arrAddr, idx, val);
        }
    }

    public static class ArrayAssignBigIntegerNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignBigIntegerNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            BigInteger val = value.evalBigInteger();
            Memory.setArrayElementBigInteger(arrAddr, idx, val);
        }
    }

    public static class ArrayAssignPlusIntNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignPlusIntNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            int current = Memory.getArrayElementInt(arrAddr, idx);
            int add = value.evalInt();
            Memory.setArrayElementInt(arrAddr, idx, current + add);
        }
    }

    public static class ArrayAssignPlusDoubleNodeI extends StatementNodeI {
        public ExprNodeI array;
        public ExprNodeI index;
        public ExprNodeI value;

        public ArrayAssignPlusDoubleNodeI(ExprNodeI array, ExprNodeI index, ExprNodeI value) {
            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public void execute() {
            int arrAddr = array.evalInt();
            int idx = index.evalInt();
            double current = Memory.getArrayElementDouble(arrAddr, idx);
            double add = value.evalReal();
            Memory.setArrayElementDouble(arrAddr, idx, current + add);
        }
    }

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

    public static class AssignIntNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignIntNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setInt(address, expr.evalInt());
        }
    }

    public static class AssignDoubleNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignDoubleNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setDouble(address, expr.evalReal());
        }
    }

    public static class AssignBooleanNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignBooleanNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setBoolean(address, expr.evalBool());
        }
    }

    public static class AssignBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setBigInteger(address, expr.evalBigInteger());
        }
    }

    public static class AssignIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setInt(address, val);
        }
    }

    public static class AssignDoubleCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignDoubleCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setDouble(address, val);
        }
    }

    public static class AssignBigIntegerCNodeI extends StatementNodeI {
        public int address;
        public BigInteger val;

        public AssignBigIntegerCNodeI(int address, BigInteger val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setBigInteger(address, val);
        }
    }

    public static class AssignPlusIntNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignPlusIntNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            int add = expr.evalInt();
            Memory.setInt(address, current + add);
        }
    }

    public static class AssignPlusIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignPlusIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            Memory.setInt(address, current + val);
        }
    }

    public static class AssignPlusDoubleNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignPlusDoubleNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            double add = expr.evalReal();
            Memory.setDouble(address, current + add);
        }
    }

    public static class AssignPlusDoubleCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignPlusDoubleCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            Memory.setDouble(address, current + val);
        }
    }

    public static class AssignPlusBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignPlusBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            BigInteger add = expr.evalBigInteger();
            Memory.setBigInteger(address, current.add(add));
        }
    }

    public static class AssignPlusBigIntegerCNodeI extends StatementNodeI {
        public int address;
        public BigInteger val;

        public AssignPlusBigIntegerCNodeI(int address, BigInteger val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.add(val));
        }
    }

    public static class AssignMinusIntNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMinusIntNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            int sub = expr.evalInt();
            Memory.setInt(address, current - sub);
        }
    }

    public static class AssignMinusIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignMinusIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            Memory.setInt(address, current - val);
        }
    }

    public static class AssignMultIntNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMultIntNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            int mul = expr.evalInt();
            Memory.setInt(address, current * mul);
        }
    }

    public static class AssignMultIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignMultIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            Memory.setInt(address, current * val);
        }
    }

    public static class AssignDivDoubleNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignDivDoubleNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            double div = expr.evalReal();
            Memory.setDouble(address, current / div);
        }
    }

    public static class AssignDivDoubleCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignDivDoubleCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            Memory.setDouble(address, current / val);
        }
    }

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
        public StatementNodeI start;
        public ExprNodeI condition;
        public StatementNodeI increment;
        public StatementNodeI body;

        public ForNodeI(StatementNodeI start, ExprNodeI condition,
                        StatementNodeI increment, StatementNodeI body) {
            this.start = start;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }

        @Override
        public void execute() {
            start.execute();
            while (condition.evalBool()) {
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
            if ("print".equals(name) && pars != null) {
                for (ExprNodeI expr : pars.lst) {
                    try {
                        System.out.print(expr.evalInt() + " ");
                    } catch (Exception e1) {
                        try {
                            System.out.print(expr.evalReal() + " ");
                        } catch (Exception e2) {
                            try {
                                System.out.print(expr.evalBool() + " ");
                            } catch (Exception e3) {
                                try {
                                    System.out.print(expr.evalBigInteger() + " ");
                                } catch (Exception e4) {
                                    System.out.print("? ");
                                }
                            }
                        }
                    }
                }
                System.out.println();
            }
        }
    }
}