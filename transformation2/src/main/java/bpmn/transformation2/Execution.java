package bpmn.transformation2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.engine.impl.cmd.ExecuteFilterCountCmd;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Execution {


    public String input;
    private String path;
    private ArrayList<Model> startingModels;
    private ArrayList<Parameter> parameters;
    public ArrayList<Transformation> activities;
    public ArrayList<Model> resultingModels;
    public Report report;
    //TODO is the log necessary?    	public String log;
    public String destinationPath;

    public Execution (String input) throws IOException, SAXException, ParserConfigurationException {

	//From input, let's get parameters and an array of models
	this.input = input;

	String[] pathAndParameters = separatePathAndParameters(input);
	this.path = pathAndParameters[0];
	findParameters(pathAndParameters[1]);
	initializeModels(path);
	Report report = new Report();
	//TODO initialize the report

    }

    /**
     * TODO what if the path of a file contains " -" ??
     * @param input: user-provided input String
     * @return the path part of the string will be in position [0] while the params in string form will be in [1]
     */
    private String[] separatePathAndParameters (String input) {
	String path;
	String params = "";

	//in case there are no parameters:
	if (!input.contains(" -")) {
	    System.out.println("no parameters");
	    path = input;
	} else {
	    path = input.substring(0, input.indexOf(" -")); //getting the part before the dash
	    params = input.substring(input.indexOf(" -")); //getting the part after the dash
	}

	String[] pathAndParams = {path, params};
	System.out.println(pathAndParams[0]);
	System.out.println(pathAndParams[1]);

	return pathAndParams;
    }

    public void initializeModels (String path) throws IOException, SAXException, ParserConfigurationException {

	if (path.contains(".bpmn.xml")) {
	    //it means we are getting a single file
	    Model model = new Model(path); //let's create a new model out of the path
	    startingModels.add(model); //and add it to our list

	} else {
	    //it means we are getting a folder
	    System.out.println("Opening folder at: " + path);
	    List<String> bmpnXMLFiles = new ArrayList<String>();
	    File dir = new File(path);
	    System.out.println("I've found " + dir.listFiles().length + " files with the extension '.bpmn.xml'");
	    for (File file : dir.listFiles()) {
		if (file.getName().endsWith((".bpmn.xml"))) {
		    Model model = new Model(file.getPath()); //let's create a new model out of the path
		    startingModels.add(model); //and add it to our list
		}
	    }
	}
    }




    //TODO make it more resistant to user mistakes, or remember to specify in the help to add a 'space' before every parameter
	private void findParameters(String paramsWholeString) {
	    
	//let's separate the parameters between each other
	ArrayList<String> paramStrings = new ArrayList<String>();

	//first, let's remove the first space:
	paramsWholeString = paramsWholeString.substring(1);
	//System.out.println("i have removed the empty space and now it looks like this: " + paramsString);

	while (paramsWholeString.contains("-")) {
	    String param;
	    if (paramsWholeString.contains(" ")) {
		//it means it is not the last parameter
		param = paramsWholeString.substring(paramsWholeString.indexOf("-"), paramsWholeString.indexOf(" "));
		//System.out.println("param i have found " + param);
		paramsWholeString = paramsWholeString.substring(param.length()+1);
		paramStrings.add(param.substring(1));
	    } else {
		//System.out.println("we have reached the last parameter");
		param = paramsWholeString;
		//System.out.println("param i have found " + param);
		paramStrings.add(param.substring(1));
		paramsWholeString = paramsWholeString.substring(param.length());

	    }

	}
	System.out.println("List of found parameters: ");
	for (String param : paramStrings) {
	    System.out.println("            		 " + param);
	    Parameter parameter = new Parameter(param); //Finally transforming our string into a Parameter
	    parameters.add(parameter); //Adding it to the list of parameters
	}
    }

    /**
     * TODO does this have to be static?
     * TODO TEST THIS
     * @return 
     * @return 
     * @throws XPathExpressionException 
     */
    static boolean modelsAreDifferent(Model a, Model b) throws XPathExpressionException {

	System.out.println("I'm comparing two models.");

	TravelAgency taA = new TravelAgency(a);
	TravelAgency taB = new TravelAgency(b);

	ArrayList<ArrayList<Element>> pA = taA.paths;
	ArrayList<ArrayList<Element>> pB = taB.paths;

	//First of all, if the two models have a different number of paths, we can stop
	//right here knowing that they must be different
	if (pA.size() != pB.size()) {
	    System.out.println("The two models have a different number of paths so they must be different.");
	    return true;
	}

	ArrayList<ArrayList<String>> pAInTypes = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> pBInTypes = new ArrayList<ArrayList<String>>();

	//transforming all the paths in A into sequences of types
	//instead of paths of 
	for (ArrayList<Element> path : pA) {
	    pAInTypes.add(fromElementToTypes(path));
	}

	//transforming all the paths in B into sequences of types
	//instead of paths of 
	for (ArrayList<Element> path : pB) {
	    pBInTypes.add(fromElementToTypes(path));
	}


	//let's see if for every type sequence in A i can find a match in B.
	for (ArrayList<String> pathA : pAInTypes) {

	    boolean foundAMatch = true; //let's assume the two paths will be equal unless we find a difference

	    for (ArrayList<String> pathB : pBInTypes) {

		//first of all we know that if they are of different length they cant be equal
		if (pathA.size() != pathB.size()) {
		    foundAMatch = false;
		}

		//let's go through every item and see if there's a difference between those two paths
		for (int i = 0; i < pathA.size(); i++) {

		    if (pathA.get(i) != pathB.get(i)) {
			System.out.println("This node is of a different type");
			//foundAMatch = false;
		    }
		}
	    }

	    //if I have gone through all paths in B and I havent found a match, the two models are different
	    if (foundAMatch == false) {
		System.out.println("The two models are different"); //UNLOCKTHIS for testing
		return true;
	    }
	}
	
	//if I haven't returned false up until now, then it must mean that the two models are the same
	System.out.println("The two models are the same"); //UNLOCKTHIS for testing
	return false;
    }

    //ASKANA do I also have to compare flows instead of just nodes?
    //I guess I do.
    //But maybe I don't because I have no rule that changes just flows without changing nodes.
    /**
     * This method changes an array of elements into an array of tag types
     * @param path a path in the BPMN sense from an arbitrary node to the end of the path.
     * @return
     */
    private static ArrayList<String> fromElementToTypes (ArrayList<Element> path){

	ArrayList<String> sequenceOfTypes = new ArrayList<String>();

	for (Element element : path) {
	    sequenceOfTypes.add(element.getTagName());
	}

	return sequenceOfTypes;
    }

    
    /**
     * 
     * @param model
     * @param filename
     * @param folderPath
     * @throws IOException 
     * @throws TransformerException 
     */
    public static void saveModelToFile(Model model, String filename, String folderPath, String rulesApplied) throws TransformerException {

	//TODO remember to add rules applied like here: String newFilePath = folderPath + "output/" + filename + rulesApplied + "TESTTESTTEST" + ".bpmn.xml";
	String outputFilepath = "./TestGraphs/output/Test.bpmn.xml"; //TODO this is wrong
	
	model.saveToFile(outputFilepath);
    }
    
    


}
