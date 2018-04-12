package com.guohuai.mmp.publisher.holdapart.snapshot;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.product.Product;
import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 发行人-投资人-分仓快照
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class SnapshotEntity extends UUID {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -934419153790296513L;

	/**
	 * 所属订单
	 */
	private String orderOid;
	
	/**
	 * 所属投资人
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investorOid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/**
	 * 所属合仓
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "holdOid", referencedColumnName = "oid")
	private PublisherHoldEntity hold;
	
	/**
	 * 所属产品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productOid", referencedColumnName = "oid")
	private Product product;
	
	/**
	 * 持仓天数
	 */
	private Integer holdDays;
	
	
	/**
	 * 快照总份额
	 */
	private BigDecimal totalSnapshotVolume	 = BigDecimal.ZERO;
	
	/**
	 * 计息快照份额
	 */
	private BigDecimal snapshotVolume = BigDecimal.ZERO;
	
	/**
	 * 订单金额
	 */
	private BigDecimal holdVolume = BigDecimal.ZERO;
	
	/**
	 * 开始赎回日
	 */
	private Date beginRedeemDate;
	
	/**
	 * (基础收益留存,4位小数)
	 */
	private BigDecimal remainderBaseIncome = BigDecimal.ZERO;
	
	/**
	 * (奖励收留存,4位小数)
	 */
	private BigDecimal remainderRewardIncome = BigDecimal.ZERO;
	
	
	/**
	 * (加息券收益留存,4位小数)
	 */
	private BigDecimal remainderCouponIncome = BigDecimal.ZERO;
	
	/**
	 * (中间值基础收益留存,4位小数)
	 */
	private BigDecimal mdBaseIncome = BigDecimal.ZERO;
	
	/**
	 * (中间值奖励收留存,4位小数)
	 */
	private BigDecimal mdRewardIncome = BigDecimal.ZERO;
	
	
	/**
	 * (中间值加息券收益留存,4位小数)
	 */
	private BigDecimal mdCouponIncome = BigDecimal.ZERO;
	
	
	/**
	 * (最近的基础收益留存,4位小数)
	 */
	private BigDecimal latestRemainderBaseIncome = BigDecimal.ZERO;
	
	/**
	 * (最近的奖励收留存,4位小数)
	 */
	private BigDecimal latestRemainderRewardIncome = BigDecimal.ZERO;
	
	
	/**
	 * (最近的加息券收益留存,4位小数)
	 */
	private BigDecimal latestRemainderCouponIncome = BigDecimal.ZERO;
	
	
	/**
	 * 快照日期
	 */
	private Date snapShotDate;
	
	/**
	 * 基础收益
	 */
	private BigDecimal baseIncome= BigDecimal.ZERO;
	
	/**
	 * 奖励收益
	 */
	private BigDecimal rewardIncome= BigDecimal.ZERO;
	
	/**
	 * 持仓收益
	 */
	private BigDecimal holdIncome= BigDecimal.ZERO;
	
	/**
	 * 奖励规则
	 */
	private String rewardRuleOid;
	
	/**
	 * 奖励万份收益率
	 */
	private BigDecimal rewardIncomeRatio= BigDecimal.ZERO;
	/**
	 * 加息收益率
	 */
	private BigDecimal additionalInterestRate= BigDecimal.ZERO;
	/**
	 * 加息有效天数
	 */
	private Integer affectiveDays = SysConstant.INTEGER_defaultValue;
	/**
	 * 加息收益
	 */
	private BigDecimal couponIncome= BigDecimal.ZERO;
	
	Timestamp updateTime;
	Timestamp createTime;
	
}
