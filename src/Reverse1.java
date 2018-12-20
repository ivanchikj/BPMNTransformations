import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

class Reverse1 {


    /**
     * @param model       the model that will be transformed
     * @param aggregateBy the number of incoming Flows to aggregate. For
     *                    example, if aggregateBy == 2 then for
     *                    every 2 incoming flows I create one new gateway. If
     *                    aggregateBy == 3 then for every 3 incoming
     *                    flows I create a new gateway.
     */
    static void apply (Model model, int aggregateBy) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE1 to model " + model.name);

        if (aggregateBy < 2) {
            System.err.println("aggregateBy by must be bigger than 1");
            aggregateBy = 2;
        }

        //let's find the candidate gateways.
        //we can accept every gateway that is a merge,
        //that means it has only one outgoing flow and that has more than one
        // incoming flow.

        //all the parallel gateways in the model:
        ArrayList<Element> parallelGatewayInstances =
         model.findElementsByType("parallelGateway");

        System.out.println("number of parallel gateway instances: " + parallelGatewayInstances.size());

        if (parallelGatewayInstances.size() == 0) {
            System.out.println("RULE1: there are no parallel gateways in " +
            "this" + " model");
        }

        ArrayList<Element> candidates = new ArrayList<>();
        //let's add in our candidate list only those gateways that are merges.
        for (Element candidate : parallelGatewayInstances) {
            //additionally, to avoid having a "one in, one out" type of
            // gateway (which is undesired) we have to check that the number of
            //incomingFlows of our candidate is bigger than the parameter
            // aggregateBy
            if (model.isAMerge(candidate) && model.getIncomingFlows(candidate).size() > aggregateBy) {
                candidates.add(candidate);
            }
        }

        //ok now we have our list of candidates
        for (Element parallel : candidates) {
            ArrayList<Element> incomingFlows = model.getIncomingFlows(parallel);

            ArrayList<Element> predecessors = model.getPredecessors(parallel);

            //now we have to create some new parallels
            //the number varies depending on the number of incomingFlows and
            // on the aggregateBy parameter
            //we should divide the number of incomingFlows by the aggregateBy
            // parameter.

            int numberOfParallels = incomingFlows.size() / aggregateBy;
            //also, since we're dealing with ints, we should add one more
            // parallel in the case there's a remainder.
            if (incomingFlows.size() % aggregateBy != 0) {
                numberOfParallels++;
            }

            //ok let's now create those new parallel:
            for (int n = 0 ; n < numberOfParallels ; n++) {
                ArrayList<Element> myPredecessors = new ArrayList<>();

                //we have to find the predecessors for the new gateway, among
                // the ones that used to be
                //the predecessors of the original gateway
                //obviously, the last parallel might not have enough
                // predecessors to comply with the
                //parameter 'aggregateBy' but might have less
                for (int a = 1 ; a <= aggregateBy ; a++) {
                    if (predecessors.size() == 0) {
                        //System.out.println("no more predecessors to get");
                    } else if (predecessors.size() > 0) {
                        myPredecessors.add(predecessors.get(0)); //the
                        // precessor is now a predecessor of the new parallel
                        predecessors.remove(0); //it's not a predecessor of
                        // the original parallel anymore
                    }
                }
                //System.out.println("My predecessors SIZE " + myPredecessors
                // .size()); UNLOCKTHIS
                Coordinates position =
                 model.calculatePositionOfNewNode(myPredecessors, parallel);

                String newParallelID = model.newParallelGateway(position); //TODO
                // make this method accept a 'position'
                // object
                Element newParallel = model.findElemById(newParallelID);

                // newParallel.setAttribute("name", "NEW");//UNLOCKTHIS

                //now that I have created my parallel in a sensible position,
                //I can connect it to the original parallel

                model.newSequenceFlow(newParallelID, parallel.getAttribute(
                "id"));

                //now I can connect the element in myPredecessors to the new
                // Parallel

                for (Element predecessor : myPredecessors) {
                    Element outgoingFlow =
                     model.getOutgoingFlows(predecessor).get(0); //we expect
                    // the predecessor to have only one outgoingFlow of course.
                    model.setTarget(outgoingFlow.getAttribute("id"),
                     newParallelID);
                }

                //one last thing. We want to avoid having "one in, one out"
                // types of gateways, so to avoid this situation we want to
                //do one last check:
                if (model.isUselessGateway(newParallel)) {
                    model.deleteUselesGateway(newParallel);
                }
            }
        }
    }


}

