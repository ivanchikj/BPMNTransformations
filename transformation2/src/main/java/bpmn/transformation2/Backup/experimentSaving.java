package bpmn.transformation2.Backup;

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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.impl.util.IoUtil;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

public class experimentSaving {

    //TODO in all those method where i use the path,
    //it would be wiser not to work directly on my XML file,
    //but to clone it at the launch of the program so as not to mess up the original file.

    
    //TODO: find a better place to put those in: 
    // this keeps track of the rules that have been applied, in the right order.
    public static String rulesApplied = "";
    public static String report = ""; //TODO public should be avoided, right?
    
    //This remembers all of the items that I have to delete from the XML's bpmdi section
    public static List<String> bpmndiItemsToDelete = new ArrayList<String>();
    
    public static void main(String[] args) throws IOException, XPathExpressionException, TransformerConfigurationException, ParserConfigurationException, SAXException, TransformerException {

	//String path = askForPath(); //UNLOCKTHIS to ask the user for a path

	// Used to test Rule3
	// TODO create ad hoc xml files with camunda modeler

	// USED to test Rule4
	//part1:
	String path = "./TestGraphs/Test17May.bpmn.xml";

	//Used to test the deleting System
	//String path = "./TestGraphs/deletingTest.bpmn.xml";

	//reading a file 
	File bpmnFile = new File(path);


	System.out.println("Trying to open file " + path); //this will be useful when opening a folder with multiple files

	// this variable is used later to replace the filename with the new one more
	// easily.
	String filename = bpmnFile.getName().replace(".bpmn.xml", "");
	// Will be used when naming the output file (inside method writeModeltoFile)

	// Creating output model
	BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);


	//figuring out the folder in which the file is located
	String folderPath = getFolderFromPath(path);

	// Writing the output model to file
	writeModeltoFile(inputModelInstance, filename, folderPath);
	
    }
    // This lets the user decide the path of the file
    // TODO provide exceptions? What happens if it's not the correct path?
    public static String askForPath() {
	Scanner reader = new Scanner(System.in);
	System.out.println("Please enter the location of the bpmn file you wan to transform:");
	String filePath = reader.next();
	reader.close();
	return filePath;
    }
    //This method finds the path of the folder of the file. Used to save 
    public static String getFolderFromPath(String path) {
	int index=path.lastIndexOf('/');
	String folderpath = path.substring(0,index+1);
	return folderpath;
    }

    public static void writeModeltoFile(BpmnModelInstance ModelInstance, String filename, String folderPath) {
	//Validate Model UNLOCKTHIS (disabled for testing purposes)
	//NOTE even with the following line disabled, it still refuses to save a malformed bpmn graph:
	//writemodeltofile probably already includes validateModel inside.
	Bpmn.validateModel(ModelInstance);
	// TODO use also Ana's test at
	// https://github.com/camunda/camunda-engine-unittest

	// Write to file
	// TODO "rulesApplied" never gets updated. Why? Shoul i provide get and set methods? I guess there's a simpler way.
	String location = folderPath + "/output/" +  filename + rulesApplied + "DELETINGTEST" + ".bpmn.xml";
	File file = new File(location);
	file.getParentFile().mkdirs();
	System.out.println(location);
	// TODO I will have to find a way to automatically append to the filename the
	// rules that have been applied. For now this works because I only have one.
	Bpmn.writeModelToFile(file, ModelInstance);
    }

}
