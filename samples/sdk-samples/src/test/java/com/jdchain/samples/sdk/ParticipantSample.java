package com.jdchain.samples.sdk;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;

import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.httpservice.converters.JsonResponseConverter;
import com.jd.httpservice.utils.web.WebResponse;

import utils.codec.Base58Utils;
import utils.crypto.classic.SHA256Utils;
import utils.security.ShaUtils;

/**
 * 参与方节点相关操作示例：
 * 注册/激活/移除参与方操作
 * <p>
 * 本样例无法直接运行，请用户务必参照共识节点相关操作文档步骤完成各种前置操作，然后根据实际配置修改本样例各方法内参数
 */
public class ParticipantSample extends SampleBase {

    // 注册参与方
    @Test
    public void registerParticipant() {
        // 新建交易
        TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
        // 生成用户信息
        BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
        String pwd = Base58Utils.encode(SHA256Utils.hash("1".getBytes()));
        System.out.println("参与方私钥：" + KeyGenUtils.encodePrivKey(user.getPrivKey(), pwd));
        System.out.println("参与方私钥密码：" + pwd);
        System.out.println("参与方公钥：" + KeyGenUtils.encodePubKey(user.getPubKey()));
        System.out.println("参与方地址：" + user.getAddress());
        // 注册参与方
        txTemp.participants().register("new peer node", user.getIdentity());
        // 交易准备
        PreparedTransaction ptx = txTemp.prepare();
        // 提交交易
        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

    /**
     * 激活参与方
     * 执行前请确保新节点已注册，且已经参照共识节点相关操作文档创建并启动新节点！！！
     * 然后根据实际情况修改请求参数
     *
     * @throws Exception
     */
    @Test
    public void activeParticipant() throws Exception {
        // 新节点API服务IP和端口
        String newNodeIp = "127.0.0.1";
        String newNodeApiPort = "12040";
        // 账本信息
        String ledgerHash = ledger.toString();
        // 新节点共识配置
        String newNodeConsensusHost = "127.0.0.1";
        String newNodeConsensusPot = "8950";
        // 区块高度最新的节点API服务IP和端口，用于区块同步
        String syncNodeHost = "127.0.0.1";
        String syncNodePort = "12000";

        // 发送POST请求执行节点激活操作
        HttpPost httpPost = new HttpPost(String.format("http://%s:%s/management/delegate/activeparticipant", newNodeIp, newNodeApiPort));
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ledgerHash", ledgerHash));
        params.add(new BasicNameValuePair("consensusHost", newNodeConsensusHost));
        params.add(new BasicNameValuePair("consensusPort", newNodeConsensusPot));
        params.add(new BasicNameValuePair("remoteManageHost", syncNodeHost));
        params.add(new BasicNameValuePair("remoteManagePort", syncNodePort));
        params.add(new BasicNameValuePair("shutdown", "false"));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpPost);
        JsonResponseConverter jsonConverter = new JsonResponseConverter(WebResponse.class);

        WebResponse webResponse = (WebResponse) jsonConverter.getResponse(null, response.getEntity().getContent(), null);
        Assert.assertTrue(webResponse.isSuccess());
    }

    /**
     * 移除参与方
     * 执行前请确保新节点已启动且出于参与共识状态！！！
     * 然后根据实际情况修改请求参数
     *
     * @throws Exception
     */
    @Test
    public void removeParticipant() throws Exception {

        // 待移除节点API服务IP和端口
        String nodeIp = "127.0.0.1";
        String nodeApiPort = "12030";
        // 账本信息
        String ledgerHash = ledger.toString();
        // 待移除节点地址
        String participantAddress = "LdeNekdXMHqyz9Qxc2jDSBnkvvZLbty6pRDdP";
        // 区块高度最新的节点API服务IP和端口，用于区块同步
        String syncNodeHost = "127.0.0.1";
        String syncNodePort = "12000";

        // 发送POST请求执行节点移除操作
        HttpPost httpPost = new HttpPost(String.format("http://%s:%s/management/delegate/deactiveparticipant", nodeIp, nodeApiPort));
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ledgerHash", ledgerHash));
        params.add(new BasicNameValuePair("participantAddress", participantAddress));
        params.add(new BasicNameValuePair("remoteManageHost", syncNodeHost));
        params.add(new BasicNameValuePair("remoteManagePort", syncNodePort));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpPost);
        JsonResponseConverter jsonConverter = new JsonResponseConverter(WebResponse.class);

        WebResponse webResponse = (WebResponse) jsonConverter.getResponse(null, response.getEntity().getContent(), null);
        Assert.assertTrue(webResponse.isSuccess());
    }

}
