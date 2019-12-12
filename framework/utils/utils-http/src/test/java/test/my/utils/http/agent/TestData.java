package test.my.utils.http.agent;

import java.io.Serializable;

public class TestData implements Serializable {

	private static final long serialVersionUID = -4339820200060895807L;

	private int id;
	
	private String value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
