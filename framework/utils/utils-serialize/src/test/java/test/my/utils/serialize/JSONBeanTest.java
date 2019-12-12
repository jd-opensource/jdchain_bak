package test.my.utils.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.jd.blockchain.utils.serialize.json.JSONBean;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

public class JSONBeanTest {

	@Test
	public void testGet() {
		CompositData compositeData = new CompositData();
		compositeData.setTelNo("18800101234");
		compositeData.setTime(new Date());

		Score[] scores = new Score[2];
		scores[0] = new Score();
		scores[0].setId("A");
		scores[0].setValue(10);
		scores[1] = new Score();
		scores[1].setId("B");
		scores[1].setValue(20);

		ExtData extdata = new ExtData();
		extdata.setCode("ext-00012");
		extdata.setAmount(1000);
		extdata.setAddress("mvsekkjfdsafie932kjkasfdas");
		extdata.setEnable(true);
		extdata.setCompositeData(compositeData);
		extdata.setScores(scores);

		JSONBean jsonBean = JSONBean.wrap(extdata);

		String telNo = jsonBean.getString("compositeData.telNo");
		assertEquals(compositeData.getTelNo(), telNo);

		Date time = jsonBean.getDate("compositeData.time");
		assertEquals(compositeData.getTime(), time);

		int score2 = jsonBean.getIntValue("scores[1].value");
		assertEquals(scores[1].getValue(), score2);
	}

	@Test
	public void testMergeJSONBeanArrayMergeStrategy() {
		// 准备初始数据；
		CompositData compositeData = new CompositData();
		compositeData.setTelNo("18800101234");
		compositeData.setTime(new Date());

		Score[] scores = new Score[2];
		scores[0] = new Score();
		scores[0].setId("A");
		scores[0].setValue(10);
		scores[1] = new Score();
		scores[1].setId("B");
		scores[1].setValue(20);

		ExtData extdata = new ExtData();
		extdata.setCode("ext-00012");
		extdata.setAmount(1000);
		extdata.setAddress("mvsekkjfdsafie932kjkasfdas");
		extdata.setEnable(true);
		extdata.setCompositeData(compositeData);
		extdata.setScores(scores);

		JSONBean jsonBean = JSONBean.wrap(extdata);
		
		String telNo = jsonBean.getString("compositeData.telNo");
		assertEquals(compositeData.getTelNo(), telNo);
		
		int score2 = jsonBean.getIntValue("scores[1].value");
		assertEquals(scores[1].getValue(), score2);

		// 准备合并数据；
		Score[] scores2 = new Score[3];
		scores2[0] = new Score();
		scores2[0].setId("c");
		scores2[0].setValue(28);
		scores2[1] = new Score();
		scores2[1].setId("d");
		scores2[1].setValue(50);
		scores2[2] = new Score();
		scores2[2].setId("e");
		scores2[2].setValue(66);

		SubData subdata2 = new SubData();
		subdata2.setCode("sub-00018");
		subdata2.setAmount(180);
		subdata2.setScores(scores2);

		JSONBean jsonBean2 = JSONBean.wrap(subdata2);
		
		jsonBean.merge(jsonBean2);
		String code = jsonBean.getString("code");
		assertEquals(subdata2.getCode(), code);
		telNo = jsonBean.getString("compositeData.telNo");
		assertEquals(compositeData.getTelNo(), telNo);
		
		score2 = jsonBean.getIntValue("scores[1].value");
		assertEquals(scores2[1].getValue(), score2);
		
		int score3 = jsonBean.getIntValue("scores[2].value");
		assertEquals(scores2[2].getValue(), score3);
	}
	
