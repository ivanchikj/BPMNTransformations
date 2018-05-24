package bpmn.transformation2;

import org.camunda.bpm.model.bpmn.impl.instance.TargetRef;
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

public class Model {

    public Document doc; //This is the xml representation of my model
    private Element process;
    private Element bpmndiDiagram;
    public String path; ///TODO decide whether this will be private or not.
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

	//Xpath needed to easily interact with XMLs
	XPathFactory xPathFactory = XPathFactory.newInstance();
	xpath = xPathFactory.newXPath();

	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	docBuilder = docFactory.newDocumentBuilder();

	//TODO make it look better
	System.out.println("Opening file at position" + path);

	doc = docBuilder.parse(path);
	process = (Element) doc.getElementsByTagName("bpmn:process").item(0); //TODO what happens if there's more than one process? Is it possible? I have to do both i guess.
	System.out.println("\033[36m " + "I'm working on the following bpmndiDiagram: " + process.getAttribute("id"));

	//there's always one single bmpdi:BMPNDiagram, and one single process so it's "item 0"
	bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0);
	System.out.println("\033[36m " + "I'm working on the following bpmndiDiagram: " + bpmndiDiagram.getAttribute("id"));
    }

    /**
     * TODO guarda anche gli altri attributi tipo "completionQuantity="1" isForCompensation="false" startQuantity="1", non solo id
     * TODO fai BPMNDI thingy
     * @return the id of the newTask
     */
    public String newTask(){
	String id = "GenereateRandomString"; //TODO
	Element newTask = doc.createElement("bpmn:task");
	newTask.setAttribute("id", id);
	System.out.println("\033[36m " + "I created a new Task with the id " + id );
	return id;
    } //ritorna l'id dell'oggetto? Forse non è necessario. Oppure lo prendo come ID? Come faccio a generare ID dal nulla?

    /**
     *  TODO manage the BPMNDI aspects
     * @return id of the new parallel gateway
     */
    public String newParallelGateway(){
	String id = "GenereateRandomString"; //TODO
	Element newTask = doc.createElement("bpmn:task");
	newTask.setAttribute("id", id);
	System.out.println("\033[36m " + "I created a new Parallel Gateway with the id " + id );
	return id;
    }

    /**
     * 
     * @param type
     * @return
     * @throws Exception
     */
    public String newElement (String type) throws Exception {

	//This checks that the provided type is a true BPMN type
	String[] allowed = {"bpmn:task","bpmn:startEvent"};
	if (!Arrays.asList(allowed).contains(type)) {
	    throw new Exception(type + "is not a bpmn type");
	}
	String id = "GenerateRandomString";
	Element newTask = doc.createElement(type);
	newTask.setAttribute("id", id);
	System.out.println("I created a new element of type " + type +  " and with the id " + id);
	return id;

    }


    /**
     * TODO manage BPMNDI
     * TODO give option to create with a condition.
     * @param source
     * @param target
     * @return
     */
    public String newSequenceFlowString (String source, String target) {
	String id = "GenerateRandomString";//TODO
	Element flow = doc.createElement("bpmn:sequenceFlow");
	flow.setAttribute("sourceRef", source);
	flow.setAttribute("TargetRef", target);
	System.out.println("\033[36m " + "I created a new SequenceFlow with the id " + id + " and the source " + source + " and the target " + target);
	return id;
    }

    /**
     * TODO aggiungere un errore quando l'id non corrisponde a un elemento SequenceFlow 
     * TODO manage BPMNDI aspects
     * @param id the id of the sequenceFlow that will be changing source 
     * @param source the new source
     */
    public void setSource(String id, String source){
	Element sequenceFlow = doc.getElementById(id);
	sequenceFlow.setAttribute("sourceRef", source);
	System.out.println("\033[36m " + "I have changed the source of flow " + id + " to " +  source);
    }

    /**
     * TODO aggiungere un errore quando l'id non corrisponde a un elemento SequenceFlow
     * TODO manage BPMNDI aspects
     * @param id the id of the sequenceFlow that will be chaning source
     * @param target the new target
     */
    public void setTarget(String id, String target){
	Element sequenceFlow = doc.getElementById(id);
	sequenceFlow.setAttribute("targetRef", target);
	System.out.println("\033[36m " + "I have changed the target of flow " + id + " to " +  target);
    }

    /**
     * TODO switch from a NodeList to a list of Strings?
     * @param type the type of elements that we want to search for
     * @return a NodeList of elements
     */
    public NodeList findElementByType (String type){
	NodeList elementsOfType = doc.getElementsByTagName(type);
	System.out.println("\033[36m " + "I have found " + elementsOfType.getLength() +  " elements of type " + type) ;
	return elementsOfType;
    }

    /**
     * TODO switch from a NodeList to a list of Strings?
     * Returns a list of the immediate predecessors of a certain Element
     * @param id the id of said Element
     * @return a NodeList of the predecessors of a certain Element
     * @throws XPathExpressionException 
     */
    public  String[] getPredecessors(String id) throws XPathExpressionException{

	NodeList incomingFlows = (NodeList) xpath.evaluate("//*[@targetRef='" + id + "']", doc, XPathConstants.NODESET);
	String[] predecessors = new String[incomingFlows.getLength()];
	for (int i = 0; i < incomingFlows.getLength(); i ++) {
	    Element element =  (Element) incomingFlows.item(i);
	}

	System.out.println("\033[36m " + "I have found " + predecessors.length + " immediate predecessors");
	return predecessors;
    }

    /**
     * TODO switch from a NodeList to a list of Strings?
     * Returns a list of the immediate successors of a certain Element
     * TODO if a sequenceFlow comes out of a parallel gateway it might be that two sequence flows from outgoingFlows have the same source.
     * It should not happen, but if it does I should remove duplicates
     * TODO test this
     * @param id the id of said Element
     * @return a NodeList of the successors of a certain Element
     * @throws XPathExpressionException 
     */
    public String[] getSuccessors(String id) throws XPathExpressionException{

	NodeList outgoingFlows = (NodeList) xpath.evaluate("//*[@sourceRef='" + id + "']", doc, XPathConstants.NODESET);
	String[] successors = new String[outgoingFlows.getLength()];

	for (int i = 0; i < outgoingFlows.getLength(); i++) {
	    Element element = (Element) outgoingFlows.item(i);
	    successors[i] = element.getAttribute(id);
	}
	System.out.println("\033[36m " + "I have found " + successors.length + " immediate successors");
	return successors;
    }

    /**
     * Returns the type (tagname) of an element
     * @param id
     * @return
     */
    public String getType(String id){
	String type = doc.getElementById(id).getTagName();
	System.out.println("\033[36m " + "The type of element " +  id + " is ");
	return type;
    }

    /**
     * TODO see if this works just as well as deleteElement
     * This is much simpler
     * @param id the ID of the element to delete
     */
    public void delete (String id) {
	Element elementToDelete = doc.getElementById(id);
	elementToDelete.getParentNode().removeChild(elementToDelete);
	System.out.println("\033[36m " + "I'm deleting element with id " +  id);
    }

    /**
     * This method is used to delete elements from the diagram in an XML file.
     * Note that I will use the attribute "BMPMN Element" to find the BMPNDI element that I want to delete, instead of "id", because
     * the ID of a bpmndi element will sometimes be different from the corresponding BPMN element's own iD.
     * @param id
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws TransformerException
     * @throws TransformerConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public void deleteElement(String id) throws ParserConfigurationException, SAXException, IOException,
    XPathExpressionException, TransformerException, TransformerConfigurationException, org.xml.sax.SAXException {

	System.out.println("I'm searching for id: " + id);
	NodeList nodeList = (NodeList) xpath.evaluate("//*[@bpmnElement='" + id + "']", doc, XPathConstants.NODESET);
	for (int i = 0; i < nodeList.getLength(); ++i) { //This for is used in case there are more than one bmpndi with the same id, which shouldn't happen TODO decide	
	    Element element = (Element) nodeList.item(i);
	    System.out.println(element.getAttribute("bpmnElement"));
	    element.getParentNode().removeChild(element);
	    System.out.println("\033[36m " + "I removed element with ID: " + id);
	}
	System.out.println("\033[36m " + "Deleted element " + id);

    }

    public void saveToFile() throws TransformerException {
	//Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(path));
	transformer.transform(source, result);
	System.out.println("Saved the XML file in + " + path);

    }

}