package backend.procedure;

import backend.Procedure;
import intermediate.Node;

import java.util.ArrayList;

public class Car implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        Node newNode = parameters.get(0).getCar().clone();

        if (newNode.getToken() == null) {
            newNode = newNode.getCar().clone();
            newNode.setPrintTree(true);
        }

        returnObject.add(newNode);
        return returnObject;
    }
}
