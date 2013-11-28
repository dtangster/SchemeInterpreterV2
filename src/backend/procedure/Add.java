package backend.procedure;

import backend.Procedure;
import frontend.TokenType;
import intermediate.Node;

import java.util.ArrayList;

public class Add implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters)
    {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        double sum = 0;

        for (Node node : parameters) {
            if (node.getToken().getType() == TokenType.SS_QUOTE) {
                node = node.getCdr();
            }

            sum += ((Number) node.getToken().getValue()).doubleValue();
        }

        returnObject.add(sum);
        return returnObject;
    }
}
