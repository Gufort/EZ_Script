public class PrettyPrinterSecond implements ASTNodes.IVisitor<String>{
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
        return node.left.visit(this) + " " + node.op + " " + node.right.visit(this);
    }
    @Override
    public String visitStatementList(ASTNodes.StatementListNode lst) {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < lst.statements.size(); i++){
            if(i > 0) res.append("\n");
            res.append(lst.statements.get(i).visit(this));
        }

        return res.toString();
    }
    @Override
    public String visitExprList(ASTNodes.ExprListNode lst) {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < lst.lst.size(); i++){
            if(i > 0) res.append(", ");
            res.append(ind() + lst.lst.get(i).visit(this));
        }

        return res.toString();
    }
    @Override
    public String visitInt(ASTNodes.IntNode node) {
        return Integer.toString(node.value);
    }
    @Override
    public String visitDouble(ASTNodes.DoubleNode node) {
        return Double.toString(node.value);
    }
    @Override
    public String visitId(ASTNodes.IdNode node) {
        return node.name.toString();
    }
    @Override
    public String visitAssign(ASTNodes.AssignNode node) {
        return ind() + node.id.name + " := " + node.expr.visit(this);
    }
    @Override
    public String visitAssignPlus(ASTNodes.AssignPlusNode node) {
        return ind() + node.id.name + " += " + node.expr.visit(this);
    }
    @Override
    public String visitIf(ASTNodes.IfNode node) {
        StringBuilder res = new StringBuilder();

        res.append("if").append(" ").append(node.cond.visit(this)).append(" ").append("then:").append("\n")
                .append(indInc()).append(node.then.visit(this)).append(indDec()).append('\n');
        if(node.elseif != null){
            res.append("else:").append('\n').append(indInc()).append(node.elseif.visit(this)).append(indDec());
        }

        return res.toString();
    }
    @Override
    public String visitWhile(ASTNodes.WhileNode node) {
        StringBuilder res = new StringBuilder();

        res.append("while").append(" ").append(node.cond.visit(this)).append(" ").append("do:").append("\n")
                .append(indInc()).append(node.stat.visit(this)).append(indDec());

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
}
