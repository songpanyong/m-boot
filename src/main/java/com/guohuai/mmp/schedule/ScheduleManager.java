package com.guohuai.mmp.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guohuai.ams.duration.fact.income.schedule.IncomeScheduleService;
import com.guohuai.ams.illiquidAsset.IlliquidAssetScheduleService;
import com.guohuai.ams.portfolio20.estimate.PortfolioEstimateService;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentScheduleService;
import com.guohuai.ams.product.ProductSchedService;
import com.guohuai.ams.switchcraft.SwitchService;
import com.guohuai.calendar.TradeCalendarService;
import com.guohuai.mmp.investor.baseaccount.check.CheckScheduleService;
import com.guohuai.mmp.investor.baseaccount.log.TaskCouponLogService;
import com.guohuai.mmp.investor.tradeorder.InvestorClearTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderBatchPayService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.PlanController;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.OnePlanScheduleService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanMonthScheduleService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanProductRedeemService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.IncomeBalanceService;
import com.guohuai.mmp.ope.schedule.OpeScheduleService;
import com.guohuai.mmp.platform.accment.AccResendService;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsScheduleService;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckService;
import com.guohuai.mmp.platform.payment.PayResendService;
import com.guohuai.mmp.platform.publisher.dividend.offset.DividendOffsetService;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.platform.tulip.TulipResendService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.holdapart.snapshot.SnapshotService;
import com.guohuai.mmp.publisher.investor.InterestTnRaise;
import com.guohuai.mmp.publisher.product.agreement.ProductAgreementPDFService;
import com.guohuai.mmp.publisher.product.agreement.ProductAgreementService;
import com.guohuai.mmp.publisher.product.rewardincomepractice.PracticeService;
import com.guohuai.mmp.serialtask.SerialTaskService;
import com.guohuai.mmp.sms.SMSScheduleService;

import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(prefix = "ams", name = "needSchedule", havingValue = "yes")
@Component
@Async("mySimpleAsync")
@Slf4j
public class ScheduleManager {

//	private Logger logger = LoggerFactory.getLogger(ScheduleManager.class);

	@Autowired
	private PublisherOffsetService publisherOffsetService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private PracticeService practiceService;
	@Autowired
	private ProductSchedService productSchedService;
	@Autowired
	private InterestTnRaise interestTnRaise;
	@Autowired
	private ResetTodayService resetTodayService;
	@Autowired
	private ResetMonthService resetMonthService;
	@Autowired
	private OverdueTimesService overdueTimesService;
	@Autowired
	private InvestorClearTradeOrderService investorClearTradeOrderService;
	@Autowired
	private ProductAgreementService productAgreementService;
	@Autowired
	private TulipResendService tulipResendService;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private SnapshotService snapshotService;
	@Autowired
	private AccResendService accResendService;
	@Autowired
	private PayResendService payResendService;
	@Autowired
	private OpeScheduleService opeScheduleService;
	@Autowired
	private PortfolioEstimateService portfolioEstimateService;
	@Autowired
	private IlliquidAssetScheduleService illiquidAssetUpdateStateScheduleService;
	@Autowired
	private PortfolioIlliquidHoldRepaymentScheduleService portfolioIlliquidHoldRepaymentScheduleService;
	@Autowired
	private ProductAgreementPDFService productAgreementPDFService;
	@Autowired
	private CheckScheduleService checkScheduleService;
	@Autowired
	private TradeCalendarService tradeCalendarService;
	@Autowired
	private YsService ysService;
	@Autowired
	private IncomeScheduleService incomeScheduleService;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private TaskCouponLogService taskCouponLogService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private PlatformFinanceCheckService platformFinanceCheckService;
	@Autowired
	private DividendOffsetService dividendOffsetService;
	@Autowired
	private InvestorTradeOrderBatchPayService investorTradeOrderBatchPayService;
	@Autowired
	private SwitchService switchService;
	@Autowired
	private PlatformStatisticsScheduleService platformStatisticsScheduleService;
	@Autowired
	private SMSScheduleService sMSScheduleService;

	@Autowired
	private PlanMonthScheduleService planMothScheduleService;
	
	@Autowired
	private IncomeBalanceService incomeBalanceService;
	
	@Autowired
	private PlanProductRedeemService planRedeemService;
	
