package bpmn.transformation2;

import org.apache.taglibs.standard.lang.jstl.AndOperator;
import org.jboss.com.sun.corba.se.impl.orbutil.closure.Constant;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.crypto.interfaces.DHPublicKey;
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
import java.util.ArrayList;
import java.util.UUID;

public class Model {

    public Document doc; // This is the xml representation of my model
    private Element process;
    private Element bpmndiDiagram;
    private Element bpmndiPlane;
    public String path; /// TODO decide whether this will be private or not.
    private XPath xpath;
    private DocumentBuilder docBuilder;

    //TODO fare metodo che prende un gateway e controlla se è un merge. Mi serve in più di una regola mi sa. Quindi è meglio metterlo qua

    /**
     * @param path
     * @return 
     * @throws IOException
     * @throws org.xml.sax.SAXException
     * @throws ParserConfigurationException
     */
    public Model(String path) throws IOException, org.xml.sax.SAXException, ParserConfigurationException {
	System.out.println("New model instance: ");

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
     * ASKANA cosa sono gli altri attributi tipo "completionQuantity="1"
     * isForCompensation="false" startQuantity="1", li devo aggiungere?
     *
     * @return the id of the newTask
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    public String newTask(String x, String y) throws SAXException, IOException, ParserConfigurationException {
	// PROCESS VIEW
	String id = newId();
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
    }


    public String newInclusiveGateway(String x, String y) { 
	// PROCESS VIEW
	String id = newId(); 
	Element newNode = doc.createElement("bpmn:inclusiveGateway");
	newNode.setAttribute("id", id);
	newNode.setAttribute("name", "NEW");
	process.appendChild(newNode);

	// BPMNDI VIEW
	Element newNodeDI = doc.createElement("bpmndi:BPMNShape");
	//bpmndiDiagram.appendChild(newGatDI);
	newNodeDI.setAttribute("bpmnElement", id);
	newNodeDI.setAttribute("id", id + "_di"); // I don't know if this is mandatory
	System.out.println("		I created a new Inclusive Gateway with the id " + id);

	Element size = doc.createElement("dc:Bounds");
	bpmndiPlane.appendChild(newNodeDI);

	newNodeDI.appendChild(size);
	size.setAttribute("height", "50");
	size.setAttribute("width", "50");
	size.setAttribute("x", x);
	size.setAttribute("y", y);
	return id;

    }

    public String newParallelGateway(String x, String y) {

	// PROCESS VIEW
	String id = newId(); 
	Element newNode = doc.createElement("bpmn:parallelGateway");
	newNode.setAttribute("id", id);
	newNode.setAttribute("name", "NEW");
	process.appendChild(newNode);

	// BPMNDI VIEW
	Element newNodeDI = doc.createElement("bpmndi:BPMNShape");
	//bpmndiDiagram.appendChild(newGatDI);
	newNodeDI.setAttribute("bpmnElement", id);
	newNodeDI.setAttribute("id", id + "_di"); // I don't know if this is mandatory
	System.out.println("		I created a new Parallel Gateway with the id " + id);

	Element size = doc.createElement("dc:Bounds");
	bpmndiPlane.appendChild(newNodeDI);

	newNodeDI.appendChild(size);
	size.setAttribute("height", "50");
	size.setAttribute("width", "50");
	size.setAttribute("x", x);
	size.setAttribute("y", y);
	return id;
    }
    
    
    public String newExclusiveGateway(String x, String y) {

	// PROCESS VIEW
	String id = newId(); 
	Element newNode = doc.createElement("bpmn:exclusiveGateway");
	newNode.setAttribute("id", id);
	newNode.setAttribute("name", "NEW");
	process.appendChild(newNode);

	// BPMNDI VIEW
	Element newNodeDI = doc.createElement("bpmndi:BPMNShape");
	//bpmndiDiagram.appendChild(newGatDI);
	newNodeDI.setAttribute("bpmnElement", id);
	newNodeDI.setAttribute("id", id + "_di"); // I don't know if this is mandatory
	System.out.println("		I created a new Parallel Gateway with the id " + id);

	Element size = doc.createElement("dc:Bounds");
	bpmndiPlane.appendChild(newNodeDI);

	newNodeDI.appendChild(size);
	size.setAttribute("height", "50");
	size.setAttribute("width", "50");
	size.setAttribute("x", x);
	size.setAttribute("y", y);
	return id;
    }

    
    
    /**
     * TODO manage BPMNDI TODO give option to create with a condition. Like, "if
     * condition is null, then apply none".
     * 
     * @param sourceID
     * @param targetID
     * @return
     * @throws XPathExpressionException 
     */
    public String newSequenceFlow(String sourceID, String targetID) throws XPathExpressionException {
	String id = newId();
	Element flow = doc.createElement("bpmn:sequenceFlow");
	flow.setAttribute("id", id);
	flow.setAttribute("sourceRef", sourceID);
	flow.setAttribute("targetRef", targetID);
	process.appendChild(flow);
	
	//BPMNDI
	Element newFlowDI = doc.createElement("bpmndi:BPMNEdge");
	bpmndiPlane.appendChild(newFlowDI);
	newFlowDI.setAttribute("bpmnElement", id);
	newFlowDI.setAttribute("id", id+"_di");
	System.out.println("		I created a new SequenceFlow with the id " + id + " and the source " + sourceID
		+ " and the target " + targetID);
	
	Element source = findElemById(sourceID);
	Element target = findElemById(targetID);
	

//	//TODO fare un metodo per avere altezza, larghezza, e posizione di un elemento a partire da un ELEMENT e in una hashmap.
//	//Perché così è brutto.

	Element sourceWP = doc.createElement("di:waypoint");

	Element targetWP = doc.createElement("di:waypoint");

	newFlowDI.appendChild(sourceWP);
	newFlowDI.appendChild(targetWP);
	
	setSource(id, sourceID);
	setTarget(id, targetID);
	
	return id;
    }
    
 



    /**
     * TODO aggiungere un errore quando l'id non corrisponde a un elemento
     * SequenceFlow TODO manage BPMNDI aspects
     * TODO vedi se puoi riutilizzare alcuni pezzi per il metodo newSequenceFlow
     * TODO vedi cosa succede se una task è source di due flow, se ne cambi uno, cosa succede al secondo?
     * @param id
     *            the id of the sequenceFlow that will be changing source
     * @param source
     *            the new source
     * @throws XPathExpressionException
     */
    public void setSource(String id, String source) throws XPathExpressionException {
	System.out.println("PROVAVAAVAVAVA" +  source);
	String previousSourceId =  findElemById(id).getAttribute("sourceRef");
	Element previousSource = findElemById(previousSourceId);
	Element sequenceFlow = findElemById(id);

//	System.out.println("		This element's previous source is: " + previousSource.getAttribute("id"));
//	System.out.println("");
//
//	System.out.println("		Content of child: " + previousSource.getTextContent());
//
//	System.out.println("		Content of 2child: " + xpath.evaluate("./text()", previousSource));

	//TODO both the two lines above do the same thing.
	
	deleteFlowFromOldSourceorTarget(sequenceFlow, previousSource);
//	if (previousSource.hasChildNodes()) { //This is expected to be always true anyway
//	    NodeList childList = previousSource.getChildNodes();
//	    for (int i = 0; i < childList.getLength(); i++) {
//		Node childInCase = childList.item(i);
//		String textContentString = childInCase.getTextContent();
//		System.out.println("		Searching for child to delete, child content in case: " + textContentString);
//		System.out.println("		Searching for child to delete, child i'm looking for: " + id);
//		//TODO add a check to see if it's of the TAG bpmn:outgoing. It should 
//		//be checked in case the task is both the source and the target of a SequenceFlow!
//		if (textContentString.equals(id)){
//		    childInCase.getParentNode().removeChild(childInCase);
//		    System.out.println("		I have found the child that I want to delete!!");
//		    //TODO if you want, find a way to remove the blank space that gets created
//		}
//
//	    }
//
//	}


//	System.out.println("		The id of the sequenceFlow that I have found is " + sequenceFlow.getAttribute("id"));
	sequenceFlow.setAttribute("sourceRef", source);
	// We still need to change also the element of the Source to have my outgoing
	// flow as a child
	Element sourceElement = findElemById(source);
//	System.out.println("		The new source is: " + source);
	Element outgoing = doc.createElement("bpmn:outgoing");
	outgoing.appendChild(doc.createTextNode(id)); //This adds the id as a text inside the tags
	sourceElement.appendChild(outgoing);


	//Since it's impossible to distinguish the source waypoints from the target waipoints, 
	//(aside from looking at the order, but this doesn't seem like a good solution
	//it's best to simply delete all
	//existing waypoints and create two of them from scratch.



	//we want to get the position of the source to know where the flow will have to point

	Element sourceBPMNDI = findBPMNDI(source);

	Element sourceDcBounds = findDcBounds(sourceBPMNDI); //the dc:bounds tag contains the info about the position

	String xSource = sourceDcBounds.getAttribute("x");
	String ySource = sourceDcBounds.getAttribute("y");
	String sourceItemHeight = sourceDcBounds.getAttribute("height");
	String sourceItemWidth = sourceDcBounds.getAttribute("width");


	//we want to get the position of the target to know where the flow will have to point
	String target = sequenceFlow.getAttribute("targetRef");
	Element targetBPMNDI = findBPMNDI(target);
	Element targetDcBounds = findDcBounds(targetBPMNDI); //the dc:bounds tag contains the info about the position
	String xTarget = targetDcBounds.getAttribute("x");
	String yTarget = targetDcBounds.getAttribute("y");
	String targetItemHeight = targetDcBounds.getAttribute("height");
	String targetItemWidth = targetDcBounds.getAttribute("width");

	//Calculating the best options for the placement of the sequenceFlow
	String[] seQFlowPositions = decideArrowPosition(xSource, ySource, sourceItemHeight, sourceItemWidth, xTarget, yTarget, targetItemHeight, targetItemWidth);

	Element sourceWP = createWaypoint(seQFlowPositions[0], seQFlowPositions[1]);
	Element targetWP = createWaypoint(seQFlowPositions[2], seQFlowPositions[3]);

	//This is the bpmndi corresponding to our sequenceFlow
	Element sequenceFlowBPMNDI = findBPMNDI(id);

	//let's remove the previous waypoints:
	while (sequenceFlowBPMNDI.hasChildNodes()) {
	    sequenceFlowBPMNDI.removeChild(sequenceFlowBPMNDI.getFirstChild());
//	    System.out.println("		Just deleted the a waypoint");
	}

	//let's now add the previously created waypoints:
	//NOTE: the order in which the waypoints are added decides the order of the arrow!
	//This is a little bit counterintuitive imho, but it is like it is.
	sequenceFlowBPMNDI.appendChild(sourceWP);
	sequenceFlowBPMNDI.appendChild(targetWP);

	System.out.println("		I have changed the source of flow " + id + " to " + source);
    }
    
        
    /**
     * used to get an item's position
     * (for example when wanting to substitute it
     * TODO change the methods setSource and setTarget to use this method
     * X is in the first position, Y in the second
     * @throws XPathExpressionException 
     */
    public String[] getPosition (Element element) throws XPathExpressionException {
	Element elementBPMNDI = findBPMNDI(element.getAttribute("id"));
	Element dcBounds = findDcBounds(elementBPMNDI); //The dcBounds contains the coordinates
	System.out.println("Get Position  X : "+ dcBounds.getAttribute("x") + "Y: " + dcBounds.getAttribute("y"));
	String [] coordinates = {dcBounds.getAttribute("x"), dcBounds.getAttribute("y")}; 
	return coordinates;
    }    

    /**
     * This method calculates the x and y positions of a new element based on the position of its predecessors
     * It will be useful when placing new elements based on the position of the predecessor and the successor.
     * If as it often happens an element has more than one predecessor and more than one successor,
     * first we calculate the average position of the successors, then the average position of the predecessors
     * And we place the new element in the average of the two averages.
     * 
     *
     * TODO explain this in the thesis
     *  
     * @param predecessorId
     * @param successorId
     * @return positions, the coordinates of the X axis are in the [0] position while the y information is
     *  stored on the [1] position
     * @throws NumberFormatException 
     * @throws XPathExpressionException 
     */
    public String[] calculatePositionOfNewNode(ArrayList<Element> predecessors, ArrayList<Element> successors) throws NumberFormatException, XPathExpressionException {

	//let's calculate the average positions of the predecessors on the X axis
	int avgPredX = 0;
	for (Element predecessor : predecessors) {
	    avgPredX += Double.parseDouble(getPosition(predecessor)[0]);
	}
	avgPredX = avgPredX/predecessors.size();

	//let's calculate the average positions of the predecessors on the Y axis
	int avgPredY = 0;
	for (Element predecessor : predecessors) {
	    avgPredY += Double.parseDouble(getPosition(predecessor)[1]);
	}
	avgPredY = avgPredY/predecessors.size();


	//let's calculate the average positions of the successors on the X axis
	int avgSuccX = 0;
	for (Element successor : successors) {
	    avgSuccX += Double.parseDouble(getPosition(successor)[0]);
	}
	avgSuccX = avgSuccX/successors.size();

	//let's calculate the average positions of the successors on the Y axis
	int avgSuccY = 0;
	for (Element successor : successors) {
	    avgSuccY += Double.parseDouble(getPosition(successor)[1]);
	}
	avgSuccY = avgSuccY/successors.size();


	String[] positions = new String[2];
	positions[0] = "" + (avgPredX+avgSuccX)/2; //not super precise but enough for our scope
	positions[1] = "" + (avgPredY+avgSuccY)/2;

	return positions;
    }

    //Allowing for different situations, different inputs:
    public String[] calculatePositionOfNewNode(ArrayList<Element> predecessors, Element successor) throws NumberFormatException, XPathExpressionException{
	ArrayList<Element> successors = new ArrayList<Element>();
	successors.add(successor);
	String[] positions = calculatePositionOfNewNode(predecessors, successors);
	return positions;
    }
    public String[] calculatePositionOfNewNode(Element predecessor, ArrayList<Element> successors) throws NumberFormatException, XPathExpressionException{
	ArrayList<Element> predecessors = new ArrayList<Element>();
	predecessors.add(predecessor);
	String[] positions = calculatePositionOfNewNode(predecessors, successors);
	return positions;
    }



    /**
     * TODO cambialo per avere due element invece di due id come parametri
     * TODO in generale cerca di cambiare tutti i metodi per avere elementi invece che stringhe come parametri
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

	String previousTargetId =  findElemById(id).getAttribute("targetRef");
	Element previousTarget = findElemById(previousTargetId);
//	System.out.println("		This element's previous target is: " + previousTarget.getAttribute("id"));
//	System.out.println("");
//
//	System.out.println("		Content of child: " + previousTarget.getTextContent());

	Element sequenceFlow = findElemById(id);


	deleteFlowFromOldSourceorTarget(sequenceFlow, previousTarget);

	
//	System.out.println("		The id of the sequenceFlow that I have found is " + sequenceFlow.getAttribute("id"));
	sequenceFlow.setAttribute("targetRef", target);
	// We still need to change also the element of the Source to have my outgoing
	// flow as a child
	Element targetElement = findElemById(target);
//	System.out.println("		The new target is: " + targetElement.getAttribute("id"));
	Element incoming = doc.createElement("bpmn:incoming");
	incoming.appendChild(doc.createTextNode(id)); //This adds the id as a text inside the tags
	targetElement.appendChild(incoming);

	//Since it's impossible to distinguish the source waypoints from the target waipoints, 
	//(aside from looking at the order, but this doesn't seem like a good solution
	//it's best to simply delete all
	//existing waypoints and create two of them from scratch.

	//we want to get the position of the source to know where the flow will have to point

	Element targetBPMNDI = findBPMNDI(target);

	Element targetDcBounds = findDcBounds(targetBPMNDI); //the dc:bounds tag contains the info about the position
	
	//TODO make this position thing a separate method
	System.out.println("Positions will follow");
	String xTarget = targetDcBounds.getAttribute("x");
	System.out.println(xTarget);
	String yTarget = targetDcBounds.getAttribute("y");
	System.out.println(yTarget);
	String targetItemHeight = targetDcBounds.getAttribute("height");
	System.out.println(targetItemHeight);
	String targetItemWidth = targetDcBounds.getAttribute("width");
	System.out.println(targetItemWidth);


	//we want to get the position of the target to know where the flow will have to point
	String source = sequenceFlow.getAttribute("sourceRef");
	Element sourceBPMNDI = findBPMNDI(source);
	Element sourceDcBounds = findDcBounds(sourceBPMNDI); //the dc:bounds tag contains the info about the position
	String xSource = sourceDcBounds.getAttribute("x");
	System.out.println(xSource);
	String ySource = sourceDcBounds.getAttribute("y");
	System.out.println(ySource);
	String sourceItemHeight = sourceDcBounds.getAttribute("height");
	System.out.println(sourceItemHeight);
	String sourceItemWidth = sourceDcBounds.getAttribute("width");
	System.out.println(sourceItemWidth);

	//Calculating the best options for the placement of the sequenceFlow
	String[] seQFlowPositions = decideArrowPosition(xSource, ySource, sourceItemHeight, sourceItemWidth, xTarget, yTarget, targetItemHeight, targetItemWidth);


	Element sourceWP = createWaypoint(seQFlowPositions[0], seQFlowPositions[1]);
	Element targetWP = createWaypoint(seQFlowPositions[2], seQFlowPositions[3]);

	//This is the bpmndi corresponding to our sequenceFlow
	Element sequenceFlowBPMNDI = findBPMNDI(id);

	//let's remove the previous waypoints:
	while(sequenceFlowBPMNDI.hasChildNodes()) {
	    sequenceFlowBPMNDI.removeChild(sequenceFlowBPMNDI.getFirstChild());
	    System.out.println("		Just deleted the waypoint");
	}

	//let's now add the previously created waypoints:
	//NOTE: the order in which the waypoints are added decides the order of the arrow!
	//This is a little bit counterintuitive imho, but it is like it is.
	sequenceFlowBPMNDI.appendChild(sourceWP);
	sequenceFlowBPMNDI.appendChild(targetWP);

	findDcBounds(targetBPMNDI);

	System.out.println("		I have changed the source of flow " + id + " to " + source);
    }

    /**
     * TODO if a sequenceFlow comes out of
     * a parallel gateway it might be that two sequence flows from outgoingFlows
     * have the same source. It should not happen, but if it does I should remove
     * duplicates 
     * TODO test this
     * 
     * @param id
     *            the id of said Element
     * @return a NodeList of the successors of a certain Element
     * @throws XPathExpressionException
     */
    public ArrayList<Element> getSuccessors(Element element) throws XPathExpressionException {


	ArrayList<Element> outgoingFlows = getOutgoingFlows(element);
	ArrayList<Element> successors = new ArrayList<Element>();

	for (int i = 0; i < outgoingFlows.size(); i++) {
	    successors.add(findElemById(outgoingFlows.get(i).getAttribute("targetRef"))); 
	}
	//System.out.println("		I have found " + successors.size() + " immediate successors");
	return successors;
    }


    public ArrayList<Element> getPredecessors(Element element) throws XPathExpressionException{
	ArrayList<Element> incomingFlows = getIncomingFlows(element);
	ArrayList<Element> predecessors = new ArrayList<Element>();

	for (int i = 0; i < incomingFlows.size(); i++) {
	    predecessors.add(findElemById(incomingFlows.get(i).getAttribute("sourceRef")));
	}
	//System.out.println("		I have found " + predecessors.size() + " immediate predecessors");
	return predecessors;
    }
    /**
     * From an element, get its outgoing flows as an array of elements
     * @param element
     * @return
     * @throws XPathExpressionException 
     */
    public ArrayList <Element> getOutgoingFlows (Element element) throws XPathExpressionException {

	String id = element.getAttribute("id");
	NodeList outgoingFlowsNodes = (NodeList) xpath.evaluate("//*[@sourceRef='" + id + "']", doc, XPathConstants.NODESET);
	ArrayList<Element> outgoingFlows = new ArrayList<Element>();
	for (int i = 0; i < outgoingFlowsNodes.getLength(); i++) { //TODO why not <=?
	    //System.out.println("Getting outgoingFlow: "+ ((Element) outgoingFlowsNodes.item(i)).getAttribute("id"));
	    outgoingFlows.add((Element) outgoingFlowsNodes.item(i));
	}
	if (outgoingFlows.size() == 0) {
	    System.out.println("this element has no outgoing Flows");
	} else { 
	    //System.out.println("The element " + element.getAttribute("id") + " has " + outgoingFlows.size() + " outgoing Flows");
	}

	return outgoingFlows;

    }

    /**
     * From an element, get its incoming flows as an array of elements
     * @param element
     * @return
     * @throws XPathExpressionException 
     */
    public ArrayList <Element> getIncomingFlows (Element element) throws XPathExpressionException {

	String id = element.getAttribute("id");
	NodeList incomingFlowsNodes = (NodeList) xpath.evaluate("//*[@targetRef='" + id + "']", doc, XPathConstants.NODESET);
	ArrayList<Element> incomingFlows = new ArrayList<Element>();
	for (int i = 0; i < incomingFlowsNodes.getLength(); i++) { //TODO why not <=?
	    //System.out.println("Getting incomingFlow: "+ ((Element) incomingFlowsNodes.item(i)).getAttribute("id"));
	    incomingFlows.add((Element) incomingFlowsNodes.item(i));
	}
	if (incomingFlows.size() == 0) {
	    System.out.println("this element has no incoming Flows");
	} else { 
	    //
	    System.out.println("The element " + element.getAttribute("id") + " has " + incomingFlows.size() + " incoming Flows");
	}

	return incomingFlows;

    }
    

    /**
     * This checks whether a sequenceFlow has a condition or not.
     * If it has not, then there's no need to generate one and append it to the new flows.
     * TODO delete this method and use returnCondition instead
     * @return
     */
    public boolean hasCondition(Element sequenceFlow) {
	NodeList children =  sequenceFlow.getElementsByTagName("bpmn:conditionExpression"); //TODO

	boolean hasCondition = false;

	if (children.getLength() > 0) {
	    hasCondition = true; 
	}
	return hasCondition;
    }

    public String returnConditionString (Element sequenceFlow) {
	Element conditionElement = returnConditionElement(sequenceFlow);
	String condition = conditionElement.getTextContent();
	return condition;
    }

    public Element returnConditionElement (Element sequenceFlow) {
	NodeList children = sequenceFlow.getElementsByTagName("bpmn:conditionExpression"); //TODO
	if (children.getLength() > 1) {
	    System.err.println("How can an array have more than one condition children?");
	}
	return (Element) children.item(0);
    }

    /**
     * TODO maybe put this in the method class
     * If a condition is present, it deletes it and then adds the new one inside.
     */
    public void applyCondition(Element sequenceFlow, String condition) {
	if (hasCondition(sequenceFlow)) { 
	    // getting all the conditions of a sequenceFlow
	    ArrayList<Element> children = 
		    (ArrayList<Element>) sequenceFlow.getElementsByTagName("bpmn:conditionExpression"); //TODO
	    //removing all the previous conditions:
	    for (int i = 0; i < children.size(); i++) {
		sequenceFlow.removeChild(children.get(i));
	    }
	}
	Element conditionElement = doc.createElement("bpmn:conditionExpression");
	conditionElement.setAttribute("xsi:type", "bpmn:tFormalExpression");
	conditionElement.appendChild(doc.createTextNode(condition));
	sequenceFlow.appendChild(conditionElement);
    }

    /**
     * Returns the type (tagname) of an element
     * TODO maybe this can be deleted. See which methods use it
     * @param id
     * @return
     */
    public String getType(String id) {
	String type = doc.getElementById(id).getTagName();
	return type;
    }


    //TODO this would be better if I had a class sequenceFlow    
    public Element getTarget (Element sequenceFlow) throws XPathExpressionException {

	String targetID = sequenceFlow.getAttribute("targetRef");
	Element target = findElemById(targetID);
	return target;

    }

    //TODO this would be better if I had a class sequenceFlow
    public Element getSource (Element sequenceFlow) throws XPathExpressionException {

	String sourceID = sequenceFlow.getAttribute("sourceRef");
	Element source = findElemById(sourceID);
	return source;

    }

    
    //TODO use this method in other rules
    public void deleteIncomingFlows(Element element) throws XPathExpressionException {
	ArrayList<Element> incomingFlows = getIncomingFlows(element);
	for (Element flow : incomingFlows) {
	    delete(flow.getAttribute("id"));
	}
	
    }
    //TODO use this method in other rules
    public void deleteOutgoingFlows(Element element) throws XPathExpressionException {
	ArrayList<Element> outgoingFlows = getOutgoingFlows(element);
	for (Element flow : outgoingFlows) {
	    delete(flow.getAttribute("id"));
	}
    }

    /**
     * TODO make this method use the element as input
     * @param id
     *            the ID of the element to delete
     * @throws XPathExpressionException
     */
    public void delete(String id) throws XPathExpressionException {
	System.out.println("		I'm deleting the element with id " + id);
	Element elementToDelete = findElemById(id);
	String type = elementToDelete.getTagName();


	//If the element is a sequence flow, we also need to delete it from it's target / source children	
	if (type == "bpmn:sequenceFlow") {
	    //Finding the parents
	    Element oldSource = getSource(elementToDelete);
	    Element oldTarget = getTarget(elementToDelete);
	    //removingTheElement
	    deleteFlowFromOldSourceorTarget(elementToDelete, oldSource);
	    deleteFlowFromOldSourceorTarget(elementToDelete, oldTarget);

	}


	//from the PROCESS
	elementToDelete.getParentNode().removeChild(elementToDelete);
	System.out.println("		I've deleted the element with id " + id);

	//FROM THE BPMNDI
	Element bpmndiElementToDelete = findBPMNDI(id);
	System.out.println(bpmndiElementToDelete.getAttribute("bpmnElement"));
	bpmndiElementToDelete.getParentNode().removeChild(bpmndiElementToDelete);



    }
    
    public void delete(Element element) throws XPathExpressionException {
	String id = element.getAttribute("id");
	delete(id);
    }


    //TODO use this inside the SetSource and SetTarget methods.
    public void deleteFlowFromOldSourceorTarget (Element sequenceFlow, Element oldSourceOrTarget) {
	if (sequenceFlow.getTagName() != "bpmn:sequenceFlow") {
	    System.out.println("This method is only for sequenceFlows");
	} else if (sequenceFlow.getTagName() == "bpmn:sequenceFlow") {
	    String id = sequenceFlow.getAttribute("id");
	    if (oldSourceOrTarget.hasChildNodes()) { //This is expected to be always true anyway
		NodeList childList = oldSourceOrTarget.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
		    Node childInCase = childList.item(i);
		    String textContentString = childInCase.getTextContent();
		    //System.out.println("		Searching for child to delete, child content in case: " + textContentString);
		    //System.out.println("		Searching for child to delete, child i'm looking for: " + id);
		    //TODO add a check to see if it's of the TAG bpmn:outgoing. It should 
		    //be checked in case the task is both the source and the target of a SequenceFlow!
		    if (textContentString.equals(id)){
			childInCase.getParentNode().removeChild(childInCase);
			//System.out.println("		I have found the child that I want to delete!!");
			//TODO if you want, find a way to remove the blank space that gets created
		    }

		}

	    }       
	}
    }



