package com.jd.blockchain.ledger.core.impl.handles;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataAccountKVSetOperation.KVWriteEntry;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;
import com.jd.blockchain.utils.Bytes;
import org.springframework.stereotype.Service;

@Service
public class DataAccountKVSetOperationHandle implements OperationHandle{
	static {
		DataContractRegistry.register(BytesValue.class);
	}

	@Override
	public void process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
		DataAccountKVSetOperation kvWriteOp = (DataAccountKVSetOperation) op;
		DataAccount account = dataset.getDataAccountSet().getDataAccount(kvWriteOp.getAccountAddress());
		KVWriteEntry[] writeset = kvWriteOp.getWriteSet();
		for (KVWriteEntry kvw : writeset) {
			byte[] value = BinaryProtocol.encode(kvw.getValue(), BytesValue.class);
			account.setBytes(Bytes.fromString(kvw.getKey()), value, kvw.getExpectedVersion());
		}
	}

	@Override
	public boolean support(Class<?> operationType) {
		return DataAccountKVSetOperation.class.isAssignableFrom(operationType);
	}

}
