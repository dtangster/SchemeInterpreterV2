package backend.procedure;

import frontend.TokenType;
import intermediate.Node;
import intermediate.SymTabStack;

import java.util.ArrayList;
import java.util.HashMap;

import backend.Procedure;

public class Let implements Procedure
{
    private SymTabStack runTimeStack;
    public ArrayList<Object> run(ArrayList<Node> parameters)
    {
        this.runTimeStack = runTimeStack;
        ArrayList<Object> returnObject = new ArrayList<Object>();
        HashMap<String,Object> variables = new HashMap<String,Object>();

        Node currentNode = parameters.get(0);
        Node car = currentNode.getCar();
        while(currentNode != null)
        {
            variables.put(car.getToken().getText(), getValue(car.getCdr().getCar()));

            currentNode = currentNode.getCdr();
        }

        currentNode = parameters.get(1);

        returnObject.add(variables.get(currentNode.getToken().getText()));
        return returnObject;
    }

    public Object getValue(Node root)
    {
        Object result = null;
        ArrayList<Node> parameters = extractParameters(root);
        switch(root.getToken().getType())
        {
            case KW_PLUS:
                Procedure add = new Add();
                result = add.run(parameters);
        }
        return result;
    }

    public ArrayList<Node> extractParameters(Node node) {
        ArrayList<Node> parameters = new ArrayList<Node>();
        boolean isLet = false;
        if(node.getToken().getType() == TokenType.KW_LET)
            isLet = true;

        while ((node = node.getCdr()) != null) {
            // If it gets into this block, it means that it is a list, so execute it and get the results
            if (node.getToken() == null)
            {
                if(isLet)
                {
                    if(node.getCar() == null)
                        parameters.add(node);
                    else
                        parameters.add(node.getCar());
                }
            }
            else if (node.getToken().getType() == TokenType.SS_QUOTE) {
                node = node.getCdr();
                parameters.add(node);
            }
            else {
                parameters.add(node);
            }
        }

        return parameters;
    }
}
