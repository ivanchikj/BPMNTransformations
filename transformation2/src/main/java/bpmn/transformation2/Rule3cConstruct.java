package bpmn.transformation2;

import java.util.ArrayList;

import org.apache.ibatis.jdbc.Null;
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

    public Rule3cConstruct() {
     }
    
    public void setFirstParallel(Element firstParallel) {
	this.firstParallel = firstParallel;
    }
    
    public void setFirstParellelMeetingPoint(Element firstParallelMeetingPoint) {
	this.firstParallelMeetingPoint = firstParallelMeetingPoint;
    }

    public void setExclusiveSuccessors(ArrayList<Element> exclusiveSuccessors) {
	this.exclusiveSuccessors = exclusiveSuccessors;
    }
    
    public void setExclusivePredecessors( ArrayList<Element> exclusivePredecessors) {
	this.exclusivePredecessors = exclusivePredecessors;
    }
    
    public boolean isComplete() {
	if (firstParallel.equals(null) || firstParallelMeetingPoint.equals(null) || exclusiveSuccessors.equals(null) || exclusivePredecessors.equals(null)) {
	    return false;
	} else {
	    return true;
	}
    }
    
}
