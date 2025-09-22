import com.sun.jdi.DoubleType;

import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {
    public enum SemanticType{IntType, DoubleType, BoolType,
                            StringType, ObjectType, BadType, NoType}
    public enum KindType{VarName, FuncName}

    public class SymbolInfo{
        public String name; //Имя символа
        public KindType kindType; //Вид символа - переменная или функция
        public SemanticType semanticType; //Тип переменной
        public SemanticType[] params; //Только для функций

        public SymbolInfo(String name, KindType kindType, SemanticType semanticType, SemanticType... params) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = semanticType;
            this.params = params;
        }
        public SymbolInfo(String name, KindType kindType, SemanticType semanticType) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = semanticType;
        }
    }

    public static SemanticType[] NumTypes = new SemanticType[]{SemanticType.IntType, SemanticType.DoubleType};
    public static Dictionary<String, SymbolInfo> SymTable = new Hashtable<String, SymbolInfo>(){};

    public void initStandardFunctionsTable(){
        SymTable.put("sqrt", new SymbolInfo("sqrt", KindType.FuncName, SemanticType.DoubleType, SemanticType.DoubleType));
        SymTable.put("print", new SymbolInfo("print", KindType.VarName, SemanticType.DoubleType, SemanticType.NoType));
    }
}
