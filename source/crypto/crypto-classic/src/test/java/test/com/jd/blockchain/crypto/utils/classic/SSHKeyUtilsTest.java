package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.SSHKeyParser;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author zhanglin33
 * @title: SSHKeyUtilsTest
 * @description: Tests for methods in SSHKeyUtils
 * @date 2019-05-07, 15:14
 */
public class SSHKeyUtilsTest {

    @Test
    public void parseRSAPublicKeyTest() {

        String pubKeyStr = "AAAAB3NzaC1yc2EAAAADAQABAAABAQCYwLN4EXy7g0Xugv4lQfoujbARi48gPSxVupt" +
                "GsSoGqsS00e9rA7v0qzFKa9Zhnw1WkjCnEXRYAMiCAJYkM/mGI8mb3qkcdNhGWZmPnopV+D46CTFB1" +
                "4yeR9mjoOPXs4pjX3zGveKx5Nx8jvdoFewbTCtdN0x1XWTjNT5bXqP/4gXkLENEU5tLsWVAOu0ME/N" +
                "e/9gMujAtDoolJ181a9P06bvEpIw5cLtUnsm5CtvBuiL7WBXxDJ/IASJrKNGBdK8xib1+Kb8tNLAT6" +
                "Dj25BwylqiRNhb5l1Ni4aKrE2FqSEc5Nx5+csQMEl9MBJ3pEsLHBNbohDL+jbwLguRVD6CJ";

        BigInteger exponent = new BigInteger("010001",16);
        BigInteger modulus  = new BigInteger("0098c0b378117cbb8345ee82fe2541fa2e8db0118b8f2" +
                "03d2c55ba9b46b12a06aac4b4d1ef6b03bbf4ab314a6bd6619f0d569230a711745800c88200962" +
                "433f98623c99bdea91c74d84659998f9e8a55f83e3a093141d78c9e47d9a3a0e3d7b38a635f7cc" +
                "6bde2b1e4dc7c8ef76815ec1b4c2b5d374c755d64e3353e5b5ea3ffe205e42c4344539b4bb1654" +
                "03aed0c13f35effd80cba302d0e8a25275f356bd3f4e9bbc4a48c3970bb549ec9b90adbc1ba22f" +
                "b5815f10c9fc801226b28d18174af3189bd7e29bf2d34b013e838f6e41c3296a8913616f997536" +
                "2e1a2ab13616a484739371e7e72c40c125f4c049de912c2c704d6e88432fe8dbc0b82e4550fa089",16);
        String pubKeyFormat = "OpenSSH";
        String pubKeyType = "ssh-rsa";
        String id = "zhanglin33@zhanglin33.local";

        SSHKeyParser parser = new SSHKeyParser();

        RSAKeyParameters pubKey = (RSAKeyParameters) parser.pubKeyParse(pubKeyStr);
        BigInteger e = pubKey.getExponent();
        BigInteger n = pubKey.getModulus();
        assertEquals(exponent,e);
        assertEquals(modulus,n);
        assertEquals(pubKeyFormat,parser.getKeyFormat());
        assertEquals(pubKeyType,parser.getKeyType());

        String pubKeyStrWithHead = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCYwLN4EXy7g0Xugv4lQfou" +
                "jbARi48gPSxVuptGsSoGqsS00e9rA7v0qzFKa9Zhnw1WkjCnEXRYAMiCAJYkM/mGI8mb3qkcdNhGWZm" +
                "PnopV+D46CTFB14yeR9mjoOPXs4pjX3zGveKx5Nx8jvdoFewbTCtdN0x1XWTjNT5bXqP/4gXkLENEU5" +
                "tLsWVAOu0ME/Ne/9gMujAtDoolJ181a9P06bvEpIw5cLtUnsm5CtvBuiL7WBXxDJ/IASJrKNGBdK8xi" +
                "b1+Kb8tNLAT6Dj25BwylqiRNhb5l1Ni4aKrE2FqSEc5Nx5+csQMEl9MBJ3pEsLHBNbohDL+jbwLguRV" +
                "D6CJ zhanglin33@zhanglin33.local\n";
        pubKey = (RSAKeyParameters) parser.pubKeyParse(pubKeyStrWithHead);
        e = pubKey.getExponent();
        n = pubKey.getModulus();
        assertEquals(exponent,e);
        assertEquals(modulus,n);
        assertEquals(pubKeyFormat,parser.getKeyFormat());
        assertEquals(pubKeyType,parser.getKeyType());

        assertEquals(id, parser.getIdentity());
    }

