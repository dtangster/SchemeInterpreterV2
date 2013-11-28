package backend.procedure;

import frontend.Token;
import intermediate.Node;

import java.util.ArrayList;

import backend.Procedure;

public class Null implements Procedure
{
    private boolean isBeginning = true;

    public ArrayList<Object> run(ArrayList<Node> parameters)
    {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        ArrayList list = this.getList(parameters.get(0));

        if(list.isEmpty())
            returnObject.add(true);
        else
            returnObject.add(false);

        return returnObject;
    }

    public ArrayList getList(Node root)
    {
        Node currentNode = root;
        Node car = currentNode.getCar();
        ArrayList<Object> list = new ArrayList<Object>();

        while(currentNode != null)
        {
            if(isBeginning)
            {
                isBeginning = false;
                if(car != null)
                {
                    currentNode = car;
                    car = currentNode.getCar();
                }
            }
            if(car != null)
            {
                list.add(getList(car));
            }
            else
            {
                Token token = currentNode.getToken();
                if(token != null)
                    list.add(token.getValue());
            }

            currentNode = currentNode.getCdr();
        }
        return list;
    }
}
