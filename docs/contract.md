# 智能合约

### 1. 简介

JD Chain 智能合约系统由5个部分组成：合约代码语言、合约引擎、合约账户、合约开发框架、合约开发插件。

合约代码语言是用来编写智能合约的编程语言，合约引擎是解释和执行合约代码的虚拟机。

JD Chain 账本中以合约账户的方式对合约代码进行管理。一份部署上链的合约代码需要关联到一个唯一的公钥上，并生成与公钥对应的区块链账户地址，在账本中注册为一个合约账户。在执行之前，系统从账本中读出合约代码并将其加载到合约引擎，由交易执行器调用合约引擎触发合约执行。

JD Chain 账本定义了一组标准的账本操作指令，合约代码的执行过程实质上是向账本输出一串操作指令序列，这些指令对账本中的数据产生了变更，形成合约执行的最终结果。

合约开发框架定义了进行合约代码开发中需要依赖的一组编程接口和类库。合约开发插件提供了更方便与IDE集成的合约编译、部署工具，可以简化操作，并与持续集成过程结合。

JD Chain 以 Java 语言作为合约代码语言，合约引擎是基于 JVM 构建的安全沙盒。为了实现与主流的应用开发方式无缝兼容， JD Chain 支持以 Maven 来管理合约代码的工程项目，并提供相应的 maven 插件来简化合约的编译和部署。

>智能合约是一种可以由计算机执行的合同/协议。不同于现实生活中的合同是由自然语言来编写并约定相关方的权利和义务，智能合约是用合约代码语言来编写，以合约代码的形式存在和被执行。通过账本中的数据状态来表示合同/协议相关条款信息，合约代码的运行过程体现了合同/协议条款的执行，并记录相应的结果。

### 2. 快速入门

#### 2.1. 准备开发环境 

按照正常的 Java 应用开发环境要求进行准备，以 Maven 作为代码工程的构建管理工具，无其它特殊要求。

>检查 JDK 版本不低于 1.8 ，Maven 版本不低于 3.0。

#### 2.2. 创建合约代码工程
创建一个普通的 Java Maven 工程，打开 pom.xml 把 packaging 设为 contract .

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>your.group.id</groupId>
  <artifactId>your.project</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <!-- 声明为合约代码工程，编译输出扩展名为".car"合约代码 -->
  <packaging>contract</packaging>

  <dependencies>
     <!-- 合约项目的依赖 -->
  </dependencies>

  <build>
     <plugins>
        <!-- 合约项目的插件 -->
     </plugins>
  </build>
</project>
```

> 注：合约代码工程也是一个普通的 Java Maven 工程，因此尽管不同 IDE 创建 Maven 工程有不同的操作方式，由于对于合约开发而言并无特殊要求，故在此不做详述。

#### 2.3. 加入合约开发依赖

在合约代码工程 pom.xml 加入对合约开发 SDK 的依赖：

 ```xml
<dependency>
    <groupId>com.jd.blockchain</groupId>
    <artifactId>contract-starter</artifactId>
    <version>${jdchain.version}</version>
</dependency>
 ```

#### 2.4. 加入合约插件

在合约代码工程的 pom.xml 加入 contract-maven-plugin 插件：
 ```xml
<plugin>
   <groupId>com.jd.blockchain</groupId>
   <artifactId>contract-maven-plugin</artifactId>
   <version>${jdchain.version}</version>
   <extensions>true</extensions>
</plugin>
 ```

完整的 pom.xml 如下：

 ```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>your.group.id</groupId>
  <artifactId>your.project</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <!-- 声明为合约代码工程，编译输出扩展名为".car"合约代码 -->
  <packaging>contract</packaging>

  <properties>
     <jdchain.version>1.4.2.RELEASE</jdchain.version>
  </properties>
  
  <dependencies>
     <!-- 合约项目的依赖 -->
     <dependency>
        <groupId>com.jd.blockchain</groupId>
        <artifactId>contract-starter</artifactId>
        <version>${jdchain.version}</version>
     </dependency>
  </dependencies>

  <build>
     <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
                <optimize>false</optimize>
                <debug>true</debug>
                <showDeprecation>false</showDeprecation>
                <showWarnings>false</showWarnings>
            </configuration>
        </plugin>

        <!-- 合约项目的插件 -->
        <plugin>
           <groupId>com.jd.blockchain</groupId>
           <artifactId>contract-maven-plugin</artifactId>
           <version>${jdchain.version}</version>
           <extensions>true</extensions>
        </plugin>
     </plugins>
  </build>
