package com.guohuai.ams.illiquidAsset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_GAM_ILLIQUID_ASSET")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class IlliquidAsset extends UUID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 标的类型
	public static final String ILLIQUIDASSET_TYPE_ZHAIQUAN = "TARGETTYPE_08"; //债权
	public static final String ILLIQUIDASSET_TYPE_YINPIAO = "TARGETTYPE_15"; //银票
	public static final String ILLIQUIDASSET_TYPE_SHANGPIAO = "TARGETTYPE_16"; //商票
	public static final String ILLIQUIDASSET_TYPE_XIANJINDAI = "TARGETTYPE_17"; //现金贷
	public static final String ILLIQUIDASSET_TYPE_XIAOFEIFENQI = "TARGETTYPE_18";//消费分期
	public static final String ILLIQUIDASSET_TYPE_GYJINRONGLEI = "TARGETTYPE_19";//供应链金融类

	// 到期一次还本付息
	public static final String PAYMENT_METHOD_A_DEBT_SERVICE_DUE = "A_DEBT_SERVICE_DUE";
	// 按月付息到期还本
	public static final String PAYMENT_METHOD_EACH_INTEREST_RINCIPAL_DUE = "EACH_INTEREST_RINCIPAL_DUE";
	// 等额本息
	public static final String PAYMENT_METHOD_FIXED_PAYMENT_MORTGAGE = "FIXED-PAYMENT_MORTGAGE";
	// 等额本金
	public static final String PAYMENT_METHOD_FIXED_BASIS_MORTGAGE = "FIXED-BASIS_MORTGAGE";

	// 生命周期状态
	// 未开始募集
	public static final String ILLIQUIDASSET_LIFESTATE_B4_COLLECT = "B4_COLLECT";
	// 募集期
	public static final String ILLIQUIDASSET_LIFESTATE_COLLECTING = "COLLECTING";
	// 募集结束
	public static final String ILLIQUIDASSET_LIFESTATE_OVER_COLLECT = "OVER_COLLECT";
	// 未成立
	public static final String ILLIQUIDASSET_LIFESTATE_UNSETUP = "UNSETUP";
	// 已成立
	public static final String ILLIQUIDASSET_LIFESTATE_SETUP = "SETUP";
	// 成立失败
	public static final String ILLIQUIDASSET_LIFESTATE_SETUP_FAIL = "SETUP_FAIL";
	// 已起息
	public static final String ILLIQUIDASSET_LIFESTATE_VALUEDATE = "VALUEDATE";
	// 已到期
	public static final String ILLIQUIDASSET_LIFESTATE_OVER_VALUEDATE = "OVER_VALUEDATE";
	// 本息兑付
	public static final String ILLIQUIDASSET_LIFESTATE_REPAYMENTS = "REPAYMENTS";
	//  已逾期
	public static final String ILLIQUIDASSET_LIFESTATE_OVERDUE = "OVERDUE";
	// 逾期兑付
	public static final String ILLIQUIDASSET_LIFESTATE_OVERDUE_REPAYMENTS = "OVERDUE_REPAYMENTS";
	// 逾期转让
	public static final String ILLIQUIDASSET_LIFESTATE_OVERDUE_TRANSFER = "OVERDUE_TRANSFER";
	// 逾期坏账核销
	public static final String ILLIQUIDASSET_LIFESTATE_OVERDUE_CANCELLATION = "OVERDUE_CANCELLATION";
	// 转让
	public static final String ILLIQUIDASSET_LIFESTATE_TRANSFER = "TRANSFER";
	// 坏账核销
	public static final String ILLIQUIDASSET_LIFESTATE_CANCELLATION = "CANCELLATION";

	// 资产状态状态
	public static final String ILLIQUIDASSET_STATE_CREATE = "CREATE"; // create 新建；
	public static final String ILLIQUIDASSET_STATE_AUDITING = "AUDITING"; // auditing 审核中；
	public static final String ILLIQUIDASSET_STATE_PASS = "PASS"; // 审核通过；
	public static final String ILLIQUIDASSET_STATE_DURATION = "DURATION"; // duration 审核通过；
	public static final String ILLIQUIDASSET_STATE_REJECT = "REJECT"; // reject 驳回；
	public static final String ILLIQUIDASSET_STATE_INCALID = "INCALID"; // invalid 作废

	public static final String ILLIQUIDASSET_REPAYMENTSTART_MOONSTART = "MOONSTART"; // 月初
	public static final String ILLIQUIDASSET_REPAYMENTSTART_MOONEND = "MOONEND"; // 月末

	private String sn; // 标的编号
	private String name; // 标的名称
	private String type; // 标的类型		现金贷；	消费分期；	银票；	商票；
	private BigDecimal raiseScope; // 标的规模/转让金额
	private BigDecimal starValue; // 起夠金額
	private Integer life; // 标的期限
	private String lifeUnit; // 标的期限单位	枚举: 	年: YEAR	月: MONTH	日: DAY
	private Integer lifed; //标的期限[日]
	private Date collectStartDate; // 募集起始日
	private Date collectEndDate; // 募集截止日
	private BigDecimal collectIncomeRate; // 募集期年化收益率
	private BigDecimal expAror; // 预期年化收益率
	private BigDecimal overdueRate; // 逾期年化收益率
	private String accrualType; // 还款方式/付息方式		到期一次性还本付息；	按月付息到期还本；	等额本息；	等额本金。
	private String accrualCycleType; //付息周期方式
	private Date setDate; // 成立日
	private Date restStartDate; // 起息日
	private Date restEndDate; // 截止日
	private Date overdueDay; // 逾期开始日
	private Integer overdueDays; // 逾期开始天数
	private Integer accrualDate; //付息日
	private BigDecimal ticketValue; // 票面金额
	private BigDecimal purchaseValue; // 收购金额
	private Date capitalSettlementDate; // 本金结算日
	private Date returnBackDate; // 收益到账日
	private Date capitalBackDate; // 本金到账日
	private Integer contractDays; // 合同年天数	直接存360或者365
	private BigDecimal expIncome; // 预计收益
	private BigDecimal applyAmount; // 申请中金额
	private BigDecimal holdShare; // 持有份额
	private BigDecimal holdIncome; // 持有收益
	private BigDecimal lockupCapital; // 冻结本金
	private BigDecimal lockupIncome; // 冻结收益
	private String lifeState; //标的状态
	private String state; // 状态 create 新建；   auditing 审核中；   duration 审核通过；   reject 驳回；   invalid 作废
	private String ticketNumber; //票号
	private String productType; //产品类型
	private String drawer; // 出票人
	private String drawerAccount; // 出票人账号
	private String drawerrBank; // 出票人开户行名称（精确到支行）
	private String payee; // 收款人
	private String payeeAccount; // 收款人账号
	private String payeeBank; // 收款人银行名（精确到支行）
	private String accepter; // 承兑人
	private String accepterAccount; // 承兑人账号
	private String accepterBank; // 承兑人开户行行号
	private String borrower; // 借款人
	private String city; // 借款人所在城市
	private String province; // 借款人所在省份（市）
	private String borrowerID; // 借款人身份证号
	private Date firstBorrowDate; // 借款人第一次借款日期
	private Date repaymentDate; // 借款人成功还款日期
	private Integer repaymentTimes; // 借款人成功还款次数
	private Integer overdueTimes; // 借款人逾期次数
	private String payCapitalFrom; // 还款来源
	private String holdPorpush; // 持有目的/资金用途
	private String investment; // 投资方向
	private String subjectRating; // 主题评级
	private String ratingAgency; // 评级机构
	private Date ratingTime; // 评级时间
	private String prior; // 优先级

	private String expArorDesc; // 收益说明
	private String superiority; // 优势说明
	private String financer; // 融资方
	private String financerDesc; // 融资方简介
	private String warrantor; // 担保方
	private String warrantorDesc; // 担保方简介
	private String usages; // 资金用途
	private String repayment; // 还款说明
	private String risk; // 风控措施
	private String servicePlatform; // 服务平台
	private String regulatoryBank; // 监管银行
	private String recordOrganization; // 备案登机机构
	private String productSpecifications; // 产品说明书
	private String riskDisclosure; // 风险揭示书
	private String platformServiceAgreement; // 平台服务协议
	private String photocopy; // 影印件
	private String rejectDesc; // 拒绝理由
	private Integer collectScore; // 标的总评分
	private BigDecimal collectScoreWeight; // 标的总评分系数
	private BigDecimal riskRate; // 标的风险系数
	private BigDecimal price; // 当前单价
	private BigDecimal dayProfit; // 当日收益
	private BigDecimal totalPfofit; // 累计收益
	private BigDecimal valuations; // 当前估值
	private BigDecimal netValue; // 当前净值
	private String creator; // 创建者
	private String operator; // 操作者
	private Timestamp createTime;
	private Timestamp updateTime;
	private String court;//法院起诉
	private String messageDis;//信息披露
	private String missKey;//兜底
	private String intengo;//意向洽谈
	private String assure;//担保
	private String codeImageName;//身份证图片
	private String bankImageName;//银行流水图片
	private String marryImageName;//结婚证图片
	private String houseImageName;//房产证图片
	private BigDecimal mortgageAmount;//抵押成数
	
}
