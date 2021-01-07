## JD Chain Samples

本项目为`JD Chain SDK`的使用样例，开发者可以参考此项目快速上手`JD Chain SDK`，主要包括[交易发送查询](#交易发送查询)，[合约开发部署](#合约开发部署)两部分。

本项目提供了基于内存的`JD Chain`四节点+网关的网络环境启动程序[TestNet](/sdk-samples/src/main/java/com/jdchain/samples/sdk/TestNet.java)，运行`TestNet`的`main`方法启动测试网络，等待日志输出：`START TESTNET SUCCESS`，网络启动成功会写入一些测试数据，可直接运行本项目提供的所有测试用例。

> `TestNet`测试网络默认会占用`8910`/`8920`/`8930`/`8940`/`8911`/`8921`/`8931`/`8941`用于共识服务，`12000`/`12010`/`12020`/`12030`用于四节点`API`服务端口，`11000`用于网关`API`服务端口，启动前请检查相关端口可用。



### 交易发送查询

相关代码放在[sdk-sample](/sdk-samples/src)下。

> 若并非使用`TestNet`启动的测试网络，开发者在运行本样例前，请根据实际环境修改[config.properties](/sdk-samples/src/main/resources/config.properties)中的网关配置，用户配置等信息。

#### 交易发送

参照[UserSample](/sdk-samples/src/test/java/com/jdchain/samples/sdk/UserSample.java)实现注册用户，配置用户角色权限功能；

参照[DataAccountSample](/sdk-samples/src/test/java/com/jdchain/samples/sdk/DataAccountSample.java)实现注册数据账户，存储`KV`数据功能；

参照[EventSample](/sdk-samples/src/test/java/com/jdchain/samples/sdk/EventSample.java)实现注册事件账户，发布事件，事件监听功能；

参照[ContractSample](/sdk-samples/src/test/java/com/jdchain/samples/sdk/ContractSample.java)实现合约调用，非插件方式合约部署功能。

#### 数据查询

参照[QuerySample](/sdk-samples/src/test/java/com/jdchain/samples/sdk/QuerySample.java)实现对于区块链上数据查询功能。



### 合约开发部署

[contract-samples](/contract-samples/src)提供了通过合约注册用户，注册数据账户，注册事件账户，设置`KV`，发布事件的简单合约样例。

> 若并非使用`TestNet`启动的测试网络，开发者在运行本样例前，请根据实际环境修改[pom.xml](/contract-samples/pom.xml)中的网关配置，用户配置等信息。

修改相关代码，确认配置正确，`contract-samples`项目目录下命令行执行：

- 合约打包
```bash
mvn clean package
```
可以生成`car`包，可以用于`SDK`方式合约部署。

- 合约部署
```bash
mvn clean deploy
```
可以直接部署合约上链。



### 了解更多

访问[JD Chain官网](http://ledger.jd.com/)查阅设计及文档。
访问[github主页](https://github.com/blockchain-jd-com)阅读`JD Chain`源码并参与社区建设。

Thanks~