</project>
 ```

#### 2.5. 编写合约代码

2.5.1. **注意事项**

1. 不允许合约（包括合约接口和合约实现类）使用com.jd.blockchain开头的package；
2. 必须有且只有一个接口使用@Contract注解，且其中的event必须大于等于一个；
3. 使用@Contract注解的接口有且只有一个实现类；
4. 黑名单调用限制（具体黑名单可查看配置文件），需要注意的是，黑名单分析策略会递归分析类实现的接口和父类，也就是说调用一个实现了指定黑名单接口的类也是不允许的；
   

目前设置的黑名单如下：
```conf
java.io.File
java.io.InputStream
java.io.OutputStream
java.io.DataInput
java.io.DataOutput
java.io.Reader
java.io.Writer
java.io.Flushable
java.nio.channels.*
java.nio.file.*
java.net.*
java.sql.*
java.lang.reflect.*
java.lang.Class
java.lang.ClassLoader
java.util.Random
java.lang.System-currentTimeMillis
java.lang.System-nanoTime
com.jd.blockchain.ledger.BlockchainKeyGenerator
```

2.5.2. **声明合约**

```java
/**
 * 声明合约接口；
**/ 
@Contract
public interface AssetContract {

   @ContractEvent(name = "transfer")
   String transfer(String address, String from, String to, long amount);
   
}
```

2.5.3. **实现合约**

```java
/**
 * 实现合约；
 * 
 * 实现 EventProcessingAware 接口是可选的，目的获得 ContractEventContext 上下文对象，
 * 通过该对象可以进行账本操作；
 */
public class AssetContractImpl implements AssetContract, EventProcessingAware {
   
   // 合约事件上下文；
   private ContractEventContext eventContext;

   /**
    * 执行交易请求中对 AssetContract 合约的 transfer 调用操作；
    */
   public String transfer(String address, String from, String to, long amount) {
      //当前账本的哈希；
      HashDigest ledgerHash = eventContext.getCurrentLedgerHash();
      //当前账本上下文；
      LedgerContext ledgerContext = eventContext.getLedger();

      //做操作；
      // ledgerContext.

      //返回合约操作的结果；
      return "success";
   }

   /**
    * 准备执行交易中的合约调用操作；
    */
   @Override
   public void beforeEvent(ContractEventContext eventContext) {
      this.eventContext = eventContext;
   }

   /**
    * 完成执行交易中的合约调用操作；
    */
   @Override
   public void postEvent(ContractEventContext eventContext, Exception error) {
      this.eventContext = null;
   }
}
```

**账本数据可见范围**：

`ContractEventContext`中`getUncommittedLedger`方法可访问执行中的未提交区块数据，此方法的合理使用可以解决客户并发调用合约方法涉及数据版本/事件序列冲突的问题。
```java
/**
 * 当前包含未提交区块数据账本查询上下文；
 */
LedgerQueryService getUncommittedLedger();
```

`ContractEventContext`中`getLedger`方法访问的是链上已提交的最新区块数据，不包含未提交交易，所以存在未提交交易中多个合约方法调用操作间数据不可见，导致并发时数据版本等冲突问题。
```java
/**
 * 账本操作上下文；
 */
