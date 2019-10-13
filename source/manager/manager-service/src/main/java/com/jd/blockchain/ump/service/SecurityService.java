package com.jd.blockchain.ump.service;

import java.util.List;

public interface SecurityService {

    List<String> securityConfigs();

    List<String> participantRoleConfigs();

    void init();
}
