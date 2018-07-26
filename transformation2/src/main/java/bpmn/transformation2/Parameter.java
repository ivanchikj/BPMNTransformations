package bpmn.transformation2;

import java.util.ArrayList;
import java.util.Arrays;

import org.camunda.bpm.model.dmn.instance.Input;

public class Parameter {

    public String parameter;
    public int aggregateBy;
    public static String[] validParameters = 
	{
		"1", 
		"2" ,
		"3",
		"3a", "3b", "3c", 
		"4", "4a", "4b", "4c", 
		"r1",
		"r2",
		"r3", 
		"r3a", "r3b", "r3c", 
		"r4", 
		"r4a", "r4b", "r4c"
	};


    public Parameter (String parameter) {

	if (isValid(parameter)) {
	    this.parameter = parameter;
	}
    }
    
    //NOTE that 
    //if the AggregateBy parameter is present in a rule that doesn't use it, 
    //it will simply be ignored.
    public Parameter(String parameter, int aggregateBy) {;
    if (isValid(parameter)) {
	this.parameter = parameter;
	this.aggregateBy = aggregateBy;
    }
    }


    public boolean isValid (String parameter) {


	if (Arrays.asList(validParameters).contains(parameter)) {
	    return true;
	} else {
	    System.out.println(parameter + " is not a valid parameter.");
	    return false;
	}

    }



}
