package com.jd.blockchain.tools.initializer.web;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerInitException;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.LedgerInitProperties.CryptoProperties;
import com.jd.blockchain.ledger.LedgerInitProperties.ParticipantProperties;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.SecurityInitData;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerSecurityManager;
import com.jd.blockchain.transaction.LedgerInitData;
import com.jd.blockchain.utils.StringUtils;

public class LedgerInitConfiguration {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private static final String DEFAULT_HASH_ALGORITHM = "SHA256";

	private ParticipantProperties[] participants;

	private ConsensusConfig consensusConfiguration;

	private CryptoConfig cryptoConfig;

	private ConsensusConfig consensusConfig;

	private LedgerInitData ledgerSettings;

	private SecurityInitData securitySettings;

	public ParticipantProperties[] getParticipants() {
		return participants;
	}

	public int getParticipantCount() {
		return participants.length;
	}

	/**
	 * @param id
	 * @return
	 */
	public ParticipantProperties getParticipant(int id) {
		// 注：解析的过程确保了参与方列表是升序排列，且列表中第一个参与方的 id 为 0， id 以 1 递增；
		return participants[id];
	}

	public ConsensusConfig getConsensusConfiguration() {
		return consensusConfiguration;
	}

	public CryptoConfig getCryptoConfig() {
		return cryptoConfig;
	}

	public ConsensusConfig getConsensusConfig() {
		return consensusConfig;
	}

	public LedgerInitData getLedgerSettings() {
		return ledgerSettings;
	}

	public SecurityInitData getSecuritySettings() {
		return securitySettings;
	}

	private LedgerInitConfiguration() {
	}

	public void setConsensusSettings(ConsensusProvider consensusProvider, ConsensusSettings consensusSettings) {
		byte[] consensusSettingBytes = encodeConsensusSettings(consensusProvider, consensusSettings);
		ledgerSettings.setConsensusProvider(consensusProvider.getName());
		ledgerSettings.setConsensusSettings(consensusSettingBytes);
	}

	public static LedgerInitConfiguration create(LedgerInitProperties ledgerInitProps) {
		LedgerInitConfiguration ledgerConfig = new LedgerInitConfiguration();

		CryptoConfig cryptoConfig = createCryptoConfig(ledgerInitProps.getCryptoProperties());
		ledgerConfig.cryptoConfig = cryptoConfig;

		ConsensusConfig consensusConfig = createConsensusConfig(ledgerInitProps);
		ledgerConfig.consensusConfig = consensusConfig;

		ParticipantProperties[] participants = resolveParticipants(ledgerInitProps);
		ledgerConfig.participants = participants;

		LedgerInitData ledgerSettings = createLedgerInitSettings(ledgerInitProps, cryptoConfig, consensusConfig,
				participants);
		ledgerConfig.ledgerSettings = ledgerSettings;

		SecurityInitData securitySettings = createSecurityInitSettings(ledgerInitProps, participants);
		ledgerConfig.securitySettings = securitySettings;

		return ledgerConfig;
	}

	private static ConsensusConfig createConsensusConfig(LedgerInitProperties initProps) {
		ConsensusProvider consensusProvider = ConsensusProviders.getProvider(initProps.getConsensusProvider());

		Properties csProps = initProps.getConsensusConfig();
		ConsensusSettings protocolSettings = consensusProvider.getSettingsFactory().getConsensusSettingsBuilder()
				.createSettings(csProps, initProps.getConsensusParticipantNodes());

		ConsensusConfig config = new ConsensusConfig();
		config.setProvider(consensusProvider);
		config.setProtocolSettings(protocolSettings);

		return config;
	}

	private static CryptoConfig createCryptoConfig(CryptoProperties cryptoProperties) {
		// 总是包含默认的提供者；
		Set<String> cryptoProviderNames = new LinkedHashSet<String>();
		for (String providerName : SUPPORTED_PROVIDERS) {
			cryptoProviderNames.add(providerName);
		}
		if (cryptoProperties.getProviders() != null) {
			for (String providerName : cryptoProperties.getProviders()) {
				cryptoProviderNames.add(providerName);
			}
		}
		CryptoProvider[] cryptoProviders = new CryptoProvider[cryptoProviderNames.size()];
		int i = 0;
		for (String providerName : cryptoProviderNames) {
			cryptoProviders[i] = Crypto.getProvider(providerName);
			i++;
		}

		String hashAlgorithmName = StringUtils.trim(cryptoProperties.getHashAlgorithm());
		hashAlgorithmName = hashAlgorithmName.length() == 0 ? DEFAULT_HASH_ALGORITHM : hashAlgorithmName;
		CryptoAlgorithm hashAlgorithm = Crypto.getAlgorithm(hashAlgorithmName);

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(cryptoProviders);
		cryptoConfig.setAutoVerifyHash(cryptoProperties.isVerifyHash());
		cryptoConfig.setHashAlgorithm(hashAlgorithm);

		return cryptoConfig;
	}