    @Test
    public void parseRSAPrivateKeyTest() {

        String privKeyStr = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
                "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABFwAAAAdzc2gtcn\n" +
                "NhAAAAAwEAAQAAAQEAmMCzeBF8u4NF7oL+JUH6Lo2wEYuPID0sVbqbRrEqBqrEtNHvawO7\n" +
                "9KsxSmvWYZ8NVpIwpxF0WADIggCWJDP5hiPJm96pHHTYRlmZj56KVfg+OgkxQdeMnkfZo6\n" +
                "Dj17OKY198xr3iseTcfI73aBXsG0wrXTdMdV1k4zU+W16j/+IF5CxDRFObS7FlQDrtDBPz\n" +
                "Xv/YDLowLQ6KJSdfNWvT9Om7xKSMOXC7VJ7JuQrbwboi+1gV8QyfyAEiayjRgXSvMYm9fi\n" +
                "m/LTSwE+g49uQcMpaokTYW+ZdTYuGiqxNhakhHOTcefnLEDBJfTASd6RLCxwTW6IQy/o28\n" +
                "C4LkVQ+giQAAA9AaxHf6GsR3+gAAAAdzc2gtcnNhAAABAQCYwLN4EXy7g0Xugv4lQfoujb\n" +
                "ARi48gPSxVuptGsSoGqsS00e9rA7v0qzFKa9Zhnw1WkjCnEXRYAMiCAJYkM/mGI8mb3qkc\n" +
                "dNhGWZmPnopV+D46CTFB14yeR9mjoOPXs4pjX3zGveKx5Nx8jvdoFewbTCtdN0x1XWTjNT\n" +
                "5bXqP/4gXkLENEU5tLsWVAOu0ME/Ne/9gMujAtDoolJ181a9P06bvEpIw5cLtUnsm5CtvB\n" +
                "uiL7WBXxDJ/IASJrKNGBdK8xib1+Kb8tNLAT6Dj25BwylqiRNhb5l1Ni4aKrE2FqSEc5Nx\n" +
                "5+csQMEl9MBJ3pEsLHBNbohDL+jbwLguRVD6CJAAAAAwEAAQAAAQARfhfPSylei9TpUGTs\n" +
                "PVb6F82u5K16QqceFiWL/ePTKaEnF9d0CNRwW15kqF6/hShQ3qLlrvEE1uofQRPwh2cuvl\n" +
                "BrIh95m8PcoowcT0qGN8xgdwcGBDodMhsxSs5suCnD4X53f+1C8/Nv7CtW5xPHuHxKy3dd\n" +
                "BVn1TvaaHgdn2PwJVKtZp+WVG3/UHr25nFHd8mYgpeHZqK9AW16N0UEMXMM1u8ZCubVOoS\n" +
                "IGuMAXpTug0xA+BXHo17FcDGKSzcXFzh+urIz5glRp5zFioHBqxNmkKfQkG6C7UxnPGyS/\n" +
                "/J+3lL2lvl0G8kO/5EDFMBhTMEy1NeR2b629S4G1qUxVAAAAgHDwE9kPiVETcxSzI4wotT\n" +
                "1Ee9nKVVD3oGdRqefvX7EUR8bvdv4nCqHPNBx8C6l8zo7fsQD81YL85F4eWbtrdxEijRHX\n" +
                "5m7J/muh/laY1Hq43WCkZGboO4fZ2HHi7oN096FqrKRpvbQGQi1FLbcISUdsitwrs6ywn3\n" +
                "fNx3q+X3V6AAAAgQDJRo9v+0QvldI33cpJKPKiaop5QvfIDzMatD3vLA1qqycgIi4KOtb5\n" +
                "+LP/jgIpCYah/sk+JpKNz/vsZmZmrfaVu9D3Le2LLBgMpEoSO8jOe9WGI4Ew75C7w7AZCa\n" +
                "SyUnHIVX/9D8Y5tx4cKx6Im9AGbNF35XZoKO4KCk5VMTXhnwAAAIEAwkjKIpTYdeAQVTRf\n" +
                "C13kg84Bu5n4WkiLnp1WOCg2GN5RekqprINtpjIMZoB9Fv292Np99La8yvmRoy5qzNHGdm\n" +
                "Q6AMku8jP123jF2J+wDvF714VtZHNvdCYBGJS+rZ81xtJfHhKtZqRAVtbPertOWZeuRm9V\n" +
                "o+/rEuEzgGYGXNcAAAAbemhhbmdsaW4zM0B6aGFuZ2xpbjMzLmxvY2Fs\n" +
                "-----END OPENSSH PRIVATE KEY-----";

        BigInteger pubExponent = new BigInteger("010001",16);
        BigInteger modulus  = new BigInteger("0098c0b378117cbb8345ee82fe2541fa2e8db0118b8f2" +
                "03d2c55ba9b46b12a06aac4b4d1ef6b03bbf4ab314a6bd6619f0d569230a711745800c88200962" +
                "433f98623c99bdea91c74d84659998f9e8a55f83e3a093141d78c9e47d9a3a0e3d7b38a635f7cc" +
                "6bde2b1e4dc7c8ef76815ec1b4c2b5d374c755d64e3353e5b5ea3ffe205e42c4344539b4bb1654" +
                "03aed0c13f35effd80cba302d0e8a25275f356bd3f4e9bbc4a48c3970bb549ec9b90adbc1ba22f" +
                "b5815f10c9fc801226b28d18174af3189bd7e29bf2d34b013e838f6e41c3296a8913616f997536" +
                "2e1a2ab13616a484739371e7e72c40c125f4c049de912c2c704d6e88432fe8dbc0b82e4550fa089",16);
        BigInteger privExponent  = new BigInteger("117e17cf4b295e8bd4e95064ec3d56fa17cdaee4" +
                "ad7a42a71e16258bfde3d329a12717d77408d4705b5e64a85ebf852850dea2e5aef104d6ea1f41" +
                "13f087672ebe506b221f799bc3dca28c1c4f4a8637cc60770706043a1d321b314ace6cb829c3e1" +
                "7e777fed42f3f36fec2b56e713c7b87c4acb775d0559f54ef69a1e0767d8fc0954ab59a7e5951b" +
                "7fd41ebdb99c51ddf26620a5e1d9a8af405b5e8dd1410c5cc335bbc642b9b54ea12206b8c017a5" +
                "3ba0d3103e0571e8d7b15c0c6292cdc5c5ce1faeac8cf9825469e73162a0706ac4d9a429f4241b" +
                "a0bb5319cf1b24bffc9fb794bda5be5d06f243bfe440c5301853304cb535e4766fadbd4b81b5a94c55",16);
        BigInteger p  = new BigInteger("c9468f6ffb442f95d237ddca4928f2a26a8a7942f7c80f331ab" +
                "43def2c0d6aab2720222e0a3ad6f9f8b3ff8e02290986a1fec93e26928dcffbec666666adf695b" +
                "bd0f72ded8b2c180ca44a123bc8ce7bd586238130ef90bbc3b01909a4b25271c8557ffd0fc639b" +
                "71e1c2b1e889bd0066cd177e5766828ee0a0a4e553135e19f",16);
        BigInteger q  = new BigInteger("c248ca2294d875e01055345f0b5de483ce01bb99f85a488b9e9" +
                "d5638283618de517a4aa9ac836da6320c66807d16fdbdd8da7df4b6bccaf991a32e6accd1c6766" +
                "43a00c92ef233f5db78c5d89fb00ef17bd7856d64736f7426011894bead9f35c6d25f1e12ad66a" +
                "44056d6cf7abb4e5997ae466f55a3efeb12e1338066065cd7",16);
        BigInteger dP = new BigInteger("4bff095fa5c6cc14cca7ed6558944e70c6bb7c27adf8eacc47f" +
                "3f042a32679b51ff3c1141326be4ee9f9c4af30df9fca26d655d3d2aa99430382f1f30e27f727f" +
                "81f618504e0fc882415d025ec4ed8afe7225bbb86e79d8557d0e0d3444d5455c3e78c88d770e80" +
                "fdd1d93a656bc462276e9ec54468df38e555a28d37fde314f", 16);
        BigInteger dQ = new BigInteger("187ee2f57f3cd787dba752861541ce590bf54df8f8c63155613" +
                "74732a76742b56d22751d7581d0a65eb30f8d6db8235ff7627cf508c13efa3e718b9dad9b59617" +
                "dfb8e33cf767c34a86a4ecfa43aa777acfc9c77116e3884357277edf128a4e53bdf345cef4feb9" +
                "c13fc9ef2e3ef8fce5e007c22f7724702a58bbd5998037f5b", 16);
        BigInteger qInv = new BigInteger("70f013d90f8951137314b3238c28b53d447bd9ca5550f7a06" +
                "751a9e7ef5fb11447c6ef76fe270aa1cf341c7c0ba97cce8edfb100fcd582fce45e1e59bb6b771" +
                "1228d11d7e66ec9fe6ba1fe5698d47ab8dd60a46466e83b87d9d871e2ee8374f7a16aaca469bdb" +
                "406422d452db70849476c8adc2bb3acb09f77cdc77abe5f757a", 16);

        String privKeyFormat = "OpenSSH";
        String privKeyType = "ssh-rsa";
        String id = "zhanglin33@zhanglin33.local";


        SSHKeyParser parser = new SSHKeyParser();
        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) parser.privKeyParse(privKeyStr);
        assertEquals(pubExponent,privKey.getPublicExponent());
        assertEquals(modulus,privKey.getModulus());
        assertEquals(privExponent,privKey.getExponent());
        assertEquals(p,privKey.getP());
        assertEquals(q,privKey.getQ());
        assertEquals(dP,privKey.getDP());
        assertEquals(dQ,privKey.getDQ());
        assertEquals(qInv,privKey.getQInv());

        assertEquals(privKeyFormat,parser.getKeyFormat());
        assertEquals(privKeyType,parser.getKeyType());
        assertEquals(id,parser.getIdentity());
    }
}
