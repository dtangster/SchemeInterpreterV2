package backend.procedure;

import backend.Procedure;
import frontend.Token;
import frontend.TokenType;
import intermediate.Node;

import java.util.ArrayList;

public class Cdr implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        Token token = parameters.get(0).getToken();
        Node quoteNode = null;
        Node newNode = null;

        if (token != null && token.getType() == TokenType.SS_QUOTE) {
            quoteNode = parameters.get(0).clone();

            if (quoteNode.getCdr().getCar().getCdr() != null) {
                newNode = quoteNode.getCdr().getCar().getCdr().clone();
            }
            else {
                newNode = quoteNode.getCdr().getCar().clone();
            }

            quoteNode.getCdr().setCar(newNode);
            newNode = quoteNode;
        }
        else {
            newNode = parameters.get(0).getCar().getCdr().clone();
        }

        returnObject.add(newNode);
        return returnObject;
    }
}
