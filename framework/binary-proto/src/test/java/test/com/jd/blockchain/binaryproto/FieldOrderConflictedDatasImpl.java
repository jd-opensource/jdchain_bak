package test.com.jd.blockchain.binaryproto;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
public class FieldOrderConflictedDatasImpl implements FieldOrderConflictedDatas {

	private boolean enable;

	private byte boy;
	private short age;
	private int id;
	private String name;

	private long value;

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public short getAge() {
		return age;
	}

	public void setAge(short age) {
		this.age = age;
	}

	@Override
	public byte isBoy() {
		return this.boy;
	}

	public void setBoy(byte boy) {
		this.boy = boy;
	}

	@Override
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
