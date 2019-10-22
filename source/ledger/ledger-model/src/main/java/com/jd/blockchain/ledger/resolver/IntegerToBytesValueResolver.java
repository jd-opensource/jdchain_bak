package com.jd.blockchain.ledger.resolver;

import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

import java.util.Set;

public class IntegerToBytesValueResolver extends AbstractBytesValueResolver {

    private final Class<?>[] supportClasses = {Integer.class, int.class};

    private final DataType[] supportDataTypes = {DataType.INT32};

    private final Set<Class<?>> convertClasses = initIntConvertSet();

    @Override
    public BytesValue encode(Object value, Class<?> type) {
        if (!isSupport(type)) {
            throw new IllegalStateException(String.format("Un-support encode Class[%s] Object !!!", type.getName()));
        }
        return TypedValue.fromInt32((int) value);
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
        return BytesUtils.toInt(value.toBytes());
    }

    @Override
    public Object decode(BytesValue value, Class<?> clazz) {
        // 支持转换为short、int、long
        int intVal = (int)decode(value);
        if (convertClasses.contains(clazz)) {
            // 对于short和Short需要强制类型转换
            if (clazz.equals(short.class) || clazz.equals(Short.class)) {
                return (short) intVal;
            } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                return (long) intVal;
            }
            return intVal;
        } else {
            throw new IllegalStateException(String.format("Un-Support decode value to class[%s] !!!", clazz.getName()));
        }
    }
}
