package com.jd.blockchain.gateway.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jd.blockchain.utils.BusinessException;
import com.jd.blockchain.utils.web.model.ErrorCode;
import com.jd.blockchain.utils.web.model.WebResponse;
import com.jd.blockchain.utils.web.model.WebResponse.ErrorMessage;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class GatewayGlobalExceptionHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public WebResponse json(HttpServletRequest req, Exception ex) {
		ErrorMessage message = null;
		String reqURL = "[" + req.getMethod() + "] " + req.getRequestURL().toString();
		if (ex instanceof BusinessException) {
			logger.error("BusinessException occurred! --[RequestURL=" + reqURL + "][" + ex.getClass().toString() + "] "
					+ ex.getMessage(), ex);
			BusinessException businessException = (BusinessException) ex;
			message = new ErrorMessage(businessException.getErrorCode(), businessException.getMessage());
		} else {
			logger.error("Unexpected exception occurred! --[RequestURL=" + reqURL + "][" + ex.getClass().toString()
					+ "]" + ex.getMessage(), ex);
			message = new ErrorMessage(ErrorCode.UNEXPECTED.getValue(),
					ErrorCode.UNEXPECTED.getDescription(ex.getMessage()));
		}
		WebResponse response = WebResponse.createFailureResult(message);
		return response;
	}

}