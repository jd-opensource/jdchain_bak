package com.jd.blockchain.sdk.converters;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.transaction.TxResponseMessage;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.agent.ServiceRequest;
import com.jd.blockchain.utils.http.converters.JsonResponseConverter;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;
import com.jd.blockchain.utils.web.client.WebServiceException;
import com.jd.blockchain.utils.web.model.WebResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangshuang3 on 2018/10/17.
 */
public class HashDigestsResponseConverter implements ResponseConverter {

    private JsonResponseConverter jsonConverter = new JsonResponseConverter(WebResponse.class);

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception {
        WebResponse response = (WebResponse) jsonConverter.getResponse(request, responseStream, null);
        if (response == null) {
            return null;
        }
        if (response.getError() != null) {
            throw new WebServiceException(response.getError().getErrorCode(), response.getError().getErrorMessage());
        }
        if (response.getData() == null) {
            return null;
        }


//        byte[] serializeBytes = BytesUtils.readBytes(responseStream);
//        String jsonChar = new String(serializeBytes, "UTF-8");
//        JSONArray jsonArray = JSON.parseArray(jsonChar);
//        List<HashDigest> hashDigests = new ArrayList<>();
//        for (Object obj : jsonArray) {
//            if (obj instanceof JSONObject) {
//                String base58Str = ((JSONObject)obj).getString("value");
//                hashDigests.add(new HashDigest(Base58Utils.decode(base58Str)));
//            }
//        }
        return deserialize(response.getData());
    }

    private Object deserialize(Object object) {
        List<HashDigest> hashDigests = new ArrayList<>();
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray)object;
            for (Object obj : jsonArray) {
                if (obj instanceof Map) {
                    Map<String, String> objMap = (Map)obj;
                    String base58Str = objMap.get("value");
                    hashDigests.add(new HashDigest(Base58Utils.decode(base58Str)));
                }
            }
        }
        return hashDigests.toArray(new HashDigest[hashDigests.size()]);
    }
}
