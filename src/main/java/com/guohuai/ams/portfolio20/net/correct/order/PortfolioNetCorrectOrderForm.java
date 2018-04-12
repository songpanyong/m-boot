package com.guohuai.ams.portfolio20.net.correct.order;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午4:16:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNetCorrectOrderForm {

	private String portfolioOid;
	// 基准日
	private Date netDate;
	// 份额
	private BigDecimal share;
	// 单位净值
	private BigDecimal nav;

}