LedgerContext getLedger();
```

合约方法中对账本的操作通过调用`LedgerContext`中相关方法，可参照[示例合约](https://github.com/blockchain-jd-com/jdchain/tree/master/samples/contract-samples/src/main/java/com/jdchain/samples/contract)

#### 2.6. 编译打包合约代码

合约代码工程的编译打包操作与普通的 maven 工程是相同的，在工程的根目录下输入以下命令：

```bash
mvn clean package
```

执行成功之后，在 target 目录中输出合约代码文件 \<project-name>.\<version>.car 。

如果合约代码加入了除 com.jd.blockchain:contract-starter 之外的其它依赖，默认配置下，第三方依赖包将与 .car 文件一起打包一起部署。（也可以把第三方依赖包独立打包，具体参见以下 “3. 合约插件详细配置”

> 注意：合约代码虽然利用了 Java 语言，遵照 Java 语法进行编写，但本质上是作为一种运行于受限环境（合约虚拟机）的语言来使用，因而一些 Java 语法和 SDK 的 API 是不被允许使用的，在编译过程中将对此进行检查。

#### 2.7. 部署合约代码

##### 2.7.1. 在项目中部署合约代码

如果希望在构建打包的同时将合约代码部署到指定的区块链网络，可以在合约代码工程 pom.xml 的 contract-maven-plugin 插件配置中加入合约部署相关的信息（具体更详细的配置可以参考“3. 合约插件详细配置”）。

```xml
<plugin>
   <groupId>com.jd.blockchain</groupId>
   <artifactId>contract-maven-plugin</artifactId>
   <version>1.2.0.RELEASE</version>
   <extensions>true</extensions>
   <configuration>
      <!-- 合约部署配置 -->
      <deployment>
         <!-- 合约要部署的目标账本的哈希；Base58 格式； -->
         <ledger>j5rpuGWVxSuUbU3gK7MDREfui797AjfdHzvAMiSaSzydu7</ledger>

         <!-- 区块链网络的网关地址 -->
         <gateway>
            <host>192.168.10.10</host>
            <port>8081</port>
         </gateway>

         <!-- 合约账户 -->
         <ContractAddress>
             <pubKey>7VeRMpXVeTY4cqPogUHeNoZNk86CGAejBh9Xbd5ndFZXNFj3</pubKey>
         </ContractAddress>

         <!-- 合约部署交易的签名账户；该账户必须具备合约部署的权限； -->
         <signer>
            <pubKey>7VeRLdGtSz1Y91gjLTqEdnkotzUfaAqdap3xw6fQ1yKHkvVq</pubKey>
            <privKey>177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x</privKey>
            <privKeyPwd>DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY</privKeyPwd>
         </signer>
      </deployment>
   </configuration>
</plugin>
```

加入部署配置信息之后，对工程执行编译打包操作，输出的合约代码（.car）将自动部署到指定的区块链网络。

```bash
mvn clean deploy
```
##### 2.7.2. 发布已编译好的car
如果已经通过插件的打包方式，编译打包完成一个合约文件（.car），可通过命令行的方式进行发布，命令行要求与开发环境一致的Maven环境（包括环境变量及Setting都已配置完成）。

```bash
mvn com.jd.blockchain:contract-maven-plugin:${version}:deploy
      -DcarPath=
      -Dledger=
      -DgatewayHost=
      -DgatewayPort=
      -DcontractPubKey=
      -DcontractAddress=
      -DsignerPubKey=
      -DsignerPrivKey=
      -DsignerPrivKeyPwd=
```

各参数说明如下：

|  参数名   | 含义  |是否必填|
|  ----  | ----  |  ----  |
| ${version}  | 合约插件的版本号 | 否，系统会自动选择发布的最新的RELEASE版本，SNAPSHOT版本必须填写 |
| carPath  | 合约文件所在路径 | 是 |
| ledger  | 账本Hash（Base58编码） | 否，会自动选择线上第一个账本|
| gatewayHost  | 可访问的网关节点地址，域名或IP地址 | 是|
| gatewayPort  | 网关节点监听端口 | 是 |
| contractPubKey  | 合约账户的公钥（Base58编码）| 否，会自动创建 |
| contractAddress  | 合约账户的地址（Base58编码）|否，会根据contractPubKey生成|
| signerPubKey  | 合约签名公钥信息（Base58编码）|是|
| signerPrivKey  | 合约签名私钥信息（Base58编码）|是|
| signerPrivKeyPwd  | 合约签名私钥解密密钥（Base58编码）|是|


下面是一个示例，供参考：

```bash
mvn com.jd.blockchain:contract-maven-plugin:1.2.0.RELEASE:deploy \
      -DcarPath=/root/jdchain/contracts/contract-test-1.0-SNAPSHOT.car \
      -Dledger=j5tW5HUvMjEtm2yB7E6MHoSByoH1DXvMwvF2HurEgMSaLW \
      -DgatewayHost=127.0.0.1 \
      -DgatewayPort=11000 \
      -DcontractPubKey= 7VeRBsHM2nsGwP8b2ufRxz36hhNtSqjKTquzoa4WVKWty5sD \
      -DcontractAddress= LdeNt7sEmTirh9PmE7axKvA2txTrbB9kxz6KB \
      -DsignerPubKey=7VeRLdGtSz1Y91gjLTqEdnkotzUfaAqdap3xw6fQ1yKHkvVq \
      -DsignerPrivKey=177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x \
      -DsignerPrivKeyPwd=DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY
