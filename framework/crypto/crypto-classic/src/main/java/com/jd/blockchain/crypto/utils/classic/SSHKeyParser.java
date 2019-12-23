package com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.CryptoException;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;

import static java.math.BigInteger.ONE;

/**
 * @author zhanglin33
 * @title: SSHKeyParser
 * @description: a parser for parsing asymmetric keys in Base64 format
 * @date 2019-05-17, 17:52
 */
public class SSHKeyParser {

    private String keyFormat;
    private String keyType;
    private String identity;

    public AsymmetricKeyParameter pubKeyParse(String pubKeyStr) {

        byte[] pubKeyBytes;
        pubKeyStr = pubKeyStr.replaceAll("\\n", "");

        if (pubKeyStr.startsWith("ssh") || pubKeyStr.startsWith("ecdsa")) {
            String[] algoAndKeyAndLocal = pubKeyStr.split(" ");
            pubKeyBytes = Base64.decode(algoAndKeyAndLocal[1]);
            identity = algoAndKeyAndLocal[2];
        } else {
            pubKeyBytes = Base64.decode(pubKeyStr);
        }

        OpenSSHPublicKeySpec pubKeySpec = new OpenSSHPublicKeySpec(pubKeyBytes);

        keyFormat = pubKeySpec.getFormat();
        keyType = pubKeySpec.getType();

        return OpenSSHPublicKeyUtil.parsePublicKey(pubKeyBytes);
    }

    public AsymmetricKeyParameter privKeyParse(String privKeyStr) {

        byte[] privKeyBytes;
        try {
            privKeyBytes = new PemReader(new StringReader(privKeyStr)).readPemObject().getContent();
        } catch (IOException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        OpenSSHPrivateKeySpec privKeySpec = new OpenSSHPrivateKeySpec(privKeyBytes);
        keyFormat = privKeySpec.getFormat();

        if (!keyFormat.equals("OpenSSH")) {
            return OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(privKeyBytes);
        }
        else
        {
            byte[] AUTH_MAGIC = Strings.toByteArray("openssh-key-v1\0");
            SSHKeyReader keyReader = new SSHKeyReader(AUTH_MAGIC, privKeyBytes);

            byte[] buffer = keyReader.readBytes();
            String cipherName = Strings.fromByteArray(buffer);
            if (!cipherName.equals("none"))
            {
                throw new CryptoException("encrypted keys are not supported!");
            }

            String kdfName = Strings.fromByteArray(keyReader.readBytes());
            if (!kdfName.equals("none"))
            {
                throw new CryptoException("KDFs are not supported!");
            }

            int kdfLength = keyReader.read32Bits();
            if (kdfLength != 0) {
                throw new CryptoException("KDF's length should be 0!");
            }

            int keysNum = keyReader.read32Bits();
            if (keysNum != 1) {
                throw new CryptoException("Number of keys should be 1!");
            }

            byte[] pubKeyBytes = keyReader.readBytes();
            OpenSSHPublicKeySpec pubKeySpec = new OpenSSHPublicKeySpec(pubKeyBytes);
            keyType = pubKeySpec.getType();

            byte[] privKeyWithCmt = keyReader.readBytes();
            SSHKeyReader privKeyReader = new SSHKeyReader(privKeyWithCmt);

            int rnd1 = privKeyReader.read32Bits();
            int rnd2 = privKeyReader.read32Bits();
            if (rnd1 != rnd2) {
                throw new CryptoException("Two random values for checking are not same!");
            }

            String privKeyType = Strings.fromByteArray(privKeyReader.readBytes());
            if (!privKeyType.equals(keyType)) {
                throw new CryptoException("Two key types in public/private keys are not same!");
            }

            AsymmetricKeyParameter result = null;

            switch (privKeyType) {

                case "ssh-rsa": {
                    BigInteger n = new BigInteger(1, privKeyReader.readBytes());
                    BigInteger e = new BigInteger(1, privKeyReader.readBytes());
                    BigInteger d = new BigInteger(1, privKeyReader.readBytes());
                    BigInteger qInv = new BigInteger(1, privKeyReader.readBytes());
                    BigInteger p = new BigInteger(1, privKeyReader.readBytes());
                    BigInteger q = new BigInteger(1, privKeyReader.readBytes());

                    BigInteger dP = d.remainder(p.subtract(ONE));
                    BigInteger dQ = d.remainder(q.subtract(ONE));

                    result = new RSAPrivateCrtKeyParameters(n, e, d, p, q, dP, dQ, qInv);
                    break;
                }

                case "ssh-dss": {
                    BigInteger p = new BigInteger(1, privKeyReader.readBytes());
                    BigInteger q = new BigInteger(1, privKeyReader.readBytes());
                    BigInteger g = new BigInteger(1, privKeyReader.readBytes());
                    privKeyReader.readBytes(); // y
                    BigInteger x = new BigInteger(1, privKeyReader.readBytes());

                    result = new DSAPrivateKeyParameters(x, new DSAParameters(p, q, g));
                    break;
                }

                case "ecdsa-sha2-nistp256": {
                    privKeyReader.readBytes(); // nistp256
                    privKeyReader.readBytes(); // Q
                    BigInteger d = new BigInteger(1, privKeyReader.readBytes());
                    X9ECParameters x9Params = SECNamedCurves.getByName("secp256r1");
                    result = new ECPrivateKeyParameters(d, new ECDomainParameters(
                                    x9Params.getCurve(),
                                    x9Params.getG(),
                                    x9Params.getN(),
                                    x9Params.getH(),
                                    x9Params.getSeed()));
                    break;
                }

                case "ssh-ed25519": {
                    privKeyReader.readBytes(); // A
                    byte[] key = privKeyReader.readBytes();
                    result = new Ed25519PrivateKeyParameters(key, 0);
                    break;
                }
            }
            identity = Strings.fromByteArray(privKeyReader.readBytes());
            return result;
        }
    }

    public String getKeyFormat() {
        return keyFormat;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getIdentity() {
        return identity;
    }
}
