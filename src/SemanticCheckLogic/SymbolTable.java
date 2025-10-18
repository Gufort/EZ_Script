package SemanticCheckLogic;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {
    public enum SemanticType{IntType, DoubleType, BoolType,
                            StringType, ObjectType, BadType, NoType, AnyType}
    public enum KindType{VarName, FuncName}

    public static class SymbolInfo{
        public String name; //Имя символа
        public KindType kindType; //Вид символа - переменная или функция
        public SemanticType semanticType; //Тип переменной
        public SemanticType[] params; //Только для функций
        public RuntimeValue runtimeValue; // Для интерпретатора
        public int index;

        public SymbolInfo(String name, KindType kindType, SemanticType semanticType, int index, SemanticType... params) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = semanticType;
            this.params = params;
            this.index = index;
        }
        public SymbolInfo(String name, KindType kindType, SemanticType semanticType, int index) {
            this.name = name;
            this.index = index;
            this.kindType = kindType;
            this.semanticType = semanticType;
        }
    }

    public static SemanticType[] NumTypes = new SemanticType[]{SemanticType.IntType, SemanticType.DoubleType};
    public static Dictionary<String, SymbolInfo> SymTable = new Hashtable<String, SymbolInfo>(){};
    public static ArrayList<RuntimeValue> VarValues = new ArrayList<RuntimeValue>();

    public static void initStandardFunctionsTable(){
        SymTable.put("sqrt", new SymbolInfo("sqrt", KindType.FuncName, SemanticType.DoubleType, -1, SemanticType.DoubleType));
        SymTable.put("print", new SymbolInfo("print", KindType.FuncName, SemanticType.AnyType, -1, SemanticType.DoubleType));
    }

    static {
        initStandardFunctionsTable();
    }

    public enum RTTypeMarker{ INT, DOUBLE, BOOL};

    public static class RuntimeValue{
        public int integer;
        public double real;
        public boolean bool;
        public RTTypeMarker rtType;
        public RuntimeValue(int integer){
            this.integer = integer;
            rtType = RTTypeMarker.INT;
        }
        public RuntimeValue(double real){
            this.real = real;
            rtType = RTTypeMarker.DOUBLE;
        }
        public RuntimeValue(boolean bool){
            this.bool = bool;
            rtType = RTTypeMarker.BOOL;
        }

        public boolean isInt(){ return rtType == RTTypeMarker.INT; }
        public boolean isDouble(){ return rtType == RTTypeMarker.DOUBLE; }
        public boolean isBool(){ return rtType == RTTypeMarker.BOOL; }
    }

    public static RuntimeValue value(int integer) { return new RuntimeValue(integer); }
    public static RuntimeValue value(double real) { return new RuntimeValue(real); }
    public static RuntimeValue value(boolean bool) { return new RuntimeValue(bool); }
}
