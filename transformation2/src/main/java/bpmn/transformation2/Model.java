package bpmn.transformation2;

import org.camunda.bpm.model.bpmn.impl.instance.TargetRef;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bpmn.transformation2.Main;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.PrimitiveIterator.OfDouble;

public class Model {

    public Document doc; // This is the xml representation of my model
    private Element process;
    private Element bpmndiDiagram;
    private Element bpmndiPlane;
    public String path; /// TODO decide whether this will be private or not.
    private XPath xpath;
    private DocumentBuilder docBuilder;

    /**
     * 
     * @param path
     * @return 
     * @throws IOException
     * @throws org.xml.sax.SAXException
     * @throws ParserConfigurationException
     */
    public Model(String path) throws IOException, org.xml.sax.SAXException, ParserConfigurationException {
	load(path);
    }


    /**
     * TODO probabilmente questo verrà eliminato
     */
    public void reLoad() throws SAXException, IOException, ParserConfigurationException, TransformerException {
	//Locaiton of the temp files.
	String tempPath = "./Temp/" + newId() + "TEMP" + ".bpmn.xml"; 
	//TODO This is ugly. A better idea would be to separate the model from the file maybe. Or maybe there's no even need to save new files, maybe just restarting the xpath is enough 

	//By saving and reloading the files
	//I can "see" the new elements that I have created. But maybe I do not need them, maybe I just need to reload the Xpath.
	saveTemp(tempPath);
	load(tempPath);
    }


    /**
     * Used to (re)load the model. 
     * TODO decide if it's better to delete this and just use it as a constructor
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public void load(String path) throws SAXException, IOException, ParserConfigurationException {
	this.path = path;
	// Xpath needed to easily interact with XMLs
	XPathFactory xPathFactory = XPathFactory.newInstance();
	this.xpath = xPathFactory.newXPath();

	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	this.docBuilder = docFactory.newDocumentBuilder();

	// TODO make it look better
	System.out.println("		Opening file at position" + path);

	this.doc = docBuilder.parse(path);
	this.process = (Element) doc.getElementsByTagName("bpmn:process").item(0); // TODO what happens if there's more than
	// one process? Is it possible? I have to
	// do both i guess.
	System.out.println("		I'm working on the following Process: " + process.getAttribute("id"));

	// there's always one single bmpdi:BMPNDiagram, and one single process so it's
	// "item 0"
	this.bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0);

	// TODO check if there's not always one single bmpdi:BMPNDiagram. In that case it's better to find a way to manage this
	this.bpmndiPlane = (Element) doc.getElementsByTagName("bpmndi:BPMNPlane").item(0);
	System.out.println(
		"		I'm working on the following bpmndiDiagram: " + bpmndiDiagram.getAttribute("id"));
    }


    /**
     * Method used to test various ideas or slices of other methods.
     * @return
     */
    public void testMethod() {

    }


    /**
     * ASKANA cosa sono gli altri attributi tipo "completionQuantity="1"
     * isForCompensation="false" startQuantity="1", li devo aggiungere?
     * 
     * 
     * @return the id of the newTask
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    public String newTask(String x, String y) throws SAXException, IOException, ParserConfigurationException {
	// PROCESS VIEW
	String id = newId(); // TODO
	Element newTask = doc.createElement("bpmn:task");
	newTask.setAttribute("id", id);
	newTask.setAttribute("name", "NEW");
	process.appendChild(newTask);

	// BPMNDI VIEW
	Element newTaskDI = doc.createElement("bpmndi:BPMNShape");
	bpmndiDiagram.appendChild(newTaskDI);
	newTaskDI.setAttribute("bpmnElement", id);
	newTaskDI.setAttribute("id", id + "_di"); // I don't know if this is mandatory
	System.out.println("		I created a new Task with the id " + id);

	Element size = doc.createElement("dc:Bounds");
	bpmndiPlane.appendChild(newTaskDI); //TODO am I sure about this?

	newTaskDI.appendChild(size);
	size.setAttribute("height", "80");
	size.setAttribute("width", "100");
	size.setAttribute("x", x);
	size.setAttribute("y", y); // TODO Decide on how to calculate this.
	return id;
    } // ritorna l'id ? Forse è meglio void?

    /**
     * 
     * @param type
     * @return
     * @throws Exception
     */
    public String newNode(String type, String x, String y) throws Exception {


	// This checks that the provided type is a true BPMN type and applies the relative method
	String id = null;
	if (type.equals("bpmn:task")){
	    id = this.newTask(x, y);
	} else if (type.equals("bpmn:parallelGateway")) {
	    //TODO
	} else {
	    System.err.println("		" + type + " is not a valid BPMN type");
	}
	System.out.println("		I created a new element of type " + type + " and with the id " + id);
	doc.normalizeDocument();
	if (id.equals(null)) {} // Decide how to check that the newly created element's id is not null.
	//Maybe this is also a way to discover if this is not a valid BPMN type.
	return id;
    }

