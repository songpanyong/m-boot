package com.guohuai.mmp.jiajiacai.wishplan.plan.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.mmp.sys.SysConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 产品实体
 * 
 * @author wangyan
 *
 */
@Entity
@Table(name = "T_GAM_PRODUCT")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class WishplanProduct implements Serializable {

	private static final long serialVersionUID = 7767000944338560987L;

	public static final String YES = "YES";
	public static final String NO = "NO";

	/**
	 * 活期产品
	 */
	public static final String TYPE_Producttype_02 = "PRODUCTTYPE_02";
	/**
	 * 定期产品
	 */
	public static final String TYPE_Producttype_01 = "PRODUCTTYPE_01";

	/**
	 * 状态status：
	 */
	public static final String STATE_Create = "CREATE";// 新建
	public static final String STATE_Update = "UPDATE";// 修改
	public static final String STATE_Auditing = "AUDITING";// 审核中
	public static final String STATE_Auditfail = "AUDITFAIL";// 审核不通过
	public static final String STATE_Auditpass = "AUDITPASS";// 审核通过(复核中)
	public static final String STATE_Reviewfail = "REVIEWFAIL";// 复核不通过
	public static final String STATE_Reviewpass = "REVIEWPASS";// 复核通过
	
	public static final String STATE_Notstartraise = "NOTSTARTRAISE";// 未开始募集(定期)
	public static final String STATE_Raising = "RAISING";// 募集中(定期)
	public static final String STATE_Raiseend = "RAISEEND";//募集结束(定期)
	public static final String STATE_RaiseFail = "RAISEFAIL";//募集失败(定期)
	
//	public static final String STATE_NotstartDuration = "NOTSTARTDURATION";//存续期未开始(活期)
	public static final String STATE_Durationing = "DURATIONING";// 存续期(定期活期)
	public static final String STATE_Durationend = "DURATIONEND";//存续期结束(定期活期)
	public static final String STATE_Clearing = "CLEARING";// 清算中(活期定期)
	public static final String STATE_Cleared = "CLEARED";// 已清算(活期定期)
	
	public static final String Product_dateType_D = "D";//自然日
	public static final String Product_dateType_T = "T";//交易日

	public static final String UNIT_Day = "DAY";// 按日
	public static final String UNIT_Week = "WEEK";
	public static final String UNIT_Month = "MONTH";
	public static final String UNIT_Year = "YEAR";

	public static final String DATE_TYPE_Natrue = "NATRUE";// 自然日
	public static final String DATE_TYPE_Trade = "TRADE";// 交易日

	public static final String AUDIT_STATE_Nocommit = "NOCOMMIT";// 未提交审核
	public static final String AUDIT_STATE_Auditing = "AUDITING";// 待审核(已经提交:审核中)
	public static final String AUDIT_STATE_Reviewing = "REVIEWING";// 待复核(已经提交复核:复核中)
	public static final String AUDIT_STATE_Reviewed = "REVIEWED";// 复核通过
	public static final String AUDIT_STATE_Reject = "REJECT";// 驳回
	public static final String AUDIT_STATE_Locking = "Locking";// 锁定中

	public static final String STEMS_Userdefine = "USERDEFINE";// 前端添加
	public static final String STEMS_Plateform = "PLATEFORM";// 后台添加

	public static final String DATE_TYPE_ManualInput = "MANUALINPUT";// 固定时间(手动录入时间)
	public static final String DATE_TYPE_FirstRackTime = "FIRSTRACKTIME";// ;与首次上架时间同时

	public static final String APPLY_STATUS_ApplyOn = "APPLY_ON";// 开启申购申请 开启赎回申请
	public static final String APPLY_STATUS_ApplyOff = "APPLY_OFF";// 关闭申购申请 关闭赎回申请
	public static final String APPLY_STATUS_None = "NONE";// 无: NONE
	
	
	public static final String PRODUCT_repayLoanStatus_toRepay = "toRepay";
	public static final String PRODUCT_repayLoanStatus_repaying = "repaying";
	public static final String PRODUCT_repayLoanStatus_repayed = "repayed";
	public static final String PRODUCT_repayLoanStatus_repayFailed = "repayFailed";
	
	public static final String PRODUCT_repayInterestStatus_toRepay = "toRepay";
	public static final String PRODUCT_repayInterestStatus_repaying = "repaying";
	public static final String PRODUCT_repayInterestStatus_repayed = "repayed";
	public static final String PRODUCT_repayInterestStatus_repayFailed = "repayFailed";
	
	/**
	 * 募集满额后是否自动触发成立 枚举：MANUAL:否,AUTO:是
	 */
	public static final String RAISE_FULL_FOUND_TYPE_MANUAL = "MANUAL";
	public static final String RAISE_FULL_FOUND_TYPE_AUTO = "AUTO";
	
	/** 逾期状态--已逾期   @author yuechao*/
	public static final String OFFSET_overdueStatus_yes = "yes";
	/** 逾期状态--未逾期 */
	public static final String OFFSET_overdueStatus_no = "no";
	
	/** 活期平仓规则--先进先出 */
	public static final String PRODUCT_closingRule_FIFO = "FIFO";
	/** 活期平仓规则--先进后出 */
	public static final String PRODUCT_closingRule_LIFO = "LIFO";
	
	/** 赎回是否立刻停止计息 */
	public static final String PRODUCT_redeemWithoutInterest_on = "on";
	
	public static final String PRODUCT_redeemWithoutInterest_off = "off";
	
	/**现金分红**/
	public static final String PRODUCT_incomeDealType_cash = "cash";
	
	/***收益结转*/
	public static final String PRODUCT_incomeDealType_reinvest = "reinvest";
	
	
	
	
	@Id
	private String oid;// 产品序号

	/**
	 * 资产配置
	 */
	private String assetPoolOid;


	private String spvOid;//发行人
	
	/**
	 * 产品编号
	 */
	private String code;

	/**
	 * 产品名称
	 */
	private String name;

	/**
	 * 产品全称
	 */
	private String fullName;
	/**
	 * 产品管理人
	 */
	private String administrator;

	/**
	 * 产品类型
	 */
	private String type;

	/**
	 * 额外增信
	 */
	private String reveal;
	/**
	 * 增信备注
	 */
	private String revealComment;
	/**
	 * 币种
	 */
	private String currency;

	/**
	 * 收益计算基础
	 */
	private String incomeCalcBasis;

	/**
	 * 托管费率
	 */
//	private BigDecimal manageRate = new BigDecimal(0);
	/**
	 * 固定管理费率
	 */
//	private BigDecimal fixedManageRate = new BigDecimal(0);
	/**
	 * 基础收益率
	 */
	private BigDecimal basicRatio = new BigDecimal(0);

	/**
	 * 平台运营费率
	 */
	private BigDecimal operationRate = new BigDecimal(0);

	/**
	 * [D] 收益结转周期
	 */
	private String accrualCycleOid;

	/**
	 * 复利方式
	 */
//	private String payModeOid;
	/**
	 * 复利具体第几天
	 */
//	private Integer payModeDay = 0;

	/**
	 * 募集开始时间类型
	 */
	private String raiseStartDateType;

	/**
	 * 募集开始日
	 */
	private Date raiseStartDate;

	/**
	 * 募集期
	 */
	private Integer raisePeriodDays = 0;
	/**
	 * 锁定期
	 */
	private Integer lockPeriodDays = 0;
	/**
	 * 募集期满后最晚成立日
	 */
	private Integer foundDays = 0;
	/**
	 * 起息日
	 */
	private Integer interestsFirstDays = 0;
	/**
	 * 存续期
	 */
	private Integer durationPeriodDays = 0;

	/**
	 * 预期年化收益率
	 */
	private BigDecimal expAror = new BigDecimal(0);

	/**
	 * 预期年化收益率区间
	 */
	private BigDecimal expArorSec = new BigDecimal(0);
	
	/**
	 * 奖励收益率
	 */
	private BigDecimal rewardInterest = BigDecimal.ZERO;

	/**
	 * 募集总份额(产品可售头寸)
	 */
	private BigDecimal raisedTotalNumber = new BigDecimal(0);
	/**
	 * 单位份额净值
	 */
	private BigDecimal netUnitShare = new BigDecimal(0);
	/**
	 * 单笔投资最低份额
	 */
	private BigDecimal investMin = new BigDecimal(0);
	/**
	 * 单笔投资追加份额
	 */
	private BigDecimal investAdditional = new BigDecimal(0);
	/**
	 * 投资最高份额
	 */
	private BigDecimal investMax = new BigDecimal(0);

	/**
	 * 有效投资日类型
	 */
	private String investDateType;
	/**
	 * 单笔净赎回下限
	 */
	private BigDecimal minRredeem = new BigDecimal(0);
	/**
	 * 单笔净赎回上限
	 */
	private BigDecimal maxRredeem = new BigDecimal(0);
	/**
	 * 单笔赎回追加份额
	 */
	private BigDecimal additionalRredeem = new BigDecimal(0);
	/**
	 * rredeemDateType
	 */
	private String rredeemDateType;
	/**
	 * 单日净赎回上限
	 */
	private BigDecimal netMaxRredeemDay = new BigDecimal(0);
	/**
	 * 单人持有上限
	 */
	private BigDecimal maxHold = new BigDecimal(0);
	/**
	 * 剩余赎回金额
	 */
	private BigDecimal dailyNetMaxRredeem = new BigDecimal(0);

	/**
	 * 还本付息日
	 */
	private Integer accrualRepayDays = 0;
	/**
	 * 申购确认日
	 */
	private Integer purchaseConfirmDays = 0;
	/**
	 * 申购确认日类型
	 */
//	private String purchaseConfirmDaysType;
	/**
	 * 赎回确认日
	 */
	private Integer redeemConfirmDays = 0;
	/**
	 * 赎回确认日类型
	 */
//	private String redeemConfirmDaysType;
	/**
	 * 产品成立时间类型
	 */
	private String setupDateType;
	/**
	 * 产品成立时间
	 */
	private Date setupDate;
	/**
	 * 赎回定时任务天数
	 */
//	private Integer redeemTimingTaskDays = 0;
	/**
	 * 投资标的
	 */
	private String investComment;
	/**
	 * 产品说明
	 */
	private String instruction;
	/**
	 * 风险等级
	 */
	private String riskLevel;
	/**
	 * 投资者类型
	 */
	private String investorLevel;
	/**
	 * 附加文件
	 */
	private String fileKeys;
	/**
	 * 投资协议书
	 */
	private String investFileKey;
	/**
	 * 信息服务协议
	 */
	private String serviceFileKey;

	/**
	 * 产品状态
	 */
	private String state;

	/**
	 * 创建时间
	 */
	private Timestamp createTime;

	/**
	 * 更新时间
	 */
	private Timestamp updateTime;

	/**
	 * 操作员
	 */
	private String operator;
	/**
	 * 募集结束日期
	 */
	private Date raiseEndDate;
	/**
	 * 募集宣告失败日期
	 */
	private Date raiseFailDate;
	/**
	 * 存续期结束日期
	 */
	private Date durationPeriodEndDate;

	/**
	 * 产品清算（结束）日期
	 */
//	private Date endDate;

	/**
	 * 存续期内还款日期
	 */
//	private Date durationRepaymentDate;
	/**
	 * 来源
	 */
//	private String stems;
	/**
	 * 是否删除
	 */
	private String isDeleted;
	/**
	 * 审核状态
	 */
	private String auditState;
	/**
	 * 当前份额(投资者持有份额)
	 */
	private BigDecimal currentVolume = new BigDecimal(0);
	
	/**
	 * 系统按每个工作日处理赎回申请，赎回金额不得超过金猪宝（上一个交易日）在投总金额的20%
	 */
	
	/**
	 * 上一个交易日产品规模(基于占比算)
	 */
	private BigDecimal previousCurVolume = BigDecimal.ZERO;
	
	/**
	 * 赎回占上一交易日规模百分比
	 */
	private BigDecimal previousCurVolumePercent = BigDecimal.ZERO;
	
	/**
	 * 赎回占比开关
	 */
	private String isPreviousCurVolume = WishplanProduct.NO;
	
	
	/**
	 * 已募份额(累计申购份额)
	 */
	private BigDecimal collectedVolume = new BigDecimal(0);
	/**
	 * 已投次数
	 */
	private Integer purchaseNum = 0;
	/**
	 * 锁定已募份额(申购冻结金额)
	 */
	private BigDecimal lockCollectedVolume = new BigDecimal(0);

	/**
	 * 最迟还本付息日期
	 */
	private Date repayDate;

	/**
	 * 付息状态
	 */
	private String repayInterestStatus;

	/**
	 * 还本状态
	 */
	private String repayLoanStatus;

	/**
	 * 最新收益确认日期
	 */
	private Date newestProfitConfirmDate;

	/**
	 * 最高可售份额(申请的)
	 */
	private BigDecimal maxSaleVolume = new BigDecimal(0);

	/**
	 * 开放申购期
	 */
	private String isOpenPurchase;
	/**
	 * 开放赎回期
	 */
	private String isOpenRemeed;
	/**
	 * 申购申请状态
	 */
	private String purchaseApplyStatus;
	/**
	 * 赎回申请状态
	 */
	private String redeemApplyStatus;

	/**
	 * 清盘中时间
	 */
	private Timestamp clearingTime;
	/**
	 * 清盘中操作人
	 */
	private String clearingOperator;

	/**
	 * 清盘结束时间
	 */
	private Timestamp clearedTime;

	/**
	 * 申购人数
	 */
	private Integer purchasePeopleNum = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 单人单日赎回上限
	 */
	private BigDecimal singleDailyMaxRedeem=new BigDecimal(0);
	/**
	 * 是否屏蔽赎回确认
	 */
//	private String isOpenRedeemConfirm;
	/**
	 * 交易开始时间
	 */
	private String dealStartTime;
	/**
	 * 交易结束时间
	 */
	private String dealEndTime;
	
	/**
	 * 快速赎回开关	fastRedeemStatus	varchar(32)	32		FALSE	FALSE	FALSE
	 *打开：YES 关闭：NO
		快速赎回阀值	fastRedeemMax	decimal(20,4)	20	4	FALSE	FALSE	FALSE
	 */
//	private String fastRedeemStatus;
//	private BigDecimal fastRedeemMax = BigDecimal.ZERO;
//	private BigDecimal fastRedeemLeft = BigDecimal.ZERO;
	
	
	
	/**
	 * 募集期预期年化收益
	 */
	private BigDecimal recPeriodExpAnYield = BigDecimal.ZERO;
	/**
	 * 募集满额后是否自动触发成立 枚举：MANUAL:否,AUTO:是
	 */
	private String raiseFullFoundType;
	/**
	 * 募集满额后第X个自然日后自动成立
	 */
	private Integer autoFoundDays = 0;
	
	/**
	 * 逾期状态
	 */
	private String overdueStatus;
	
	/**
	 * 是否自动派息 YES NO
	 */
//	private String isAutoAssignIncome;
	
	/**
	 * 单人单日赎回次数
	 */
	private Integer singleDayRedeemCount; 
	/**
	 * 平仓规则
	 */
	private String closingRule;
	
	/**
	 * 赎回立刻不计息	
	 */
	private String redeemWithoutInterest = WishplanProduct.PRODUCT_redeemWithoutInterest_off;
	
	/**
	 * 收益处理方式
	 */
	private String incomeDealType = WishplanProduct.PRODUCT_incomeDealType_reinvest;
}
