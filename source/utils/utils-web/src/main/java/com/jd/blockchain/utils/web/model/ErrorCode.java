package com.jd.blockchain.utils.web.model;

/**
 * 错误代码；
 */
public enum ErrorCode {

	/** 参数格式不正确 */
	ILLEGAL_PARAMETE(1001, "参数格式不正确！[parma=%s]"),

	/** 缺少必要参数 */
	MISSING_REQUIRED_PARAMETE(1002, "缺少必要参数！[param=%s]"),

	/** 没有操作权限 */
	PERFORM_FORBIDDEN(1003, "没有该操作权限！"),

	UNEXPECTED(5000, "未预期的异常！"),

	REQUEST_PARAM_FORMAT_ILLEGAL(12001, "请求体格式不正确"),

	NULL_VALUE(4001, "空值！"),

	NULL_VALUE_PARAM(4001, "参数%s不能为空值！");

	private int value;

	private String description;

	private ErrorCode(int value, String description) {
		this.value = value;
		this.description = description;
	}

	public int getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public String getDescription(String details) {
		return details == null ? description : new StringBuilder(description).append(" --").append(details).toString();
	}

}