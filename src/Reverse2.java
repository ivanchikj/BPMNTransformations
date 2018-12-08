import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

class Reverse2 {

    //TODO decide how to generate that INT number and where.


    /**
     * @param model       the model that will be transformed
     * @param aggregateBy the number of incoming Flows to aggregate. For
     *                    example, if levels == 1 then for
     *                    every incomingFlow i create a gateway. If levels = 2
     *                    then for every two incoming gateways I create a new
     *                    gateway
     */
    static void applyRule (Model model, int aggregateBy) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE2 to model " + model.name);
        if (aggregateBy < 2) {
            System.err.println("aggregateBy by must be bigger than 1");
            aggregateBy = 2;
        }

        //let's find the candidate gateways.
        //we can accept every gateway that is a spit,
        //that means it has only one incoming flow and that has more than one
        // outgoing flows.

        //all the exclusive gateways in the model:
        NodeList exclusiveGatewayInstances =
         model.doc.getElementsByTagName(model.style("exclusiveGateway"));

        System.out.println("number of exclusive gateway instances: " + exclusiveGatewayInstances.getLength());

        if (exclusiveGatewayInstances.getLength() == 0) {
            System.out.println("Reverse 2: there are no exclusive gateways " + "in" + " " + "this model");
        }

        ArrayList<Element> candidates = new ArrayList<>();
        //let's add in our candidate list only those gateways that are splits.

        for (int i = 0 ; i < exclusiveGatewayInstances.getLength() ; i++) {
            Element candidate = (Element) exclusiveGatewayInstances.item(i);
            //additionally, to avoid having a "one in, one out" type of
            // gateway (which is undesired) we have to check that the number of
            //outgoingflows of our candidate is bigger than the parameter
            // aggregateby
            if (model.isASplit(candidate) && model.getOutgoingFlows(candidate).size() > aggregateBy) {
                candidates.add(candidate);
            }
        }

        //ok now we have our list of candidates
        for (Element exclusive : candidates) {
            ArrayList<Element> outgoingFlows =
             model.getOutgoingFlows(exclusive);

            ArrayList<Element> successors = model.getSuccessors(exclusive);

            //now we have to create some new exclusives
            //the number varies depending on the number of outgoingFlows and
            // on the aggregateBy parameter
            //we should divide the number of outgoingFlows by the aggregateBy
            // parameter.

            int numberOfExclusives = outgoingFlows.size() / aggregateBy;
            //also, since we're dealing with ints, we should add one more
            // parallel in the case there's a remainder.
            if (outgoingFlows.size() % aggregateBy != 0) {
                numberOfExclusives++;
            }

            //ok let's now create those new parallel:
            for (int n = 0 ; n < numberOfExclusives ; n++) {
                ArrayList<Element> mySuccessors = new ArrayList<>();

                //we have to find the successors for the new gateway, among
                // the ones that used to be
                //the successors of the original gateway
                //obviously, the last parallel might not have enough
                // successors to comply with the
                //parameter 'aggregateBy' but might have less
                for (int a = 1 ; a <= aggregateBy ; a++) {
                    if (successors.size() == 0) {
                        System.out.println("no more successors to get");
                    } else //noinspection ConstantConditions
                        if (successors.size() > 0) {
                            mySuccessors.add(successors.get(0)); //the successor
                            // is now a successor of the new parallel
                            successors.remove(0);
                            //it's not a successor of the
                            // original parallel anymore
                        }
                }
                System.out.println("My successors SIZE " + mySuccessors.size());
                Coordinates position =
                 model.calculatePositionOfNewNode(mySuccessors, exclusive);

                String newExclusiveID = model.newExclusiveGateway(position);
                Element newExclusive = model.findElemById(newExclusiveID);

                //newExclusive.setAttribute("name", "NEW");//UNLOCKTHIS

                //now that I have created my parallel in a sensible position,
                //I can connect it to the original parallel

                model.newSequenceFlow(exclusive.getAttribute("id"),
                 newExclusiveID);

                //now I can connect the element in myPredecessors to the new
                // exclusive

                for (Element successor : mySuccessors) {
                    Element incomingFlow =
                     model.getIncomingFlows(successor).get(0); //we expect
                    // the successor to have only one incomingFlow of course.
                    model.setSource(incomingFlow.getAttribute("id"),
                     newExclusiveID);
                }

                //TODO spiegalo nella tesi:
                //we now have to deal with the conditions.
                //we create a new condition that lead to the new gateway
                // which is simply
                // the addition of an "or" between its follower's conditions

                ArrayList<Element> flowsWithConditions =
                 model.getOutgoingFlows(newExclusive);
                ArrayList<String> conditionStrings = new ArrayList<>();

                for (Element flow : flowsWithConditions) {

                    if (model.hasCondition(flow)) {
                        System.out.println(model.returnConditionString(flow));
                        conditionStrings.add(model.returnConditionString(flow));
                    }
                }

                //now that we have all the conditions of the successors in
                // one array
                //we can calculate the new condition string
                String conditionString =
                 calculateNewCondition(conditionStrings);
                System.out.println("NEW CONDITION: " + conditionString);
                //and then applying it to the sequenceFlow that comes into
                // our new exclusive:
                Element incomingFlow =
                 model.getIncomingFlows(newExclusive).get(0);
                model.applyCondition(incomingFlow, conditionString);

                //one last thing. We want to avoid having "one in, one out"
                // types of gateways,
                //so to avoid this situation we want to do one last check:
                if (model.isUselessGateway(newExclusive)) {
                    model.deleteUselesGateway(newExclusive);
                }
            }
        }
    }


    private static String calculateNewCondition (ArrayList<String> conditions) {

        if (conditions.size() == 0) {
            return "";
        }

        StringBuilder start = new StringBuilder("" + conditions.get(0));

        for (int i = 1 ; i < conditions.size() ; i++) {
            start.append(" || ").append(conditions.get(i));
        }

        return start.toString();
    }


}

