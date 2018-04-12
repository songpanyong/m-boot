package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MainAccountInfoRep extends BaseResp{

	
	/**  姓名 */
	private String name;
	/**  身份证号 */
	private String idNumb;
	/**  银行名称   */
	private String bankName;
	/**  卡号   */
	private String bankCardNum;
	/** 余额   */
	private BigDecimal balance;
	/** 手机号 */
	private String userAcc;
	/** 可用余额*/
	private BigDecimal applyAvailableBalance;
}
