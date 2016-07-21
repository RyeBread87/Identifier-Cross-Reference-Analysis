// Generic visit
abstract class Visitor {
	public void visit(ASTNode n,int indent){ 
		//System.out.println ("In  ASTNode visit\n");

		n.accept(this, indent);
	}
	
	abstract  void visit(csxLiteNode n,int indent);
	abstract  void visit(fieldDeclsNode n,int indent);
	abstract  void visit(nullFieldDeclsNode n,int indent);
	abstract  void visit(stmtsNode n,int indent);
	abstract  void visit(varDeclNode n,int indent);
	abstract  void visit(nullTypeNode n,int indent);
	abstract  void visit(intTypeNode n,int indent);
	abstract  void visit(boolTypeNode n,int indent);
	abstract  void visit(identNode n,int indent);
	abstract  void visit(asgNode n,int indent);
	abstract  void visit(intLitNode n,int indent);
	abstract  void visit(binaryOpNode n,int indent);
	abstract  void visit(nullStmtsNode n,int indent);
	abstract  void visit(ifThenNode n,int indent);
	abstract  void visit(blockNode n,int indent);








}  
