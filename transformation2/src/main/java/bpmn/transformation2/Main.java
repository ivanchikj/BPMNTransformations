package bpmn.transformation2;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Main{


    private static String input; 
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException{

	
	
	//input = ""; //default behavior when not testing
	
	input = "testing"; //UNLOCKTHIS used only to go in the manual testing method
	
	analyzeInput(input);
    }

    
    /**
     * This method allows me to easily test any public method
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws IOException 
     * @throws XPathExpressionException 
     * @throws TransformerException 
     */
    public static void manualTest() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException {
	//String input = "./TestGraphs/rule1parallel.bpmn.xml";
	//String input = "./TestGraphs/rule1recursive.bpmn.xml";
	//String input = "./TestGraphs/rule2exclusiveConditions.bpmn.xml";
	//String input = "./TestGraphs/Rule3a.bpmn.xml";
	//String input = "./TestGraphs/Rule3b.bpmn.xml";
	
	//String input = "./TestGraphs/Rule4b.bpmn.xml";
	//String input = "./TestGraphs/Rule4c.bpmn.xml";
	//String input = "./TestGraphs/TravelingTest.bpmn.xml";
	//input = "./TestGraphs/multiple/"; //Testing more than one file.
	
	
	
	
	
//	String original = "./TestGraphs/DifferenceDetectingTest/Original.bpmn.xml"; 
//	String different1 = "./TestGraphs/DifferenceDetectingTest/slightlyDifferent.bpmn.xml";
//	String different2 = "./TestGraphs/DifferenceDetectingTest/different.bpmn.xml";
//	String same = "./TestGraphs/DifferenceDetectingTest/same.bpmn.xml";
//	String identical = "./TestGraphs/DifferenceDetectingTest/originalVariant.bpmn.xml";
//	
//	Model origina = new Model(original);
//	Model diff1 = new Model(different1);	
//	Model diff2 = new Model(different2);
//	Model sameModel = new Model(same);
//	Model identicModel = new Model(identical);
//	
//	TravelAgency ta1 = new TravelAgency(origina);
//	ta1.printNumberOfPaths();
//	TravelAgency ta2 = new TravelAgency(sameModel);
//	ta2.printNumberOfPaths();
//	
//	ta1.printMandatoryDeepSuccessors();
//	ta2.printMandatoryDeepSuccessors();
//	
//	System.out.println("SONO DIVERSI: " + Execution.modelsAreDifferent(origina, sameModel));
//	System.out.println("SONO DIVERSI: " + Execution.modelsAreDifferent(origina, origina));
//	System.out.println("SONO DIVERSI: " + Execution.modelsAreDifferent(origina, diff1));
//	System.out.println("SONO DIVERSI: " + Execution.modelsAreDifferent(origina, diff2));
//	System.out.println("SONO DIVERSI: " + Execution.modelsAreDifferent(origina, identicModel));
	
	
	String input = "./TestGraphs/ -r1*3-2-3";
	Execution execution = new Execution(input);
	
	
	//ExecutionTest execution = new ExecutionTest();
	//execution.Execute();
	
	
	
//	String m4a = "./TestGraphs/Rule4a.bpmn.xml";
//	Model test4a = new Model(m4a);
//	Model output = Rule4.a(test4a);
	
	
//	String m4b = "./TestGraphs/Rule4b.bpmn.xml";
//	Model test4b = new Model(m4b);
//	Model output = Rule4.b(test4b);

	
//	String m4c = "./TestGraphs/Rule4c.bpmn.xml";
//	Model test4c = new Model(m4c);
//	Model output = Rule4.c(test4c);
//	
	
//	String m4aR = "./TestGraphs/Rule4aR.bpmn.xml";
//	Model test4aR = new Model(m4aR);
//	Model output = Reverse4.a(test4aR);
	
//	String m4bR = "./TestGraphs/Rule4bR.bpmn.xml";
//	Model test4bR = new Model(m4bR);
//	Model output = Reverse4.b(test4bR);
	
//	String m4cR = "./TestGraphs/Rule4cR.bpmn.xml";
//	Model test4cR = new Model(m4cR);
//	Model output = Reverse4.c(test4cR);

//	String m3c = "./TestGraphs/Rule3cOneInsideTheOther.bpmn.xml";
//	Model test3c = new Model(m3c);
//	Model output = Rule3.c(test3c);

	
//	String m1R = "./TestGraphs/rule1R.bpmn.xml";
//	Model test1R = new Model(m1R);
//	Model output = Reverse1.applyRule(test1R, 3); //NOTE that the int can be whatever positive number
	
//	String m2R = "./TestGraphs/Rule2R.bpmn.xml";
//	Model test2R = new Model(m2R);
//	Model output = Reverse2.applyRule(test2R, 2);

//	String patSplitTest = "./TestGraphs/pathTest.bpmn.xml";
//	Model testPath = new Model(patSplitTest);
//	TravelAgency tAgency = new TravelAgency(testPath);
//	tAgency.printPaths();
//	tAgency.printMandatoryDeepSuccessors();
	
//	String m3cR = "./TestGraphs/Rule3cR.bpmn.xml";
//	Model test3cR = new Model(m3cR);
//	Model output = Reverse3.c(test3cR, 2);
//	
	
	//GETHPATH TEST:
//	String gPath = "./TestGraphs/GetPath.bpmn.xml";
//	Model testgPath = new Model(gPath);
//	TravelAgency tAgency = new TravelAgency(testgPath);
//	tAgency.printPaths();
//	tAgency.printMandatoryDeepSuccessors();
	
	 
	
	//Execution.saveModelToFile(output, "TODO", "TODO", "TODO");
	
	
	
	
//	Model orgnl = new Model(original);
//	Model diff1 = new Model(different1);
//	Model diff2 = new Model(different2);
//	Model sameModel = new Model(same);
	//Model identicModel = new Model(identical);
	//Element startingPointElement = orgnl.findElemById("StartEvent_1");
	
	//TravelAgency travelAgency = new TravelAgency(orgnl);
	
	//travelAgency.getPaths();
	
	//Execution.modelsAreDifferent(orgnl, identicModel);
	
	
	
	
	
    }
    
    
    /**
     * Analyze input and decide
     * If we want to ask the user or not (maybe we are testing an hardcoded input)
     * If we want to print the user guide or not
     * @param input user-provided String containing the path of the input model(s) and facultative parameters
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws IOException 
     * @throws XPathExpressionException 
     * @throws TransformerException 
     */
    private static void analyzeInput(String input) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException {
	System.out.println("Analizyng user input: " + input);
	
	if ( input.equals("") ){ //This is always expected to be true except when testing something.
	    input = askForInput();
	    analyzeInput(input);
	} else if (input.equals("testing")) {
	    System.out.println("This is a manual test!");
	    manualTest();
	    reader.close();    
	} else if (input.equalsIgnoreCase("help")) {
	    printHelp();
	    input = askForInput();
	    analyzeInput(input);
	} else { 
	    Execution execution = new Execution(input);  
	    //execution.execute(); //TODO decide if execution should execute automatically
	}
	reader.close();
    }


    private static void printHelp() {
	System.out.println();
	System.out.println(" - - - - - BPMN TRANSFORMATION TOOL GUIDE - - - - - ");
	System.out.println();
	System.out.println("The program accepts BPMN 2.0 models provided in a .bpmn.xml format ");
	System.out.println("You can either provide a single file or a whole folder,");
	System.out.println("in which case all of the .bpmn.xml files contained will be treated.");
	System.out.println();
	System.out.println("The resulting files will be saved in a subfolder of the current "
		+ "path called 'output' alongside a report of the execution.");
	System.out.println();
	System.out.println();
	System.out.print("LIST OF POSSIBLE PARAMETERS: ");
	System.out.println();
	for (int i = 0; i < Parameter.validParameters.length; i++ ) {
	    System.out.print("-" + Parameter.validParameters[i] + " ");
	}//the list of parameters is extrapolated from the actual array of strings, otherwise we could
	//have differences between the list in help and the actual list
	System.out.println();
	System.out.println();
	System.out.println("THE PARAMETER 'aggregateBY':");
	System.out.println();
	System.out.println("in the case of the rules 1R, 2R and 3cR it's possible to decide how to aggregate the");
	System.out.println("sequenceFlows of the original gateway. E.g. 2by2, 3by3 etc. The default value is 2by2");
	System.out.println("To decide how to aggregate the gateways simply add and asterisk '*' after the name of the rule");
	System.out.println();
	System.out.println();
	System.out.println("TURNING ON PERMUTATIONS: "); //TODO ADD A CHECK FOR THIS
	System.out.println();
	System.out.println("In case you want to execute multiple permutation of the rules you have selected");
	System.out.println("(i.e. apply those same rules in different orders and combinations");
	System.out.println("Just add a question mark '?' in the input somewhere in between the parameters ");
	System.out.println("WARNING: DO NOT TURN ON PERMUTATIONS WHEN A RULE AND ITS OPPOSITE ARE BOTH IN THE POOL OF RULES TO BE APPLIED TO AVOID INFINITE LOOPS.");
	System.out.println();
	System.out.println();
	System.out.println("BLOCKING RECURSIVE BEHAVIOR: "); //TODO ADD A CHECK FOR THIS
	System.out.println();
	System.out.println("In case you want to block recursive behavior");
	System.out.println("(i.e. when the program tries to apply the same rule(s))");
	System.out.println("Just add an exclamation mark '!' in the input somewhere in between the parameters ");
	System.out.println();
	System.out.println();
	System.out.println("EXAMPLES:");
	//TODO you probably can avoid this limitation by transforming every string in one without spaces and then changing the algorithm that you have
	System.out.println();
	System.out.println("	 './ExampleFolder/exampleModel.bpmn.xml -1 -R2*3 -3a'" ); //TODO così non funziona. Fai in modo che gli spazi non contino
	System.out.println("	 './ExampleFolder/-1-R2*5'" );
	System.out.println("	 './ExampleFolder/'" );
	System.out.println("	 './ExampleFolder/ -2-4!'" );
	
	System.out.println();
	System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - ");
	
	//TODO add more details on the behavior of the program, like in the slides. (EG mention recursivity, the behavior when having no parameters etc)
	//TODO insert an URL with a detailed guide (could be the readme on github)
	//In that guide the transformations done by the rules (with Ana's images) should be displayed
    }

    /**
     * Asks the user for input
     * @return user input as a string
     */
    private static String askForInput() {
	
	System.out.println("Please enter the path of the file(s) you want to transform.");
	System.out.println("Type 'help' to display the list of parameters or if this is your first time using the program.");
	String input = reader.next();
	//reader.close();
	System.out.println("Reading user input: " + input);
	return input;
    }
}