    /**
     * Replace the old element with the new.
     * In principle, nothing else should be affected (i.e. the incoming / outgoing sequenceFlows)
     * @param id
     * @param newElement
     * @throws XPathExpressionException 
     */
    public void replaceELement(Element oldElem, Element newElem) throws XPathExpressionException {

	//It's also useful to have the bpmndi ready to edit:
	Element newElementBPMNDI = findBPMNDI(newElem.getAttribute("id"));


	//Let's save the ID of the old element
	String oldId = oldElem.getAttribute("id");

	//let's save the child elements of the oldElement
	//those child elements will contain the oldElement's incoming and outgoing sequenceFlows
	//we will soon attach them to the newElement
	NodeList oldElemChildNodes = oldElem.getChildNodes();

	//Let's now append the child nodes of the old element to the new element
	for (int n = 0; n < oldElemChildNodes.getLength(); n++) {

	    Node childNode = oldElemChildNodes.item(n);
	    newElem.appendChild(childNode.cloneNode(true));
	}


	//let's delete the old element from the process
	process.removeChild(oldElem);
	bpmndiDiagram.removeChild(newElementBPMNDI);
	//let's delete the BPMNDI of the new element (we will use that of the old element)

	//let's change the ID of the new element to be equal to the id of the old one
	newElem.setAttribute("id", oldId);
	String newElemId = newElem.getAttribute("id");

	//let's remember to change the id of the BPMNDI as well:
	//TODO DELETETHIS newElementBPMNDI.setAttribute("bpmnElement", newElemId);
	//TODO DELETETHIS newElementBPMNDI.setAttribute("id", newElemId + "_di");

    }
    
