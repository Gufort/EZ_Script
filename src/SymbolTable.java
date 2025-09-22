import com.sun.jdi.DoubleType;

import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {
    public enum Semantictype{IntType, DoubleType, BoolType,
                            StringType, ObjectType, BadType, NoType}
    public enum KindType{VarName, FuncName}

    public class SymbolInfo{
        public String name; //Имя символа
        public KindType kindType; //Вид символа - переменная или функция
        public Semantictype semanticType; //Тип переменной
        public Semantictype[] params; //Только для функций

        public SymbolInfo(String name, KindType kindType, Semantictype semanticType, Semantictype... params) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = semanticType;
            this.params = params;
        }
        public SymbolInfo(String name, KindType kindType, Semantictype semanticType) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = semanticType;
        }
    }

    public Semantictype[] NumTypes = new Semantictype[]{Semantictype.IntType, Semantictype.DoubleType};
    public Dictionary<String, SymbolInfo> SymTable = new Hashtable<String, SymbolInfo>();

    public void initStandardFunctionsTable(){
        SymTable.put("sqrt", new SymbolInfo("sqrt", KindType.FuncName, Semantictype.DoubleType, Semantictype.DoubleType));
        SymTable.put("print", new SymbolInfo("print", KindType.VarName, Semantictype.DoubleType, Semantictype.NoType));
    }
}
