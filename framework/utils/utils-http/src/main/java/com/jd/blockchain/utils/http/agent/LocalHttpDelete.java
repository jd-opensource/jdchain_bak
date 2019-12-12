package com.jd.blockchain.utils.http.agent;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class LocalHttpDelete extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "DELETE";

    public LocalHttpDelete() {
        super();
    }

    public LocalHttpDelete(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public LocalHttpDelete(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

}
