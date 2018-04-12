package com.guohuai.mmp.publisher.product.rewardincomepractice;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DetailRep extends BaseResp {
	BigDecimal totalHoldVolume = BigDecimal.ZERO;
	BigDecimal totalCouponVolume = BigDecimal.ZERO;
}
