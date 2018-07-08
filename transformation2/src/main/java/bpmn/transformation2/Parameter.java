package bpmn.transformation2;

import java.util.ArrayList;

import org.camunda.bpm.model.dmn.instance.Input;

public class Parameter {

    public String parameter;
    
    
    public void Parameter (String parameter) {
	
	if (isValid(parameter)) {
	    this.parameter = parameter;
	}
    }
    
    
    public boolean isValid (String parameter) {
	ArrayList<String> validParameters = new ArrayList<String>();
	validParameters.add("R1"); //TODO add all parameters
	validParameters.add("R2");
	
	if (validParameters.contains(parameter)) {
	    return true;
	} else {
	    System.out.println(parameter + " is not a valid parameter.");
	    return false;
	}
	
    }
    
    
    
}
