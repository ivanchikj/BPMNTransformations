import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

//TODO i metodi statici potrei metterli in un altro file, o in più di uno.
// Magari un file chiamato translator che traduce da signavio a camunda e
// viceversa e altre cose simili. Per una questione di ordine.




//TODO:
//Invece di chiamare i metodi che fanno la traduzione da signavio a camunda
// dentro le regole, sarebbe meglio usare la traduzione come prima riga nei
// metodi di Model.java
public class Model {


    public Document doc; // This is the xml representation of my model
    private Element process; //This is the process view of the model
    private Element bpmndiDiagram; //This is the BPMNDI view of the model,
    // here are stored the positions of all nodes and SequenceFlows
    private Element bpmndiPlane; //A model can have more than one plane TODO
    // consider this. Maybe this can become an arrayList and in the Rules we
    // can have a for loop circling everything. This way rules get applied
    // for every plane. This will have to be considered inside Travel
    // .AreModelsDifferent() etc..
    private String tagStyle; // can be only 'signavio' or 'camunda' for now.
    public String path; /// TODO decide whether this will be private or not.
    public String name; //The name of the model. At the beginning it's equal
    // to the filename minus the extension.
    private XPath xpath; //Used to find certain elements inside the document
    private DocumentBuilder docBuilder;
    private ArrayList<String> rulesApplied; //this keeps track of the rules
    // tha have been applied to a model

    //TODO fare metodo che prende un gateway e controlla se è un merge. Mi
    // serve in più di una regola mi sa. Quindi è meglio metterlo qua


    public Document getDoc () {

        return doc;
    }


    public void setDoc (Document doc) {

        this.doc = doc;
    }


    public Element getProcess () {

        return process;
    }


    public void setProcess (Element process) {

        this.process = process;
    }


    public Element getBpmndiDiagram () {

        return bpmndiDiagram;
    }


    public void setBpmndiDiagram (Element bpmndiDiagram) {

        this.bpmndiDiagram = bpmndiDiagram;
    }


    public Element getBpmndiPlane () {

        return bpmndiPlane;
    }


    public void setBpmndiPlane (Element bpmndiPlane) {

        this.bpmndiPlane = bpmndiPlane;
    }


    public String getTagStyle () {

        return tagStyle;
    }


    public void setTagStyle (String tagStyle) {

        this.tagStyle = tagStyle;
    }


    public String getPath () {

        return path;
    }


    public void setPath (String path) {

        this.path = path;
    }


    public XPath getXpath () {

        return xpath;
    }


    public void setXpath (XPath xpath) {

        this.xpath = xpath;
    }


    public DocumentBuilder getDocBuilder () {

        return docBuilder;
    }


    public void setDocBuilder (DocumentBuilder docBuilder) {

        this.docBuilder = docBuilder;
    }


    public ArrayList<String> getRulesApplied () {

        return rulesApplied;
    }


    public void setRulesApplied (ArrayList<String> rulesApplied) {

        this.rulesApplied = rulesApplied;
    }


    /**
     * @param path the filepath of the Model
     */
    public Model (String path) throws IOException, org.xml.sax.SAXException,
     ParserConfigurationException {
        //System.out.println("New model instance: "); //UNLOCKTHIS

        this.path = path;

        String nameWithExtension = path.substring(path.lastIndexOf("/") + 1);

        System.out.println(nameWithExtension);

        this.name = nameWithExtension.substring(0,
         nameWithExtension.indexOf('.'));
        // Xpath needed to easily interact with XMLs
        XPathFactory xPathFactory = XPathFactory.newInstance();

        this.xpath = xPathFactory.newXPath();

        DocumentBuilderFactory docFactory =
         DocumentBuilderFactory.newInstance();
        this.docBuilder = docFactory.newDocumentBuilder();

        System.out.println("		Opening file at position" + path);
        //UNLOCKTHIS

        this.doc = docBuilder.parse(path);

        this.tagStyle = this.findOutTagStyle();

        this.process = (Element) doc.getElementsByTagName(this.style("process"
        )).item(0); // TODO what happens if there's more than one process?
        //System.out.println("		I'm working on the following Process: " +
        // process.getAttribute("id"));//UNLOCKTHIS

        this.bpmndiDiagram = (Element) doc.getElementsByTagName(this.style(
        "bpmndi:BPMNDiagram")).item(0); //this assumes there's always one
        // single bmpdi:BMPNDiagram, and one single process so it's always
        // item(0)

        this.bpmndiPlane = (Element) doc.getElementsByTagName(this.style(
        "bpmndi:BPMNPlane")).item(0);
        //System.out.println(	"		I'm working on the following
        // bpmndiDiagram: " + bpmndiDiagram.getAttribute("id"));

        this.rulesApplied = new ArrayList<>();
    }


    /**
     * Saves the model in a folder called "temp". Used to clone models in an
     * ugly way.
     */
    private String saveInTemp () throws TransformerException {

        return this.saveToFile("./temp/", ".bpmn.xml");
    }


    /**
     * @return the cloned Model
     */
    Model cloneModel () {
        //TODO fai una cosa sensata per clonare modelli come avevi iniziato a
        // fare qui sotto senza dover riaprire un file da disco.
        //TODO vedi questo : https://stackoverflow
        // .com/questions/279154/how-can-i-clone-an-entire-document-using-the
        // -java-dom

        try {
            String temppath = saveInTemp();
            return new Model(temppath); //TODO questo non funziona con file
            // che hanno spazi nel nome, risolvi. Gli spazi vengono
            // convertiti in '%20' e quindi il file non viene aperto nel modo
            // giusto. Inoltre il file finale viene salvato con '%20' nel
            // nome ed è brutto.
        } catch (IOException | TransformerException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return this;

//        this.name = m.name;
//        this.path = m.path;
//        XPathFactory xPathFactory = XPathFactory.newInstance();
//        this.xpath = xPathFactory.newXPath();
//        DocumentBuilderFactory docFactory = DocumentBuilderFactory
// .newInstance();
//        this.docBuilder = docFactory.newDocumentBuilder();
//
//        this.doc = m.doc;
//        this.process = m.process;
//        this.bpmndiPlane = m.bpmndiPlane;
//
//        this.bpmndiDiagram = m.bpmndiDiagram;
//        this.tagStyle = m.tagStyle;
//        this.rulesApplied = m.rulesApplied;
    }


    /**
     * TODO metti questa cosa nella tesi
     * This methods finds out whether the user-provided file that represents
     * this model is written using Signavio or
     * Camunda's tag style. To do so, it searches for the startEvent in the
     * model using both styles.
     *
     * @return the name of the style that this Model uses. Will be "" if the
     * style is unknown; this will also prompt an error message. //TODO
     * impedisci che questa cosa blocchi tutta l'esecuzione del programma.
     * MAgari impedisci che venga aggiunto agli startingModel di una execution.
     */
    private String findOutTagStyle () {

        Element camunda = (Element) doc.getElementsByTagName("bpmn:startEvent"
        ).item(0);
        Element signavio =
         (Element) doc.getElementsByTagName("startEvent").item(0);

        if (camunda != null) {
            //System.out.println("This model uses the camunda style for the
            // tag names.");
            return "camunda";
        } else if (signavio != null) {
            //System.out.println("This model uses the signavio style for the
            // tag names.");
            return "signavio";
        } else {
            System.err.println("THIS SOFTWARE ONLY SUPPORTS MODELS CREATED " + "WITH CAMUNDA OR SIGNAVIO");
            System.err.println("The model " + this.name + " appears to be " + "written in a different style.");
        }
        return "";
    }

    //TODO usare sempre questo invece del metodo che ritorna la nodeList.
    ArrayList<Element> findElementsByType (String type) {

        String t = style(type);
        NodeList nl = doc.getElementsByTagName(t);
        ArrayList<Element> ae = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++){
            ae.add((Element) nl.item(i));
        }
        return ae;
    }


