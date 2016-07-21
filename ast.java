import java.util.*;
import java.lang.*;
/*************************************************************
 * 
 * After scanning  and parsing, the structure and content of a program is represented as an
 * Abstract Syntax Tree (AST).
 * The root of the AST represents the entire program. Subtrees represent various
 * components, like declarations and statements.
 * Program translation and analysis is done by recursively walking the AST, starting
 * at the root.
 * CSX will use a variety of AST nodes since it contains a variety of structures (declarations,
 * methods, statements, expressions, etc.).
 * The AST nodes defined here represent CSX Lite, a small subset of CSX. Hence many fewer
 * nodes are needed.
 * 
 * The analysis implemented here counts the number of identifier declarations and uses
 * on a per scope basis. The entire program is one scope. A block (rooted by a blockNode)
 * is a local scope (delimited in the source program by a "{" and "}").
 * The method countDeclsAndUses implements this analysis. Each AST node has a definition of this
 * method. It may be an explicit definition intended especially for one particular node.
 * If an AST node has no local definition of countDeclsAndUses it inherits a definition from its
 * parent class. The class ASTNode (which is the ancestor of all AST nodes) has a default definition
 * of countDeclsAndUses. The definition is null (does nothing).
 *
 */
// abstract superclass; only subclasses are actually created
abstract class ASTNode {

	public final int 	linenum;
	public final int	colnum;
	
	ASTNode(){linenum=-1;colnum=-1;}
	ASTNode(int l,int c){linenum=l;colnum=c;}
	boolean   isNull(){return false;}; // Is this node null?

    	abstract void accept(Visitor v, int indent);// Will be defined in sub-classes    

	// default action on an AST node is to record no declarations and no identifier uses
	 
	 void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){ 				//*RAS
		return;
	}

};


// This node is used to root only CSXlite programs 
class csxLiteNode extends ASTNode {
	
   	public final fieldDeclsOption	progDecls;
	public final stmtsOption 	progStmts;
	private ScopeInfo  		 scopeList;
	String[] UsageArray;
	
	csxLiteNode(fieldDeclsOption decls, stmtsOption stmts, int line, int col){      
		super(line,col);
		progDecls=decls;
		progStmts=stmts;
		scopeList=null;
	}; 
	
	
	void accept(Visitor u, int indent){ u.visit(this,indent); }
	
	// This method begins the count declarations and uses analysis.
	//  It first creates a ScopeInfo node for the entire program.
	//  It then passes this ScopeInfo node to the declarations subtree and then
	//   the statements subtree. Visiting these two subtrees causes all identifier uses and
	//     declarations to be recognized and recorded in the list rooted by the ScopeInfo node.
	//  Finally, the information stored in the ScopeInfo list is converted to string form
	//   and returned to the caller of the analysis.
	 
	 String buildCrossReferences(){													//*RAS Here is where we kick things off, building cross references
		 String returnString="";													//*RAS for the AST before printing their information.
		 scopeList = new ScopeInfo(1,linenum);
		 ArrayList<identifierInfo> identifiers = new ArrayList<identifierInfo>();
		 //identifierInfo[] identifiers = new identifierInfo[1];
		 progDecls.buildCrossReferences(scopeList, identifiers);
		 //identifierInfo[] identifiers = new identifierInfo[scopeList.hashTablesSize()];
		 progStmts.buildCrossReferences(scopeList, identifiers);
		 //return scopeList.myToString(identifiers);
		 for (identifierInfo identifier : identifiers)
			 returnString = returnString + identifier.printString();
		 return returnString;
	}

};

abstract class fieldDeclsOption extends ASTNode{
	fieldDeclsOption(int line,int column){
		super(line,column);
	}
	fieldDeclsOption(){ super(); }
};

class fieldDeclsNode extends fieldDeclsOption {

	public final declNode		thisField;
	public final fieldDeclsOption 	moreFields;
	
	fieldDeclsNode(declNode d, fieldDeclsOption f, int line, int col){
		super(line,col);
		thisField=d;
		moreFields=f;
	}
	
	static nullFieldDeclsNode NULL = new nullFieldDeclsNode();

	void accept(Visitor u, int indent){ u.visit(this,indent);}
	
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){		//*RAS Build cross-references for this field and more fields
		thisField.buildCrossReferences(currentScope, identifiers);
		moreFields.buildCrossReferences(currentScope, identifiers);
		return;
	}
};

class nullFieldDeclsNode extends fieldDeclsOption {
	
	nullFieldDeclsNode(){};

	boolean   isNull(){return true;};

	void accept(Visitor u, int indent){ u.visit(this,indent);}

	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){		//*RAS
			return;
	}
};

