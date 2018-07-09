package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.File;
import java.io.IOException;

import javax.faces.flow.builder.ReturnBuilder;
import javax.ws.rs.GET;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import org.camunda.bpm.model.bpmn.instance.Task;
import org.jboss.com.sun.corba.se.pept.transport.InboundConnectionCache;
import org.joda.time.LocalDateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.apache.taglibs.standard.lang.jstl.PlusOperator;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;


//TODO This class is just for tests, and will later be deleted

public class Trials {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

	String single = "./TestGraphs/rule1parallel.bpmn.xml";

	String multiple = "./TestGraphs/multiple/"; //Testing more than one file.

	String singleandParameter = "./TestGraphs/rule1parallel.bpmn.xml -R1 - R2-R4"; 

	String multipleandParam = "./TestGraphs/multiple/ -R1 - R2-R4"; //Testing more than one file.

	String path = separatePathAndParamters(multiple)[0];

	getModels(path);
    }



    public static ArrayList<Model> getModels (String path) throws IOException, SAXException, ParserConfigurationException {
	ArrayList<Model> models = new ArrayList<Model>();

	if (path.contains(".bpmn.xml")) {
	    //it means we are getting a single file
	    Model model = new Model(path); //let's create a new model out of the path
	    models.add(model); //and add it to our list

	} else {
	    //it means we are getting a folder
	    System.out.println("Opening folder at: " + path);
	    List<String> bmpnXMLFiles = new ArrayList<String>();
	    File dir = new File(path);
	    System.out.println("I've found " + dir.listFiles().length + " files with the extension '.bpmn.xml'");
	    for (File file : dir.listFiles()) {
		if (file.getName().endsWith((".bpmn.xml"))) {
		    Model model = new Model(file.getPath()); //let's create a new model out of the path
		    models.add(model); //and add it to our list
		}
	    }
	}
	return models;
    }

    /**
     * TODO what if the path of a file contains " -" ??
     * @param input
     * @return the path part of the string will be in position [0] while the params in string form will be in [1]
     */
    public static String[] separatePathAndParamters (String input) {
	String path = "";
	String params = "";

	//in case there are no parameters:
	if (!input.contains(" -")) {
	    System.out.println("no parameters");
	    path = input;
	} else {
	    path = input.substring(0, input.indexOf(" -")); //getting the part before the dash
	    params = input.substring(input.indexOf(" -")); //getting the part after the dash
	}

	String[] pathAndParams = {path, params};
	System.out.println(pathAndParams[0]);
	System.out.println(pathAndParams[1]);

	return pathAndParams;
    }


    public ArrayList<Parameter> getParameters (String paramsString) {
	ArrayList<Parameter> params = new ArrayList<Parameter>();

	return params;
    }




}