	@Autowired
	private OnePlanScheduleService eduTourScheduleOnce;
	
	
	/**
	 * 计息份额快照, 快照-先于派息试算
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.snapshot]:0 0 1 * * ?}")
	public void snapshot() {
		// logger.info("<<-----开始计息份额快照----->>");
		try {
			this.snapshotService.snapshot();
		} catch (Throwable e) {
			// logger.error("<<-----失败计息份额快照----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功计息份额快照----->>");
	}

	/**
	 * 奖励收益试算，合起来算，可能有小数差异
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.practice]:0 0 1 * * ?}")
	public void practice() {
		// logger.info("<<-----奖励收益试算 start---->>");
		try {
			// date
			practiceService.practice();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		// logger.info("<<-----奖励收益试算 end---->>");
	}

	/**
	 * 创建平台-发行人的普通轧差批次, 生成某日期批次做清结算
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.publiser_offset]:0 0 1 * * ?}")
	public void createAllNew() {
		// logger.info("<<-----开始创建平台-发行人的普通轧差批次----->>");
		try {
			this.publisherOffsetService.createAllNew();
		} catch (Throwable e) {
			// logger.error("<<-----失败创建平台-发行人的普通轧差批次----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功创建平台-发行人的普通轧差批次----->>");
	}

	/**
	 * 解锁赎回锁定份额,定期、活期都需要一定的时间才可以解锁赎回
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.unlock_redeem]:0 0 1 * * ?}")
	public void unlockRedeem() {
		// logger.info("<<-----开始解锁赎回锁定份额----->>");
		try {
			// date
			this.publisherHoldService.unlockRedeem();
		} catch (Throwable e) {
			// logger.error("<<-----失败解锁赎回锁定份额----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功解锁赎回锁定份额----->>");
	}

	/**
	 * 解锁可计息份额，定期、活期都需要一定的时间才可以解锁计息
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.unlock_accrual]:0 0 1 * * ?}")
	public void unlockAccrual() {
		// logger.info("<<-----开始解锁计息锁定份额----->>");
		try {
			// date
			this.publisherHoldService.unlockAccrual();
		} catch (Throwable e) {
			// logger.error("<<-----失败计息赎回锁定份额----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功解锁赎回锁定份额----->>");
	}
	
	//投资者今日统计数据重置
	@Scheduled(cron = "${cron.option[cron.mmp.reset_today]:1 0 0 * * ?}")
	public void resetToday() {
		{
			// logger.info("<<-----投资者今日统计数据重置 start----->>");
			try {

				this.resetTodayService.resetToday();
			} catch (Throwable e) {
				// logger.error("<<-----投资者今日统计数据重置 failed----->>");
				e.printStackTrace();
			}
			// logger.info("<<-----投资者今日统计数据重置 success----->>");
		}
	}
	//投资者昨日统计数据重置 
	@Scheduled(cron = "${cron.option[cron.mmp.yesterday_statistics]:1 0 0 * * ?}")
	public void ys() {
		{
			// logger.info("<<-----投资者昨日统计数据重置 start----->>");
			try {

				this.ysService.ys();
			} catch (Throwable e) {
				// logger.error("<<-----投资者昨日统计数据重置 failed----->>");
				e.printStackTrace();
			}
			// logger.info("<<-----投资者昨日统计数据重置 success----->>");
		}
	}
	
	

	@Scheduled(cron = "${cron.option[cron.mmp.reset_month]:10 0 0 1 * ?}")
	public void resetMonth() {
		{
			// logger.info("<<-----投资者提现次数月统计数据重置 start----->>");
			try {
//				final Calendar c = Calendar.getInstance();
//				if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
					this.resetMonthService.resetMonth();
//				}
			} catch (Throwable e) {
				// logger.error("<<-----投资者提现次数月统计数据重置 failed----->>");
				e.printStackTrace();
			}
			// logger.info("<<-----投资者提现次数月统计数据重置 success----->>");
		}
	}

	/**
	 * 定期募集期收益发放
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.interest_tn]:0 0 1 * * ?}")
	public void interestTnRaise() {
		// logger.info("<<-----募集期已确认份额计息 start----->>");
		try {
			this.interestTnRaise.interestTnRaise();
		} catch (Throwable e) {
			// logger.error("<<-----募集期已确认份额计息 fail----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----募集期已确认份额计息 success----->>");
	}
   //清结算逾期
	@Scheduled(cron = "${cron.option[cron.mmp.overdue_times]:0 0 1 * * ?}")
	public void overdueTimes() {
		// logger.info("<<----逾期次数统计 start----->>");
		try {
			overdueTimesService.overdueTimes();
		} catch (Throwable e) {
			// logger.error("<<-----逾期次数统计 fail----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----逾期次数统计 success----->>");
	}
    /**
     * 上传模板合同
     */
//	@Scheduled(cron = "${cron.option[cron.mmp.upload_pdf]:0 0 1 * * ?}")
	@Scheduled(cron = "${cron.option[cron.mmp.up_pdf]:0 0/3 * * * ?}")
	public void uploadPdf() {
		log.info("<<----uploadPdf start----->>");
		try {
//			productAgreementPDFService.uploadPDF();
			productAgreementPDFService.uploadPDFLog();
		} catch (Throwable e) {
			log.error("<<-----uploadPdf fail----->>");
			e.printStackTrace();
		}
		log.info("<<-----uploadPdf success----->>");
	}

//	@Scheduled(cron = "${cron.option[cron.mmp.create_html]:0 0 13 * * ?}")
	@Scheduled(cron = "${cron.option[cron.mmp.html_pdf]:0 0/3 * * * ?}")
	public void createHtml() {
		log.info("<<----createHtml start----->>");
		try {
//			this.productAgreementService.createHtml();
			this.productAgreementService.createHtmlLog();
		} catch (Throwable e) {
			log.error("<<-----createHtml fail----->>");
			e.printStackTrace();
		}
		log.info("<<-----createHtml success----->>");
	}

