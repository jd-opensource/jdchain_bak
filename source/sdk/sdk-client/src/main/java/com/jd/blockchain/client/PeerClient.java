//package com.jd.blockchain.client;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//
//import com.jd.blockchain.ledger.Operation;
//
//import bftsmart.tom.AsynchServiceProxy;
//
//public class PeerClient {
//	
//	private AsynchServiceProxy peerProxy;
//	
//	public PeerClient(int clientId) {
//		this.peerProxy = new AsynchServiceProxy(clientId);
//	}
//	
//	public void close(){
//		peerProxy.close();
//	}
//	
//	public String set(String key, String value){
//		Operation.SetOP setOP = new Operation.SetOP(key, value);
//		byte[] reply = peerProxy.invokeOrdered(setOP.toBytes());
//		
//		return resolveResult(reply, String.class);
//	}
//	
//	
//	public String remove(String key){
//		Operation.RemoveOP removeOP = new Operation.RemoveOP(key);
//		byte[] reply = peerProxy.invokeOrdered(removeOP.toBytes());
//		
//		return resolveResult(reply, String.class);
//	}
//	
//	public String get(String key){
//		Operation.GetOP getOP = new Operation.GetOP(key);
//		byte[] reply = peerProxy.invokeUnordered(getOP.toBytes());
//		
//		return resolveResult(reply, String.class);
//	}
//
//	public String[] getKeys(){
//		Operation.KeysOP keysOP = new Operation.KeysOP();
//		byte[] reply = peerProxy.invokeUnordered(keysOP.toBytes());
//		
//		return resolveResult(reply, String[].class);
//	}
//	
//	public boolean contain(String key){
//		Operation.ContainOP containOP = new Operation.ContainOP(key);
//		byte[] reply = peerProxy.invokeUnordered(containOP.toBytes());
//		
//		return resolveResult(reply, Boolean.class);
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static <T> T resolveResult(byte[] bytes, Class<T> resultType){
//		if (bytes == null || bytes.length == 0) {
//			return null;
//		}
//		try {
//			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
//			ObjectInputStream in = new ObjectInputStream(bi);
//			Object op = in.readObject();
//			return (T) op;
//		} catch (ClassNotFoundException e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		} catch (IOException e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
//}
