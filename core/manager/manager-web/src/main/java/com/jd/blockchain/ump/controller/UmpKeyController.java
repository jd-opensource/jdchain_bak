package com.jd.blockchain.ump.controller;

import com.jd.blockchain.ump.model.user.UserKeyBuilder;
import com.jd.blockchain.ump.model.user.UserKeys;
import com.jd.blockchain.ump.model.user.UserKeysVv;
import com.jd.blockchain.ump.service.UmpStateService;
import com.jd.blockchain.ump.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/keys/")
public class UmpKeyController {

    @Autowired
    private UtilService utilService;

    @Autowired
    private UmpStateService umpStateService;


    /**
     * 创建用户
     *
     * @param builder
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "create")
    public UserKeysVv create(@RequestBody final UserKeyBuilder builder) {

        // 使用种子生成公私钥
        UserKeys userKeys = utilService.create(builder);

        // 将userKeys保存至数据库
        umpStateService.save(userKeys);

        return userKeys.toUserKeysVv();
    }

    @RequestMapping(method = RequestMethod.GET, path = "list")
    public List<UserKeysVv> list() {

        // 从数据库中读取，返回
        return umpStateService.readUserKeysVvList();
    }

    @RequestMapping(method = RequestMethod.GET, path = "read/{user}/{pubKey}")
    public UserKeysVv read(@PathVariable(name = "user") int userId,
                           @PathVariable(name = "pubKey") String pubKey) {

        UserKeys userKeys = utilService.read(userId);

        if (userKeys != null) {
            if (userKeys.getPubKey().equals(pubKey)) {

                return userKeys.toUserKeysVv();
            }
        }
        throw new IllegalStateException(String.format("Can not find UserKeys by %s", pubKey));
    }

    /**
     * 解析UserKeys
     *
     * @param userId
     *         用户ID
     * @param pwd
     *         密码（非编码后密码）
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "resolve/{user}/{pwd}")
    public UserKeys resolve(@PathVariable(name = "user") int userId,
                           @PathVariable(name = "pwd") String pwd) {

        UserKeys userKeys = utilService.read(userId);

        if (utilService.verify(userKeys, pwd)) {

            return userKeys;
        }
        throw new IllegalStateException(String.format("Can not resolve UserKeys by %s", pwd));
    }
}