    /**
     * TODO manage BPMNDI TODO give option to create with a condition. Like, "if
     * condition is null, then apply none".
     * 
     * @param source
     * @param target
     * @return
     */
    public String newSequenceFlow(String source, String target) {
	String id = newId();// TODO
	Element flow = doc.createElement("bpmn:sequenceFlow");
	flow.setAttribute("sourceRef", source);
	flow.setAttribute("TargetRef", target);
	System.out.println("		I created a new SequenceFlow with the id " + id + " and the source " + source
		+ " and the target " + target);
	return id;
    }

    /**
     * TODO aggiungere un errore quando l'id non corrisponde a un elemento
     * SequenceFlow TODO manage BPMNDI aspects
     * TODO vedi se puoi riutilizzare alcuni pezzi per il metodo newSequenceFlow
     * 
     * @param id
     *            the id of the sequenceFlow that will be changing source
     * @param source
     *            the new source
     * @throws XPathExpressionException
     */
    public void setSource(String id, String source) throws XPathExpressionException {

	String previousSourceId =  findElemById(id).getAttribute("sourceRef");
	Element previousSource = findElemById(previousSourceId);
	System.out.println("		This element's previous source is: " + previousSource.getAttribute("id"));
	System.out.println("");

	System.out.println("		Content of child: " + previousSource.getTextContent());

	System.out.println("		Content of 2child: " + xpath.evaluate("./text()", previousSource));

	//TODO see if there's any difference. See if it prints all of the child's content or not.

	if (previousSource.hasChildNodes()) { //This is expected to be always true anyway
	    NodeList childList = previousSource.getChildNodes();
	    for (int i = 0; i < childList.getLength(); i++) {
		Node childInCase = childList.item(i);
		String textContentString = childInCase.getTextContent();
		System.out.println("		Searching for child to delete, child content in case: " + textContentString);
		System.out.println("		Searching for child to delete, child i'm looking for: " + id);
		//TODO add a check to see if it's of the TAG bpmn:outgoing. It should 
		//be checked in case the task is both the source and the target of a SequenceFlow!
		if (textContentString.equals(id)){
		    childInCase.getParentNode().removeChild(childInCase);
		    System.out.println("		I have found the child that I want to delete!!");
		    //TODO if you want, find a way to remove the blank space that gets created
		}

	    }

	}


	Element sequenceFlow = findElemById(id);
	System.out.println("		The id of the sequenceFlow that I have found is " + sequenceFlow.getAttribute("id"));
	sequenceFlow.setAttribute("sourceRef", source);
	// We still need to change also the element of the Source to have my outgoing
	// flow as a child
	Element sourceElement = findElemById(source);
	System.out.println("		The new source is: " + sourceElement.getAttribute("id"));
	Element outgoing = doc.createElement("bpmn:outgoing");
	outgoing.appendChild(doc.createTextNode(id)); //This adds the id as a text inside the tags
	sourceElement.appendChild(outgoing);



	//Since it's impossible to distinguish the source waypoints from the target waipoints, it's best to simply delete all
	//existing waypoints and create two of them from scratch.
	//TODO a good method would be to create an algorithm that gets the position of the source element and of the target,
	//and distinguish the waypoint of the source form the waypoint of the target based on the one that is less 
	//distant from the original positions of the source and target elements.

	//TODO see how camunda solves this. One problem that this has is that it connects to the center of the items, 
	//instead of connecting to the borders of the item. I could change the position to go to the item's border by
	//adding/substracting half of the element height/width but how do i decide which operation to do? It depends
	//on whether I want to connect to the item's upper, lower, left or right border.




	//we want to get the position of the source to know where the flow will have to point
	//NOTE the first child of the BPMNDI is the one tagged "dc:Bounds"

	Element sourceBPMNDI = findBPMNDI(source);
	//TODO what happens if the elements has more than one child? It happens if there's a label, for example.
	
	
	//	Element sourceDcBounds = (Element) sourceBPMNDI.getFirstChild(); //the dc:bounds tag contains the info about the position
	//	
	//	String xSource = sourceDcBounds.getAttribute("x");
	//	String ySource = sourceDcBounds.getAttribute("y");
	//	Element sourceWP = createWaypoint(xSource, ySource);
	//	
	//	
	//	//we want to get the position of the target to know where the flow will have to point
	//	String target = sequenceFlow.getAttribute("targetRef");
	//	Element targetBPMNDI = findBPMNDI(target);
	//	Element targetDcBounds = (Element) targetBPMNDI.getFirstChild(); //the dc:bounds tag contains the info about the position
	//	String xTarget = targetDcBounds.getAttribute("x");
	//	String yTarget = targetDcBounds.getAttribute("y");
	//	Element targetWP = createWaypoint(xTarget, yTarget);
	//	
	//	//This is the bpmndi corresponding to our sequenceFlow
	//	Element sequenceFlowBPMNDI = findBPMNDI(id);
	//	
	//	//let's remove the previous waypoints:
	//	while(sequenceFlowBPMNDI.hasChildNodes()) {
	//	    sequenceFlowBPMNDI.removeChild(sequenceFlowBPMNDI.getFirstChild());
	//	    System.out.println("		Just deleted the a waypoint");
	//	}
	//
	//	//let's now add the previously created waypoints:
	//	sequenceFlowBPMNDI.appendChild(targetWP);
	//	sequenceFlowBPMNDI.appendChild(sourceWP);
	
	
	
		
	findDcBounds(sourceBPMNDI);

	System.out.println("		I have changed the source of flow " + id + " to " + source);
    }

