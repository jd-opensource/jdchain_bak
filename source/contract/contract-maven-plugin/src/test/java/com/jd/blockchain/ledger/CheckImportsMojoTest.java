package com.jd.blockchain.ledger;

import com.jd.blockchain.CheckImportsMojo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * @Author zhaogw
 * @Date 2019/3/1 21:27
 */
public class CheckImportsMojoTest {
    Logger logger = LoggerFactory.getLogger(CheckImportsMojo.class);

    @Test
    public void test1() {
        try {
            InputStream inputStream = CheckImportsMojo.class.getClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String result[] = properties.getProperty("blacklist").split(",");
            logger.info(Arrays.toString(result).toString());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
