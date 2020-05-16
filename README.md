[TOC]
#JD区块链

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jd.blockchain/sdk-pack/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jd.blockchain/sdk-pack/)
[![Build Status](https://travis-ci.com/blockchain-jd-com/jdchain.svg?branch=master)](https://travis-ci.org/blockchain-jd-com/jdchain)



------------------------------------------------------------------------

## 一、项目介绍
JD Chain 的目标是实现一个面向企业应用场景的通用区块链框架系统，能够作为企业级基础设施，为业务创新提供高效、灵活和安全的解决方案。


## 二、部署模型

JD Chain 主要部署组件包括以下几种：

- 共识节点
    
    共识节点即参与共识的节点，这是系统的核心组件，承担了运行共识协议、管理账本数据、运行智能合约的职责。
    
    一个区块链网络由多个共识节点组成，共识节点的数量范围由选择的共识协议决定。

    共识节点和账本是两个不同的概念，共识节点是个物理上的概念，账本是个逻辑上的概念。JD Chain 是一个多账本区块链系统，一个共识节点上可以装载运行多个账本。账本是数据维度的独立管理单元。共识节点和账本的关系，就像关系数据库系统中，数据库服务器和数据库实例的关系。

    共识节点通常都部署在参与方的内部网络中，通过由网络管理员指定的安全的网络出口与其它的共识节点建立通讯连接。

    共识节点在形态上是服务器中的一个处理进程，背后需要连接一个本地或者内网的NoSQL数据库系统作为账本的存储。当前版本，共识节点目前是单进程的，未来版本将实现多进程以及多服务器集群模式。

- 网关节点

    网关节点是负责终端接入的节点，负责终端连接、协议转换、交易准入、本地密码运算、密钥管理等职责。

    网关节点是一种轻量节点，需要绑定一个特定参与方的密钥对，连接到一个或多个共识节点。

    网关节点向共识节点的连接是需要通过认证的，绑定的参与方的密钥对必须事先已经注册到区块链账本中，且得到接入授权。

- 终端

    终端泛指可以提交交易的客户端，典型来说，包括人、自动化设备、链外的信息系统等。

    终端只能通过网关节点来提交交易。终端提交的交易需要用体现该终端身份的私钥来签署，产生一份电子签名。随后当交易提交给网关节点时，网关节点需要在把交易提交到共识节点之前，对交易请求以网关节点绑定的私钥追加一项“节点签名”。


- 备份节点

    仅对账本数据提供备份，但不参与交易共识的节点。（注：目前版本中尚未实现，将在后续版本中提供）


![](docs/images/deployment.jpg)


## 三、构建源代码

1. 安装 Maven 环境

    JD Chain 当前版本以 Java 语言开发，需要安装配置 JVM 和 Maven，JDK 版本不低于1.8 。(没有特殊要求，请按标准方法安装，此处不赘述)
 
2. 安装 Git 工具
    
    为了能够执行 git clone 命令获取代码仓库。 (没有特殊要求，请按标准方法安装，此处不赘述)
 
3. 项目库说明

JD Chain 源代码包括 3 个代码仓库

- jdchain 项目库：
  - URL：git@github.com:blockchain-jd-com/jdchain.git
  - 说明：主项目库，包含说明文档、示例代码，用于集成构建和打包；   
  
 #### `主项目库包含以下 6 个子模块仓库，通过执行脚本 <主项目库根目录>/build/build.sh 便可以一键完成子模块的下载和整体的编译、测试和打包操作.`

- project 项目库：
  - URL：git@github.com:blockchain-jd-com/jdchain-project.git
  - 说明：公共的父项目，定义公共的依赖；
- framework 项目库：
  - URL：git@github.com:blockchain-jd-com/jdchain-framework.git
  - 说明：框架源码库，定义公共数据类型、框架、模块组件接口、SDK、SPI、工具；
- core 项目库：
  - URL：git@github.com:blockchain-jd-com/jdchain-core.git
  - 说明：模块组件实现的源码库；
- explorer 项目库：
  - URL：git@github.com:blockchain-jd-com/explorer.git
  - 说明：相关产品的前端模块的源码库；
- libs/bft-smart 项目库：
  - URL：git@github.com:blockchain-jd-com/bftsmart.git
  - 说明：BFT-SMaRT 共识算法的源码库；
- test 项目库：
  - URL：git@github.com:blockchain-jd-com/jdchain-test.git
  - 说明：集成测试用例的源码库；


4. 构建操作


```sh
$ git clone git@github.com:blockchain-jd-com/jdchain.git jdchain

$ cd jdchain

$ git checkout develop

$ chmod +x build/*.sh

# 执行完整的构建，包括执行”集成测试“和”打包“两部分；提供两个参数：
# --skipTests ：跳过集成测试部分； 
# --update ：从远程仓库更新子模块。注意，采用此参数会导致子模块本地仓库丢失尚未 commit 的代码。
#           不附带此参数的情况下不会更新子模块仓库。
$ build/build.sh --update

# 跳过集成测试，直接编译和打包；
$ build/build.sh --skipTests

# 只执行集成测试；
$ build/test.sh
    
```

5.  jdchain 的安装包

当编译完成后，安装包位于主项目库的 deploy 目录中：

- 共识节点的安装包：
  -  <主项目库根目录>/deploy/deploy-peer/target/jdchain-peer-**.zip
- 网关节点的安装包:
  -  <主项目库根目录>/deploy/deploy-gateway/target/jdchain-gateway-**.zip