package bpmn.transformation2;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BpmndiUtilities {

    Map<String,String> elementsToDelete; //TODO find a better place to put this. Maybe in the same location of the report informations? 
    //Maybe creating an object where I will remember everything that has been done? 

    /**
     * This method is used to delete elements from the bpmndi diagram in an XML file. 
     * Unfortunately Camunda only edits the process part of an xml,
     * while we also want to edit the BPMDI side of things to display our diagram correctly.
     *  
     * Note that i will use the attribute "BMPMN Element" to find the BMPNDI element that I want to delete, instead of id, because
     * the ID of a bpmndi element will sometimes be different from the corresponding BPMN element.
     * 
     * Future improvements might be finding a way to avoid looping two times for a single entry (one time for the tag and one time for the ID), and instead
     * Looking first at a certain tag and then at all the elements of that tag. This way we only will loop once per tag.
     * 
     * TODO even better, if I can find a way to look at every element regardless of its tag,
     * 
     * @param path of the file that I want to open 
     * @param idAndTags a map that contains the IDs of the element i want to delete and the tag that they have.
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void xmlDeleteFromBpmndi (String path, String[] ids) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, TransformerConfigurationException {

	//TODO make it look better
	System.out.println("Starting to delete BMPNDI elements");
	System.out.println("Starting to delete BMPNDI elements");
	System.out.println("Starting to delete BMPNDI elements");

	//Xpath is used to simplify the interaction with my XML file.
	XPathFactory xPathFactory = XPathFactory.newInstance(); 
	XPath xpath = xPathFactory.newXPath();


	//building the document
	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	Document doc = docBuilder.parse(path);

	Element bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0); //there's always one single bmpdi:BMPNDiagram, so it's "item 0"
	System.out.println("I'm working on the following bpmndiDiagram: "+ bpmndiDiagram.getAttribute("id"));

	//Looking for each id in ids:
	for (String id : ids) {
	    System.out.println("I'm searching for id: " + id);
	    NodeList bpmndiList = (NodeList) xpath.evaluate("//*[@bpmnElement='"+ id + "']", doc, XPathConstants.NODESET);

	    //NOTE this 'for' is probably overkill, since I expect to find only one element. TODO decide if you want to keep it in case there's more than one or not.
	    for (int i = 0; i < bpmndiList.getLength(); ++i) {
		Element bpmndiElement = (Element) bpmndiList.item(i);
		System.out.println(bpmndiElement.getAttribute("bpmnElement"));
		bpmndiElement.getParentNode().removeChild(bpmndiElement);
		System.out.println("I removed element with ID: " + id);
	    }
	}

	System.out.println("Done deleting BMPNDI elements"); //TODO make it look better
	System.out.println("Done deleting BMPNDI elements");
	System.out.println("Done deleting BMPNDI elements");
	System.out.println("Saved the XML file in + " + path);
	System.out.println("Saved the XML file in + " + path);
	System.out.println("Saved the XML file in + " + path);

	//Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(path));
	transformer.transform(source, result);

    }
}






