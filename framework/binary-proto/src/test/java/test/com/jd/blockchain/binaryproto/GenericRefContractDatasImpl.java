package test.com.jd.blockchain.binaryproto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshuang3 on 2018/11/29.
 */
public class GenericRefContractDatasImpl implements GenericRefContractDatas{
    private List<Operation> operationList = new ArrayList<Operation>();

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

}
