package backend.procedure;

import backend.Procedure;
import intermediate.Node;

import java.util.ArrayList;

public class Cdr implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        Node newNode = parameters.get(0).getCar().clone();

        if (newNode.getCdr() != null) {
            newNode = newNode.getCdr().clone();
        }
        else if (newNode.getToken() == null) {
            newNode = newNode.getCar().clone();
        }
        else if (newNode.getCar() == null && newNode.getCdr() == null) {
            newNode.setToken(null);
        }

        newNode.setPrintTree(true);
        returnObject.add(newNode);
        return returnObject;
    }
}
