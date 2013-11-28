package backend.procedure;

import frontend.Token;
import frontend.TokenType;
import intermediate.Node;

import java.util.ArrayList;

import backend.Procedure;

public class Pair implements Procedure
{
    public ArrayList<Object> run(ArrayList<Node> parameters) {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        Node temp = parameters.get(0);
        Node booleanNode = new Node();
        Token token = temp.getToken();

        if (token != null && token.getType() == TokenType.SS_QUOTE) {
            temp = temp.getCdr().getCar(); // Go inside list
        }
        else {
            temp = temp.getCar(); // Go inside list
        }

        if (temp.getToken() != null || temp.getCar() != null || temp.getCdr() != null) {
            booleanNode.setToken(new Token(TokenType.TRUE));
            booleanNode.getToken().setText("#t");
        }
        else {
            booleanNode.setToken(new Token(TokenType.FALSE));
            booleanNode.getToken().setText("#f");
        }

        returnObject.add(booleanNode);
        return returnObject;
    }
}
