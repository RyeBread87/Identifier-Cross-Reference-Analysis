*************************************************************
*                   Homework #1 for Ryan Smith:             *
*				Identifier Cross-Reference Analysis			*
*************************************************************

Written by Ryan Smith.
Contact rasmith9@wisc.edu (or rsmith@epic.com).
with any problems relating to JLex.

To implement this cross-reference analysis I made the following changes:
-Created - identifierInfo
	-This is a class whose objects represent identifiers. Its attributes represent the things we want to track about identifiers in order to print the required information about them (line on which the identifier was declared, identifier name, identifier type, occurrences of the identifier on each line).
-Updated - P1
	-I updated this to call a method in identifierInfo which prints a line of information for each identifier. Before this I kick off buildCrossReferences in the csxLiteNode.
-Updated - ast (several classes)
	- I updated all locations that used to have a countDeclsAndUses method to instead have buildCrossReferences methods. These grab more detailed information about identiers as we traverse the AST. We retain ScopeInfo objects which house hashtables in order to correctly distinguish identifiers with the same name by tying them to the line on which they were declared.
-Updated - ScopeInfo
	-I updated this method to have an additional attribute - a hashtable that maps identifier names to the line on which they were declared. The chaining structure of these scopes is retained and used.

The new class - identifierInfo - has methods which calculate the number of times a method was used on a given line. This result contributes to the final string that is printed by this class's printString method to render identifier information in the desired format.
