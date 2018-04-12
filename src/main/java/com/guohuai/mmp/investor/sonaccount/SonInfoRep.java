package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SonInfoRep extends BaseResp {
	
	/**  昵称       */
	private String nickname;
	
	/**   姓名   */
	private String realName;
	
	/**  身份证号    */
	private String idCardNum;
	
	/**  银行名称     */
	private String bankName;
	
	/**   银行卡号    */
	private String bankCardNum;
	
	/**   余额     */
	private BigDecimal balance = BigDecimal.ZERO;
	/**   是否成年*/
	 private String isAdult;
	 
	 /**  关系    */
	 private String relation;

	/**   余额     */
	private BigDecimal applyAvailableBalance = BigDecimal.ZERO;
	 
}