	/**
	 * 序列化任务，交易流程，串行处理，申购
	 */
	@Scheduled(cron = "${cron.option[serital.task]:0 0/2 * * * ?}")
	public void serialTask() {
		// logger.info("<<-----serial.task start----->>");
		try {
			// useless
			this.serialTaskService.executeTask();
		} catch (Exception e) {
			//this.logger.error("serial.task fail", e);
			e.printStackTrace();
		}
		// logger.info("<<-----serial.task end----->>");
	}

	/**
	 * 取消超时序列化任务
	 */
	@Scheduled(cron = "${cron.option[cancel.task]:0 0/2 * * * ?}")
	public void cancelTask() {
		// logger.info("<<-----cancel.task start----->>");
		try {
			// useless
			this.serialTaskService.resetTimeoutTask();
		} catch (Exception e) {
			// this.logger.error("cancel.task fail", e);
			e.printStackTrace();
		}
		// logger.info("<<-----cancel.task end----->>");
	}


	/**
	 * 活期: 当<<成立开始日期>>到,募集未开始变为募集中; 定期: 当<<募集开始日期>>到,募集未开始变为募集中; 定期: 当<
	 * <募集結束日期>>到或募集满额,募集中变为募集結束; 定期: 当<<存续期结束日期>>到,存续期变为存续期結束; 定期: 当募集满额后自动触发成立
	 * 募集满额后的第X个自然日后自动成立
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.product_state]:0 0 2 * * ?}")
	public void scheduleProductState() {
		// logger.info("<<-----开始活期: 募集未开始->募集中->募集結束,定期: 募集未开始->募集中->募集結束->存续期->存续期結束生命周期变化----->>");
		try {

			this.productSchedService.notstartraiseToRaisingOrRaised();
		} catch (Exception e) {
			// logger.error("<<-----失败活期: 募集未开始->募集中->募集結束,定期: 募集未开始->募集中->募集結束->存续期->存续期結束生命周期变化----->>", e);
			e.printStackTrace();
		}
		// logger.info(// // logger.-成功活期: 募集未开始->募集中->募集結束,定期: 募集未开始->募集中->募集結束->存续期->存续期結束生命周期变化----->>");
	}

	/**
	 * 可售份额排期发放;
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.product_maxSaleVolume]:0 5 0 * * ?}")
	public void scheduleSendProductMaxSaleVolume() {
		// logger.info("<<-----可售份额排期发放 start----->>");
		try {

			productSchedService.scheduleSendProductMaxSaleVolume();
		} catch (Exception e) {
			// this. logger.error("可售份额排期发放失败", e);
			e.printStackTrace();
		}
		// logger.info("<<-----可售份额排期发放 end----->>");
	}



	@Scheduled(cron = "${cron.option[cron.mmp.clear_order]:0 0/10 * * * ?}")
	public void clear() {
		// logger.info("<<----生成清盘赎回单 start----->>");
		try {
			investorClearTradeOrderService.clear();
		} catch (Throwable e) {
			// logger.error("<<-----生成清盘赎回单 fail----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----生成清盘赎回单 success----->>");
	}



	/** 推广平台失败请求重发 */
//	@Scheduled(cron = "${cron.option[cron.mmp.tulip_resend]:0 0/10 * * * ?}")
//	public void tulipResend() {
//		// logger.info("<<-----推广平台失败请求重发 start----->>");
//		try {
//
//			this.tulipResendService.reSendTulipMessage();
//		} catch (Throwable e) {
//			// logger.error("<<-----推广平台失败请求重发 fail----->>");
//			e.printStackTrace();
//		}
//		// logger.info("<<-----推广平台失败请求重发 success----->>");
//	}

