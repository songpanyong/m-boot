package com.guohuai.mmp.platform.msgment;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
流标提醒	abortive	["产品名称","客服电话"]	流标提醒	您投资的{1}理财产品发生流标，如有疑问，请联系客服{2}。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AbortiveMailReq extends MailReq {
	
	/**
	 * 产品名称
	 */
	private String productName;
	
	/**
	 * 客户热线
	 */
	private String hotLine;
}
