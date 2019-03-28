package com.jd.blockchain.peer.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONException;
import com.jd.blockchain.utils.BusinessException;
import com.jd.blockchain.utils.web.model.ErrorCode;
import com.jd.blockchain.utils.web.model.WebResponse;
import com.jd.blockchain.utils.web.model.WebResponse.ErrorMessage;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class PeerGlobalExceptionHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public WebResponse json(HttpServletRequest req, Exception ex) {
		String reqURL = req.getRequestURL().insert(0, "[" + req.getMethod() + "] ").toString();
		ErrorMessage message = null;
		if (ex instanceof BusinessException) {
			logger.error("BusinessException occurred! --[RequestURL=" + reqURL + "][" + ex.getClass().toString() + "] "
					+ ex.getMessage(), ex);
			BusinessException businessException = (BusinessException) ex;
			message = new ErrorMessage(businessException.getErrorCode(), businessException.getMessage());
		} else if (ex instanceof JSONException) {
			logger.error("JSONException occurred! --[RequestURL=" + reqURL + "][" + ex.getClass().toString() + "] "
					+ ex.getMessage(), ex);
			message = new ErrorMessage(ErrorCode.REQUEST_PARAM_FORMAT_ILLEGAL.getValue(),
					ErrorCode.REQUEST_PARAM_FORMAT_ILLEGAL.getDescription());
		} else {
			logger.error("Unexpected exception occurred! --[RequestURL=" + reqURL + "][" + ex.getClass().toString()
					+ "]" + ex.getMessage(), ex);
			message = new ErrorMessage(ErrorCode.UNEXPECTED.getValue(), ErrorCode.UNEXPECTED.getDescription(ex.getMessage()));
		}
		WebResponse responseResult = WebResponse.createFailureResult(message);
		return responseResult;
	}

}