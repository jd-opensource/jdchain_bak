/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.SessionMessageTest
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/17 下午3:24
 * Description:
 */
package com.jd.blockchain;

import com.jd.blockchain.stp.communication.message.SessionMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/17
 * @since 1.0.0
 */

public class SessionMessageTest {


    @Test
    public void test() {
        SessionMessage message = new SessionMessage("127.0.0.1", 9001, "com.jd.blockchain.StpTest.StpMessageExecute");

        String transMsg = message.toTransfer();
        System.out.println(transMsg);

        SessionMessage sm = SessionMessage.toSessionMessage(transMsg);

        assertEquals(transMsg, sm.toTransfer());
    }
}