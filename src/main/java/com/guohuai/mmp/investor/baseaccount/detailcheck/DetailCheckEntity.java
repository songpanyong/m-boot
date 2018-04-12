package com.guohuai.mmp.investor.baseaccount.detailcheck;

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
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_MONEY_INVESTOR_DETAIL_CHECK")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class DetailCheckEntity extends UUID {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 对账状态：对账成功 */
	public static final String Detail_Check_Status_OK = "ok";
	/** 对账状态：对账失败 */
	public static final String Detail_Check_Status_Failed = "failed";
	
	/** 所属投资人 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investorOid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/** 资金账户申购可用金额 */
	private BigDecimal balance = BigDecimal.ZERO;
	
	/** 资金变动明细重算金额 */
	private BigDecimal recorrectBalance = BigDecimal.ZERO;
	
	/** 对账状态 */
	private String checkStatus;
	
	/** 对账日期 */
	private String checkTime;

	private Timestamp updateTime;
	
	private Timestamp createTime;
}
