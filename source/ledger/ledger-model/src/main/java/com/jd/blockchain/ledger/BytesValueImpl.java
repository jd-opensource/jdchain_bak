package com.jd.blockchain.ledger;

import com.jd.blockchain.utils.io.BytesSlice;

/**
 * Created by zhangshuang3 on 2018/12/3.
 */
public class BytesValueImpl implements BytesValue{
    DataType type;
    BytesSlice slice;

    public BytesValueImpl(DataType type, byte[] bytes) {
        this.type = type;
        this.slice = new BytesSlice(bytes);
    }

    @Override
    public DataType getType() {
        return this.type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    @Override
    public BytesSlice getValue() {
        return this.slice;
    }

}
