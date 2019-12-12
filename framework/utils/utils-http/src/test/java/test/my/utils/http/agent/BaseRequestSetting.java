package test.my.utils.http.agent;

import com.jd.blockchain.utils.http.RequestParam;

public abstract class BaseRequestSetting {

	@RequestParam(name = "id")
	private String id;
	
	@RequestParam(name = "type")
	public final OpType type = OpType.TYPE2;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public abstract int getValue();

	/**
	 * 只读属性；
	 * 
	 * @return
	 */
	public OpType getType() {
		return type;
	}
}
