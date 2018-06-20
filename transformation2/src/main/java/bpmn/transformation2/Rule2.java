package bpmn.transformation2;

public class Rule2 {

    /**
     * This checks whether a sequenceFlow has a condition or not.
     * If it has not, then there's no need to generate one and append it to the new flows.
     * @return
     */
    public boolean hasCondition() {
	boolean hasCondition = false;
	return hasCondition();
    }
    
    
    /**
     * 
     * AS for now, this method just
     * @return
     */
    public String generateCondition(String firstCondition, String secondCondition) {
	String newCondition = firstCondition + " && " + secondCondition;
	return newCondition;
	
    }
}
