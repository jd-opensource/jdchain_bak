//package com.jd.blockchain.sdk;
//
//import java.io.Closeable;
//
//import com.jd.blockchain.ledger.CryptoSetting;
//import com.jd.blockchain.ledger.data.TransactionService;
//import com.jd.blockchain.sdk.proxy.BlockchainServiceProxy;
//
///**
// * 
// * @author huanghaiquan
// *
// */
//public abstract class AbstractBlockchainServiceFactory implements Closeable {
//
//	private final Object mutex = new Object();
//
//	private volatile BlockchainService blockchainService;
//	
////	protected ServiceSetting setting;
//	
//	private CryptoSetting cryptoSetting;
//	
//	public AbstractBlockchainServiceFactory() {
//	}
//
//	public BlockchainService getBlockchainService() {
//		if (blockchainService == null) {
//			synchronized (mutex) {
//				if (blockchainService == null) {
//					BlockchainQueryService queryService = getQueryService(setting);
//					TransactionService consensusService = getConsensusService(setting);
//					blockchainService = createBlockchainService(setting, consensusService, queryService);
//				}
//			}
//		}
//		return blockchainService;
//	}
//	
//	protected BlockchainService createBlockchainService(ServiceSetting setting, TransactionService consensusService, BlockchainQueryService queryService) {
//		return new BlockchainServiceProxy(consensusService, queryService);
//	}
//
//	protected abstract BlockchainQueryService getQueryService(ServiceSetting setting);
//
//	protected abstract TransactionService getConsensusService(ServiceSetting setting);
//
//	@Override
//	public abstract void close();
//
//}