    /**
     * TODO aggiungere un errore quando l'id non corrisponde a un elemento
     * SequenceFlow TODO manage BPMNDI aspects
     * 
     * @param id
     *            the id of the sequenceFlow that will be changing source
     * @param target
     *            the new target
     * @throws XPathExpressionException
     * @throws DOMException
     */
    public void setTarget(String id, String target) throws DOMException, XPathExpressionException {

	// We need to remove the child from the previous target
	// The following xpath could create problems when a flow has two targets, but
	// that should be impossible.
	NodeList toDelete = (NodeList) xpath.evaluate("//bpmn:outgoing[.='" + id + "']", doc, XPathConstants.NODESET);
	// TODOthis might not work

	doc.removeChild(toDelete.item(0));

	// This changes the element of the sequenceFlow
	Element sequenceFlow = doc.getElementById(id);
	sequenceFlow.setAttribute("targetRef", target);

	// We still need to change also the element of the Source to have my outgoing
	// flow as a child
	Element sourceElement = doc.getElementById(target);
	Element incoming = doc.createElement("bpmn:outgoing");
	incoming.setTextContent(id); // TODO I'm not sure this does what I want.
	sourceElement.appendChild(incoming);

	System.out.println("		I have changed the target of flow " + id + " to " + target);
    }

    /**
     * TODO switch from a NodeList to a list of Strings?
     * 
     * @param type
     *            the type of elements that we want to search for
     * @return a NodeList of elements
     */
    public NodeList findElementByType(String type) {
	NodeList elementsOfType = doc.getElementsByTagName(type);
	System.out.println("		I have found " + elementsOfType.getLength() + " elements of type " + type);
	return elementsOfType;
    }

    /**
     * TODO switch from a NodeList to a list of Strings? Returns a list of the
     * immediate predecessors of a certain Element
     * 
     * @param id
     *            the id of said Element
     * @return a NodeList of the predecessors of a certain Element
     * @throws XPathExpressionException
     */
    public String[] getPredecessors(String id) throws XPathExpressionException {

	NodeList incomingFlows = (NodeList) xpath.evaluate("//*[@targetRef='" + id + "']", doc, XPathConstants.NODESET);
	String[] predecessors = new String[incomingFlows.getLength()];
	for (int i = 0; i < incomingFlows.getLength(); i++) {
	    predecessors[i] = ((Element) incomingFlows.item(i)).getAttribute("sourceRef");
	}

	System.out.println("		I have found " + predecessors.length + " immediate predecessors");
	return predecessors;
    }

