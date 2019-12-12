package com.jd.blockchain.tools.initializer.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class InitServiceExceptionHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public LedgerInitResponse json(HttpServletRequest req, Exception ex) {
//		logger.error("Error of web controllers! --" + ex.getMessage(), ex);
		System.out.println("[InitServiceExceptionHandler] Error of web controllers! --" + ex.getMessage());
		return LedgerInitResponse.error(ex.getMessage());
	}

}