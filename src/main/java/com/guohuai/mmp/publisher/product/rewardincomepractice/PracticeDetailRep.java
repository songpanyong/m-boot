package com.guohuai.mmp.publisher.product.rewardincomepractice;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.sys.SysConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PracticeDetailRep extends BaseResp {
	/**
	 * 持有人总份额
	 */
	BigDecimal totalHoldVolume = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 奖励收益
	 */
	BigDecimal totalRewardIncome = SysConstant.BIGDECIMAL_defaultValue;
	

}
