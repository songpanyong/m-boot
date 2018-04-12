package com.guohuai.mmp.investor.baseaccount;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseAccountRep extends BaseResp {
	
	private String investorOid;
	
	/**
	 * 结算系统memberId
	 */
	private String memberId;

	/**
	 * 全手机号
	 */
	private String phone;
	
	/**
	 * 手机号
	 */
	private String phoneNum;

	/**
	 * 真实姓名
	 */
	private String realName;
	
	/**
	 * 状态
	 */
	private String status;
	private String statusDisp;

	/**
	 * 余额
	 */
	private BigDecimal balance;
	
	/**
	 * 提现冻结
	 */
	private BigDecimal withdrawFrozenBalance;
	
	/**
	 * 充值冻结
	 */
	private BigDecimal rechargeFrozenBalance;
	
	/**
	 * 申购冻结
	 */
	private BigDecimal applyAvailableBalance;
	
	/**
	 * 提现可用
	 */
	private BigDecimal withdrawAvailableBalance;

	/**
	 * 账户所有者
	 */
	private String owner;
	private String ownerDisp;
	
	/**
	 * 是否新手
	 */
	private String isFreshman;
	private String isFreshmanDisp;
	
	
	/**
	 * 累计充值总额
	 */
	private BigDecimal totalDepositAmount;

	/**
	 * 累计提现总额
	 */
	private BigDecimal totalWithdrawAmount;

	/**
	 * 累计投资总额
	 */
	private BigDecimal totalInvestAmount;

	/**
	 * 累计赎回总额
	 */
	private BigDecimal totalRedeemAmount;

	/**
	 * 累计收益总额
	 */
	private BigDecimal totalIncomeAmount;
	
	/**
	 * 累计还本总额
	 */
	private BigDecimal totalRepayLoan;

	/**
	 * 活期昨日收益额
	 */
	private BigDecimal t0YesterdayIncome;

	/**
	 * 定期总收益额
	 */
	private BigDecimal tnTotalIncome;

	/**
	 * 活期总收益额
	 */
	private BigDecimal t0TotalIncome;

	/**
	 * 活期资产总额
	 */
	private BigDecimal t0CapitalAmount;
	
	/**定期总资产*/
	private BigDecimal tnCapitalAmount;
	
	/**
	 * 累计充值次数
	 */
	private Integer totalDepositCount;

	/**
	 * 累计提现次数
	 */
	private Integer totalWithdrawCount;

	/**
	 * 累计投资次数
	 */
	private Integer totalInvestCount;

	/**
	 * 累计赎回次数
	 */
	private Integer totalRedeemCount;

	/**
	 * 当日充值次数
	 */
	private Integer todayDepositCount;
	/**
	 * 当日提现次数
	 */
	private Integer todayWithdrawCount;
	private Integer monthWithdrawCount;
	/**
	 * 当日投资次数
	 */
	private Integer todayInvestCount;
	/**
	 * 当日赎回次数
	 */
	private Integer todayRedeemCount;
	
	/**
	 * 当日充值总额
	 */
	private BigDecimal todayDepositAmount;
	/**
	 * 当日提现总额
	 */
	private BigDecimal todayWithdrawAmount;
	/**
	 * 当日投资总额
	 */
	private BigDecimal todayInvestAmount;
	
	/**
	 * 当日赎回总额
	 */
	private BigDecimal todayRedeemAmount;
	
	/**
	 * 收益确认日期
	 */
	private Date incomeConfirmDate;
	
	private Timestamp updateTime;
	
	private Timestamp createTime;
	
	/** 判断是否是主、子账户 */
	private Boolean isSonAccount;
	/** 心愿计划的总资产*/
	private BigDecimal wishPlanTotalCapitalAmount;
	/** 心愿计划的总收益  */
	private BigDecimal wishPlanTotalIncomeAmount;
	
	/** 判断主账户下的所有子账户的数量 */
	private Integer sonAccountCount;
	
	/**主账户转账到所有子账户下的累计金额*/
	private BigDecimal transferToSonAccounts;
	
	/** 子账户从主账户中转入的金额  */
	private BigDecimal transferFromPAccount;
	
	/** 子账户从主账户转账累计次数*/
	private Integer sonTransferCount;
	
	/**主账户转账到子账户的累计次数 */
	private Integer pTransferCount;
}
