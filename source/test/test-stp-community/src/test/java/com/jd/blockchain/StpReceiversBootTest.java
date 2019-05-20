/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.StpReceiversBootTest
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/18 下午3:53
 * Description:
 */
package com.jd.blockchain;

import com.jd.blockchain.stp.commucation.MyMessageExecutor;
import com.jd.blockchain.stp.commucation.StpReceiversBoot;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import org.junit.Test;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/18
 * @since 1.0.0
 */

public class StpReceiversBootTest {

    public static final int[] localPorts = new int[]{9900, 9901};

    @Test
    public void test() {
        StpReceiversBoot stpReceiversBoot = new StpReceiversBoot(9900, 9901);
        RemoteSessionManager[] sessionManagers = stpReceiversBoot.start(new MyMessageExecutor());

        try {
            Thread.sleep(10000);

            // 关闭所有的监听器
            for (RemoteSessionManager sessionManager : sessionManagers) {
                sessionManager.connectionManager().closeReceiver();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}