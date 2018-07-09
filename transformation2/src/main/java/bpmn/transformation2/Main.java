package bpmn.transformation2;
import java.util.Scanner;

public class Main{


    private static String input;
    public static String[] validParameters = {"1", "2" ,"3", "4", "R1", "R2", "R3", "R4"};
    
    
    public static void main(String[] args){

	//String input = "./TestGraphs/rule1parallel.bpmn.xml";
	//String input = "./TestGraphs/rule1recursive.bpmn.xml";
	//String input = "./TestGraphs/rule2exclusiveConditions.bpmn.xml";
	//String input = "./TestGraphs/Rule3a.bpmn.xml";
	//String input = "./TestGraphs/Rule3b.bpmn.xml";
	//String input = "./TestGraphs/Rule4a.bpmn.xml";
	//String input = "./TestGraphs/Rule4b.bpmn.xml";
	//String input = "./TestGraphs/Rule4c.bpmn.xml";
	//String input = "./TestGraphs/TravelingTest.bpmn.xml";
	input = "./TestGraphs/multiple/"; //Testing more than one file.
	
	input = ""; //default behavior when not testing

	analyzeInput(input); 


    }
    
    /**
     * Analyze input and decide
     * If we want to ask the user or not (maybe we are testing an hardcoded input)
     * If we want to print the user guide or not
     * @param input
     */
    private static void analyzeInput(String input) {
	if ( input.equals("") ){ //This is always expected to be true except when testing something.
	    input = askForInput();
	    analyzeInput(input);
	}
	
	if (input.equals("help")) {
	    printHelp();
	    input = askForInput();
	    analyzeInput(input);
	} else { 
	    Execution execution = new Execution(input);  
	    execution.execute(); //TODO decide if execution should execute automatically
	}
	
    }
    
    
    private static void printHelp() {
	
	System.out.println(" - - - - - BPMN TRANSFORMATION TOOL - - - - - ");
	System.out.println("The program accepts BPMN 2.0 models provided in a .bpmn.xml format ");
	System.out.println("You can either provide a single file or a whole folder, in which case all of the .bpmn.xml files contained will be treated.");
	System.out.println("The resulting files will be saved in a subfolder of the current path called 'output' alongside a report of the execution.");
	System.out.println("List of possible parameters: ");
	for (int i = 0; i < validParameters.length; i++ ) {
	    System.out.print("-" + validParameters[i] + " ");
	}
	
	
	//TODO
	//NOTE it's better if the list of parameters is extrapolated from the actual array of strings, otherwise we could
	//have differences between the list in help and the actual list

    }

    /**
     * Asks the user for input
     * @return user input as a string
     */
    private static String askForInput() {
	Scanner reader = new Scanner(System.in);
	System.out.println("Please enter the path of the file(s) you want to transform. "
		+ "Type 'help' to display the list of parameters.");
	String input = reader.next();
	reader.close();
	return input;
    }
}
