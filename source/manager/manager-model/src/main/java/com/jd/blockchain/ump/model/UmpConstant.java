package com.jd.blockchain.ump.model;

import java.io.File;

public class UmpConstant {

    public static String PROJECT_PATH = "";

    public static final String DB_NAME = "jumpdb";

    public static final String URL_SEPARATOR = "/";

    public static final String URL_MASTER = "/master";

    public static final String URL_PEER = "/peer";

    public static final String PRIVATE_KEY_SUFFIX = ".priv";

    public static final String PUBLIC_KEY_SUFFIX = ".pub";

    public static final String PWD_SUFFIX = ".pwd";

    public static final String REQUEST_SHARED_URL = URL_MASTER + URL_SEPARATOR + "share";

    public static final String REQUEST_STATE_URL = URL_MASTER + URL_SEPARATOR + "receive";

    public static final String PARTINODE_COUNT = "cons_parti.count";

    public static final String PARTINODE_FORMAT = "cons_parti.%s";

    public static final String PARTINODE_NAME_FORMAT = PARTINODE_FORMAT + ".name";

    public static final String PARTINODE_PUBKEY_FORMAT = PARTINODE_FORMAT + ".pubkey";

    public static final String PARTINODE_ROLES_FORMAT = PARTINODE_FORMAT + ".roles";

    public static final String PARTINODE_ROLES_POLICY_FORMAT = PARTINODE_FORMAT + ".roles-policy";

    public static final String PARTINODE_INIT_FORMAT = PARTINODE_FORMAT + ".initializer";

    public static final String PARTINODE_INIT_HOST_FORMAT = PARTINODE_INIT_FORMAT + ".host";

    public static final String PARTINODE_INIT_PORT_FORMAT = PARTINODE_INIT_FORMAT + ".port";

    public static final String PARTINODE_INIT_SECURE_FORMAT = PARTINODE_INIT_FORMAT + ".secure";

    public static final String LEDGER_PREFIX = "ledger";

    public static final String LEDGER_SEED_PREFIX = LEDGER_PREFIX + ".seed";

    public static final String LEDGER_NAME_PREFIX = LEDGER_PREFIX + ".name";

    public static final String CREATE_TIME_PREFIX = "created-time";

    public static final String SECURITY_PREFIX = "security";

    public static final String SECURITY_ROLES = SECURITY_PREFIX + ".roles";

    public static final String SECURITY_ROLES_PRIVILEGES_LEDGER_FORMAT = SECURITY_ROLES + ".%s.ledger-privileges";

    public static final String SECURITY_ROLES_PRIVILEGES_TX_FORMAT = SECURITY_ROLES + ".%s.tx-privileges";

    public static final String SECURITY_PARTI_PREFIX = "participant.default";

    public static final String SECURITY_PARTI_ROLES = SECURITY_PARTI_PREFIX + ".roles";

    public static final String SECURITY_PARTI_ROLES_POLICY = SECURITY_PARTI_PREFIX + ".roles-policy";

    public static final String CRYPTO_PREFIX = "crypto";

    public static final String CRYPTO_HASH_VERIFY = CRYPTO_PREFIX + ".verify-hash";

    public static final String CRYPTO_HASH_ALGORITHM = CRYPTO_PREFIX + ".hash-algorithm";

    public static final String CONSENSUS_PREFIX = "consensus";

    public static final String CONSENSUS_PROVIDER_PREFIX = CONSENSUS_PREFIX + ".service-provider";

    public static final String CONSENSUS_CONF_PREFIX = CONSENSUS_PREFIX + ".conf";

    public static final String CRYPTO_PROVIDERS_PREFIX = CRYPTO_PREFIX + ".service-providers";

    public static final String LOCAL_PREFIX = "local";

    public static final String LOCAL_PARTI_PREFIX = LOCAL_PREFIX + ".parti";

    public static final String LOCAL_PARTI_ID_PREFIX = LOCAL_PARTI_PREFIX + ".id";

    public static final String LOCAL_PARTI_PUBKEY_PREFIX = LOCAL_PARTI_PREFIX + ".pubkey";

    public static final String LOCAL_PARTI_PRIVKEY_PREFIX = LOCAL_PARTI_PREFIX + ".privkey";

    public static final String LOCAL_PARTI_PWD_PREFIX = LOCAL_PARTI_PREFIX + ".pwd";

    public static final String LEDGER_BINDING_OUT_PREFIX = LEDGER_PREFIX + ".binding.out";

    public static final String LEDGER_DB_URI_PREFIX = LEDGER_PREFIX + ".db.uri";

    public static final String LEDGER_DB_PWD_PREFIX = LEDGER_PREFIX + ".db.pwd";

    public static final String CMD_LEDGER_INIT = "/bin/bash %s -monitor";

    public static final String CMD_START_UP_FORMAT = "/bin/bash %s";

    public static final String PATH_BIN = File.separator + "bin";

    public static final String PATH_LEDGER_INIT_BIN = PATH_BIN + File.separator + "ledger-init.sh";

    public static final String PATH_PEER_STARTUP_BIN = PATH_BIN + File.separator + "peer-startup.sh";

    public static final String PATH_LIBS = File.separator + "libs";

    public static final String PATH_SYSTEM = File.separator + "system";

    public static final String PATH_CONFIG = File.separator + "config";

    public static final String PATH_CONFIG_KEYS = PATH_CONFIG + File.separator + "keys";

    public static final String PATH_LEDGER_BINDING_CONFIG = PATH_CONFIG + File.separator + "ledger-binding.conf";

    public static final String PATH_CONFIG_INIT = PATH_CONFIG + File.separator + "init";

    public static final String PATH_LOCAL_CONFIG = PATH_CONFIG_INIT + File.separator + "local.conf";

    public static final String PATH_LEDGER_INIT_CONFIG = PATH_CONFIG_INIT + File.separator + "ledger.init";

    public static final String PEER_HOST_IP = "peerHostIp";
    public static final String INIT_PORT = "iPort";
    public static final String CONSENSUS_PORT = "cPort";
    public static final String DELIMETER_QUESTION = "?";   //逗号分隔符;
    public static final String LEDGER_LIST = "ledger_list";   //the key that save all the ledger hash in the rocksdb;
    public static final int MEMORY_MAP_MAX_COUNT=10000;
    public static final int MEMORY_MAP_REMOVE_COUNT=50;
    public static final String ALL_LEDGER="all_ledger";
    public static final String DELIMETER_MINUS = "-";
    public static final String SCHEMA_PREFIX = "schema_";
    public static final String SCHEMA_RETRIEVAL_URL = "schema.retrieval.url";
    public static final String TASK_RETRIEVAL_URL = "task.retrieval.url";
}
