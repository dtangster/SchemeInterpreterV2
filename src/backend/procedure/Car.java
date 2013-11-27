package backend.procedure;

import backend.Procedure;
import intermediate.Node;

import java.util.ArrayList;

public class Car implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        Node newNode = parameters.get(0).getCar().clone();

        if (newNode.getCar() == null) {
            newNode.setCdr(null);
        }
        else if (newNode.getToken() == null) {
            newNode.setCdr(null);
        }

        returnObject.add(newNode);
        return returnObject;
    }
}
