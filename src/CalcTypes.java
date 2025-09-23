import java.util.Arrays;

public class CalcTypes {
    public static ASTNodes.OperationType[] ArithmeticOperations =
            new ASTNodes.OperationType[] { ASTNodes.OperationType.opPlus,
            ASTNodes.OperationType.opMinus, ASTNodes.OperationType.opMultiply, ASTNodes.OperationType.opDivide,};
    public static ASTNodes.OperationType[] CompareOperations =
            new ASTNodes.OperationType[] { ASTNodes.OperationType.opEqual, ASTNodes.OperationType.opLess,
            ASTNodes.OperationType.opLessEqual, ASTNodes.OperationType.opGreater, ASTNodes.OperationType.opGreaterEqual, ASTNodes.OperationType.opNotEqual};
    public static ASTNodes.OperationType[] LogicalOperations =
            new ASTNodes.OperationType[]{ ASTNodes.OperationType.opAnd, ASTNodes.OperationType.opOr, ASTNodes.OperationType.opNotEqual};

    public static SymbolTable.SemanticType getPureType(ASTNodes.ExprNode expr) {
        return switch (expr){
            case ASTNodes.BinOpNode bin -> calcTypeHelper(bin);
            case ASTNodes.IdNode id -> checkSymbolTable(id.name);
            case ASTNodes.IntNode ints -> SymbolTable.SemanticType.IntType;
            case ASTNodes.DoubleNode doubles -> SymbolTable.SemanticType.DoubleType;
            case ASTNodes.FuncCallNode func -> checkSymbolTable(func.name.name);
            case null, default -> SymbolTable.SemanticType.BadType;
        };
    }


    public static SymbolTable.SemanticType checkSymbolTable(String name) {
        return SymbolTable.SymTable.get(name) != null?
                SymbolTable.SymTable.get(name).semanticType :
                SymbolTable.SemanticType.BadType;
    }

    // Чистая функция для вычисления типа (без семантических проверок)
    public static SymbolTable.SemanticType calcTypeHelper(ASTNodes.BinOpNode node) {
        var leftType = getPureType(node.left);
        var rightType = getPureType(node.right);

        if(Arrays.stream(ArithmeticOperations).anyMatch(curr -> curr == node.op)){
            if(node.op == ASTNodes.OperationType.opDivide)
                return SymbolTable.SemanticType.DoubleType;
            else if(leftType == rightType)
                return leftType;
            else return SymbolTable.SemanticType.DoubleType;
        }

        else if(Arrays.stream(CompareOperations).anyMatch(curr -> curr == node.op))
            return SymbolTable.SemanticType.BoolType;

        else if(Arrays.stream(LogicalOperations).anyMatch(curr -> curr == node.op))
            return SymbolTable.SemanticType.BoolType;

        else return SymbolTable.SemanticType.BadType;
    }

    // Функция проверки совместимости типов для операции
    public static boolean areTypesCompatibleForOp(ASTNodes.OperationType op, SymbolTable.SemanticType left, SymbolTable.SemanticType right) {
        if(Arrays.stream(ArithmeticOperations).anyMatch(curr -> curr == op))
            return Arrays.stream(SymbolTable.NumTypes).anyMatch(curr -> curr == left) &&
                    Arrays.stream(SymbolTable.NumTypes).anyMatch(curr -> curr == right);
        else if(Arrays.stream(CompareOperations).anyMatch(curr -> curr == op))
            return Arrays.stream(SymbolTable.NumTypes).anyMatch(curr -> curr == left) &&
                    Arrays.stream(SymbolTable.NumTypes).anyMatch(curr -> curr == right);
        else if(Arrays.stream(LogicalOperations).anyMatch(curr -> curr == op))
            return left == SymbolTable.SemanticType.BoolType && right == SymbolTable.SemanticType.BoolType;
        else return false;
    }

    public static boolean assignComparable(SymbolTable.SemanticType left, SymbolTable.SemanticType right) {
        if(left == right)
            return true;
        else if(left == SymbolTable.SemanticType.DoubleType && right == SymbolTable.SemanticType.IntType)
            return true;
        else if(left == SymbolTable.SemanticType.ObjectType && right != SymbolTable.SemanticType.NoType
                && right != SymbolTable.SemanticType.BadType)
            return true;
        return false;
    }
}
