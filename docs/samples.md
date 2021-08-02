## 示例代码

克隆[JD Chain主项目](https://github.com/blockchain-jd-com/jdchain)，切换到指定分支。

[Samples](https://github.com/blockchain-jd-com/jdchain/tree/master/samples) 中提供了 `用户`，`数据账户`，`合约`，`事件`，`查询API`相关使用。

### 依赖 TestNet

`Samples`项目中提供了基于内存的四节点加单网关的网络环境初始化和启动方式（`TestNet`类），若需要此运行环境，请执行：

```bash
build/build.sh --update --skipTests
```
将项目子项目及依赖都更新到指定分支对应版本，并完成编译打包。

执行`TestNet`类`main`即可启动测试网络，网络成功启动后可执行`sdk-samples`中所有测试用例。

### 不依赖 TestNet

对于已有`JD Chain`测试网络，不使用`TestNet`的情况，开发者可以只导入`Samples`项目，并删除`TestNet`相关的包和类，去除`pom.xml`中以下依赖：
```xml
<!--以下依赖用于 com.jdchain.samples.Network 中四节点网路环境初始化和启动 -->
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.9.12</version>
</dependency>
<dependency>
    <groupId>com.jd.blockchain</groupId>
    <artifactId>tools-initializer</artifactId>
    <version>${framework.version}</version>
</dependency>
<dependency>
    <groupId>com.jd.blockchain</groupId>
    <artifactId>peer</artifactId>
    <version>${framework.version}</version>
</dependency>
<dependency>
    <groupId>com.jd.blockchain</groupId>
    <artifactId>gateway</artifactId>
    <version>${framework.version}</version>
</dependency>
```

修改`resources`中关于网络的相关参数，即可运行所有测试用例。