package bpmn.transformation2;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.w3c.dom.Element;

public class Execution {

    public String log;
    public String input;
    public ArrayList<Model> startingModels;

    public Execution (String input) {

    }

    /**
     * TODO does this have to be static?
     * @return 
     */
    public static void compareModels(Model a, Model b) {
	boolean same = true;
	//TravelAgency taa = new TravelAgency(a);
	//TravelAgency tab = new TravelAgency(b);
	
	
	
	if (same) {
	    System.out.println("Those models are the same " );    
	} else if (!same) {
	    System.out.println("Those models are NOT the same " );
	}
	
    }

    //TODO do I also have to compare flows instead of just nodes?
    //I guess I do.
    /**
     * This method changes an array of elements into an array of tag types
     * @param path
     * @return
     */
    private ArrayList<String> fromElementToTypes (ArrayList<Element> path){
	
	ArrayList<String> sequenceOfTypes = new ArrayList<String>();
	
	for (Element element : path) {
	    sequenceOfTypes.add(element.getTagName());
	}
	
	return sequenceOfTypes;
    }



}
