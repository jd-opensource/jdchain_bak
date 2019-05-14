package com.jd.blockchain.storage.service.impl.composite;

import com.jd.blockchain.storage.service.DbConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangshuang3 on 2018/11/26.
 */
@Configuration
@ComponentScan
public class CompositeStorageConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public DbConnectionFactory compositeConnectionFactory() {
        return new CompositeConnectionFactory();
    }
}
