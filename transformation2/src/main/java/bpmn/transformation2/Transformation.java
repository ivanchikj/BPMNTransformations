package bpmn.transformation2;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

import camundafeel.de.odysseus.el.tree.impl.Scanner.ScanException;

public class Transformation {

    Model startingModel;
    Model resultingModel;
    Parameter parameter; //the rule it will be trying to apply
    boolean successful; //if the resulting models are different, it's a success, otherwise it's a failure.
    //TODO decide if that will be the way to decide if successful or fail.

    //TODO if the resulting file does not pass the BPMN test, then it's not a success it's a fail. Thus the program will fail
    //gracefully, but that will be written in the report differently.
    public String newName;
    



    public Transformation(Model startingModel, Parameter parameter, Execution execution) throws Exception {

	this.startingModel = new Model(startingModel.path);
	this.parameter = parameter;
	boolean successful = false; //default value
	applyTheRule();
	
	boolean different = modelsAreDifferent(startingModel, resultingModel);
	
	boolean valid = true; // TODO ADD A CHECK
	
	if (different && valid) {
	    this.successful = true;
	    System.out.println("The transformation has been successfull!");
	}
    }

    /**
     * TODO decide if there's a better way than having this long list of 'ifs'
     * @return
     * @throws Exception
     */
    private void applyTheRule() throws Exception {
	if (parameter.rule.equals("1")) {
	    System.out.println();
	    System.out.println("IM APPLIYING RULE 1");
	    resultingModel = Rule1.applyRule(startingModel);
	}

	if (parameter.rule.equals("2")) {
	    resultingModel = Rule2.applyRule(startingModel);

	}

	if (parameter.rule.equals("3")) {
	    resultingModel = Rule3.all(startingModel);
	}

	if (parameter.rule.equals("3a")) {
	    resultingModel = Rule3.a(startingModel);
	}

	if (parameter.rule.equals("3b")) {
	    resultingModel = Rule3.b(startingModel);
	}

	if (parameter.rule.equals("3c")) {
	    resultingModel = Rule3.c(startingModel);
	}

	if (parameter.rule.equals("4")) {
	    resultingModel = Rule4.all(startingModel);
	}

	if (parameter.rule.equals("4a")) {
	    resultingModel = Rule4.a(startingModel);
	}

	if (parameter.rule.equals("4b")) {
	    resultingModel = Rule4.b(startingModel);
	}

	if (parameter.rule.equals("4c")) {
	    resultingModel = Rule4.c(startingModel);
	}

	if (parameter.rule.equals("R1")) {
	    resultingModel = Reverse1.applyRule(startingModel, parameter.aggregateBy);
	}

	if (parameter.rule.equals("R2")) {
	    resultingModel = Reverse2.applyRule(startingModel, parameter.aggregateBy);
	}

	if (parameter.rule.equals("R3")) {
	    resultingModel = Reverse3.applyRule(startingModel, parameter.aggregateBy);
	}

	if (parameter.rule.equals("R3a")) {
	    resultingModel = Reverse3.a(startingModel);
	}

	if (parameter.rule.equals("R3b")) {
	    resultingModel = Reverse3.b(startingModel);
	}

	if (parameter.rule.equals("R3c")) {
	    //resultingModel = Reverse3.c(startingModel, aggregateBy)
	}

	if (parameter.rule.equals("R4")) {
	    System.err.println("TODO");
	}

	if (parameter.rule.equals("R4a")) {
	    resultingModel = Reverse4.a(startingModel);
	}

	if (parameter.rule.equals("R4b")) {
	    resultingModel = Reverse4.b(startingModel);
	}

	if (parameter.rule.equals("R4c")) {
	    resultingModel = Reverse4.c(startingModel);
	}
	resultingModel.addRule(parameter);
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

	ArrayList<ArrayList<Element>> pathsA = taA.paths;
	ArrayList<ArrayList<Element>> pathsB = taB.paths;

	//First of all, if the two models have a different number of paths, we can stop
	//right here knowing that they must be different
	if (pathsA.size() != pathsB.size()) {
	    System.out.println("The two models have a different number of paths so they must be different.");
	    return true;
	}

	ArrayList<ArrayList<String>> pathsAInTypes = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> pathsBInTypes = new ArrayList<ArrayList<String>>();

	//transforming all the paths in A into sequences of types
	//instead of paths of 
	for (ArrayList<Element> path : pathsA) {
	    pathsAInTypes.add(fromElementToTypes(path));
	}

	//transforming all the paths in B into sequences of types
	//instead of paths of 
	for (ArrayList<Element> path : pathsB) {
	    pathsBInTypes.add(fromElementToTypes(path));
	}

	//let's see if for every type sequence in A i can find a match in B.
	for (ArrayList<String> pathInTypes : pathsAInTypes) {
	    if (foundAMatch(pathInTypes, pathsBInTypes)) {
		//found a match for this path. Lets keep analyzing the other paths.
	    } else {
		System.out.println("The models: " + a.path + " and " + b.path + " are different");
		return true; //i have one path which doesn't have a match. The models must be different.
	    }
	}
	System.out.println("The models: " + a.path + " and " + b.path + " are equal");
	return false;
    }


    private static boolean foundAMatch (ArrayList<String> pathInTypes, ArrayList<ArrayList<String>> AllPathsInTypes) {

	//let's go through every path in allPathsInTypes and see if there's one identical to pathInTypes
	for (ArrayList<String> path : AllPathsInTypes) {

	    if (path.equals(pathInTypes)) {
		return true;
	    }

	}

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

    
    
    public void save(String folder) throws TransformerException {
	resultingModel.saveToFile(folder);
	
    }
    
    
}