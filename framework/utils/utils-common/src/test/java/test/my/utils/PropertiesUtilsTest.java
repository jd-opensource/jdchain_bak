package test.my.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;

import com.jd.blockchain.utils.PropertiesUtils;

public class PropertiesUtilsTest {

	public static class TestData {

		private String name;

		private int id;

		private boolean enable;

		private long life;

		private HomeAddress homeAddress;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public long getLife() {
			return life;
		}

		public void setLife(long life) {
			this.life = life;
		}

		public HomeAddress getHomeAddress() {
			return homeAddress;
		}

		public void setHomeAddress(HomeAddress homeAddress) {
			this.homeAddress = homeAddress;
		}

	}

	public static class HomeAddress {

		private int number;

		private String street;

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}
	}

	@Test
	public void test() {
		Properties props = new Properties();
		props.setProperty("name", "john");
		props.setProperty("id", "10");
		props.setProperty("enable", "true");
		props.setProperty("life", "120088835993666666");
		props.setProperty("remark", "dfdafew");
		props.setProperty("homeAddress.number", "18");
		props.setProperty("homeAddress.street", "newyork");

		TestData data = PropertiesUtils.createInstance(TestData.class, props);
		assertNotNull(data);
		assertEquals("john", data.getName());
		assertEquals(10, data.getId());
		assertEquals(true, data.isEnable());
		assertEquals(120088835993666666L, data.getLife());
		assertNotNull(data.getHomeAddress());
		assertEquals(18, data.getHomeAddress().getNumber());
		assertEquals("newyork", data.getHomeAddress().getStreet());

		Properties props1 = new Properties();
		props1.setProperty("abc.name", "john");
		props1.setProperty("abc.id", "10");
		props1.setProperty("abc.enable", "true");
		props1.setProperty("abc.life", "120088835993666666");
		props1.setProperty("remark", "dfdafew");
		props1.setProperty("abc.homeAddress.number", "18");

		TestData data1 = PropertiesUtils.createInstance(TestData.class, props1, "abc.");
		assertNotNull(data1);
		assertEquals("john", data1.getName());
		assertEquals(10, data1.getId());
		assertEquals(true, data1.isEnable());
		assertEquals(120088835993666666L, data1.getLife());
		assertNotNull(data1.getHomeAddress());
		assertEquals(18, data1.getHomeAddress().getNumber());
		assertNull(data1.getHomeAddress().getStreet());
	}

}
