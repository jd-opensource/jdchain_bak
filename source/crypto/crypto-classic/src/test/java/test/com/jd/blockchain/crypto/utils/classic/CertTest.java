package test.com.jd.blockchain.crypto.utils.classic;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author zhanglin33
 * @title: CertTest
 * @description: TODO
 * @date 2019-05-09, 11:34
 */
public class CertTest {

    private byte[] certBytes = Base64.decode("MIIEQDCCAyigAwIBAgIFICdVYzEwDQYJKoZIhvcNAQEFBQAwWTELMAkGA1UEBhMCQ04xMDAuBgNVBAoTJ0NoaW5hIEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEYMBYGA1UEAxMPQ0ZDQSBURVNUIE9DQTExMB4XDTE5MDUxMDExMjAyNFoXDTIxMDUxMDExMjAyNFowcjELMAkGA1UEBhMCQ04xGDAWBgNVBAoTD0NGQ0EgVEVTVCBPQ0ExMTERMA8GA1UECxMITG9jYWwgUkExFTATBgNVBAsTDEluZGl2aWR1YWwtMTEfMB0GA1UEAxQWMDUxQGFhYWFhQFpIMDkzNTgwMjhAMzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJx3F2WD1dJPzK/nRHO7d1TJ1hTjzGTmv0PQ7ECsJAh3U3BtnGTpCB+b4+JMI4LO8nHkKIBQ3P9XnF+Bf1iXdWNAQ4aWCxa2nV7lCp4w0GliPu/EMgIfmsSDUtgqbM3cr8sR8r9m1xG3gt2TIQJ+jT7sAiguU/kyNzpjaccOUIgUFa8IDFq9UeB76MXtCuhlERRZQCl47e+9w7ZoxmE7e6IZORxPp7rQWVBHlR9ntWjJfNDTm3gMP5ehP+yIZnKx1LudxkBLQxpMmspzOyH1zqx5nkKe49AfWWpDxxRvYkriyYC3aE81qLsU/bhLwNEKOju7BGDF/mhJLZUedojM0gMCAwEAAaOB9TCB8jAfBgNVHSMEGDAWgBT8C7xEmg4xoYOpgYcnHgVCxr9W+DBIBgNVHSAEQTA/MD0GCGCBHIbvKgECMDEwLwYIKwYBBQUHAgEWI2h0dHA6Ly93d3cuY2ZjYS5jb20uY24vdXMvdXMtMTUuaHRtMDoGA1UdHwQzMDEwL6AtoCuGKWh0dHA6Ly8yMTAuNzQuNDIuMy9PQ0ExMS9SU0EvY3JsMjU2OTMuY3JsMAsGA1UdDwQEAwID6DAdBgNVHQ4EFgQU5oKGaQs7Jt5Gfbt1XhFTWAySEKswHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMA0GCSqGSIb3DQEBBQUAA4IBAQAlmPRaImZV51iKjtpMKuyLMw7dX8L0lY3tl+pVZZSxHuwsN4GCCtV0Ej50up+/6EbfL4NUTiuHVAjCroKKvb+94CrdEwdnQGM5IbGSjT78nQpeASXbIWuUwA+ImjvZOzvq/0b56AzonNzBxOMGko/bj5smM6X8jrgJ0NQppo2KNSVNC4JbuoNWI4FM94SE4DUi9H7EYl4JdOtDaDtCsq49o/A1CZyYrmoOPCgxpQQXmuB3lGq/jyoOlW2aW8uee/hYG1JJcSHLBjF0WBwdxssgbBotA5f1PebiIMSbFgjk57bd4M80hhU/rI4Hkn9pcp5R7NsX95TtyDIg90LboBnW");
    public static String getSubjectDN(byte[] der) {
        String dn = "";
        try {
            ByteArrayInputStream bIn = new ByteArrayInputStream(der);
            //BouncyCastleProvider provider = new BouncyCastleProvider();
            //CertificateFactory cf = CertificateFactory.getInstance("X509",
            //provider);
            //CertificateFactory cf = CertificateFactory.getInstance("X.509",
            //		"SUN");
            //android 需采用bcprov
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            System.out.println(cf.getProvider().getName());
            X509Certificate cert = (X509Certificate) cf
                    .generateCertificate(bIn);
            dn = cert.getSubjectDN().getName();

            System.out.println(Hex.toHexString(cert.getEncoded()));
            byte[] pubKeyBytes = cert.getPublicKey().getEncoded();
            System.out.println(Hex.toHexString(pubKeyBytes));
            System.out.println(cert.getSigAlgName());
            String issuerName = cert.getIssuerDN().getName();
            System.out.println(issuerName);
            String oid = cert.getSigAlgOID();
            System.out.println(oid);
            System.out.println();
            String name = cert.getIssuerX500Principal().getName();
            System.out.println(name);
            bIn.close();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dn;
    }


//    public static String parseCertDN(String dn, String type) {
//        type = type + "=";
//        String[] split = dn.split(",");
//        for (String x : split) {
//            if (x.contains(type)) {
//                x = x.trim();
//                return x.substring(type.length());
//            }
//        }
//        return null;
//    }


    @Test
    public void cert1Test() {
        String string = getSubjectDN(certBytes);
        System.out.println(Hex.toHexString(certBytes));
        System.out.println(string);
    }
}
