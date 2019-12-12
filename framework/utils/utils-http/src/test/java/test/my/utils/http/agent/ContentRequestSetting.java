package test.my.utils.http.agent;

import com.jd.blockchain.utils.http.RequestParam;

/**
 * ContentRequestSetting 用于测试以 POJO 方式定义请求参数；
 * 
 * 并且通过继承 BaseRequestSetting 用于测试通过继承 POJO 的方式继承请求参数的定义；
 * 
 * @author haiq
 *
 */
public class ContentRequestSetting extends BaseRequestSetting {

	@RequestParam(name = "permited")
	private boolean permited;
	@RequestParam(name = "male", converter = CustomBooleanConverter.class)
	private boolean male;
	

	@RequestParam(name = "value", required = false, ignoreValue = "-1")
	private int value;

	public boolean isPermited() {
		return permited;
	}

	public void setPermited(boolean permited) {
		this.permited = permited;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	@Override
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}