	/**
	 * 账户系统请求重发，废弃
	 */
//	@Scheduled(cron = "${cron.option[cron.mmp.acc_resend]:0 0/30 * * * ?}")
//	public void accResend() {
//		// logger.info("<<-----账户系统失败请求重发 start----->>");
//		try {
//			accResendService.resend();
//		} catch (Throwable e) {
//			// logger.error("<<-----账户系统失败请求重发 fail----->>");
//			e.printStackTrace();
//		}
//		// logger.info("<<-----账户系统失败请求重发 success----->>");
//	}
//
//	/**
//	 * 结算系统请求重发，废弃
//	 */
//	@Scheduled(cron = "${cron.option[cron.mmp.pay_resend]:0 0/30 * * * ?}")
//	public void payResend() {
//		// logger.info("<<-----结算系统失败请求重发 start----->>");
//		try {
//			payResendService.resend();
//		} catch (Throwable e) {
//			// logger.error("<<-----结算系统失败请求重发 fail----->>");
//			e.printStackTrace();
//		}
//		// logger.info("<<-----结算系统失败请求重发 success----->>");
//	}


	/**
	 * 运营查询扫描，统计数据
	 */
	@Scheduled(cron = "${cron.option[opeschedule.task]:0 0/5 * * * ?}")
	public void opeScheduleTask() {
		// logger.info("<<-----运营查询扫描 start----->>");
		try {
			this.opeScheduleService.scheduler();
		} catch (Exception e) {
			// this. logger.error("运营查询扫描  fail", e);
			e.printStackTrace();
		}
		// logger.info("<<-----运营查询扫描  end----->>");
	}

	/**
	 * 非现金类标的, 状态更新
	 */
	@Scheduled(cron = "${cron.option[illiquid.state.update.task]:0 0 2 * * ?}")
	public void updateIlliquidAssetLifeState() {
		// logger.info("<<-----非现金类标的, 状态更新 start----->>");
		try {
			this.illiquidAssetUpdateStateScheduleService.updateState(new java.sql.Date(System.currentTimeMillis()));
		} catch (Exception e) {
			// this. logger.error("非现金类标的, 状态更新  fail - x00002e", e);
			e.printStackTrace();
		}
		// logger.info("<<-----非现金类标的, 状态更新  end----->>");
	}
	
	
	@Scheduled(cron = "${cron.option[illiquid.repayment.state.update.task]:0 30 2 * * ?}")
	public void updateIlliquidAssetRepaymentState() {
		// logger.info("<<-----非现金类标的还款计划, 状态更新 start----->>");
		try {
			this.portfolioIlliquidHoldRepaymentScheduleService.updateState(new java.sql.Date(System.currentTimeMillis()));
		} catch (Exception e) {
			// this. logger.error("非现金类标的还款计划, 状态更新  fail - x00003e", e);
			e.printStackTrace();
		}
		// logger.info("<<-----非现金类标的还款计划, 状态更新  end----->>");
	}

