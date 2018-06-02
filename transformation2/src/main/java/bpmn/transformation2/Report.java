package bpmn.transformation2;
/**
 * TODO
 * @author rubenfolini
 *
 */

import org.camunda.bpm.engine.impl.cmd.SaveAttachmentCmd;

public class Report {

    public String date;
    public String rulesApplied;
    public String outputpath; //TODO do not use this
    public String inputPath;

    public Report(){

    }

    public String concatenate() {
	return "" + date + rulesApplied; //TODO complete this. Add also line breaks and spacing.
    }
    public void save() {

    }
}