// abstract superclass; only subclasses are actually created
abstract class declNode extends ASTNode {
	declNode(){super();};
	declNode(int l,int c){super(l,c);};
};


class varDeclNode extends declNode { 
	
	public final	identNode	varName;
	public final	typeNode 	varType;
	public final	exprOption 	initValue;
	
	varDeclNode(identNode id, typeNode t, exprOption e,
			int line, int col){
		super(line,col);
		varName=id;
		varType=t;
		initValue=e;
	}
	
	void accept(Visitor u, int indent){ u.visit(this,indent);}


	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){
		String typeString;															//*RAS Here we decorate an identifier that's been declared with
		currentScope.ident.put(varName.idname, linenum);							//*RAS information about its name, type, the scope in which it's
		identifierInfo identifier = new identifierInfo();							//*RAS information about its name, type, the scope in which it's
		identifier.identifierName = varName.idname;									//*RAS been declared, and the line number on which it was declared.
		identifier.declaredLine = linenum;
		identifier.declaredScope = currentScope;
		typeString = varType.toString();
		identifier.identifierType = identifier.getName(typeString);
		identifiers.add(identifier);
	}
	
	
};

abstract class typeNode extends ASTNode {
// abstract superclass; only subclasses are actually created
	typeNode(){super();};
	typeNode(int l,int c){super(l,c);};
	static nullTypeNode NULL = new nullTypeNode();
};

class nullTypeNode extends typeNode {

	nullTypeNode(){};

	boolean   isNull(){return true;};

	void accept(Visitor u, int indent){ u.visit(this,indent); }
};


class intTypeNode extends typeNode {
	intTypeNode(int line, int col){
		super(line,col);
	}

	void accept(Visitor u, int indent){ u.visit(this,indent); }
	
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){					//*RAS
		return;
	}
};


class boolTypeNode extends typeNode {
	boolTypeNode(int line, int col){
		super(line,col);
	}

	void accept(Visitor u, int indent){ u.visit(this,indent); }
};

//abstract superclass; only subclasses are actually created
abstract class stmtOption extends ASTNode {
	stmtOption(){super();};
	stmtOption(int l,int c){super(l,c);};
	//static nullStmtNode NULL = new nullStmtNode();
};

// abstract superclass; only subclasses are actually created
abstract class stmtNode extends stmtOption {
	stmtNode(){super();};
	stmtNode(int l,int c){super(l,c);};
	static nullStmtNode NULL = new nullStmtNode();
};

class nullStmtNode extends stmtOption {
	nullStmtNode(){};
	boolean   isNull(){return true;};
	void accept(Visitor u, int indent){ u.visit(this,indent);}
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){return;}			//*RAS
};

abstract class stmtsOption extends ASTNode{
	stmtsOption(int line,int column){
		super(line,column);
	}
	stmtsOption(){ super(); }
};

class stmtsNode extends stmtsOption { 
	public final stmtNode	    	thisStmt;
	public final stmtsOption 	moreStmts;

	stmtsNode(stmtNode stmt, stmtsOption stmts, int line, int col){
		super(line,col);
		thisStmt=stmt;
		moreStmts=stmts;
	};

	static nullStmtsNode NULL = new nullStmtsNode();
	
	void accept(Visitor u, int indent){ u.visit(this,indent);}
	
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){
		//Build cross-references in both subtrees:
			 thisStmt.buildCrossReferences(currentScope, identifiers);
			 moreStmts.buildCrossReferences(currentScope, identifiers);
			}
};


class nullStmtsNode extends stmtsOption {
	nullStmtsNode(){};
	boolean   isNull(){return true;};

	void accept(Visitor u, int indent){ u.visit(this,indent);}
	
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){return;}			//*RAS

};

class asgNode extends stmtNode {    

	public final identNode	target;
	public final exprNode 	source;
	
	asgNode(identNode n, exprNode e, int line, int col){       
		super(line,col);
		target=n;
		source=e;
	};
	
	void accept(Visitor u, int indent){ u.visit(this,indent);}
	
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){					//*RAS Here we collect a usage instance
		int currentVarDeclaredLine = 0;																			//*RAS for an identifier. We loop through
		if (currentScope.ident.get(target.idname) != null){														//*RAS scopes to find the right scope for
		currentVarDeclaredLine = currentScope.ident.get(target.idname);											//*RAS the current identifier (using the
		}																										//*RAS scope attribute I created for the
		else for (identifierInfo identifier : identifiers){														//*RAS identifierInfo object).
			if (identifier.declaredScope.ident.get(target.idname) != null){
				currentVarDeclaredLine = identifier.declaredScope.ident.get(target.idname);						//*RAS setLineInfo is the call to add
				}																								//*RAS a usage occurrence for a given line
		}																										//*RAS to the arraylist tracking this information.
		for (identifierInfo identifier : identifiers)
		    if (currentVarDeclaredLine == identifier.declaredLine){
		    	identifier.setLineInfo(linenum);															
		    }
		source.buildCrossReferences(currentScope, identifiers);													//*RAS Build cross-references for the
		}																										//*RAS source of the assignment
};


