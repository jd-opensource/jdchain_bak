package test.com.jd.blockchain.binaryproto;

/**
 * Created by zhangshuang3 on 2018/11/29.
 */
public class SubOperationImpl implements SubOperation {
    String userName;

    @Override
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
