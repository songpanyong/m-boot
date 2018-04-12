package com.guohuai.ams.portfolio20.net.correct.order;

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

@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_NET_CORRECT_ORDER")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNetCorrectOrderEntity implements Serializable {

	private static final long serialVersionUID = 7230999547356120896L;

	// 待审核 SUBMIT
	public static final String ORDER_STATE_SUBMIT = "SUBMIT";
	// 审核通过 PASS
	public static final String ORDER_STATE_PASS = "PASS";
	// 审核失败 FAIL
	public static final String ORDER_STATE_FAIL = "FAIL";
	// 已删除 DELETE
	public static final String ORDER_STATE_DELETE = "DELETE";
	@Id
	private String oid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	// 基准日
	private Date netDate;

	// 份额
	private BigDecimal share;
	// 单位净值
	private BigDecimal nav;
	// 总资产净值
	private BigDecimal net;

	// 	净充值
	private BigDecimal chargeAmount;
	// 净提现
	private BigDecimal withdrawAmount;
	// 净交易
	private BigDecimal tradeAmount;
	// 净值增长率
	private BigDecimal netYield;

	// 申请人
	private String creator;
	// 申请时间
	private Timestamp createTime;

	// 审核人
	private String auditor;
	// 审核时间
	private Timestamp auditTime;
	// 审核状态
	private String orderState;
	// 审核意见
	private String auditMark;

}
