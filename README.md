![logo](http://storage.jd.com/jd.block.chain/jdt-jdchain.png)

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jd.blockchain/sdk-pack/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jd.blockchain/sdk-pack/)
[![Build Status](https://travis-ci.com/blockchain-jd-com/jdchain.svg?branch=master)](https://travis-ci.org/blockchain-jd-com/jdchain)

> **管理工具(`manager-startup.sh`)不再推荐使用，1.6.3版本已完全移除管理工具相关代码，组网和节点管理请参照最新官方文档，使用命令行方式更安全更便捷！**

> **管理工具设计初衷仅做网络初始化使用，可执行节点启停操作，本身不涉及鉴权逻辑，使用旧版本管理工具的用户请停止管理工具服务进程或做好严格的外部访问控制。**

> **不要对外暴露管理工具访问地址！！！**

一个面向企业应用场景的通用区块链框架系统，能够作为企业级基础设施，为业务创新提供高效、灵活和安全的解决方案。

## 源码构建

`JD Chain`源码通过`git`及`git submodule`进行管理，如下操作可快速构建：

```bash
$ git clone https://github.com/blockchain-jd-com/jdchain.git jdchain

$ cd jdchain

# 此处仅以 master 分支为例，正常情况下 master 分支可无障碍构建成功
# 不推荐使用 develop 分支，submodule 代码可能未对齐
# 推荐切换到具体已发布的版本分支
$ git checkout master

$ chmod +x build/*.sh

# 执行完整的构建，包括执行”集成测试“和”打包“两部分；提供两个参数：
# --skipTests ：跳过集成测试部分； 
# --update ：从远程仓库更新子模块。注意，采用此参数会导致子模块本地仓库丢失尚未 commit 的代码。
#           不附带此参数的情况下不会更新子模块仓库。
$ build/build.sh --update

# 跳过子模块代码更新和集成测试，直接编译和打包；
$ build/build.sh --skipTests

# 首次代码拉取，跳过集成测试和编译打包可执行：
$ build/build.sh --update --skipTests
```

构建完成后会在`deploy`模块，`deploy-gateway`和`deploy-peer`目录`target`中生成网关安装部署包（`jdchain-gateway-*.zip`）和共识节点安装部署包（`jdchain-peer-*.zip`）。


## 部署使用

### 快速部署

使用[源码构建](#源码构建)生成的部署安装包，或者下载[官方部署安装包](https://blockchain-jd-com.github.io/#/download) 参照[快速部署文档](https://blockchain-jd-com.github.io/#/install/testnet?id=%e5%91%bd%e4%bb%a4%e8%a1%8c%e6%96%b9%e5%bc%8f%e3%80%90%e6%8e%a8%e8%8d%90%e3%80%91)可快速部署运行`JD Chain`网络。

### 数据上链

1. 命令行工具

`JD Chain` 命令行工具集，即[jdchain-cli](https://blockchain-jd-com.github.io/#/cli/tx)，可快速执行数据上链和链上数据查询。

2. SDK

`JD Chain`提供了`Java`和`Go`版本的`SDK`。实际项目开发中`Java`可参照[示例代码](https://github.com/blockchain-jd-com/jdchain-samples)，`Go`语言`SDK`参照[framework-go](https://github.com/blockchain-jd-com/framework-go/blob/master/sdk/test/tx_test.go)。

### 更多

`JD Chain`功能开发，使用问题等欢迎`issue`中探讨，也欢迎广大开发者积极参与`JD Chain`社区活动及代码开发~

- 文档：[docs](https://blockchain-jd-com.github.io)
- `JD Chain`官网：https://ledger.jd.com/
- 京东智臻链官网：https://blockchain.jd.com/
- 服务邮箱：jdchain-support@jd.com
