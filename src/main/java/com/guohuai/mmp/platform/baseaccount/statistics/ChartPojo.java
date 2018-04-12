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
public class ChartPojo {

	/**
	 *  x
	 */
	private String xName;
	/** 
	 * y
	 */
	private BigDecimal yValue = SysConstant.BIGDECIMAL_defaultValue;

}
