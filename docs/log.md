## 日志

`JD Chain`使用`Log4j2`配置日志输出

### Gateway

网关日志默认配置文件为`config/log4j2-gw.xml`，可修改启动脚本（`bin/startup.sh`）中`-Djdchain.log=$APP_HOME/logs`和`-Dlogging.config=file:$APP_HOME/config/log4j2-gw.xml`两个选项修改日志配置。

启动过程中会有少量控制台输出`bin/peer.out`，注意观察启动时是否有错误信息。

运行中日志根据默认配置会输出到`logs`目录中。

### Peer

节点日志默认配置文件为`config/log4j2-peer.xml`，可修改启动脚本（`bin/peer-startup.sh`）中`-Djdchain.log=$APP_HOME/logs`和`-Dlogging.config=file:$APP_HOME/config/log4j2-peer.xml`两个选项修改日志配置。

启动过程中会有少量控制台输出`bin/peer.out`，注意观察启动时是否有错误信息。

运行中日志根据默认配置会输出到`logs`目录中。