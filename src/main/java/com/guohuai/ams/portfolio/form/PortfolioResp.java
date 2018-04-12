package com.guohuai.ams.portfolio.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.guohuai.ams.dict.Dict;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.Data;

@Data
public class PortfolioResp implements Serializable {

	private static final long serialVersionUID = -9221782875969382366L;

	public PortfolioResp() {
		super();
	}

	public PortfolioResp(PortfolioEntity p) {
		super();
		this.oid = p.getOid();
		this.name = p.getName();
		this.spvOid = p.getSpvEntity().getOid();
		this.spvName = p.getSpvEntity().getRealName();
		this.liquidRate = p.getLiquidRate();
		this.illiquidRate = p.getIlliquidRate();
		this.cashRate = p.getCashRate();
		this.liquidFactRate = p.getLiquidFactRate();
		this.illiquidFactRate = p.getIlliquidFactRate();
		this.cashFactRate = p.getCashFactRate();
		this.manageRate = p.getManageRate();
		this.trusteeRate = p.getTrusteeRate();
		this.calcBasis = p.getCalcBasis();
		this.organization = p.getOrganization();
		this.planName = p.getPlanName();
		this.bank = p.getBank();
		this.account = p.getAccount();
		this.contact = p.getContact();
		this.telephone = p.getTelephone();

		this.nav = p.getNav();
		this.shares = p.getShares();
		this.netValue = p.getNetValue();
		this.baseDate = p.getBaseDate();

		this.dimensions = p.getDimensions();
		this.cashPosition = p.getCashPosition();
		this.liquidDimensions = p.getLiquidDimensions();
		this.illiquidDimensions = p.getIlliquidDimensions();
		this.deviationValue = p.getDeviationValue();
		this.freezeCash = p.getFreezeCash();
		this.dimensionsDate = p.getDimensionsDate();

		this.drawedChargefee = p.getDrawedChargefee();
		this.countintChargefee = p.getCountintChargefee();

		this.state = p.getState();

		this.auditor = p.getAuditor();
		this.auditMark = p.getAuditMark();
		this.auditTime = p.getAuditTime();

		this.creater = p.getCreater();
		this.createTime = p.getCreateTime();
		this.operator = p.getOperator();
		this.updateTime = p.getUpdateTime();

	}

	private String oid;
	// 投资组合名称
	private String name;
	// 投资范围
	private List<Dict> scopes;

	// 关联发行人OID
	private String spvOid;
	// 关联发行人名称
	private String spvName;

	// 计划现金类资产占比
	private BigDecimal liquidRate;
	// 计划非现金类资产占比
	private BigDecimal illiquidRate;
	// 计划现金存款占比
	private BigDecimal cashRate;
	// 实际现金类资产占比
	private BigDecimal liquidFactRate;
	// 实际非现金类资产占比
	private BigDecimal illiquidFactRate;
	// 实际现金存款占比
	private BigDecimal cashFactRate;
	// 管理费率
	private BigDecimal manageRate;
	// 托管费率
	private BigDecimal trusteeRate;
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
	// SPV持有的基子单位净值
	private BigDecimal nav;
	// SPV持有的基子份额
	private BigDecimal shares;
	// SPV持有的总资产净值
	private BigDecimal netValue;
	// SPV净值校准日期
	private Date baseDate;

	// [估值]投资组合总规模
	private BigDecimal dimensions;
	// [估值]账户现金
	private BigDecimal cashPosition;
	// [估值]现金类资产总规模
	private BigDecimal liquidDimensions;
	// [估值]非现金类资产总规模
	private BigDecimal illiquidDimensions;
	// [估值]偏离损益
	private BigDecimal deviationValue;
	// [估值]冻结现金
	private BigDecimal freezeCash;
	// [估值]最新估值日
	private Date dimensionsDate;

	// SPV累计提取费金
	private BigDecimal drawedChargefee;
	// SPV累计计提费金
	private BigDecimal countintChargefee;

	// 状态
	private String state;

	// 审核人
	private String auditor;
	// 审核时间
	private Timestamp auditTime;
	// 审核意见
	private String auditMark;

	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;
	
}
