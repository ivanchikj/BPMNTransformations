package bpmn.transformation2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<Model> startingModels;
    private ArrayList<Parameter> parameters;
    private boolean recursive;
    public ArrayList<Transformation> activities;
    public ArrayList<Model> resultingModels;
    public Report report;
    public String destinationPath;

    public Execution (String input) throws IOException, SAXException, ParserConfigurationException {

	//From input, let's get parameters and an array of models
	this.input = input;
	this.parameters = new ArrayList<Parameter>();
	this.startingModels = new ArrayList<Model>();
	this.report = new Report();
	
	String[] pathAndParameters = separatePathAndParameters(input);

	System.out.println("path: " + pathAndParameters[0]);
	System.out.println("params: " + pathAndParameters[1]);

	this.path = pathAndParameters[0];
	findParameters(pathAndParameters[1]);
	initializeModels(path);
	printParams();
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
	//let's ignore also uppercase / lowercase istances
	input = input.toLowerCase();

	//in case there is a whole folder:
	if (!input.contains(".bpmn.xml")) {
	    //System.out.println("whole folder. I will use '/' to distinguish path from params");
	    path = input.substring(0, input.lastIndexOf('/') + 1);
	    params = input.substring(input.lastIndexOf('/') + 1);

	} else if (input.contains(".bpmn.xml")){
	    path = input.substring(0, input.indexOf(".bpmn.xml") + 9);
	    params = input.substring(input.indexOf(".bpmn.xml") + 9); //getting the part after the dash
	}

	String[] pathAndParams = {path, params};
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

	while (str.contains("-") && str.indexOf("-") != str.lastIndexOf("-") ) {
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
	
	if (str.contains("-")) { //let's add the last one:
	    String lastParam = str.substring(str.indexOf("-")+1);
	    paramStrings.add(lastParam);
	}

	System.out.println("I've finished separating the parameters in different strings");

	for (String param : paramStrings) {
	    if (param.contains("*")) { // it means we have to use a different contstructor because we have an aggregateBy param
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
		System.out.println("      Param " + parameter.parameter);
	    }
	    if (parameter.aggregateBy != 0) {
		System.out.println("      Param " + parameter.parameter + ", aggregateBy: " + parameter.aggregateBy);
	    }
	}
    }

    public void printModels() {
	System.out.println("I found those starting models: ");
	for (Model model : startingModels) {
	    System.out.println(model.path);
	}
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
