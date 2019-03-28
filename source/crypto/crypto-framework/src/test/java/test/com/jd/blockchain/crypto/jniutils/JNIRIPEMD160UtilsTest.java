package test.com.jd.blockchain.crypto.jniutils;

import com.jd.blockchain.crypto.jniutils.hash.JNIRIPEMD160Utils;

public class JNIRIPEMD160UtilsTest {

    /* Program entry function */
    public static void main(String args[]) {
        byte[] array1 = "abc".getBytes();
        byte[] array2;
        JNIRIPEMD160Utils ripemd160 = new JNIRIPEMD160Utils();
        array2 = ripemd160.hash(array1);

        System.out.print("JAVA to C : ");
        for (byte anArray1 : array1) {
            System.out.print(anArray1 + " ");
        }
        System.out.println();
        System.out.print("C to JAVA : ");
        for (byte anArray2 : array2) {
            System.out.print(anArray2 + " ");
        }
        System.out.println();

        String str = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        byte[] array = str.getBytes();
        int count = 1000000;

        System.out.println("=================== do RIPEMD160 hash test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                ripemd160.hash(array);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("RIPEMD160 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }
    }
}