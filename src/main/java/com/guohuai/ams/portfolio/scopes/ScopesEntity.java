package com.guohuai.ams.portfolio.scopes;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.dict.Dict;

import lombok.Data;

/**
 * 投资范围
 * 
 * @author star.zhu 2016年12月26日
 */
@Data
@Entity
@Table(name = "T_GAM_INVEST_SCOPE")
@DynamicInsert
@DynamicUpdate
public class ScopesEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	// 关联Oid
	private String relationOid;
	// 投资类型
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "assetTypeOid", referencedColumnName = "oid")
	private Dict assetType;
}
