package test.my.utils.serialize;

import java.lang.reflect.Type;

public class Entity<T> {
	private String name;

	private int value;
	
	private Level level;

	private String data;

	private T header;

	private transient Type type;

	public Entity() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public T getHeader() {
		return header;
	}

	public void setHeader(T header) {
		this.header = header;
	}

	public Type getType() {
		return type;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

}