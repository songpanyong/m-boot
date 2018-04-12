package com.guohuai.mmp.platform.msgment;

import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * 投资成功	buysuccess	["产品名称"]		【掌悦理财】恭喜您成功投资{1}理财产品，请您耐心等待产品成立。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BuySuccessMsgReq extends MsgReq {
	
	/**
	 * 产品名称
	 */
	private String productName;
}	
