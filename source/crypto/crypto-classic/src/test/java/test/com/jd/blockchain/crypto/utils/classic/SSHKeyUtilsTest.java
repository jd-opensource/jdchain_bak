package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.RSAUtils;
import com.jd.blockchain.crypto.utils.classic.SSHKeyParser;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SSHKeyUtilsTest
 * @description: Tests for methods in SSHKeyUtils
 * @date 2019-05-07, 15:14
 */
public class SSHKeyUtilsTest {

    private static SecureRandom secureRandom = new SecureRandom();
    private static SSHKeyParser parser = new SSHKeyParser();

    private static final String rsaPubKeyWithHead = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCYwLN4EXy7g0Xugv4lQfou" +
            "jbARi48gPSxVuptGsSoGqsS00e9rA7v0qzFKa9Zhnw1WkjCnEXRYAMiCAJYkM/mGI8mb3qkcdNhGWZm" +
            "PnopV+D46CTFB14yeR9mjoOPXs4pjX3zGveKx5Nx8jvdoFewbTCtdN0x1XWTjNT5bXqP/4gXkLENEU5" +
            "tLsWVAOu0ME/Ne/9gMujAtDoolJ181a9P06bvEpIw5cLtUnsm5CtvBuiL7WBXxDJ/IASJrKNGBdK8xi" +
            "b1+Kb8tNLAT6Dj25BwylqiRNhb5l1Ni4aKrE2FqSEc5Nx5+csQMEl9MBJ3pEsLHBNbohDL+jbwLguRV" +
            "D6CJ zhanglin33@zhanglin33.local\n";
    private static final String rsaPrivKeyWithHead = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
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

    private static final String dsaPubKeyWithHead = "ssh-dss AAAAB3NzaC1kc3MAAACBAIOpp3qEY9zVOxQFgLS+2sOqXT+lnJVc" +
            "Nr68eAE/iYG02kYKs/BrJYBbr0nb10ERdQv6Yte9tQilpJTMKPhNuTVIHTK2xbV0nfChN4wePY+XJyb" +
            "Ima/m41FnIlfp2ov54ePLsNgY41qYWxuKzxURV67DgPxbMqMZmBV2Ccpb5+t/AAAAFQCcyJjOhDH3Ck" +
            "Lbs9dHx/gUtmsh0wAAAIB3MyVR817NTbaRA4pO0AVnk4jC7JKVeKDlZaBBLt1CzyMKY7OzNgioqZmBX" +
            "tffkHXBXxd75AdiBLeurmAPLKsZ50O+lRQI8QwGL5ne0dkGP+sRFdqTEA2BMjWyvlx6xwJD14/cKpgg" +
            "fLt+FBt5nhwmbewxt9uWXENfzD/84Z7aOQAAAIA0DOAIv6SqSEPOyzPMHVWGGiqNFFgQ0gOH7BhrLD/" +
            "hNqFz5vuKQd5rhKCVXxzANTQ8WrINMIk3aYIRgp96oxI3xsL1w7rYODoQmkx4JFHfvU/Sls40r1h09D" +
            "OWSbqE99mo41RIZMauVFlYlhYnusziX1QyGAabHMcz73Y19vrKCg== zhanglin33@JRMVP10WHTD5";
    private static final String dsaPrivKeyWithHead = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
            "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABsQAAAAdzc2gtZH\n" +
            "NzAAAAgQCDqad6hGPc1TsUBYC0vtrDql0/pZyVXDa+vHgBP4mBtNpGCrPwayWAW69J29dB\n" +
            "EXUL+mLXvbUIpaSUzCj4Tbk1SB0ytsW1dJ3woTeMHj2PlycmyJmv5uNRZyJX6dqL+eHjy7\n" +
            "DYGONamFsbis8VEVeuw4D8WzKjGZgVdgnKW+frfwAAABUAnMiYzoQx9wpC27PXR8f4FLZr\n" +
            "IdMAAACAdzMlUfNezU22kQOKTtAFZ5OIwuySlXig5WWgQS7dQs8jCmOzszYIqKmZgV7X35\n" +
            "B1wV8Xe+QHYgS3rq5gDyyrGedDvpUUCPEMBi+Z3tHZBj/rERXakxANgTI1sr5cescCQ9eP\n" +
            "3CqYIHy7fhQbeZ4cJm3sMbfbllxDX8w//OGe2jkAAACANAzgCL+kqkhDzsszzB1VhhoqjR\n" +
            "RYENIDh+wYayw/4Tahc+b7ikHea4SglV8cwDU0PFqyDTCJN2mCEYKfeqMSN8bC9cO62Dg6\n" +
            "EJpMeCRR371P0pbONK9YdPQzlkm6hPfZqONUSGTGrlRZWJYWJ7rM4l9UMhgGmxzHM+92Nf\n" +
            "b6ygoAAAHwI0abUyNGm1MAAAAHc3NoLWRzcwAAAIEAg6mneoRj3NU7FAWAtL7aw6pdP6Wc\n" +
            "lVw2vrx4AT+JgbTaRgqz8GslgFuvSdvXQRF1C/pi1721CKWklMwo+E25NUgdMrbFtXSd8K\n" +
            "E3jB49j5cnJsiZr+bjUWciV+nai/nh48uw2BjjWphbG4rPFRFXrsOA/FsyoxmYFXYJylvn\n" +
            "638AAAAVAJzImM6EMfcKQtuz10fH+BS2ayHTAAAAgHczJVHzXs1NtpEDik7QBWeTiMLskp\n" +
            "V4oOVloEEu3ULPIwpjs7M2CKipmYFe19+QdcFfF3vkB2IEt66uYA8sqxnnQ76VFAjxDAYv\n" +
            "md7R2QY/6xEV2pMQDYEyNbK+XHrHAkPXj9wqmCB8u34UG3meHCZt7DG325ZcQ1/MP/zhnt\n" +
            "o5AAAAgDQM4Ai/pKpIQ87LM8wdVYYaKo0UWBDSA4fsGGssP+E2oXPm+4pB3muEoJVfHMA1\n" +
            "NDxasg0wiTdpghGCn3qjEjfGwvXDutg4OhCaTHgkUd+9T9KWzjSvWHT0M5ZJuoT32ajjVE\n" +
            "hkxq5UWViWFie6zOJfVDIYBpscxzPvdjX2+soKAAAAFEJXzZu8UDkISU8DCj/KY7Fq31R8\n" +
            "AAAAF3poYW5nbGluMzNASlJNVlAxMFdIVEQ1AQIDBA==\n" +
            "-----END OPENSSH PRIVATE KEY-----\n";