    /**
     * used when editing the model. This, together with rulesApplied should
     * always be upgraded together
     *
     * @param rule        the rule that will be added to the model
     * @param aggregateBy changed the behavior of the rule todo spiega meglio?
     */
    private void addRuleToName (String rule, int aggregateBy) {

        if (aggregateBy > 0) {
            this.name += rule + "*" + aggregateBy;
        } else {
            this.name += rule;
        }
    }


    void addRule (Parameter parameter) {

        String rule = parameter.rule;
        addRuleToName(rule, parameter.aggregateBy);
        rulesApplied.add(rule);
    }


    /**
     * TODO SPIEGA PERCHÉ QUESTO METODO ESISTE
     */
    void addOutputInPath () {
        //i need to add 'output/' in the new path
        String original = this.path;
        String name = original.substring(original.lastIndexOf("/") + 1);
        String folder = original.substring(0, original.lastIndexOf(name)) +
        "output/";
        this.path = folder + name;
    }


    /**
     * This method is actually not relevant for our transformations.
     * It was one of my first tries with the BPMNDI, and it should still work.
     *
     * @return the id of the newTask
     */
//    public String newTask (String x, String y) {
//        // PROCESS VIEW
//        String id = newId();
//        Element newTask = doc.createElement(this.style("task"));
//        newTask.setAttribute("id", id);
//        //newTask.setAttribute("name", "NEW");
//        process.appendChild(newTask);
//
//        // BPMNDI VIEW
//        Element newTaskDI = doc.createElement(this.style("bpmndi:BPMNShape"));
//        bpmndiDiagram.appendChild(newTaskDI);
//        newTaskDI.setAttribute("bpmnElement", id);
//        newTaskDI.setAttribute("id", id + "_di"); // I don't know if this is
//        // mandatory
//        //System.out.println("		I created a new Task with the id " +
// id);
//
//        Element size = doc.createElement(this.style("Bounds"));
//        bpmndiPlane.appendChild(newTaskDI);
//
//        newTaskDI.appendChild(size);
//        size.setAttribute("height", "80");
//        size.setAttribute("width", "100");
//        size.setAttribute("x", x);
//        size.setAttribute("y", y);
//        return id;
//    }

    String newInclusiveGateway (Coordinates c) {
        String x = c.x;
        String y = c.y;

        // PROCESS VIEW
        String id = newId();
        Element newNode = doc.createElement(this.style("inclusiveGateway"));
        newNode.setAttribute("id", id);
        //newNode.setAttribute("name", "NEW");
        this.process.appendChild(newNode);

        // BPMNDI VIEW
        Element newNodeDI = doc.createElement(this.style("bpmndi:BPMNShape"));
        //bpmndiDiagram.appendChild(newGatDI);
        bpmndiPlane.appendChild(newNodeDI);
        newNodeDI.setAttribute("bpmnElement", id);
        newNodeDI.setAttribute("id", id + "_di"); // I don't know if this is
        // mandatory
        //System.out.println("		I created a new Inclusive Gateway with the
        // id " + id);

        Element size = doc.createElement(this.style("Bounds"));
        newNodeDI.appendChild(size);
        size.setAttribute("height", "50");
        size.setAttribute("width", "50");
        size.setAttribute("x", x);
        size.setAttribute("y", y);
        return id;
    }


    String newParallelGateway (Coordinates c) {
        String x = c.x;
        String y = c.y;
        // PROCESS VIEW
        String id = newId();
        Element newNode = doc.createElement(this.style("parallelGateway"));
        newNode.setAttribute("id", id);
        //newNode.setAttribute("name", "NEW");
        process.appendChild(newNode);

        // BPMNDI VIEW
        Element newNodeDI = doc.createElement(this.style("bpmndi:BPMNShape"));
        //bpmndiDiagram.appendChild(newGatDI);
        newNodeDI.setAttribute("bpmnElement", id);
        newNodeDI.setAttribute("id", id + "_di"); // I don't know if this is
        // mandatory
        //System.out.println("		I created a new Parallel Gateway with the
        // id " + id);

        Element size = doc.createElement(this.style("Bounds"));
        bpmndiPlane.appendChild(newNodeDI);

        newNodeDI.appendChild(size);
        size.setAttribute("height", "50");
        size.setAttribute("width", "50");
        size.setAttribute("x", x);
        size.setAttribute("y", y);
        return id;
    }


    String newExclusiveGateway (Coordinates c) {

        String x = c.x;
        String y = c.y;

        // PROCESS VIEW
        String id = newId();
        Element newNode = doc.createElement(this.style("exclusiveGateway"));
        newNode.setAttribute("id", id);
        //newNode.setAttribute("name", "NEW");
        process.appendChild(newNode);

        // BPMNDI VIEW
        Element newNodeDI = doc.createElement(this.style("bpmndi:BPMNShape"));
        //bpmndiDiagram.appendChild(newGatDI);
        newNodeDI.setAttribute("bpmnElement", id);
        newNodeDI.setAttribute("id", id + "_di"); //It seems like adding "di"
        // after the id is common practice for BPMNDI elements.
        //System.out.println("		I created a new Parallel Gateway with the
        // id " + id);

        Element size = doc.createElement(this.style("Bounds"));
        bpmndiPlane.appendChild(newNodeDI);

        newNodeDI.appendChild(size);
        size.setAttribute("height", "50");
        size.setAttribute("width", "50");
        size.setAttribute("x", x);
        size.setAttribute("y", y);
        return id;
    }


    /**
     * TODO manage BPMNDI TODO give option to create with a condition. Like, "if
     * condition is null, then apply none".
     * TODO usa gli element invece degli id come parametri
     *
     * @param sourceID the id of the source of the new sequenceFlow
     * @param targetID the id of the target of the new sequenceFlow
     * @return the id of the newly created sequenceFlow
     */
    @SuppressWarnings ("UnusedReturnValue")
    String newSequenceFlow (String sourceID, String targetID) throws XPathExpressionException {

        String id = newId();
        Element flow = doc.createElement(this.style("sequenceFlow"));
        flow.setAttribute("id", id);
        flow.setAttribute("sourceRef", sourceID);
        flow.setAttribute("targetRef", targetID);
        process.appendChild(flow);

        //BPMNDI
        Element newFlowDI = doc.createElement(this.style("bpmndi:BPMNEdge"));
        bpmndiPlane.appendChild(newFlowDI);
        newFlowDI.setAttribute("bpmnElement", id);
        newFlowDI.setAttribute("id", id + "_di");
        //System.out.println("		I created a new SequenceFlow with the id "
        // + id + " and the source " + sourceID
        //	+ " and the target " + targetID);

        //Element source = findElemById(sourceID);
        //Element target = findElemById(targetID);

        //TODO fare un metodo per avere altezza, larghezza, e posizione di un
        // elemento a partire da un ELEMENT e in una hashmap.
        //Perché così è brutto.

        Element sourceWP = doc.createElement("waypoint");

        Element targetWP = doc.createElement("waypoint");

        newFlowDI.appendChild(sourceWP);
        newFlowDI.appendChild(targetWP);

        setSource(id, sourceID);
        setTarget(id, targetID);

        return id;
    }


