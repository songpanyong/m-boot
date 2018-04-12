package com.guohuai.ams.portfolio20.order;

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

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentEntity;

import lombok.Data;

/**
 * 资产交易订单表
 * 
 * @author star.zhang 2017年2月01日
 */
@Entity
@Data
@Table(name = "T_GAM_ASSET_ORDER")
@DynamicInsert
@DynamicUpdate
public class MarketOrderEntity implements Serializable {

	private static final long serialVersionUID = -6907108767514414990L;

	/**
	 * 标的类型
	 */
	public static final String TYPE_LIQUID = "LIQUID";
	public static final String TYPE_ILLIQUID = "ILLIQUID";

	/**
	 * 操作类型
	 */
	// 申购
	public static final String DEALTYPE_PURCHASE = "PURCHASE";
	// 认购
	public static final String DEALTYPE_SUBSCRIPE = "SUBSCRIPE";
	// 退款
	public static final String DEALTYPE_REFUND = "REFUND";
	// 赎回
	public static final String DEALTYPE_REDEEM = "REDEEM";
	// 还款
	public static final String DEALTYPE_REPAYMENT = "REPAYMENT";
	// 转出
	public static final String DEALTYPE_SELLOUT = "SELLOUT";
	// 转让
	public static final String DEALTYPE_TRANSFER = "TRANSFER";
	// 逾期转让
	public static final String DEALTYPE_OVERDUETRANS = "OVERDUETRANS";
	// 坏账核销
	public static final String DEALTYPE_CANCELLATE = "CANCELLATE";
	// 逾期坏账核销
	public static final String DEALTYPE_OVERDUECANCELLATE = "OVERDUECANCELLATE";

	/**
	 * 订单状态
	 */
	// 待审核 SUBMIT
	// 审核通过 PASS
	// 审核失败 FAIL
	// 已删除 DELETE
	public static final String ORDER_STATE_SUBMIT = "SUBMIT";
	public static final String ORDER_STATE_PASS = "PASS";
	public static final String ORDER_STATE_FAIL = "FAIL";
	public static final String ORDER_STATE_DELETE = "DELETE";

	// 估值方式 - 账面价值
	public static final String EXCEPT_WAY_BOOK_VALUE = "BOOK_VALUE";
	// 估值方式 - 摊余成本
	public static final String EXCEPT_WAY_AMORTISED_COST = "AMORTISED_COST";

	@Id
	private String oid;

	// 投资标的类型
	private String type;

	//投资组合
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	//关联现金类标的
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "liquidAssetOid", referencedColumnName = "oid")
	private LiquidAsset liquidAsset;

	//关联非现金类标的
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "illiquidAssetOid", referencedColumnName = "oid")
	private IlliquidAsset illiquidAsset;

	//关联非现金类标的还款计划
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "illiquidAssetRepaymentOid", referencedColumnName = "oid")
	private PortfolioIlliquidHoldRepaymentEntity illiquidAssetRepayment;

	// 订单状态(待审核: SUBMIT;通过: PASS;驳回: FAIL;已删除: DELETE)
	private String orderState;
	// 交易类型
	private String dealType;
	// 交易金额
	private BigDecimal orderAmount;
	// 交易份额
	private BigDecimal tradeShare;
	// 交易本金
	private BigDecimal capital;
	// 交易收益
	private BigDecimal income;
	//交易日期
	private Date orderDate;
	//审核人 
	private String auditor;
	// 审批时间
	private Timestamp auditTime;
	//审核意见 
	private String auditMark;
	// 估值方式
	private String exceptWay;
	// 是否强制平仓
	private String forceClose;

}
