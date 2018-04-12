package com.guohuai.ams.portfolio.form;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 投资组合要素
 * 
 * @author star.zhu 2016年12月26日
 */
@Data
public class PortfolioForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oid;
	// 关联发行人OID
	private String spvOid;
	// 关联发行人名称
	private String spvName;
	// 投资组合名称
	private String name;

	// 计划现金类资产占比
	private BigDecimal liquidRate;
	// 计划非现金类资产占比
	private BigDecimal illiquidRate;
	// 计划现金存款占比
	private BigDecimal cashRate;

	// 投资范围
	private String[] scopes;

	// 托管费率
	private BigDecimal trusteeRate;
	// 管理费率
	private BigDecimal manageRate;
	// 费用计算基础
	private int calcBasis;

	// 资管机构名称
	private String organization;
	// 资管计划名称
	private String planName;
	// 托管银行
	private String bank;
	// 托管银行账号
	private String account;
	// 联系人
	private String contact;
	// 联系电话
	private String telephone;

}