	/**
	 * 投资组合每日估值，发行人
	 */
	@Scheduled(cron = "${cron.option[portfolio.estimate.task]:0 0 3 * * ?}")
	public void portfolioEstimate() {
		// logger.info("<<-----投资组合每日估值 start----->>");
		try {
			this.portfolioEstimateService.batchEstimate();
		} catch (Exception e) {
			// this. logger.error("投资组合每日估值  fail - x00001e", e);
			e.printStackTrace();
		}
		// logger.info("<<-----投资组合每日估值  end----->>");
	}
	//对账，余额对账，针对投资者
	/*@Scheduled(cron = "${cron.option[cron.check.task]:0 30 1 * * ?}")
	public void platformCheck() {
		// logger.info("<<-----平台余额对账 start----->>");
		try {
			this.checkScheduleService.platformCheck();
		} catch (Exception e) {
			// this. logger.error("平台余额对账 fail", e);
			e.printStackTrace();
		}
		// logger.info("<<-----平台余额对账 end----->>");

	}*/
	
	@Value("${ams.tradeCalendarSchedule:yes}")
	private String tradeCalendarSchedule;
	//定时任务执行同步交易日历,从页面导入后、进行修改，然后通过代码同步到内存。
	@Scheduled(cron = "${cron.option[tradeCalendar.task]:0 0 21 * * ?}")
	public void tradeCalendarScheduleTask() {
		if (tradeCalendarSchedule != null && tradeCalendarSchedule.equals("yes")){
			// logger.info("<<-----定时任务执行同步交易日历 start----->>");
			try {
				this.tradeCalendarService.tradeCalendarTask();
			} catch (Exception e) {
				// this. logger.error("定时任务执行同步交易日历fail", e);
				e.printStackTrace();
			}
			// logger.info("<<-----定时任务执行同步交易日历 end----->>");
		}else{
			// logger.info("<<-----定时任务执行同步交易日历配置未启动----->>，ams.tradeCalendarSchedule="+tradeCalendarSchedule);
		}
		
	}
	/**
	 * 收益分配排期执行，（类似可售份额发放排期）
	 * 
	 */
	@Scheduled(cron = "${cron.option[incomeDistributionSchedule.task]:0 10 0 * * ?}")
	public void incomeDistributionSchedule() {
		// logger.info("<<-----收益分配排期执行incomeDistributionSchedule.task start----->>");
		try {
			this.incomeScheduleService.incomeSchedule();
		} catch (Exception e) {
			// this. logger.error("收益分配排期执行incomeDistributionSchedule.task fail", e);
			e.printStackTrace();
		}
		// logger.info("<<-----收益分配排期执行incomeDistributionSchedule.task end----->>");
	}
	
