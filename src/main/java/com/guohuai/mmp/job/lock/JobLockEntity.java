package com.guohuai.mmp.job.lock;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_JOB_LOCK")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@lombok.Builder
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class JobLockEntity extends UUID {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5109160803296992814L;

	public static final String JOB_jobStatus_toRun = "toRun";
	public static final String JOB_jobStatus_processing = "processing";

	public static final String JOB_jobId_snapshot = "cron.mmp.snapshot";
	public static final String JOB_jobId_practice = "cron.mmp.practice";
	public static final String JOB_jobId_createAllNew = "cron.mmp.publiser_offset";
//	public static final String JOB_jonId_createBankNew = "cron.mmp.bank_offset";
	public static final String JOB_jobId_unlockRedeem = "cron.mmp.unlock_redeem";
	public static final String JOB_jobId_unlockAccrual = "cron.mmp.unlock_accrual";
	public static final String JOB_jobId_resetToday = "cron.mmp.reset_today";
	public static final String JOB_jobId_interestTnRaise = "cron.mmp.interest_tn";
	public static final String JOB_jobId_overdueTimes = "cron.mmp.overdue_times";
	public static final String JOB_jobId_uploadPDF = "cron.mmp.upload_pdf";
	public static final String JOB_jobId_createHtml = "cron.mmp.create_html";
	public static final String JOB_jobId_BankStatement = "cron.mmp.bank_statement";
	//	public static final String JOB_jobId_calcPoolProfitSchedule = "cron.ams.calcPoolProfitSchedule"; // 计算资产池当日收益
	//	public static final String JOB_jobId_updateStateSchedule = "cron.ams.updateStateSchedule"; // 重置资产池收益计算和收益分配的状态
	//	public static final String JOB_jobId_updateLifeStateSchedule = "cron.ams.updateLifeStateSchedule"; // 改变标的状态（存续期-->兑付期）
	public static final String JOB_jobId_scheduleProductState = "cron.mmp.product_state";//当<<募集开始日期>>到,募集未开始变为募集中;
	public static final String JOB_jobId_scheduleSendProductMaxSaleVolume = "cron.mmp.product_maxSaleVolume";//可售份额排期发放
//	public static final String JOB_jobId_publishersProductInvestorTop5 = "cron.mmp.publisher_investTop5Product";
	public static final String JOB_jobId_clear = "cron.mmp.clear_order"; // 清盘赎回
	public static final String JOB_jobId_statChannelYesterdayInvestInfo = "cron.mmp.platform_channelinveststat";
	public static final String JOB_jobId_tulipResend = "cron.mmp.tulip_resend";
	public static final String JOB_jobId_accResend = "cron.mmp.acc_resend";
	public static final String JOB_jobId_payResend = "cron.mmp.pay_resend";  // 结算系统请求重发
	public static final String JOB_jobId_taskUseCoupon = "taskUseCoupon.task"; //体验金异步投资
	public static final String JOB_jobId_scheduleBatchBack = "scheduleBatchBack.task";
	public static final String JOB_jobId_scheduleInsertCompareCheck = "scheduleInsertCompareCheck.task";
	public static final String JOB_jobId_opeschedule = "opeschedule.task"; // 运营查询JOB_jobId_portfolioEstimate
	
	public static final String JOB_jobId_portfolioEstimate = "portfolio.estimate.task"; // 投资组合每日估值
	
	public static final String JOB_jobId_illiquidStateUpdate = "illiquid.state.update.task"; // 非现金类标的, 状态更新
	
	public static final String JOB_jobId_serital = "serital.task"; // 序列化任务
	
	public static final String JOB_jobId_resetMonth = "cron.mmp.reset_month"; // 投资者提现次数月统计数据重置
	
	public static final String JOB_jobId_cancel = "cancel.task"; // 取消超时序列化任务
		
	public static final String JOB_jobId_illiquidRepaymentStateUpdate = "illiquid.repayment.state.update.task"; // 非现金类标的还款计划, 状态更新
	
	public static final String JOB_jobId_cronCheck = "cron.check.task"; // 平台余额对账
	
	public static final String JOB_jobId_tradeCalendar = "tradeCalendar.task"; // 同步交易日历
	
	public static final String JOB_jobId_incomeDistributionSchedule = "incomeDistributionSchedule.task";	// 收益分配排期执行
	public static final String JOB_jobId_incomeDistributionNotice = "incomeDistributionNotice.task";		// 未收益分配排期通知执行

	public static final String JOB_jobId_interestT0Cash = "cron.mmp.interestT0Cash.task";		// 活期现金分红-调结算转入用户余额
	
	public static final String JOB_jobId_platFormStatisticsAllShedule = "cron.mmp.platForm.statistics.all.shedule";	// 平台基本账户统计任务-全表
	public static final String JOB_jobId_platFormStatisticsIncrementShedule = "cron.mmp.platForm.statistics.increment.shedule";	// 平台基本账户统计任务-增量
	public static final String JOB_jobId_SMSSendShedule = "cron.mmp.sms.send.shedule";	// 短信发送任务
	
	public static final String JOB_jobId_tulipEventRegister = "cron.tulip.event.register.shedule";	//tulip 注册事件
	public static final String JOB_jobId_tulipEventFriend = "cron.tulip.event.friend.shedule";	//tulip 推荐人事件
	public static final String JOB_jobId_tulipEventRealname = "cron.tulip.event.realName.shedule";	//tulip 实名认证事件
	public static final String JOB_jobId_tulipEventInvestment = "cron.tulip.event.investment.shedule";	//tulip 申购事件
	public static final String JOB_jobId_tulipEventRedeem = "cron.tulip.event.redeem.shedule";	//tulip 赎回事件
	public static final String JOB_jobId_tulipEventBeare = "cron.tulip.event.beare.shedule";	//tulip 到期兑付事件
	public static final String JOB_jobId_tulipEventCash = "cron.tulip.event.cash.shedule";	//tulip 提现事件
	public static final String JOB_jobId_tulipEventRefund = "cron.tulip.event.refund.shedule";	//tulip 退款事件
	public static final String JOB_jobId_tulipEventBingd = "cron.tulip.event.bingd.shedule";	//tulip 绑卡事件
	public static final String JOB_jobId_tulipEventRecharge = "cron.tulip.event.recharge.shedule";	//tulip 充值事件
	public static final String JOB_jobId_tulipEventSign = "cron.tulip.event.sign.shedule";	//tulip 签到事件
	public static final String JOB_jobId_tulipEventInvalidb = "cron.tulip.event.invalidb.shedule";	//tulip 流标事件
	
	
	String jobId;
	String jobTime;
	String jobStatus;

	private Timestamp createTime;
	private Timestamp updateTime;

}
