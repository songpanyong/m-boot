package com.guohuai.mmp.investor.sonaccount;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.investor.bank.BankEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "t_money_investor_sonaccount")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class SonAccountEntity extends UUID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String sid;
	
	/** 主账号 */
	private String pid;
	
	/** 昵称 */
	private String nickname;
	
	/** 关系 */
	private String relation;
	
	/** 主子帐号关系 */
	private Integer status;
	
	
}