    /**
     * TODO aggiungere un errore quando l'id non corrisponde a un elemento
     * SequenceFlow TODO manage BPMNDI aspects
     * TODO vedi se puoi riutilizzare alcuni pezzi per il metodo newSequenceFlow
     * TODO vedi cosa succede se una task è source di due flow, se ne cambi
     * uno, cosa succede al secondo?
     *
     * @param id     the id of the sequenceFlow that will be changing source
     * @param source the new source
     */
    void setSource (String id, String source) throws XPathExpressionException {

        System.out.println("SETSOURCE");
        System.out.println("ID of the flow: " + id);
        System.out.println("new SRC " + source);
        String previousSourceId = findElemById(id).getAttribute("sourceRef");
        Element previousSource = findElemById(previousSourceId);
        Element sequenceFlow = findElemById(id);

//	System.out.println("		This element's previous source is: " +
// previousSource.getAttribute("id"));
//	System.out.println("");
//
//	System.out.println("		Content of child: " + previousSource
// .getTextContent());
//
//	System.out.println("		Content of 2child: " + xpath.evaluate("./text
// ()", previousSource));


        deleteFlowFromOldSourceOrTarget(sequenceFlow, previousSource);
        //	System.out.println("		The id of the sequenceFlow that I have
        // found is " + sequenceFlow.getAttribute("id"));
        sequenceFlow.setAttribute("sourceRef", source);
        // We still need to change also the element of the Source to have my
        // outgoing
        // flow as a child
        Element sourceElement = findElemById(source);
        System.out.println("		The new source is: " + source);
        Element outgoing = doc.createElement(style("outgoing"));
        outgoing.appendChild(doc.createTextNode(id)); //This adds the id as a
        // text inside the tags
        sourceElement.appendChild(outgoing);

        //Since it's impossible to distinguish the source waypoints from the
        // target waipoints,
        //(aside from looking at the order, but this doesn't seem like a good
        // solution
        //it's best to simply delete all
        //existing waypoints and create two of them from scratch.

        //we want to get the position of the source to know where the flow
        // will have to point
        Element sourceBPMNDI = findBPMNDI(source);

        Element sourceDcBounds = findDcBounds(sourceBPMNDI); //the dc:bounds
        // tag contains the info about the position

        String xSource = sourceDcBounds.getAttribute("x");
        String ySource = sourceDcBounds.getAttribute("y");
        String sourceItemHeight = sourceDcBounds.getAttribute("height");
        String sourceItemWidth = sourceDcBounds.getAttribute("width");

        //we want to get the position of the target to know where the flow
        // will have to point
        String target = sequenceFlow.getAttribute("targetRef");
        Element targetBPMNDI = findBPMNDI(target);
        Element targetDcBounds = findDcBounds(targetBPMNDI); //the dc:bounds
        // tag contains the info about the position
        String xTarget = targetDcBounds.getAttribute("x");
        String yTarget = targetDcBounds.getAttribute("y");
        String targetItemHeight = targetDcBounds.getAttribute("height");
        String targetItemWidth = targetDcBounds.getAttribute("width");

        //Calculating the best options for the placement of the sequenceFlow
        String[] seQFlowPositions = decideArrowPosition(xSource, ySource,
         sourceItemHeight, sourceItemWidth, xTarget, yTarget,
          targetItemHeight, targetItemWidth);

        Element sourceWP = createWaypoint(seQFlowPositions[0],
         seQFlowPositions[1]);
        Element targetWP = createWaypoint(seQFlowPositions[2],
         seQFlowPositions[3]);

        //This is the bpmndi corresponding to our sequenceFlow
        Element sequenceFlowBPMNDI = findBPMNDI(id);

        //let's remove the previous waypoints:
        while (sequenceFlowBPMNDI.hasChildNodes()) {
            sequenceFlowBPMNDI.removeChild(sequenceFlowBPMNDI.getFirstChild());
//	    System.out.println("		Just deleted the a waypoint");
        }

        //let's now add the previously created waypoints:
        //NOTE: the order in which the waypoints are added decides the order
        // of the arrow!
        //This is a little bit counterintuitive imho, but it is like it is.
        sequenceFlowBPMNDI.appendChild(sourceWP);
        sequenceFlowBPMNDI.appendChild(targetWP);

        //System.out.println("		I have changed the source of flow " + id +
        // " to " + source);
    }


    /**
     * used to get an item's position
     * (for example when wanting to substitute it
     * TODO change the methods setSource and setTarget to use this method
     * X is in the first position, Y in the second
     */
    Coordinates getPosition (Element element) throws XPathExpressionException {

        Element elementBPMNDI = findBPMNDI(element.getAttribute("id"));
        Element dcBounds = findDcBounds(elementBPMNDI); //The dcBounds
        // contains the coordinates
        //System.out.println("Get Position  X : "+ dcBounds.getAttribute("x")
        // + "Y: " + dcBounds.getAttribute("y"));
        return new Coordinates(dcBounds.getAttribute("x"),
         dcBounds.getAttribute("y"));

    }


    /**
     * This method calculates the x and y positions of a new element based on
     * the position of its predecessors
     * It will be useful when placing new elements based on the position of
     * the predecessor and the successor.
     * If as it often happens an element has more than one predecessor and
     * more than one successor,
     * first we calculate the average position of the successors, then the
     * average position of the predecessors
     * And we place the new element in the average of the two averages.
     * <p>
     * <p>
     * TODO explain this in the thesis
     *
     * @param predecessors the predecessors of the new node i want to
     *                     calculate the position of. It can be only one.
     * @param successors   the successors of the new node i want to
     *                     calculate the position of. It can be only one.
     * @return positions, the coordinates of the X axis are in the [0]
     * position while the y information is stored on the [1] position
     */
    private Coordinates calculatePositionOfNewNode (ArrayList<Element> predecessors,
     ArrayList<Element> successors) throws NumberFormatException, XPathExpressionException {

        //let's calculate the average positions of the predecessors on the X
        // axis
        int avgPredX = 0;
        for (Element predecessor : predecessors) {
            avgPredX += Double.parseDouble(getPosition(predecessor).x);
        }
        avgPredX = avgPredX / predecessors.size();

        //let's calculate the average positions of the predecessors on the Y
        // axis
        int avgPredY = 0;
        for (Element predecessor : predecessors) {
            avgPredY += Double.parseDouble(getPosition(predecessor).y);
        }
        avgPredY = avgPredY / predecessors.size();

        //let's calculate the average positions of the successors on the X axis
        int avgSuccX = 0;
        for (Element successor : successors) {
            avgSuccX += Double.parseDouble(getPosition(successor).x);
        }
        avgSuccX = avgSuccX / successors.size();

        //let's calculate the average positions of the successors on the Y axis
        int avgSuccY = 0;
        for (Element successor : successors) {
            avgSuccY += Double.parseDouble(getPosition(successor).y);
        }
        avgSuccY = avgSuccY / successors.size();

        String x = "" + (avgPredX + avgSuccX) / 2; //not super
        // precise
        // but enough for our scope
        String y = "" + (avgPredY + avgSuccY) / 2;

        return new Coordinates(x, y);
    }


