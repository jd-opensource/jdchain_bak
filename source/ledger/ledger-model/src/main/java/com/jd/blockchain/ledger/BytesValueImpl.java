package com.jd.blockchain.ledger;

import com.jd.blockchain.utils.io.BytesSlice;

/**
 * Created by zhangshuang3 on 2018/12/3.
 */
public class BytesValueImpl implements BytesValue{
    BytesValueType type;
    BytesSlice slice;

    public BytesValueImpl(BytesValueType type, byte[] bytes) {
        this.type = type;
        this.slice = new BytesSlice(bytes);
    }

    @Override
    public BytesValueType getType() {
        return this.type;
    }

    public void setType(BytesValueType type) {
        this.type = type;
    }

    @Override
    public BytesSlice getValue() {
        return this.slice;
    }

}
