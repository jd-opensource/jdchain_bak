/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.converters.BinarySerializeResponseConverter
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/5 下午5:22
 * Description:
 */
package com.jd.blockchain.utils.http.converters;

import java.io.InputStream;

import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.agent.ServiceRequest;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class BinarySerializeResponseConverter implements ResponseConverter {

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext)
            throws Exception {
    	return BinarySerializeUtils.deserialize(responseStream);
    }

}