    private static final String ecdsaPubKeyWithHead = "ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbml" +
            "zdHAyNTYAAABBBEi5T13AMBMBthe7a6GwSoK5JK8mVEMXztIA6kUGRuNefoRtY0lPx3kmeIW4GGFVK" +
            "ct+Kcc6vtNKuUhn8fVqEZU= zhanglin33@JRMVP10WHTD5";
    private static final String ecdsaPrivKeyWithHead = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
            "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAaAAAABNlY2RzYS\n" +
            "1zaGEyLW5pc3RwMjU2AAAACG5pc3RwMjU2AAAAQQRIuU9dwDATAbYXu2uhsEqCuSSvJlRD\n" +
            "F87SAOpFBkbjXn6EbWNJT8d5JniFuBhhVSnLfinHOr7TSrlIZ/H1ahGVAAAAsGEuQiRhLk\n" +
            "IkAAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBEi5T13AMBMBthe7\n" +
            "a6GwSoK5JK8mVEMXztIA6kUGRuNefoRtY0lPx3kmeIW4GGFVKct+Kcc6vtNKuUhn8fVqEZ\n" +
            "UAAAAhAPEgd42vUGT8APac9sNgj7zIkE4m9/r8+7tATePBXucRAAAAF3poYW5nbGluMzNA\n" +
            "SlJNVlAxMFdIVEQ1\n" +
            "-----END OPENSSH PRIVATE KEY-----";

