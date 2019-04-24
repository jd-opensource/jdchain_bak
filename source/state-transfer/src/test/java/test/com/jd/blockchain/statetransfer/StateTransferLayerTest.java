package test.com.jd.blockchain.statetransfer;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.process.DSProcessManager;
import com.jd.blockchain.utils.codec.Base58Utils;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StateTransferLayerTest {

    private final int[] listenPorts = new int[]{9000, 9010, 9020, 9030};

    private String localIp = "127.0.0.1";

    private int DataSequenceNum = 1;

    private int nodesNum = 4;

    private byte[] idBytes = new byte[20];

    private Random rand = new Random();

    private String[] dataSequenceIds = new String[DataSequenceNum];

    private InetSocketAddress[] remoteNodeIps = new InetSocketAddress[nodesNum];

    private final ExecutorService threadPool = Executors.newFixedThreadPool(8);

    private static LinkedList<DataSequence> dataSequencesPerNode = new LinkedList<>();

    // 假定每个数据序列元素里有四条记录数据
    private byte[][] dsElementDatas = new byte[4][];


    @Before
    public void init() {

        // 产生两个唯一的数据序列Id标识
        for (int i = 0; i < DataSequenceNum; i++) {

            dataSequenceIds[i] = new String();
            rand.nextBytes(idBytes);
            dataSequenceIds[i] = Base58Utils.encode(idBytes);
        }

        // 准备好所有的远端结点，包括监听者
        for (int i = 0; i < nodesNum; i++) {
            remoteNodeIps[i] = new InetSocketAddress(localIp, listenPorts[i]);
        }

        // 为数据序列的每个高度准备好内容，为了方便测试，每个高度的内容设置为一致
        for (int i = 0; i < dsElementDatas.length; i++) {
            rand.nextBytes(idBytes);
            dsElementDatas[i] = idBytes;
        }

        // 为结点准备数据序列
        for (String id : dataSequenceIds) {
            for (int i = 0; i < remoteNodeIps.length; i++) {
               DataSequence dataSequence = new DataSequence(remoteNodeIps[i], id);

                // 为数据序列的0，1，2高度添加内容
                for (int j = 0; j < 3; j++) {
                    dataSequence.addElement(new DataSequenceElement(id, j, dsElementDatas));
                }
                dataSequencesPerNode.addLast(dataSequence);
            }

            // 把其中一个结点的数据序列与其他结点区别开来
            for (int i = 0; i < dataSequencesPerNode.size(); i++) {
                DataSequence dataSequence = dataSequencesPerNode.get(i);
                if (dataSequence.getAddress().getPort() != listenPorts[0]) {
                    // 为数据序列的3,4高度添加内容
                    for (int j = 3; j < 5; j++) {
                        dataSequence.addElement(new DataSequenceElement(id, j, dsElementDatas));
                    }
                }
            }
        }
    }

    // 获得除监听结点之外的其他远端结点
    InetSocketAddress[] getTargetNodesIp(InetSocketAddress listenIp, InetSocketAddress[] remoteNodeIps) {

        InetSocketAddress[] targets = new InetSocketAddress[remoteNodeIps.length - 1];
        int j = 0;

        for (int i = 0; i < remoteNodeIps.length; i++) {
            if ((remoteNodeIps[i].getHostName().equals(listenIp.getHostName())) && (remoteNodeIps[i].getPort() == listenIp.getPort())) {
                continue;
            }
            targets[j++] = new InetSocketAddress(remoteNodeIps[i].getHostName(), remoteNodeIps[i].getPort());
        }

        return targets;

    }

    DataSequence findDataSequence(String id, InetSocketAddress listenNodeAddr) {
        for (DataSequence dataSequence : dataSequencesPerNode) {
            if ((dataSequence.getAddress().getPort() == listenNodeAddr.getPort() && (dataSequence.getAddress().getHostName().equals(listenNodeAddr.getHostName()))
                && (dataSequence.getId().equals(id)))) {
                return dataSequence;
            }
        }
        return null;
    }


    @Test
    public void test() {

        CountDownLatch countDownLatch = new CountDownLatch(nodesNum);

        for (String id : dataSequenceIds) {
            for (int i = 0; i < nodesNum; i++) {
                InetSocketAddress listenNode = remoteNodeIps[i];
                threadPool.execute(() -> {
                    // 创建数据序列处理管理者实例
                    DSProcessManager dsProcessManager = new DSProcessManager();
                    DataSequence currDataSequence = findDataSequence(id, listenNode);
                    DataSequenceInfo dsInfo = currDataSequence.getDSInfo();
                    InetSocketAddress[] targets = getTargetNodesIp(listenNode, remoteNodeIps);
                    dsProcessManager.startDSProcess(dsInfo, listenNode, targets, new DataSequenceWriterImpl(currDataSequence), new DataSequenceReaderImpl(currDataSequence));
                    countDownLatch.countDown();
                });
            }
        }

        // 等待数据序列更新完成
        try {
            Thread.sleep(60000);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
