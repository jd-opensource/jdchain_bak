### 证书管理

`jdchain-cli`提供**`ED25519`,`RSA`,`ECDSA`,`SM2`**密钥算法的证书签发工具：[证书列表](#证书列表)，[显示证书](#显示证书)，[CSR](#CSR)，[CRT](#CRT)，[更新证书](#更新证书)，[生成测试证书](#生成测试证书)

> 目前支持创建`ED25519`,RSA`,`ECDSA`,`SM2`四种签名算法，请使用对应算法的公私钥

```bash
:bin$ ./jdchain-cli.sh ca -h
Usage: jdchain-cli ca [-hV] [--pretty] [--home=<path>] [COMMAND]
List, create, update certificates.
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
                        Default: ../
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
Commands:
  list   List all the certificates.
  show   Show certificate.
  csr    Create certificate signing request.
  crt    Create new certificate.
  renew  Update validity period.
  test   Create certificates for a testnet.
  help   Displays help information about the specified command
```
- `home`，指定密钥和证书存储相关目录，`${home}/config/keys`

#### 证书列表
```bash
:bin$ ./jdchain-cli.sh ca list -h
List all the certificates.
Usage: jdchain-cli ca list [-hV] [--pretty] [--home=<path>]
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
```

如：
```bash
:bin$ ./jdchain-cli.sh keys list
NAME           ALGORITHM        ADDRESS PUBKEY
```
- `NAME`，名称
- `ALGORITHM`，算法
- `ADDRESS`，地址
- `PUBKEY`，公钥

#### 显示证书
```bash
:bin$ ./jdchain-cli.sh ca show -h
Show certificate.
Usage: jdchain-cli ca show [-hV] [--pretty] [--home=<path>] -n=<name>
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
  -n, --name=<name>   Name of the certificate
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
```
- `name`，证书名称

如显示`${home}/config/keys`下名为`G1`的证书信息：
```bash
:bin$ ./jdchain-cli.sh ca show -n G1
./jdchain-cli.sh ca show -n G1
NAME    ALGORITHM       TYPE    ROLE    PUBKEY
G1      SM2     ROLE-TODO       [GW]    SFZ6LjGKVz6wdU4G9PAraojyzCYPJ1BXAg1XBwSPCMC6Ug6u5oom5zcLPUzWtz42aCp9PLGXpHweBjSu3EW2aDzsa4JoT
  [0]         Version: 3
         SerialNumber: 440724497
             IssuerDN: O=JDT,OU=ROOT,C=CN,ST=BJ,L=BJ,CN=ROOT,E=imuge@jd.com
           Start Date: Fri Sep 03 16:43:01 GMT+08:00 2021
           Final Date: Thu May 30 16:43:01 GMT+08:00 2024
            SubjectDN: O=JDT,OU=GW,C=CN,ST=BJ,L=BJ,CN=G1,E=imuge@jd.com
           Public Key: EC Public Key [c0:b9:58:d1:35:3d:a9:bc:1d:85:2a:ea:bf:57:80:39:e9:f6:57:6d]
            X: 67e4a4afe0a5beb1e5fb6e915314a9ed94b74f449cc4f50314ff78ecf62ba786
            Y: 2d5c233bfcd582f0c1098dbe4f1319db074fcf00023fdc9f3461a8d01488d9f2

  Signature Algorithm: SM3WITHSM2
            Signature: 3046022100b70107554a723ec96569bbb23c65cb
                       ac6d7934f47722aa50f18a5e9ca3a978b9022100
                       9b68e5f3bd14bf103248c8516c493e5e1d9a872c
                       39841c3704686ca85311bac0
```

#### CSR

生成证书请求文件
```bash
:bin$ ./jdchain-cli.sh ca csr -h
Create certificate signing request.
Usage: jdchain-cli ca csr [-hV] [--pretty] [--home=<path>] [-n=<name>]
                          [--priv=<privPath>] [--pub=<pubPath>]
  -h, --help              Show this help message and exit.
      --home=<path>       Set the home directory.
  -n, --name=<name>       Name of the key
      --pretty            Pretty json print
      --priv=<privPath>   Path of the private key file
      --pub=<pubPath>     Path of the public key file
  -V, --version           Print version information and exit.
```

- `name`，密钥对名称，创建公私钥请参照[keys](keys.md)文档说明

如使用`${home}/config/keys`下名为`ROOT`的公私钥信息创建`CSR`：
```bash
:bin$ ./jdchain-cli.sh ca csr -n ROOT
// 选择证书角色，输入对应数字即可，多个角色使用半角逗号相隔
input certificate roles (0 for ROOT, 1 for CA, 2 for PEER, 3 for GW, 4 for USER. multi values use ',' split):
> 1
input country:
> CN
input locality:
> BJ
input province:
> BJ
input organization name:
> JDT
input email address:
> imuge@jd.com
// 输入ROOT私钥密码
input password of the key:
> 1
create [${home}/config/keys/ROOT.csr] success
```
成功后会创建`${home}/config/keys/ROOT.csr`文件。

#### CRT

签发证书：
```bash
:bin$ ./jdchain-cli.sh ca crt -h
Create new certificate.
Usage: jdchain-cli ca crt [-hV] [--pretty] [--csr=<csrPath>] --days=<days>
                          [--home=<path>] [--issuer-crt=<issuerCrtPath>]
                          [--issuer-name=<issuerName>]
                          [--issuer-priv=<issuerPrivPath>] [-n=<name>]
      --csr=<csrPath>   Path of the certificate signing request file
      --days=<days>     Days of certificate validity
  -h, --help            Show this help message and exit.
      --home=<path>     Set the home directory.
      --issuer-crt=<issuerCrtPath>
                        Path of the issuer certificate file
      --issuer-name=<issuerName>
                        Name of the issuer key
      --issuer-priv=<issuerPrivPath>
                        Path of the issuer private key file
  -n, --name=<name>     Name of the certificate signing request file
      --pretty          Pretty json print
  -V, --version         Print version information and exit.
```

- `name`，`CSR`文件名，不为空时要求在`${home}/config/keys`目录下存在`${name.csr}`文件
- `csr`，`CSR`文件路径，与`name`二选一，优先使用`name`参数
- `days`，证书有效天数，当前签发时间开始计算
- `issuer-name`，签发者公私钥对名称，不为空时需要`${home}/config/keys`目录下至少存在`${issuer-name}.priv`，`${issuer-name}.crt`
- `issuer-crt`，签发者证书文件
- `issuer-priv`，签发者私钥文件
> `issuer-name`为空时，`issuer-crt`和`issuer-priv`必须同时提供


如使用`${home}/config/keys`下名为`ROOT`签发自签名证书：
```bash
./jdchain-cli.sh ca crt -n CA --issuer-name ROOT --days 1000
// 输入签发者私钥密码
input password of the issuer:
> 1
create [${home}/config/keys/ROOT.crt] success
```

#### 更新证书

仅可更新证书有效天数
```bash
Update validity period.
Usage: jdchain-cli ca renew [-hV] [--pretty] [--crt=<crtPath>] --days=<days>
                            [--home=<path>] [--issuer-crt=<issuerCrtPath>]
                            [--issuer-name=<issuerName>]
                            [--issuer-priv=<issuerPrivPath>] [-n=<name>]
      --crt=<crtPath>   File of the certificate
      --days=<days>     Days of certificate validity
  -h, --help            Show this help message and exit.
      --home=<path>     Set the home directory.
      --issuer-crt=<issuerCrtPath>
                        Path of the issuer certificate file
      --issuer-name=<issuerName>
                        Name of the issuer key
      --issuer-priv=<issuerPrivPath>
                        Path of the issuer private key file
  -n, --name=<name>     Name of the certificate
      --pretty          Pretty json print
  -V, --version         Print version information and exit.
```
- `name`，`CRT`文件名，不为空时要求在`${home}/config/keys`目录下存在`${name.crt}`文件
- `crt`，`CRT`文件路径，与`name`二选一，优先使用`name`参数
- `days`，证书有效天数，当前签发时间开始计算
- `issuer-name`，签发者公私钥对名称，不为空时需要`${home}/config/keys`目录下至少存在`${issuer-name}.priv`，`${issuer-name}.crt`
- `issuer-crt`，签发者证书文件
- `issuer-priv`，签发者私钥文件
> `issuer-name`为空时，`issuer-crt`和`issuer-priv`必须同时提供

如更新`${home}/config/keys`下名为`ROOT`证书有效期：
```bash
./jdchain-cli.sh ca crt -n ROOT --issuer-name ROOT --days 2000
input password of the issuer:
> 1
renew [${home}/config/keys/ROOT.crt] success success
```

#### 生成测试证书

一键生成可用于初始化`JD Chain`网络及使用需要的证书
```bash
:bin$ ./jdchain-cli.sh ca test -h
Create certificates for a testnet.
Usage: jdchain-cli ca test [-hV] [--pretty] [-a=<algorithm>]
                           --country=<country> --email=<email> [--gws=<gws>]
                           [--home=<path>] --locality=<locality>
                           [--nodes=<nodes>] --org=<organization>
                           [-p=<password>] --province=<province>
                           [--users=<users>]
  -a, --algorithm=<algorithm>
                             Crypto algorithm
      --country=<country>    Country
      --email=<email>        Email address
      --gws=<gws>            Gateway size
  -h, --help                 Show this help message and exit.
      --home=<path>          Set the home directory.
      --locality=<locality>  Locality
      --nodes=<nodes>        Node size
      --org=<organization>   Organization name
  -p, --password=<password>  Password of the key
      --pretty               Pretty json print
      --province=<province>  Province
      --users=<users>        Available user size
  -V, --version              Print version information and exit.
```
- `algorithm`，签名算法，默认`ED25519`，仅支持传入`ED25519`,  `RSA`，`ECDSA`，`SM2`之一
- `nodes`，共识节点个数，生成`nodes`个`PEER`类型的证书，可用于节点使用。默认：`4`
-  `gws`，网关节点个数，生成`gws`个`GW`类型的证书，可用于网关使用。默认：`1`
- `users`，用户个数，生成`users`可个可用于普通用户使用的证书。默认：`10`

如创建基于`SM2`签名算法的一个`ROOT`类型证书，`4`个节点证书，`1`个网关证书，`10`个用户证书：
```bash
:bin$ ./jdchain-cli.sh ca test --org JDT --country CN --locality BJ --province BJ --email jdchain@jd.com
input private key password:
// 输入操作过程中生成的私钥加密密码
> 1
create test certificates in [${home}/config/keys] success
```