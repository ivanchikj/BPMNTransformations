import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

//TODO la tua logica è sbagliata, non c'è bisogno che TUTTI i predecessori
// siano parallel gateway, perché tu non cancelli il gateway, ma solo i
// predecessori. Quindi basta che un parallel merge abbia un parallel come
// follower per poterlo cambiare. Devo vedere le cose dal punto di vista
// dell'outermost, non dell'innermost.
class Rule1 {


    static void applyRule (Model model) throws Exception {
//    System.out.println("I'm applying rule RULE1 to model " + startingModel
// .path);

        // Here I'm creating a list of all the parallel gateways in the
        // inputModel
        NodeList parallelGatewayInstances =
         model.doc.getElementsByTagName(model.style("parallelGateway"));
        System.out.println("number of parallel gateway instances: " + parallelGatewayInstances.getLength());

        if (parallelGatewayInstances.getLength() == 0) {
            System.out.println("RULE1: there are no parallel gateways in " +
             "this" + " model");
        }

        ArrayList<Element> readyToBeChangedGateways = new ArrayList<>();
        //NOTE with this rule
        //I cannot do edits in real time otherwise the program would behave
        //recursively depending on which
        //elements it analyzes first.

        //going through all of the parallelGateways in the model:
        for (int i = 0 ; i < parallelGatewayInstances.getLength() ; i++) {

            Element gateway = (Element) parallelGatewayInstances.item(i);
            //this will be the element in case
//	    System.out.println("working on the " + (i) + "nd parallelGateway");
//	    System.out.println("working on " + gateway.getAttribute("id"));
//	    System.out.println("The id of the element is " + gateway.getAttribute
// ("id") );

            if (model.isAMerge(gateway) && isApplicable(model, gateway)) {
                //TODO perché non mettere isAMerge dentro isApplicable per
                // chiarezza?

                readyToBeChangedGateways.add(gateway); //I do the edits at
                // the end to avoid recursive behavior
            }
        }

        if (readyToBeChangedGateways.size() > 0) {
            for (Element gateway : readyToBeChangedGateways) {
                ArrayList<Element> predecessors =
                 model.getPredecessors(gateway); //predecessors will be
                // eliminated
                for (Element pred : predecessors) {

                    ArrayList<Element> flowsToDelete =
                     model.getOutgoingFlows(pred); //this is also the
                    // incoming flow of the gateway at hand
                    ArrayList<Element> flowsToKeep =
                     model.getIncomingFlows(pred);

                    //deleting all the flows (it should be only one) from the
                    // item to be deleted to its successor:
                    for (Element aFlowsToDelete : flowsToDelete) {
                        model.delete(aFlowsToDelete.getAttribute("id"));
                    }
                    //changing the targets of all their sequence flows
                    for (Element aFlowsToKeep : flowsToKeep) {
                        model.setTarget(aFlowsToKeep.getAttribute("id"),
                         gateway.getAttribute("id"));
                    }
                    //finally deleting the gateways
                    model.delete(pred.getAttribute("id"));
                }
            }
        }
    }


    /**
     * This method checks if the rule1 is applicable on a given parallel
     * gateway.
     * That is, if the gateway is preceded only by other gateways of the same
     * type, AND those gateways are not preceded
     * by a gateway of the same type themselves AND those gateways are
     * then the rule is applicable.
     */
    private static boolean isApplicable (Model model,
     Element parallelGateway) throws XPathExpressionException {

        System.out.println("I'm checking if gateway " + parallelGateway.getAttribute("id") + " is suitable for rule 1:");

        ArrayList<Element> predecessors =
         model.getPredecessors(parallelGateway);
        String type = parallelGateway.getTagName();

        boolean sameType = true;
        boolean innerMost = false;
        boolean noSplitsInPreds = true; //all predecessors (that will be
        // deleted) have to be merges

        for (Element predecessor : predecessors) {

            //checking that all predecessors are of the same type:
            System.out.println("I'm analyzing predecessor " + predecessor.getAttribute("id"));
            if (! predecessor.getTagName().equals(type)) {
                System.out.println("The gateway " + parallelGateway.getAttribute("id") + " is preceded by something that is not a gateway of the same type.");
                System.out.println("The rule1 cannot be applied on that " +
                "gateway");
                sameType = false;
            } else {
                System.out.println("I haven't found a single predecessor of " + parallelGateway.getAttribute("id") + " that is not a parallel gateway");
                //sameType remains true
            }

            //Checking that we are on the innermost level. I.e. that every
            // predecessor is not preceded by a gateway of the same type himself
            ArrayList<Element> predsOfPred =
             model.getPredecessors(predecessor); //the predecessors of the
            // predecessor

            for (Element predOfPred : predsOfPred) {
                System.out.println("Im analyzing predecessor " + predOfPred.getAttribute("id") + " of the predecessor " + predecessor.getAttribute("id"));

                if (! predOfPred.getTagName().equals(type)) {
                    System.out.println("I have found a predecessor that is " + "not a gateway of the same type!");
                    innerMost = true;
                }

                //Checking that all predecessors are merges
                if (!model.isAMerge(predecessor)) {
                    System.out.println("I have found a predecessor that is " + "not a merge but rather a split!");
                    noSplitsInPreds = false;
                }
            }
        }

        //Finally if both conditions are true, return true.
        if (sameType && innerMost && noSplitsInPreds) {
            System.out.println("All predecessors are of same type AND they " + "are all merges AND we are on the innermost level");
            return true;
        } else {
            return false;
        }
    }


}