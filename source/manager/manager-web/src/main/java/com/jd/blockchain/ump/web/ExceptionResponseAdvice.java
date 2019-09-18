package com.jd.blockchain.ump.web;

import com.jd.blockchain.ump.model.web.ErrorCode;
import com.jd.blockchain.ump.model.web.WebResponse;
import com.jd.blockchain.utils.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionResponseAdvice {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public WebResponse json(HttpServletRequest req, Exception ex) {

        WebResponse.ErrorMessage message;

        String reqURL = "[" + req.getMethod() + "] " + req.getRequestURL().toString();

        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            message = new WebResponse.ErrorMessage(businessException.getErrorCode(), businessException.getMessage());
        } else {
            logger.error("Exception occurred! --[RequestURL=" + reqURL + "][" + ex.getClass().toString()
                    + "]" + ex.getMessage(), ex);

            message = new WebResponse.ErrorMessage(ErrorCode.UNEXPECTED.getValue(), ex.toString());
        }
        return WebResponse.createFailureResult(message);
    }
}