	@Test
	public void testSerializeAndDeserialize1(){
		CompositData compositeData = new CompositData();
		compositeData.setTelNo("18800101234");
		compositeData.setTime(new Date());
		
		Score[] scores = new Score[2];
		scores[0] = new Score();
		scores[0].setId("A");
		scores[0].setValue(10);
		scores[1] = new Score();
		scores[1].setId("B");
		scores[1].setValue(20);
		
		ExtData extdata = new ExtData();
		extdata.setCode("ext-00012");
		extdata.setAmount(1000);
		extdata.setAddress("mvsekkjfdsafie932kjkasfdas");
		extdata.setEnable(true);
		extdata.setCompositeData(compositeData);
		extdata.setScores(scores);
		
		JSONBean jsonBean = JSONBean.wrap(extdata);
		
		String json = JSONSerializeUtils.serializeToJSON(jsonBean);
		System.out.println("--------- serialize ----------");
		System.out.println(json);
		
		JSONBean jsonBean2 = JSONSerializeUtils.deserializeFromJSON(json, JSONBean.class);
		System.out.println("--------- deserialize ----------");
		assertNotNull(jsonBean2);
		assertEquals(extdata.getCode(), jsonBean2.getString("code"));
		assertEquals(extdata.getAmount(), jsonBean2.getIntValue("amount"));
		assertEquals(extdata.isEnable(), jsonBean2.getBooleanValue("enable"));
	}
	@Test
	public void testSerializeAndDeserialize2(){
		CompositData compositeData = new CompositData();
		compositeData.setTelNo("18800101234");
		compositeData.setTime(new Date());

		Score[] scores = new Score[2];
		scores[0] = new Score();
		scores[0].setId("A");
		scores[0].setValue(10);
		scores[1] = new Score();
		scores[1].setId("B");
		scores[1].setValue(20);

		ExtData extdata = new ExtData();
		extdata.setCode("ext-00012");
		extdata.setAmount(1000);
		extdata.setAddress("mvsekkjfdsafie932kjkasfdas");
		extdata.setEnable(true);
		extdata.setCompositeData(compositeData);
		extdata.setScores(scores);

		JSONBean jsonBean = JSONBean.wrap(extdata);
		
		TopMetadata meta = new TopMetadata();
		meta.setId(UUID.randomUUID().toString());
		meta.setHeader(jsonBean);
		
		String json = JSONSerializeUtils.serializeToJSON(meta);
		System.out.println("--------- serialize ----------");
		System.out.println(json);
		
		TopMetadata meta2 = JSONSerializeUtils.deserializeFromJSON(json, TopMetadata.class);
		System.out.println("--------- deserialize ----------");
		System.out.println("id="+ meta2.getId());
		assertNotNull(meta2.getHeader());
		assertEquals(extdata.getCode(), meta2.getHeader().getString("code"));
		assertEquals(extdata.getAmount(), meta2.getHeader().getIntValue("amount"));
		assertEquals(extdata.isEnable(), meta2.getHeader().getBooleanValue("enable"));
	}
	
	public static class TopMetadata{
		
		private String id;
		
		private JSONBean header;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public JSONBean getHeader() {
			return header;
		}

		public void setHeader(JSONBean header) {
			this.header = header;
		}
		
	}
	
	public static class SubData {

		private String code;

		private int amount;

		private Score[] scores;

		public Score[] getScores() {
			return scores;
		}

		public void setScores(Score[] scores) {
			this.scores = scores;
		}

		public String getCode() {
			return code;
		}

		public int getAmount() {
			return amount;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

	}

	public static class Score {
		private String id;

		private int value;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	}

	public static class ExtData extends SubData {

		private String address;

		private boolean enable;

		private CompositData compositeData;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public CompositData getCompositeData() {
			return compositeData;
		}

		public void setCompositeData(CompositData compositeData) {
			this.compositeData = compositeData;
		}
	}

	public static class CompositData {

		private String telNo;

		private Date time;

		public String getTelNo() {
			return telNo;
		}

		public void setTelNo(String telNo) {
			this.telNo = telNo;
		}

		public Date getTime() {
			return time;
		}

		public void setTime(Date time) {
			this.time = time;
		}

	}

}
