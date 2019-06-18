package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BytesValue;

interface OperationReturnValueHandler {

	int getOperationIndex();

	Object setReturnValue(BytesValue bytesValue);

}
