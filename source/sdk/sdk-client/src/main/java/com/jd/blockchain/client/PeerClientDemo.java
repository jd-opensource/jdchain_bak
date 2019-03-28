//package com.jd.blockchain.client;
//
//import java.io.Console;
//
//public class PeerClientDemo {
//
//	public static void main(String[] args) {
//		int clientId;
//		if (args.length == 0) {
////			System.out.println("No client id !!!");
////			return;
//			clientId = 7;
//		}else{
//			clientId = Integer.parseInt(args[0]);
//		}
//
//		PeerClient client = new PeerClient(clientId);
//		Command cmd = new Command(client);
//		System.out.println("---------------- Client["+clientId+"] started -----------------");
//		
//		do {
//			System.out.println(">>");
//			Console console = System.console();
//			String op = console.readLine();
//			if ("exit".equalsIgnoreCase(op)) {
//				break;
//			}
//			cmd.execute(op);
//		} while (true);
////		
//		System.out.println("Client exist!");
//		client.close();
//	}
//
//}