    private static final String ed25519PubKeyWithHead = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAICWVU6JcJ3k/ZM9FxKY5h" +
            "kxWSi1EEZaYwCChiJ00wwps zhanglin33@JRMVP10WHTD5";
    private static final String ed25519PrivKeyWithHead = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
            "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAMwAAAAtzc2gtZW\n" +
            "QyNTUxOQAAACAllVOiXCd5P2TPRcSmOYZMVkotRBGWmMAgoYidNMMKbAAAAKB9rg8cfa4P\n" +
            "HAAAAAtzc2gtZWQyNTUxOQAAACAllVOiXCd5P2TPRcSmOYZMVkotRBGWmMAgoYidNMMKbA\n" +
            "AAAEBK22Wm3Hb4mV3qKkAZ/eG2y7go3Wc0xMbCFrqfvZJghiWVU6JcJ3k/ZM9FxKY5hkxW\n" +
            "Si1EEZaYwCChiJ00wwpsAAAAF3poYW5nbGluMzNASlJNVlAxMFdIVEQ1AQIDBAUG\n" +
            "-----END OPENSSH PRIVATE KEY-----";


    @Test
    public void parseRSAPublicKeyTest() {

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

        RSAKeyParameters pubKey = (RSAKeyParameters) parser.pubKeyParse(rsaPubKeyWithHead);
        BigInteger e = pubKey.getExponent();
        BigInteger n = pubKey.getModulus();
        assertEquals(exponent,e);
        assertEquals(modulus,n);

        assertEquals(pubKeyFormat,parser.getKeyFormat());
        assertEquals(pubKeyType,parser.getKeyType());
        assertEquals(id, parser.getIdentity());
    }

    @Test
    public void parseRSAPrivateKeyTest() {

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

        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) parser.privKeyParse(rsaPrivKeyWithHead);
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

    @Test
    public void parseDSAPublicKeyTest() {

        BigInteger p = new BigInteger("83a9a77a8463dcd53b140580b4bedac3aa5d3fa59c955c36bebc" +
                "78013f8981b4da460ab3f06b25805baf49dbd74111750bfa62d7bdb508a5a494cc28f84db93548" +
                "1d32b6c5b5749df0a1378c1e3d8f972726c899afe6e351672257e9da8bf9e1e3cbb0d818e35a98" +
                "5b1b8acf151157aec380fc5b32a31998157609ca5be7eb7f", 16);
        BigInteger q = new BigInteger("9cc898ce8431f70a42dbb3d747c7f814b66b21d3", 16);
        BigInteger g = new BigInteger("77332551f35ecd4db691038a4ed005679388c2ec929578a0e565" +
                "a0412edd42cf230a63b3b33608a8a999815ed7df9075c15f177be4076204b7aeae600f2cab19e7" +
                "43be951408f10c062f99ded1d9063feb1115da93100d813235b2be5c7ac70243d78fdc2a98207c" +
                "bb7e141b799e1c266dec31b7db965c435fcc3ffce19eda39", 16);
        BigInteger y = new BigInteger("340ce008bfa4aa4843cecb33cc1d55861a2a8d145810d20387ec" +
                "186b2c3fe136a173e6fb8a41de6b84a0955f1cc035343c5ab20d308937698211829f7aa31237c6" +
                "c2f5c3bad8383a109a4c782451dfbd4fd296ce34af5874f4339649ba84f7d9a8e3544864c6ae54" +
                "5958961627bacce25f543218069b1cc733ef7635f6faca0a", 16);

        String pubKeyFormat = "OpenSSH";
        String pubKeyType = "ssh-dss";
        String id = "zhanglin33@JRMVP10WHTD5";

        DSAPublicKeyParameters pubKey = (DSAPublicKeyParameters) parser.pubKeyParse(dsaPubKeyWithHead);
        DSAParameters parameters = pubKey.getParameters();
        assertEquals(p, parameters.getP());
        assertEquals(q, parameters.getQ());
        assertEquals(g, parameters.getG());
        assertEquals(y, pubKey.getY());

        assertEquals(pubKeyFormat,parser.getKeyFormat());
        assertEquals(pubKeyType,parser.getKeyType());
        assertEquals(id, parser.getIdentity());
    }

