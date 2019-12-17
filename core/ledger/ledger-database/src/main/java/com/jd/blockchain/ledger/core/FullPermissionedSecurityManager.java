package com.jd.blockchain.ledger.core;

import java.util.Set;

import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerSecurityException;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.utils.Bytes;

class FullPermissionedSecurityManager implements LedgerSecurityManager {

	public static final FullPermissionedSecurityManager INSTANCE = new FullPermissionedSecurityManager();

	@Override
	public SecurityPolicy createSecurityPolicy(Set<Bytes> endpoints, Set<Bytes> nodes) {
		return new FullPermissionedPolicy(endpoints, nodes);
	}

	private static class FullPermissionedPolicy implements SecurityPolicy {

		private Set<Bytes> endpoints;
		private Set<Bytes> nodes;

		public FullPermissionedPolicy(Set<Bytes> endpoints, Set<Bytes> nodes) {
			this.endpoints = endpoints;
			this.nodes = nodes;
		}

		@Override
		public Set<Bytes> getEndpoints() {
			return endpoints;
		}

		@Override
		public Set<Bytes> getNodes() {
			return nodes;
		}

		@Override
		public boolean isEndpointEnable(LedgerPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isEndpointEnable(TransactionPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isNodeEnable(LedgerPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isNodeEnable(TransactionPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public void checkEndpointPermission(LedgerPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
		}

		@Override
		public void checkEndpointPermission(TransactionPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
		}

		@Override
		public void checkNodePermission(LedgerPermission permission, MultiIDsPolicy midPolicy) throws LedgerSecurityException {
		}

		@Override
		public void checkNodePermission(TransactionPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
		}

		@Override
		public boolean isEndpointValid(MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isNodeValid(MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public void checkEndpointValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException {
		}

		@Override
		public void checkNodeValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException {
		}

	}

}