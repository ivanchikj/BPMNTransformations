

import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;


public class Reverse4 {


    //TODO scrivi nella tesi ogni volta che vedi una task che ha due
    // outgoingFlows, crea un parallel davanti e usa quello come split"
    //TODO devo anche controllare che NON ci sia una condition in nessuno
    // degli outgoingFlows
    public static void a (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE4a to model " + model.name);


        ArrayList<Element> tasks = model.findElementsByType("task");

        for (Element task : tasks) {

            ArrayList<Element> outgoingFlows = model.getOutgoingFlows(task);

            //if it has more than one outgoing Flow then we can add a
            // parallel in there
            //BUT only if none of the incoming flows has a condiition
            if (outgoingFlows.size() > 1) {

                //let's check that NONE of the outgoing flows has a condition
                //TODO actually if the condition is always true, it doesn't
                // matter.
                //Write in the thesis that future improvements could consider
                // that.
                boolean oneFlowHasACondition = false;
                for (Element flow : outgoingFlows) {
                    if (model.hasCondition(flow)) {
                        oneFlowHasACondition = true;
                    }
                }

                //Now that we know that none of the multiple outgoing flows
                // has a condition we can apply the rule
                if (! oneFlowHasACondition) {

                    //Before creating the new parallel gateway, we have to
                    // calculate its future position

                    ArrayList<Element> successors = model.getSuccessors(task);

                    Coordinates position =
                     model.calculatePositionOfNewNode(task, successors);

                    String newParallelID = model.newParallelGateway(position);

                    //finally here's the newly created parallel gateway
                    //Element newParall = model.findElemById(newParallelID);

                    //let's change the two outgoing flows of our task to have
                    // the parallel element as their source
                    for (Element flow : outgoingFlows) {
                        model.setSource(flow.getAttribute("id"), newParallelID);
                    }

                    //let's now create a new flow from the task to the new
                    // parallel
                    model.newSequenceFlow(task.getAttribute("id"),
                     newParallelID);
                }
            }
        }
    }


    //TODO scrivi nella tesi 
    //Ogni volta che vedi una task che ha due incoming, mettici un exclusive
    // gateway merge davanti
    static void b (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE4b to model " + model.path);

        //TODO spiegare nella tesi perché non si può applicare alle altre
        // cose che non sono task
        //TODO Ana ha detto che si può applicare anche agli eventi. Basta
        // cercarli con il loro tag e poi
        //aggiungerli alla lista tasks (che a questo punto avrà un nome diverso)

        ArrayList<Element> tasks = model.findElementsByType("task");

        for (Element task : tasks) {

            ArrayList<Element> incomingFlows = model.getIncomingFlows(task);

            //if it has more than one incoming Flow then we can add a
            // parallel in there

            if (incomingFlows.size() > 1) {

                //Before creating the new exclusive gateway, we have to
                // calculate its future position

                ArrayList<Element> predecessors = model.getPredecessors(task);

                Coordinates position =
                 model.calculatePositionOfNewNode(predecessors, task);

                String newExclusiveID = model.newExclusiveGateway(position);

                //finally here's the newly created exclusive gateway
                //Element newExclus = model.findElemById(newExclusiveID);

                //let's change the two incoming flows of our task to have the
                // exclusive element as their Target
                for (Element flow : incomingFlows) {
                    model.setTarget(flow.getAttribute("id"), newExclusiveID);
                }

                //let's now create a new flow from the task to the new exclusive
                model.newSequenceFlow(newExclusiveID, task.getAttribute("id"));
            }
        }
    }


    //TODO to write in the thesis:
    //This starts out like rule 4aR but we can apply regardless of the fact
    // that the outgoingFlows of a task
    //has a condition or not
    static void c (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE4c to model " + model.path);

        ArrayList<Element> tasks = model.findElementsByType("task");

        for (Element task : tasks) {

            ArrayList<Element> outgoingFlows = model.getOutgoingFlows(task);

            //if it has more than one outgoing Flow then we can add a
            // parallel in there
            //BUT only if none of the incoming flows has a condition

            if (outgoingFlows.size() > 1 && allOutHaveNoConditions(outgoingFlows,
             model)) {

                //Before creating the new parallel gateway, we have to
                // calculate its future position

                ArrayList<Element> successors = model.getSuccessors(task);

                Coordinates position = model.calculatePositionOfNewNode(task,
                 successors);

                String newParallelID = model.newInclusiveGateway(position);

                //finally here's the newly created parallel gateway
                model.findElemById(newParallelID);

                //let's change the two outgoing flows of our task to have the
                // parallel element as their source
                for (Element flow : outgoingFlows) {
                    model.setSource(flow.getAttribute("id"), newParallelID);
                }

                //let's now create a new flow from the task to the new parallel
                model.newSequenceFlow(task.getAttribute("id"), newParallelID);
            }
        }
    }


    /**
     * Checks if all the flows contained in outgoingFlows have no conditions
     * @param outgoingFlows
     * @param model
     * @return true if none has a condition, false otherwise.
     */
    private static boolean allOutHaveNoConditions (ArrayList<Element> outgoingFlows, Model model) {

        for (Element flow : outgoingFlows) {
            if (model.hasCondition(flow)) {
                return false;
            }
        }
        return true;
    }

    //TODO decidi se quest'ordine va bene
    public static void all (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE4 to model " + model.path);

        a(model);
        b(model);
        c(model);
    }


}
