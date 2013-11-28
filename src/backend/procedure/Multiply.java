package backend.procedure;

import backend.Procedure;
import frontend.Token;
import frontend.TokenType;
import intermediate.Node;

import java.util.ArrayList;

public class Multiply implements Procedure {
    public Node run(ArrayList<Node> parameters) {
        double product = 1;

        for (Node node : parameters) {
            if (node.getToken().getType() == TokenType.SS_QUOTE) {
                node = node.getCdr();
            }

            product *= ((Number) node.getToken().getValue()).doubleValue();
        }

        Node node = new Node();
        node.setToken(new Token(TokenType.NUMBER));
        node.getToken().setText(Double.toString(product));
        node.getToken().setValue(product);

        return node;
    }
}