package bpmn.transformation2.Backup;

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
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import bpmn.transformation2.Model;

public class File {

    private XPath xpath;
    private DocumentBuilder docBuilder;
    private Model Model;
    private String path;
    private Document doc;
    

    public File(String path) throws ParserConfigurationException, SAXException, IOException {
	load(path);
    }
    private void load(String path) throws ParserConfigurationException, SAXException, IOException {


	this.path = path;
	// Xpath needed to easily interact with XMLs
	XPathFactory xPathFactory = XPathFactory.newInstance();
	xpath = xPathFactory.newXPath();

	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	docBuilder = docFactory.newDocumentBuilder();

	// TODO make it look better
	System.out.println("		Opening file at position" + path);

	doc = docBuilder.parse(path);

    }


    private void save(String filepath) {

	//Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(this.doc);
	StreamResult result = new StreamResult(new File(filepath));
	transformer.transform(source, result);
	System.out.println(filepath);
	System.out.println("Saving file in : " + filepath);
    }
}
