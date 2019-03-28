package com.jd.blockchain.consensus;

import java.util.Properties;

public interface ConsensusSettingsBuilder {

	/**
	 * 从属性表中解析生成共识网络的参数配置；
	 * 
	 * @param props
	 *            属性表；
	 * @param keyPrefix
	 *            属性的key 的前缀；<br>
	 *            在解析过程中，以具体协议实现的标准参数的key 加入此前缀后从属性表中检索参数值；<br>
	 *            如果指定为 null 或者空白，则忽略此参数；
	 * @return
	 */
	ConsensusSettings createSettings(Properties props);
	
	Properties createPropertiesTemplate();

	void writeSettings(ConsensusSettings settings, Properties props);
}
