package com.jd.blockchain.web.serializes;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.jd.blockchain.binaryproto.BaseType;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesSlice;

import java.lang.reflect.Type;

public class ByteArrayObjectJsonSerializer implements ObjectSerializer {

    private Class<?> clazz;

    private ByteArrayObjectJsonSerializer(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static ByteArrayObjectJsonSerializer getInstance(Class<?> clazz) {
        return new ByteArrayObjectJsonSerializer(clazz);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        if (object.getClass() != clazz) {
            serializer.writeNull();
            return;
        }
        if (object instanceof HashDigest) {
            serializer.write(new HashDigestJson(((HashDigest) object).toBase58()));
        } else if (object instanceof PubKey) {
            serializer.write(new HashDigestJson(((PubKey) object).toBase58()));
        } else if (object instanceof SignatureDigest) {
            serializer.write(new HashDigestJson(((SignatureDigest) object).toBase58()));
        } else if (object instanceof Bytes) {
            serializer.write(new HashDigestJson(((Bytes) object).toBase58()));
        } else if (object instanceof BytesSlice) {
            serializer.write(Base58Utils.encode(((BytesSlice) object).toBytes()));
        }

//        else if (object instanceof BytesValue) {
//            DataType dataType = ((BytesValue) object).getType();
//            BytesSlice bytesValue = ((BytesValue) object).getValue();
//            Object realVal;
//            switch (dataType) {
//                case NIL:
//                    realVal = null;
//                    break;
//                case TEXT:
//                    realVal = bytesValue.getString();
//                    break;
//                case BYTES:
//                    realVal = ByteArray.toHex(bytesValue.toBytes());
//                    break;
//                case INT32:
//                    realVal = bytesValue.getInt();
//                    break;
//                case INT64:
//                    realVal = bytesValue.getLong();
//                    break;
//                case JSON:
//                    realVal = bytesValue.getString();
//                    break;
//                default:
//                    realVal = ByteArray.toHex(bytesValue.toBytes());
//                    break;
//            }
//            serializer.write(new BytesValueJson(dataType, realVal));
//        }
    }

    private static class HashDigestJson {

        String value;

        public HashDigestJson(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class BytesValueJson {

        public BytesValueJson(BaseType type, Object value) {
            this.type = type;
            this.value = value;
        }

        BaseType type;

        Object value;

        public BaseType getType() {
            return type;
        }

        public void setType(BaseType type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