    @Test
    public void parseDSAPrivateKeyTest() {

        BigInteger p = new BigInteger("83a9a77a8463dcd53b140580b4bedac3aa5d3fa59c955c36bebc" +
                "78013f8981b4da460ab3f06b25805baf49dbd74111750bfa62d7bdb508a5a494cc28f84db93548" +
                "1d32b6c5b5749df0a1378c1e3d8f972726c899afe6e351672257e9da8bf9e1e3cbb0d818e35a98" +
                "5b1b8acf151157aec380fc5b32a31998157609ca5be7eb7f", 16);
        BigInteger q = new BigInteger("9cc898ce8431f70a42dbb3d747c7f814b66b21d3", 16);
        BigInteger g = new BigInteger("77332551f35ecd4db691038a4ed005679388c2ec929578a0e565" +
                "a0412edd42cf230a63b3b33608a8a999815ed7df9075c15f177be4076204b7aeae600f2cab19e7" +
                "43be951408f10c062f99ded1d9063feb1115da93100d813235b2be5c7ac70243d78fdc2a98207c" +
                "bb7e141b799e1c266dec31b7db965c435fcc3ffce19eda39", 16);
        BigInteger y = new BigInteger("340ce008bfa4aa4843cecb33cc1d55861a2a8d145810d20387ec" +
                "186b2c3fe136a173e6fb8a41de6b84a0955f1cc035343c5ab20d308937698211829f7aa31237c6" +
                "c2f5c3bad8383a109a4c782451dfbd4fd296ce34af5874f4339649ba84f7d9a8e3544864c6ae54" +
                "5958961627bacce25f543218069b1cc733ef7635f6faca0a", 16);

        BigInteger x = new BigInteger("4257cd9bbc503908494f030a3fca63b16adf547c", 16);

        String privKeyFormat = "OpenSSH";
        String privKeyType = "ssh-dss";
        String id = "zhanglin33@JRMVP10WHTD5";

        DSAPrivateKeyParameters privKey = (DSAPrivateKeyParameters) parser.privKeyParse(dsaPrivKeyWithHead);
        assertEquals(x,privKey.getX());
        assertEquals(y,g.modPow(x,p));

        DSAParameters parameters = privKey.getParameters();
        assertEquals(p, parameters.getP());
        assertEquals(q, parameters.getQ());
        assertEquals(g, parameters.getG());
        assertEquals(x, privKey.getX());

        assertEquals(privKeyFormat,parser.getKeyFormat());
        assertEquals(privKeyType,parser.getKeyType());
        assertEquals(id, parser.getIdentity());
    }

