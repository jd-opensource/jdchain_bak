以下四种方式均可初始化/运行`JD Chain`网络，组网过程有难易，需要开发者细心操作，操作过程中遇到问题，可随时与我们联系。

### 1. 官方完整步骤

`JD Chain`官网提供了[安装部署](http://ledger.jd.com/setup.html)详细介绍，较为繁琐，但是其他便捷组网方法的基础。

### 2. 管理工具

`JD Chain`提供了基于界面操作的网络初始化启动工具，相关脚本为`manager-startup.sh`和`manager-shutdown.sh`。

送上操作视频：http://storage.jd.com/jd.block.chain/init-jdchain-by-manager-tool.mp4

### 3. 基于内存的四节点网络

1. 克隆[ JD Chain主项目](https://github.com/blockchain-jd-com/jdchain)源码，并切换到对应版本分支

请查阅**主项目首页介绍**，里面有子项目代码拉取，项目编译打包的介绍。代码根路径下执行：
```bash
build/build.sh --update --skipTests
```
即可完成所有子项目代码拉取，完成编译打包


2. 运行[Samples](https://github.com/blockchain-jd-com/jdchain/tree/master/samples)模块下代码

参照[Samples介绍](https://github.com/blockchain-jd-com/jdchain/tree/master/samples)
运行`sdk-samples`里的`TestNet`类`main`方法即可启动基于内存的四节点+单网关区块链网络环境，浏览器地址为`http://localhost:11000`。

`sdk-samples`中测试用例默认基于`TestNet`启动的网络环境配置，都可直接运行。覆盖绝大多数交易类型提交，交易查询。

###  4. 基于安装包和部署脚本

1. 下载`JD Chain`安装包

安装包获取途径：

- 下载编译[`JD Chain`源码](https://github.com/blockchain-jd-com/jdchain)，参照首页说明进行编译打包。
- 访问[JD Chain官网](http://ledger.jd.com/downloadapps.html)下载，版本更新可能不及源码快。

2. 脚本初始化

复制[testnet.sh](scripts/testnet.sh)脚本，保存到本地，设置可运行权限

> 脚本仅在特定的`linux`环境下测试通过，不同系统环境可能存在`shell`语句或者依赖差异，请酌情修改

> 此脚本可一键生成多节点，多账本，目前还相当粗糙，仅当抛砖引玉～

将`jdchain-peer-*.RELEASE.zip`，`jdchain-gateway-*.RELEASE.zip`压缩包以及`testnet.sh`脚本放置同一目录下。

直接运行`testnet.sh`便可自动初始化默认四节点+单网关的环境，同时生成一键启动（`start.sh`）和关闭（`shutdown.sh`）的脚本。

运行`start.sh`便可启动测试网络，参照[JD Chain Samples](https://github.com/blockchain-jd-com/jdchain/tree/master/samples)介绍，配置好网络环境参数，即可快速上手`JD Chain SDK`使用。
