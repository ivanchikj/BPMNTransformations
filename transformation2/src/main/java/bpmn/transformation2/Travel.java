package bpmn.transformation2;

import java.util.ArrayList;import javax.swing.text.AbstractDocument.LeafElement;
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
    public ArrayList <Element> getAPath (ArrayList<Element> past) throws XPathExpressionException{
	
	ArrayList<Element> path = new ArrayList<Element>();
	
	Element startingPoint = past.get(past.size()-1); //we start from the last element of this path
	
	System.out.println("Visiting element " + startingPoint.getAttribute("name"));
	//adding the startingPoint to the list of visited elements
	visited.add(startingPoint.getAttribute("id"));
	
	// getting the successors of our starting point
	ArrayList<Element> immediateSuccessors = model.getSuccessors(startingPoint);
	
	
	//going through all the immediate successors
	for (Element successor : immediateSuccessors) {
	    //checking if the immediateSuccessors has been visited
	    
	  //  if (!visited.contains(successor.getAttribute("id"))) {
		past.add(successor);
		System.out.println("STARTING FROM " +  successor.getAttribute("name"));
		
		//I need to create a clone lest the past array remains editable in the future and would be changed 
		//by this method's next travels
		ArrayList<Element> newPast = (ArrayList<Element>) past.clone();
		
		getAPath(newPast);
		
		if (model.getSuccessors(successor).size() == 0) {
		    System.out.println("I've reached the end of the path");
		    paths.add(past);
		}
		
	  //  }
	    
	}
	
	
	
	
	
	//poi usi il metodo getdeepsuccessors (questo) e appendi il risultato alla lista dei deepFollowers.
	//SOLO se non è stato visitato, lo aggiungi alla lista dei visited.
	//Quando c'è un IF crei un nuovo array (che corrisponde a un nuovo path), però prima copi il contenuto 
	//del (singolo) path che c'era fino a quel momento. Altrimenti se non lo fai partiresti da troppo dopo
	return path;
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
    public void travelModel() throws XPathExpressionException {
	//Finding the start element
	Element start = (Element) model.doc.getElementsByTagName("bpmn:startEvent").item(0);
	//Getting paths from the start element:
	
	ArrayList<Element> startingList = new ArrayList<Element>();
	
	startingList.add(start);
	getAPath(startingList);

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
