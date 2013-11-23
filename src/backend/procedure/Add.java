package backend.procedure;

import backend.Procedure;
import intermediate.Node;

import java.util.ArrayList;

public class Add implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        double sum = 0;

        for (Object node : parameters) {
            sum += ((Node) node).getToken().getValue().doubleValue();
        }

        returnObject.add(sum);
        return returnObject;
    }
}