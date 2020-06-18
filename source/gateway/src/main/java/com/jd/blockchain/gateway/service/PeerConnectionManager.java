package com.jd.blockchain.gateway.service;

import javax.annotation.PreDestroy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.service.PeerServiceProxy;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.gateway.PeerConnector;
import com.jd.blockchain.gateway.PeerService;
import com.jd.blockchain.sdk.service.PeerBlockchainServiceFactory;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.utils.net.NetworkAddress;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class PeerConnectionManager implements PeerService, PeerConnector {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PeerConnectionManager.class);

	/**
	 * 30秒更新一次最新的情况
	 */
	private static final long PERIOD_SECONDS = 30L;

	private final ScheduledThreadPoolExecutor peerConnectExecutor;

	private final Set<HashDigest> localLedgerCache = new HashSet<>();

	private final Lock ledgerHashLock = new ReentrantLock();

	private Map<NetworkAddress, PeerBlockchainServiceFactory> peerBlockchainServiceFactories = new ConcurrentHashMap<>();

	private Map<HashDigest, PeerBlockchainServiceFactory> latestPeerServiceFactories = new ConcurrentHashMap<>(16);

	private Set<NetworkAddress> peerAddresses = new HashSet<>();

	private volatile PeerServiceFactory mostLedgerPeerServiceFactory;

	private volatile PeerBlockchainServiceFactory masterPeerServiceFactory;

	private volatile AsymmetricKeypair gateWayKeyPair;

	private volatile List<String> peerProviders;

	private volatile EventListener eventListener;

	public PeerConnectionManager() {
		peerConnectExecutor = scheduledThreadPoolExecutor();
		executorStart();
	}

	@Override
	public Set<NetworkAddress> getPeerAddresses() {
		return peerAddresses;
	}

	@Override
	public boolean isConnected() {
		return !peerBlockchainServiceFactories.isEmpty();
	}

	@Override
	public synchronized void connect(NetworkAddress peerAddress, AsymmetricKeypair defaultKeyPair, List<String> peerProviders) {
		if (peerAddresses.contains(peerAddress)) {
			return;
		}
		// 连接成功的话，更新账本
		ledgerHashLock.lock();
		try {
			addPeerAddress(peerAddress);
			setGateWayKeyPair(defaultKeyPair);
			setPeerProviders(peerProviders);

			PeerBlockchainServiceFactory peerServiceFactory = PeerBlockchainServiceFactory.connect(defaultKeyPair, peerAddress, peerProviders);
			if (peerServiceFactory != null) {
				LOGGER.error("Connect peer {} success !!!", peerAddress);
				// 连接成功
				if (masterPeerServiceFactory == null) {
					masterPeerServiceFactory = peerServiceFactory;
					LOGGER.error("Master remote update to {}", peerAddress);
				}
				if (mostLedgerPeerServiceFactory == null) {
					// 默认设置为第一个连接成功的，后续更新需要等待定时任务处理
					mostLedgerPeerServiceFactory = new PeerServiceFactory(peerAddress, peerServiceFactory);
					LOGGER.error("Most ledgers remote update to {}", peerAddress);
				}
				peerBlockchainServiceFactories.put(peerAddress, peerServiceFactory);
				updateLedgerCache();
			}
		} finally {
			// 连接成功的话，更新账本
			ledgerHashLock.unlock();
		}
	}

	@Override
	public void monitorAndReconnect() {
		if (getPeerAddresses().isEmpty()) {
			throw new IllegalArgumentException("Peer addresses must be init first !!!");
		}
		/**
		 * 1、首先判断是否之前连接成功过，若未成功则重连，走auth逻辑
		 * 2、若成功，则判断对端节点的账本与当前账本是否一致，有新增的情况下重连
		 */
		ledgerHashLock.lock();
		try {
			if (isConnected()) {
				// 已连接成功，判断账本信息
				PeerServiceFactory serviceFactory = mostLedgerPeerServiceFactory;
				if (serviceFactory == null) {
					// 等待被更新
					return;
				}
				BlockchainQueryService queryService = serviceFactory.serviceFactory.getBlockchainService();
				NetworkAddress peerAddress = serviceFactory.peerAddress;

				HashDigest[] peerLedgerHashs = queryService.getLedgerHashs();
				if (peerLedgerHashs != null && peerLedgerHashs.length > 0) {
					boolean haveNewLedger = false;
					for (HashDigest hash : peerLedgerHashs) {
						if (!localLedgerCache.contains(hash)) {
							haveNewLedger = true;
							break;
						}
					}
					if (haveNewLedger) {
						// 有新账本的情况下重连，并更新本地账本
						PeerBlockchainServiceFactory peerServiceFactory = PeerBlockchainServiceFactory.connect(
								gateWayKeyPair, peerAddress, peerProviders);
						peerBlockchainServiceFactories.put(peerAddress, peerServiceFactory);
						localLedgerCache.addAll(Arrays.asList(peerLedgerHashs));
					}
				}
			}
			// 未连接成功的情况下不处理，等待定时连接线程来处理
		} finally {
			ledgerHashLock.unlock();
		}
	}

	@Override
	public void close() {
		for (Map.Entry<NetworkAddress, PeerBlockchainServiceFactory> entry : peerBlockchainServiceFactories.entrySet()) {
			PeerBlockchainServiceFactory serviceFactory = entry.getValue();
			if (serviceFactory != null) {
				serviceFactory.close();
			}
		}
		peerBlockchainServiceFactories.clear();
	}

	@Override
	public BlockchainQueryService getQueryService() {
		// 查询选择最新的连接Factory
		PeerServiceFactory serviceFactory = this.mostLedgerPeerServiceFactory;
		if (serviceFactory == null) {
			throw new IllegalStateException("Peer connection was closed!");
		}
		return serviceFactory.serviceFactory.getBlockchainService();
	}

	@Override
	public BlockchainQueryService getQueryService(HashDigest ledgerHash) {
		PeerBlockchainServiceFactory serviceFactory = latestPeerServiceFactories.get(ledgerHash);
		if (serviceFactory == null) {
			return getQueryService();
		}
		return serviceFactory.getBlockchainService();
	}

	@Override
	public TransactionService getTransactionService() {
		// 交易始终使用第一个连接成功的即可
		PeerBlockchainServiceFactory serviceFactory = this.masterPeerServiceFactory;
		if (serviceFactory == null) {
			throw new IllegalStateException("Peer connection was closed!");
		}

		return serviceFactory.getTransactionService();
	}

	@PreDestroy
	private void destroy() {
		close();
	}

	public void addPeerAddress(NetworkAddress peerAddress) {
		this.peerAddresses.add(peerAddress);
	}

	public void setGateWayKeyPair(AsymmetricKeypair gateWayKeyPair) {
		this.gateWayKeyPair = gateWayKeyPair;
	}

	public void setPeerProviders(List<String> peerProviders) {
		this.peerProviders = peerProviders;
	}

	/**
	 * 更新本地账本缓存
	 */
	private void updateLedgerCache() {
		if (isConnected()) {
			HashDigest[] peerLedgerHashs = getQueryService().getLedgerHashs();
			if (peerLedgerHashs != null && peerLedgerHashs.length > 0) {
				localLedgerCache.addAll(Arrays.asList(peerLedgerHashs));
			}
		}
	}

	/**
	 * 创建定时线程池
	 * @return
	 */
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat("peer-connect-%d").build();
		return new ScheduledThreadPoolExecutor(1,
				threadFactory,
				new ThreadPoolExecutor.AbortPolicy());
	}

	private void executorStart() {
		// 定时任务处理线程
		peerConnectExecutor.scheduleAtFixedRate(new PeerConnectRunner(), 0, PERIOD_SECONDS, TimeUnit.SECONDS);
	}

	private class PeerServiceFactory {

		private NetworkAddress peerAddress;

		private PeerBlockchainServiceFactory serviceFactory;

		PeerServiceFactory(NetworkAddress peerAddress, PeerBlockchainServiceFactory serviceFactory) {
			this.peerAddress = peerAddress;
			this.serviceFactory = serviceFactory;
		}
	}

	private class PeerConnectRunner implements Runnable {

		@Override
		public void run() {
			// 包括几部分工作
			// 1、重连没有连接成功的Peer；
			// 2、从已经连接成功的Peer节点获取账本数量和最新的区块高度
			// 3、根据目前的情况更新缓存
			ledgerHashLock.lock();
			try {
				reconnect();
				// 更新账本数量最多的节点连接
				HashDigest[] ledgerHashs = updateMostLedgerPeerServiceFactory();
				if (ledgerHashs != null) {
					LOGGER.info("Most ledgers remote update to {}", mostLedgerPeerServiceFactory.peerAddress);
					// 更新每个账本对应获取最高区块的缓存
					updateLatestPeerServiceFactories(ledgerHashs);
				}
			} catch (Exception e) {
				LOGGER.error("Peer Connect Task Error !!!", e);
			} finally {
				ledgerHashLock.unlock();
			}
		}

		/**
		 * 更新可获取最新区块的连接工厂
		 *
		 * @param ledgerHashs
		 *             账本列表
		 */
		private void updateLatestPeerServiceFactories(HashDigest[] ledgerHashs) {
			Map<HashDigest, PeerBlockchainServiceFactory> blockHeightServiceFactories = new HashMap<>();
			for (HashDigest ledgerHash : ledgerHashs) {
				long blockHeight = -1L;
				PeerBlockchainServiceFactory serviceFactory = latestPeerServiceFactories.get(ledgerHash);
				try {
					if (serviceFactory != null) {
						blockHeight = serviceFactory.getBlockchainService()
								.getLedger(ledgerHash).getLatestBlockHeight();
						blockHeightServiceFactories.put(ledgerHash, serviceFactory);
					}
				} catch (Exception e) {
					latestPeerServiceFactories.remove(ledgerHash);
					serviceFactory = null;
					LOGGER.error("Peer get latest block height fail !!!", e);
				}

				// 查询其他所有节点对应的区块高度的情况
				NetworkAddress defaultPeerAddress = null, latestPeerAddress = null;
				for (Map.Entry<NetworkAddress, PeerBlockchainServiceFactory> entry : peerBlockchainServiceFactories.entrySet()) {
					PeerBlockchainServiceFactory sf = entry.getValue();
					if (sf != serviceFactory) {
						try {
							long latestBlockHeight = sf.getBlockchainService().getLedger(ledgerHash).getLatestBlockHeight();
							if (latestBlockHeight > blockHeight) {
								latestPeerAddress = entry.getKey();
								blockHeightServiceFactories.put(ledgerHash, sf);
							}
							blockHeight = Math.max(latestBlockHeight, blockHeight);
						} catch (Exception e) {
							LOGGER.error(String.format("Peer[%s] get ledger[%s]'s latest block height fail !!!",
									entry.getKey(), ledgerHash.toBase58()), e);
						}
					} else {
						defaultPeerAddress = entry.getKey();
					}
				}
				LOGGER.info("Ledger[{}]'s master remote update to {}", ledgerHash.toBase58(),
						latestPeerAddress == null ? defaultPeerAddress : latestPeerAddress);
			}
			// 更新结果集
			latestPeerServiceFactories.putAll(blockHeightServiceFactories);
		}

		/**
		 * 之前未连接成功的Peer节点进行重连操作
		 *
		 */
		private void reconnect() {
			for (NetworkAddress peerAddress : peerAddresses) {
				if (!peerBlockchainServiceFactories.containsKey(peerAddress)) {
					// 重连指定节点
					try {
						PeerBlockchainServiceFactory peerServiceFactory = PeerBlockchainServiceFactory.connect(gateWayKeyPair, peerAddress, peerProviders);
						if (peerServiceFactory != null) {
							peerBlockchainServiceFactories.put(peerAddress, peerServiceFactory);
						}
					} catch (Exception e) {
						LOGGER.error(String.format("Reconnect %s fail !!!", peerAddress), e);
					}
				}
			}
		}

		private HashDigest[] updateMostLedgerPeerServiceFactory() {
			int ledgerSize = -1;
			if (mostLedgerPeerServiceFactory == null) {
				return null;
			}
			HashDigest[] ledgerHashs = null;
			BlockchainService blockchainService = mostLedgerPeerServiceFactory.serviceFactory.getBlockchainService();
			try {
				if (blockchainService instanceof PeerServiceProxy) {
					ledgerHashs = ((PeerServiceProxy) blockchainService).getLedgerHashsDirect();
					if (ledgerHashs != null) {
						ledgerSize = ledgerHashs.length;
					}
				}
			} catch (Exception e) {
				// 连接失败的情况下清除该连接
				LOGGER.error(String.format("Connect %s fail !!!", mostLedgerPeerServiceFactory.peerAddress), e);
				peerBlockchainServiceFactories.remove(mostLedgerPeerServiceFactory.peerAddress);
				mostLedgerPeerServiceFactory = null;
				blockchainService = null;
			}
			PeerServiceFactory tempMostLedgerPeerServiceFactory = mostLedgerPeerServiceFactory;

			// 遍历，获取对应端的账本数量及最新的区块高度
			for (Map.Entry<NetworkAddress, PeerBlockchainServiceFactory> entry : peerBlockchainServiceFactories.entrySet()) {
				BlockchainService loopBlockchainService = entry.getValue().getBlockchainService();
				if (loopBlockchainService != blockchainService) {
					// 处理账本数量
					try {
						if (loopBlockchainService instanceof PeerServiceProxy) {
							ledgerHashs = ((PeerServiceProxy) loopBlockchainService).getLedgerHashsDirect();
							if (ledgerHashs.length > ledgerSize) {
								tempMostLedgerPeerServiceFactory = new PeerServiceFactory(entry.getKey(),entry.getValue());
							}
						}
					} catch (Exception e) {
						LOGGER.error(String.format("%s get ledger hash fail !!!", entry.getKey()), e);
					}
				}
			}
			// 更新mostLedgerPeerServiceFactory
			mostLedgerPeerServiceFactory = tempMostLedgerPeerServiceFactory;
			return ledgerHashs;
		}
	}
}
