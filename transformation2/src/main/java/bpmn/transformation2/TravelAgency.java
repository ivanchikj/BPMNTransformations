package bpmn.transformation2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.text.AbstractDocument.LeafElement;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.xml.xpath.XPathExpressionException;

import org.apache.ibatis.annotations.ConstructorArgs;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class TravelAgency {

    public Model model;
    public ArrayList<ArrayList<Element>> paths;
    public ArrayList<String> visited;
    public Element startingPoint;
    public ArrayList<Element> startingList;
    public ArrayList<Element> mandatoryDeepSuccessors;
    //TODO decide if the starting point has to be a class variable

    /**
     * Constructor with startingPoint
     * TODO 
     * Then initialize the variables using the methods I wrote.
     * @param model
     * @throws XPathExpressionException 
     */
    public TravelAgency (Model model, Element startingPoint) throws XPathExpressionException {

	this.model = model;
	this.startingPoint = startingPoint;

	this.startingList = fromElementToArrayList();

	this.paths = new ArrayList<ArrayList<Element>>();
	
	visited = new ArrayList<String>();

	getPathsFrom(startingList);
	
	mandatoryDeepSuccessors = new ArrayList<Element>();
	getMandatoryDeepSuccessors();

    }

    public TravelAgency (Model model) throws XPathExpressionException {

	this.model = model;

	this.visited = new ArrayList<String>();

	this.paths = new ArrayList<ArrayList<Element>>();

	getPaths();

	mandatoryDeepSuccessors = new ArrayList<Element>();
	getMandatoryDeepSuccessors();

    }

    /**
     * From a starting point element, get all the possible paths
     * @throws XPathExpressionException 
     */
    public ArrayList <Element> getPathsFrom (ArrayList<Element> past) throws XPathExpressionException{

	Element startingPoint = getLast(past);

	//printVisited();

	visit(startingPoint);

	ArrayList<Element> immediateSuccessors = model.getSuccessors(startingPoint);

	if (immediateSuccessors.size() > 1) {
	    System.out.println("THIS IS A SPLIT !");

	    for (Element successor : immediateSuccessors ) {

		if (iHaveNotVisited(successor)){    

		    ArrayList<Element> newPast = (ArrayList<Element>) past.clone();
		    newPast.add(successor);
		    getPathsFrom(newPast);

		}
	    }

	} else if (immediateSuccessors.size() == 1){
	    System.out.println("THIS IS NOT A SPLIT !");
	    immediateSuccessors.get(0);
	    past.add(immediateSuccessors.get(0));
	    getPathsFrom(past);

	} else if (immediateSuccessors.size() == 0){
	    System.out.println("THIS IS THE END");
	    paths.add(past);
	    printPaths();
	}

	return past;
    }


    private boolean iHaveNotVisited(Element element) {
	return !this.visited.contains(element.getAttribute("id"));
    }

    /**
     * Used only for testing
     */
    private void printVisited() {
	System.out.println("VISITED SO FAR: ");
	for (String id : visited) {
	    System.out.println("               " + id);
	}
    }

    public void visit (Element element) {
	System.out.println("I'm visiting: " + element.getAttribute("name"));
	visited.add(element.getAttribute("id"));
    }
    /**
     * This serves when starting getPathsFrom() from an arbitrary element instead of the start
     * I need to transform it into an arrayList to be able to use it as an input to
     * getPathsFrom()
     * @return
     */
    public ArrayList<Element> fromElementToArrayList(){
	ArrayList<Element> list = new ArrayList<Element>();
	list.add(startingPoint);
	return list;
    }


    public Element getLast (ArrayList<Element> past) {
	return past.get(past.size()-1);
    }



    /**
     * From the list of possible paths, discover mandatory elements
     * The way we do it is we copy the first path in the paths array
     * Then we check which elements are repeated in all paths. Those that are not repeated get removed.
     * Note that we use IDs instead of element types, as otherwise we could have
     * the impression that two paths have a deepSuccessor in common while it is not the same exact element.
     * @throws XPathExpressionException 
     */
    public ArrayList <Element> getMandatoryDeepSuccessors () throws XPathExpressionException{

	ArrayList<String> firstPathIDs = new ArrayList<String>();

	//Adding the first path's element in ID form
	firstPathIDs = fromElementsToIDs(paths.get(0));

	//Going through all the other paths
	for (int i = 1; i < paths.size(); i++) {

	    ArrayList<String> idForm = fromElementsToIDs(paths.get(i));

	    //Only keeping those that are repeated in all paths
	    firstPathIDs.retainAll(idForm);

	}

	mandatoryDeepSuccessors = fromIDsToElements(firstPathIDs);

	//of course we remove the starting point because we know that will always be in common:
	mandatoryDeepSuccessors.remove(0);

	printMandatoryDeepSuccessors(); //for testing

	return mandatoryDeepSuccessors;

    }

    private ArrayList<String> fromElementsToIDs (ArrayList<Element> elementList ) {

	ArrayList<String> idList = new ArrayList<String>();

	for (Element element : elementList) {
	    idList.add(element.getAttribute("id"));
	}
	return idList;
    }


    private ArrayList<Element> fromIDsToElements (ArrayList<String> idList) throws XPathExpressionException {
	ArrayList<Element> elementList = new ArrayList<Element>();

	for (String id : idList) {
	    elementList.add(model.findElemById(id));    
	}

	return elementList;
    }

    /**
     * TODO this now prints names instead of ID's
     */   
    private void printMandatoryDeepSuccessors() {
	System.out.println("PRINTING THE MANDATORY DEEP SUCCESSORS: ");
	for (Element element : mandatoryDeepSuccessors) {
	    System.out.println("                       " + element.getAttribute("name"));
	}


    }


    /**
     * Navigate a model from its starting point
     * @throws XPathExpressionException
     */
    public void getPaths() throws XPathExpressionException {
	//Finding the start element
	Element start = (Element) model.doc.getElementsByTagName("bpmn:startEvent").item(0);
	//Getting paths from the start element:

	ArrayList<Element> startingList = new ArrayList<Element>();

	startingList.add(start);
	getPathsFrom(startingList);

    }

    /**
     * Used for testing
     * That's why for now I'm using names instead of id, but that
     * shall be changed (TODO)
     */
    public void printPaths() {
	System.out.println("======= PRINTING PATHS =======");

	for (ArrayList<Element> path : paths) {
	    System.out.println("-----a path:-----");
	    for (Element element : path) {
		System.out.println(element.getAttribute("name"));
	    }

	}
    }

}
