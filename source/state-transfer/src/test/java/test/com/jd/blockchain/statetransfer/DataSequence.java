package test.com.jd.blockchain.statetransfer;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;

import java.net.InetSocketAddress;
import java.util.LinkedList;

public class DataSequence {

    private InetSocketAddress address;
    private String id;

    private LinkedList<DataSequenceElement> dataSequenceElements = new LinkedList<>();


    public DataSequence(InetSocketAddress address, String id) {
        this.address = address;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void addElements(DataSequenceElement[] elements) {
        for (DataSequenceElement element : elements) {
            addElement(element);
        }
    }

    public void addElement(DataSequenceElement element) {
        try {
            if (dataSequenceElements.size() == 0) {
                if (element.getHeight() != 0) {
                    throw new IllegalArgumentException("Data sequence add element height error!");
                }
                dataSequenceElements.addLast(element);
            }
            else {
                if (dataSequenceElements.getLast().getHeight() != element.getHeight() - 1) {
                    throw new IllegalArgumentException("Data sequence add element height error!");
                }
                dataSequenceElements.addLast(element);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public LinkedList<DataSequenceElement> getDataSequenceElements() {
        return dataSequenceElements;
    }

    public DataSequenceInfo getDSInfo() {
        if (dataSequenceElements.size() == 0) {
            return new DataSequenceInfo(id, -1);
        }
        else {
            return new DataSequenceInfo(id, dataSequenceElements.getLast().getHeight());
        }
    }

}
