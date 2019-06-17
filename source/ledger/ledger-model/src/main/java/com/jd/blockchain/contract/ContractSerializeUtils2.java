//package com.jd.blockchain.contract;
//
//import com.jd.blockchain.binaryproto.BinaryProtocol;
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.consts.DataCodes;
//import com.jd.blockchain.ledger.*;
//import com.jd.blockchain.utils.Bytes;
//import com.jd.blockchain.utils.IllegalDataException;
//import org.springframework.util.ReflectionUtils;
//
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author zhaogw
// * date 2019/5/16 18:05
// */
//public class ContractSerializeUtils {
//    public static Map<Integer, Class<?>> DATA_CONTRACT_MAP = new HashMap<>();
//    public static final Integer[] PRIMITIVE_DATA_CODES = {DataCodes.CONTRACT_INT8, DataCodes.CONTRACT_INT16, DataCodes.CONTRACT_INT32,
//            DataCodes.CONTRACT_INT64, DataCodes.CONTRACT_BIG_INT,DataCodes.CONTRACT_TEXT, DataCodes.CONTRACT_BINARY };
//
//    /**
//     * serialize the Object[] by List<DataContract> list;
//     * @param objArr
//     * @param dataContractList
//     * @return
//     */
//    public static byte[] serializeMethodParam(Object[] objArr,List<DataContract> dataContractList)  {
//        byte[][] result = new byte[objArr.length][];
//        //将method中形参转换为实体对象，每个形参都必须为@DataContract类型;每个形参使用系统的BinaryProtocol来进行序列化,如果有5个参数，则使用5次序列化;
//        int sum = 0;
//
//        for(int i=0;i<objArr.length;i++){
//            DataContract dataContract = dataContractList.get(i);
//            objArr[i] = regenObj(dataContract,objArr[i]);
//            //get data interface;
//            result[i] = BinaryProtocol.encode(objArr[i],getDataIntf().get(dataContract.code()));
//            sum += result[i].length;
//        }
//        /**
//         * return byte[] format:
//         return is byte[], but now is byte[][], so we should reduct dimension by adding the header info to the rtnBytes[];
//         rtnBytes[]=classTypes.length/first length/second length/third length/result[0]/result[1]/result[2];
//         rtnBytes[0]: 4 bytes(classTypes.length);
//         rtnBytes[1]: 4 bytes(1 param's length);
//         rtnBytes[2]: 4 bytes(2 param's length);
//         rtnBytes[3]: 4 bytes(3 param's length);
//         rtnBytes[...]: result[0][] bytes(1 param's length);
//         rtnBytes[...]: result[1][] bytes(2 param's length);
//         rtnBytes[...]: result[2][] bytes(3 param's length);
//         */
//        int bodyFirstPosition = 4 + 4 * (objArr.length);
//        ByteBuffer byteBuffer = ByteBuffer.allocate(bodyFirstPosition + sum);
//        byteBuffer.putInt(objArr.length);
//        for(int j=0; j<result.length; j++) {
//            byte[] curResult = result[j];
//            byteBuffer.putInt(curResult.length);
//        }
//        for(int k=0; k<result.length; k++){
//            byteBuffer.put(result[k]);
//        }
//        return byteBuffer.array();
//    }
//
//    /**
//     * deserialize the params bytes[];
//     * params format: nums|first length| second length| third length| ... |bytes[0]| byte[1] | bytes[2]| ...
//     * @param params
//     * @param dataContractList
//     * @return
//     */
//    public static Object[] deserializeMethodParam(byte[] params, List<DataContract> dataContractList)  {
//        Object result[] = new Object[dataContractList.size()];
//        ByteBuffer byteBuffer = ByteBuffer.allocate(params.length);
//        byteBuffer.put(params);
//        int paramNums = byteBuffer.getInt(0);
//
//        if(paramNums != dataContractList.size()){
//            throw new IllegalArgumentException("deserialize Method param. params'length in byte[] != method's param length");
//        }
//
//        int offsetPosition = (1 + dataContractList.size())*4; //start position of real data;
//        for(int i=0; i<dataContractList.size(); i++){
//            DataContract dataContract = dataContractList.get(i);
//            int curParamLength = byteBuffer.getInt((i+1)*4);
//            ByteBuffer byteBuffer1 = ByteBuffer.allocate(curParamLength);
//            byteBuffer1.put(params,offsetPosition,curParamLength);
//            offsetPosition += curParamLength;
//            //if dataContract=primitive type(byte/short/int/long/String),only use its getValues();
//            Object object = BinaryProtocol.decodeAs(byteBuffer1.array(),
//                    getDataIntf().get(dataContract.code()));
//            if(isPrimitiveType(dataContract.code())){
//                Class<?> classObj = getDataIntf().get(dataContract.code());
//                try {
//                    result[i] = ReflectionUtils.invokeMethod(classObj.getMethod("getValue"),object);
//                } catch (NoSuchMethodException e) {
//                    throw new IllegalStateException("no getValue(). detail="+e.getMessage());
//                }
//            }else {
//                result[i] = object;
//            }
//            byteBuffer1.clear();
//        }
//
//        return result;
//    }
//
//
//    /**
//     * the param types that we can support;
//     * @param <T>
//     * @return
//     */
//    public static <T> Map<Integer, Class<?> > getDataIntf(){
//        DATA_CONTRACT_MAP.put(DataCodes.CONTRACT_INT8, CONTRACT_INT8.class);
//        DATA_CONTRACT_MAP.put(DataCodes.CONTRACT_INT16, CONTRACT_INT16.class);
//        DATA_CONTRACT_MAP.put(DataCodes.CONTRACT_INT32, CONTRACT_INT32.class);
//        DATA_CONTRACT_MAP.put(DataCodes.CONTRACT_INT64, CONTRACT_INT64.class);
//        DATA_CONTRACT_MAP.put(DataCodes.CONTRACT_TEXT, CONTRACT_TEXT.class);
//        DATA_CONTRACT_MAP.put(DataCodes.CONTRACT_BINARY, CONTRACT_BINARY.class);
//        DATA_CONTRACT_MAP.put(DataCodes.CONTRACT_BIZ_CONTENT, ContractBizContent.class);
//        return DATA_CONTRACT_MAP;
//    }
//
//    public static boolean isPrimitiveType(int dataContractCode){
//        return  Arrays.asList(PRIMITIVE_DATA_CODES).contains(dataContractCode);
//    }
//
//    private static Object regenObj(DataContract dataContract, Object object){
//        if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT8.class)){
//
//            return (CONTRACT_INT8) () -> Byte.parseByte(object.toString());
//        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT16.class)){
//            return (CONTRACT_INT16) () -> Short.parseShort(object.toString());
//        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT32.class)){
//            return (CONTRACT_INT32) () -> Integer.parseInt(object.toString());
//        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_INT64.class)){
//            return (CONTRACT_INT64) () -> Long.parseLong(object.toString());
//        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_TEXT.class)){
//            return (CONTRACT_TEXT) () -> object.toString();
//        }else if(getDataIntf().get(dataContract.code()).equals(CONTRACT_BINARY.class)){
//            return (CONTRACT_BINARY) () -> (Bytes) object;
//        }else if(getDataIntf().get(dataContract.code()).equals(ContractBizContent.class)){
//            ContractBizContent contractBizContent = (ContractBizContent)object;
//            return contractBizContent;
//        }else {
//            throw new IllegalDataException("cann't get new Object by dataContract and object.");
//        }
//    }
//
//    /**
//     * get contractType(contain @DataContract) by primitive class type;
//     * some class type can be supported default (byte/char/int/long/String/Bytes, and so on).
//     * in other words, need not contain the @DataContract in its class for contract param's serialization or deserialization.
//     * @param classType
//     * @return
//     */
//    private static Class<?> getContractTypeByPrimitiveType(Class<?> classType) {
//        if(classType.equals(byte.class) || classType.equals(Byte.class)){
//            return CONTRACT_INT8.class;
//        }else if(classType.equals(char.class) || classType.equals(Character.class)){
//            return CONTRACT_INT16.class;
//        }else if(classType.equals(int.class) || classType.equals(Integer.class)){
//            return CONTRACT_INT32.class;
//        }else if(classType.equals(long.class) || classType.equals(Long.class)){
//            return CONTRACT_INT64.class;
//        }else if(classType.equals(String.class)){
//            return CONTRACT_TEXT.class;
//        }else if(classType.equals(Bytes.class)){
//            return CONTRACT_BINARY.class;
//        }else {
//            throw new IllegalDataException(String.format("no support the classType=%s, please check @DataContract.",classType.toString()));
//        }
//    }
//
//    public static DataContract parseDataContract(Class<?> classType){
//        DataContract dataContract = classType.getAnnotation(DataContract.class);
//        //if the param's class Type don't contain @DataContract, then check parameterAnnotations of this method.
//        if(dataContract == null){
//            boolean canPass = false;
//            //if parameterAnnotations don't contain @DataContract, is it primitive type?
//            Class<?> contractType = getContractTypeByPrimitiveType(classType);
//            dataContract = contractType.getAnnotation(DataContract.class);
//        }
//        if(!getDataIntf().containsKey(dataContract.code())){
//            throw new IllegalArgumentException(String.format(
//                    "for now, this @dataContract(code=%s) is forbidden in the param list.",dataContract.code()));
//        }
//        return dataContract;
//    }
//}
