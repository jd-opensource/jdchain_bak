package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Int8Code;

/**
 * 账本相关的权限，这些权限属于全局性的；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.ENUM_LEDGER_PERMISSION)
public enum LedgerPermission implements Int8Code{

	/**
	 * 配置角色的权限；<br>
	 */
	CONFIGURE_ROLES((byte) 0x01),

	/**
	 * 授权用户角色；<br>
	 */
	AUTHORIZE_USER_ROLES((byte) 0x02),

	/**
	 * 设置共识协议；<br>
	 */
	SET_CONSENSUS((byte) 0x03),

	/**
	 * 设置密码体系；<br>
	 */
	SET_CRYPTO((byte) 0x04),

	/**
	 * 注册参与方；<br>
	 */
	REGISTER_PARTICIPANT((byte) 0x05),

	/**
	 * 注册用户；<br>
	 * 
	 * 如果不具备此项权限，则无法注册用户；
	 */
	REGISTER_USER((byte) 0x06),

	/**
	 * 注册数据账户；<br>
	 */
	REGISTER_DATA_ACCOUNT((byte) 0x07),

	/**
	 * 注册合约；<br>
	 */
	REGISTER_CONTRACT((byte) 0x08),

	/**
	 * 升级合约
	 */
	UPGRADE_CONTRACT((byte) 0x14),

	/**
	 * 设置用户属性；<br>
	 */
	SET_USER_ATTRIBUTES((byte) 0x09),

	/**
	 * 写入数据账户；<br>
	 */
	WRITE_DATA_ACCOUNT((byte) 0x0A),

	/**
	 * 参与方核准交易；<br>
	 * 
	 * 如果不具备此项权限，则无法作为节点签署由终端提交的交易；
	 * <p>
	 * 只对交易请求的节点签名列表{@link TransactionRequest#getNodeSignatures()}的用户产生影响；
	 */
	APPROVE_TX((byte) 0x0B),

	/**
	 * 参与方共识交易；<br>
	 * 
	 * 如果不具备此项权限，则无法作为共识节点接入并对交易进行共识；
	 */
	CONSENSUS_TX((byte) 0x0C);

	@EnumField(type = PrimitiveType.INT8)
	public final byte CODE;

	private LedgerPermission(byte code) {
		this.CODE = code;
	}

	@Override
	public byte getCode() {
		return CODE;
	}

}
