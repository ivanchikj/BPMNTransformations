import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * This is an example Rule that can be modified to quickly add an additional
 * rule to the program. The content of apply is just an example and can
 * be deleted.
 */
class Rule5 {


    /**
     * In this example rule 5 deletes Tasks from the startingModel whose name
     * begins with an 'e' or 'E' and has exactly one outgoing flow and one
     * incoming flow.
     *
     * @param model
     * @throws Exception
     */
    static void apply (Model model) throws Exception {

        System.out.println("I'm applying rule RULE5 to model " + model.path);

        // usually rules start with the collection of some sort of elements,
        ArrayList<Element> tasks = model.findElementsByType("Task");
        System.out.println("number of task instances: " + tasks.size());

        if (tasks.size() == 0) {
            System.out.println("RULE5: there are no tasks in " + "this" + " " + "model");
        }

        //Usually there is a for that goes through all the elements that it
        // found.
        for (Element task : tasks) {

            ArrayList<Element> preds = model.getPredecessors(task);
            ArrayList<Element> succs = model.getSuccessors(task);
            //let's get the first letter of the name
            Character firstChar =
             task.getAttribute("name").toLowerCase().charAt(0);

            if (preds.size() == 1 && succs.size() == 1 && firstChar == 'e') {

                //before deleting the task we have to adjust its incoming and
                //outgoing sequence flows

                ArrayList<Element> inc = model.getIncomingFlows(task);
                ArrayList<Element> out = model.getOutgoingFlows(task);

                for (Element incF : inc) {
                    model.delete(incF.getAttribute("id"));
                }

                for (Element outF : out) {
                    model.setSource(outF.getAttribute("id"),
                    preds.get(0).getAttribute("id"));
                }

                model.delete(task.getAttribute("id"));
            }
        }
    }


}