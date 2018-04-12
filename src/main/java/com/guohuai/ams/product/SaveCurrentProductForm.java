package com.guohuai.ams.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.guohuai.basic.component.ext.web.parameter.validation.Enumerations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCurrentProductForm implements Serializable {

	private static final long serialVersionUID = -7236645696766816814L;

	private String oid;
	
	@NotBlank
	private String code; //产品编号
	@NotBlank
	private String name;// 产品简称
	@NotBlank
	private String fullName;// 产品全称
	
	@NotBlank
	private String typeOid;// 产品类型
	@NotBlank
	private String administrator;// 产品管理人
	
	@Length(min = 32, max = 32, message = "所属资产池长度为32个字符")
	private String assetPoolOid;// 所属资产池
	
	@NotNull(message = "预期年化收益率起始区间不能为空")
	private BigDecimal expAror;// 预期年化收益率
	@NotNull(message = "预期年化收益率结束区间不能为空")
	private BigDecimal expArorSec;// 预期年化收益率区间
	private BigDecimal rewardInterest; //平台奖励收益
	private String incomeCalcBasis;// 收益计算基础
	
	
	private BigDecimal operationRate;// 平台运营费率
	@Enumerations(values = { "CNY", "USD", "EUR", "JPY", "GBP", "HKD", "SGD", "JMD", "AUD", "CHF" }, message = "产品币种类型参数错误")
	private String currency;// 币种
	
	
	@Enumerations(values = { "MANUALINPUT", "FIRSTRACKTIME" }, message = "产品成立时间类型参数错误")
	private String setupDateType;// 产品成立时间类型
	private Date setupDate;// 成立时间
	
	
	@NotBlank(message = "收益结转周期不可为空")
	private String accrualCycleOid;// 收益结转周期
	@Range(min = 0)
	private int lockPeriod; // 锁定期
	
//	@Range(min = 1)
	private int purchaseConfirmDate;// 申购确认日
//	@Range(min = 1)
	private int interestsDate;// 起息日
//	@Range(min = 1)
	private int redeemConfirmDate;// 赎回确认日
	
	
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	private BigDecimal investMin;// 单笔投资最低份额
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	private BigDecimal investAdditional;// 单笔投资追加份额
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	private BigDecimal investMax;// 单笔投资最高份额
	
	
	private BigDecimal maxHold; //单人持有份额上限
	private BigDecimal netMaxRredeemDay;// 单日净赎回上限
	private BigDecimal singleDailyMaxRedeem;//单人单日赎回上限
	
	
	private BigDecimal minRredeem;// 单笔净赎回下限
	private BigDecimal additionalRredeem;  //单笔赎回递增份额
	private BigDecimal maxRredeem; // 单笔净赎回上限
	
	
	private BigDecimal netUnitShare;// 单位份额净值
	private String basicProductLabel;//基础标签
	private String[] expandProductLabels;//扩展标签
	
	
	private String dealStartTime;//交易开始时间
	private String dealEndTime;//交易结束时间
	private String investDateType; //有效投资日类型
	private String rredeemDateType; //有效赎回日类型
	
	/**
	 * 赎回占比开关
	 */
	@Enumerations(values = { "YES", "NO" }, message = "赎回占比开关参数错误,只能是开或关")
	private String isPreviousCurVolume;
	
	/**
	 * 赎回占上一交易日规模百分比
	 */
	private BigDecimal previousCurVolumePercent = BigDecimal.ZERO;
	
	
	@NotBlank
	@Enumerations(values = { "YES", "NO" }, message = "额外增信参数错误,只能是有或无")
	private String reveal;// 额外增信
	private String revealComment;// 增信备注
	private String instruction;// 产品说明
	private String investComment;// 投资标的
	private String riskLevel;// 风险等级
	private String investorLevel;// 投资者类型
	
	private String files;// 附加文件
	@NotBlank
	private String investFile;// 投资协议书
	@NotBlank
	private String serviceFile;// 信息服务协议
	
	private Integer singleDayRedeemCount; // 单人单日赎回次数
	
	
	/**
	 * 赎回立刻不计息	
	 */
	@Enumerations(values = {Product.PRODUCT_redeemWithoutInterest_on, Product.PRODUCT_redeemWithoutInterest_off})
	private String redeemWithoutInterest;
	
	@NotBlank
	@Enumerations(values = { "cash", "reinvest" }, message = "额收益处理方式参数错误,只能是现金分红或结转")
	private String incomeDealType;	// 收益处理方式

}