    /**
     * TODO switch from a NodeList to a list of Strings? Returns a list of the
     * immediate successors of a certain Element TODO if a sequenceFlow comes out of
     * a parallel gateway it might be that two sequence flows from outgoingFlows
     * have the same source. It should not happen, but if it does I should remove
     * duplicates TODO test this
     * 
     * @param id
     *            the id of said Element
     * @return a NodeList of the successors of a certain Element
     * @throws XPathExpressionException
     */
    public String[] getSuccessors(String id) throws XPathExpressionException {

	NodeList outgoingFlows = (NodeList) xpath.evaluate("//*[@sourceRef='" + id + "']", doc, XPathConstants.NODESET);
	String[] successors = new String[outgoingFlows.getLength()];

	for (int i = 0; i < outgoingFlows.getLength(); i++) {
	    successors[i] = ((Element) outgoingFlows.item(i)).getAttribute("targetRef");
	}
	System.out.println("		I have found " + successors.length + " immediate successors");
	return successors;
    }

    /**
     * Returns the type (tagname) of an element
     * 
     * @param id
     * @return
     */
    public String getType(String id) {
	String type = doc.getElementById(id).getTagName();
	System.out.println("		The type of element " + id + " is ");
	return type;
    }

    /**
     * TODO see if this works just as well as deleteElement This is much simpler
     * 
     * @param id
     *            the ID of the element to delete
     * @throws XPathExpressionException
     */
    public void delete(String id) throws XPathExpressionException {
	Element elementToDelete = doc.getElementById(id);
	elementToDelete.getParentNode().removeChild(elementToDelete);
	System.out.println("		I'm deleting element with id " + id);

	NodeList bpmndiElementsToDelete = (NodeList) xpath.evaluate("//*[@bpmnElement='" + id + "']", doc,
		XPathConstants.NODESET);
	for (int i = 0; i < bpmndiElementsToDelete.getLength(); ++i) { // This for is used in case there are more than
	    // one bmpndi with
	    // the same id, which shouldn't happen TODO decide
	    Element element = (Element) bpmndiElementsToDelete.item(i);
	    System.out.println(element.getAttribute("bpmnElement"));
	    element.getParentNode().removeChild(element);
	}
    }

    /**
     * This method is used to delete elements from the diagram in an XML file. Note
     * that I will use the attribute "BMPMN Element" to find the BMPNDI element that
     * I want to delete, instead of "id", because the ID of a bpmndi element will
     * sometimes be different from the corresponding BPMN element's own iD.
     * 
     * @param id
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws TransformerException
     * @throws TransformerConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public void deleteElement(String id)
	    throws ParserConfigurationException, SAXException, IOException, XPathExpressionException,
	    TransformerException, TransformerConfigurationException, org.xml.sax.SAXException {

	System.out.println("I'm searching for id: " + id);
	NodeList nodeList = (NodeList) xpath.evaluate("//*[@bpmnElement='" + id + "']", doc, XPathConstants.NODESET);
	for (int i = 0; i < nodeList.getLength(); ++i) { // This for is used in case there are more than one bmpndi with
	    // the same id, which shouldn't happen TODO decide
	    Element element = (Element) nodeList.item(i);
	    System.out.println(element.getAttribute("bpmnElement"));
	    element.getParentNode().removeChild(element);
	    System.out.println("		I removed element with ID: " + id);
	}
	System.out.println("		Deleted element " + id);
    }

    public void saveToFile(String path) throws TransformerException {
	// Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(path));
	transformer.transform(source, result);
	System.out.println("Saved the XML file in + " + path);

    }

    public void saveTemp(String tempPath) throws TransformerException {
	// Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(tempPath));
	transformer.transform(source, result);
	System.out.println("		Saved the TEMPORARy XML file in + " + path);    
    }
    /**
     * GetElementById doesn't work with our XML so this method serves that purpose.
     * The problem is that while our XML elements have an ID property, that is not
     * recognized by the GetElementId because it's not specified in the appropriate
     * way inside the document. In other words, it's not enough to have a property
     * called "id" for it to be an id.
     * @return
     * @throws XPathExpressionException 
     */
    public Element findElemById(String id) throws XPathExpressionException {

	Node node = (Node) xpath.evaluate("//*[@id='" + id + "']", doc, XPathConstants.NODE);
	//We expect only one element to have the same id
	return (Element) node;
    }

