package com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.CryptoException;

/**
 * @author zhanglin33
 * @title: SSHKeyReader
 * @description: TODO
 * @date 2019-05-20, 15:56
 */
public class SSHKeyReader {

    private byte[] input;
    private int pos = 0;
    private byte[] magic;

    public SSHKeyReader(byte[] magicBytes, byte[] keyBytes) {

        magic = magicBytes;
        input = keyBytes;

        for (int i = 0; i < magic.length; i++) {
            if (magic[i] != keyBytes[i]) {
                throw new CryptoException("Magic bytes are inconsistent!");
            }
        }
        pos += magic.length;
    }

    public SSHKeyReader(byte[] keyBytes) {
        magic = null;
        input = keyBytes;
    }

    public int read32Bits() {
        return (input[pos++] & 0xFF) << 24 |
                 (input[pos++] & 0xFF) << 16 |
                 (input[pos++] & 0xFF) << 8  |
                 (input[pos++] & 0xFF);
    }

    public byte[] readBytes() {
        int count = read32Bits();
        byte[] result = new byte[count];
        System.arraycopy(input, pos, result, 0, result.length);
        pos += count;
        return result;
    }

    public byte[] getMagic() {
        return magic;
    }
}
