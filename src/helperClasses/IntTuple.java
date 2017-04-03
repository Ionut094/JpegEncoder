package helperClasses;

public class IntTuple {
	private final int x;
	private final int y;
	
	public static IntTuple tupleOf(int x,int y){
		return new IntTuple(x, y);
	}
	
	private IntTuple(int x,int y){
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
}
