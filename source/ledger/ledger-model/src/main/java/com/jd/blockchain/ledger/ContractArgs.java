package com.jd.blockchain.ledger;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;

import java.lang.reflect.Method;

/**
 * contract's args;
 */
@DataContract(code = DataCodes.CONTRACT_ARGS)
public interface ContractArgs  {
	Method getMethod();
	Object[] getArgs();
}
