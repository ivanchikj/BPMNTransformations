import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

class Rule2 {


    /**
     * Rule 2 in the normal direction:
     *
     * @param model the model that will be transformed
     */
    static void applyRule (Model model) throws XPathExpressionException {

        System.out.println("I'm applying Rule2 to model: " + model.name);
        //TODO make it such that it works with every gateway, not just
        // exclusive ones.
        ArrayList<Element> gatewayInstances =
         model.findElementsByType("exclusiveGateway");
        System.out.println("number of " + " gateway instances: " + gatewayInstances.size()); //TODO make it work with every gateway type.

        if (gatewayInstances.size() == 0) {
            System.out.println("RULE1: there are no exclusive gateways in " +
             "this model");
            //RETURN FALSE
        }

        ArrayList<Element> readyToBeChangedGateways = new ArrayList<>();
        //NOTE with this rule
        //I cannot do edits in real time otherwise the program would behave
        // recursively depending on which
        //elements it analyzes first.

        //going through all of the Gateways in the model:
        for (Element gateway : gatewayInstances) {

//            System.out.println("working on " + gateway.getAttribute("id"));
//            System.out.println("The id of the element is " + gateway.getAttribute("id"));

            //TODO perché non mettere isAMerge dentro isApplicable per
            // chiarezza?
            if (isApplicable(model, gateway) && model.isASplit(gateway)) {
                readyToBeChangedGateways.add(gateway); //I will do the edits
                // at the end to avoid recursive behavior


            }
        }

        if (readyToBeChangedGateways.size() > 0) {
            for (Element gateway : readyToBeChangedGateways) {
                ArrayList<Element> successors = model.getSuccessors(gateway); //successors will be eliminated
                for (Element succ : successors) {

                    //Let's add the conditions of the incomingFlows to the
                    // OutgoingFlows

                    ArrayList<Element> flowsToDelete =
                     model.getIncomingFlows(succ); //this is also the
                    // outGoing flow of the gateway at hand. It should be
                    // only one.
                    ArrayList<Element> flowsToKeep =
                     model.getOutgoingFlows(succ);
                    String firstPartofCondition = "";
                    @SuppressWarnings ("UnusedAssignment") String secondPartOfCondition = "";
                    //deleting all the flows (it should be only one) from the
                    // item to be deleted to its successor:
                    for (Element aFlowsToDelete : flowsToDelete) {
                        model.delete(aFlowsToDelete.getAttribute("id"));
                        firstPartofCondition =
                         model.returnConditionString(aFlowsToDelete);
                        //TODO This works only because I have only one
                        // outGoing flow. Otherwise it should be outside of
                        // the for loop.
                    }
                    //changing the targets of all their sequence flows
                    for (Element flowToKeep : flowsToKeep) {
                        model.setSource(flowToKeep.getAttribute("id"),
                         gateway.getAttribute("id"));

                        String newCondition = firstPartofCondition; // the
                        // initialization value should not matter.

                        secondPartOfCondition =
                         model.returnConditionString(flowToKeep);
                        // if both flows have a condition, I merge them with
                        // an AND in the middle.
                        // if else, I only keep the non - empty one.
                        if (! firstPartofCondition.equals("") && ! secondPartOfCondition.equals("")) {
                            //changing the condition of that outgoing flow:
                            newCondition =
                             generateCondition(firstPartofCondition,
                              secondPartOfCondition);
                        } else if (! firstPartofCondition.equals("") && secondPartOfCondition.equals("")) {
                            //noinspection ConstantConditions
                            newCondition = firstPartofCondition;
                        } else if (firstPartofCondition.equals("") && ! secondPartOfCondition.equals("")) {
                            newCondition = secondPartOfCondition;
                        }

                        //Adding the newCondition

                        System.out.println(newCondition);
                        //Normally, we wouldn't need this check, because it
                        // is illegal to have exclusive that have no
                        // conditions on both outgoing flows.
                        //Still, sometimes it happens.
                        if (model.hasCondition(flowToKeep)) {
                            model.returnConditionElement(flowToKeep).setTextContent(newCondition);
                        }
                    }
                    //finally deleting the gateways
                    model.delete(succ.getAttribute("id"));
                }
            }
        }
    }


    private static boolean isApplicable (Model model, Element gateway) throws XPathExpressionException {

        System.out.println("I'm checking if gateway " + gateway.getAttribute(
        "id") + " is suitable for rule 2:");

        ArrayList<Element> successors = model.getSuccessors(gateway);
        String type = gateway.getTagName();

        boolean sameType = true;
        boolean innerMost = false;
        boolean noMergesInPreds = true; //all successors (that will be
        // deleted) have to be splits

        for (Element successor : successors) {

            //checking that all successors are of the same type:
            System.out.println("I'm analyzing predecessor " + successor.getAttribute("id"));
            if (! successor.getTagName().equals(type)) {
                System.out.println("The gateway " + gateway.getAttribute("id") + " is succeded by something that is not a gateway of the same type.");
                System.out.println("The rule2 cannot be applied on that " +
                "gateway");
                sameType = false;
            } else {
                System.out.println("I haven't found a single successor of " + gateway.getAttribute("id") + " that is not a " + type);
                //sameType remains true
            }

            //Checking that we are on the innermost level. I.e. that every
            // successor is not succeded by a gateway of the same type himself
            ArrayList<Element> succsOfSucc = model.getSuccessors(successor);
            //the predecessors of the predecessor

            for (Element succOfSucc : succsOfSucc) {
                System.out.println("Im analyzing successor " + succOfSucc.getAttribute("id") + " of the successor " + successor.getAttribute("id"));

                if (! succOfSucc.getTagName().equals(type)) {
                    System.out.println("I have found a successor that is not "
                     + "a gateway of the same type!");
                    innerMost = true;
                }

                //Checking that all predecessors are splits
                if (model.isAMerge(successor)) {
                    //TODO this includes 1in 1out gateways. Decide what to do
                    // with merges. I would do it more resistant.
                    System.out.println("I have found a predecessor that is " + "not a split but rather a merge!");
                    noMergesInPreds = false;
                }
            }
        }
        //Finally if all conditions are true, return true.
        if (sameType && innerMost && noMergesInPreds) {
            System.out.println("All predecessors are of same type AND they " + "are all splits AND we are on the innermost level");
            return true;
        } else {
            return false;
        }
    }


    /**
     * AS for now, this method just adds an AND between the first gateway's
     * flow's condition and the second's.
     *
     * @return the new condition
     */
    private static String generateCondition (String firstCondition,
     String secondCondition) {

        return firstCondition + " && " + secondCondition;
    }


}
