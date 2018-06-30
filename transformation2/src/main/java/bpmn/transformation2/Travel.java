package bpmn.transformation2;

import java.util.ArrayList;
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
    public ArrayList <Element> getAPath (Element startingPoint) throws XPathExpressionException{
	ArrayList<String> visited = new ArrayList<String>(); //contains the ID's of the visited elements
	ArrayList<Element> deepSuccessors = new ArrayList<Element>();

	System.out.println("Visiting element " + startingPoint.getAttribute("id"));
	//adding the startingPoint to the list of visited elements
	visited.add(startingPoint.getAttribute("id")); 

	// getting the successors of our starting point
	ArrayList<Element> immediateSuccessors = model.getSuccessors(startingPoint);

	//Adding the successors to the list of deepSuccessors
	for (Element successor : immediateSuccessors) {
	    deepSuccessors.add(successor);
	}

	//going through all the immediate successors
	for (Element successor : immediateSuccessors)
	    //checking if the immediateSuccessors has been visited
	    if (!visited.contains(successor.getAttribute("id"))) {
		paths.add(getAPath(successor));

	    }



	//TODO:
	// Aggiungi startingPoint alla lista dei visited,
	//poi fai getSuccessors, poi per ogni successor lo aggiungi alla lista dei deepFollowers,
	// poi usi il metodo getdeepsuccessors (questo) e appendi il risultato alla lista dei deepFollowers.
	//  SOLO se non è stato visitato, lo aggiungi alla lista dei visited.
	//Quando c'è un IF crei un nuovo array (che corrisponde a un nuovo path), però prima copi il contenuto 
	//del (singolo) path che c'era fino a quel momento. Altrimenti se non lo fai partiresti da troppo dopo
	return deepSuccessors;

    }

    /**
     * From the list of possible paths, discover mandatory elements
     */
    public ArrayList <Element> mandatoryDeepSuccessors (ArrayList paths){
	ArrayList <Element> mandatoryDeepSuccessors = new ArrayList<Element>();


	return mandatoryDeepSuccessors;

    }
    
    
    public void travelModel() throws XPathExpressionException {
	//Finding the start element
	Element start = (Element) model.doc.getElementsByTagName("bpmn:startEvent").item(0);
	
	//Getting paths from the start element:
	getAPath(start);
	
    }
    
    /**
     * Used for testing
     * That's why for now I'm using names instead of id, but that
     * shall be changed (TODO)
     */
    public void printPaths() {
	System.out.println("=====PRINTING PATHS=====");
	
	for (ArrayList<Element> path : paths) {
	    for (Element element : path) {
		System.out.println(element.getAttribute("name"));
	    }
	    System.out.println("-----next path:-----");
	}
    }

}
