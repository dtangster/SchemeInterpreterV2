package backend.procedure;

import backend.Procedure;
import intermediate.Node;

import java.util.ArrayList;

public class Multiply implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        double product = 1;

        for (Node node : parameters) {
            product *= ((Number) node.getToken().getValue()).doubleValue();
        }

        returnObject.add(product);
        return returnObject;
    }
}