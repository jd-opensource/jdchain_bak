package com.jd.blockchain.peer.consensus;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.consensus.service.StateMachineReplicate;
import com.jd.blockchain.consensus.service.StateSnapshot;
import org.springframework.stereotype.Component;

@Component
public class LedgerStateManager implements StateMachineReplicate {

	private final Map<String, StateSnapshot> stateSnapshots = new ConcurrentHashMap<>();

	@Override
	public long getLatestStateID(String realmName) {
		StateSnapshot snapshot = stateSnapshots.get(realmName);
		if (snapshot == null) {
			return -1L;
		}
		return snapshot.getId();
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
		stateSnapshots.put(realmName, snapshot);
	}
}
