//package test.com.jd.blockchain.crypto.utils.classic;
//
//import org.bouncycastle.asn1.ASN1Sequence;
//import org.bouncycastle.crypto.params.RSAKeyParameters;
//import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
//import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
//import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
//import org.bouncycastle.jce.spec.OpenSSHPrivateKeySpec;
//import org.bouncycastle.jce.spec.OpenSSHPublicKeySpec;
//import org.bouncycastle.util.Strings;
//import org.bouncycastle.util.encoders.Base64;
//import org.bouncycastle.util.encoders.Hex;
//import org.bouncycastle.util.io.pem.PemReader;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.math.BigInteger;
//
///**
// * @author zhanglin33
// * @title: SSHKeyUtilsTest
// * @description: Tests for methods in SSHKeyUtils
// * @date 2019-05-07, 15:14
// */
//public class SSHKeyUtilsTest {
//
//    @Test
//    public void parseRSAPublicKeyTest() {
//
//        String pubKeyStr = "AAAAB3NzaC1yc2EAAAADAQABAAABAQCYwLN4EXy7g0Xugv4lQfoujbARi48gPSxVupt" +
//                "GsSoGqsS00e9rA7v0qzFKa9Zhnw1WkjCnEXRYAMiCAJYkM/mGI8mb3qkcdNhGWZmPnopV+D46CTFB1" +
//                "4yeR9mjoOPXs4pjX3zGveKx5Nx8jvdoFewbTCtdN0x1XWTjNT5bXqP/4gXkLENEU5tLsWVAOu0ME/N" +
//                "e/9gMujAtDoolJ181a9P06bvEpIw5cLtUnsm5CtvBuiL7WBXxDJ/IASJrKNGBdK8xib1+Kb8tNLAT6" +
//                "Dj25BwylqiRNhb5l1Ni4aKrE2FqSEc5Nx5+csQMEl9MBJ3pEsLHBNbohDL+jbwLguRVD6CJ";
//
//        byte[] pubKeyBytes = Base64.decode(pubKeyStr);
//        System.out.println(Hex.toHexString(pubKeyBytes));
//        OpenSSHPublicKeySpec pubKeySpec = new OpenSSHPublicKeySpec(pubKeyBytes);
//
//        String pubKeyFormat = pubKeySpec.getFormat();
//        String pubKeyType   = pubKeySpec.getType();
//        System.out.println(pubKeyFormat);
//        System.out.println(pubKeyType);
//
//        RSAKeyParameters pubKey = (RSAKeyParameters) OpenSSHPublicKeyUtil.parsePublicKey(pubKeyBytes);
//        BigInteger e = pubKey.getExponent();
//        BigInteger n = pubKey.getModulus();
//        System.out.println(Hex.toHexString(e.toByteArray()));
//        System.out.println(Hex.toHexString(n.toByteArray()));
//
//        System.out.println();
//        System.out.println("-------------------------------------------------------");
//        System.out.println();
//
//
//    }
//
//    @Test
//    public void parseRSAPrivateKeyTest() {
//
//        String str2 = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
//                "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABFwAAAAdzc2gtcn\n" +
//                "NhAAAAAwEAAQAAAQEAwFyeWgHFu/ZMvqWa28QUGlKMDV7vpbzT7kyA/4yuotfprZKHNeEy\n" +
//                "GugleJ/Kv5kqHh8Km4IZcfNcerTYds+U5m/uX4bSYpEbXco3DQ2lYQbYo7PBWwPMq2aIdd\n" +
//                "i7WxUAlt0z1ugLNimskPzJ7DNra+ax0Wh9RnMsjZkfuBZiKq7wbBm7NyJmpg2B7xo5cz+G\n" +
//                "Lw9e0tDlvgeLe+n68WvYWWFP59mfP6Qoy+NwjQnnwrhJi2j4dEexO97KmgnJhL07lu4eCQ\n" +
//                "fdv68Tai9+aeDNawe7nmFYf2eNjah2jW/DwOwA/ErXnvgjLSMsgc6WGKfokhytAOFDGgvH\n" +
//                "KKNd6BMYZwAAA9A7JircOyYq3AAAAAdzc2gtcnNhAAABAQDAXJ5aAcW79ky+pZrbxBQaUo\n" +
//                "wNXu+lvNPuTID/jK6i1+mtkoc14TIa6CV4n8q/mSoeHwqbghlx81x6tNh2z5Tmb+5fhtJi\n" +
//                "kRtdyjcNDaVhBtijs8FbA8yrZoh12LtbFQCW3TPW6As2KayQ/MnsM2tr5rHRaH1GcyyNmR\n" +
//                "+4FmIqrvBsGbs3ImamDYHvGjlzP4YvD17S0OW+B4t76frxa9hZYU/n2Z8/pCjL43CNCefC\n" +
//                "uEmLaPh0R7E73sqaCcmEvTuW7h4JB92/rxNqL35p4M1rB7ueYVh/Z42NqHaNb8PA7AD8St\n" +
//                "ee+CMtIyyBzpYYp+iSHK0A4UMaC8coo13oExhnAAAAAwEAAQAAAQAEEvIXnen+LR06/G7n\n" +
//                "MKPsWss0jUouDG3AokYpI2WfdUsxreTHM1nIUBpbD6dPn4LQ2H91A7BeRXUz9BiRi5vvtX\n" +
//                "cq9sQF6mTV+65mzF8wSuDTtr7lmpL/HlDNjiWJrEwy5cRvTMLQBtnsyC3OntgrlNs3QCtH\n" +
//                "DrFm3lNZpr+1f62Vu43dbcTPvLwcc335cJ73BU5WsMGaouCAqVXsVsgfkA66u6+gQs8O3F\n" +
//                "IQntdzS8vYpkzH8N9qqNZit7kbFCRUTI7CDLHquJmclzB8uVwO0pR5+Aross+YL3QxPZoJ\n" +
//                "+LXLlCi27oSmYk3fx3uh0XwwO3JFDQpeCxOuEsZbOy8BAAAAgCsktFksS0BViRuLyzC2H7\n" +
//                "X7Uonf+dr8e4Yn+RgR329KFh/ok28/KZndZHsUnhdmiIjPr+SplFZZMrV/uJDkGezUNWGf\n" +
//                "8qn+eEglm7nYfVf2EXTVNhpg8yfPChx90ybc8GYlqpEqf7LiCuEBCPqPJgq6K7i6UKbwn2\n" +
//                "SfqUOBcz5BAAAAgQDqszdiNv0cTvZ/Xg3qJPsHXqQuLBYoe2vhU+Pipt9HviruUD1y3Mso\n" +
//                "rOL9QBwjE7szGnwVE00J0mLp+s309+kftADLXqMyqFYiy7S8GIWQw0YNB2m8yjq+phHbBm\n" +
//                "/Gs2P4+s8yKTcVJvMTyWr02rpCHiLTKDHoXPJcJ8yVMTHFRwAAAIEA0dHB9fXiesEKfwcp\n" +
//                "X11IHAV8pTd+7VN81oGwxtRg88U7H2rmQFCxSZah2O/OCCmYLH3PHT95qnMHHqzcsVvoIy\n" +
//                "7AfnMpp4KYU0Ic3aFuRjZk2sDsYUniPcCpuCvs8Jb75sDwKDW2EM8MowiNylDnYMmfYj0l\n" +
//                "gIhz1/p79hXEI+EAAAAbemhhbmdsaW4zM0B6aGFuZ2xpbjMzLmxvY2Fs\n" +
//                "-----END OPENSSH PRIVATE KEY-----";
//
//        byte[] Bytes2 = null;
//        try {
//            Bytes2 = new PemReader(new StringReader(str2)).readPemObject().getContent();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        System.out.println(Hex.toHexString(Bytes2));
//
//        String str3 = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
//                "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABFwAAAAdzc2gtcn\n" +
//                "NhAAAAAwEAAQAAAQEAvummQZm1FUFc/cV5nQBeowhjX4vIU4kBmyPmXHMViX4ORvWvD1yi\n" +
//                "oxcaawPpP9QconpzjdCrNbmw0oZNt9UKlmrOU34YTRD5LFlEVOYjr/21/SO5yDGog8xJBU\n" +
//                "HQYnXY5L2q9EXKOF45e5P6gSGUovrhePEsaniuQN48GIObPCOFkEN0ZV2DqRsn3It1vY+D\n" +
//                "GiSb5EaZ2sNkudyzYfgFxcCbqBXmDa1WeyX5xYh8wldBJLUH+pO4gPoTXXX4UI4yNdDmPD\n" +
//                "BWFvPVIOdpfdBnDbEp1AoE5Jx/+tbwFBIEvTPOECtOUKDGIlXXIH0I4waHbwf6EnHD5+BR\n" +
//                "N0XwrzSkuwAAA9DYV/7H2Ff+xwAAAAdzc2gtcnNhAAABAQC+6aZBmbUVQVz9xXmdAF6jCG\n" +
//                "Nfi8hTiQGbI+ZccxWJfg5G9a8PXKKjFxprA+k/1ByienON0Ks1ubDShk231QqWas5TfhhN\n" +
//                "EPksWURU5iOv/bX9I7nIMaiDzEkFQdBiddjkvar0Rco4Xjl7k/qBIZSi+uF48SxqeK5A3j\n" +
//                "wYg5s8I4WQQ3RlXYOpGyfci3W9j4MaJJvkRpnaw2S53LNh+AXFwJuoFeYNrVZ7JfnFiHzC\n" +
//                "V0EktQf6k7iA+hNddfhQjjI10OY8MFYW89Ug52l90GcNsSnUCgTknH/61vAUEgS9M84QK0\n" +
//                "5QoMYiVdcgfQjjBodvB/oSccPn4FE3RfCvNKS7AAAAAwEAAQAAAQArRruxUy6BSvfRbtpK\n" +
//                "hLLvMg+UsRMQHJaInHKzskLHkBOcckTkrpMPdUU/zPsqxOJY0nkvRIYK/7TdhCRJ77ker8\n" +
//                "dllcfccGSLcRDUTfb5BgIjB94tS1Rvy/chgfHC4APyliwSg197t6BAKyM18m7HIyfJSqJO\n" +
//                "4FxfyADHbc3aq654tu+eaUtD7TEN1bH6PKMDvwSioMLgKU43GQeDJZbqamBE9y+KVhVx9y\n" +
//                "3DEHrOPkRkZIG33y9j7B/i0vl+WnwUTzmLGRR0U6J9wrzyANL8ODYaAvk4FvUED8hQ72jh\n" +
//                "NpAXsSgf6COUE1sUnO5DOwN1zHBNHaSo73Qu7aKZtL4BAAAAgDBW3ItiqU9Ip34KtLaayK\n" +
//                "/BkRDDwFNvAxpOw9alpfbLGF5xbVjRN4wy7HJ4eA+7JJnx6A6xYrzykbPP+dnUnfzYuxH8\n" +
//                "MrihOkYipw1VaR6/0XH+apmE1SmotuYbl+bpl9dlZYUI0pJ8wldqoDCNlSOcLy77HnKwu9\n" +
//                "GpJx9KmW9WAAAAgQDdnrwfVv5trAuZZIcw2vLRWhoDT196k/Ty1GP4zFpDttb8OyZ8Ygpx\n" +
//                "oA5PhYyl5M1g/2oR/Rpp3vfKDVThfn/bCnMtAbUHMfvYK3Oufvq5JmzT1rgGr3MEek+JBR\n" +
//                "O17I87m4GE7iM1LzCUs2G/fKt2uoVXdniv0Vn0iCiZZc7JmwAAAIEA3IdsccarkUfFcQ2c\n" +
//                "4TdHrx/RGmoTAO6k1xOHXZjWPmerinuOspIJL/ymWfqIxABCjub3UHyP7ap+1+AAnk+TMU\n" +
//                "eR3tLEp9tRM6n0Td56DnQ9Q+RZhPqR486/teZ33cMBMHg52aIs/3AzMpK9xTFCRgqsKa6e\n" +
//                "ednMB4Q1txvHU2EAAAAbemhhbmdsaW4zM0B6aGFuZ2xpbjMzLmxvY2Fs\n" +
//                "-----END OPENSSH PRIVATE KEY-----";
//
//        byte[] Bytes3 = null;
//        try {
//            Bytes3 = new PemReader(new StringReader(str3)).readPemObject().getContent();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        System.out.println(Hex.toHexString(Bytes3));
////        System.out.println(Hex.toHexString(Base64.decode("oNE9iA4ZyuZLbpEL7B29NaxGi4puT2Y5RDaMoEkoAKI")));
////        String test = "1ac477fa";
////        byte[] testBytes = Hex.decode(test);
////
////        System.out.println(Base64.toBase64String(testBytes));
//
//        byte[] AUTH_MAGIC = Strings.toByteArray("openssh-key-v1\0");
//        System.out.println(Hex.toHexString(AUTH_MAGIC));
//        System.out.println(Base64.toBase64String(AUTH_MAGIC));
//        String privKeyStr = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
//                "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABFwAAAAdzc2gtcn\n" +
//                "NhAAAAAwEAAQAAAQEAmMCzeBF8u4NF7oL+JUH6Lo2wEYuPID0sVbqbRrEqBqrEtNHvawO7\n" +
//                "9KsxSmvWYZ8NVpIwpxF0WADIggCWJDP5hiPJm96pHHTYRlmZj56KVfg+OgkxQdeMnkfZo6\n" +
//                "Dj17OKY198xr3iseTcfI73aBXsG0wrXTdMdV1k4zU+W16j/+IF5CxDRFObS7FlQDrtDBPz\n" +
//                "Xv/YDLowLQ6KJSdfNWvT9Om7xKSMOXC7VJ7JuQrbwboi+1gV8QyfyAEiayjRgXSvMYm9fi\n" +
//                "m/LTSwE+g49uQcMpaokTYW+ZdTYuGiqxNhakhHOTcefnLEDBJfTASd6RLCxwTW6IQy/o28\n" +
//                "C4LkVQ+giQAAA9AaxHf6GsR3+gAAAAdzc2gtcnNhAAABAQCYwLN4EXy7g0Xugv4lQfoujb\n" +
//                "ARi48gPSxVuptGsSoGqsS00e9rA7v0qzFKa9Zhnw1WkjCnEXRYAMiCAJYkM/mGI8mb3qkc\n" +
//                "dNhGWZmPnopV+D46CTFB14yeR9mjoOPXs4pjX3zGveKx5Nx8jvdoFewbTCtdN0x1XWTjNT\n" +
//                "5bXqP/4gXkLENEU5tLsWVAOu0ME/Ne/9gMujAtDoolJ181a9P06bvEpIw5cLtUnsm5CtvB\n" +
//                "uiL7WBXxDJ/IASJrKNGBdK8xib1+Kb8tNLAT6Dj25BwylqiRNhb5l1Ni4aKrE2FqSEc5Nx\n" +
//                "5+csQMEl9MBJ3pEsLHBNbohDL+jbwLguRVD6CJAAAAAwEAAQAAAQARfhfPSylei9TpUGTs\n" +
//                "PVb6F82u5K16QqceFiWL/ePTKaEnF9d0CNRwW15kqF6/hShQ3qLlrvEE1uofQRPwh2cuvl\n" +
//                "BrIh95m8PcoowcT0qGN8xgdwcGBDodMhsxSs5suCnD4X53f+1C8/Nv7CtW5xPHuHxKy3dd\n" +
//                "BVn1TvaaHgdn2PwJVKtZp+WVG3/UHr25nFHd8mYgpeHZqK9AW16N0UEMXMM1u8ZCubVOoS\n" +
//                "IGuMAXpTug0xA+BXHo17FcDGKSzcXFzh+urIz5glRp5zFioHBqxNmkKfQkG6C7UxnPGyS/\n" +
//                "/J+3lL2lvl0G8kO/5EDFMBhTMEy1NeR2b629S4G1qUxVAAAAgHDwE9kPiVETcxSzI4wotT\n" +
//                "1Ee9nKVVD3oGdRqefvX7EUR8bvdv4nCqHPNBx8C6l8zo7fsQD81YL85F4eWbtrdxEijRHX\n" +
//                "5m7J/muh/laY1Hq43WCkZGboO4fZ2HHi7oN096FqrKRpvbQGQi1FLbcISUdsitwrs6ywn3\n" +
//                "fNx3q+X3V6AAAAgQDJRo9v+0QvldI33cpJKPKiaop5QvfIDzMatD3vLA1qqycgIi4KOtb5\n" +
//                "+LP/jgIpCYah/sk+JpKNz/vsZmZmrfaVu9D3Le2LLBgMpEoSO8jOe9WGI4Ew75C7w7AZCa\n" +
//                "SyUnHIVX/9D8Y5tx4cKx6Im9AGbNF35XZoKO4KCk5VMTXhnwAAAIEAwkjKIpTYdeAQVTRf\n" +
//                "C13kg84Bu5n4WkiLnp1WOCg2GN5RekqprINtpjIMZoB9Fv292Np99La8yvmRoy5qzNHGdm\n" +
//                "Q6AMku8jP123jF2J+wDvF714VtZHNvdCYBGJS+rZ81xtJfHhKtZqRAVtbPertOWZeuRm9V\n" +
//                "o+/rEuEzgGYGXNcAAAAbemhhbmdsaW4zM0B6aGFuZ2xpbjMzLmxvY2Fs\n" +
//                "-----END OPENSSH PRIVATE KEY-----";
//
//        byte[] privKeyBytes = null;
//        try {
//            privKeyBytes = new PemReader(new StringReader(privKeyStr)).readPemObject().getContent();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//
//        assert privKeyBytes != null;
//        System.out.println(Hex.toHexString(privKeyBytes));
//
//
//        OpenSSHPrivateKeySpec privKeySpec = new OpenSSHPrivateKeySpec(privKeyBytes);
//
//        String privKeyFormat = privKeySpec.getFormat();
//        System.out.println(privKeyFormat);
//
//        RSAKeyParameters privKey = (RSAKeyParameters) OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(privKeyBytes);
//
////        BigInteger e = privKey.getPublicExponent();
////        BigInteger n = privKey.getModulus();
////
////        BigInteger d = privKey.getExponent();
////        BigInteger p = privKey.getP();
////        BigInteger q = privKey.getQ();
////        BigInteger dP = privKey.getDP();
////        BigInteger dQ = privKey.getDQ();
////        BigInteger qInv = privKey.getQInv();
//
////        System.out.println(Hex.toHexString(e.toByteArray()));
////        System.out.println(Hex.toHexString(n.toByteArray()));
////
////        System.out.println(Hex.toHexString(d.toByteArray()));
////        System.out.println(Hex.toHexString(p.toByteArray()));
////        System.out.println(Hex.toHexString(q.toByteArray()));
////        System.out.println(Hex.toHexString(dP.toByteArray()));
////        System.out.println(Hex.toHexString(dQ.toByteArray()));
////        System.out.println(Hex.toHexString(qInv.toByteArray()));
//
//
//
//    }
//
//}
