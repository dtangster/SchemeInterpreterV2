package backend.procedure;

import backend.Procedure;
import intermediate.Node;

import java.util.ArrayList;

public class Cdr implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        Node newNode = parameters.get(0).getCdr().clone();

        returnObject.add(newNode);
        return returnObject;
    }
}
