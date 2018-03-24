package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

//This class is just for tests, and will later be deleted

public class Trial {

    public static void main(String[] args) throws IOException {
	String prova = "prova prova" + '\n' +  "prova a capo";
	String folderPath = "/Users/rubenfolini/Desktop/";
	writeReportToFile(prova, folderPath);
    }
    public static void writeReportToFile(String report, String folderPath) throws IOException {
	LocalDateTime timestamp = LocalDateTime.now();
	BufferedWriter writer = new BufferedWriter( new PrintWriter(folderPath + "Report" + timestamp +".txt"));
	writer.write(report);
	writer.close( );
	
    }
}
