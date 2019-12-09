package test.com.jd.blockchain.ledger;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.resolver.StringToBytesValueResolver;
import com.jd.blockchain.utils.Bytes;
import static org.junit.Assert.*;

import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

public class StringToBytesValueResolverTest {

    private StringToBytesValueResolver resolver = new StringToBytesValueResolver();

    @Test
    public void testText() {
        String textVal = "JDChain";

        BytesValue textBytesValue = resolver.encode(textVal);

        assertEquals(Bytes.fromString(textVal), textBytesValue.getBytes());

        assertEquals(textBytesValue.getType(), DataType.TEXT);

        String resolveText = (String)resolver.decode(textBytesValue);

        assertEquals(resolveText, textVal);

        byte[] resolveBytes = (byte[]) resolver.decode(textBytesValue, byte[].class);

        assertArrayEquals(resolveBytes, BytesUtils.toBytes(textVal));

        Bytes resolveBytesObj = (Bytes) resolver.decode(textBytesValue, Bytes.class);

        assertEquals(resolveBytesObj, Bytes.fromString(textVal));

    }

    @Test
    public void testJson() {
        Person person = new Person("zhangsan", 80);
        String personJson = JSON.toJSONString(person);
        BytesValue textBytesValue = resolver.encode(personJson);
        assertEquals(Bytes.fromString(personJson), textBytesValue.getBytes());
        assertEquals(textBytesValue.getType(), DataType.JSON);
    }

    public static class Person {
        private String name;

        private int age;

        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
