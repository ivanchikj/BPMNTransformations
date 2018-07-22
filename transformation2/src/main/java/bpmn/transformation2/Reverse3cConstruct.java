package bpmn.transformation2;

import org.w3c.dom.Element;

public class Reverse3cConstruct {


    Element firstInclusive;
    Element firstInclusiveMeetingPoint;

    Reverse3cConstruct(Element firstParallel, Element firstInclusiveMeetingPoint){
	this.firstInclusive = firstParallel;
	this.firstInclusiveMeetingPoint= firstInclusiveMeetingPoint;
    }

    public Reverse3cConstruct() {
     }
    
    public void setFirstInclusive(Element firstInclusive) {
	this.firstInclusive = firstInclusive;
    }
    
    public void setFirstInclusiveMeetingPoint(Element firstInclusiveMeetingPoint) {
	this.firstInclusiveMeetingPoint = firstInclusiveMeetingPoint;
    }

      
    public boolean isComplete() {
	if (firstInclusive.equals(null) || firstInclusiveMeetingPoint.equals(null)) {
	    return false;
	} else {
	    return true;
	}
    }
    
}
