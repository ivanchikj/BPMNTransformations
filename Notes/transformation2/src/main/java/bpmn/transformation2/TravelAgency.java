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
    public ArrayList<String> toVisit;
    public Element startingPoint;
    public ArrayList<Element> startingList;
    public ArrayList<Element> mandatoryDeepSuccessors;
    public int pastID;
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
	toVisit = new ArrayList<String>();
	
	this.pastID = 0;
	
	//Past past = new Past(startingList, pastID++);
	
	//getPathsFrom(past);
	
	//getPathsFrom(startingList);
	getPaths(startingPoint);

	mandatoryDeepSuccessors = new ArrayList<Element>();
	getMandatoryDeepSuccessors();

	//printPaths();//UNLOCKTHIS for testing

    }
    /**
     *  TODO scrivi la docum
     *  Spiega che se non è specificato lo startingPoint, inizi dallo start
     * @param model
     * @throws XPathExpressionException
     */
    public TravelAgency (Model model) throws XPathExpressionException {

	this.model = model;

	this.visited = new ArrayList<String>();
	toVisit = new ArrayList<String>();

	this.paths = new ArrayList<ArrayList<Element>>();


	//Finding the start element
	Element start = (Element) model.doc.getElementsByTagName("bpmn:startEvent").item(0);

	this.startingPoint = start;

	this.startingList = fromElementToArrayList();

	//Past past = new Past(startingList, pastID++);
	
	//getPathsFrom(past);
	
	//getPathsFrom(startingList);
	getPaths(startingPoint);

	//getPaths(); TODO deletethis

	mandatoryDeepSuccessors = new ArrayList<Element>();
	getMandatoryDeepSuccessors();

	//printPaths();//UNLOCKTHIS for testing

    }

    
    private void getPaths(Element startingPoint) throws XPathExpressionException {
	ArrayList<Element> path = new ArrayList<Element>();
	getPathsFromPast(startingPoint, path);
    }
    

    private void getPathsFromPast(Element start, ArrayList<Element> past) throws XPathExpressionException { //TODO fai partire solo da uno startingPoint
	past.add(start);
	String id = start.getAttribute("id");
	System.out.println("I'm visiting element " + id);
	ArrayList<Element> successors = model.getSuccessors(start);
	
	if (successors.size() == 0) {
	    System.out.println("this is the end: " + id);
	    paths.add(past);
	} else {
	    for (Element successor : successors) {
		ArrayList<Element> newPastArrayList = (ArrayList<Element>) past.clone();
		getPathsFromPast(successor, newPastArrayList);
	    }
	}
	
    }
    


