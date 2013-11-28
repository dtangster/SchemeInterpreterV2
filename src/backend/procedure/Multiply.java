package backend.procedure;

import backend.Procedure;
import frontend.TokenType;
import intermediate.Node;

import java.util.ArrayList;

public class Multiply implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        double product = 1;

        for (Node node : parameters) {
            if (node.getToken().getType() == TokenType.SS_QUOTE) {
                node = node.getCdr();
            }

            product *= ((Number) node.getToken().getValue()).doubleValue();
        }

        returnObject.add(product);
        return returnObject;
    }
}