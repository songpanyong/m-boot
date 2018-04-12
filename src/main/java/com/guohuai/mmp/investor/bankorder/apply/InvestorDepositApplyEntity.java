package com.guohuai.mmp.investor.bankorder.apply;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 投资人-银行委托单
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_INVESTOR_DEPOSIT_APPLY")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class InvestorDepositApplyEntity extends UUID {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 655200494172011373L;

	/**
	 * 人
	 */
	
	private String investorOid;

	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	private String sendObj;
	
	private int errorCode;
	private String errorMessage;
	
	/**
	 * 支付流水号
	 */
	private String payNo;


	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	

	private Timestamp updateTime;
}
