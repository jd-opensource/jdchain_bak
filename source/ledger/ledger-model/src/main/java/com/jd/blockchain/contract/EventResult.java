package com.jd.blockchain.contract;

import com.jd.blockchain.utils.IllegalDataException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class EventResult<T> {

    private static final long MAX_SECONDS = 30;

    private CompletableFuture<T> data = new CompletableFuture<>();

    private int opIndex;

    public EventResult() {
    }

    public EventResult(int opIndex) {
        this.opIndex = opIndex;
    }

    public void done(T value) {
        data.complete(value);
    }

    public int opIndex() {
        return this.opIndex;
    }

    public T get() {
        try {
            // 防止长时间阻塞
            return data.get(MAX_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalDataException(e.getMessage());
        }
    }
}
