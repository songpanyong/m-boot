package com.guohuai.ams.product;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ProductLogPojo {
	/**
	 * 审核状态
	 */
	private String auditStateDisp;
	
	/**
	 * 审核类型
	 */
	private String auditTypeDisp;
	
	/**
	 * 审核时间
	 */
	private Timestamp auditTime;
	
	/**
	 * 审核备注
	 */
	private String auditComment;
}
