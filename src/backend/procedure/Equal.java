package backend.procedure;

import frontend.TokenType;
import intermediate.Node;

import java.util.ArrayList;

import backend.Procedure;

public class Equal implements Procedure
{
    public ArrayList<Object> run(ArrayList<Node> parameters)
    {
        ArrayList<Object> returnObject = new ArrayList<Object>();
        /*
        //Node a = parameters.get(0), b = parameters.get(1);
        Number a, b;
        //if(parameters.get(0).getToken().getType() == TokenType.NUMBER)
        
        if(a instanceof Number && b instanceof Number)
        {
        	if(((Number) a).floatValue() == ((Number) b).floatValue())
        		returnObject.add(true);
        	else
        		returnObject.add(false);
        }*/

        Number a = (Number) parameters.get(0).getToken().getValue(),
                b = (Number) parameters.get(1).getToken().getValue();

        if(((Number) a).floatValue() == ((Number) b).floatValue())
            returnObject.add(true);
        else
            returnObject.add(false);
        return returnObject;
    }

}