    @Test
    public void parseECDSAPublicKeyTest() {

        BigInteger gX = new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16);
        BigInteger gY = new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16);

        BigInteger QX = new BigInteger("48b94f5dc0301301b617bb6ba1b04a82b924af26544317ced200ea450646e35e",16);
        BigInteger QY = new BigInteger("7e846d63494fc779267885b818615529cb7e29c73abed34ab94867f1f56a1195",16);

        String pubKeyFormat = "OpenSSH";
        String pubKeyType = "ecdsa-sha2-nistp256";
        String id = "zhanglin33@JRMVP10WHTD5";

        ECPublicKeyParameters pubKey = (ECPublicKeyParameters) parser.pubKeyParse(ecdsaPubKeyWithHead);
        SecP256R1Curve curve = new SecP256R1Curve();
        ECPoint g = curve.createPoint(gX,gY);
        ECPoint Q = curve.createPoint(QX,QY);
        assertEquals(g,pubKey.getParameters().getG());
        assertEquals(Q,pubKey.getQ());

        assertEquals(pubKeyFormat,parser.getKeyFormat());
        assertEquals(pubKeyType,parser.getKeyType());
        assertEquals(id, parser.getIdentity());
    }

    @Test
    public void parseECDSAPrivateKeyTest() {

        BigInteger gX = new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16);
        BigInteger gY = new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16);

        BigInteger QX = new BigInteger("48b94f5dc0301301b617bb6ba1b04a82b924af26544317ced200ea450646e35e",16);
        BigInteger QY = new BigInteger("7e846d63494fc779267885b818615529cb7e29c73abed34ab94867f1f56a1195",16);
        BigInteger d = new BigInteger("00f120778daf5064fc00f69cf6c3608fbcc8904e26f7fafcfbbb404de3c15ee711",16);

        String privKeyFormat = "OpenSSH";
        String privKeyType = "ecdsa-sha2-nistp256";
        String id = "zhanglin33@JRMVP10WHTD5";

        ECPrivateKeyParameters privKey = (ECPrivateKeyParameters) parser.privKeyParse(ecdsaPrivKeyWithHead);

        SecP256R1Curve curve = new SecP256R1Curve();
        ECPoint g = curve.createPoint(gX,gY);
        ECPoint Q = curve.createPoint(QX,QY);
        assertEquals(g,privKey.getParameters().getG());
        assertEquals(d,privKey.getD());
        assertEquals(Q,g.multiply(d));

        assertEquals(privKeyFormat,parser.getKeyFormat());
        assertEquals(privKeyType,parser.getKeyType());
        assertEquals(id, parser.getIdentity());
    }


    @Test
    public void parseED25519PublicKeyTest() {

        byte[] A = Hex.decode("259553a25c27793f64cf45c4a639864c564a2d44119698c020a1889d34c30a6c");

        String pubKeyFormat = "OpenSSH";
        String pubKeyType = "ssh-ed25519";
        String id = "zhanglin33@JRMVP10WHTD5";

        Ed25519PublicKeyParameters pubKey = (Ed25519PublicKeyParameters) parser.pubKeyParse(ed25519PubKeyWithHead);
        assertArrayEquals(A,pubKey.getEncoded());

        assertEquals(pubKeyFormat,parser.getKeyFormat());
        assertEquals(pubKeyType,parser.getKeyType());
        assertEquals(id, parser.getIdentity());
    }

    @Test
    public void parseED25519PrivateKeyTest() {

        byte[] A = Hex.decode("259553a25c27793f64cf45c4a639864c564a2d44119698c020a1889d34c30a6c");

        byte[] s = Hex.decode("4adb65a6dc76f8995dea2a4019fde1b6cbb828dd6734c4c6c216ba9fbd926086");

        String privKeyFormat = "OpenSSH";
        String privKeyType = "ssh-ed25519";
        String id = "zhanglin33@JRMVP10WHTD5";

        Ed25519PrivateKeyParameters privKey = (Ed25519PrivateKeyParameters) parser.privKeyParse(ed25519PrivKeyWithHead);
        assertArrayEquals(s, privKey.getEncoded());
        assertArrayEquals(A, privKey.generatePublicKey().getEncoded());

        assertEquals(privKeyFormat,parser.getKeyFormat());
        assertEquals(privKeyType,parser.getKeyType());
        assertEquals(id, parser.getIdentity());
    }

    @Test
    public void rsaTest() {

        byte[] msg = new byte[128];
        secureRandom.nextBytes(msg);

        byte[] signature = RSAUtils.sign(msg,parser.privKeyParse(rsaPrivKeyWithHead));
        boolean isValid = RSAUtils.verify(msg,parser.pubKeyParse(rsaPubKeyWithHead),signature);
        assertTrue(isValid);
    }

    @Test
    public void dsaTest() {

        byte[] msg = new byte[128];
        secureRandom.nextBytes(msg);

        DSASigner signer = new DSASigner();
        signer.init(true, parser.privKeyParse(dsaPrivKeyWithHead));
        BigInteger[] signature = signer.generateSignature(msg);

        signer.init(false, parser.pubKeyParse(dsaPubKeyWithHead));
        boolean isValid = signer.verifySignature(msg, signature[0], signature[1]);
        assertTrue(isValid);
    }

    @Test
    public void ecdsaTest() {

        byte[] msg = new byte[128];
        secureRandom.nextBytes(msg);

        ECDSASigner signer = new ECDSASigner();
        signer.init(true, parser.privKeyParse(ecdsaPrivKeyWithHead));
        BigInteger[] signature = signer.generateSignature(msg);

        signer.init(false, parser.pubKeyParse(ecdsaPubKeyWithHead));
        boolean isValid = signer.verifySignature(msg, signature[0], signature[1]);
        assertTrue(isValid);
    }

    @Test
    public void ed25519Test() {

        byte[] msg = new byte[128];
        secureRandom.nextBytes(msg);

        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, parser.privKeyParse(ed25519PrivKeyWithHead));
        signer.update(msg, 0, msg.length);
        byte[] signature = signer.generateSignature();

        signer.init(false, parser.pubKeyParse(ed25519PubKeyWithHead));
        signer.update(msg, 0, msg.length);

        boolean isValid = signer.verifySignature(signature);
        assertTrue(isValid);
    }
}
