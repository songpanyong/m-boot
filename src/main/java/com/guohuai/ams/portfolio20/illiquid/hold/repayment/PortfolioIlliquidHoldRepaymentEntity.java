package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

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

import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "T_GAM_PORTFOLIO_ILLIQUID_HOLD_REPAYMENT")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioIlliquidHoldRepaymentEntity implements Serializable {

	private static final long serialVersionUID = -598770878909480929L;

	public static final String LAST_ISSUE_YES = "YES";
	public static final String LAST_ISSUE_NO = "NO";

	// 还款状态 - 未到期
	public static final String STATE_UNDUE = "UNDUE";
	// 还款状态 - 已到期未还款
	public static final String STATE_PAYING = "PAYING";
	// 还款状态 - 还款待审核
	public static final String STATE_AUDIT = "AUDIT";
	// 还款状态 - 已还款
	public static final String STATE_PAID = "PAID";

	@Id
	private String oid;

	// 合仓
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "holdOid", referencedColumnName = "oid")
	private PortfolioIlliquidHoldEntity hold;

	// 期数
	private int issue;
	// 还款方式
	private String repaymentType;
	// 计息天数
	private int intervalDays;
	// 计息起始日
	private Date startDate;
	// 计息截止日
	private Date endDate;
	// 还款日
	private Date dueDate;
	// 预计还款本金
	private BigDecimal principalPlan;
	// 预计还款利息
	private BigDecimal interestPlan;
	// 预计还款总额
	private BigDecimal repaymentPlan;
	// 实际还款本金
	private BigDecimal principal;
	// 实际还款利息
	private BigDecimal interest;
	// 实际还款总额
	private BigDecimal repayment;
	// 是否最后一期
	private String lastIssue;
	// 还款状态
	private String state;

	private String creator;
	private Timestamp createTime;
	private String operator;
	private Timestamp operateTime;
}
