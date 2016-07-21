import java.io.*;
import java_cup.runtime.*;
// CS536 Spring 2015, project 1 (count identifier definitions and uses on
//  a per scope basis for CSX Lite programs).
// A scope is the entire program, or a block (delimited by "{" and "}").
// Scopes may nest.

// In your solution of this project you will need to replace the call
//  to countDeclsAndUses (at the bottom of the program) with a call
//  to the method you write to do a cross-reference analysis

public class P1 {    
  public static void
  main(String args[]) throws java.io.IOException {
	
	System.out.println ("Full Name: Ryan Smith");  												//*RAS - printing,
	System.out.println ("Student ID Number (NetID): rasmith9");									//*RAS - student ID number,
	System.out.println ("File Being Analyzed: " + args[0]);										//*RAS - and file being analyzed
	
    // Test that a program name appears on the command line.
	if (args.length != 1) {
       		System.out.println(
			"Error: Input file must be named on command line." ); 
		System.exit(-1);
    	}

	
    	java.io.FileInputStream yyin = null;
    	
        // Open the file named on the command line. yyin will reference it.
    	try {
    		yyin = new java.io.FileInputStream(args[0]);
    	} catch (FileNotFoundException notFound){
       		System.out.println ("Error: unable to open input file.");
		System.exit(-1);
    	}

    Scanner.init(yyin); // Initialize Scanner class that will read and scan yyin

    //Create a parser that will parse the tokens returned by the scanner
    parser csxParser = new parser(); 

    Symbol root=null;
    
    // Call the parser. If the parse is successful, root will point
    //  to the root of the AST (abstract syntax tree) the papser builds
    try {
    	root = csxParser.parse(); // do the parse
    	System.out.println ("CSX Lite program parsed correctly.");
	
    } catch (Exception e) {
    	System.out.println ("Compilation terminated due to syntax errors.");
    	System.exit(0);
    }
    
    // Print out a listing of the program just parsed. This is done using an
    //  "unparser". A unparser reverses the parsing process, transforming an AST
    //  for a program into a textual representation of the program. This mechanism
    //  is useful in verifying that the AST for a program is correct.
    // The unparsing is done using the visitor mechanism, which we will discuss in class.
    System.out.println ("Here is its unparsing:");
    Unparsing unparse = new Unparsing();
     unparse.visit((csxLiteNode) root.value,0);
 
    //To do the identifier declaration and use analysis, we call countDeclsAndUses
    // in the root node of the AST. This method creates the necessary data structures
    // and walks the AST, calling countDeclsAndUses as necessary in various subtrees.
    // When the entire AST is traversed (and analyzed), the data structures built are
    // converted to textual form and returned to the caller.
     
    System.out.println ("\n\nHere is an identifier cross-reference analysis for "+
    		args[0]+ ":");
    System.out.println (((csxLiteNode)root.value).buildCrossReferences());						// *RAS - call to buildCrossReferences
    
    return;
    }
}
