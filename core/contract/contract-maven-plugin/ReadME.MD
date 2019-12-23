# 合约编译插件使用教程

### 1、maven引入

在pom.xml文件中引入合约编译插件：
```xml
<plugin>
    <groupId>com.jd.blockchain</groupId>
    <artifactId>contract-maven-plugin</artifactId>
    <version>1.0.0.RELEASE</version>
    <executions>
        <execution>
            <id>make-contract</id>
            <phase>package</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <archive>
            <manifest>
                <mainClass>com.jd.chain.contracts.ContractTestInfImpl</mainClass>
            </manifest>
        </archive>
        <finalName>contract</finalName>
    </configuration>
</plugin>

```

需要说明的几点如下：
 + 1）version：请根据实际JDChain发布版本确认，不同版本会有区别；
 + 2）executions->execution->id：该值请随意指定；
 + 3）executions->execution->phase：建议使用package及其后续阶段（若不了解phase含义，请自行查阅相关信息）；
 + 4）executions->execution->goals->goal：必须使用compile；
 + 5）mainClass：必填，该类为需要发布的合约执行类（注意此处是类，不是接口），必须正确配置；
 + 6）finalName：必填，最终在编译正常的情况下，会产生{finalName}-JDChain-Contract.jar文件，只有该文件是可以发布到JDChain的合约包；


### 2、执行命令
使用mvn执行命令，下面两种方式均可：

方式一：只执行contract插件命令
```shell
 mvn clean compile contract:compile
```

方式二：直接执行打包命令：
```shell
mvn clean package
```


### 3、合约编写要求
合约的执行结果会对整条链产生比较深刻的影响，为了使用户能够更好、更合理的使用合约，目前JDChain约定合约编写规则包括以下几点：

（违反其中任何一点都可能导致合约编译失败，但即使合约编译通过也不能保证合约可百分百运行正常）

 + 1）合约工程必须引入com.jd.blockchain:sdk-pack：该包中有合约正常编写需要使用的基本类；
 + 2）com.jd.blockchain:sdk-pack的scope必须定义为provided；
 + 3）合约发布必须通过合约编译插件进行打包：合约编译插件不但会对Jar包进行校验，同时也会加入JDChain独有的特征，只有具有该特征的Jar才能正常发布；
 + 4）合约中严禁使用随机数、IO、NIO等操作；
 + 5）合约打包时，请使用<scope>provided</scope>排除常用的工具包，例如FastJson、apache下的一些工具包等；
 + 6）合约必须有一个接口和该接口的实现类，详细要求如下：
    -    a. 接口必须有@Contract注解；
    -    b. 接口的可调用方法上必须有@ContractEvent注解，且每个注解中的name属性不能重复；
    -    c. 合约方法支持入参和返回值，其主要包括所有基本类型；



### 4、合约案例

#### 4.1、代码实例
以下是一个可创建银行账户，指定具体金额，并可以转账的合约代码（逻辑较简单，仅供参考）：

合约接口代码如下：
```java
package com.jd.chain.contract;


@Contract
public interface TransferContract {

    @ContractEvent(name = "create")
    String create(String address, String account, long money);

    @ContractEvent(name = "transfer")
    String transfer(String address, String from, String to, long money);

    @ContractEvent(name = "read")
    long read(String address, String account);

    @ContractEvent(name = "readAll")
    String readAll(String address, String account);
}

```

合约实现类代码如下：
```java
package com.jd.chain.contract;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.KVDataVO;
import com.jd.blockchain.ledger.KVInfoVO;

public class TransferContractImpl implements EventProcessingAware, TransferContract {

    private ContractEventContext eventContext;

    private HashDigest ledgerHash;

    @Override
    public String create(String address, String account, long money) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, account);
        // 肯定有返回值，但若不存在则返回version=-1
        if (kvDataEntries != null && kvDataEntries.length > 0) {
            long currVersion = kvDataEntries[0].getVersion();
            if (currVersion > -1) {
                throw new IllegalStateException(String.format("%s -> %s already have created !!!", address, account));
            }
            eventContext.getLedger().dataAccount(address).setInt64(account, money, -1L);
        } else {
            throw new IllegalStateException(String.format("Ledger[%s] inner Error !!!", ledgerHash.toBase58()));
        }
        return String.format("DataAccountAddress[%s] -> Create(By Contract Operation) Account = %s and Money = %s Success!!! \r\n",
                address, account, money);
    }

    @Override
    public String transfer(String address, String from, String to, long money) {
        // 首先查询余额
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, from, to);
        if (kvDataEntries == null || kvDataEntries.length != 2) {
            throw new IllegalStateException(String.format("%s -> %s - %s may be not created !!!", address, from, to));
        } else {
            // 判断from账号中钱数量是否足够
            long fromMoney = 0L, toMoney = 0L, fromVersion = 0L, toVersion = 0L;
            for (KVDataEntry kvDataEntry : kvDataEntries) {
                if (kvDataEntry.getKey().equals(from)) {
                    fromMoney = (long) kvDataEntry.getValue();
                    fromVersion = kvDataEntry.getVersion();
                } else {
                    toMoney = (long) kvDataEntry.getValue();
                    toVersion = kvDataEntry.getVersion();
                }
            }
            if (fromMoney < money) {
                throw new IllegalStateException(String.format("%s -> %s not have enough money !!!", address, from));
            }
            long fromNewMoney = fromMoney - money;
            long toNewMoney = toMoney + money;
            // 重新设置
            eventContext.getLedger().dataAccount(address).setInt64(from, fromNewMoney, fromVersion);
            eventContext.getLedger().dataAccount(address).setInt64(to, toNewMoney, toVersion);
        }

        return String.format("DataAccountAddress[%s] transfer from [%s] to [%s] and [money = %s] Success !!!", address, from, to, money);
    }

    @Override
    public long read(String address, String account) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, account);
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return -1;
        }
        return (long)kvDataEntries[0].getValue();
    }

    @Override
    public String readAll(String address, String account) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, account);
        // 获取最新的版本号
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return "";
        }
        long newestVersion = kvDataEntries[0].getVersion();
        if (newestVersion == -1) {
            return "";
        }
        KVDataVO[] kvDataVOS = new KVDataVO[1];
        long[] versions = new long[(int)newestVersion + 1];
        for (int i = 0; i < versions.length; i++) {
            versions[i] = i;
        }
        KVDataVO kvDataVO = new KVDataVO(account, versions);

        kvDataVOS[0] = kvDataVO;

        KVInfoVO kvInfoVO = new KVInfoVO(kvDataVOS);

        KVDataEntry[] allEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, kvInfoVO);

        return JSON.toJSONString(allEntries);
    }

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
        this.ledgerHash = eventContext.getCurrentLedgerHash();
    }

    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {

    }
}
```


#### 4.2、pom.xml文件实例

```xml

<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <groupId>com.jd.chain</groupId>
    <version>1.0.0.RELEASE</version>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>contract-samples</artifactId>

    <name>contract-samples</name>

    <dependencies>
        <dependency>
            <groupId>com.jd.blockchain</groupId>
            <artifactId>sdk-pack</artifactId>
            <version>1.0.0.RELEASE</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.32</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>com.jd.blockchain</groupId>
                <artifactId>contract-maven-plugin</artifactId>
                <version>1.0.0.RELEASE</version>
                <executions>
                    <execution>
                        <id>make-contract</id>
                        <phase>package</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.jd.chain.contract.TransferContractImpl</mainClass>
                        </manifest>
                    </archive>
                    <finalName>contract</finalName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```