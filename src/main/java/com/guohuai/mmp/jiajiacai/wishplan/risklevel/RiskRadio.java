package com.guohuai.mmp.jiajiacai.wishplan.risklevel;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskRadio {

	private String type;
	private BigDecimal BankDeposit; // 银行存款类
	private BigDecimal cash; // 现金标的类
	private BigDecimal nonCash; // 非现金标的类
	private BigDecimal total; // 总额度
}