	/**
	 * 收益排期当天未设置, 自动收益分配排期(需审核)，短信通知
	 * 
	 */
	@Scheduled(cron = "${cron.option[incomeDistributionNotice.task]:0 55 8 * * ?}")
	public void incomeDistributionNotice() {
		// logger.info("<<-----活期收益未分配提醒执行incomeDistributionNotice.task start----->>");
		try {
			this.incomeScheduleService.noticeSchedule();
		} catch (Exception e) {
			// this. logger.error("活期收益未分配执行incomeDistributionNotice.task fail", e);
			e.printStackTrace();
		}
		// logger.info("<<-----活期收益未分配执行incomeDistributionNotice.task end----->>");
	}
	/**
	 * 体验金异步投资，自动投资
	 */
	@Scheduled(cron = "${cron.option[taskUseCoupon.task]:0 0/5 * * * ?}")
	public void taskUseCoupon() {
		// logger.info("<<-----定时任务执行体验金投资 start----->>");
		try {
			taskCouponLogService.taskUseCoupon();
		} catch (Throwable e) {
			// logger.error("<<-----定时任务执行体验金投资 fail----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----定时任务执行体验金投资 success----->>");
	}
	/**
	 * 卡券自动到期，自动失效
	 */
	//@Scheduled(cron = "${cron.option[tulip.autoModifyCouponStatus]:*/5 * * * * ?}")
	public void autoModifyCouponStatus() {
		// logger.info("<<-----开始凌晨0点5分自动更新卡券状态----->>");
		System.out.println("<<-----开始凌晨0点5分自动更新卡券状态----->>");
		try {
			this.tulipService.updateCouponForExpired();
		} catch (Throwable e) {
			// logger.error("<<-----失败凌晨0点5分自动更新卡券状态----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功凌晨0点5分自动更新卡券状态----->>");
	}
	//tulip活动
	@Scheduled(cron = "${cron.option[tulip.autoOnEvent]:0 15 0 * * ?}")
	public void autoOnEvent() {
		// logger.info("<<-----开始凌晨0点15分自动活动上线----->>");
		try {
			this.tulipService.autoOnEvent();
		} catch (Throwable e) {
			// logger.error("<<-----失败凌晨0点15分自动活动上线----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功凌晨0点15分自动活动上线----->>");
	}
	
	
	@Scheduled(cron = "${cron.option[tulip.autoIssuedCouponBySchedule]:0 15 2 * * ?}")
	public void autoIssuedCouponBySchedule() {
		// logger.info("<<-----开始凌晨2点15分自动更新卡券状态----->>");
		System.out.println("<<-----开始凌晨2点15分自动更新卡券状态----->>");
		try {
			this.tulipService.issuedCouponBySchedule();
		} catch (Throwable e) {
			// logger.error("<<-----失败凌晨2点15分自动更新卡券状态----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功凌晨2点15分自动更新卡券状态----->>");
	}
	@Scheduled(cron = "${cron.option[tulip.autoIssuedCouponByBirthDay]:0 0 9 * * ?}")
	public void autoIssuedBirthDayCoupon() {
		// logger.info("<<-----开始早晨9点处理生日活动----->>");
		System.out.println("<<-----开始早晨9点开始处理生日活动----->>");
		try {
			this.tulipService.issuedCouponByBirthDay();
		} catch (Throwable e) {
			// logger.error("<<-----失败早晨9点处理生日活动----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功早晨9点处理生日活动----->>");
	}
	
	/**
	 * 体验金平仓，平掉本金，收益还在持有人手册中，需要发动一次正常赎回，才能回到余额。
	 */
	@Scheduled(cron = "${cron.option[flat.exp.gold.task]:10 0 15 * * ?}")
    public void flatExpGold() {
        // logger.info("<<-----flatExpGold.task start----->>");
        try {
            investorTradeOrderService.flatExpGold();
        } catch (Exception e) {
            // this. logger.error("flatExpGold.task fail", e);
            e.printStackTrace();
        }
        // logger.info("<<-----flatExpGold.task end----->>");
    }
	/**
	 * 创建-业务to结算订单对账批次，人工对账四部曲，充值提现、红包
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.platform_CheckOrderBatch]:0 15 1 * * ?}")
	public void createCheckOrderBatch() {
		// logger.info("<<-----开始创建平台--业务to结算订单对账批次----->>");
		try {
			this.platformFinanceCheckService.createCheckOrderBatch();
		} catch (Throwable e) {
			// logger.error("<<-----失败创建平台--业务to结算订单对账批次----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功创建平台-业务to结算订单对账批次----->>");
	}
	/**
	 * 活期现金分红-调结算转入用户余额，（活期分红派息后）
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.dividen.auto.close]:0 0/5 * * * ?}")
	public void interestT0Cash() {
		// logger.info("<<-----开始活期现金分红-调结算转入用户余额----->>");
		try {
			this.dividendOffsetService.dividend();
		} catch (Throwable e) {
			// logger.error("<<-----失败活期现金分红-调结算转入用户余额----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----成功活期现金分红-调结算转入用户余额----->>");
	}
	
	/**
	 * 批量代付给投资者，转账
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.batchpay]:0 0/5 * * * ?}")
	public void batchPay() {
		// logger.info("<<-----批量代付给投资者 start ----->>");
		try {
			this.investorTradeOrderBatchPayService.batchPay();
		} catch (Throwable e) {
			// logger.error("<<-----批量代付给投资者 failure ----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----批量代付给投资者 end ----->>");
	}
	
	/**
	 * 平台基本账户统计定时任务-全表扫描，cms首页
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.platForm.statistics.all.shedule]:0 0 0/1 * * ?}")
	public void platFormStatisticsAllSchedule() {
		// logger.info("<<-----平台基本账户统计定时任务-全表 start ----->>");
		try {
			this.platformStatisticsScheduleService.statisticsAllSchedule();
		} catch (Throwable e) {
			// logger.error("<<-----平台基本账户统计定时任务-全表 failure ----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----平台基本账户统计定时任务-全表 end ----->>");
	}
	
	/**
	 * 平台基本账户统计定时任务-增量扫描
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.platForm.statistics.increment.shedule]:0 0/5 * * * ?}")
	public void platFormStatisticsIncrementSchedule() {
		// logger.info("<<-----平台基本账户统计定时任务-增量 start ----->>");
		try {
			this.platformStatisticsScheduleService.statisticsIncrementSchedule();
		} catch (Throwable e) {
			// logger.error("<<-----平台基本账户统计定时任务-增量 failure ----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----平台基本账户统计定时任务-增量 end ----->>");
	}

	/**
	 * 发送短信
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.sms.send.shedule]:0/2 * * * * ?}")
	public void reSendSMS() {
		// logger.info("<<-----发送短信start ----->>");
		try {
			this.sMSScheduleService.smsReSend();
		} catch (Throwable e) {
			// logger.error("<<-----发送短信 failure ----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----发送短信 end ----->>");
	}
	/**
	 * 以下是运营平台定时任务，已经变换成同步,全部注掉，2018-03-15
	 * 自动发送注册
	 */
	/*
	@Scheduled(cron = "${cron.option[cron.tulip.event.register.shedule]:0/2 * * * * ?}")
	public void autoModifysendRegisterEvent() {
		// logger.info("<<-----开始自动发送注册事件----->>");
		try {
			this.tulipService.sendRegisterEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送注册事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送注册事件成功----->>");
	}
	
	 // 自动发送推荐人
	 
	@Scheduled(cron = "${cron.option[cron.tulip.event.friend.shedule]:0/2 * * * * ?}")
	public void autoModifysendFriendEvent() {
		// logger.info("<<-----开始自动发送推荐人事件----->>");
		try {
			this.tulipService.sendFriendEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送推荐人事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送推荐人事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.realName.shedule]:0/2 * * * * ?}")
	public void autoModifysendSetRealNameEvent() {
		// logger.info("<<-----开始自动发送实名认证事件----->>");
		try {
			this.tulipService.sendSetRealNameEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送实名认证事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送实名认证事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.investment.shedule]:0/2 * * * * ?}")
	public void autoModifysendInvestmentEvent() {
		// logger.info("<<-----开始自动发送申购事件----->>");
		try {
			this.tulipService.sendInvestmentEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送申购事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送申购事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.redeem.shedule]:0/2 * * * * ?}")
	public void autoModifysendRedeemEvent() {
		// logger.info("<<-----开始自动发送赎回事件----->>");
		try {
			this.tulipService.sendRedeemEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送赎回事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送赎回事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.beare.shedule]:0/2 * * * * ?}")
	public void autoModifysendBearerEvent() {
		// logger.info("<<-----开始自动发送到期兑付事件----->>");
		try {
			this.tulipService.sendBearerEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送到期兑付事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送到期兑付事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.cash.shedule]:0/2 * * * * ?}")
	public void autoModifysendCashEvent() {
		// logger.info("<<-----开始自动发送提现事件----->>");
		try {
			this.tulipService.sendCashEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送提现事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送提现事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.refund.shedule]:0/2 * * * * ?}")
	public void autoModifysendRefundEvent() {
		// logger.info("<<-----开始自动发送退款事件----->>");
		try {
			this.tulipService.sendRefundEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送退款事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送退款事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.bingd.shedule]:0/2 * * * * ?}")
	public void autoModifysendBindingCardEvent() {
		// logger.info("<<-----开始自动发送绑卡事件----->>");
		try {
			this.tulipService.sendBindingCardEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送绑卡事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送绑卡事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.recharge.shedule]:0/2 * * * * ?}")
	public void autoModifysendRechargeEvent() {
		// logger.info("<<-----开始自动发送充值事件----->>");
		try {
			this.tulipService.sendRechargeEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送充值事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送充值事件成功----->>");
	}
	@Scheduled(cron = "${cron.option[cron.tulip.event.sign.shedule]:0/2 * * * * ?}")
	public void autoModifysendSignEvent() {
		// logger.info("<<-----开始自动发送签到事件----->>");
		try {
			this.tulipService.sendSignEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送签到事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送签到事件成功----->>");
	}
	
	@Scheduled(cron = "${cron.option[cron.tulip.event.invalidb.shedule]:0/2 * * * * ?}")
	public void autoModifysendInvalidBidsEvent() {
		// logger.info("<<-----开始自动发送流标事件----->>");
		try {
			this.tulipService.sendInvalidBidsEvent();
		} catch (Throwable e) {
			// logger.error("<<-----自动发送流标事件失败----->>");
			e.printStackTrace();
		}
		// logger.info("<<-----自动发送流标事件成功----->>");
	}
	*/
	//Commented out above on 2018-03-15
	/**
	 * monthInvest
	 */
//	@Scheduled(cron = "${cron.option[cron.mmp.monthInvest]:0 15 14 * * ?}")
	@Scheduled(cron = "${cron.option[cron.mmp.monthInvest]:0 5 10,14,21 * * ?}")
	public void monthInvest() {
		log.info("<<-----monthInvest start ----->>");
		try {
			planMothScheduleService.scheduleProcess();
		} catch (Throwable e) {
			log.info("<<-----monthInvest failure ----->>");
			e.printStackTrace();
		}
		log.info("<<-----monthInvest end ----->>");
	}
	
	/**
	 * salaryInvest
	 */
//	@Scheduled(cron = "${cron.option[cron.mmp.salaryInvest]:0 30 14 * * ?}")
//	public void monthSalary() {
//		log.info("<<-----monthSalary start ----->>");
//		try {
//			planMothScheduleService.salarySchedule();
//		} catch (Throwable e) {
//			log.info("<<-----monthSalary failure ----->>");
//			e.printStackTrace();
//		}
//		log.info("<<-----monthSalary end ----->>");
//	}
	
	
	/**
	 * monthOverdue
	 */
//	@Scheduled(cron = "${cron.option[cron.mmp.monthOverdue]:0 45 14 * * ?}")
	@Scheduled(cron = "${cron.option[cron.mmp.monthOverdue]:0 0 10,14,21 * * ?}")
	public void monthOverdue() {
		log.info("<<-----monthOverdue start ----->>");
		try {
			planMothScheduleService.scheduleOverdueProcess();
		} catch (Throwable e) {
			log.info("<<-----monthOverdue failure ----->>");
			e.printStackTrace();
		}
		log.info("<<-----monthOverdue end ----->>");
	}
	
	
	/**
	 * 每半小时一次
	 * redeem2basic
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.redeem2basic]:0 0/15 * * * ?}")
	public void redeem2basic() {
		log.info("<<-----redeem2basic start ----->>");
		try {
			incomeBalanceService.redeem2basic();
		} catch (Throwable e) {
			log.info("<<-----redeem2basic failure ----->>");
			e.printStackTrace();
		}
		log.info("<<-----redeem2basic end ----->>");
	}
	
	/**
	 * overdue  plan product Redeem 产品是活期
	 */
	/*
	@Scheduled(cron = "${cron.option[cron.mmp.planRedeemAgain]:0 50 15 * * ?}")
	public void overduPlanProductRedeem() {
		log.info("<<-----overdue planRedeem start ----->>");
		try {
			planRedeemService.planRedeemAgain();
		} catch (Throwable e) {
			log.info("<<-----overdue planRedeem failure ----->>");
			e.printStackTrace();
		}
		log.info("<<-----overdue planRedeem end ----->>");
	}
	*/
	
	/**
	 * planRedeem on completing 产品是活期
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.planRedeem]:0 0 16,17,18 * * ?}")
	public void planRedeem() {
		log.info("<<-----planRedeem start ----->>");
		try {
			planRedeemService.planRedeem();
		} catch (Throwable e) {
			log.info("<<-----planRedeem failure ----->>");
			e.printStackTrace();
		}
		log.info("<<-----planRedeem end ----->>");
	}
	
	
	/**
	 * eduTourInvest
	 * 每三分钟触发一次
	 */
	@Scheduled(cron = "${cron.option[cron.mmp.eduTourInvest]:0 0/1 * * * ?}")
	public void eduTourInvest() {
		log.info("<<-----eduTourInvest start ----->>");
		try {
			eduTourScheduleOnce.scheduleProcess();
		} catch (Throwable e) {
			log.info("<<-----eduTourInvest failure ----->>");
			e.printStackTrace();
		}
		log.info("<<-----eduTourInvest end ----->>");
	}
}

