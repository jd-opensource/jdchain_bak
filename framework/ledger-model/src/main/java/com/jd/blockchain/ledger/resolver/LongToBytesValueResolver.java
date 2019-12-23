package com.jd.blockchain.ledger.resolver;

import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

import java.util.Set;

public class LongToBytesValueResolver extends AbstractBytesValueResolver {

    private final Class<?>[] supportClasses = {Long.class, long.class};

    private final DataType[] supportDataTypes = {DataType.INT64};

    private final Set<Class<?>> convertClasses = initIntConvertSet();

    @Override
    public BytesValue encode(Object value, Class<?> type) {
        if (!isSupport(type)) {
            throw new IllegalStateException(String.format("Un-support encode Class[%s] Object !!!", type.getName()));
        }
        return TypedValue.fromInt64((long)value);
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
        return BytesUtils.toLong(value.toBytes());
    }

    @Override
    public Object decode(BytesValue value, Class<?> clazz) {
        // 支持转换为short、int、long
        long longVal = (long)decode(value);
        if (convertClasses.contains(clazz)) {
            // 对于short和Short需要强制类型转换
            if (clazz.equals(short.class) || clazz.equals(Short.class)) {
                return (short) longVal;
            } else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                return (int) longVal;
            }
            return longVal;
        } else {
            throw new IllegalStateException(String.format("Un-Support decode value to class[%s] !!!", clazz.getName()));
        }
    }
}