package ast.base;

public class ABase extends Base {
	private String position;
	
	public ABase(String pos) {
		position = pos;
	}
	
	public String getPos() {
		return position;
	}
}
