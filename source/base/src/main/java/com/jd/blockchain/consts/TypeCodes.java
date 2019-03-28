package com.jd.blockchain.consts;

/**
 * @author huanghaiquan
 *
 */
public interface TypeCodes {

	public static final int BYTES_VALUE = 0x80;

	public static final int BLOCK_CHAIN_IDENTITY = 0x90;

	public static final int BLOCK = 0x100;

	public static final int BLOCK_BODY = 0x110;

	// public static final int BLOCK_LEDGER = 0x110;

	public static final int BLOCK_GENESIS = 0x120;

	public static final int DATA_SNAPSHOT = 0x130;

	public static final int TX = 0x200;

	public static final int TX_LEDGER = 0x201;

	public static final int TX_CONTENT = 0x210;

	public static final int TX_CONTENT_BODY = 0x220;

	public static final int TX_OP = 0x300;

	public static final int TX_OP_LEDGER_INIT = 0x301;

	public static final int TX_OP_USER_REG = 0x310;
	public static final int TX_OP_USER_INFO_SET = 0x311;
	public static final int TX_OP_USER_INFO_SET_KV = 0x312;

	public static final int TX_OP_DATA_ACC_REG = 0x320;
	public static final int TX_OP_DATA_ACC_SET = 0x321;
	public static final int TX_OP_DATA_ACC_SET_KV = 0x322;

	public static final int TX_OP_CONTRACT_DEPLOY = 0x330;
	public static final int TX_OP_CONTRACT_UPDATE = 0x331;

	public static final int TX_OP_CONTRACT_EVENT_SEND = 0x340;

	public static final int TX_RESPONSE = 0x350;

	public static final int METADATA = 0x600;

	public static final int METADATA_INIT_SETTING = 0x610;

	public static final int METADATA_INIT_PERMISSION = 0x611;

	public static final int METADATA_INIT_DECISION = 0x612;

	public static final int METADATA_LEDGER_SETTING = 0x620;

	public static final int METADATA_CONSENSUS_PARTICIPANT = 0x621;

//	public static final int METADATA_CONSENSUS_NODE = 0x630;

	public static final int METADATA_CONSENSUS_SETTING = 0x631;

//	public static final int METADATA_PARTICIPANT_INFO = 0x640;

	public static final int METADATA_CRYPTO_SETTING = 0x642;

	// public static final int ACCOUNT = 0x700;

	public static final int ACCOUNT_HEADER = 0x710;

	public static final int USER = 0x800;

	public static final int DATA = 0x900;

	public static final int CONTRACT = 0xA00;

	public static final int HASH = 0xB00;

	public static final int HASH_OBJECT = 0xB10;

	public static final int ENUM_TYPE = 0xB20;

	public static final int CRYPTO_ALGORITHM = 0xB21;

	public static final int ENUM_TYPE_TRANSACTION_STATE = 0xB22;

    public static final int ENUM_TYPE_DATA_TYPE= 0xB23;

	public static final int DIGITALSIGNATURE = 0xB30;

	public static final int DIGITALSIGNATURE_BODY = 0xB31;

	public static final int CLIENT_IDENTIFICATION = 0xC00;

	public static final int CLIENT_IDENTIFICATIONS = 0xC10;

	public static final int REQUEST = 0xD00;

	public static final int REQUEST_NODE = 0xD10;

	public static final int REQUEST_ENDPOINT = 0xD20;



	// ------------------ 共识相关 ----------------

	public static final int CONSENSUS = 0x1000;

	public static final int CONSENSUS_ACTION_REQUEST = CONSENSUS | 0x01;

	public static final int CONSENSUS_ACTION_RESPONSE = CONSENSUS | 0x02;
	
	
	public static final int CONSENSUS_SETTINGS = CONSENSUS | 0x03;

	public static final int CONSENSUS_NODE_SETTINGS = CONSENSUS | 0x04;
	
	public static final int CONSENSUS_CLI_INCOMING_SETTINGS = CONSENSUS | 0x05;

	// ------------------ 共识相关（BFTSMART） ----------------
	public static final int CONSENSUS_BFTSMART = 0x1100;

	public static final int CONSENSUS_BFTSMART_SETTINGS = CONSENSUS_BFTSMART | 0x01;

	public static final int CONSENSUS_BFTSMART_NODE_SETTINGS = CONSENSUS_BFTSMART | 0x02;
	
	public static final int CONSENSUS_BFTSMART_CLI_INCOMING_SETTINGS = CONSENSUS_BFTSMART | 0x03;

	public static final int CONSENSUS_BFTSMART_BLOCK_SETTINGS = CONSENSUS_BFTSMART | 0x04;

	// ------------------ 共识相关（MSGQUEUE） ----------------
	public static final int CONSENSUS_MSGQUEUE = 0x1200;

	public static final int CONSENSUS_MSGQUEUE_SETTINGS = CONSENSUS_MSGQUEUE | 0x01;

	public static final int CONSENSUS_MSGQUEUE_NODE_SETTINGS = CONSENSUS_MSGQUEUE | 0x02;

	public static final int CONSENSUS_MSGQUEUE_CLI_INCOMING_SETTINGS = CONSENSUS_MSGQUEUE | 0x03;

	public static final int CONSENSUS_MSGQUEUE_NETWORK_SETTINGS = CONSENSUS_MSGQUEUE | 0x04;

	public static final int CONSENSUS_MSGQUEUE_BLOCK_SETTINGS = CONSENSUS_MSGQUEUE | 0x05;

	
}
