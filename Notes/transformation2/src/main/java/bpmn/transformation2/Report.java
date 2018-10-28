package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintWriter;

public class Report {

    public String time;
    public String rulesApplied;
    public String outputpath; //TODO do not use this
    public String inputPath;
    public String text;
   
    
    public Report(){	
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	this.text = "";
	this.time = dateFormat.format(new Date());
    }

    public String concatenate() {
	return "" + time + rulesApplied; //TODO complete this. Add also line breaks and spacing.
    }
    /**
     * TODO instead of a string, get a model as a param and find his path autonomously
     * TODO instead of a string, get a list of parameters and compose the string autonomously
     * 
     * @param modelInvolved
     * @param ruleString
     */
    public void addOutcome(String modelInvolved, String ruleString, boolean outcome) {
	String newline = System.getProperty("line.separator");
	
	text += modelInvolved + ruleString + outcome + newline; 
    }
    
    
    public void saveToFile(String folderPath) throws IOException {
	// saving the report as a txt
	BufferedWriter writer = new BufferedWriter(new PrintWriter(folderPath + "Report" + this.time + ".txt"));
	writer.write(text);
	writer.close();
    }
}
