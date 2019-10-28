package com.jd.blockchain.ledger.resolver;

import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

import java.util.Set;

public class ShortToBytesValueResolver extends AbstractBytesValueResolver {

    private final Class<?>[] supportClasses = {Short.class, short.class};

    private final DataType[] supportDataTypes = {DataType.INT16};

    private final Set<Class<?>> convertClasses = initIntConvertSet();

    @Override
    public BytesValue encode(Object value, Class<?> type) {
        if (!isSupport(type)) {
            throw new IllegalStateException(String.format("Un-support encode Class[%s] Object !!!", type.getName()));
        }
        return TypedValue.fromInt16((short)value);
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
        return BytesUtils.toShort(value.toBytes());
    }

    @Override
    public Object decode(BytesValue value, Class<?> clazz) {
        // 支持转换为short、int、long，由short转int、long无需转换
        short shortVal = (short)decode(value);
        if (convertClasses.contains(clazz)) {
            if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                return (int) shortVal;
            } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                return (long) shortVal;
            }
            return shortVal;
        } else {
            throw new IllegalStateException(String.format("Un-Support decode value to class[%s] !!!", clazz.getName()));
        }
    }
}