package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class GetMasterInfoRep extends BaseResp {
	
	/**  主账户的id     */
	private String pid;
	
	/**    主账户的余额        */
	private BigDecimal applyAvailableBalance;
	
	
}
