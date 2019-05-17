package com.jd.blockchain.utils.serialize.binary;

import com.jd.blockchain.utils.ArrayUtils;
import com.jd.blockchain.utils.io.BytesUtils;
import org.springframework.util.SerializationUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * @author zhaogw
 * date 2019/5/16 18:05
 */
public class ContractSerializeUtils {
    public static final Class[] confirmedType = {Integer.class, Long.class, Double.class,
            int.class,long.class,double.class,String.class,  BigDecimal.class};

    /**
     * valid then parse the Object by Method params;
     * @param object
     * @param method
     * @return
     */
    public static byte[] serializeMethodParam(Object object,Method method)  {
        if (object == null) {
            return BytesUtils.EMPTY_BYTES;
        }

        Class<?>[] classType = method.getParameterTypes();
        for(Class<?> curClass : classType){
            if(!ArrayUtils.asList(confirmedType).contains(curClass)){
                throw new IllegalArgumentException("not support this type="+curClass.toString());
            }
        }
        return SerializationUtils.serialize(object);
    }

    public static Object deserializeMethodParam(byte[] bytes,Method method)  {
        if (bytes == null) {
            return null;
        }

        Class<?>[] classType = method.getParameterTypes();
        for(Class<?> curClass : classType){
            if(!ArrayUtils.asList(confirmedType).contains(curClass)){
                throw new IllegalArgumentException("not support this type="+curClass.toString());
            }
        }
        return SerializationUtils.deserialize(bytes);
    }
}
