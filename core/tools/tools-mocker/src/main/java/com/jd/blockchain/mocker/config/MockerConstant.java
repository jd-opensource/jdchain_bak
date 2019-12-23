package com.jd.blockchain.mocker.config;

public class MockerConstant {

    public static final String DEFAULT_LEDGER_SEED = "932dfe23-fe23232f-283f32fa-dd32aa76-8322ca2f-56236cda-7136b322-cb323ffa";

    public static final int PEER_PORT_START = 12000;

    public static final int LEDGER_INIT_PORT_START = 1600;

    public static final int GATEWAY_PORT = 11000;

    public static final String LOCAL_ADDRESS = "127.0.0.1";

    public static final String LEDGER_NAME = "JDChain";

    public static final String LEDGER_INIT_FORMATTER = "ledger%s.init";

    public static final String[] CONSENSUS_PROVIDERS = new String[] {"com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider"};

    public static final String PASSWORD = "abc";

    public static final String PASSWORD_ENCODE = "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY";

    public static final String[] PUBLIC_KEYS = {
            "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9",
            "3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX",
            "3snPdw7i7PZi6TStiyc6mzjprnNhgs2atSGNS8wPYzhbKaUWGFJt7x",
            "3snPdw7i7PifPuRX7fu3jBjsb3rJRfDe9GtbDfvFJaJ4V4hHXQfhwk",
            "3snPdw7i7PXPRMp3EAjsxJkHe7aZJRLNzdW8kEHBWeQsSgcPAiHP2J",
            "3snPdw7i7PmmQoPgUpUmmAUj6nakHj8wMSQmiMi1RaiZp4YU1D4AXk",
            "3snPdw7i7PiJKsa94q3EcLT1y6GRJ7LeFGe799hdzRRHmf6LNodyiM",
            "3snPdw7i7Pm2wJwEnKn8kK8eGTkN36C2BZRRjVTr9FPB2rqtcgTq7h"
    };

    public static final String[] ADDRESS_ARRAY = {
            "LdeP3fY7jJbNwL8CiL2wU21AF9unDWQjVEW5w",
            "LdeNnz88dH6CA6PwkVdn3nFRibUKP3sFT2byG",
            "LdeNmdpT4DiTwLUP9jRQhwdRBRiXeHno456vy",
            "LdeNekdXMHqyz9Qxc2jDSBnkvvZLbty6pRDdP",
            "LdeNryu2DK96tDvtLJfBz7ArWynAWPJAep38n",
            "LdeNkoQpXffVF9qjsa4A7wZVgT9W2vnhpEEm5",
            "LdeNzfFrsJT7Ni1L7k1EP3NuxUfK8QGAxMGpt",
            "LdeNuLhR5AoyhQoVeS15haJvvGC5ByoPezrGq"
    };

    public static final String[] PRIVATE_KEYS = {
            "177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x",
            "177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT",
            "177gjtwLgmSx5v1hFb46ijh7L9kdbKUpJYqdKVf9afiEmAuLgo8Rck9yu5UuUcHknWJuWaF",
            "177gk1pudweTq5zgJTh8y3ENCTwtSFsKyX7YnpuKPo7rKgCkCBXVXh5z2syaTCPEMbuWRns",
            "177gjwyHzfmsD4g3MVB655seYWXua2KBdQEqTf9kHgeMc6gdRZADKb6cL13L5koqMsBtkGX",
            "177gk2C9V7gwPhAGgawL53W8idDpSo63jnbg8finbZkk4zermr5aqgTeKspN45fbymey8t6",
            "177gjtz29TXa2E3FFBpCNr5LpU5zYxkNPAgcAJZW7tCGUgWQr4gcVv8PHmoVVPeSVVnyZV5",
            "177gjzpHnqGEuSKyi3pW69WhpEPmeFPxLNVmUfXQb4DDV2EfnMgY7T4NFsyRsThjJFsau7X"};

    public static final String[] DB_MEMS = {
            "memory://127.0.0.1/0",
            "memory://127.0.0.1/1",
            "memory://127.0.0.1/2",
            "memory://127.0.0.1/3",
            "memory://127.0.0.1/4",
            "memory://127.0.0.1/5",
            "memory://127.0.0.1/6",
            "memory://127.0.0.1/7"
    };
}
