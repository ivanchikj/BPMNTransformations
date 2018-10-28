import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.security.sasl.SaslException;
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

public class Model {
    // "Tools needed for my interactions
    private XPathFactory xPathFactory;
    private XPath xpath;
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;

    private Document doc; // This is the xml representation of my model
    private Element process;
    private Element bpmndiDiagram;
    private String path; /// TODO decide whether this will be private or not.

    /**
     *
     */
    public Model(String path) throws IOException, org.xml.sax.SAXException, ParserConfigurationException {
	this.path = path;
	xPathFactory = XPathFactory.newInstance();
	xpath = xPathFactory.newXPath();
	docFactory = DocumentBuilderFactory.newInstance();
	docBuilder = docFactory.newDocumentBuilder();

	// TODO make it look better
	System.out.println("Opening file at position" + path);

	doc = docBuilder.parse(path);
	process = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0);
	// there's always one single bmpdi:BMPNDiagram, so it's "item 0"
	bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0);

	Element bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0); // there's always one
												  // single
												  // bmpdi:BMPNDiagram,
												  // so it's "item 0"
	System.out.println("I'm working on the following bpmndiDiagram: " + bpmndiDiagram.getAttribute("id"));
    }

    /**
     *
     * @param path
     */
    public Document openFile(String path) throws IOException, org.xml.sax.SAXException {
	// TODO make it look better
	System.out.println("Opening file at position" + path);

	Document doc = docBuilder.parse(path);

	Element bpmndiDiagram = (Element) doc.getElementsByTagName("bpmndi:BPMNDiagram").item(0); // there's always one
												  // single
												  // bmpdi:BMPNDiagram,
												  // so it's "item 0"
	System.out.println("I'm working on the following bpmndiDiagram: " + bpmndiDiagram.getAttribute("id"));

	return doc;
    }

    /**
     *
     * @return
     */
    public String createElement() {
	String id = "GenereateRandomString"; // TODO
	return id;
    } // ritorna l'id dell'oggetto? Forse non è necessario. Oppure lo prendo come ID?
      // Come faccio a generare ID dal nulla?

    /**
     *
     * @param id
     * @param source
     */
    public void setSource(String id, String source) {
    }

    /**
     *
     * @param id
     * @param target
     */
    public static void setTarget(String id, String target) {
    }

    /**
     *
     * @param type
     */
    public static void findElementByType(String type) {
    }

    /**
     *
     * @param id
     * @return
     */
    public static String[] getPredecessors(String id) {
	String[] predecessors = {};
	return predecessors;
    } // immediate predecessors

    /**
     *
     * @param id
     * @return
     */
    public static String[] getSuccessors(String id) {
	String[] successors = {};
	return successors;
    } // immediate predecessors

    /**
     *
     * @param id
     * @return
     */
    public static String getType(String id) {
	String type = ""; // TODO
	return type;
    } // return a string of the type

    // the two following methods are a more generic alternative to getCondition and
    // SetCondition:
    public static String getValue(String property) {
	String value = "";
	return value;
    } // return the value of the property

    public static void setProperty(String property, String value) {
    }

    public void deleteElement(String id)
	    throws ParserConfigurationException, SaslException, IOException, XPathExpressionException,
	    TransformerException, TransformerConfigurationException, org.xml.sax.SAXException {

	// Looking for each id in ids:
	System.out.println("I'm searching for id: " + id);
	NodeList bpmndiList = (NodeList) xpath.evaluate("//*[@bpmnElement='" + id + "']", doc, XPathConstants.NODESET);
	for (int i = 0; i < bpmndiList.getLength(); ++i) {
	    Element bpmndiElement = (Element) bpmndiList.item(i);
	    System.out.println(bpmndiElement.getAttribute("bpmnElement"));
	    bpmndiElement.getParentNode().removeChild(bpmndiElement);
	    System.out.println("I removed element with ID: " + id);
	}

	System.out.println("Done deleting BMPNDI elements");

    }

    public void saveToFile() throws TransformerException {
	// Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(path));
	transformer.transform(source, result);
	System.out.println("Saved the XML file in + " + path);

    }

}