    /**
     * TODO test this
     * @param element
     */
    public void changeType(Element element, String newType) {
	doc.renameNode(element, doc.getDocumentURI(), newType);
    }

    /**
     * This method is used to delete elements from the diagram in an XML file. Note
     * that I will use the attribute "BMPMN Element" to find the BMPNDI element that
     * I want to delete, instead of "id", because the ID of a bpmndi element will
     * sometimes be different from the corresponding BPMN element's own iD.
     * 
     * TODO probably this method shall be deleted (pun intended)
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
    /**
     * NOTE this deletes all children of an element
     * @param id
     * @throws XPathExpressionException
     */
    public void deleteChildren(String id) throws XPathExpressionException {
	Element element = findElemById(id);
	if (element.hasChildNodes()) { //This is expected to be always true anyway
	    NodeList childList = element.getChildNodes();
	    for (int i = 0; i < childList.getLength(); i++) {
		Node childInCase = childList.item(i);
		String textContentString = childInCase.getTextContent();
		//System.out.println("		Searching for child to delete, child content in case: " + textContentString); UNLOCKTHIS
		//System.out.println("		Searching for child to delete, child i'm looking for: " + id); UNLOCKTHIS
		//TODO add a check to see if it's of the TAG bpmn:outgoing. It should 
		//be checked in case the task is both the source and the target of a SequenceFlow!
		if (textContentString.equals(id)){
		    childInCase.getParentNode().removeChild(childInCase);
		    //System.out.println("		I have found the child that I want to delete!!");
		    //TODO if you want, find a way to remove the blank space that gets created
		}

	    }

	}
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
     * Used to find elements of a certain type.
     * @return a list of elements of the desired tag type.
     * NOTE: can also be used to find other tagnames.
     * 
     */
    public Element[] findElemByType(String tagname) {
	Element[] elements = new Element[9];
	//Element[] elements = (Element[]) doc.getElementsByTagName(tagname);
	//TODO see if I want to do this method or simply use getElementsByTagName
	return elements;
    }
    /**
     * ASKANA if this is a good method to recognize merges
     * @param gateway
     * @return
     * @throws XPathExpressionException
     */
    public boolean isAMerge(Element gateway) throws XPathExpressionException {
	//if (gateway.getTagName()) TODO check that is a gateway and return a message when it's not
	if (getIncomingFlows(gateway).size() > 1 && getOutgoingFlows(gateway).size() == 1) {
	    return true;
	} else { 
	    return false;
	}
    }
    
    //TODO spiega nella tesi che isAmerge e isASplit sono mutualmente esclusivi ma non del tutto
    //perché potrebbe esserci un elemento che non è né uno split né un merge se ha un solo incoming e un solo outgoingflow
    //anche se non sarebbe corretto secondo lo standard BPMN 2.0
    public boolean isASplit(Element gateway) throws XPathExpressionException {
	if (getOutgoingFlows(gateway).size() > 1 && getIncomingFlows(gateway).size()==1) {
	    return true;
	} else {
	    return false;
	}
	
    }
    
    /**
     * An useless gateway is a gateway that has one incoming and one outgoing flow.
     * It is illegal per BPMN 2.0
     * 
     * TODO ricordati di spiegarlo nella tesi
     * @param gateway
     * @return
     * @throws XPathExpressionException 
     */
    public boolean isUselessGateway (Element gateway) throws XPathExpressionException {
	if (getOutgoingFlows(gateway).size() == 1 && getIncomingFlows(gateway).size() == 1) {
	    return true;
	} else {
	    return false;
	}
    }
    /*
     * We delete an useless gateway by removing its incomingFlow, then setting its outgoing flow
     * to have its predecessor as a target and finally deleting the gateway.
     */
    public void deleteUselesGateway(Element gateway) throws XPathExpressionException {
	if (!isUselessGateway(gateway)) {
	    System.out.println("This gateway is not useless and should not be deleted!");
	} else {
	    setSource(getOutgoingFlows(gateway).get(0).getAttribute("id"), getPredecessors(gateway).get(0).getAttribute("id"));
	    delete(getIncomingFlows(gateway).get(0));
	    delete(gateway);
	}
	
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
	System.out.println("Found the BPMNDI element corresponding to " + id);
	System.out.println("The id of the BPMNDI is " + ((Element) bpmndi).getAttribute("id"));
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
    public Element findDcBounds(Element bpmndiElement){
	String idofBPMNDI = bpmndiElement.getAttribute("id");
	System.out.println("The id of the bpmndi is " + idofBPMNDI);
	//Element dcBounds = (Element) xpath.evaluate("//dc:Bounds", bpmndiElement, XPathConstants.NODE); // TODO delete this line
	Element dcBounds = (Element) bpmndiElement.getElementsByTagName("dc:Bounds").item(0);

	if (dcBounds == null) {
	    System.err.println("It means that this bpmdiElement has not any dc:Bounds item. This is not expected");
	}

	return dcBounds;

    }





    /**
     * This method creates the waypoints used in the BPMNDI to position the sequenceFlows
     * This method is used both to create the waypoints of the "pointy" side
     * of the arrow and of the flat side of the arrows, as they are identical.
     * The program decided the direction in which the arrow points, depending on the order of the waypoints.
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
     * This item decides in which exact position to put the end or the start of the sequenceFlow
     * based on the target/source location and their height / width.
     * This way we can have the arrow nicely connect to the border of the items
     *  
     *  Note that since the gateways are tilted squares, it's better to only
     *  point the arrows in the 4 corners of items. This works best for both tasks and 
     *  gateways. 
     *  
     *  NOTE that counterintuitively, with this system, the higher the value of Y, the LOWER an item is.
     *  
     *  TODO this can be done more elegantly, with less repetition of code, and also in a way
     *  to avoid problems when an item is just SLIGHTLY higher or lower than the other
     *  Another idea would be to create 4 candidate anchor points for both items,
     *  go through each permutation (16 in total) of the 4 anchor points 
     *  and select the one with the lower distance. This would take less code and be a more 
     *  generalized solution, but it would be a little bit overkill maybe.
     *  NOTE at one point you will see an if inside an if:
     *  	since we don't want to place the arrows on the corner,
     *  but on the side,only one of the following operations 
     *  has to be performed we decide which one based on which one
     *  is the greatest distance, the horizontal o
     *  or the vertical one

     *  
     * @param type
     * @param x
     * @param y
     * @return
     */
    public String[] decideArrowPosition(String sourceXPosition, String sourceYPosition, String sourceItemHeight, String sourceItemWidth, String targetXPosition, String targetYPosition, String targetItemHeight, String targetItemWidth) {

	//Transforming everything into ints to do the calculations
	double sourceX = Double.parseDouble(sourceXPosition);
	double sourceY = Double.parseDouble(sourceYPosition);
	double sourceHeight = Double.parseDouble(sourceItemHeight);
	double sourceWidth = Double.parseDouble(sourceItemWidth);

	double targetX = Double.parseDouble(targetXPosition);
	double targetY = Double.parseDouble(targetYPosition);
	double targetHeight = Double.parseDouble(targetItemHeight);
	double targetWidth = Double.parseDouble(targetItemWidth);

	//To be precise, X and Y are not the exact centers of the item
	//instead, they represent the upper left corner of an item. 
	//We will replace them with the actual centers.
	//We deduce the position of the centers by using the width and height
	//of items:
	sourceX = sourceX + (sourceWidth/2);
	sourceY = sourceY + (sourceHeight/2);

	targetX = targetX + (targetWidth/2);
	targetY = targetY + (targetHeight/2);

	double horizontalDiff = sourceX - targetX;
	// if it's positive it means that the source it's on below the target 
	// (Y coordinates work in the opposite way as they normally would in a graph, for some reason)

	double verticalDiff = sourceY - targetY;
	// if it's positive it means that the source it's on the right of the target

	// let's prepare the resulting Ints
	// of course the start will be connected to the source and the 
	// end will be connected to the target

	double resultStartX = sourceX;
	double resultStartY = sourceY;
	double resultEndX = targetX;
	double resultEndY = targetY;

	if (horizontalDiff == 0 && verticalDiff == 0) {
	    //This should be almost impossible
	    //nonetheless, i tested it and it works
	    System.out.println("The source and target are in the same position ! ");    
	} else if (horizontalDiff == 0 && verticalDiff < 0) {
	    //this works fine
	    System.out.println("The source and target are in the same horizontal position but the target is lower");
	    resultStartY = sourceY + (sourceHeight/2);
	    resultEndY = targetY - (targetHeight/2);

	} else if (horizontalDiff == 0 && verticalDiff > 0) {
	    //this works as intended
	    System.out.println("The source and target are in the same horizontal position but the target is higher");   
	    resultStartY = sourceY - (sourceHeight/2);
	    resultEndY = targetY + (targetHeight/2);

	} else if (horizontalDiff > 0 && verticalDiff == 0) {
	    //this works as intended
	    System.out.println("The source and target are at the same height but the target is left of the source");
	    resultStartX = sourceX - (sourceWidth/2);
	    resultEndX =  targetX + (targetWidth/2);

	} else if (horizontalDiff < 0 && verticalDiff == 0) {
	    //this works as intended
	    System.out.println("The source and target are at the same height but the target is right of the source");
	    resultStartX = sourceX + (sourceWidth/2);
	    resultEndX =  targetX - (targetWidth/2);

	} else if (horizontalDiff < 0 && verticalDiff < 0) {
	    //this works as intended

	    System.out.println("The target is on the lower right of the source");

	    if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {
		resultStartY = sourceY + (sourceHeight/2);
		resultEndY = targetY - (targetHeight/2);
	    } else {
		resultStartX = sourceX + (sourceWidth/2);
		resultEndX =  targetX - (targetWidth/2);
	    }

	} else if (horizontalDiff < 0 && verticalDiff > 0) {
	    //this works as intended

	    System.out.println("The target is on the upper right of the source");

	    if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {

		resultStartY = sourceY - (sourceHeight/2);
		resultEndY = targetY + (targetHeight/2);
	    } else {
		resultStartX = sourceX + (sourceWidth/2);
		resultEndX =  targetX - (targetWidth/2);
	    }
	} else if (horizontalDiff > 0 && verticalDiff < 0) {
	    //this works as intended
	    System.out.println("The target is on the lower left of the source");
	    if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {
		resultStartY = sourceY + (sourceHeight/2);
		resultEndY = targetY - (targetHeight/2);
	    } else {
		resultStartX = sourceX - (sourceWidth/2);
		resultEndX =  targetX + (targetWidth/2);
	    }
	} else if (horizontalDiff > 0 && verticalDiff > 0) {
	    //this works as intended

	    System.out.println("The target is on the upper left of the source");

	    if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {
		resultStartY = sourceY - (sourceHeight/2);
		resultEndY = targetY + (targetHeight/2);
	    } else {
		resultStartX = sourceX - (sourceWidth/2);
		resultEndX =  targetX + (targetWidth/2);
	    }
	}

	//finally
	//let's transform back our Ints into string, ready to be put into an XML
	//TODO there's probably a better way to return this info, maybe as an object? Maybe as a map.
	String[] positions = new String[4];
	positions[0] = "" + resultStartX; 
	positions[1] = "" + resultStartY;
	positions[2] = "" + resultEndX;
	positions[3] = "" + resultEndY;

	return positions;
    }





    /**
     * TODO explain in the thesis that a QName (what is it?) cannot start with
     * a number but a letter.
     * 
     * @return
     */
    public String newId() {
	return "USI" + UUID.randomUUID().toString(); //I added 'USI' because Ids cannot start with a digit.
    }

}