    /**
     * This version of the method is used when having multiple predecessors
     * (for example in a merge)
     * //TODO Use Coordinates instead of array of strings
     *
     * @param predecessors the predecessors of the new node
     * @param successor    the successors of the new node
     * @return the coordinates of the new node
     */
    Coordinates calculatePositionOfNewNode (ArrayList<Element> predecessors,
     Element successor) throws NumberFormatException, XPathExpressionException {

        ArrayList<Element> successors = new ArrayList<>();
        successors.add(successor);
        return calculatePositionOfNewNode(predecessors,
         successors);
    }


    /**
     * This version of the method is used when having multiple successors
     * (for example in a split)
     * //TODO Use Coordinates instead of array of strings
     *
     * @param predecessor the predecessor of the new node
     * @param successors  the successors of the new node
     * @return the coordinates of the new node
     * @throws NumberFormatException
     * @throws XPathExpressionException
     */
    Coordinates calculatePositionOfNewNode (Element predecessor,
     ArrayList<Element> successors) throws NumberFormatException,
      XPathExpressionException {

        ArrayList<Element> predecessors = new ArrayList<>();
        predecessors.add(predecessor);
        return calculatePositionOfNewNode(predecessors, successors);
    }


    /**
     * TODO cambialo per avere due element invece di due id come parametri
     * TODO in generale cerca di cambiare tutti i metodi per avere elementi
     * invece che stringhe come parametri
     * SequenceFlow TODO manage BPMNDI aspects
     *
     * @param id     the id of the sequenceFlow that will be changing source
     * @param target the new target
     */
    void setTarget (String id, String target) throws DOMException,
     XPathExpressionException {

        String previousTargetId = findElemById(id).getAttribute("targetRef");
        Element previousTarget = findElemById(previousTargetId);
//	System.out.println("		This element's previous target is: " +
// previousTarget.getAttribute("id"));
//	System.out.println("");
//
//	System.out.println("		Content of child: " + previousTarget
// .getTextContent());

        Element sequenceFlow = findElemById(id);

        deleteFlowFromOldSourceOrTarget(sequenceFlow, previousTarget);

//	System.out.println("		The id of the sequenceFlow that I have found
// is " + sequenceFlow.getAttribute("id"));
        sequenceFlow.setAttribute("targetRef", target);
        // We still need to change also the element of the Source to have my
        // outgoing
        // flow as a child
        Element targetElement = findElemById(target);
//	System.out.println("		The new target is: " + targetElement
// .getAttribute("id"));
        Element incoming = doc.createElement(style("incoming"));
        incoming.appendChild(doc.createTextNode(id)); //This adds the id as a
        // text inside the tags
        targetElement.appendChild(incoming);

        //Since it's impossible to distinguish the source waypoints from the
        // target waipoints,
        //(aside from looking at the order, but this doesn't seem like a good
        // solution
        //it's best to simply delete all
        //existing waypoints and create two of them from scratch.

        //we want to get the position of the source to know where the flow
        // will have to point

        Element targetBPMNDI = findBPMNDI(target);

        Element targetDcBounds = findDcBounds(targetBPMNDI); //the dc:bounds
        // tag contains the info about the position

        //TODO make this position thing a separate method
        //System.out.println("Positions will follow");
        String xTarget = targetDcBounds.getAttribute("x");
        //System.out.println(xTarget);
        String yTarget = targetDcBounds.getAttribute("y");
        //System.out.println(yTarget);
        String targetItemHeight = targetDcBounds.getAttribute("height");
        //System.out.println(targetItemHeight);
        String targetItemWidth = targetDcBounds.getAttribute("width");
        //System.out.println(targetItemWidth);

        //we want to get the position of the target to know where the flow
        // will have to point
        String source = sequenceFlow.getAttribute("sourceRef");
        Element sourceBPMNDI = findBPMNDI(source);
        Element sourceDcBounds = findDcBounds(sourceBPMNDI); //the dc:bounds
        // tag contains the info about the position
        String xSource = sourceDcBounds.getAttribute("x");
        //System.out.println(xSource);
        String ySource = sourceDcBounds.getAttribute("y");
        //System.out.println(ySource);
        String sourceItemHeight = sourceDcBounds.getAttribute("height");
        //System.out.println(sourceItemHeight);
        String sourceItemWidth = sourceDcBounds.getAttribute("width");
        //System.out.println(sourceItemWidth);

        //Calculating the best options for the placement of the sequenceFlow
        String[] seQFlowPositions = decideArrowPosition(xSource, ySource,
         sourceItemHeight, sourceItemWidth, xTarget, yTarget,
          targetItemHeight, targetItemWidth);

        Element sourceWP = createWaypoint(seQFlowPositions[0],
         seQFlowPositions[1]);
        Element targetWP = createWaypoint(seQFlowPositions[2],
         seQFlowPositions[3]);

        //This is the bpmndi corresponding to our sequenceFlow
        Element sequenceFlowBPMNDI = findBPMNDI(id);

        //let's remove the previous waypoints:
        while (sequenceFlowBPMNDI.hasChildNodes()) {
            sequenceFlowBPMNDI.removeChild(sequenceFlowBPMNDI.getFirstChild());
            // System.out.println("		Just deleted the waypoint");
        }

        //let's now add the previously created waypoints:
        //NOTE: the order in which the waypoints are added decides the order
        // of the arrow!
        //This is a little bit counterintuitive imho, but it is like it is.
        sequenceFlowBPMNDI.appendChild(sourceWP);
        sequenceFlowBPMNDI.appendChild(targetWP);

        findDcBounds(targetBPMNDI);

        //System.out.println("		I have changed the source of flow " + id +
        // " to " + source);
    }


    /**
     *
     * @param element the id of said Element
     * @return a NodeList of the successors of a certain Element
     */
    ArrayList<Element> getSuccessors (Element element) throws XPathExpressionException {

        ArrayList<Element> outgoingFlows = getOutgoingFlows(element);
        ArrayList<Element> successors = new ArrayList<>();

        for (Element outgoingFlow : outgoingFlows) {
            successors.add(findElemById(outgoingFlow.getAttribute("targetRef")));
        }
        //System.out.println("		I have found " + successors.size() + "
        // immediate successors");
        return successors;
    }


    ArrayList<Element> getPredecessors (Element element) throws XPathExpressionException {

        ArrayList<Element> incomingFlows = getIncomingFlows(element);
        ArrayList<Element> predecessors = new ArrayList<>();

        for (Element incomingFlow : incomingFlows) {
            predecessors.add(findElemById(incomingFlow.getAttribute(
            "sourceRef")));
        }
        //System.out.println("		I have found " + predecessors.size() + "
        // immediate predecessors");
        return predecessors;
    }


