package com.jd.blockchain.ledger.resolver;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;

public abstract class AbstractBytesValueResolver implements BytesValueResolver {

    protected boolean isSupport(Class<?> type) {
        if (type == null) {
            return false;
        }
        Class<?>[] supports = supportClasses();
        if (supports != null && supports.length > 0) {
            for (Class<?> clazz : supports) {
                if (type.equals(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isSupport(DataType dataType) {
        if (dataType == null) {
            return false;
        }
        DataType[] supports = supportDataTypes();
        if (supports != null && supports.length > 0) {
            for (DataType dt : supports) {
                if (dataType.equals(dt)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BytesValue encode(Object value) {
        return encode(value, value.getClass());
    }

    @Override
    public Object decode(BytesValue value) {
        DataType dataType = value.getType();
        if (!isSupport(dataType)) {
            throw new IllegalStateException(String.format("Un-support encode DataType[%s] Object !!!", dataType.name()));
        }
        return decode(value.getBytes());
    }

    protected abstract Object decode(Bytes value);
}