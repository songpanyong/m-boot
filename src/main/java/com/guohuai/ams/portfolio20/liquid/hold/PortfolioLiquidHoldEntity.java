package com.guohuai.ams.portfolio20.liquid.hold;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.component.util.BigDecimalUtil;

import lombok.Data;

@Entity
@Table(name = "T_GAM_PORTFOLIO_LIQUID_HOLD")
@DynamicInsert
@DynamicUpdate
@Data
public class PortfolioLiquidHoldEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String HOLD_STATE_HOLDING = "HOLDING";
	public static final String HOLD_STATE_CLOSED = "CLOSED";
	public static final String HOLD_STATE_CLOSE_CONFIRM = "CLOSE_CONFIRM";

	public PortfolioLiquidHoldEntity() {
		this.holdAmount = BigDecimalUtil.init0;
		this.holdShare = BigDecimalUtil.init0;
		this.investAmount = BigDecimalUtil.init0;
		this.investCome = BigDecimalUtil.init0;
		this.totalPfofit = BigDecimalUtil.init0;
		this.lockupAmount = BigDecimalUtil.init0;

	}

	@Id
	private String oid;
	// 关联现金管理工具
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	//投资组合
	private PortfolioEntity portfolio;
	// 投资标的
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "liquidAssetOid", referencedColumnName = "oid")
	private LiquidAsset liquidAsset;
	//建仓日期
	private Date investDate;
	// 起息日
	private Date valueDate;
	// 持有金额
	private BigDecimal holdAmount;
	// 持有份额
	private BigDecimal holdShare;
	// 投资本金
	private BigDecimal investAmount;
	//投资收益
	private BigDecimal investCome;
	//[统计]累计收益
	private BigDecimal totalPfofit;
	//[统计]最新估值日	lastValueDate
	private Date newValueDate;
	//[统计]最新估值日收益
	private BigDecimal newPfofit;
	//赎回冻结份额
	private BigDecimal lockupAmount;
	//持仓状态
	private String holdState;
	private String creator;
	private String operator;
	private Timestamp createTime;
	private Timestamp updateTime;
}
