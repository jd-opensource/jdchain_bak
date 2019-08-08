package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.user.UserKeyBuilder;
import com.jd.blockchain.ump.model.user.UserKeys;

public interface UtilService {

    UserKeys create(UserKeyBuilder builder);

    UserKeys create(String name, String seed, String pwd);

    UserKeys read(int id);

    boolean verify(UserKeys userKeys, String pwd);
}
