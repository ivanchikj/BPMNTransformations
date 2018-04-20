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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
    public static void xmlDeleting (String path, Map<String,String> idAndTags) throws SAXException, IOException, ParserConfigurationException, TransformerException {

	//TODO make it look better
	System.out.println("Starting to delete BMPNDI elements");
	System.out.println("Starting to delete BMPNDI elements");
	System.out.println("Starting to delete BMPNDI elements");

	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	Document doc = docBuilder.parse(path);

	//I go through each entry in my map:
	Iterator<Entry<String, String>> it = idAndTags.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String,String> entry = it.next();
	    NodeList bpmndiList = doc.getElementsByTagName(entry.getValue()); // I hope getValue gives me the Tag and not the Id
	    //I go through each corresponding element in my document that has a certain tag.
	    for (int i = 0; i < bpmndiList.getLength(); ++i) {
		Element bpmndiElement = (Element) bpmndiList.item(i);
		System.out.println("I'm looking at the element " +  bpmndiElement.getAttribute("bpmnElement"));
		//If the ID is the one that I'm looking for, delete the element.
		if (bpmndiElement.getAttribute("bpmnElement").equals(it)) {
		    bpmndiElement.getParentNode().removeChild(bpmndiElement); // This actually works.
		    System.out.println("I removed element with ID: " + entry.getKey());
		}
	    }
	}
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(path));
	transformer.transform(source, result);

	//TODO make it look better
	System.out.println("Done deleting BMPNDI elements");
	System.out.println("Done deleting BMPNDI elements");
	System.out.println("Done deleting BMPNDI elements");
    }

}
