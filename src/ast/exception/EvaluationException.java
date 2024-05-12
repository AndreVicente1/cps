package ast.exception;

/**
 * Represents an exception that occurs during evaluation in the AST
 * This class exception is only used by the Interpreter
 */
public class EvaluationException extends Exception{
    private static final long serialVersionUID = 1L;

	public EvaluationException(String msg){
        super(msg);
    }
}