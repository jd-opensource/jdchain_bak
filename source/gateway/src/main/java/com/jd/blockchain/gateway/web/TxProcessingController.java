package com.jd.blockchain.gateway.web;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.gateway.service.GatewayInterceptService;
import com.jd.blockchain.transaction.SignatureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.gateway.PeerService;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.utils.BusinessException;
import com.jd.blockchain.web.converters.BinaryMessageConverter;

/**
 * @author huanghaiquan
 *
 */
@RestController
public class TxProcessingController implements TransactionService {

	@Autowired
	private PeerService peerService;

	@Autowired
	private GatewayInterceptService interceptService;

	@RequestMapping(path = "rpc/tx", method = RequestMethod.POST, consumes = BinaryMessageConverter.CONTENT_TYPE_VALUE, produces = BinaryMessageConverter.CONTENT_TYPE_VALUE)
	@Override
	public @ResponseBody TransactionResponse process(@RequestBody TransactionRequest txRequest) {
		// 拦截请求进行校验
		interceptService.intercept(txRequest);

		// 检查交易请求的信息是否完整；
		HashDigest ledgerHash = txRequest.getTransactionContent().getLedgerHash();
		if (ledgerHash == null) {
			// 未指定交易的账本；
			throw new IllegalArgumentException("The TransactionRequest miss ledger hash!");
		}

		// 预期的请求中不应该包含节点签名，首个节点签名应该由当前网关提供；
		if (txRequest.getNodeSignatures() != null && txRequest.getNodeSignatures().length > 0) {
			throw new IllegalArgumentException("Gateway cann't accept TransactionRequest with any NodeSignature!");
		}

		// TODO:检查参与者的签名；
		DigitalSignature[] partiSigns = txRequest.getEndpointSignatures();
		if (partiSigns == null || partiSigns.length == 0) {
			// 缺少参与者签名，则采用检查托管账户并进行托管签名；如果请求未包含托管账户，或者托管账户认证失败，则返回401错误；
			// TODO: 未实现！
			throw new IllegalStateException("Not implemented!");
		} else {
			// 验证签名；
			for (DigitalSignature sign : partiSigns) {
				if (!SignatureUtils.verifySignature(txRequest.getTransactionContent(), sign.getDigest(), sign.getPubKey())) {
					throw new BusinessException("The validation of participant signatures fail!");
				}
			}
		}

		// 注：转发前自动附加网关的签名并转发请求至共识节点；异步的处理方式
		return peerService.getTransactionService().process(txRequest);
	}
}
