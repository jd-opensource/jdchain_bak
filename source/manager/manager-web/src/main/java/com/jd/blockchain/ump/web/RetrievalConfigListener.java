package com.jd.blockchain.ump.web;


import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Properties;

public class RetrievalConfigListener implements ApplicationListener<ContextRefreshedEvent> {
    private Properties props;

    public RetrievalConfigListener(Properties props){
        this.props = props;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        RetrievalConfig.processProperties(props);
    }
}
