package com.guohuai.mmp.platform.baseaccount.statistics;

import java.math.BigDecimal;

import com.guohuai.mmp.sys.SysConstant;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class PlatformChartQueryRep {

	/**
	 *  渠道名称
	 */
	private String channelName;
	/** 
	 * 昨日投资额 
	 */
	private BigDecimal ysTodayInvestAmount = SysConstant.BIGDECIMAL_defaultValue;

}
