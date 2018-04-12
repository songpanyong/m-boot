package com.guohuai.mmp.publisher.hold;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class HoldQueryRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 持仓OID
	 */
	private String holdOid;
	
	/**
	 * 产品OID
	 */
	private String productOid;
	
	/**
	 * 产品编号
	 */
	private String productCode;
	/**
	 * 产品名称
	 */
	private String productName;
	
	/**
	 * 预期年化收益率
	 */
	private String expAror;
	
	/**
	 * 持有人OID
	 */
	private String investorOid;
	/**
	 * 持有人手机号
	 */
	private String phoneNum;
	
	/**
	 * 总份额
	 */
	private BigDecimal totalVolume;
	
	/**
	 * 持有份额
	 */
	private BigDecimal holdVolume;
	
	/**
	 * 待确认投资份额
	 */
	private BigDecimal toConfirmInvestVolume;
	
	/**
	 * 赎回待确认
	 */
	private BigDecimal toConfirmRedeemVolume;
	
	/**
	 * 累计投资
	 */
	private BigDecimal totalInvestVolume;
	
	
	/**
	 * 可计息份额
	 */
	private BigDecimal accruableHoldVolume;
	
	/**
	 * 可赎回份额
	 */
	private BigDecimal redeemableHoldVolume;
	
	/**
	 * 赎回锁定份额
	 */
	private BigDecimal lockRedeemHoldVolume;
	
	/**
	 * 最新市值
	 */
	private BigDecimal value;
	
	/**
	 * 体验金
	 */
	private BigDecimal expGoldVolume;
	
	/**
	 * 累计收益
	 */
	private BigDecimal holdTotalIncome;
	
	/**
	 * 累计基础收益
	 */
	private BigDecimal totalBaseIncome;
	
	/**
	 * 累计奖励收益
	 */
	private BigDecimal totalRewardIncome;
	
	/**
	 * 累计加息券收益
	 */
	private BigDecimal totalCouponIncome;
	
	/**
	 * 昨日收益
	 */
	private BigDecimal holdYesterdayIncome;
	
	
	/**
	 * 昨日基础收益
	 */
	private BigDecimal yesterdayBaseIncome;
	/**
	 * 昨天奖励收益
	 */
	private BigDecimal yesterdayRewardIncome;
	/**
	 * 昨天加息收益
	 */
	private BigDecimal yesterdayCouponIncome;
	
	/**
	 * 最近派息日
	 */
	private Date confirmDate;
	
	/**
	 * 预期收益
	 */
	private String expectIncome;
	
	/**
	 * 账户类型
	 */
	private String accountType;
	private String accountTypeDisp;
	
	
	/**
	 * 
	 * 单日赎回份额
	 */
	private BigDecimal dayRedeemVolume;
	
	/**
	 * 
	 * 单日投资份额
	 */
	private BigDecimal dayInvestVolume;
	
	/**
	 * 单日赎回次数
	 */
	private int dayRedeemCount;
	
	/**
	 * 最大持有份额
	 */
	private BigDecimal maxHoldVolume;
	
	/**
	 * 持仓状态
	 */
	private String holdStatus;
	private String holdStatusDisp;
	
	/**
	 * 最近投资时间
	 */
	private Timestamp latestOrderTime;
	
	/**
	 * 产品分期号
	 */
	private String productAlias;
	
	
	
	
	
	

}
