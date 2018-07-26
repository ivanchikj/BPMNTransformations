package bpmn.transformation2;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class Transformation {

    Model startingModel;
    Model resultingModel;
    Parameter parameter; //the rule it will be trying to apply
    boolean successful; //if the resulting models are different, it's a success, otherwise it's a failure.
    //TODO decide if that will be the way to decide if successful or fail.

    //TODO if the resulting file does not pass the BPMN test, then it's not a success it's a fail. Thus the program will fail
    //gracefully, but that will be written in the report differently.
    public String newName;
    
    
    
    public Transformation(Model startingModel, Parameter parameter) throws Exception {

	this.startingModel = startingModel;
	this.parameter = parameter;
	boolean successful = false; //default value
	applyTheRule();

    }

    /**
     * TODO decide if there's a better way than having this long list of 'ifs'
     * @return
     * @throws Exception
     */
    private void applyTheRule() throws Exception {
	if (parameter.equals("1")) {
	    resultingModel = Rule1.applyRule(startingModel);
	}

	if (parameter.equals("2")) {
	    resultingModel = Rule2.applyRule(startingModel);
	    
	}

	if (parameter.equals("3")) {
	    resultingModel = Rule3.all(startingModel);
	}

	if (parameter.equals("3a")) {
	    resultingModel = Rule3.a(startingModel);
	}

	if (parameter.equals("3b")) {
	    resultingModel = Rule3.b(startingModel);
	}

	if (parameter.equals("3c")) {
	    resultingModel = Rule3.c(startingModel);
	}

	if (parameter.equals("4")) {
	    resultingModel = Rule4.all(startingModel);
	}

	if (parameter.equals("4a")) {
	    resultingModel = Rule4.a(startingModel);
	}

	if (parameter.equals("4b")) {
	    resultingModel = Rule4.b(startingModel);
	}

	if (parameter.equals("4c")) {
	    resultingModel = Rule4.c(startingModel);
	}

	if (parameter.equals("R1")) {
	    //resultingModel = Reverse1.applyRule(startingModel, aggregateBy)
	}

	if (parameter.equals("R2")) {
	    System.err.println("TODO");
	}

	if (parameter.equals("R3")) {
	    System.err.println("TODO");
	}

	if (parameter.equals("R3a")) {
	    resultingModel = Reverse3.a(startingModel);
	}

	if (parameter.equals("R3b")) {
	    resultingModel = Reverse3.b(startingModel);
	}

	if (parameter.equals("R3c")) {
	    //resultingModel = Reverse3.c(startingModel, aggregateBy)
	}

	if (parameter.equals("R4")) {
	    System.err.println("TODO");
	}

	if (parameter.equals("R4a")) {
	    resultingModel = Reverse4.a(startingModel);
	}

	if (parameter.equals("R4b")) {
	    resultingModel = Reverse4.b(startingModel);
	}

	if (parameter.equals("R4c")) {
	    resultingModel = Reverse4.c(startingModel);
	}

	boolean valid = true; //TODO what if the resulting file is not valid? Insert the test here.
	//if the models are different, it means that the transformation was successful
	boolean different = modelsAreDifferent(startingModel, resultingModel);

	if (different && valid) {
	    successful = true;
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


}
