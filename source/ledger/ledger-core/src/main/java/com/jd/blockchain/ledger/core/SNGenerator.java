package com.jd.blockchain.ledger.core;

import com.jd.blockchain.utils.Bytes;

/**
 * 序列号生成器；
 * 
 * @author huanghaiquan
 *
 */
public interface SNGenerator {

	long generate(Bytes key);

}
