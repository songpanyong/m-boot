package com.guohuai.ams.portfolio20.estimate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月21日 - 下午4:56:12
 */

@Data
@Table(name = "T_GAM_PORTFOLIO_ESTIMATE")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioEstimateEntity implements Serializable {

	private static final long serialVersionUID = 7140403147257015317L;
	@Id
	private String oid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	// 现金类标的估值
	private BigDecimal liquidEstimate;
	// 非现金类标的估值
	private BigDecimal illiquidEstimate;
	// 现金估值
	private BigDecimal cashEstimate;
	// 计提托管费用
	private BigDecimal manageChargefee;
	// 计提管理费用
	private BigDecimal trusteeChargefee;
	// 计提费用合计	
	private BigDecimal chargefee;
	// 估值日期	
	private Date estimateDate;
	// 估值时间	
	private Timestamp estimateTime;

}
