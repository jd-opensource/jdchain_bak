package test.com.jd.blockchain.binaryproto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshuang3 on 2018/11/30.
 */
public class CompositeDatasImpl implements CompositeDatas{
    private boolean enable;
    private EnumLevel level;
    PrimitiveDatas primitiveDatas;
    private List<Operation> operationList = new ArrayList<Operation>();
    private short age;

    @Override
    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public EnumLevel getLevel() {
        return this.level;
    }

    public void setLevel(EnumLevel level) {
        this.level = level;
    }

    @Override
    public PrimitiveDatas getPrimitive() {
        return this.primitiveDatas;
    }

    public void setPrimitiveDatas(PrimitiveDatas primitiveDatas) {
        this.primitiveDatas = primitiveDatas;
    }

    @Override
    public Operation[] getOperations() {
        return operationList.toArray(new Operation[operationList.size()]);
    }

    public void setOperations(Object[] operations) {
        for (Object operation : operations) {
            Operation op = (Operation)operation;
            addOperation(op);
        }
    }

    public void addOperation(Operation operation) {
        operationList.add(operation);
    }

    @Override
    public short getAge() {
        return this.age;
    }

    public void setAge(short age) {
        this.age = age;
    }

}
