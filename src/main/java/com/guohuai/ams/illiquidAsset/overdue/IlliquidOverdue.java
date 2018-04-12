package com.guohuai.ams.illiquidAsset.overdue;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.component.persist.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 非现金类资产逾期表
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_GAM_ILLIQUID_OVERDUE")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class IlliquidOverdue extends UUID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="illiquidAssetOid",referencedColumnName="oid")
	private IlliquidAsset illiquidAsset;
	
	private Date overdueStartDate;		// 逾期开始日
	private Date overdueEndDate;		// 逾期截止日
	private Integer overdueDays;		// 逾期天数
	
	private String creator;				// 创建者
	private String operator;			// 操作者
	private Timestamp createTime;		
	private Timestamp updateTime;		

}
