package com.guohuai.ams.portfolio.form;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuitRecordResp implements Serializable {
	
	private static final long serialVersionUID = 821045694218246898L;

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
