package bpmn.transformation2;

import java.io.IOException;
import java.util.Random;

public class ExecutionTest {
    
    Report report;
    
    public ExecutionTest() {
	this.report = new Report();
    }
    public void Execute() throws IOException {
	
	for (int i = 0; i < 100; i++) {
	    report.addOutcome("TEST number: ", "" + (i+1) + " outcome: ", generateRandomOutput());  
	}
    
    report.saveToFile( "./TestGraphs/output/report.txt" );
    
    }
    /**
     * 
     * @return True 20% of the times
     */
    private boolean generateRandomOutput() {
	Random gen = new Random();
	int a = gen.nextInt(100);
	return a <= 35;
    }
    
    
}