    /**
     * From an element, get its outgoing flows as an array of elements
     *
     * @param element the element we want to find the outgoing flows of
     * @return an ArrayList containing the sequenceFlows' elements of the
     * sequenceFlows that are outgoing from the Element
     */
    ArrayList<Element> getOutgoingFlows (Element element) throws XPathExpressionException {

        String id = element.getAttribute("id");
        NodeList outgoingFlowsNodes = xpathFindNodeWithCertainAttributeValue(
        "sourceRef", id);
        ArrayList<Element> outgoingFlows = new ArrayList<>();
        for (int i = 0 ; i < outgoingFlowsNodes.getLength() ; i++) { //TODO
            // why not <=?
            //System.out.println("Getting outgoingFlow: "+ ((Element)
            // outgoingFlowsNodes.item(i)).getAttribute("id"));
            outgoingFlows.add((Element) outgoingFlowsNodes.item(i));
        }
        //noinspection StatementWithEmptyBody
        if (outgoingFlows.size() == 0) {
            // System.out.println("this element has no outgoing Flows");
        } else {
            //System.out.println("The element " + element.getAttribute("id")
            // + " has " + outgoingFlows.size() + " outgoing Flows");
        }

        return outgoingFlows;
    }


    private NodeList xpathFindElementsWithCertainSource (String id) throws XPathExpressionException {

        return (NodeList) xpath.evaluate("//*[@sourceRef='" + id + "']", doc,
         XPathConstants.NODESET);
    }


    private NodeList xpathFindElementsWithCertainTarget (String id) throws XPathExpressionException {

        return (NodeList) xpath.evaluate("//*[@targetRef='" + id + "']", doc,
         XPathConstants.NODESET);
    }


    /**
     * From an element, get its incoming flows as an array of elements.
     *
     * @param element the element we want to find the incoming flows of
     * @return an ArrayList containing the elements of the sequenceFlows that
     * are incoming of the provided element
     */
    ArrayList<Element> getIncomingFlows (Element element) throws XPathExpressionException {

        String id = element.getAttribute("id");
        NodeList incomingFlowsNodes = xpathFindNodeWithCertainAttributeValue(
        "targetRef", id);
        ArrayList<Element> incomingFlows = new ArrayList<>();
        for (int i = 0 ; i < incomingFlowsNodes.getLength() ; i++) {
            //System.out.println("Getting incomingFlow: "+ ((Element)
            // incomingFlowsNodes.item(i)).getAttribute("id"));
            incomingFlows.add((Element) incomingFlowsNodes.item(i));
        }
        //noinspection StatementWithEmptyBody
        if (incomingFlows.size() == 0) {
            //System.out.println("this element has no incoming Flows");
        } else {
            //
            //System.out.println("The element " + element.getAttribute("id")
            // + " has " + incomingFlows.size() + " incoming Flows");
        }

        return incomingFlows;
    }


    /**
     * This checks whether a sequenceFlow has a condition or not.
     * If it has not, then there's no need to generate one and append it to
     * the new flows.
     *
     * @param sequenceFlow the sequenceFlow I want to check
     * @return true if the sequenceFlow has a condition, false otherwise.
     */
    boolean hasCondition (Element sequenceFlow) {

        NodeList children = sequenceFlow.getElementsByTagName(style(
        "conditionExpression")); //TODO

        boolean hasCondition = false;

        if (children.getLength() > 0) {
            hasCondition = true;
        }
        return hasCondition;
    }


    String returnConditionString (Element sequenceFlow) {

        if (this.hasCondition(sequenceFlow)) {
            Element conditionElement = returnConditionElement(sequenceFlow);
            return conditionElement.getTextContent();
        } else {
            //this should never happen in theory if I get a well formed bpmn
            // file.
            System.out.println("this should never happen in theory if I get " +
             "a" + " well formed bpmn file.");
            return "";
        }
    }


    Element returnConditionElement (Element sequenceFlow) {

        NodeList children = sequenceFlow.getElementsByTagName(style(
        "conditionExpression")); //TODO
        if (children.getLength() > 1) {
            System.err.println("How can an array have more than one " +
            "condition" + " children?");
        }
        return (Element) children.item(0);
    }


    /**
     * TODO maybe put this in the method class
     * If a condition is present, it deletes it and then adds the new one
     * inside.
     */
    void applyCondition (Element sequenceFlow, String condition) {

        if (hasCondition(sequenceFlow)) {
            // getting all the conditions of a sequenceFlow
            NodeList children = sequenceFlow.getElementsByTagName(style(
            "conditionExpression"));
            //removing all the previous conditions:
            for (int i = 0 ; i < children.getLength() ; i++) {
                Node child = children.item(i);
                sequenceFlow.removeChild(child);
                System.out.println("I've removed: ");
                System.out.println(child.getTextContent());

            }
        }
        Element conditionElement = doc.createElement(style(
        "conditionExpression"));
        conditionElement.setAttribute("xsi:type", style("tFormalExpression"));
        conditionElement.appendChild(doc.createTextNode(condition));
        sequenceFlow.appendChild(conditionElement);
        System.out.println("I've added:");
        System.out.println(conditionElement.getTextContent());
    }


    /**
     * Returns the type (tagname) of an element
     *
     * @param e the element i want to know the ID of
     * @return the tagname of the element. For a bpmn element, this should be
     * the type of node.
     */
    @SuppressWarnings ("unused")
    public String getType (Element e) {

        return e.getTagName();
    }


    /**
     * TODO use this instead of "replaceElement" when possible. This way you
     * won't risk losing attributes or child nodes
     *
     * TODO questo non funziona sempre. Vedi se riesci a risolvere
     * altrimenti continua a tenere il vecchio metodo.
     *
     * @param //e    the element that will be changed
     * @param //type the nwe type that the element will be. Has to be a valid
     *             BPMN Type
     */
    void changeType (Element e, String type) {

        String styledType = this.style(type);
        System.out.println("CIAO " +  styledType);
        this.doc.renameNode(e,this.doc.getNamespaceURI(), styledType);
    }


    //TODO this would be better if I had a class sequenceFlow
    Element getTarget (Element sequenceFlow) throws XPathExpressionException {

        String targetID = sequenceFlow.getAttribute("targetRef");
        return findElemById(targetID);
    }


    //TODO this would be better if I had a class sequenceFlow
    private Element getSource (Element sequenceFlow) throws XPathExpressionException {

        String sourceID = sequenceFlow.getAttribute("sourceRef");
        return findElemById(sourceID);
    }


    //TODO use this method in other rules
    void deleteIncomingFlows (Element element) throws XPathExpressionException {

        ArrayList<Element> incomingFlows = getIncomingFlows(element);
        for (Element flow : incomingFlows) {
            delete(flow.getAttribute("id"));
        }
    }


    //TODO use this method in other rules
    void deleteOutgoingFlows (Element element) throws XPathExpressionException {

        ArrayList<Element> outgoingFlows = getOutgoingFlows(element);
        for (Element flow : outgoingFlows) {
            delete(flow.getAttribute("id"));
        }
    }


