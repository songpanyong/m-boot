package com.guohuai.mmp.platform.msgment;

import lombok.Data;
import lombok.EqualsAndHashCode;



/**
投资成功提醒	buysuccess	["产品名称"]	投资成功提醒	恭喜您成功投资{1}理财产品，请您耐心等待产品成立。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BuySuccessMailReq extends MailReq {
	
	/**
	 * 产品名称
	 */
	private String productName;
}	
