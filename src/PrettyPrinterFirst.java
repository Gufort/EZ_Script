public class PrettyPrinterFirst implements ASTNodes.IVisitor<String> {
    private int indent = 0;

    public String ind(){ return " ".repeat(indent); }
    public String indInc(){
        var res = "";
        indent += 4;
        return res;
    }
    public String indDec(){
        var res = "";
        indent -= 4;
        return res;
    }

    @Override
    public String visitNode(ASTNodes.Node node) {
        return ind() + node.visit(this);
    }
    @Override
    public String visitExprNode(ASTNodes.ExprNode node) {
        return node.visit(this);
    }
    @Override
    public String visitStatementNode(ASTNodes.StatementNode node) {
        return ind() + node.visit(this);
    }
    @Override
    public String visitBinOp(ASTNodes.BinOpNode node) {
        return node.left.visit(this) + " " + ASTNodes.operationToStr.get(node.op) + " " + node.right.visit(this);
    }
    @Override
    public String visitStatementList(ASTNodes.StatementListNode stl) {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < stl.statements.size(); i++) {
            var curr = stl.statements.get(i);
            res.append(curr.visit(this));
            if(i < stl.statements.size() - 1) {
                res.append(";").append('\n');
            }
        }

        res.append(ind());
        return res.toString();
    }
    @Override
    public String visitExprList(ASTNodes.ExprListNode stl) {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < stl.lst.size(); i++) {
            if(i > 0) res.append(", ");
            res.append(ind() + stl.lst.get(i).visit(this));
        }
        return res.toString();
    }
    @Override
    public String visitInt(ASTNodes.IntNode node) {
        return String.valueOf(node.value);
    }
    @Override
    public String visitDouble(ASTNodes.DoubleNode node) {
        return String.valueOf(node.value);
    }
    @Override
    public String visitId(ASTNodes.IdNode node) {
        return node.name.toString();
    }
    @Override
    public String visitAssign(ASTNodes.AssignNode node) {
        return ind() + node.id.name + " = " + node.expr.visit(this);
    }
    @Override
    public String visitAssignPlus(ASTNodes.AssignPlusNode node) {
        return ind() + node.id.name + " += " + node.expr.visit(this);
    }
    @Override
    public String visitIf(ASTNodes.IfNode node) {
        StringBuilder res = new StringBuilder();
        res.append(ind()).append("if").append("(").append(node.cond.visit(this)).append(")").append('\n').append('{').append(indInc()).append('\n').append(node.then.visit(this)).append(indDec()).append('\n').append('}').append('\n');
        if(node.elseif != null){
            res.append(ind()).append("else").append('\n').append('{').append(indInc()).append('\n').append(node.elseif.visit(this))
                    .append(indDec()).append('\n').append('}');
        }
        return res.toString();
    }
    @Override
    public String visitWhile(ASTNodes.WhileNode node) {
        StringBuilder res = new StringBuilder();
        res.append(ind()).append("while").append('(').append(node.cond.visit(this)).append(") \n");
        res.append(ind()).append('{').append('\n');
        indInc();
        res.append(node.stat.visit(this)).append("\n");
        indDec();
        res.append(ind()).append("}");
        return res.toString();
    }
    @Override
    public String visitProcCall(ASTNodes.ProcCallNode node) {
        if (node.pars == null || node.pars.lst.isEmpty()) {
            return ind() + node.name.name + "();";
        }

        StringBuilder res = new StringBuilder();
        res.append(ind()).append(node.name.name).append("(");

        for(int i = 0; i < node.pars.lst.size(); i++){
            if(i > 0) res.append(", ");
            res.append(node.pars.lst.get(i).visit(this));
        }

        res.append(")");
        return res.toString();
    }
    @Override
    public String visitFuncCall(ASTNodes.FuncCallNode node) {
        if (node.pars == null || node.pars.lst.isEmpty()) {
            return ind() + node.name.name + "()";
        }

        StringBuilder res = new StringBuilder();
        res.append(ind()).append(node.name.name).append("(");

        for(int i = 0; i < node.pars.lst.size(); i++){
            if(i > 0) res.append(", ");
            res.append(node.pars.lst.get(i).visit(this));
        }

        res.append(")");
        return res.toString();
    }
}
