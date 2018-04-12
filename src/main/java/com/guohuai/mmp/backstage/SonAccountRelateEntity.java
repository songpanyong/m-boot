package com.guohuai.mmp.backstage;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_money_investor_sonaccount")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class SonAccountRelateEntity extends UUID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	//private String sid
	/**
	 *相关的子账户投资人
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity sonBaseAccount;
	
	/**
	 * 关联的主账户投资人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/** 昵称 */
	private String nickname;
	
	/** 关系 */
	private String relation;
	
	/** 主子帐号关系 */
	private Integer status;
	
}
