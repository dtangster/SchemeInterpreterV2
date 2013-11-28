package backend.procedure;

import backend.Executor;
import backend.Procedure;
import frontend.Token;
import frontend.TokenType;
import intermediate.Attribute;
import intermediate.Node;
import intermediate.SymTabEntry;

import java.util.ArrayList;

public class Multiply implements Procedure {
    public Node run(ArrayList<Node> parameters) {
        double product = 1;

        for (Node node : parameters) {
            switch (node.getToken().getType()) {
                case SS_QUOTE:
                    node = node.getCdr();
                    break;
                case IDENTIFIER:
                case SYMBOL:
                    SymTabEntry nodeType = Executor.getVariableType(node);

                    //TODO: Assuming the identifier refers to a number constant for now
                    Number constant = (Number) nodeType.get(Attribute.NUMBER_CONSTANT);
                    node = node.clone();
                    node.setToken(new Token(TokenType.NUMBER));
                    node.getToken().setValue(constant);
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