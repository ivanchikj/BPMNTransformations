package bpmn.transformation2;

import org.camunda.bpm.model.bpmn.impl.instance.TargetRef;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
     * @throws IOException
     * @throws org.xml.sax.SAXException
     * @throws ParserConfigurationException
     */
    public Model(String path) throws IOException, org.xml.sax.SAXException, ParserConfigurationException {

	this.path = path;

	// Xpath needed to easily interact with XMLs
	XPathFactory xPathFactory = XPathFactory.newInstance();
	xpath = xPathFactory.newXPath();

	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	docBuilder = docFactory.newDocumentBuilder();

	// TODO make it look better
	System.out.println("		Opening file at position" + path);

	doc = docBuilder.parse(path);
	process = (Element) doc.getElementsByTagName("bpmn:process").item(0); // TODO what happens if there's more than
	// one process? Is it possible? I have to
	// do both i guess.
	System.out.println("		I'm working on the following Process: " + process.getAttribute("id"));

	// there's always one single bmpdi:BMPNDiagram, and one single process so it's
	// "item 0"
	bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0);

	// TODO check if there's not always one single bmpdi:BMPNDiagram. In that case it's better to find a way to manage this
	bpmndiPlane = (Element) doc.getElementsByTagName("bpmndi:BPMNPlane").item(0);
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
     */
    public String newTask(String x, String y) {
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

	String id = newId();
	// This checks that the provided type is a true BPMN type and applies the relative method

	if (type.equals("bpmn:task")){
	    this.newTask(x, y);
	} else if (type.equals("bpmn:parallelGateway")) {
	    //TODO
	} else {
	    System.out.println("		" + type + " is not a valid BPMN type");
	}
	System.out.println("		I created a new element of type " + type + " and with the id " + id);
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
     * 
     * @param id
     *            the id of the sequenceFlow that will be changing source
     * @param source
     *            the new source
     * @throws XPathExpressionException
     */
    public void setSource(String id, String source) throws XPathExpressionException {
	//	// We need to remove the child from the previous target
	//	// The following xpath could create problems when a flow has two targets, but
	//	// that should be impossible.
	//	NodeList toDelete = (NodeList) xpath.evaluate("//bpmn:incoming[.='" + id + "']", doc, XPathConstants.NODESET); // TODO
	//	// this
	//	// might
	//	// not
	//	// work
	//
	//	//TODO this is empty. And it shoudn't be.
	//	for (int i = 0; i < toDelete.getLength(); i++) {
	//	    System.out.println("STO ELIMINANDO QUESTI: ");
	//	    System.out.println("       " + toDelete.item(i).getTextContent());
	//	    doc.removeChild(toDelete.item(i));
	//	}
	//	

	/**
	 * Basically what i'm doing here is
	 * finding the previous source of the seqFlow that i want to change
	 * the source of
	 * 
	 * Then once I found it i want to delete all the children of which
	 * the text content equals the id of the SequenceFlow that will
	 * be changing source
	 */
	String previousSourceId =  findElemById(id).getAttribute("sourceRef");
	Element previousSource = findElemById(previousSourceId);
	while (previousSource.hasChildNodes()) {
	    if (previousSource.getFirstChild().getTextContent().equals(id)) {
		previousSource.removeChild(previousSource.getFirstChild());
		System.out.println("I deleted the children from the previous Source of " + id);
	    }
	}





	// This changes the element of the sequenceFlow
	Element sequenceFlow = findElemById(id);
	System.out.println("The id of the sequenceFlow that I have found is" + sequenceFlow.getAttribute(id));
	sequenceFlow.setAttribute("sourceRef", source);
	// We still need to change also the element of the Source to have my outgoing
	// flow as a child
	Element sourceElement = doc.getElementById(source);
	Element outgoing = doc.createElement("bpmn:outgoing");
	outgoing.setTextContent(id); // TODO I'm not sure this does what I want.
	sourceElement.appendChild(outgoing);

	String x = doc.getElementById(source + "_di").getAttribute("x");

	// TODO We need to remove the child of the previous target

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
	    System.out.println("\033[36m " + "I removed element with ID: " + id);
	}
	System.out.println("		Deleted element " + id);
    }

    public void saveToFile() throws TransformerException {
	// Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(path));
	transformer.transform(source, result);
	System.out.println("		Saved the XML file in + " + path);

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

	NodeList nodeList = (NodeList) xpath.evaluate("//*[@id='" + id + "']", doc, XPathConstants.NODESET);
	//We expect only one element to have the same id
	return (Element) nodeList.item(0);
    }

    /**
     * This method calculates the x and y positions of a new element based on the position of its predecessors
     * The method simply calculates the position as the average of the old elements on both axis
     * the x position will be the first position of the array[0], while the y information is stored on the [1] position 
     * @param predecessorId
     * @param successorId
     * @return
     * @throws XPathExpressionException 
     */
    public String[] calculatePosition(String predecessorId, String successorId) throws XPathExpressionException {
	NodeList predecessorBpmndi = (NodeList) xpath.evaluate("//*[@bpmnElement='" + predecessorId + "']", doc, XPathConstants.NODESET);
	NodeList successorBpmndi = (NodeList) xpath.evaluate("//*[@bpmnElement='" + successorId + "']", doc, XPathConstants.NODESET);
	// Predecessor position
	int predX = Integer.parseInt(((Element) predecessorBpmndi.item(0).getFirstChild()).getAttribute("x")); //getFirstChild is not ideal maybe use Xpath
	int predY = Integer.parseInt(((Element) predecessorBpmndi.item(0).getFirstChild()).getAttribute("y")); //getFirstChild is not ideal maybe use Xpath
	// Successor position
	int succX = Integer.parseInt(((Element) successorBpmndi.item(0).getFirstChild()).getAttribute("x")); //getFirstChild is not ideal maybe use Xpath
	int succY = Integer.parseInt(((Element) successorBpmndi.item(0).getFirstChild()).getAttribute("y")); //getFirstChild is not ideal maybe use Xpath

	String[] positions = new String[2];
	positions[0] = "" + (predX+succX)/2; //not super precise but who cares?
	positions[1] = "" + (predY+succY)/2;

	return positions;
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