### 密钥管理

`jdchain-cli`提供基于本地目录的密钥管理：[密钥对列表](#密钥对列表)，[显示密钥对](#显示密钥对)，[添加密钥对](#添加密钥对)，[更新私钥密码](#更新私钥密码)，[删除密钥对](#删除密钥对)

```bash
:bin$ ./jdchain-cli.sh keys -h
Usage: git status [<options>...] [--] [<pathspec>...]
List, create, update or delete keypairs.
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
                        Default: ../
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
Commands:
  list    List all the keypairs.
  show    Show keypair.
  add     Create a new keypair.
  update  Update privkey password.
  delete  Delete keypair.
  help    Displays help information about the specified command
```

#### 密钥对列表
```bash
:bin$ ./jdchain-cli.sh keys list -h
List all the keypairs.
Usage: jdchain-cli keys list [-hV] [--pretty] [--home=<path>]
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
```
- `home`，指定密钥存储相关目录，`${home}/config/keys`

如：
```bash
:bin$ ./jdchain-cli.sh keys list
NAME         ALGORITHM      ADDRESS      PUBKEY
```
- `NAME`，名称
- `ALGORITHM`，算法
- `ADDRESS`，地址
- `PUBKEY`，公钥

#### 显示密钥对
```bash
:bin$ ./jdchain-cli.sh keys show -h
Show the keypair.
Usage: jdchain-cli keys show [-hV] [--pretty] [--home=<path>] -n=<name>
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
  -n, --name=<name>   Name of the key
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
```
- `name`，密钥对名称

如：
```bash
:bin$ ./jdchain-cli.sh keys show -n k1
input the password:
> 1
NAME           ALGORITHM        ADDRESS PUBKEY  PRIVKEY PASSWORD
k1             ED25519          LdeNwzRRuF33BNkyzbMuzKV3zFNGMrYBsRXvm   7VeRPc4QsYJX7qpzHBBJTzwvvmXXFVvP1MwmdU7WCBv9Uvc5        177gk2XHAsWRMXyHLLcJsig2jvXWpgo4ZVg2HYgGaiXauAZqPcnsETNeLUeRShw2BKgHVbN 8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG
```
会显示`k1`所有信息

#### 添加密钥对
```bash
:bin$ ./jdchain-cli.sh keys add -h
Create a new keypair.
Usage: jdchain-cli keys add [-hV] [--pretty] [-a=<algorithm>] [--home=<path>]
                            -n=<name>
  -a, --algorithm=<algorithm>
                      Crypto algorithm
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
  -n, --name=<name>   Name of the key
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
```

- `a`/`algorithm`，密钥算法：`SM2`，`ED25519`等，默认`ED25519`
- `name`，密钥对名称

如：
```bash
:bin$ ./jdchain-cli.sh keys add -n k1
please input password: >
// 输入私钥密码
1
NAME           ALGORITHM      ADDRESS                                 PUBKEY
k1             ED25519        LdeP1iczD3zpmcayKAxTfSywict9y2r6Jpq6n   7VeRBamwPeMb7jzTNg3Ap2DscBiy3QE3PK5NqBvv9tUjQVk4
```

#### 更新私钥密码

```bash
:bin$ ./jdchain-cli.sh keys update -h
Update privkey password.
Usage: jdchain-cli keys update [-hV] [--pretty] [--home=<path>] -n=<name>
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
  -n, --name=<name>   Name of the key
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
```
- `name`，密钥对名称

如：
```bash
:bin$ ./jdchain-cli.sh keys update -n k1
input the current password:
// 输入当前密码
> 1
input new password:
// 输入新密码
> 2
NAME           ALGORITHM      ADDRESS                                 PUBKEY
k1             ED25519        LdeP1iczD3zpmcayKAxTfSywict9y2r6Jpq6n   7VeRBamwPeMb7jzTNg3Ap2DscBiy3QE3PK5NqBvv9tUjQVk4
```

#### 删除密钥对

```bash
:bin$ ./jdchain-cli.sh keys delete -h
Delete keypair.
Usage: jdchain-cli keys delete [-hV] [--pretty] [--home=<path>] -n=<name>
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
  -n, --name=<name>   Name of the key
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
```
- `name`，密钥对名称

如：
```bash
:bin$ ./jdchain-cli.sh keys delete -n k1
input the current password: >
// 输入当前密码
2
[k1] deleted
```