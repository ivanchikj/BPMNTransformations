package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import org.camunda.bpm.model.bpmn.instance.Task;
import org.joda.time.LocalDateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;


//TODO This class is just for tests, and will later be deleted

public class Trials {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, TransformerException {
	xmlDeletingTest();
	System.out.println("provaXX"); 
    }

    public static void xmlDeletingTest () throws Exception, SAXException, IOException, ParserConfigurationException, TransformerException, XPathExpressionException {
	//TODO later the input model and the element id shall be parameters.
	//Also, it should be an array of all the IDs to delete, together with their Tag Name
	//Another thing to note is that for some reason the ID of an element sometimes they add "_di" at the end of the ID.
	//But I think it's only for elements with the "bpmndi:BPMNEdge" tag.
	
	XPathFactory xPathFactory = XPathFactory.newInstance(); //Xpath is used to simplify the interaction with my XML file.
        XPath xpath = xPathFactory.newXPath();
	
	String id = "ExclusiveGateway_11hlkwm_di";
	String path = "./TestGraphs/DiagramForRule4a.bpmn.xml";
	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	Document doc = docBuilder.parse(path);
	
	Element bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0); //there's always one single bmpdi:BMPNDiagram, so it's "item 0"
	System.out.println("I'm working on the following bpmndiDiagram: "+ bpmndiDiagram.getAttribute("id"));
	
	NodeList bpmndiList = (NodeList) xpath.evaluate("//*[@key='mykey1']", doc, XPathConstants.NODESET);
	
	for (int i = 0; i < bpmndiList.getLength(); ++i) {
	    
	    Element bpmndi = (Element) bpmndiList.item(i);
	    
	    System.out.println(bpmndi.getAttribute("bpmnElement"));
	    
	    if (bpmndi.getAttribute("bpmnElement").equals(id)) {
	    bpmndi.getParentNode().removeChild(bpmndi); // This actually works.
	    System.out.println("I removed element with ID: " + id);
	    }
	    
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path));
		transformer.transform(source, result);

		System.out.println("Done");
		System.out.println(i);
		System.out.println("Done");
		System.out.println(i);
		System.out.println("Done");
		
	    }
	}

    public static void insertXMLelement() throws SAXException, IOException, ParserConfigurationException {
	//TODO later the input model and the element data shall be parameters. And also the parent element to find the right location in the XML
	//And also the exact position should be given as input, but first it should be calculated by a third method that gets the information of the neighbors and computes the averages
	//of the position of the neighbors. THose two averages will be the Y-position and the X-position of the new element here.
	//we also have to create a BPMNLabel with the title.
	
	//ASKANA do we have to consider the "lane" in which we are? Since now we just worked with one single (invisible) lane, but in theory there could be more.
	//This affects also the applicability of the rules, because if a structure that we identify as "ready to be transformed" spans on more than one lane, the rule is actually not
	//applicable because it would change the lane of the corresponding element.
	
	//Otherwise TODO add this to the limitations on the readme (and on the thesis)
	
	String id = "ExclusiveGateway_11hlkwm_di";
	String path = "./TestGraphs/DiagramForRule4a.bpmn.xml";
	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	Document doc = docBuilder.parse(path);
	
	Node bpmndiLane = doc.getElementsByTagName("bpmndi:BPMNPlane").item(0); //because I know there's only one lane
	bpmndiLane.appendChild();
	

	
    }



    

    public static void writeReportToFile(String report, String folderPath) throws IOException {
	LocalDateTime timestamp = LocalDateTime.now();
	BufferedWriter writer = new BufferedWriter( new PrintWriter(folderPath + "Report" + timestamp + ".txt"));
	writer.write(report);
	writer.close( );
    }

    public static void deletingTest (BpmnModelInstance inputModel) {

	Collection <Process> Processes = inputModel.getModelElementsByType(Process.class); 
	//ASKANA can an xml file contain more than one process? If so in the main class I should have a loop going through all processes
	//ASKANA But if that's the case, then i cannot simply change the "applyRule" methods to use a process instead of an inputModela s a parameter
	//ASKANA because the bpmndi is not contained in the process.
	Process process = Processes.iterator().next();
	System.out.println("WORKING ON PROCESS : " +  process.getId());


	Collection <Task> tasks = inputModel.getModelElementsByType(Task.class);


	Collection <SequenceFlow> sequenceFlows = process.getChildElementsByType(SequenceFlow.class);

	int counter = 1;
	for (SequenceFlow flow : sequenceFlows) {
	    System.out.println(flow.getId());
	    flow.setName("" + counter);
	    counter++;
	}

	Task task = tasks.iterator().next();

	Collection <SequenceFlow> outgoingFlows = task.getOutgoing();

	SequenceFlow outFlow = outgoingFlows.iterator().next();
	task.getOutgoing().remove(outFlow); //ASKANA this does not work
	process.removeChildElement(outFlow);

	Collection <SequenceFlow> incomingFlows = task.getIncoming();

	SequenceFlow inFlow = incomingFlows.iterator().next();
	task.getIncoming().remove(inFlow); //ASKANA this does not work
	process.removeChildElement(inFlow);

	process.removeChildElement(task);
	System.out.println("removing flow : " + outFlow.getName());



	Collection <EndEvent> endEvent = process.getChildElementsByType(EndEvent.class);
	EndEvent end = endEvent.iterator().next();
	end.setName("Fine");

	for (SequenceFlow flow : sequenceFlows) {
	    if (flow.getName().toString().equals("3")) {
		System.out.println("Found it");
		//flow.setName("Found it"); //This works
		//flow.setTarget(task); //ASKANA this does not work (on camunda)

	    }

	}
    }
}