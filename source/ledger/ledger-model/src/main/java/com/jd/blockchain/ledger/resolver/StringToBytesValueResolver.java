package com.jd.blockchain.ledger.resolver;

import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

import java.util.Set;


public class StringToBytesValueResolver extends AbstractBytesValueResolver {

    private final Class<?>[] supportClasses = {String.class};

    private final DataType[] supportDataTypes = {DataType.TEXT, DataType.XML, DataType.JSON};

    private final Set<Class<?>> convertClasses = initByteConvertSet();

    @Override
    public BytesValue encode(Object value, Class<?> type) {
        if (!isSupport(type)) {
            throw new IllegalStateException(String.format("Un-support encode Class[%s] Object !!!", type.getName()));
        }
        // 类型判断
        String valString = (String)value;
        if (JSONSerializeUtils.isJSON(valString)) {
            return TypedValue.fromJSON(valString);
        }
        // 暂不处理XML格式
        return TypedValue.fromText(valString);
    }

    @Override
    public Class<?>[] supportClasses() {
        return supportClasses;
    }

    @Override
    public DataType[] supportDataTypes() {
        return supportDataTypes;
    }

    @Override
    protected Object decode(Bytes value) {
        return BytesUtils.toString(value.toBytes());
    }

    @Override
    public Object decode(BytesValue value, Class<?> clazz) {
        // 支持三种类型对象返回，String.class,byte[].class,Bytes.class
        String textValue = (String)decode(value);

        if (!convertClasses.contains(clazz)) {
            throw new IllegalStateException(String.format("Un-Support decode value to class[%s] !!!", clazz.getName()));
        }

        if (clazz.equals(byte[].class)) {
            return BytesUtils.toBytes(textValue);
        } else if (clazz.equals(Bytes.class)) {
            return Bytes.fromString(textValue);
        }
        return textValue;
    }
}