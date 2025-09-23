import java.beans.Expression;

public class SemanticCheck extends AutoVisitorUnit {
    public boolean checkExpressionType(ASTNodes.ExprNode expr, SymbolTable.SemanticType expectedType) throws Exception {
        var actualType = getExpressionType(expr);
        var res = CalcTypes.assignComparable(expectedType, actualType);
        if(res) return true;

        else CompilerException.semanticError("Ожидается тип " + expectedType + ", но получен " + actualType, expr.position);
        return false;
    }

    public SymbolTable.SemanticType getExpressionType(ASTNodes.ExprNode expr) {
        return switch (expr){
            case ASTNodes.BinOpNode bin -> CalcTypes.calcTypeHelper(bin);
            case ASTNodes.IdNode id -> CalcTypes.checkSymbolTable(id.name);
            case ASTNodes.IntNode ints -> SymbolTable.SemanticType.IntType;
            case ASTNodes.DoubleNode doubles -> SymbolTable.SemanticType.DoubleType;
            case ASTNodes.FuncCallNode func -> CalcTypes.checkSymbolTable(func.name.name);
            default -> null;
        };
    }

    public void visitAssign(ASTNodes.AssignNode assign) throws Exception{
        assign.expr.visitP(this);

        if(SymbolTable.SymTable.get(assign.id.name) == null){
            var type = getExpressionType(assign.expr);
            SymbolTable.SymTable.put(assign.id.name, new SymbolTable.SymbolInfo(assign.id.name, SymbolTable.KindType.VarName, type));
        }

        else{
            if(SymbolTable.SymTable.get(assign.id.name).kindType != SymbolTable.KindType.VarName){
                CompilerException.semanticError(assign.id.name + " не является именем переменной", assign.id.position);
            }
            checkExpressionType(assign.expr, SymbolTable.SymTable.get(assign.id.name).semanticType);
        }
    }

    public void visitBiOp(ASTNodes.BinOpNode bin) throws Exception{
        bin.left.visitP(this);
        bin.right.visitP(this);
        var leftType = getExpressionType(bin.left);
        var rightType = getExpressionType(bin.right);

        if(!CalcTypes.areTypesCompatibleForOp(bin.op, leftType, rightType))
            CompilerException.semanticError("Операция" + bin.op + " не определена для типов " + leftType + " и " + rightType, bin.position);
    }

    public void visitId(ASTNodes.IdNode id) throws Exception{
        if(SymbolTable.SymTable.get(id.name) == null)
            CompilerException.semanticError("Идентификатор " + id.name + " не определен", id.position);
        else if(SymbolTable.SymTable.get(id.name).kindType != SymbolTable.KindType.VarName)
            CompilerException.semanticError(id.name + " не является переменной", id.position);
    }

    public void visitAssignPlus(ASTNodes.AssignPlusNode assign) throws Exception{
        assign.expr.visitP(this);

        if(SymbolTable.SymTable.get(assign.id.name) == null)
            CompilerException.semanticError("Переменная " + assign.id.name + " не определена", assign.id.position);

        var idType = SymbolTable.SymTable.get(assign.id.name).semanticType;
        if(idType != SymbolTable.SemanticType.IntType && idType != SymbolTable.SemanticType.DoubleType)
            CompilerException.semanticError("Операция += не определена для типа " + idType, assign.position);
    }

    public void visitIf(ASTNodes.IfNode ifNode) throws Exception{
        ifNode.cond.visitP(this);
        if(!checkExpressionType(ifNode.cond, SymbolTable.SemanticType.BoolType))
            CompilerException.semanticError("Ожидалось выражение логического типа", ifNode.cond.position);

        ifNode.then.visitP(this);

        if(ifNode.elseif != null)
            ifNode.elseif.visitP(this);
    }

    public void visitWhile(ASTNodes.WhileNode whileNode) throws Exception{
        whileNode.cond.visitP(this);
        if(!checkExpressionType(whileNode.cond, SymbolTable.SemanticType.BoolType))
            CompilerException.semanticError("Ожидалось выражение логического типа", whileNode.cond.position);

        whileNode.stat.visitP(this);
    }

    public void visitProcCall(ASTNodes.ProcCallNode proc) throws Exception{
        for(var curr: proc.pars.lst)
            curr.visitP(this);

        if(SymbolTable.SymTable.get(proc.name.name) == null)
            CompilerException.semanticError("Функция с именем " + proc.name.name + " не определена", proc.name.position);

        var sym = SymbolTable.SymTable.get(proc.name.name);
        if(sym.kindType != SymbolTable.KindType.FuncName)
            CompilerException.semanticError("Данное имя " + proc.name.name + " не является именем функции", proc.name.position);

        if(sym.params.length != proc.pars.lst.size())
            CompilerException.semanticError("Несоответствие количества параметров при вызове метода " + proc.name.name, proc.name.position);

        if(sym.semanticType != SymbolTable.SemanticType.NoType)
            CompilerException.semanticError("Попытка вызвать функцию " + proc.name.name + " как процедуру", proc.name.position);

        //Проверяем типы аргументов
        for(int i = 0; i < proc.pars.lst.size(); i++){
            var expectedType = sym.params[i];
            var actualType = getExpressionType(proc.pars.lst.get(i));

            if(!CalcTypes.assignComparable(expectedType, actualType))
                CompilerException.semanticError("Тип аргумента процедуры " + actualType.toString() +
                        "не соответствует типу формального аргумента " + expectedType.toString(), proc.pars.lst.get(i).position);
        }
    }

    public void visitFuncCall(ASTNodes.FuncCallNode func) throws Exception{
        for(var curr: func.pars.lst)
            curr.visitP(this);

        if(SymbolTable.SymTable.get(func.name.name) == null)
            CompilerException.semanticError("Функция с именем " + func.name.name + " не определена", func.name.position);

        var sym = SymbolTable.SymTable.get(func.name.name);
        if(sym.kindType != SymbolTable.KindType.FuncName)
            CompilerException.semanticError("Данное имя " + func.name.name + " не является именем функции", func.name.position);

        if(sym.params.length != func.pars.lst.size())
            CompilerException.semanticError("Несоответствие количества параметров при вызове метода " + func.name.name, func.name.position);

        if(sym.semanticType == SymbolTable.SemanticType.NoType)
            CompilerException.semanticError("Попытка вызвать процедуру " + func.name.name + " как функцию", func.name.position);

        for(int i = 0; i < func.pars.lst.size(); i++){
            var expectedType = sym.params[i];
            var actualType = getExpressionType(func.pars.lst.get(i));

            if(!CalcTypes.assignComparable(expectedType, actualType))
                CompilerException.semanticError("Тип аргумента процедуры " + actualType.toString() +
                        "не соответствует типу формального аргумента " + expectedType.toString(), func.pars.lst.get(i).position);
        }
    }
}
