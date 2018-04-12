package com.guohuai.mmp.platform.msgment;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 流标	abortive	["产品名称","客服电话"]		【掌悦理财】您投资的{1}理财产品发生流标，如有疑问，请联系客服{2}。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AbortiveMsgReq extends MsgReq {
	
	/**
	 * 产品名称
	 */
	private String productName;
	
	/**
	 * 客户热线
	 */
	private String hotLine;
}
