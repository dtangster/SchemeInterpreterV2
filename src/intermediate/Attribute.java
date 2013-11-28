package intermediate;

public enum Attribute {
    QUOTE_NODE, // Root node of the quoted node
    LAMBDA_NODE, // Root node of the lambda tree
    VARIABLE_NODE, // Root node of a variable that has been defined before
    NUMBER_CONSTANT,
    BUILTIN_PROCEDURE,
}
