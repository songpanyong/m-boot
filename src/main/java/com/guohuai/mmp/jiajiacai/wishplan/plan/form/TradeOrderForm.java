package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeOrderForm {
	@NotNull
	private String uid;
	@NotNull
	private String planRedeemOid;
	@NotNull
	private BigDecimal moneyVolume;
}
