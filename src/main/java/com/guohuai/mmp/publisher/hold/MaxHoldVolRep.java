package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MaxHoldVolRep extends BaseResp {
	BigDecimal maxHoldVol;
}
