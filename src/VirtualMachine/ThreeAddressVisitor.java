package VirtualMachine;

import Basic.*;
import SemanticCheckLogic.CalcTypes;
import SemanticCheckLogic.SymbolTable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class ThreeAddressVisitor implements ASTNodes.IVisitorP{
    private int tempCounter = 100;
    private int labelCounter = 0;
    private Hashtable<String, Integer> labelAddresses = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> variableAddresses = new Hashtable<String, Integer>();
    private ArrayList<ThreeAddressCode> code = new ArrayList<ThreeAddressCode>();
    private int nextVariableAddress = 0;

    private Stack<Integer> resultStack = new Stack<Integer>();

    private int newTemp(){
        int temp = tempCounter++;
        return temp;
    }

    private void pushResult(int tempIndex) {
        resultStack.push(tempIndex);
    }

    private int popResult() {
        return resultStack.pop();
    }

    private String newLabel(){ return "L" + labelCounter++; }
    private int getVariableAddress(String name){
        if(variableAddresses.get(name) == null)
            variableAddresses.put(name, nextVariableAddress++);
        return variableAddresses.get(name);
    }
    public ArrayList<ThreeAddressCode> getCode(){
        return code;
    }

    @Override public void visitNode(ASTNodes.Node node) {}
    @Override public void visitExprNode(ASTNodes.ExprNode node) {}
    @Override public void visitStatementNode(ASTNodes.StatementNode node) {}

    @Override public void visitBinOp(ASTNodes.BinOpNode node) throws Exception{
        node.left.visitP(this);
        int left = popResult();

        node.right.visitP(this);
        int right = popResult();

        int res = newTemp();

        var leftType = CalcTypes.calcType(node.left);
        var rightType = CalcTypes.calcType(node.right);

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
            // Повторно выполняем операцию с преобразованными типами
            visitBinOpAfterConversion(node, left, right, res);
            return;
        }

        else if(leftType == SymbolTable.SemanticType.DoubleType && rightType == SymbolTable.SemanticType.IntType){
            int convert = newTemp();
            code.add(ThreeAddressCode.createConvert(ThreeAddressCode.Commands.CONITR, right, convert));
            right = convert;
            rightType = SymbolTable.SemanticType.DoubleType;
            visitBinOpAfterConversion(node, left, right, res);
            return;
        }

        pushResult(res);
    }

    private void visitBinOpAfterConversion(ASTNodes.BinOpNode node, int left, int right, int res) throws Exception {
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
        pushResult(res);
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
        pushResult(temp);
    }

    public void visitDouble(ASTNodes.DoubleNode node) throws  Exception{
        int temp = newTemp();
        code.add(ThreeAddressCode.createConst(ThreeAddressCode.Commands.RCAAS, temp, node.value));
        pushResult(temp);
    }

    public void visitId(ASTNodes.IdNode node) throws  Exception{
        int temp = newTemp();
        int address = getVariableAddress(node.name);
        var varType = CalcTypes.calcType(node);
        ThreeAddressCode.Commands command = varType == SymbolTable.SemanticType.DoubleType ?
                ThreeAddressCode.Commands.RASS : ThreeAddressCode.Commands.IASS;
        code.add(ThreeAddressCode.createAssign(command, temp, address));
        pushResult(temp);
    }

    public void visitAssign(ASTNodes.AssignNode node) throws  Exception{
        node.expr.visitP(this);
        int exprResult = popResult();
        int address = getVariableAddress(node.id.name);
        var exprType = CalcTypes.calcType(node.expr);

        if(exprType == SymbolTable.SemanticType.DoubleType)
            code.add(ThreeAddressCode.createAssign(ThreeAddressCode.Commands.RASS, address, exprResult));
        else if(exprType == SymbolTable.SemanticType.IntType)
            code.add(ThreeAddressCode.createAssign(ThreeAddressCode.Commands.IASS, address, exprResult));
        else if(exprType == SymbolTable.SemanticType.BoolType)
            code.add(ThreeAddressCode.createAssign(ThreeAddressCode.Commands.BASS, address, exprResult));

        pushResult(address);
    }

    public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws  Exception{
        int address = getVariableAddress(node.id.name);
        var varType = CalcTypes.calcType(node.id);

        int currentValueTemp = newTemp();
        ThreeAddressCode.Commands loadCommand = varType == SymbolTable.SemanticType.DoubleType ?
                ThreeAddressCode.Commands.RASS : ThreeAddressCode.Commands.IASS;
        code.add(ThreeAddressCode.createAssign(loadCommand, currentValueTemp, address));

        // Вычисляем выражение
        node.expr.visitP(this);
        int exprResult = popResult();
        var exprType = CalcTypes.calcType(node.expr);

        if(varType == SymbolTable.SemanticType.DoubleType && exprType == SymbolTable.SemanticType.IntType){
            int convert = newTemp();
            code.add(ThreeAddressCode.createConvert(ThreeAddressCode.Commands.CONITR, exprResult, convert));
            exprResult = convert;
        }

        int operationResult = newTemp();

        if(varType == SymbolTable.SemanticType.DoubleType){
            switch (node.op) {
                case '+':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RADD, currentValueTemp, exprResult, operationResult));
                    break;
                case '-':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RSUB, currentValueTemp, exprResult, operationResult));
                    break;
                case '*':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RMUL, currentValueTemp, exprResult, operationResult));
                    break;
                case '/':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.RDIV, currentValueTemp, exprResult, operationResult));
                    break;
            }
        }
        else if(varType == SymbolTable.SemanticType.IntType){
            switch (node.op) {
                case '+':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IADD, currentValueTemp, exprResult, operationResult));
                    break;
                case '-':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.ISUB, currentValueTemp, exprResult, operationResult));
                    break;
                case '*':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IMUL, currentValueTemp, exprResult, operationResult));
                    break;
                case '/':
                    code.add(ThreeAddressCode.createBinary(ThreeAddressCode.Commands.IDIV, currentValueTemp, exprResult, operationResult));
                    break;
            }
        }

        ThreeAddressCode.Commands storeCommand = varType == SymbolTable.SemanticType.DoubleType ?
                ThreeAddressCode.Commands.RASS : ThreeAddressCode.Commands.IASS;
        code.add(ThreeAddressCode.createAssign(storeCommand, address, operationResult));
        pushResult(address);
    }

    public void visitIf(ASTNodes.IfNode node) throws  Exception{
        node.cond.visitP(this);
        int condResult = popResult();
        var elseLabel = newLabel();
        var endLabel = newLabel();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.IIF, condResult, elseLabel));
        node.then.visitP(this);
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.GOTO, endLabel));

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, elseLabel));
        if(node.elseif != null)
            node.elseif.visitP(this);

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, endLabel));
    }

    public void visitWhile(ASTNodes.WhileNode node) throws  Exception{
        var startLabel = newLabel();
        var endLabel = newLabel();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, startLabel));

        node.cond.visitP(this);
        int condResult = popResult();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.IFN, condResult, endLabel));
        node.stat.visitP(this);
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.GOTO, startLabel));

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, endLabel));
    }

    public void visitFor(ASTNodes.ForNode node) throws  Exception{
        var startLabel = newLabel();
        var endLabel = newLabel();

        node.start.visitP(this);
        popResult();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, startLabel));

        node.condition.visitP(this);
        int condResult = popResult();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.IFN, condResult, endLabel));

        node.body.visitP(this);
        popResult();

        node.increment.visitP(this);
        popResult();

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.GOTO, startLabel));

        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.LABEL, endLabel));
    }

    public void visitProcCall(ASTNodes.ProcCallNode node) throws  Exception{
        for(var curr: node.pars.lst){
            curr.visitP(this);
            int currTemp = popResult();
            code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.PARAM, currTemp));
        }
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.CALL, 0, node.name.name));
    }

    public void visitFuncCall(ASTNodes.FuncCallNode node) throws  Exception{
        for(var curr: node.pars.lst){
            curr.visitP(this);
            int currTemp = popResult();
            code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.PARAM, currTemp));
        }
        int resultTemp = newTemp();
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.CALL, resultTemp, node.name.name));
        pushResult(resultTemp);
    }

    public void Stop(){
        code.add(ThreeAddressCode.create(ThreeAddressCode.Commands.STOP));
        labelAddresses.clear();
        for(var i = 0; i < code.size(); i++)
            if(code.get(i).command == ThreeAddressCode.Commands.LABEL)
                labelAddresses.put(code.get(i).label, i);
    }
}