	private static SecurityInitData createSecurityInitSettings(LedgerInitProperties ledgerInitProps,
			ParticipantProperties[] participants) {
		// 设置角色；
		SecurityInitData securityInitData = new SecurityInitData();
		securityInitData.setRoles(ledgerInitProps.getRoles());
		// 如果没有默认角色，则创建“默认”角色;
		if (securityInitData.getRolesCount() == 0) {
			securityInitData.addRole(LedgerSecurityManager.DEFAULT_ROLE, LedgerPermission.values(),
					TransactionPermission.values());
		} else if (!securityInitData.containsRole(LedgerSecurityManager.DEFAULT_ROLE)) {
			// 如果定义了角色，则必须显式地定义“默认”角色；
			throw new LedgerInitException("Miss definition of role[DEFAULT]!");
		}

		// 设置授权；
		for (ParticipantProperties partiProps : participants) {
			String[] roles = partiProps.getRoles();
			for (String role : roles) {
				if (!securityInitData.containsRole(role)) {
					throw new LedgerInitException(
							String.format("The role[%s] authenticated to participant[%s-%s] is not defined!", role,
									partiProps.getId(), partiProps.getName()));
				}
			}
			//去掉对默认角色的授权；
			
			securityInitData.addUserAuthencation(partiProps.getAddress(), roles, partiProps.getRolesPolicy());
		}

		return securityInitData;
	}

	private static LedgerInitData createLedgerInitSettings(LedgerInitProperties ledgerProps,
			CryptoSetting cryptoSetting, ConsensusConfig consensusConfig, ParticipantProperties[] participants) {
		// 创建初始化配置；
		LedgerInitData initSetting = new LedgerInitData();
		initSetting.setLedgerSeed(ledgerProps.getLedgerSeed());
		initSetting.setCryptoSetting(cryptoSetting);

		initSetting.setConsensusParticipants(participants);

		initSetting.setCreatedTime(ledgerProps.getCreatedTime());

		// 创建共识配置；
		try {
			byte[] consensusSettingsBytes = encodeConsensusSettings(consensusConfig.getProvider(),
					consensusConfig.protocolSettings);
			initSetting.setConsensusProvider(consensusConfig.getProvider().getName());
			initSetting.setConsensusSettings(consensusSettingsBytes);
		} catch (Exception e) {
			throw new LedgerInitException("Create default consensus config failed! --" + e.getMessage(), e);
		}

		return initSetting;
	}

	public static byte[] encodeConsensusSettings(ConsensusProvider consensusProvider,
			ConsensusSettings consensusSettings) {
		return consensusProvider.getSettingsFactory().getConsensusSettingsEncoder().encode(consensusSettings);
	}

	/**
	 * 解析参与方列表；
	 * 
	 * @param ledgerInitProps
	 * @return
	 */
	private static ParticipantProperties[] resolveParticipants(LedgerInitProperties ledgerInitProps) {
		List<ParticipantProperties> partiList = ledgerInitProps.getConsensusParticipants();
		ParticipantProperties[] parties = new ParticipantProperties[partiList.size()];
		parties = partiList.toArray(parties);
		ParticipantProperties[] orderedParties = sortAndVerify(parties);

		return orderedParties;
	}

	/**
	 * 对参与者列表按照 id 进行升序排列，并校验id是否从 1 开始且没有跳跃；
	 * 
	 * @param parties
	 * @return
	 */
	private static ParticipantProperties[] sortAndVerify(ParticipantProperties[] parties) {
		Arrays.sort(parties, new Comparator<ParticipantProperties>() {
			@Override
			public int compare(ParticipantProperties o1, ParticipantProperties o2) {
				return o1.getId() - o2.getId();
			}
		});
		for (int i = 0; i < parties.length; i++) {
			if (parties[i].getId() != i) {
				throw new LedgerInitException(
						"The ids of participants are not match their positions in the participant-list!");
			}
		}
		return parties;
	}

	public static class ConsensusConfig {

		private ConsensusProvider provider;

		private ConsensusSettings protocolSettings;

		public ConsensusSettings getProtocolSettings() {
			return protocolSettings;
		}

		public void setProtocolSettings(ConsensusSettings protocolSettings) {
			this.protocolSettings = protocolSettings;
		}

		public ConsensusProvider getProvider() {
			return provider;
		}

		public void setProvider(ConsensusProvider provider) {
			this.provider = provider;
		}
	}
}
