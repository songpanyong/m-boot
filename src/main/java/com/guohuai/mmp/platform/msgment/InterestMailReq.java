package com.guohuai.mmp.platform.msgment;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品成立进入存续期	计息提醒	interest	["产品名称"]	计息提醒	您投资的{1}理财产品开始计息！详情请查看个人消息中心。

 * @author yuechao
 *
 */


@Data
@EqualsAndHashCode(callSuper = true)
public class InterestMailReq extends MailReq {
	
	/**
	 * 产品名称
	 */
	private String productName;
}
