package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.CryptoFunction;
import com.jd.blockchain.crypto.CryptoService;
import com.jd.blockchain.provider.NamedProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author zhanglin33
 * @title: PKICryptoService
 * @description: TODO
 * @date 2019-05-15, 16:35
 */
@NamedProvider("PKI-SOFTWARE")
public class PKICryptoService implements CryptoService {

    public static final SHA1WITHRSA2048SignatureFunction SHA1WITHRSA2048 = new SHA1WITHRSA2048SignatureFunction();

    public static final SHA1WITHRSA4096SignatureFunction SHA1WITHRSA4096 = new SHA1WITHRSA4096SignatureFunction();

    public static final SM3WITHSM2SignatureFunction SM3WITHSM2 = new SM3WITHSM2SignatureFunction();

    private static final Collection<CryptoFunction> FUNCTIONS;

    static {
        List<CryptoFunction> funcs = Arrays.asList(SHA1WITHRSA2048, SHA1WITHRSA4096, SM3WITHSM2);
        FUNCTIONS = Collections.unmodifiableList(funcs);
    }

    @Override
    public Collection<CryptoFunction> getFunctions() {
        return FUNCTIONS;
    }
}
