package test.my.utils.serialize;

public enum Level {
	
	HIGH(10),
	
	LOW(1);
	
	private final int value;
	
	private Level(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
