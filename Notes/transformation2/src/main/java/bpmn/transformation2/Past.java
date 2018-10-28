package bpmn.transformation2;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class Past {

    ArrayList<Element> elements;
    int id;
    
    
    public Past() {
	this.elements = new ArrayList<Element>();
	this.id = 0;
    }
    
    public Past(ArrayList<Element> previous, int id) {
	this.elements = previous;
	this.id = id;
    }
}
