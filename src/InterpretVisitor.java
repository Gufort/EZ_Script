public class InterpretVisitor implements ASTNodes.IVisitor<Object>{
    @Override public Object visitNode(ASTNodes.Node node) throws Exception { return null; }
    @Override public Object visitExprNode(ASTNodes.ExprNode node) throws Exception { return null; }
    @Override public Object visitStatementNode(ASTNodes.StatementNode node) throws Exception { return null; }
    @Override public Object visitBinOp(ASTNodes.BinOpNode node) throws Exception{
        var left = node.left.visit(this);
        var right = node.right.visit(this);
        var sit = 0;
        if(right instanceof Double)
            sit++;
        else if(right instanceof Boolean)
            sit += 2;
        if(left instanceof Double)
            sit += 3;
        else if(left instanceof Boolean)
            sit += 6;

        switch(node.op){
            case PLUS -> {
                switch(sit){
                    case 4: return (double)left + (double)right;
                    case 1: return (int)left + (double)right;
                    case 3: return (double)left + (int)right;
                    case 0: return (int)left + (int)right;
                }
            }
            case MINUS -> {
                switch(sit){
                    case 4: return (double)left - (double)right;
                    case 1: return (int)left - (double)right;
                    case 3: return (double)left - (int)right;
                    case 0: return (int)left - (int)right;
                }
            }
            case MULTIPLE ->  {
                switch(sit){
                    case 4: return (double)left * (double)right;
                    case 1: return (int)left * (double)right;
                    case 3: return (double)left * (int)right;
                    case 0: return (int)left * (int)right;
                }
            }
            case DIVIDE ->  {
                switch(sit){
                    case 4: return (double)left / (double)right;
                    case 1: return (int)left / (double)right;
                    case 3: return (double)left / (double)right;
                    case 0: return (int)left / (double)right;
                }
            }
            case EQUAL -> {
                switch(sit){
                    case 4: return (double)left == (double)right;
                    case 1: return (int)left == (double)right;
                    case 3: return (double)left == (double)right;
                    case 0: return (int)left == (double)right;
                    case 8: return (boolean)left == (boolean)right;
                    default: CompilerException.semanticError("Операция == не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case NOTEQUAL -> {
                switch(sit){
                    case 4: return (double)left != (double)right;
                    case 1: return (int)left != (double)right;
                    case 3: return (int)left != (double)right;
                    case 0: return (int)left != (double)right;
                    case 8: return (boolean)left != (boolean)right;
                    default: CompilerException.semanticError("Операция != не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case LESS -> {
                switch(sit){
                    case 4: return (double)left < (double)right;
                    case 1: return (int)left < (double)right;
                    case 3: return (int)left < (double)right;
                    case 0: return (int)left < (double)right;
                    default: CompilerException.semanticError("Операция < не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case LESSEQUAL -> {
                switch(sit){
                    case 4: return (double)left <= (double)right;
                    case 1: return (int)left <= (double)right;
                    case 3: return (int)left <= (double)right;
                    case 0: return (int)left <= (double)right;
                    default: CompilerException.semanticError("Операция <= не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case GREATER -> {
                switch(sit){
                    case 4: return (double)left > (double)right;
                    case 1: return (int)left > (double)right;
                    case 3: return (int)left > (double)right;
                    case 0: return (int)left > (double)right;
                    default: CompilerException.semanticError("Операция > не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case GREATEREQUAL -> {
                switch(sit){
                    case 4: return (double)left >= (double)right;
                    case 1: return (int)left >= (double)right;
                    case 3: return (int)left >= (double)right;
                    case 0: return (int)left >= (double)right;
                    default: CompilerException.semanticError("Операция >= не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case AND -> {
                switch(sit){
                    case 8: return (boolean)left && (boolean)right;
                    default: CompilerException.semanticError("Операция && не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case OR -> {
                switch(sit){
                    case 8: return (boolean)left || (boolean)right;
                    default: CompilerException.semanticError("Операция || не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
        }
        return null;
    }
    @Override public Object visitStatementList(ASTNodes.StatementListNode stl) throws Exception {
        for(var curr: stl.statements)
            curr.visit(this);
        return null;
    }
    @Override public Object visitExprList(ASTNodes.ExprListNode elt) throws Exception {
        return null;
    }
    @Override public Object visitInt(ASTNodes.IntNode node) throws Exception {
        return node.value;
    }
    @Override public Object visitDouble(ASTNodes.DoubleNode node) throws Exception {
        return node.value;
    }
    @Override public Object visitId(ASTNodes.IdNode node) throws Exception {
        return SymbolTable.SymTable.get(node.name).runtimeValue;
    }
    @Override public Object visitAssign(ASTNodes.AssignNode node) throws Exception {
        var value = node.expr.visit(this);
        SymbolTable.SymTable.get(node.id.name).runtimeValue = (SymbolTable.RuntimeValue) value;
        return null;
    }
    @Override
    public Object visitAssignOperation(ASTNodes.AssignOperationNode ass) throws Exception {
        return null;
    }
    @Override public Object visitIf(ASTNodes.IfNode node) throws Exception {
        var cond = node.cond.visit(this);
        if((boolean)cond)
            node.then.visit(this);
        else if(node.elseif != null)
            node.elseif.visit(this);
        return null;
    }
    @Override public Object visitWhile(ASTNodes.WhileNode node) throws Exception {
        while((boolean)node.cond.visit(this))
            node.stat.visit(this);
        return null;
    }
    @Override public Object visitFor(ASTNodes.ForNode node) throws Exception {
        return null;
    }
    @Override public Object visitProcCall(ASTNodes.ProcCallNode node) throws Exception {
        return null;
    }
    @Override public Object visitFuncCall(ASTNodes.FuncCallNode node) throws Exception {
        return null;
    }
}
