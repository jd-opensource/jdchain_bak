package test.com.jd.blockchain.ledger;

public class KeyValueObject implements KeyValueEntry {

	private String key;

	private String value;

	private long version;

	public KeyValueObject() {
	}

	public KeyValueObject(String key, String value, long version) {
		this.key = key;
		this.value = value;
		this.version = version;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public long getVersion() {
		return version;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
