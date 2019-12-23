package test.com.jd.blockchain.intgr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;

public class IntegratedContext {

	private HashDigest ledgerHash;

	private Map<Integer, Node> nodes = new HashMap<>();

	public int[] getNodeIds() {
		int[] ids = new int[nodes.size()];
		int i = 0;
		for (Integer id : nodes.keySet()) {
			ids[i] = id.intValue();
			i++;
		}
		Arrays.sort(ids);
		return ids;
	}

	public HashDigest getLedgerHash() {
		return ledgerHash;
	}

	public void setLedgerHash(HashDigest ledgerHash) {
		this.ledgerHash = ledgerHash;
	}

	public Node getNode(int id) {
		return nodes.get(id);
	}

	public void addNode(Node node) {
		nodes.put(node.getId(), node);
	}

	public static class Node {

		private int id;

		private AsymmetricKeypair partiKeyPair;

		// private NetworkAddress consensusAddress;
		private ConsensusSettings consensusSettings;

		private LedgerManager ledgerManager;

		private DbConnectionFactory storageDB;

		private LedgerBindingConfig bindingConfig;

		public Node(int id) {
			this.id = id;
		}

		public ConsensusSettings getConsensusAddress() {
			return consensusSettings;
		}

		public LedgerManager getLedgerManager() {
			return ledgerManager;
		}

		public DbConnectionFactory getStorageDB() {
			return storageDB;
		}

		public AsymmetricKeypair getPartiKeyPair() {
			return partiKeyPair;
		}

		public void setPartiKeyPair(AsymmetricKeypair partiKeyPair) {
			this.partiKeyPair = partiKeyPair;
		}

		public int getId() {
			return id;
		}

		public void setConsensusSettings(ConsensusSettings consensusSettings) {
			this.consensusSettings = consensusSettings;
		}

		public void setLedgerManager(LedgerManager ledgerManager) {
			this.ledgerManager = ledgerManager;
		}

		public void setStorageDB(DbConnectionFactory storageDB) {
			this.storageDB = storageDB;
		}

		public LedgerBindingConfig getBindingConfig() {
			return bindingConfig;
		}

		public void setBindingConfig(LedgerBindingConfig bindingConfig) {
			this.bindingConfig = bindingConfig;
		}

	}

}
