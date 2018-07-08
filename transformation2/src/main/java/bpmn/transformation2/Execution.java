package bpmn.transformation2;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class Execution {

    
    public String input;
    public ArrayList<Model> startingModels;
    public Report report;
    public ArrayList<Parameter> parameters;
    public String log;

    public Execution (String input) {

    	//From input, let's get parameters and 

    	this.input = input;
    	
    }



    /**
     * TODO does this have to be static?
     * TODO TEST THIS
     * @return 
     * @return 
     * @throws XPathExpressionException 
     */
    public static boolean compareModels(Model a, Model b) throws XPathExpressionException {

	System.out.println("I'm comparing two models.");

	TravelAgency taA = new TravelAgency(a);
	TravelAgency taB = new TravelAgency(b);

	ArrayList<ArrayList<Element>> pA = taA.paths;
	ArrayList<ArrayList<Element>> pB = taB.paths;

	//First of all, if the two models have a different number of paths, we can stop
	//right here knowing that they must be different
	if (pA.size() != pB.size()) {
	    System.out.println("The two models have a different number of paths so they must be different.");
	    return false;
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
			foundAMatch = false;
		    }
		}
	    }
	    
	    //if I have gone through all paths in B and I havent found a match, the two models are different
	    if (foundAMatch == false) {
		return false;
	    }
	}

	//if I haven't returned false up until now, then it must mean that the two models are the same
	return true;
    }

    //TODO do I also have to compare flows instead of just nodes?
    //I guess I do.
    //But maybe I dont because I have no rule that changes just flows without changing nodes.
    /**
     * This method changes an array of elements into an array of tag types
     * @param path
     * @return
     */
    private static ArrayList<String> fromElementToTypes (ArrayList<Element> path){

	ArrayList<String> sequenceOfTypes = new ArrayList<String>();

	for (Element element : path) {
	    sequenceOfTypes.add(element.getTagName());
	}

	return sequenceOfTypes;
    }



    public void execute() {
	// TODO Auto-generated method stub
	
    }



}
