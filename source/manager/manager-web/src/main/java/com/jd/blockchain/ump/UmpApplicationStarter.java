package com.jd.blockchain.ump;

import com.jd.blockchain.ump.web.RetrievalConfigListener;
import org.springframework.boot.SpringApplication;

import java.util.Properties;

/**
 * JDChain Manager 应用启动器
 *
 * @author shaozhuguang
 * @date 2019-09-20
 *
 */
public class UmpApplicationStarter {

    /**
     * 启动SpringBoot
     *     使用自定义ClassLoader加载该类及方法调用
     *
     * @param args
     * @param props
     */
    public static void start(String[] args, Properties props) {
        SpringApplication springApplication = new SpringApplication(UmpConfiguration.class);
        springApplication.addListeners(new RetrievalConfigListener(props));
        springApplication.run(args);
    }
}
