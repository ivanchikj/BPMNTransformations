package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintWriter;

public class Report {

    public String time;
    public String rulesApplied;
    public String outputpath; //TODO do not use this
    public String inputPath;

    public Report(){
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	this.time = dateFormat.format(new Date());
	
    }

    public String concatenate() {
	return "" + time + rulesApplied; //TODO complete this. Add also line breaks and spacing.
    }
    
    private void saveToFile(String report, String folderPath) throws IOException {
	// saving the report as a txt
	BufferedWriter writer = new BufferedWriter(new PrintWriter(folderPath + "Report" + this.time + ".txt"));
	writer.write(report);
	writer.close();
    }
}
