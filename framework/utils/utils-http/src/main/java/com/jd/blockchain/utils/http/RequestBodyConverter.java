package com.jd.blockchain.utils.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 标注了 {@link RequestBody} 的参数的转换器；
 * 
 * <br>
 * 定义了如何将某个参数从特定类型转换为用于发送 http 请求体的输入流；
 * 
 * <br>
 * 如果将多个参数都标注了  {@link RequestBody} ，则会按顺序对每一个参数调用其对应的转换器；
 * 
 * <br>
 * 注：任何时候，实现者都应避免在完成一个参数写入之后主动关闭输出流；
 * 
 * @author haiq
 *
 */
public interface RequestBodyConverter {
	
	void write(Object param, OutputStream out) throws IOException;
	
}
