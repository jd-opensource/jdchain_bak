package com.jd.blockchain.tools.initializer.web;

import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitProposal;
import com.jd.blockchain.utils.http.HttpAction;
import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.HttpService;
import com.jd.blockchain.utils.http.PathParam;
import com.jd.blockchain.utils.http.RequestBody;

@HttpService
public interface LedgerInitConsensusService {

	/**
	 * 请求账本的初始化许可；
	 * 
	 * @param requesterId
	 *            发起请求的参与者 id；
	 * @param signature
	 *            请求者的私钥对 “id” + “账本种子” 做出的签名；只有签名合法且参与者是初始化配置中的参与方才能获得有效返回，否则将被拒绝；
	 */
	@HttpAction(path = "/legerinit/permission/{requesterId}", method = HttpMethod.POST, contentType = LedgerInitMessageConverter.CONTENT_TYPE_VALUE, responseConverter = PermissionResponseConverter.class)
	LedgerInitProposal requestPermission(@PathParam(name = "requesterId") int requesterId,
			@RequestBody(converter = SignatureDigestRequestBodyConverter.class) SignatureDigest signature);

	/**
	 * 同步账本初始化决议；
	 * 
	 * @param initDecision
	 *            调用者的账本初始化决议；
	 * @return 目标参与方的账本初始化决议；如果目标参与者尚未准备就绪, 则返回 null；
	 */
	@HttpAction(path = "/legerinit/decision", method = HttpMethod.POST, contentType = LedgerInitMessageConverter.CONTENT_TYPE_VALUE, responseConverter = DecisionResponseConverter.class)
	LedgerInitDecision synchronizeDecision(@RequestBody(converter = DecisionRequestBodyConverter.class) LedgerInitDecision initDecision);

}