    /**
     *
     * @param id the ID of the element to delete
     */
    void delete (String id) throws XPathExpressionException {
        //System.out.println("		I'm deleting the element with id " + id);
        Element elementToDelete = findElemById(id);
        String type = elementToDelete.getTagName();

        //If the element is a sequence flow, we also need to delete it from
        // it's target / source children
        if (type.contains("sequenceFlow")) {
            //Finding the parents
            Element oldSource = getSource(elementToDelete);
            Element oldTarget = getTarget(elementToDelete);
            //removingTheElement
            deleteFlowFromOldSourceOrTarget(elementToDelete, oldSource);
            deleteFlowFromOldSourceOrTarget(elementToDelete, oldTarget);
        }

        //from the PROCESS
        elementToDelete.getParentNode().removeChild(elementToDelete);
        //System.out.println("		I've deleted the element with id " + id);

        //FROM THE BPMNDI
        Element bpmndiElementToDelete = findBPMNDI(id);
        //System.out.println(bpmndiElementToDelete.getAttribute("bpmnElement"));
        bpmndiElementToDelete.getParentNode().removeChild(bpmndiElementToDelete);
    }


    private void delete (Element element) throws XPathExpressionException {

        String id = element.getAttribute("id");
        delete(id);
    }


    //TODO use this inside the SetSource and SetTarget methods.
    private void deleteFlowFromOldSourceOrTarget (Element sequenceFlow,
                                                  Element oldSourceOrTarget) {

        //noinspection StatementWithEmptyBody
        if (! sequenceFlow.getTagName().equals(style("sequenceFlow"))) {
            //System.out.println("This method is only for sequenceFlows");
        } else if (sequenceFlow.getTagName().equals(style("sequenceFlow"))) {
            String id = sequenceFlow.getAttribute("id");
            if (oldSourceOrTarget.hasChildNodes()) { //This is expected to be
                // always true anyway
                NodeList childList = oldSourceOrTarget.getChildNodes();
                for (int i = 0 ; i < childList.getLength() ; i++) {
                    Node childInCase = childList.item(i);
                    String textContentString = childInCase.getTextContent();
                    //System.out.println("		Searching for child to delete,
                    // child content in case: " + textContentString);
                    //System.out.println("		Searching for child to delete,
                    // child i'm looking for: " + id);
                    //TODO add a check to see if it's of the TAG
                    // bpmn:outgoing. It should
                    //be checked in case the task is both the source and the
                    // target of a SequenceFlow!
                    if (textContentString.equals(id)) {
                        childInCase.getParentNode().removeChild(childInCase);
                        //System.out.println("		I have found the child
                        // that I want to delete!!");
                        //TODO if you want, find a way to remove the blank
                        // space that gets created
                    }
                }
            }
        }
    }


    /**
     * Replace the old element with the new.
     * Nothing else should be affected (i.e. the incoming / outgoing
     * sequenceFlows)
     * @param newElem the old Element (will be deleted)
     * @param oldElem the new Element
     */
    void replaceElement (Element oldElem, Element newElem) throws XPathExpressionException {

        //It's also useful to have the bpmndi ready to edit:
        Element newElementBPMNDI = findBPMNDI(newElem.getAttribute("id"));

        //Let's save the ID of the old element
        String oldId = oldElem.getAttribute("id");
        System.out.println("CIAO");
        System.out.println("New el ID " + newElem.getAttribute("id"));
        System.out.println("Old El ID " + oldId);
        //let's save the child elements of the oldElement
        //those child elements will contain the oldElement's incoming and
        // outgoing sequenceFlows
        //we will soon attach them to the newElement
        NodeList oldElemChildNodes = oldElem.getChildNodes();

        //Let's now append the child nodes of the old element to the new element
        for (int n = 0 ; n < oldElemChildNodes.getLength() ; n++) {

            Node childNode = oldElemChildNodes.item(n);
            newElem.appendChild(childNode.cloneNode(true));
        }

        //let's delete the old element from the process
        process.removeChild(oldElem);

        bpmndiPlane.removeChild(newElementBPMNDI);
        //let's delete the BPMNDI of the new element (we will use that of the
        // old element)

        //let's change the ID of the new element to be equal to the id of the
        // old one
        newElem.setAttribute("id", oldId);
        String newElemId = newElem.getAttribute("id");

        //let's remember to change the id of the BPMNDI as well:
        //TODO DELETETHIS newElementBPMNDI.setAttribute("bpmnElement",
        // newElemId);
        //TODO DELETETHIS newElementBPMNDI.setAttribute("id", newElemId +
        // "_di");
    }

//
//    /**
//     * This method is used to delete elements from the diagram in an XML
// file. Note
//     * that I will use the attribute "BMPMN Element" to find the BMPNDI
// element that
//     * I want to delete, instead of "id", because the ID of a bpmndi
// element will
//     * sometimes be different from the corresponding BPMN element's own iD.
//     * <p>
//     * TODO probably this method shall be deleted
//     *
//     * @param id
//     * @throws ParserConfigurationException
//     * @throws SAXException
//     * @throws IOException
//     * @throws XPathExpressionException
//     * @throws TransformerException
//     * @throws TransformerConfigurationException
//     * @throws org.xml.sax.SAXException
//     */
//    public void deleteElement(String id)
//            throws ParserConfigurationException, SAXException, IOException,
// XPathExpressionException,
//            TransformerException, TransformerConfigurationException, org
// .xml.sax.SAXException {
//
//        //System.out.println("I'm searching for id: " + id);
//        NodeList nodeList = (NodeList) xpath.evaluate("//*[@bpmnElement='"
// + id + "']", doc, XPathConstants.NODESET);
//        for (int i = 0; i < nodeList.getLength(); ++i) { // This for is
// used in case there are more than one bmpndi with
//            // the same id, which shouldn't happen TODO decide
//            Element element = (Element) nodeList.item(i);
//            //System.out.println(element.getAttribute("bpmnElement"));
//            element.getParentNode().removeChild(element);
//            //System.out.println("		I removed element with ID: " + id);
//        }
//        //System.out.println("		Deleted element " + id);
//    }

//    /**
//     * NOTE this deletes all children of an element
//     *
//     * @param id
//     * @throws XPathExpressionException
//     */
//    public void deleteChildren(String id) throws XPathExpressionException {
//        Element element = findElemById(id);
//        if (element.hasChildNodes()) { //This is expected to be always true
// anyway
//            NodeList childList = element.getChildNodes();
//            for (int i = 0; i < childList.getLength(); i++) {
//                Node childInCase = childList.item(i);
//                String textContentString = childInCase.getTextContent();
//                //System.out.println("		Searching for child to delete,
// child content in case: " + textContentString); UNLOCKTHIS
//                //System.out.println("		Searching for child to delete,
// child i'm looking for: " + id); UNLOCKTHIS
//                //TODO add a check to see if it's of the TAG bpmn:outgoing.
// It should
//                //be checked in case the task is both the source and the
// target of a SequenceFlow!
//                if (textContentString.equals(id)) {
//                    childInCase.getParentNode().removeChild(childInCase);
//                    //System.out.println("		I have found the child
// that I want to delete!!");
//                    //TODO if you want, find a way to remove the blank
// space that gets created
//                }
//
//            }
//
//        }
//    }


    /**
     * GetElementById doesn't work with our XML so this method serves that
     * purpose.
     * The problem is that while our XML elements have an ID property, that
     * is not
     * recognized by the GetElementId because it's not specified in the
     * appropriate
     * way inside the document. In other words, it's not enough to have a
     * property
     * called "id" for it to be an id. TODO spiegare questa cosa nella tesi?
     * Troppo dettagliata forse.
     *
     * @param id the id of the element i'm looking for.
     * @return the Element found
     */
    Element findElemById (String id) throws XPathExpressionException {

        Node node = xpathFindNodeWithCertainAttributeValue("id", id).item(0);
        //We expect only one element to have the same id
        return (Element) node;
    }


