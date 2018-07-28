package bpmn.transformation2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.el.StaticFieldELResolver;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.engine.impl.cmd.ExecuteFilterCountCmd;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Execution {


    public String input;
    private String path;
    private String folderPath;
    private String outputPath;
    boolean folder;
    private ArrayList<Model> startingModels;
    private ArrayList<Parameter> parameters;
    private boolean recursive;
    private boolean permutations;
    public ArrayList<Transformation> activities;
    public ArrayList<Model> resultingModels;
    public Report report;
    public String destinationPath;

    public Execution (String input) throws Exception {

	//From input, let's get parameters and an array of models
	this.input = input;
	this.parameters = new ArrayList<Parameter>();
	this.startingModels = new ArrayList<Model>();
	this.resultingModels = new ArrayList<Model>();
	this.report = new Report();
	
	doesItLooksLikeAPath();
	searchForRecursive();
	searchForPermutations();

	String[] pathAndParameters = separatePathAndParameters(this.input);

	//System.out.println("path: " + pathAndParameters[0]); 
	//System.out.println("params: " + pathAndParameters[1]); //TODO cancella questi due sysout

	this.path = pathAndParameters[0];
	
	if (folder) {
	folderPath = path;    
	} else {
	this.folderPath = getFolderPath(path);
	}
	
	this.outputPath = folderPath + "output/";
	
	findParameters(pathAndParameters[1]);
	initializeModels(path);
	printExecutionStatus(); //UNLOCKTHIS used just for testing TODO this informations could be something to add in the report

	decideWhatToDo(); //(and do it)
	System.out.println("CIAONE: " + resultingModels.size());
	saveResultingModels();
	
	//CONTINUE FROM HERE. We need to decide how to proceed . Devo passare da EXECUTION a transformation.

    }

    
    private void saveResultingModels() throws TransformerException {
	
	for (Model m : resultingModels) {
	    saveModel(m, outputPath);
	}
	
    }

    
    /**
     * TODO SPIEGA PERCHË QUESTO METODO ESISTE
     * @param model
     * @param filename
     * @param folderPath
     * @throws IOException 
     * @throws TransformerException 
     */
    public void saveModel(Model model, String filename) throws TransformerException {
	
	//i need to add 'output/' in the new path
	String original = model.path;
	String name  = original.substring(original.lastIndexOf("/")+1);
	String folder = original.substring(0, original.lastIndexOf(name)) + "output/";
	
	System.out.println("name " + name);
	System.out.println("folder " + folder);
	model.saveToFile(folder+name);
	
    }
    

    

    //TODO maybe we can have a better check than just looking for a slash.
    private void doesItLooksLikeAPath() {
	
	if (!input.contains("/")) {
	    System.err.println("This doesn't look like a path");
	}
	
    }

    private String getFolderPath(String path) {
	
	String folderPath = path.substring(0, path.lastIndexOf("/") + 1);
	
	return folderPath;
    }

    private void searchForPermutations() {
	if (input.contains("?")) {
	    permutations = true;
	} else {
	    permutations = false;
	}
	input = input.replace("?", ""); //let's remember to delete the mark 
	//TODO is it possible the question mark is in the filename?
    }

    private void searchForRecursive() {
	if (input.contains("!")) {
	    recursive = false;
	} else {
	    recursive = true;
	}
	input = input.replace("!", ""); //let's remember to delete the exclamation mark 
	//TODO is it possible the exclamation mark is in the filename?
    }

    /**
     * TODO what if the path of a file contains " -" ??
     * @param input: user-provided input String
     * @return the path part of the string will be in position [0] while the params in string form will be in [1]
     */
    private String[] separatePathAndParameters (String input) {
	String path = "";
	String params = "";

	//let's ignore spaces
	input = input.replaceAll(" ", "");

	//in case there is a whole folder:
	if (!input.contains(".bpmn.xml")) {
	    //System.out.println("whole folder. I will use '/' to distinguish path from params");
	    path = input.substring(0, input.lastIndexOf('/') + 1);
	    params = input.substring(input.lastIndexOf('/') + 1);

	} else if (input.contains(".bpmn.xml")){
	    path = input.substring(0, input.indexOf(".bpmn.xml") + 9);
	    params = input.substring(input.indexOf(".bpmn.xml") + 9); //getting the part after the dash
	}

	String[] pathAndParams = {path, params.toLowerCase()}; 	//let's ignore also uppercase / lowercase istances for parameters
	input = input.toLowerCase();
	//System.out.println(pathAndParams[0]);
	//System.out.println(pathAndParams[1]);

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



    private void findParameters(String paramsWholeString) {
	String str = paramsWholeString;
	System.out.println("String : " + str);
	//let's separate the parameters between each other
	ArrayList<String> paramStrings = new ArrayList<String>();

	while (str.indexOf("-") != str.lastIndexOf("-") ) {
	    System.out.println(str);
	    String param;
	    int firstDash = str.indexOf("-") + 1;
	    int nextDash = str.indexOf("-", firstDash + 1);

	    param = str.substring(firstDash, nextDash);

	    //System.out.println("Parampapag " + param);

	    paramStrings.add(param);
	    System.out.println("str : " +  str);
	    str = str.substring(param.length()+1);
	    System.out.println("e adesso " +  str);
	}

	if (str.contains("-") && str.indexOf("-") == str.indexOf("-")) { //let's add the last one:
	    String lastParam = str.substring(str.indexOf("-")+1);
	    paramStrings.add(lastParam);
	}

	System.out.println("I've finished separating the parameters in different strings");

	for (String param : paramStrings) {
	    if (param.contains("*")) { // it means we have to use a different constructor because we have an aggregateBy param
		int aggregateBy = Integer.parseInt(param.substring(param.indexOf("*")+1));
		System.out.println("AggregateBY " + aggregateBy);
		param = param.substring(0, param.indexOf("*"));
		Parameter parameter = new Parameter(param, aggregateBy); //Finally transforming our string into a Parameter
		this.parameters.add(parameter); //Adding it to the list of parameters
	    } else {
		Parameter parameter = new Parameter(param); //Finally transforming our string into a Parameter
		this.parameters.add(parameter); //Adding it to the list of parameters	
	    }
	}
    }


    public void printParams() {
	System.out.println("I found those parameters: ");
	for (Parameter parameter : parameters) {
	    if (parameter.aggregateBy == 0) {
		System.out.println("      Param " + parameter.rule);
	    }
	    if (parameter.aggregateBy != 0) {
		System.out.println("      Param " + parameter.rule + ", aggregateBy: " + parameter.aggregateBy);
	    }
	}
	System.out.println();
    }

    public void printModels() {
	System.out.println();
	System.out.println("I found those starting models: ");
	for (Model model : startingModels) {
	    System.out.println("        " + model.path);
	}
	System.out.println();
    }

    public void printExecutionStatus() {
	System.out.println("EXECUTION SETTINGS: ");
	printModels();
	printParams();
	System.out.println("Recursive behavior: " + recursive);
	System.out.println("Permutations: " + permutations);
	System.out.println("Output Location: " + outputPath);
    }

    
    public void execute() throws Exception {
	decideWhatToDo();
    }

    private void decideWhatToDo() throws Exception {

	
	if (!permutations && !recursive) { //we have a simple order of rules to be applied to the various startingModels
	    System.out.println();
	    System.out.print("Simple execution with rules: ");
	    for (Parameter parameter : parameters) {
		System.out.print(parameter.rule);
	    }
	    for (Model startingModel : startingModels) {
		for (Parameter param : parameters) {
		    Transformation transf = new Transformation(startingModel, param, this);
		    if (transf.successful) {		
			resultingModels.add(transf.resultingModel);
		    }
		    
		}
	    }
	}
    }


    /**
     * TODO this works but has false positives.
     * This method wants to avoid having infinite loops of trying to apply a rule and 
     * then immediately afterwards its opposite etc. 
     * TODO this check is only applied when recursive behavior is on. Otherwise it's not needed because infinite 
     * loops can only happen when recursive behavior is on.
     * Moreover, it doesn't stop the program. It merely asks the user if he/she wishes to continue.
     * @param parameter1
     * @param parameter2
     * @return
     */
    private boolean areRulesOpposite ( Parameter parameter1, Parameter parameter2 ) {
	String p1 = parameter1.rule;
	String p2 = parameter2.rule;

	if ( p1.equals("r" + p2) || p2.equals("r" + p1) ) {
	    return true;
	}
	else return false;

    }


    private boolean continueAfterWarning ( String warning ) {
	System.out.println();
	System.out.println();
	System.out.println(" WARNING :" + warning);
	System.out.println();
	System.out.println(" Do you wish to continue?  - Y/N");
	Scanner scanner = new Scanner(System.in);
	String input = scanner.next().toLowerCase();

	if (input.equals("y")) {
	    return true;
	} else if (input.equals("n")) {
	    return false;
	} else {
	    System.out.println("You can only write - y/n");
	    return continueAfterWarning(warning); //Does this works?
	}

    }


}