```

> 重点说明：
命令行中输入参数的优先级高于配置文件，就是说通过2.7.1方式发布合约时也可以采用命令行的参数（指-D相关配置），其优先级高于配置文件。

### 3. 合约插件详细配置

```xml
<plugin>
   <groupId>com.jd.blockchain</groupId>
   <artifactId>contract-maven-plugin</artifactId>
   <version>1.2.0.RELEASE</version>
   <extensions>true</extensions>
   <configuration>
      <!-- 是否把所有的依赖项打包输出到一个独立的 “库文件(.lib)”，默认为 false-->
      <!-- 设置为 false 时 ，合约代码和依赖项一起打包输出到 “合约代码文件(.car)” -->
      <!-- 设置为 true ，合约代码和依赖项分别打包，分别输出 “合约代码文件(.car)” 和 “库文件(.lib)” -->
      <!-- 注：
            1. 如果“合约代码文件(.car)”的尺寸超出最大尺寸将引发异常，可把此项配置设置为 true 以减小“合约代码文件(.car)”的尺寸。
            2. “合约代码文件(.car)”的默认最大尺寸为 1 MB，由区块链网络的配置设定，如果不满足则需要由区块链网络的管理员进行调整。 
            3. “合约库文件（.lib）”的尺寸不受“合约代码文件(.car)”的最大尺寸限制，部署过程只有“哈希上链”，库文件通过链下的分发网络自动同步至各个共识节点。
            -->
      <outputLibrary>false</outputLibrary>

      <!-- 合约代码最大字节数；可选；--> 
      <!-- 默认为 1 (MB)；如果超出该值将给予错误提示；如果值小于等于 0，则不做校验 --> 
      <!-- 注：此参数仅影响编译打包时的本地校验，实际部署时仍然由区块链网络上的配置决定 -->
      <maxCarSize>1</maxCarSize>
      <!-- 合约代码最大字节数的单位；-->
      <!-- 合法值的格式为“整数值+单位”；可选单位有： Byte, KB, MB；不区分大小写;-->
      <maxCarSizeUnit>MB</maxCarSizeUnit>

      <!-- 合约部署配置；可选 -->
      <deployment>
         <!-- 账本的哈希；Base58 格式；非必填项，会自动选择线上第一个账本 -->
         <ledger></ledger>

         <!-- 区块链网络的网关地址 -->
         <gateway>
            <host></host>
            <port></port>
         </gateway>

         <!-- 合约账户 -->
         <!-- 合约账户的地址address（Base58编码）,会根据pubKey生成> -->
         <contractAddress>
              <pubKey></pubKey>
              <address></address>
          </contractAddress>

         <!-- 合约部署交易的签名账户；该账户必须具备合约部署的权限； -->
         <signer>
            <!-- 账户公钥；Base58 格式； -->
            <pubKey></pubKey>
            <!-- 账户私钥；Base58 格式； -->
            <privKey></privKey>
            <!-- 账户私钥解密密码；Base58 格式； -->
            <privKeyPwd></privKeyPwd>
         </signer>
      </deployment>
   </configuration>
