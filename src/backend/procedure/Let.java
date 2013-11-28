package backend.procedure;

import backend.Executor;
import frontend.Token;
import intermediate.Attribute;
import intermediate.Node;

import java.util.ArrayList;

import backend.Procedure;
import intermediate.SymTabEntry;

public class Let implements Procedure
{
    public Node run(ArrayList<Node> parameters) {
        Node bindNodes = parameters.get(0).getCar().clone();
        Node executionNode = parameters.get(0).getCdr().clone();

        do {
            Node temp = bindNodes.getCar().clone();
            String variableId = temp.getToken().getText();
            SymTabEntry entry = Executor.runTimeStack.enterLocal(variableId);
            temp = temp.getCdr().clone();
            Token token = temp.getToken();

            if (token == null) {
                ArrayList<Node> results = Executor.execute(temp.getCar());
                temp = results.get(0);
                token = temp.getToken();
            }

            if (token != null) {
                switch (token.getType()) {
                    case SS_QUOTE:
                        entry.put(Attribute.QUOTE_NODE, temp);
                        break;
                    case IDENTIFIER:
                    case SYMBOL:
                        SymTabEntry nodeType = Executor.getVariableType(temp);

                        //TODO: Assuming the identifier refers to a number constant for now
                        Number constant = (Number) nodeType.get(Attribute.NUMBER_CONSTANT);
                        entry.put(Attribute.NUMBER_CONSTANT, constant);
                        break;
                    case NUMBER:
                        entry.put(Attribute.NUMBER_CONSTANT, temp.getToken().getValue());
                }
            }

        } while ((bindNodes = bindNodes.getCdr()) != null);

        return executionNode;
    }
}