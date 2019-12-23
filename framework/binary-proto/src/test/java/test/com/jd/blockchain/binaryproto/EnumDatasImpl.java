package test.com.jd.blockchain.binaryproto;

/**
 * Created by zhangshuang3 on 2018/11/29.
 */
public class EnumDatasImpl implements EnumDatas {
    private EnumLevel level;

    @Override
    public EnumLevel getLevel() {
        return this.level;
    }
    public void setLevel(EnumLevel level) {
        this.level = level;
    }

}
