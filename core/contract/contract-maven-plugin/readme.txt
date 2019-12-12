说明
1.编译：mvn clean install

快速自测：
1.ContractRemoteAutoMojoTest类用于快速自测发布和执行，快速自测是在测试链的环境中发布和执行合约；
2.修改sys-contract.properties文件中的相关信息；
3.合约发布之后，会在控制台生成合约地址，待5秒钟之后，会执行此合约。sys-contract.properties的contractArgs参数可修改，查看其不同效果；
###
contract's address=5SmEqUsnLY4APVfS32xYDpRPuz55Rsuupdt1
execute the contract,result=true
exeContract(),SUCCESS
###
4.在peer节点的控制台可以看到输出的结果信息。

通过maven插件中通过ContractAllAutoMojo做简单的编译、发布和执行测试，对应单元测试类ContractAllAutoMojoTest；