class ifThenNode extends stmtNode {
	
	public final exprNode 		condition;
	public final stmtNode 		thenPart;
	public final stmtOption 	elsePart;
	
	ifThenNode(exprNode e, stmtNode s1, stmtOption s2, int line, int col){
		super(line,col);
		condition=e;
		thenPart=s1;
		elsePart=s2;
	};
	
	void accept(Visitor u, int indent){ u.visit(this,indent);}
	
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){
		// build cross-references in control expression and then statement.
		// In CSX Lite the else statement is always null
		condition.buildCrossReferences(currentScope, identifiers);
		thenPart.buildCrossReferences(currentScope, identifiers);
		}
};


class blockNode extends stmtNode {
	
	public final fieldDeclsOption 	decls;  
	public final stmtsOption 	stmts;
	
	blockNode(fieldDeclsOption f, stmtsOption s, int line, int col){
		super(line,col);
		decls=f;
		stmts=s;
	}
	
	 void accept(Visitor u, int indent){ u.visit(this,indent);}
	 
	 void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){			//*RAS - new ScopeInfo nodes are created with
		 ScopeInfo  localScope = new ScopeInfo(linenum);												//*RAS - new blocks, just like with countDeclsAndUses.
		 ScopeInfo.append(currentScope,localScope);														//*RAS - We use scopes to associate with hashtables,
		 decls.buildCrossReferences(localScope, identifiers);											//*RAS - which we use down at leaf nodes to match
		 stmts.buildCrossReferences(localScope, identifiers);											//*RAS - identifier uses with their declarations.
	}
};


//abstract superclass; only subclasses are actually created
abstract class exprOption extends ASTNode {
	exprOption(){super();};
	exprOption(int l,int c){super(l,c);};
	//static nullStmtNode NULL = new nullStmtNode();
};

// abstract superclass; only subclasses are actually created
abstract class exprNode extends exprOption {
	exprNode(){super();};
	exprNode(int l,int c){super(l,c);};
	static nullExprNode NULL = new nullExprNode();
};

class nullExprNode extends exprOption {
	nullExprNode(){super();};
	boolean   isNull(){return true;};
	void accept(Visitor u, int indent){}
};

class binaryOpNode extends exprNode {
	
	public final exprNode 	leftOperand;
	public final exprNode 	rightOperand;
	public final int	operatorCode; // Token code of the operator
	
	binaryOpNode(exprNode e1, int op, exprNode e2, int line, int col){
		super(line,col);
		operatorCode=op;
		leftOperand=e1;
		rightOperand=e2;
	};

	void accept(Visitor u, int indent){ u.visit(this,indent);}
	 
		void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){				//*RAS Build cross-references for left and
			leftOperand.buildCrossReferences(currentScope, identifiers);										//*RAS right operands.
			rightOperand.buildCrossReferences(currentScope, identifiers);
};
}

class identNode extends exprNode {
	
	public final String 	idname;
	
	identNode(String identname, int line, int col){
		super(line,col);
		idname   = identname;
	};

	void accept(Visitor u, int indent){ u.visit(this,indent);}
	
	void buildCrossReferences(ScopeInfo currentScope, ArrayList<identifierInfo> identifiers){					//*RAS Here we collect a usage instance
		int currentVarDeclaredLine = 0;																			//*RAS for an identifier. We loop through
		if (currentScope.ident.get(idname) != null){															//*RAS scopes to find the right scope for
			currentVarDeclaredLine = currentScope.ident.get(idname);											//*RAS the current identifier (using the
			}																									//*RAS scope attribute I created for the
		else for (identifierInfo identifier : identifiers){														//*RAS identifierInfo object).
			if (identifier.declaredScope.ident.get(idname) != null){
				currentVarDeclaredLine = identifier.declaredScope.ident.get(idname);							//*RAS setLineInfo is the call to add
				}																								//*RAS a usage occurrence for a given line
		}																										//*RAS to the arraylist tracking this information.
		for (identifierInfo identifier : identifiers)
		    if (currentVarDeclaredLine == identifier.declaredLine){
		    	identifier.setLineInfo(linenum);
		    }
		}
};


class intLitNode extends exprNode {
	public final int 	intval;
	intLitNode(int val, int line, int col){
		super(line,col);
		intval=val;
	}

	void accept(Visitor u, int indent){ u.visit(this,indent);}
};
