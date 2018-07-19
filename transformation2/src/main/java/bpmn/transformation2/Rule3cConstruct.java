package bpmn.transformation2;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class Rule3cConstruct {

    
    Element firstParallel;
    Element firstParallelMeetingPoint;
    ArrayList<Element> exclusiveSuccessors; //the exclusive-gateway successors of the firstParallel
    ArrayList<Element> exclusivePredecessors; // the exclusive-gateway successors of the firstParallelMeetingPoint
    
    Rule3cConstruct(Element firstParallel, Element firstParallelMeetingPoint, ArrayList<Element> exclusiveSuccessors, ArrayList<Element> exclusivePredecessors){
	this.firstParallel = firstParallel;
	this.firstParallelMeetingPoint= firstParallelMeetingPoint;
	this.exclusiveSuccessors = exclusiveSuccessors;
	this.exclusivePredecessors = exclusivePredecessors;
    }
    
}
