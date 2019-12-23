### 合约相关说明
1. 编译合约入口：ContractCompilerCmdTest.java;
2. 账本调用合约测试入口：ContractEventSendOperationHandleTest.java;

### 单元测试注意事项
使用ContractEventSendOperationHandleTest.java进行单元测试，注意事项：
1. 设置合约使用PUB_CLASS_PATH、CORE_CLASS_PATH位置（sys-contract.properties）;
PUB包在根目录中的contract-libs文件夹;core包需要编译来生成。具体如下：
    - 进入contract-jar模块，执行maven命令：
    ```
    mvn clean assembly:assembly
    ```
    - 生成的core包位于模块的target中的contract-jar-xxx所在的coreLib中;
    - 将此coreLib目录作为CORE_CLASS_PATH指向的目录。
2. 编译生成合约压缩包，即执行：ContractCompilerCmdTest.java对应的mainTestOk();
在编译之前，修改sys-contract.properties文件的变量CONTRACT_FROM_PATH(合约源文件位置)、CONTRACT_SAVE_TO_PATH(合约保存位置)。
3. 在合约保存位置中可看到生成的压缩包：xxx.contract;然后执行 ContractEventSendOperationHandleTest.java测试用例test1()即可。

### 20180910版本改造  
1. 在contract-jar中添加了mvn assembly处理逻辑，将合约用到的lib包全部放置其target/xxx/pubLib文件夹中，执行：
```
mvn clean assembly:assembly
```
2. 修改了sys-contract.properties文件，新增了CONTRACT_CLASS_LIBS参数;
    - 将CONTRACT_CLASS_PATH专用于存储路径
    - CONTRACT_CLASS_LIBS来存放所有的jar包
3. 删减了contract-libs文件夹中需要动态生成的jar。
