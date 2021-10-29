## 账户级别权限

数据账户，事件账户以及合约账户数据权限设计。

**数据读取完全开放**，本文档讨论全新变更仅对数据写入和合约调用生效。

### 权限定义

类似`linux`文件权限，使用用10位数据表示账户数据权限信息:
`0 123 456 789`

- `0`: 数据集或者合约, `-`或`c`
- `123`: 所有者列表, `read(-/r)`, `write(-/w)` 以及 `execute(-/x)`
- `456`: 所属角色, `read(-/r)`, `write(-/w)` 以及 `execute(-/x)`
- `789`: 其他用户, `read(-/r)`, `write(-/w)` 以及 `execute(-/x)`

> 当前实现数据账户仅对`write`权限更新有效；事件账户仅对`write`权限更新有效；合约仅对`execute`权限更新有效。

### 实现

权限数据存储与数据集头信息中，`SecurityPolicy`中增加：
```java
// 查询/写入/执行 权限校验
void checkDataPermission(DataPermission permission, DataPermissionType permissionType) throws LedgerSecurityException;

// 账户创建者校验，只有创建者才能修改数据权限
void checkDataOwners(DataPermission permission, MultiIDsPolicy midPolicy) throws LedgerSecurityException;
```

在数据写入/合约方法调用前进行权限校验



数据账户，事件账户，合约账户均实现`PermissionAccount`接口：
```java
public interface PermissionAccount {

DataPermission getPermission();

void setPermission(DataPermission permission);

void setModeBits(AccountModeBits modeBits);

void setRole(String role);
}
```

增加`AccountPermissionSetOperation`账户数据权限设置操作及其处理逻辑

### SDK

统一使用风格

#### 数据账户

```java
txTemp.dataAccount("LdeNrUrMGxkG1R5mDNwrUvkFdRdD91xH1Pcvd")
.permission() // 创建权限修改操作构造器
.mode(777) // 设置权限值，与 linux chmod 操作类似
.role("ADMIN"); // 设置账户数据所属角色
```

#### 事件账户

```java
txTemp.eventAccount("LdeNrUrMGxkG1R5mDNwrUvkFdRdD91xH1Pcvd")
.permission() // 创建权限修改操作构造器
.mode(777) // 设置权限值，与 linux chmod 操作类似
.role("ADMIN"); // 设置账户数据所属角色
```

#### 合约账户

```java
txTemp.contract("LdeNrUrMGxkG1R5mDNwrUvkFdRdD91xH1Pcvd")
.permission() // 创建权限修改操作构造器
.mode(777) // 设置权限值，与 linux chmod 操作类似
.role("ADMIN"); // 设置账户数据所属角色
```

### JD Chain Cli


#### 数据账户

[更新数据账户权限](cli/tx.md#修改数据账户权限)

#### 事件账户

[更新数据账户权限](cli/tx.md#修改事件账户权限)

#### 合约账户

[更新合约权限](cli/tx.md#修改合约权限)
