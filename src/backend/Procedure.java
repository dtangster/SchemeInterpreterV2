package backend;

import intermediate.Node;

import java.util.ArrayList;

public interface Procedure {
    public ArrayList<Object> run(ArrayList<Node> parameters);
}
