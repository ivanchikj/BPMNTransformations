package bpmn.transformation2;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.text.AbstractDocument.LeafElement;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.xml.xpath.XPathExpressionException;

import org.apache.ibatis.annotations.ConstructorArgs;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Travel {

    public Model model;
    public ArrayList<ArrayList<Element>> paths;
    public ArrayList<String> visited;


    /**
     * Constructor
     * @param model
     */
    public Travel (Model model) {
	this.model = model;
	paths = new ArrayList<ArrayList<Element>>();
	visited = new ArrayList<String>();
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
	}

	return past;
    }



    public ArrayList<Element> single (ArrayList<Element> past){
	return past; 
    }

    private boolean iHaveNotVisited(Element element) {
	return !this.visited.contains(element.getAttribute("id"));
    }

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

    public Element getLast (ArrayList<Element> past) {
	return past.get(past.size()-1);
    }



    /**
     * From the list of possible paths, discover mandatory elements
     */
    public ArrayList <Element> mandatoryDeepSuccessors (ArrayList paths){
	ArrayList <Element> mandatoryDeepSuccessors = new ArrayList<Element>();
	//TODO
	return mandatoryDeepSuccessors;

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
