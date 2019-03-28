package test.my.utils.serialize;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class BinarySerializeUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testSerializePrimitive() {
		int v = 5;
		byte[] bytes = BinarySerializeUtils.serialize(v);
		Object desV = BinarySerializeUtils.deserialize(bytes);
		
		int v2 = (int) desV;
		
		assertEquals(v, v2);
	}
	
	@Test
	public void testSerializeMultiValues() {
		String str = "kkkjijfekwejwe";
		int v = 5;
		Data data = new Data();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinarySerializeUtils.serialize(str, out);
		BinarySerializeUtils.serialize(v, out);
		BinarySerializeUtils.serialize(data, out);
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		
		String deStr = BinarySerializeUtils.deserialize(in);
		int deV = BinarySerializeUtils.deserialize(in);
		Data deData = BinarySerializeUtils.deserialize(in);
		
		assertEquals(str, deStr);
		assertEquals(v, deV);
		assertEquals(data.getId(), deData.getId());
		assertEquals(data.getName(), deData.getName());
		assertEquals(data.isMale(), deData.isMale());
	}

	
	public static class Data implements Serializable{

		private static final long serialVersionUID = -3168475060300920369L;
		
		private int id = 12;
		private boolean male = false;
		
		private String name = "John";

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public boolean isMale() {
			return male;
		}

		public void setMale(boolean male) {
			this.male = male;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
		
	}
}
