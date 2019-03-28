package com.jd.blockchain.crypto.paillier;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PaillierUtils {

    // To convert BigInteger to byte[] whose length is l
    public static byte[] BigIntegerToLBytes(BigInteger b, int l){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[l];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }

    public static byte[] intToBytes(int i){
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i);
        return result;
    }

    public static int bytesToInt(byte[] array){
        int result = 0;
        result |= ((array[0] & 0xFF) << 24);
        result |= ((array[1] & 0xFF) << 16);
        result |= ((array[2] & 0xFF) << 8);
        result |= ((array[3] & 0xFF));
        return result;
    }

    public static List<byte[]> split(byte[] array, byte[] delimiter) {
        List<byte[]> byteArrays = new LinkedList<>();
        if (delimiter.length == 0) {
            return byteArrays;
        }
        int begin = 0;

        outer:
        for (int i = 0; i < array.length - delimiter.length + 1; i++) {
            for (int j = 0; j < delimiter.length; j++) {
                if (array[i + j] != delimiter[j]) {
                    continue outer;
                }
            }
            byteArrays.add(Arrays.copyOfRange(array, begin, i));
            begin = i + delimiter.length;
        }
        byteArrays.add(Arrays.copyOfRange(array, begin, array.length));
        return byteArrays;
    }
}
