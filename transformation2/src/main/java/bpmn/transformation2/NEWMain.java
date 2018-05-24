package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.impl.util.IoUtil;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

public class NEWMain {

    // TODO: find a better place to put those in:
    // this keeps track of the rules that have been applied, in the right order.
    public static String rulesApplied = "";
    public static String report = "";

    public static void main(String[] args) throws IOException, XPathExpressionException,
    TransformerConfigurationException, ParserConfigurationException, SAXException, TransformerException {

	// Used to test XML methods:
	String path = "./TestGraphs/DiagramForRule4a.bpmn.xml"; // TODO create an ad hoc model to test all of my XML functions
	if ( path.equals("") ){ //This is always expected to be true except when I'm testing something. Might even delete it later.
	    path = askForPath();
	}
	// reading a file
	File bpmnFile = new File(path);

	System.out.println("Trying to open file " + path);

	// this variable is used later to replace the filename with the new one more
	// easily. (inside method writeModeltoFile)
	String filename = bpmnFile.getName().replace(".bpmn.xml", "");

	// figuring out the folder in which the file is located
	String folderPath = getFolderFromPath(path);

	Model model = new Model(path);
	
	//applyRules(path, parameters);

	//Writing the output model to file
	writeToFile(model, filename, folderPath);

    }
    /**
     * TODO to be completed
     * This separates the filepath from the parameters
     * in the resulting array, the filepath is in the first position [0]
     * while the parameters are in the second position [1]
     * @param input
     * @return
     */
    public String[] findParameters (String input) {
	String[] pathAndParameters = new String[2];
	pathAndParameters[0] = input.substring( 0, input.indexOf("-"));
	pathAndParameters[1]  = input.substring(input.indexOf(",")+1, input.length());  
	return pathAndParameters;
    }
    /**
     * TODO this method should consider the parameters provided by the users and decide which rules to apply in which order.
     * @param model
     * @param parameters
     */
    public static void applyRules (Model model, String parameters) {
	
    }
    
    // This lets the user decide the path of the file
    public static String askForPath() {
	Scanner reader = new Scanner(System.in);
	System.out.println("Please enter the location of the bpmn file you wan to transform:");
	String filePath = reader.next();
	reader.close();
	return filePath;
    }

    // This method finds the path of the folder of the file. Used to save.
    public static String getFolderFromPath(String path) {
	int index = path.lastIndexOf('/');
	String folderpath = path.substring(0, index + 1);
	return folderpath;
    }

    /**
     * 
     * @param report
     * @param folderPath
     * @throws IOException
     */
    public static void writeReportToFile(String report, String folderPath) throws IOException {
	// getting today's date
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	String timeAndDate = dateFormat.format(new Date());
	// saving the report as a txt
	BufferedWriter writer = new BufferedWriter(new PrintWriter(folderPath + "Report" + timeAndDate + ".txt"));
	writer.write(report);
	writer.close();
    }
    /**
     * 
     * @param ModelInstance
     * @param filename
     * @param folderPath
     * @throws IOException 
     * @throws TransformerException 
     */
    public static void writeToFile(Model model, String filename, String folderPath) throws IOException, TransformerException {
	// TODO I will have to find a way to automatically append to the filename the
	// rules that have been applied. For now this works because I only have one.
	String filepath = folderPath + rulesApplied + "COMMENT" + ".filetype";

	//Saving the file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(model.doc);
	StreamResult result = new StreamResult(new File(filepath));
	transformer.transform(source, result);

	System.out.println("Saving file in : " + folderPath + "UNVALIDATED XML.txt");
    }

}
