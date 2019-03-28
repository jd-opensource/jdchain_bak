//package com.jd.blockchain.client;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Command {
//
//	private PeerClient peerClient;
//
//	private Map<String, CmdExecutor> executors;
//
//	public Command(PeerClient peerClient) {
//		this.peerClient = peerClient;
//		this.executors = new HashMap<>();
//		executors.put("set", new SetCmd());
//		executors.put("get", new GetCmd());
//		executors.put("remove", new RemoveCmd());
//		executors.put("keys", new KeysCmd());
//		executors.put("contain", new ContainCmd());
//	}
//
//	public void execute(String cmd) {
//		String[] cmdArgs = cmd.split(" ");
//		if (cmdArgs.length == 0) {
//			System.out.println("Illegal input!!");
//			return;
//		}
//
//		String command = cmdArgs[0].trim();
//		String[] args = new String[cmdArgs.length - 1];
//		for (int i = 1; i < cmdArgs.length; i++) {
//			args[i - 1] = cmdArgs[i].trim();
//		}
//
//		CmdExecutor executor = executors.get(command);
//		executor.execute(args);
//	}
//
//	private static interface CmdExecutor {
//
//		public void execute(String[] args);
//
//	}
//
//	private class SetCmd implements CmdExecutor {
//		@Override
//		public void execute(String[] args) {
//			if (args.length < 2) {
//				System.out.println("SET command require 2 args!");
//				return;
//			}
//			String result = peerClient.set(args[0], args[1]);
//			System.out.println("ok. --[" + result + "]");
//		}
//	}
//
//	private class GetCmd implements CmdExecutor {
//		@Override
//		public void execute(String[] args) {
//			if (args.length < 1) {
//				System.out.println("GET command require 1 args!");
//				return;
//			}
//			String result = peerClient.get(args[0]);
//			System.out.println("ok. --[" + result + "]");
//		}
//	}
//
//	private class RemoveCmd implements CmdExecutor {
//		@Override
//		public void execute(String[] args) {
//			if (args.length < 1) {
//				System.out.println("REMOVE command require 1 args!");
//				return;
//			}
//			String result = peerClient.remove(args[0]);
//			System.out.println("ok. --[" + result + "]");
//		}
//	}
//
//	private class KeysCmd implements CmdExecutor {
//		@Override
//		public void execute(String[] args) {
//			String[] result = peerClient.getKeys();
//			System.out.println("ok. --[" + result + "]");
//		}
//	}
//
//	private class ContainCmd implements CmdExecutor {
//		@Override
//		public void execute(String[] args) {
//			if (args.length < 1) {
//				System.out.println("CONTAIN command require 1 args!");
//				return;
//			}
//			boolean result = peerClient.contain(args[0]);
//			System.out.println("ok. --[" + result + "]");
//		}
//	}
//}
