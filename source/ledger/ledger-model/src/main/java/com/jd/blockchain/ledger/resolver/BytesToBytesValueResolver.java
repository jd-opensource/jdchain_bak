package com.jd.blockchain.ledger.resolver;

import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;

import java.util.Set;

public class BytesToBytesValueResolver extends AbstractBytesValueResolver {

    private final Class<?>[] supportClasses = {Bytes.class, byte[].class};

    private final DataType[] supportDataTypes = {DataType.BYTES};

    private final Set<Class<?>> convertClasses = initByteConvertSet();

    @Override
    public BytesValue encode(Object value, Class<?> type) {
        if (!isSupport(type)) {
            throw new IllegalStateException(String.format("Un-support encode Class[%s] Object !!!", type.getName()));
        }
        if (type.equals(byte[].class)) {
            return TypedValue.fromBytes((byte[]) value);
        }
        return TypedValue.fromBytes((Bytes) value);
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
        return value;
    }

    @Override
    public Object decode(BytesValue value, Class<?> clazz) {
        Bytes bytesVal = (Bytes) decode(value);
        if (!convertClasses.contains(clazz)) {
            throw new IllegalStateException(String.format("Un-Support decode value to class[%s] !!!", clazz.getName()));
        }

        if (clazz.equals(String.class)) {
            return bytesVal.toUTF8String();
        } else if (clazz.equals(byte[].class)) {
            return bytesVal.toBytes();
        }
        return bytesVal;
    }
}