package com.guohuai.mmp.jiajiacai.wishplan.plan.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_plan_invest")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class BackPlanInvestEntity implements Serializable {
	
	private static final long serialVersionUID = 7644481711548905139L;
		
	@Id
	private String oid;
	
	/**investor OID */
	
	//private String uid;
	//关联心愿计划类型的表
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "uid", referencedColumnName = "oid")
		private InvestorBaseAccountEntity investBaseAccount;
	
	//Only for month plan
	private String monthOid; 
	
	private String investorBankOid;
	
	private BigDecimal depositAmount;
	//The income balance.
	private BigDecimal balance;
	
	private Timestamp updateTime;
	private Timestamp createTime;
	private Timestamp endTime;
	
	private int investDuration;
	
    private BigDecimal expectedRate;
    
    private BigDecimal actualRate;
    
    private BigDecimal expectedAmount;
	 
	private String status;
	

	private String planType;
	
	private String cid;
	
	private String ckey;
	//代扣次数
	private int withholdCount;
	//心愿目标
	private String planTarget;
	
//	private BigDecimal income;
	
}
