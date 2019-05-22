package com.jd.blockchain.contract;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.ArrayUtils;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SerializationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaogw
 * date 2019/5/16 18:05
 */
public class ContractSerializeUtils {
    public static Map<Integer, Class<?>> dataContractMap = new HashMap<>();
    public static final Class[] confirmedType = {CONTRACT_INT8.class,CONTRACT_INT16.class,CONTRACT_INT32.class,CONTRACT_INT64.class,
            CONTRACT_TEXT.class,CONTRACT_BINARY.class,CONTRACT_BIG_INT.class};

    /**
     * valid then parse the Object by Method params;
     * @param object
     * @param method
     * @return
     */
    public static byte[] serializeMethodParam(Object object,Method method)  {
        if (object == null) {
            return null;
        }

        Object[] objArr = null;
        if(object.getClass().isArray()){
            objArr = (Object[]) object;
        }

        Class<?>[] classTypes = method.getParameterTypes();
        Annotation [][] annotations = method.getParameterAnnotations();
        byte[][] result = new byte[classTypes.length][];
        //将method中形参转换为实体对象，每个形参都必须为@DataContract类型;每个形参使用系统的BinaryProtocol来进行序列化,如果有5个参数，则使用5次序列化;
        int sum = 0;
        for(int i=0;i<classTypes.length;i++){
            Class <?> classType = classTypes[i];
            DataContract dataContract = classType.getDeclaredAnnotation(DataContract.class);
            if(dataContract == null){
                boolean canPass = false;
                //check by annotation;
                Annotation[] annotationArr = annotations[i];
                for(Annotation annotation : annotationArr){
                    if(annotation.annotationType().equals(DataContract.class)){
                        dataContract = (DataContract) annotation;
                        objArr[i] = regenObj(dataContract,objArr[i]);
                        canPass = true;
                    }
                }
                if(!canPass){
                    throw new IllegalArgumentException("must set annotation in each param of contract.");
                }
            }
            //get data interface;
            result[i] = BinaryProtocol.encode(objArr[i],getDataIntf().get(dataContract.code()));
            sum += result[i].length;
        }
        /**
         * //return is byte[], but now is byte[][], so we should reduct dimension, use the header info to the first byte(length=classTypes.length);
         format:result[][]={{1,2,3},{4,5},{6,7}};  newResult[]=classTypes.length/first length/second length/3 length/result[0]/result[1]/result[2];
         rtnBytes[0]: 4 bytes(classTypes.length, <255);
         rtnBytes[1]: 4 bytes(each param's length, )
         rtnBytes[2]: 4 bytes(each param's length, )
         rtnBytes[3]: 4 bytes(each param's length, )
         rtnBytes[4...]: result[0][] bytes(each param's length)
         rtnBytes[5...]: result[1][] bytes(each param's length)
         rtnBytes[6...]: result[2][] bytes(each param's length)
         */

        int bodyFirstPosition = 4 + 4*(classTypes.length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bodyFirstPosition + sum);
        byteBuffer.putInt(classTypes.length);
        for(int j=0; j<result.length; j++) {
            byte[] curResult = result[j];
            byteBuffer.putInt(curResult.length);
        }
        for(int k=0; k<result.length; k++){
            byteBuffer.put(result[k]);
        }
        return byteBuffer.array();
    }

