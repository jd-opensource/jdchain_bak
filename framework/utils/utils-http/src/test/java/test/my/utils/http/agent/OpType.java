package test.my.utils.http.agent;

/**
 * 用于验证将枚举类型的 POJO 字段映射为请求参数时，默认情况下通过 toString 方法取得参数值；
 * 
 * @author haiq
 *
 */
public enum OpType {

	TYPE1(1),

	TYPE2(2);

	private int value;

	private OpType(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
}
