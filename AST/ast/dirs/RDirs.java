package ast.dirs;

public class RDirs extends Dirs{
	private String dir; //NE || NW || SE || SW
	private Dirs dirs;
	
	public RDirs(String dir, Dirs dirs) {
		this.dir = dir;
		this.dirs = dirs;
	}
	
	public String getDir() {
		return dir;
	}
	
	public Dirs getDirs() {
		return dirs;
	}
	
}