    /**
     * deserialize the params bytes[];
     * params format: nums|first length| second length| third length| ... |bytes[0]| byte[1] | bytes[2]| ...
     * @param params
     * @param method
     * @return
     */
    public static Object[] deserializeMethodParam(byte[] params, Method method)  {
        if (params == null) {
            return null;
        }

        Class<?>[] classTypes = method.getParameterTypes();
        Object result[] = new Object[classTypes.length];

        ByteBuffer byteBuffer = ByteBuffer.allocate(params.length);
        byteBuffer.put(params);
        int paramNums = byteBuffer.getInt(0);

        if(paramNums != classTypes.length){
            throw new IllegalArgumentException("deserializeMethodparm. params'length in byte[] != method's param length");
        }

        Annotation [][] annotations = method.getParameterAnnotations();
        int offsetPosition = (1 + classTypes.length)*4; //start position of real data;
        for(int i=0; i<classTypes.length; i++){
            Class<?> classType = classTypes[i];
            int curParamLength = byteBuffer.getInt((i+1)*4);
            DataContract dataContract = classType.getDeclaredAnnotation(DataContract.class);
            if(dataContract == null){
                boolean canPass = false;
                //check by annotation;
                Annotation[] annotationArr = annotations[i];
                for(Annotation annotation : annotationArr){
                    if(annotation.annotationType().equals(DataContract.class)){
                        dataContract = (DataContract) annotation;
                        canPass = true;
                    }
                }
                if(!canPass){
                    throw new IllegalArgumentException("must set annotation in each param of contract.");
                }
            }
            ByteBuffer byteBuffer1 = ByteBuffer.allocate(curParamLength);
            byteBuffer1.put(params,offsetPosition,curParamLength);
            offsetPosition += curParamLength;
            //if dataContract=primitive type(byte/short/int/long/String),only use its getValues();
            Object object = BinaryProtocol.decodeAs(byteBuffer1.array(),
                    getDataIntf().get(dataContract.code()));
            if(isPrimitiveType(dataContract.code())){
                Class<?> classObj = getDataIntf().get(dataContract.code());
                try {
                    result[i] = ReflectionUtils.invokeMethod(classObj.getMethod("getValue"),object);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("no getValue(). detail="+e.getMessage());
                }
            }else {
                result[i] = object;
            }
            byteBuffer1.clear();
        }

        return result;
    }


    public static <T> Map<Integer, Class<?> > getDataIntf(){
        dataContractMap.put(DataCodes.CONTRACT_INT8, CONTRACT_INT8.class);
        dataContractMap.put(DataCodes.CONTRACT_INT16, CONTRACT_INT16.class);
        dataContractMap.put(DataCodes.CONTRACT_INT32, CONTRACT_INT32.class);
        dataContractMap.put(DataCodes.CONTRACT_INT64, CONTRACT_INT64.class);
        dataContractMap.put(DataCodes.CONTRACT_TEXT, CONTRACT_TEXT.class);
        dataContractMap.put(DataCodes.CONTRACT_BINARY, CONTRACT_BINARY.class);
        dataContractMap.put(DataCodes.CONTRACT_BIG_INT, CONTRACT_BIG_INT.class);
        dataContractMap.put(DataCodes.TX_CONTENT_BODY, TransactionContentBody.class);
        return dataContractMap;
    }

    public static boolean isPrimitiveType(int dataContractCode){
        return (dataContractCode == DataCodes.CONTRACT_INT8 ||
                dataContractCode == DataCodes.CONTRACT_INT16 ||
                dataContractCode == DataCodes.CONTRACT_INT32 ||
                dataContractCode == DataCodes.CONTRACT_INT64 ||
                dataContractCode == DataCodes.CONTRACT_TEXT
        );
    }

    private static Object regenObj(DataContract dataContract, Object object){
        if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT8.class)){
            return new CONTRACT_INT8() {
                @Override
                public Byte getValue() {
                    return Byte.parseByte(object.toString());
                }
            };
        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT16.class)){
            return new CONTRACT_INT16() {
                @Override
                public short getValue() {
                    return Short.parseShort(object.toString());
                }
            };
        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT32.class)){
            return new CONTRACT_INT32() {
                @Override
                public int getValue() {
                    return Integer.parseInt(object.toString());
                }
            };
        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT64.class)){
            return new CONTRACT_INT64() {
                @Override
                public long getValue() {
                    return Long.parseLong(object.toString());
                }
            };
        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_TEXT.class)){
            return new CONTRACT_TEXT() {
                @Override
                public String getValue() {
                    return object.toString();
                }
            };
        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_BINARY.class)){
            return new CONTRACT_BINARY() {
                @Override
                public Bytes getValue() {
                    return (Bytes) object;
                }
            };
        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_BIG_INT.class)){
            return new CONTRACT_BIG_INT() {
                @Override
                public BigDecimal getValue() {
                    return new BigDecimal(object.toString());
                }
            };
        }
        return null;
    }
}
