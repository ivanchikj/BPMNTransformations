package bpmn.transformation2;

import java.util.ArrayList;
import java.util.Arrays;

import org.camunda.bpm.model.dmn.instance.Input;

public class Parameter {

    public String parameter;
    
    

    public Parameter (String parameter) {
	
	if (isValid(parameter)) {
	    this.parameter = parameter;
	}
    }
    
    
    public boolean isValid (String parameter) {
	
	
	if (Arrays.asList(Main.validParameters).contains(parameter)) {
	    return true;
	} else {
	    System.out.println(parameter + " is not a valid parameter.");
	    return false;
	}
	
    }
    
    
    
}