    private Node xpathfindElemById (String id) throws XPathExpressionException {

        return (Node) xpath.evaluate("//*[@id='" + id + "']", doc,
         XPathConstants.NODE);
    }


    /**
     * ASKANA if this is a good method to recognize merges
     *
     * @param gateway the gateway that we have to evaluate
     * @return true is it has one outgoingFlow and more than one incoming flow.
     */
    boolean isAMerge (Element gateway) throws XPathExpressionException {
        //if (gateway.getTagName()) TODO check that is a gateway and return a
        // message when it's not
        //noinspection RedundantIfStatement
        if (getIncomingFlows(gateway).size() > 1 && getOutgoingFlows(gateway).size() == 1) {
            return true;
        } else {
            return false;
        }
    }


    //TODO spiega nella tesi che isAmerge e isASplit sono mutualmente
    // esclusivi ma non del tutto
    //TODO spiegalo nella prima regola che lo usa, e poi menzionalo anche
    // nelle regole che lo usano successivamente nella tesi. (as we have seen
    // in [])
    //perché potrebbe esserci un elemento che non è né uno split né un merge
    // se ha un solo incoming e un solo outgoingflow
    //anche se non sarebbe corretto secondo lo standard BPMN 2.0
    boolean isASplit (Element gateway) throws XPathExpressionException {
        //noinspection RedundantIfStatement
        if (getOutgoingFlows(gateway).size() > 1 && getIncomingFlows(gateway).size() == 1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * An 'useless' gateway is a gateway that has one incoming and one outgoing
     * flow.
     * It is illegal per BPMN 2.0
     * TODO ricordati di spiegarlo nella tesi
     *
     * @param gateway the element corresponding to the gateway we want to
     *                analyze
     * @return true if the gateway is useless (has exactly both one incoming
     * flow AND one outgoing flow), false otherwise
     */
    boolean isUselessGateway (Element gateway) throws XPathExpressionException {
        //noinspection RedundantIfStatement
        if (getOutgoingFlows(gateway).size() == 1 && getIncomingFlows(gateway).size() == 1) {
            return true;
        } else {
            return false;
        }
    }


    /*
     * We delete an useless gateway by removing its incomingFlow, then
     * setting its outgoing flow
     * to have its predecessor as a target and finally deleting the gateway.
     */
    void deleteUselesGateway (Element gateway) throws XPathExpressionException {
        //noinspection StatementWithEmptyBody
        if (! isUselessGateway(gateway)) {
            // System.out.println("This gateway is not useless and should not
            // be deleted!");
        } else {
            setSource(getOutgoingFlows(gateway).get(0).getAttribute("id"),
             getPredecessors(gateway).get(0).getAttribute("id"));
            delete(getIncomingFlows(gateway).get(0));
            delete(gateway);
        }
    }


    /**
     * Method used to find the BPMNDI element relative to an id provided by
     * the user
     *
     * @param id the id of the element that i want to find the bpmndi element of
     * @return the  BPMNDI relative to the element that has the ID provided
     */
    private Element findBPMNDI (String id) throws XPathExpressionException {
        //System.out.println("Found the BPMNDI element corresponding to " + id);
        //Element bpmndi = (Element) xpath.evaluate("//*[@bpmnElement='" + id
        // + "']", doc, XPathConstants.NODE);;
        //System.out.println("The id of the BPMNDI is " + ((Element) bpmndi)
        // .getAttribute("id"));
        //We expect only one element to have the same id
        return (Element) xpathFindNodeWithCertainAttributeValue("bpmnElement"
        , id).item(0);
    }


    /**
     * TODO vedi se funziona e elimina gli altri metodi.
     *
     * @param attribute the attribute name that I'm looking for a certain
     *                  value of
     * @param value     the value of the attribute that I'm looking for.
     * @return the NodeList of Nodes that match the search
     */
    private NodeList xpathFindNodeWithCertainAttributeValue (String attribute
    , String value) throws XPathExpressionException {

        return (NodeList) xpath.evaluate("//*[@" + attribute + "='" + value + "']", doc, XPathConstants.NODESET);
    }
    //TODO


    /**
     * this method takes a Bpmndi Element as input
     * and returns the relative dc:Bounds element
     * (which contains informations about the X and Y positions of the BPMND
     * element.
     * TODO future improvement for this method would be to create an
     * algorithm that gets the position
     * of the source element and of the target,
     * and distinguish the waypoint of the source form the waypoint of the
     * target based on the one that is less
     * distant from the original positions of the source and target elements.
     *
     * @param bpmndiElement the element I want to find the dc:Bounds of.
     * @return the dc:Bounds of the provided element
     */
    private Element findDcBounds (Element bpmndiElement) {

        @SuppressWarnings ("unused") String idofBPMNDI =
         bpmndiElement.getAttribute("id");
        System.out.println("The id of the bpmndi is " + idofBPMNDI);
        Element dcBounds =
         (Element) bpmndiElement.getElementsByTagName(this.style("Bounds")).item(0);

        if (dcBounds == null) {
            System.err.println("It means that this bpmdiElement has not any " + "dc:Bounds item. This is not expected");
        }

        return dcBounds;
    }


    /**
     * This method creates the waypoints used in the BPMNDI to position the
     * sequenceFlows
     * This method is used both to create the waypoints of the "pointy" side
     * of the arrow and of the flat side of the arrows, as they are identical.
     * The program decided the direction in which the arrow points, depending
     * on the order of the waypoints.
     *
     * @param x the x position of the Element that will use this waypoint
     * @param y the y position of the Element that will use this waypoint
     * @return the newly created waypoint Element
     */
    private Element createWaypoint (String x, String y) {

        Element waypoint = doc.createElement("di:waypoint");

        waypoint.setAttribute("x", x);

        waypoint.setAttribute("xsi:type", "dc:Point"); //this attribute never
        // changes and is always the same (?)
        waypoint.setAttribute("y", y);

        //System.out.println("		I created a waypoint with the coordinates
        // " + x + " and " + y); //UNLOCKTHIS
        return waypoint;
    }


    /**
     * This item decides in which exact position to put the end or the start
     * of the sequenceFlow
     * based on the target/source location and their height / width.
     * This way we can have the arrow nicely connect to the border of the items
     * <p>
     * Note that since the gateways are tilted squares, it's better to only
     * point the arrows in the 4 corners of items. This works best for both
     * tasks and
     * gateways.
     * <p>
     * NOTE that counter intuitively, with this system, the higher the value
     * of Y, the LOWER an item is.
     * <p>
     * TODO this can be done more elegantly, with less repetition of code,
     * and also in a way
     * to avoid problems when an item is just SLIGHTLY higher or lower than
     * the other
     * Another idea would be to create 4 candidate anchor points for both items,
     * go through each permutation (16 in total) of the 4 anchor points
     * and select the one with the lower distance. This would take less code
     * and be a more
     * generalized solution, but it would be a little bit overkill maybe.
     * NOTE at one point you will see an if inside an if:
     * since we don't want to place the arrows on the corner,
     * but on the side,only one of the following operations
     * has to be performed we decide which one based on which one
     * is the greatest distance, the horizontal o
     * or the vertical one
     *
     * @return the coordinates of the beginning and the ending points of the
     * arrow, respective to the target and the source's position.
     */
    private String[] decideArrowPosition (String sourceXPosition,
     String sourceYPosition, String sourceItemHeight, String sourceItemWidth,
      String targetXPosition, String targetYPosition, String targetItemHeight
      , String targetItemWidth) {

        //Transforming everything into ints to do the calculations
        double sourceX = Double.parseDouble(sourceXPosition);
        double sourceY = Double.parseDouble(sourceYPosition);
        double sourceHeight = Double.parseDouble(sourceItemHeight);
        double sourceWidth = Double.parseDouble(sourceItemWidth);

        double targetX = Double.parseDouble(targetXPosition);
        double targetY = Double.parseDouble(targetYPosition);
        double targetHeight = Double.parseDouble(targetItemHeight);
        double targetWidth = Double.parseDouble(targetItemWidth);

        //To be precise, X and Y are not the exact centers of the item
        //instead, they represent the upper left corner of an item.
        //We will replace them with the actual centers.
        //We deduce the position of the centers by using the width and height
        //of items:
        sourceX = sourceX + (sourceWidth / 2);
        sourceY = sourceY + (sourceHeight / 2);

        targetX = targetX + (targetWidth / 2);
        targetY = targetY + (targetHeight / 2);

        double horizontalDiff = sourceX - targetX;
        // if it's positive it means that the source it's on below the target
        // (Y coordinates work in the opposite way as they normally would in
        // a graph, for some reason)

        double verticalDiff = sourceY - targetY;
        // if it's positive it means that the source it's on the right of the
        // target

        // let's prepare the resulting Ints
        // of course the start will be connected to the source and the
        // end will be connected to the target

        double resultStartX = sourceX;
        double resultStartY = sourceY;
        double resultEndX = targetX;
        double resultEndY = targetY;

        //noinspection StatementWithEmptyBody
        if (horizontalDiff == 0 && verticalDiff == 0) {
            //This should be almost impossible
            //nonetheless, i tested it and it works
            // System.out.println("The source and target are in the same
            // position ! ");
        } else if (horizontalDiff == 0 && verticalDiff < 0) {
            //this works fine
            // System.out.println("The source and target are in the same
            // horizontal position but the target is lower");
            resultStartY = sourceY + (sourceHeight / 2);
            resultEndY = targetY - (targetHeight / 2);
        } else if (horizontalDiff == 0 && verticalDiff > 0) {
            //this works as intended
            // System.out.println("The source and target are in the same
            // horizontal position but the target is higher");
            resultStartY = sourceY - (sourceHeight / 2);
            resultEndY = targetY + (targetHeight / 2);
        } else if (horizontalDiff > 0 && verticalDiff == 0) {
            //this works as intended
            //System.out.println("The source and target are at the same
            // height but the target is left of the source");
            resultStartX = sourceX - (sourceWidth / 2);
            resultEndX = targetX + (targetWidth / 2);
        } else if (horizontalDiff < 0 && verticalDiff == 0) {
            //this works as intended
            // System.out.println("The source and target are at the same
            // height but the target is right of the source");
            resultStartX = sourceX + (sourceWidth / 2);
            resultEndX = targetX - (targetWidth / 2);
        } else if (horizontalDiff < 0 && verticalDiff < 0) {
            //this works as intended

            // System.out.println("The target is on the lower right of the
            // source");

            if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {
                resultStartY = sourceY + (sourceHeight / 2);
                resultEndY = targetY - (targetHeight / 2);
            } else {
                resultStartX = sourceX + (sourceWidth / 2);
                resultEndX = targetX - (targetWidth / 2);
            }
        } else if (horizontalDiff < 0 && verticalDiff > 0) {
            //this works as intended

            //System.out.println("The target is on the upper right of the
            // source");

            if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {

                resultStartY = sourceY - (sourceHeight / 2);
                resultEndY = targetY + (targetHeight / 2);
            } else {
                resultStartX = sourceX + (sourceWidth / 2);
                resultEndX = targetX - (targetWidth / 2);
            }
        } else if (horizontalDiff > 0 && verticalDiff < 0) {
            //this works as intended
            //System.out.println("The target is on the lower left of the
            // source");
            if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {
                resultStartY = sourceY + (sourceHeight / 2);
                resultEndY = targetY - (targetHeight / 2);
            } else {
                resultStartX = sourceX - (sourceWidth / 2);
                resultEndX = targetX + (targetWidth / 2);
            }
        } else if (horizontalDiff > 0 && verticalDiff > 0) {
            //this works as intended

            //System.out.println("The target is on the upper left of the
            // source");

            if (Math.abs(horizontalDiff) < Math.abs(verticalDiff)) {
                resultStartY = sourceY - (sourceHeight / 2);
                resultEndY = targetY + (targetHeight / 2);
            } else {
                resultStartX = sourceX - (sourceWidth / 2);
                resultEndX = targetX + (targetWidth / 2);
            }
        }

        //finally
        //let's transform back our Ints into string, ready to be put into an XML
        //TODO there's probably a better way to return this info, maybe as an
        // inner object?
        //TODO > return an arrayList of <Coordinates> (will be of size 2)
        String[] positions = new String[4];
        positions[0] = "" + resultStartX;
        positions[1] = "" + resultStartY;
        positions[2] = "" + resultEndX;
        positions[3] = "" + resultEndY;

        return positions;
    }

    /**
     * this method changes a tag to conform to the correct style of the document
     * for now this uses camunda or signavio styles.
     *
     * @param tagName the tagName that has to be 'translated'
     * @return the translated String
     */
    String style (String tagName) {

        //BPMNDI elements have the same tagnames in both camunda and signavio
        // so no need to change anything
        if (tagName.contains("bpmndi")) {
            return tagName;
        } else if (tagName.equals("Bounds")) {
            if (tagStyle.equals("camunda")) {
                return "dc:" + tagName;
            }
            if (tagStyle.equals("signavio")) {
                return "omgdc:" + tagName;
            }
        } else if (tagName.equals("waypoint")) {
            if (tagStyle.equals("camunda")) {
                return "di:" + tagName;
            }
            if (tagStyle.equals("signavio")) {
                return "omgdi:" + tagName;
            }
        } else {
            switch (tagStyle) {
                case "camunda":
                    return "bpmn:" + tagName;

                case "signavio":
                    return tagName;
                default:
                    //this is impossible but let's use signavio style
                    return tagName;
            }
        }
        return tagName;
    }


    /**
     * TODO explain in the thesis that a QName (what is it?) cannot start
     * with a number but a letter.
     *
     * @return the newly created ID. All new ID start with "USI" as new IDs
     * cannot start with a digit
     */
    private String newId () {

        return "USI" + UUID.randomUUID().toString();
    }


    //TODO "extension" should be a field?
    //TODO add description
    String saveToFile (String outputPath, String extension) throws TransformerException {
        //Building the name:
        String path = outputPath + name + extension;
        // Saving the file

        TransformerFactory transformerFactory =
         TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);
        //System.out.println("Saved the XML file in + " + path);

        return path;
    }


}