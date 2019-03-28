package test.com.jd.blockchain.crypto.jniutils;

import com.jd.blockchain.crypto.jniutils.hash.JNIMBSHA256Utils;

public class JNIMBSHA256UtilsTest {
    /* Program entry function */
    public static void main(String args[]) {

        String osName = System.getProperty("os.name").toLowerCase();

        if (! osName.contains("linux")) {
            return ;
        }

        byte[] array1 = "abc".getBytes();
        byte[] array2 = "abcd".getBytes();
        byte[] array3 = "abcde".getBytes();
        byte[] array4 = "abcdef".getBytes();

        byte[][] arrays = {array1,array2,array3,array4};
        JNIMBSHA256Utils mbsha256 = new JNIMBSHA256Utils();
        byte[][] results = mbsha256.multiBufferHash(arrays);

        System.out.println("JAVA to C : ");
        for (int i = 0; i < arrays.length; i++) {
            for (int j = 0; j < arrays[i].length; j++) {
                System.out.print(arrays[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();

        System.out.println("C to JAVA : ");
        for (int i = 0; i < results.length; i++) {
            for (int j = 0; j < results[i].length; j++) {
                System.out.print(results[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();

        String str = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        byte[] array = str.getBytes();


        int count = 1000000;


        byte[][] arraysx4 = {array,array,array,array};
        byte[][] arraysx8 = {array,array,array,array,array,array,array,array};
        byte[][] arraysx16 = {array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array};
        byte[][] arraysx32 = {array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array,array};


        System.out.println("=================== do MBSHA256 hash test in x4===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                mbsha256.multiBufferHash(arraysx4);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SHA256 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f;  Total KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS, (count * 1000.00D) / elapsedTS*4));
        }
        System.out.println();
        System.out.println();

        System.out.println("=================== do MBSHA256 hash test in x8===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                mbsha256.multiBufferHash(arraysx8);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SHA256 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f;  Total KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS, (count * 1000.00D) / elapsedTS*8));
        }
        System.out.println();
        System.out.println();

        System.out.println("=================== do MBSHA256 hash test in x16===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                mbsha256.multiBufferHash(arraysx16);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SHA256 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f;  Total KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS, (count * 1000.00D) / elapsedTS*16));
        }
        System.out.println();
        System.out.println();

        System.out.println("=================== do MBSHA256 hash test in x32===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                mbsha256.multiBufferHash(arraysx32);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SHA256 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f;  Total KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS, (count * 1000.00D) / elapsedTS*32));
        }
    }
}
