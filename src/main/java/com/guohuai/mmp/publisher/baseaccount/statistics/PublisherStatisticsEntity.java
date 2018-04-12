package com.guohuai.mmp.publisher.baseaccount.statistics;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 发行人-基本账户-统计
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_PUBLISHER_STATISTICS")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class PublisherStatisticsEntity extends UUID {
	/**
	* 
	*/
	private static final long serialVersionUID = -2464621001909376882L;

	/**
	 * 所属发行人
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisherOid", referencedColumnName = "oid")
	private PublisherBaseAccountEntity publisherBaseAccount;

	/**
	 * 累计充值总额
	 */
	private BigDecimal totalDepositAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计提现总额
	 */
	private BigDecimal totalWithdrawAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计借款总额
	 */
	private BigDecimal totalLoanAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计还款总额
	 */
	private BigDecimal totalReturnAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计付息总额
	 */
	private BigDecimal totalInterestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日投资总额
	 */
	private BigDecimal todayInvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日活期投资额
	 */
	private BigDecimal todayT0InvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日定期投资总额
	 */
	private BigDecimal todayTnInvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日赎回金额
	 */
	private BigDecimal todayRedeemAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日还本金额
	 */
	private BigDecimal todayRepayInvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日付息金额
	 */
	private BigDecimal todayRepayInterestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 逾期次数
	 */
	private Integer overdueTimes = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 总投资人数
	 */
	private Integer investorAmount = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 今日活期投资人数
	 */
	private Integer todayT0InvestorAmount = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 今日定期投资人数
	 */
	private Integer todayTnInvestorAmount = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 今日投资人数
	 */
	private Integer todayInvestorAmount = SysConstant.INTEGER_defaultValue;
	
	
	private Timestamp updateTime;
	
	private Timestamp createTime;
	
	
}
