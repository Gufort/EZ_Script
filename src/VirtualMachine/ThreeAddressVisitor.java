package VirtualMachine;

import Basic.*;
import SemanticCheckLogic.CalcTypes;
import SemanticCheckLogic.SymbolTable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class ThreeAddressVisitor implements ASTNodes.IVisitorP{
    private int tempCounter = 0;
    private int labelCounter = 0;
    private Hashtable<String, Integer> labelAddresses = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> variavleAddreses = new Hashtable<String, Integer>();
    private ArrayList<ThreeAddressCode> code = new ArrayList<ThreeAddressCode>();
    private int nextVariableAddress = 0;

    private int newTemp(){ return tempCounter++; }
    private String newLabel(){ return "L" + labelCounter++; }
    private int getVariableAddress(String name){
        if(variavleAddreses.get(name) == null)
            variavleAddreses.put(name, nextVariableAddress++);
        return variavleAddreses.get(name);
    }
    public ArrayList<ThreeAddressCode> getCode(){
        return code;
    }

    @Override public void visitNode(ASTNodes.Node node) {}
    @Override public void visitExprNode(ASTNodes.ExprNode node) {}
    @Override public void visitStatementNode(ASTNodes.StatementNode node) {}

    @Override public void visitBinOp(ASTNodes.BinOpNode node) throws Exception{
        node.left.visitP(this);
        int left = tempCounter - 1;

        node.right.visitP(this);
        int right = tempCounter - 1;

        int res = newTemp();

        var leftType = CalcTypes.calcType(node.left);
        var rightType = CalcTypes.calcType(node.right);
        var resType = CalcTypes.calcType(node);

        if(leftType == SymbolTable.SemanticType.IntType && rightType == SymbolTable.SemanticType.IntType){
            switch (node.op){
                case LexerUnit.TokenType.PLUS:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IADD, left, right, res));
                    break;
                case LexerUnit.TokenType.MINUS:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.ISUB, left, right, res));
                    break;
                case LexerUnit.TokenType.MULTIPLE:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IMUL, left, right, res));
                    break;
                case LexerUnit.TokenType.DIVIDE:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IDIV, left, right, res));
                    break;
                case LexerUnit.TokenType.LESS:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.ILT, left, right, res));
                    break;
                case LexerUnit.TokenType.GREATER:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IGT, left, right, res));
                    break;
                case LexerUnit.TokenType.EQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IEQ, left, right, res));
                    break;
                case LexerUnit.TokenType.NOTEQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.INEQ, left, right, res));
                    break;
                case LexerUnit.TokenType.GREATEREQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IGEQ, left, right, res));
                    break;
                case LexerUnit.TokenType.LESSEQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.ILEQ, left, right, res));
                    break;
            }
        }

        else if(leftType == SymbolTable.SemanticType.DoubleType && rightType == SymbolTable.SemanticType.DoubleType){
            switch (node.op){
                case LexerUnit.TokenType.PLUS:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RADD, left, right, res));
                    break;
                case LexerUnit.TokenType.MINUS:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RSUB, left, right, res));
                    break;
                case LexerUnit.TokenType.MULTIPLE:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RMUL, left, right, res));
                    break;
                case LexerUnit.TokenType.DIVIDE:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RDIV, left, right, res));
                    break;
                case LexerUnit.TokenType.LESS:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RLT, left, right, res));
                    break;
                case LexerUnit.TokenType.GREATER:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RGT, left, right, res));
                    break;
                case LexerUnit.TokenType.EQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.REQ, left, right, res));
                    break;
                case LexerUnit.TokenType.NOTEQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RNEQ, left, right, res));
                    break;
                case LexerUnit.TokenType.GREATEREQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RGEQ, left, right, res));
                    break;
                case LexerUnit.TokenType.LESSEQUAL:
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RLEQ, left, right, res));
                    break;
            }
        }

        else if(leftType == SymbolTable.SemanticType.IntType && rightType == SymbolTable.SemanticType.DoubleType){
            int convert = newTemp();
            code.add(ThreeAddressCode.createConvert(ThreeAddressCode.Commands.CONITR, left, convert));
            left = convert;
            leftType = SymbolTable.SemanticType.DoubleType;
        }

        else if(leftType == SymbolTable.SemanticType.DoubleType && rightType == SymbolTable.SemanticType.IntType){
            int convert = newTemp();
            code.add(ThreeAddressCode.createConvert(ThreeAddressCode.Commands.CONITR, right, convert));
            right = convert;
            rightType = SymbolTable.SemanticType.DoubleType;
        }
    }

    public void visitStatementList(ASTNodes.StatementListNode node) throws  Exception{
        for(var curr: node.statements)
            curr.visitP(this);
    }

    public void visitExprList(ASTNodes.ExprListNode node) throws  Exception{
        for(var curr: node.lst)
            curr.visitP(this);
    }

    public void visitInt(ASTNodes.IntNode node) throws  Exception{
        int temp = newTemp();
        code.add(ThreeAddressCode.createConst(ThreeAddressCode.Commands.ICAAS, temp, node.value));
    }

    public void visitDouble(ASTNodes.DoubleNode node) throws  Exception{
        int temp = newTemp();
        code.add(ThreeAddressCode.createConst(ThreeAddressCode.Commands.RCAAS, temp, node.value));
    }

    public void visitId(ASTNodes.IdNode node) throws  Exception{
        int temp = newTemp();
        int address = getVariableAddress(node.name);
        var varType = CalcTypes.calcType(node);
        ThreeAddressCode.Commands command = varType == SymbolTable.SemanticType.DoubleType ?
                ThreeAddressCode.Commands.RASS : ThreeAddressCode.Commands.IASS;
        code.add(ThreeAddressCode.createAssign(command, temp, address));
    }

    public void visitAssign(ASTNodes.AssignNode node) throws  Exception{
        node.expr.visitP(this);
        var exprType = CalcTypes.calcType(node.expr);
        var exprRes = tempCounter - 1;
        int address = getVariableAddress(node.id.name);

        if(exprType == SymbolTable.SemanticType.DoubleType)
            code.add(ThreeAddressCode.createAssign(ThreeAddressCode.Commands.RASS, address, exprRes));
        else if(exprType == SymbolTable.SemanticType.IntType)
            code.add(ThreeAddressCode.createAssign(ThreeAddressCode.Commands.IASS, address, exprRes));
        else if(exprType == SymbolTable.SemanticType.BoolType)
            code.add(ThreeAddressCode.createAssign(ThreeAddressCode.Commands.BASS, address, exprRes));
    }

    public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws  Exception{
        node.expr.visitP(this);
        int address = getVariableAddress(node.id.name);
        var varType = CalcTypes.calcType(node.id);
        var varValue = newTemp();
        var exprType = CalcTypes.calcType(node.expr);
        var exprRes = tempCounter - 1;

        ThreeAddressCode.Commands command = varType == SymbolTable.SemanticType.DoubleType ?
                ThreeAddressCode.Commands.RASS : ThreeAddressCode.Commands.IASS;
        code.add(ThreeAddressCode.createAssign(command, varValue, address)); // текущее значение переменной

        if(varType == SymbolTable.SemanticType.DoubleType && exprType == SymbolTable.SemanticType.IntType){
            int convert = newTemp();
            code.add(ThreeAddressCode.createConvert(ThreeAddressCode.Commands.CONITR, exprRes, convert));
            exprRes = convert;
        }

        int operationResult = newTemp();

        if(varType == SymbolTable.SemanticType.DoubleType){
            switch (node.op) {
                case '+': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RASSADD, varValue, exprRes, operationResult)); break;
                case '-': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RASSSUB, varValue, exprRes, operationResult)); break;
                case '*': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RASSMUL, varValue, exprRes, operationResult)); break;
                case '/': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RASSDIV, varValue, exprRes, operationResult)); break;
            }
        }

        else if(varType == SymbolTable.SemanticType.IntType){
            switch (node.op) {
                case '+': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IASSADD, varValue, exprRes, operationResult)); break;
                case '-': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IASSSUB, varValue, exprRes, operationResult)); break;
                case '*': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IASSMUL, varValue, exprRes, operationResult)); break;
                case '/': code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IASSDIV, varValue, exprRes, operationResult)); break;
            }
        }

        var endCommand = varType == SymbolTable.SemanticType.DoubleType ? ThreeAddressCode.Commands.RASS : ThreeAddressCode.Commands.IASS;
        code.add(ThreeAddressCode.createAssign(endCommand, address, operationResult)); // сохранение результата
    }

    public void visitIf(ASTNodes.IfNode node) throws  Exception{
        node.cond.visitP(this);
        var condTemp = tempCounter - 1;
        var elseLabel = newLabel();
        var endLabel = newLabel();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.IIF, condTemp, elseLabel));
        node.then.visitP(this);
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.GOTO, endLabel));

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, elseLabel));
        if(node.elseif != null)
            node.elseif.visitP(this);

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.GOTO, endLabel));
    }

    public void visitWhile(ASTNodes.WhileNode node) throws  Exception{
        var startLabel = newLabel();
        var endLabel = newLabel();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, startLabel));

        node.cond.visitP(this);
        var condTemp = tempCounter - 1;

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.IFN, condTemp, endLabel));
        node.stat.visitP(this);
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.GOTO, startLabel));

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, endLabel));
    }

    public void visitFor(ASTNodes.ForNode node) throws  Exception{
        var startLabel = newLabel();
        var endLabel = newLabel();

        node.start.visitP(this);
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, startLabel));

        node.condition.visitP(this);
        var condTemp = tempCounter - 1;

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.IFN, condTemp, endLabel));

        node.body.visitP(this);
        node.increment.visitP(this);

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.GOTO, startLabel));

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, endLabel));
    }

    public void visitProcCall(ASTNodes.ProcCallNode node) throws  Exception{
        for(var curr: node.pars.lst){
            curr.visitP(this);
            var currTemp = tempCounter - 1;
            code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.PARAM, currTemp));
        }
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.CALL, 0, node.name.name)); // процедура ничего не возвращает
    }

    public void visitFuncCall(ASTNodes.FuncCallNode node) throws  Exception{
        for(var curr: node.pars.lst){
            curr.visitP(this);
            var currTemp = tempCounter - 1;
            code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.PARAM, currTemp));
        }
        var resultTemp = tempCounter - 1;
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.CALL, resultTemp, node.name.name));
    }

    public void Stop(){
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.STOP));
        labelAddresses.clear();
        for(var i = 0; i < code.size(); i++)
            if(code.get(i).command == ThreeAddressCode.Commands.LABEL)
                labelAddresses.put(code.get(i).label, i);
    }
}