</plugin>
```

### 4. 最简化合约插件配置示例

在pom.xml中有部分配置是非必填项，下面是一份最简化的合约发布（deploy）配置示例，供参考：


```xml
<plugin>
   <groupId>com.jd.blockchain</groupId>
   <artifactId>contract-maven-plugin</artifactId>
   <version>1.2.0.RELEASE</version>
   <extensions>true</extensions>
   <configuration>
      <!-- 合约部署配置-->
      <deployment>
         <!-- 区块链网络的网关地址 -->
         <gateway>
            <host>127.0.0.1</host>
            <port>8081</port>
         </gateway>

         <!-- 合约部署交易的签名账户；该账户必须具备合约部署的权限； -->
         <signer>
            <pubKey>7VeRLdGtSz1Y91gjLTqEdnkotzUfaAqdap3xw6fQ1yKHkvVq</pubKey>
            <privKey>177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x</privKey>
            <privKeyPwd>DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY</privKeyPwd>
         </signer>
      </deployment>
   </configuration>
</plugin>
```

### 5. 合约SDK

除上述使用 maven 命令方式部署合约外，JD Chain SDK 提供了 Java 和 Go 语言的合约部署/升级，合约调用等方法。
以下以 Java SDK 为例讲述主要步骤，完整代码参照[JD Chain Samples](https://github.com/blockchain-jd-com/jdchain/tree/master/samples)合约部分。

#### 5.1 合约部署

```java
// 新建交易
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
// 生成合约账户
BlockchainKeypair contractAccount = BlockchainKeyGenerator.getInstance().generate();
System.out.println("合约地址：" + contractAccount.getAddress());
// 部署合约
txTemp.contracts().deploy(contractAccount.getIdentity(), FileUtils.readBytes("src/main/resources/contract-samples-1.4.2.RELEASE.car"));
```

#### 5.2 合约升级

```java
// 新建交易
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
// 解析合约身份信息
BlockchainIdentity contractIdentity = new BlockchainIdentityData(KeyGenUtils.decodePubKey("7VeRCfSaoBW3uRuvTqVb26PYTNwvQ1iZ5HBY92YKpEVN7Qht"));
System.out.println("合约地址：" + contractIdentity.getAddress());
// 指定合约地址，升级合约，如合约地址不存在会创建该合约账户
txTemp.contracts().deploy(contractIdentity, FileUtils.readBytes("src/main/resources/contract-samples-1.4.2.RELEASE.car"));
```

#### 5.3 合约调用

5.3.1 动态代理方式

基于动态代理方式合约调用，需要依赖合约接口

```java
// 新建交易
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 一次交易中可调用多个（多次调用）合约方法
// 调用合约的 registerUser 方法
SampleContract sampleContract = txTemp.contract("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", SampleContract.class);
GenericValueHolder<String> userAddress = ContractReturnValue.decode(sampleContract.registerUser(UUID.randomUUID().toString()));

// 准备交易
PreparedTransaction ptx = txTemp.prepare();
// 交易签名
ptx.sign(adminKey);
// 提交交易
TransactionResponse response = ptx.commit();
Assert.assertTrue(response.isSuccess());

// 获取返回值
System.out.println(userAddress.get());
```

5.3.2 非动态代理方式

不需要依赖合约接口及实现，传入参数构造合约调用操作

```java
// 新建交易
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

ContractEventSendOperationBuilder builder = txTemp.contract();

// 一次交易中可调用多个（多次调用）合约方法
// 调用合约的 registerUser 方法，传入合约地址，合约方法名，合约方法参数列表
builder.send("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", "registerUser",
        new BytesDataList(new TypedValue[]{
                TypedValue.fromText(UUID.randomUUID().toString())
        })
);
// 准备交易
PreparedTransaction ptx = txTemp.prepare();
// 交易签名
ptx.sign(adminKey);
// 提交交易
TransactionResponse response = ptx.commit();
Assert.assertTrue(response.isSuccess());

Assert.assertEquals(1, response.getOperationResults().length);
// 解析合约方法调用返回值
for (int i = 0; i < response.getOperationResults().length; i++) {
    BytesValue content = response.getOperationResults()[i].getResult();
    switch (content.getType()) {
        case TEXT:
            System.out.println(content.getBytes().toUTF8String());
            break;
        case INT64:
            System.out.println(BytesUtils.toLong(content.getBytes().toBytes()));
            break;
        case BOOLEAN:
            System.out.println(BytesUtils.toBoolean(content.getBytes().toBytes()[0]));
            break;
        default: // byte[], Bytes
            System.out.println(content.getBytes().toBase58());
            break;
    }
}
```