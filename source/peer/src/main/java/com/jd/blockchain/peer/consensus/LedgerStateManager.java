package com.jd.blockchain.peer.consensus;

import java.io.InputStream;
import java.util.Iterator;

import com.jd.blockchain.consensus.service.StateMachineReplicate;
import com.jd.blockchain.consensus.service.StateSnapshot;
import org.springframework.stereotype.Component;

@Component
public class LedgerStateManager implements StateMachineReplicate{

	@Override
	public long getLatestStateID(String realmName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public StateSnapshot getSnapshot(String realmName, long stateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<StateSnapshot> getSnapshots(String realmName, long fromStateId, long toStateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream readState(String realmName, long stateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setupState(String realmName, StateSnapshot snapshot, InputStream state) {
		// TODO Auto-generated method stub
		
	}

}
