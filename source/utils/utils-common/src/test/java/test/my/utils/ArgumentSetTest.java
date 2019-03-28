package test.my.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jd.blockchain.utils.ArgumentSet;

public class ArgumentSetTest {

	@Test
	public void test() {
		String[] args = { "-n", "my", "-o", "./keys" };
		ArgumentSet argSet = ArgumentSet.resolve(args, ArgumentSet.setting().prefix("-n", "-o"));
		
		assertNotNull(argSet.getArg("-n"));
		assertEquals("my",argSet.getArg("-n").getValue());
		assertNotNull(argSet.getArg("-o"));
		assertEquals("./keys",argSet.getArg("-o").getValue());
		
	}

}
