package intermediate;

import backend.Procedure;
import backend.procedure.*;

import java.util.HashMap;

//TODO: Add more procedure classes. These are linked in the SymTabStack constructor.
public class Predefined {
    public static HashMap<String, Procedure> procedureMap = new HashMap<String, Procedure>();

    static {
        procedureMap.put("+", new Add());
        procedureMap.put("*", new Multiply());
        procedureMap.put("car", new Car());
        procedureMap.put("cdr", new Cdr());
        procedureMap.put("equal?", new Equal());
        procedureMap.put("null?", new Null());
        procedureMap.put("let", new Let());
        procedureMap.put("pair?", new Pair());
    }
}
