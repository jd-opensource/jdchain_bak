package com.jd.blockchain.setting;

/**
 * 网关接入设置；
 * 
 * @author huanghaiquan
 *
 */
public class GatewayIncomingSetting {

	private LedgerIncomingSetting[] ledgers;

	/**
	 * 所有账本的接入设置；
	 * 
	 * @return
	 */
	public LedgerIncomingSetting[] getLedgers() {
		return ledgers;
	}

	public void setLedgers(LedgerIncomingSetting[] ledgerSettings) {
		this.ledgers = ledgerSettings;
	}

}
