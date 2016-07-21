import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
/*************************************************************
 * 
 * This is the main class used to keep track of information for identifiers. Each identifier is given its own identifier object
 * from this class. Its attributes are comprised of the things we want to track about identifiers:
 * 
 * 	-the line on which the identifier was declared
 *  -the identifier's type
 *  -the identifier's name
 *  -the scope in which the identifier was declared (an instance of ScopeInfo)
 *  -an arraylist of integers representing occurrences of an identifier on a line equal to the value of the arraylist's nodes.
 *  	-for example, if an identifier X occurred once on line 2 and twice on line 3, the arraylist would look like:
 *  		-lineUse[0] = 2, lineUse[1] = 3, lineUse[2] = 3
 *  
 * Logic implemented in the methods in this class organizes and aggregates the contents of the array list and does other necessary processing to
 * create the string printed by the printString method. The printString method is what we call from csxLiteNode for each identifier after traversing 
 * the AST to print a line for each identifier.
 *
 */

public class identifierInfo {
	
	int declaredLine;											//*RAS - the line on which an idenfitier was declared
	String identifierType;										//*RAS - the idenfitier's type
	String identifierName;										//*RAS - the idenfitier's name
	ScopeInfo declaredScope;									//*RAS - the scope in which the idenfitier was declared
	ArrayList<Integer> lineUse = new ArrayList<Integer>();		//*RAS - an arraylist of lines on which an identifier was uses, with more than one of 
																//*RAS - the same line number value if an identifer was used more than once on a line
	public String printString() {																									//*RAS The main tag
		String returnString=declaredLine + ": " + identifierName + "(" + identifierType + "): " + buildLineStrings() + "\n";		//to print a line of 
		return returnString;																										//information for each
	   }																															//identifier.
	
	public String getName(String Type) {											//*RAS This is used to return either "int" or "bool"
		if (Type.startsWith("i")) Type = "int";										//*RAS to represent an identifier's type.
		if (Type.startsWith("b")) Type = "bool";
	    return Type;
	};
	
	void setLineInfo(Integer lineNumber){											//*RAS This adds a node to the arraylist for each use of an identifier
		lineUse.add(lineNumber);													//*RAS after declaration.
	}
	
	public String buildLineStrings(){												//*RAS This is the entry point for the method that builds a string of
		String completeLineString = "";												//*RAS usage information for a given identifier. It uses a new arraylist
		ArrayList<Integer> uniqueLineSet = removeDuplicates(lineUse);				//*RAS called uniqueLineSet, which stores the same contents of the lineUse
		for(int i = 0; i < uniqueLineSet.size(); i++){								//*RAS arraylist, but with redundancies removed. This represents a unique
			if (completeLineString == "")											//*RAS list of lines on which an identifier occurs. For each line on which
				completeLineString = buildLineString(uniqueLineSet.get(i));			//*RAS an identifier is used, we call buildLineString (without the "s" at
			else completeLineString = completeLineString + ", " + buildLineString(uniqueLineSet.get(i));	//*RAS the end) to build the string of how many
			}																		//*RAS times the identifier was used on that line. The uses of an identifier
		return completeLineString;													//*RAS on various lines are concatenated to produce completeLineString
	}																				//*RAS (which will look something like "9, 10(2)".
	
	public String buildLineString(Integer lineNumber){								//*RAS This method takes a line number as input and calculates the
		String lineString = "";														//*RAS number of times an identifier was used on that line, formatting
		for(int i = 0; i < lineUse.size(); i++){									//*RAS to include parentheses with the number of uses on that line if
			if (getNumberOfUsesOnLine(lineNumber) == 1){							//*RAS greater than 1.
				lineString = Integer.toString(lineNumber);
			}
			else if (getNumberOfUsesOnLine(lineNumber) > 1){
				lineString = Integer.toString(lineNumber) + "(" + getNumberOfUsesOnLine(lineNumber) + ")";
			}
		}
		return lineString;
	}
	
	public Integer getNumberOfUsesOnLine(int lineNumber) {							//*RAS This is called by buildLineString to help with tallying up
		Integer usesOnLine=0;														//*RAS the number of times an identifier was used on a given line.
		for(int i = 0; i < lineUse.size(); i++){
			if (lineUse.get(i) == lineNumber){
				usesOnLine = usesOnLine + 1;
			}
		}
	    return usesOnLine;											//*RAS
	}
	
	public ArrayList<Integer> removeDuplicates(ArrayList<Integer> lineUse){			//*RAS This is called to remove duplicates from the lineUse arraylist
		ArrayList<Integer> uniqueLineList = new ArrayList<>();						//*RAS so we can have a unique set of lines on which to calculate use,
		HashSet<Integer> set = new HashSet<>();										//*RAS the calculation of which uses the original lineUse arraylist in
		for (Integer i : lineUse) {													//*RAS the buildLineString and getNumberOfUsesOnLine methods.
		    if (!set.contains(i)) {
		    	uniqueLineList.add(i);
		    	set.add(i);
		    }
		}
		return uniqueLineList;
	}
}