    /**
     * Method used to find the BPMNDI element relative to an id provided by the user
     * @param id the id of the element that i want to find the bpmndi element of
     * @return
     * @throws XPathExpressionException
     */
    public Element findBPMNDI(String id) throws XPathExpressionException {
	Node bpmndi = (Node) xpath.evaluate("//*[@bpmnElement='" + id + "']", doc, XPathConstants.NODE);
	//We expect only one element to have the same id
	return (Element) bpmndi;
    }

    /**
     * this method takes a Bpmndi Element as input
     * and returns the relative dc:Bounds element
     * (which contains informations about the X and Y positions of the BPMND element.
     * TODO future improvement for this method would be to create an algorithm that gets the position 
     * of the source element and of the target,
     * and distinguish the waypoint of the source form the waypoint of the target based on the one that is less 
     * distant from the original positions of the source and target elements.
     * @param bpmndiElement
     * @return
     * @throws XPathExpressionException 
     */
    public Element findDcBounds(Element bpmndiElement) throws XPathExpressionException {
	
	Element dcBounds = (Element) xpath.evaluate("//*[dc:Bounds=", bpmndiElement, XPathConstants.NODE);
	System.out.println(dcBounds);
	System.out.println(dcBounds.getAttribute("x"));
	System.out.println(dcBounds.getAttribute("x"));
	System.out.println(dcBounds.getAttribute("x"));
	System.out.println(dcBounds.getAttribute("x"));
	System.out.println(dcBounds.getAttribute("x"));

	return bpmndiElement;

    }

    /**
     * This method calculates the x and y positions of a new element based on the position of its predecessors
     * The method simply calculates the position as the average of the old elements on both axis
     * the x position will be the first position of the array[0], while the y information is stored on the [1] position 
     * 
     * 
     * TODO this is not tested nor finished
     * 
     * 
     * @param predecessorId
     * @param successorId
     * @return
     * @throws XPathExpressionException 
     */
    public String[] calculatePosition(String predecessorId, String successorId) throws XPathExpressionException {
	Node predecessorBpmndi = (Node) xpath.evaluate("//*[@bpmnElement='" + predecessorId + "']", doc, XPathConstants.NODE);
	Node successorBpmndi = (Node) xpath.evaluate("//*[@bpmnElement='" + successorId + "']", doc, XPathConstants.NODE);
	// Predecessor position
	int predX = Integer.parseInt(((Element) predecessorBpmndi.getFirstChild()).getAttribute("x")); //TODO getFirstChild does not works. use Xpath
	int predY = Integer.parseInt(((Element) predecessorBpmndi.getFirstChild()).getAttribute("y")); //TODO getFirstChild does not works. use Xpath
	// Successor position
	int succX = Integer.parseInt(((Element) successorBpmndi.getFirstChild()).getAttribute("x")); //TODO getFirstChild does not works. use Xpath
	int succY = Integer.parseInt(((Element) successorBpmndi.getFirstChild()).getAttribute("y")); //TODO getFirstChild does not works. use Xpath

	String[] positions = new String[2];
	positions[0] = "" + (predX+succX)/2; //not super precise but who cares?
	positions[1] = "" + (predY+succY)/2;

	return positions;
    }

    /**
     * This method creates the waypoints used in the BPMNDI to position the sequenceFlows
     * This method is used both to create the waypoints of the "pointy" side
     * of the arrow and of the flat side of the arrows, as they are identical.
     * Probably the program deduces where to put the arrow based on the process part of the XML
     * X and Y are provided by the user. This methods only creates the elements
     * @param x
     * @param y
     * @return
     */
    public Element createWaypoint(String x, String y){

	Element waypoint = doc.createElement("di:waypoint");

	waypoint.setAttribute("x", x);

	waypoint.setAttribute("xsi:type", "dc:Point"); //this attribute never changes and is always the same (?)
	waypoint.setAttribute("y", y);

	System.out.println("		I created a waypoint with the coordinates " + x + " and " + y);
	return waypoint;
    }


    /**
     * TODO maybe add a check that the ID is not already in use?
     * 
     * @return
     */
    public String newId() {
	return UUID.randomUUID().toString();
    }

}