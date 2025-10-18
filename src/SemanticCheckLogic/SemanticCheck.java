package SemanticCheckLogic;

import Basic.ASTNodes;
import ExceptionLogic.CompilerException;
import Pointers.IntPointer;
import Pointers.RealPointer;

public class SemanticCheck extends AutoVisitorUnit {
    @Override public void visitAssign(ASTNodes.AssignNode node) throws Exception {
        node.expr.visitP(this);
        if(SymbolTable.SymTable.get(node.id.name) == null){
            var type = CalcTypes.calcTypeVis(node.expr);
            switch(type){
                case IntType -> SymbolTable.VarValues.add(SymbolTable.value(0));
                case BoolType -> SymbolTable.VarValues.add(SymbolTable.value(false));
                case DoubleType -> SymbolTable.VarValues.add(SymbolTable.value(0.0));
            }

            int lastIndex = SymbolTable.VarValues.size() - 1;
            SymbolTable.RuntimeValue lastValue = SymbolTable.VarValues.get(lastIndex);

            switch(type){
                case IntType -> node.id.pi = new IntPointer(lastValue.integer);
                case DoubleType -> node.id.pr = new RealPointer(lastValue.real);
            }
            node.id.ind = SymbolTable.VarValues.size() - 1;
            SymbolTable.SymTable.put(node.id.name, new SymbolTable.SymbolInfo(node.id.name,
                    SymbolTable.KindType.VarName, type, node.id.ind));
        }
        else{
            if(SymbolTable.SymTable.get(node.id.name).kindType == SymbolTable.KindType.FuncName)
                CompilerException.semanticError("Имени стандартной функции " + node.id.name + " нельзя присвоить значение", node.id.position);
            var type = CalcTypes.calcTypeVis(node.expr);
            var idType = SymbolTable.SymTable.get(node.id.name).semanticType;
            if(!CalcTypes.assignComparable(type, idType))
                CompilerException.semanticError("Переменной " + node.id.name + " типа " + idType + " нельзя присвоить значение типа " + type, node.id.position);

            var ind = SymbolTable.SymTable.get(node.id.name).index;
            node.id.ind = ind;
            SymbolTable.RuntimeValue existingValue = SymbolTable.VarValues.get(ind);

            switch(type){
                case IntType -> node.id.pi = new IntPointer(existingValue.integer);
                case DoubleType -> node.id.pr = new RealPointer(existingValue.real);
            }
        }
    }

    @Override
    public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception {
        node.expr.visitP(this);

        if(SymbolTable.SymTable.get(node.id.name) == null)
            CompilerException.semanticError("Переменная " + node.id.name + " не определена", node.id.position);
        else{
            if(SymbolTable.SymTable.get(node.id.name).kindType == SymbolTable.KindType.FuncName)
                CompilerException.semanticError("Имени стандартной функции " + node.id.name + " нельзя присвоить значение", node.id.position);

            var type = CalcTypes.calcTypeVis(node.expr);
            var idType = SymbolTable.SymTable.get(node.id.name).semanticType;

            if(idType != SymbolTable.SemanticType.IntType && idType != SymbolTable.SemanticType.DoubleType)
                CompilerException.semanticError("Операция " + node.op + " не определена для типа " + idType, node.id.position);

            if(node.op == '/' && idType == SymbolTable.SemanticType.IntType && type == SymbolTable.SemanticType.IntType)
                CompilerException.semanticError("Операция /= не определена для целочисленных типов", node.id.position);

            if(!CalcTypes.assignComparable(type, idType))
                CompilerException.semanticError("Переменной " + node.id.name + " типа " + idType + " нельзя присвоить значение типа " + type, node.id.position);

            var ind = SymbolTable.SymTable.get(node.id.name).index;
            node.id.ind = ind;
            SymbolTable.RuntimeValue existingValue = SymbolTable.VarValues.get(ind);

            switch(idType){
                case IntType -> node.id.pi = new IntPointer(existingValue.integer);
                case DoubleType -> node.id.pr = new RealPointer(existingValue.real);
            }
        }
    }
    @Override public void visitIf(ASTNodes.IfNode node) throws Exception {
        node.cond.visitP(this);
        var type = CalcTypes.calcTypeVis(node.cond);
        if(type != SymbolTable.SemanticType.BoolType)
            CompilerException.semanticError("Ожидалось выражение логического типа, а встречено выражение типа " + type, node.cond.position);
        node.then.visitP(this);
        if(node.elseif != null)
            node.elseif.visitP(this);
    }

    @Override public void visitWhile(ASTNodes.WhileNode node) throws Exception {
        node.cond.visitP(this);
        var type = CalcTypes.calcTypeVis(node.cond);
        if(type != SymbolTable.SemanticType.BoolType)
            CompilerException.semanticError("Ожидалось выражение логического типа, а встречено выражение типа " + type, node.cond.position);
        node.stat.visitP(this);
    }

    @Override
    public void visitFor(ASTNodes.ForNode node) throws Exception {
        node.start.visitP(this);
        node.condition.visitP(this);
        var type = CalcTypes.calcTypeVis(node.condition);
        if(type != SymbolTable.SemanticType.BoolType)
            CompilerException.semanticError("Ожидалось выражение логического типа, а встречено выражение типа " + type, node.condition.position);
        node.increment.visitP(this);
        node.body.visitP(this);
    }

    @Override public void visitId(ASTNodes.IdNode node) throws Exception {
        var ind = SymbolTable.SymTable.get(node.name).index;
        var type = SymbolTable.SymTable.get(node.name).semanticType;
        node.ind =  ind;
        SymbolTable.RuntimeValue existingValue = SymbolTable.VarValues.get(ind);

        switch(type){
            case IntType -> node.pi = new IntPointer(existingValue.integer);
            case DoubleType -> node.pr = new RealPointer(existingValue.real);
        }
    }
}
