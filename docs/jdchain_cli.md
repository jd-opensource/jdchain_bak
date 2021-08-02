## JD Chain Cli

JD Chain 命令行工具集，提供密钥管理，实时交易，链上信息查询，离线交易，共识节点变更等操作。

```bash
:bin$ ./jdchain-cli.sh -h
Usage: jdchain-cli [-hV] [--pretty] [--home=<path>] [COMMAND]
JDChain Cli is a convenient tool to manage jdchain keys, sign and send
transactions to jdchain network, query data from jdchain network.
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
      --pretty        Pretty json print
  -V, --version       Print version information and exit.

Commands:

The most commonly used git commands are:
  keys         List, create, update or delete keypairs.
  tx           Build, sign or send transaction.
  query        Query commands.
  participant  Add, update or delete participant.
  help         Displays help information about the specified command

See 'jdchain-cli help <command>' to read about a specific subcommand or concept.
```

- `keys` [密钥管理](cli/keys.md)
- `tx` [交易](cli/tx.md)
- `query` [链上信息查询](cli/query.md)
- `participant` [共识节点变更](cli/participant.md)