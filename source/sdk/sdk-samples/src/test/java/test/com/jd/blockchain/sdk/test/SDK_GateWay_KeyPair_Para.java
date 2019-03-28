package test.com.jd.blockchain.sdk.test;

import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.tools.keygen.KeyGenCommand;

/**
 * Created by zhangshuang3 on 2018/10/17.
 */
public class SDK_GateWay_KeyPair_Para {
    public static final String PASSWORD = "abc";

    public static final String[] PUB_KEYS = { "endPsK36imXrY66pru6ttZ8dZ3TynWekmdqoM1K7ZRRoRBBiYVzM",
            "endPsK36jQE1uYpdVRSnwQXVYhgAMWTaMJiAqii7URiULoBDLUUN",
            "endPsK36fc7FSecKAJCJdFhTejbPHMLaGcihJVQCv95czCq4tW5n",
            "endPsK36m1grx8mkTMgh8XQHiiaNzajdC5hkuqP6pAuLmMbYkzd4" };

    public static final String[] PRIV_KEYS = {
            "177gjsuHdbf3PU68Sm1ZU2aMcyB7sLWj94xwBUoUKvTgHq7qGUfg6ynDB62hocYYXSRXD4X",
            "177gjwQwTdXthkutDKVgKwiq6wWfLWYuxhji1U2N1C5MzqLRWCLZXo3i2g4vpfcEAQUPG8H",
            "177gjvLHUjxvAWsqVcGgV8eHgVNBvJZYDfpP9FLjTouR1gEJNiamYu1qjTNDh18XWyLg8or",
            "177gk2VtYeGbK5TS2xWhbSZA4BsT9Xj5Fb8hqCzxzgbojVVcqaDSFFrFPsLbZBx7rszyCNy" };

    public static PrivKey privkey0 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
    public static PrivKey privkey1 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
    public static PrivKey privkey2 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
    public static PrivKey privkey3 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);

    public static PubKey pubKey0 = KeyGenCommand.decodePubKey(PUB_KEYS[0]);
    public static PubKey pubKey1 = KeyGenCommand.decodePubKey(PUB_KEYS[1]);
    public static PubKey pubKey2 = KeyGenCommand.decodePubKey(PUB_KEYS[2]);
    public static PubKey pubKey3 = KeyGenCommand.decodePubKey(PUB_KEYS[3]);

}
