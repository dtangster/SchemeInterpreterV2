package backend.procedure;

import backend.Procedure;
import frontend.Token;
import frontend.TokenType;
import intermediate.Node;

import java.util.ArrayList;

public class Car implements Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        Token token = parameters.get(0).getToken();
        Node quoteNode = null;
        Node newNode = null;

        if (token != null && token.getType() == TokenType.SS_QUOTE) {
            quoteNode = parameters.get(0).clone();
            newNode = parameters.get(0).getCdr().getCar().clone();
            quoteNode.setCdr(newNode);
        }
        else {
            newNode = parameters.get(0).getCar().clone();
        }

        if (newNode.getCar() == null) {
            newNode.setCdr(null);
        }
        else if (newNode.getToken() == null) {
            newNode.setCdr(null);
        }

        if (quoteNode != null) {
            returnObject.add(quoteNode);
        }
        else {
            returnObject.add(newNode);
        }

        return returnObject;
    }
}
