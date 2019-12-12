/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.ClientIdentificationsProvider
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/19 下午3:59
 * Description:
 */
package com.jd.blockchain.consensus;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/19
 * @since 1.0.0
 */

public class ClientIdentificationsProvider implements ClientIdentifications {

    private List<ClientIdentification> clientIdentifications = new ArrayList<>();

    public void add(ClientIdentification clientIdentification) {
        clientIdentifications.add(clientIdentification);
    }

    @Override
    public ClientIdentification[] getClientIdentifications() {
        return clientIdentifications.toArray(new ClientIdentification[clientIdentifications.size()]);
    }
}