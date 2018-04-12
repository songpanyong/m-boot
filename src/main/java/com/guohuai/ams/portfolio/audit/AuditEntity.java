package com.guohuai.ams.portfolio.audit;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.Data;

/**
 * 审核记录
 * @author star.zhu
 * 2016年12月26日
 */
@Data
@Entity
@Table(name = "T_GAM_AUDIT_LOG")
public class AuditEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 审核记录状态
	 */
	public static final String AuditRecord_STATE_pass = "PASS";//审核通过
	public static final String AuditRecord_STATE_reject = "REJECT";//驳回
	
	@Id
	private String oid;

	// 关联投资组合
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;
	
	// 审核类型
	private String auditType;
	// 标的名称
	private String targetName;
	// 审核人
	private String auditor;
	// 审核状态
	private String auditState;
	// 审核时间
	private Timestamp auditTime;
	// 审核意见
	private String auditMark;
	
	private String creater;
	private Timestamp createTime;
}