//    private void getPathsFrom (Past past) throws XPathExpressionException{
//	ArrayList<Element> pastArrayList = past.elements;
//	
//	Element startingPoint = getLast(pastArrayList);
//	String id = startingPoint.getAttribute("id");
//	visit(startingPoint);
//	printPast(past);
//	ArrayList<Element> successors = model.getSuccessors(startingPoint);
//
//	if (successors.size() == 1) {
//	    System.out.println(id + " IS NOT A SPLIT");
//	    Element successor = successors.get(0);
//	    pastArrayList.add(successor);
//	    getPathsFrom(past);
//	}
//
//	if (successors.size() == 0) {
//	    System.out.println(id + " IS THE END");
//	    paths.add(past.elements);
//	}
//
//	if (successors.size() > 1 ) {
//	    System.out.println(id + " IS A SPLIT");
//
//	    for (Element successor : successors) {
//		String succId = successor.getAttribute("id");
//		toVisit.add(succId);
//		System.out.println("I have to remember I want to start from : " + succId );
//	    }
//	    for (String ElId : toVisit) {
//		Element newSP = model.findElemById(ElId);
//		if (iHaveNotVisited(newSP)) {
//		    past.elements.remove(past.elements.size()-1);
//		    
//		    Past newPast = new Past(past.elements, pastID++);
//
//		    newPast.elements.add(newSP);
//
//		    getPathsFrom(newPast);
//		    
//		}
//	    }
//	}
//
//
//    }



    private void printPast(Past past) {
	System.out.println( "          My Past:" + past.id);
	for (Element element : past.elements) {
	    System.out.println("                    " + element.getAttribute("id"));
	}

    }



    //    /**
    //     * From a starting point element, get all the possible paths
    //     * @throws XPathExpressionException 
    //     */
    //    private ArrayList <Element> getPathsFrom(ArrayList<Element> past) throws XPathExpressionException{
    //
    //	Element startingPoint = getLast(past);
    //
    //	//printVisited(); //UNLOCKTHIS for testing
    //	visit(startingPoint);
    //
    //	ArrayList<Element> immediateSuccessors = model.getSuccessors(startingPoint);
    //
    //	if (immediateSuccessors.size() > 1) {
    //	    System.out.println( startingPoint.getAttribute("id") + " IS A SPLIT !");
    //
    ////	    for (Element successor : immediateSuccessors ) {
    ////
    ////		if (iHaveNotVisited(successor)){    
    ////
    ////		    @SuppressWarnings("unchecked")
    ////		    ArrayList<Element> newPast = (ArrayList<Element>) past.clone();
    ////		    newPast.add(successor);
    ////		    System.out.println("I decided to go there: " + successor.getAttribute("id"));
    ////		    getPathsFrom(newPast);
    ////		 
    ////		}
    ////	    }
    //
    //	    //creating a list of the unvisited successors:
    //	    ArrayList< Element> unvisitedSuccessors = new ArrayList<Element>();
    //	    
    //	    for (Element successor : immediateSuccessors) { 
    //		if (iHaveNotVisited(successor)) {
    //		 unvisitedSuccessors.add(successor);   
    //		}
    //	    }
    //	    
    //	    
    //	    while (unvisitedSuccessors.size() > 0) {
    //		Element toVisit = unvisitedSuccessors.get(0);
    //		ArrayList<Element> newPast = new ArrayList<Element>();
    //		newPast.add(toVisit);
    //		getPathsFrom(newPast);
    //		unvisitedSuccessors.remove(toVisit);
    //	    }
    //	    
    //	    
    //	    
    //	    return past;
    //	    
    //	} else if (immediateSuccessors.size() == 1){
    //	    
    //	    Element successor = immediateSuccessors.get(0);
    //	    System.out.println(startingPoint.getAttribute("id") + " IS NOT A SPLIT !");
    //	    past.add(successor);
    //	    getPathsFrom(past);
    //	    return past;
    //
    //	} else if (immediateSuccessors.size() == 0){
    //	    //System.out.println("THIS IS THE END");
    //	    paths.add(past);
    //	    //printPaths(); //UNLOCKTHIS for testing
    //	    return past;
    //	} else {
    //	     //this is impossible
    //	    System.out.println("I did not expect for this to happen.");
    //	    return past;
    //	}
    //    }


    private boolean iHaveToVisit (Element element) {
	return this.toVisit.contains(element.getAttribute("id"));
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

    private void visit(Element element) {
	System.out.println("I'm visiting: " + element.getAttribute("id"));
	visited.add(element.getAttribute("id"));
	toVisit.remove(element.getAttribute("id"));
    }

    /**
     * This serves when starting getPathsFrom() from an arbitrary element instead of the start
     * I need to transform it into an arrayList to be able to use it as an input to
     * getPathsFrom()
     * @return
     */
    private ArrayList<Element> fromElementToArrayList(){
	ArrayList<Element> list = new ArrayList<Element>();
	list.add(startingPoint);
	return list;
    }


    private Element getLast(ArrayList<Element> past) {
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
    public ArrayList <Element> getMandatoryDeepSuccessors() throws XPathExpressionException{

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

	//printMandatorySuccessors(); //UNLOCKTHIS for testing

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
    public void printMandatoryDeepSuccessors() {
	System.out.println("PRINTING THE MANDATORY DEEP SUCCESSORS: ");
	for (Element element : mandatoryDeepSuccessors) {
	    System.out.println("                       " + element.getAttribute("id"));
	}


    }


    /**
     * Navigate a model from its starting point
     * @throws XPathExpressionException
     */
    private void getPaths() throws XPathExpressionException {
	//Finding the start element
	Element start = (Element) model.doc.getElementsByTagName("bpmn:startEvent").item(0);
	//Getting paths from the start element:

	ArrayList<Element> startingList = new ArrayList<Element>();

	startingList.add(start);
	
	//Past past = new Past(startingList, pastID++ );
	//getPathsFrom(past);
	
	getPaths(start);

    }

    /**
     * Used for testing
     * That's why for now I'm using names instead of id, but that
     * shall be changed (TODO)
     */
    public void printPaths() {
	System.out.println("======= PRINTING PATHS =======");
	System.out.println("STARTING POINT: " +  startingPoint.getAttribute("name"));
	int i = 1;
	for (ArrayList<Element> path : paths) {
	    System.out.println("-----Path number: "+ i + "-----");
	    for (Element element : path) {
		System.out.println(element.getAttribute("id"));
	    }
	    i++;
	}
    }

    public void printNumberOfPaths() {
	System.out.println("THE NUMBER OF PATHS FROM STARTING POINT: " + startingPoint.getAttribute("id") + " is: " + paths.size()); 
    }

}
