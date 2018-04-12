package com.guohuai.ams.portfolio20.liquid.hold.part;

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
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.component.util.BigDecimalUtil;

import lombok.Data;

@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_LIQUID_HOLD_PART")
@DynamicInsert
@DynamicUpdate
public class PortfolioLiquidHoldPartEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String HOLD_STATE_HOLDING = "HOLDING";
	public static final String HOLD_STATE_CLOSED = "CLOSED";
	
	public PortfolioLiquidHoldPartEntity() {
		this.holdAmount 		= BigDecimalUtil.init0;
		this.holdShare = BigDecimalUtil.init0;
		this.investAmount = BigDecimalUtil.init0;
		
	}

	@Id
	private String oid;
	// 关联合仓
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "holdOid", referencedColumnName="oid")
	private PortfolioLiquidHoldEntity hold;
	//投资组合
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;
	// 投资标的
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liquidAssetOid", referencedColumnName = "oid")
	private LiquidAsset liquidAsset;
	
	//关联申购订单
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "orderOid", referencedColumnName="oid")
	private MarketOrderEntity marketOrder;
	
	
	// 持有金额
	private BigDecimal holdAmount;
	// 持有份额
	private BigDecimal holdShare;
	// 投资本金
	private BigDecimal investAmount;
	// 投资份额
	private BigDecimal investShare;
	//冻结持有份额
	private BigDecimal freezeHoldAmount;
    //单位净值
	private BigDecimal unitNet;
	//现价率
	private BigDecimal priceRatio;
	//赎回冻结份额
	//private BigDecimal lockupAmount;
	//持仓状态
	private String holdState;
	// 建仓时间
	private Timestamp investDate;
	private Date valueDate;
}
