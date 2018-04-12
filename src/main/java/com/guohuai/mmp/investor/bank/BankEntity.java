package com.guohuai.mmp.investor.bank;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.basic.component.ext.hibernate.UUID;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * user's debit cards
 * @author Jeffrey.Wong
 *
 */
@lombok.Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Entity
@Table(name="T_MONEY_INVESTOR_BANK")
public class BankEntity extends UUID implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 绑卡状态：已绑卡 */
	public static final String Bank_BindStatus_ok = "ok";
	/** 绑卡状态：未绑卡 */
	public static final String Bank_BindStatus_no = "no";
	
	/** 关联投资者 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="investorOid", referencedColumnName="oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/** 姓名 */
	private String name;
	
	/** 身份证号 */
	private String idCard;
	
	/** 银行名称 */
	private String bankName;
	
	/** 银行卡号	 */
	private String debitCard;
	
	/** 银行预留手机号 */
	private String phoneNo;
	
	/** 绑卡状态 */
	private String bindStatus;
	
	/** createTime */
	private Timestamp createTime;
	
	/** updateTime */
	private Timestamp updateTime;
}
