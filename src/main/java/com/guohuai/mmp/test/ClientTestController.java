package com.guohuai.mmp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.duration.fact.income.schedule.IncomeScheduleService;
import com.guohuai.ams.product.ProductSchedService;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.product.order.salePosition.ProductSalePositionApplyResp;
import com.guohuai.ams.product.order.salePosition.ProductSalePositionService;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.investor.baseaccount.log.TaskCouponLogService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.tradeorder.InvestorInvestTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorRepayInterestTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderBatchPayService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.platform.accment.AccResendService;
import com.guohuai.mmp.platform.accment.AccSyncService;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckService;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayResendService;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.holdapart.snapshot.SnapshotService;
import com.guohuai.mmp.publisher.investor.InterestDistributionService;
import com.guohuai.mmp.publisher.investor.InterestTnRaise;
import com.guohuai.mmp.publisher.product.agreement.ProductAgreementPDFService;
import com.guohuai.mmp.publisher.product.agreement.ProductAgreementService;
import com.guohuai.mmp.publisher.product.rewardincomepractice.PracticeService;
import com.guohuai.mmp.schedule.OverdueTimesService;
import com.guohuai.mmp.schedule.ResetMonthService;
import com.guohuai.mmp.schedule.ResetTodayService;
import com.guohuai.mmp.schedule.YsService;
import com.guohuai.mmp.serialtask.SerialTaskService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "test - api")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/mimosa/client/test", produces = "application/json")
public class ClientTestController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(ClientTestController.class);

	@Autowired
	PublisherOffsetService publisherOffsetService;
	@Autowired
	PublisherHoldService publisherHoldService;
	@Autowired
	PracticeService practiceService;
	@Autowired
	SnapshotService snapshotService;
	@Autowired
	ProductSchedService productSchedService;
	@Autowired
	ProductAgreementService productAgreementService;
	@Autowired
	InvestorStatisticsService investorStatisticsService;
	@Autowired
	InvestorRepayInterestTradeOrderService investorRepayInterestTradeOrderService;
	@Autowired
	InvestorInvestTradeOrderService investorInvestTradeOrderService;
	@Autowired
	OverdueTimesService overdueTimesService;
	@Autowired
	InterestTnRaise interestTnRaise;
	@Autowired
	SerialTaskService serialTaskEntityService;
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private AccResendService accResendService;
	@Autowired
	private AccSyncService accSyncService;
	@Autowired
	private PayResendService payResendService;
	@Autowired
	private TaskCouponLogService taskCouponLogService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private ResetTodayService resetTodayService;
	@Autowired
	private ResetMonthService resetMonthService;
	@Autowired
	private ProductAgreementPDFService productAgreementPDFService;
	@Autowired
	private ProductService productService;
	@Autowired
	private YsService ysService;
	@Autowired
	private IncomeScheduleService incomeScheduleService;
	@Autowired
	private InterestDistributionService interestDistributionService;
	@Autowired
	private PlatformFinanceCheckService platformFinanceCheckService;
	@Autowired
	private InvestorTradeOrderBatchPayService investorTradeOrderBatchPayService;
	
	@Autowired
	private ProductSalePositionService salePositionService;

	@RequestMapping(value = "snapshot", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> snapshot() {
		BaseResp rep = new BaseResp();
		logger.info("<<-----开始计息份额快照----->>");
		try {
			this.snapshotService.snapshot();
		} catch (Throwable e) {
			logger.error("<<-----失败计息份额快照----->>");
			e.printStackTrace();
		}
		logger.info("<<-----成功计息份额快照----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "unlockredeem", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> unlockRedeem() {
		BaseResp rep = new BaseResp();
		logger.info("<<-----开始解锁赎回锁定份额----->>");
		try {
			this.publisherHoldService.unlockRedeem();
		} catch (Throwable e) {
			logger.error("<<-----失败解锁赎回锁定份额----->>");
			e.printStackTrace();
		}
		logger.info("<<-----成功解锁赎回锁定份额----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "unlockAccrual", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> unlockAccrual() {
		BaseResp rep = new BaseResp();
		logger.info("<<-----开始解锁计息锁定份额----->>");
		try {
			this.publisherHoldService.unlockAccrual();
		} catch (Throwable e) {
			logger.error("<<-----失败解锁计息锁定份额----->>");
			e.printStackTrace();
		}
		logger.info("<<-----成功解锁计息锁定份额----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "practice", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> practice() {
		BaseResp rep = new BaseResp();
		logger.info("<<-----奖励收益试算 start----->>");
		try {
			practiceService.practice();
		} catch (Throwable e) {
			logger.error("<<-----奖励收益试算 fail----->>");
			e.printStackTrace();
		}
		logger.info("<<-----计息success----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "resetToday", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> resetToday() {
		BaseResp rep = new BaseResp();
		logger.info("<<-----投资者今日统计数据重置 start----->>");
		try {
			this.resetTodayService.resetToday();
		} catch (Throwable e) {
			logger.error("<<-----投资者今日统计数据重置 failed----->>");
			e.printStackTrace();
		}
		logger.info("<<-----投资者今日统计数据重置 success----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "resetMonth", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> resetMonth() {
		BaseResp rep = new BaseResp();
		logger.info("<<-----投资者提现次数统计数据重置 start----->>");
		try {
			this.resetMonthService.resetMonth();
		} catch (Throwable e) {
			logger.error("<<-----投资者提现次数统计数据重置 failed----->>");
			e.printStackTrace();
		}
		logger.info("<<-----投资者提现次数统计数据重置 success----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "makeContract", notes = "生成合同 ")
	@RequestMapping(value = "makeContract", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> makeContract() {
		BaseResp rep = new BaseResp();
		logger.info("<<-----create html start----->>");
		try {
			this.productAgreementService.createHtml();
		} catch (Throwable e) {
			logger.error("<<-----create html fail----->>");
			e.printStackTrace();
		}
		logger.info("<<-----pdf success----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "makeContract for one product", notes = "生成合同 ")
	@RequestMapping(value = "makeProductContract", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> makeProductContract(@RequestParam String productOid) {
		BaseResp rep = new BaseResp();
		logger.info("<<-----create html start----->>");
		try {
			// this.productAgreementService.processHTML4Product(this.productService.findOne(productOid));
			this.productAgreementService.processJJCHTML4Product(this.productService.findOne(productOid));
		} catch (Throwable e) {
			logger.error("<<-----create html fail----->>");
			e.printStackTrace();
		}
		logger.info("<<-----pdf success----->>");
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "上传PDF", notes = "上传pdf ")
	@RequestMapping(value = "uploadPDF", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> uploadPDF() {
		BaseResp rep = new BaseResp();

		try {
			productAgreementPDFService.uploadPDFLog();
		} catch (Throwable e) {
			logger.error("<<----- fail----->>");
			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	//

	@RequestMapping(value = "qieri", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> qieri() {
		BaseResp rep = new BaseResp();

		try {
			this.publisherOffsetService.createAllNew();
		} catch (Throwable e) {
			logger.error("<<----- fail----->>");
			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	// @RequestMapping(value = "bankRun", method = { RequestMethod.GET,
	// RequestMethod.POST })
	// @ResponseBody
	// public ResponseEntity<BaseResp> bankRun() {
	// BaseResp rep = new BaseResp();
	// try {
	// //创建定时器
	// this.publisherOffsetService.createBankNew();
	// //导入数据
	// //this.platformFinanceCompareDataService.synCompareData("2017-02-12");
	// //对账
	// //this.platformFinanceCheckService.checkOrderDo("4028813c5a83d27d015a83d4dff10017",
	// "2017-02-12", "wr73242fsf");
	// } catch (Throwable e) {
	// logger.error("<<----- fail----->>");
	// e.printStackTrace();
	// }
	//
	// return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	// }

	@RequestMapping(value = "interest", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> interestTn() {
		BaseResp rep = new BaseResp();

		try {
			this.interestTnRaise.interestTnRaise();
		} catch (Throwable e) {
			logger.error("<<----- fail----->>");
			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "calcSerFee", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> calcSerFee() {
		BaseResp rep = new BaseResp();

		try {
			// this.snapshotService.calcSerFee();
		} catch (Throwable e) {
			logger.error("<<----- fail----->>");
			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	};

	@RequestMapping(value = "notstartraiseToRaisingOrRaised", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> notstartraiseToRaisingOrRaised() {
		BaseResp rep = new BaseResp();

		logger.info("<<-----产品状态变化----->>");
		try {
			this.productSchedService.notstartraiseToRaisingOrRaised();
		} catch (Throwable e) {
			logger.error("<<-----产品状态变化 fail----->>");
			e.printStackTrace();
		}
		logger.info("<<-----产品状态变化 success----->>");

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "overdueTimes", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> overdueTimes() {
		BaseResp rep = new BaseResp();

		try {
			this.overdueTimesService.overdueTimes();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "executeTask", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> executeTask() {
		BaseResp rep = new BaseResp();
		try {
			this.serialTaskEntityService.executeTask();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "pinganRedeem", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<Boolean> pinganRedeem(@RequestParam String orderCode, @RequestParam String returnCode) {

		boolean flag = false;
		try {

			OrderNotifyReq orderResponse = new OrderNotifyReq();
			// orderResponse.setIPayNo(orderCode);
			orderResponse.setOrderCode(orderCode);
			orderResponse.setReturnCode(returnCode);
			flag = this.paymentServiceImpl.tradeCallback(orderResponse);
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<Boolean>(flag, HttpStatus.OK);
	}

	@RequestMapping(value = "accResendService", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> accResendService() {
		BaseResp rep = new BaseResp();
		try {
			accResendService.resend();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "payResendService", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> payResendService() {
		BaseResp rep = new BaseResp();
		try {
			payResendService.resend();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "accSyncService", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> accSyncService() {
		BaseResp rep = new BaseResp();
		try {
			accSyncService.test();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "writerOffOrder", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> writerOffOrder(@RequestParam String orderCode) {
		BaseResp rep = new BaseResp();
		try {
			// WriterOffOrderRequest req = new WriterOffOrderRequest();
			// req.setOriginalRedeemOrderCode(orderCode);
			// this.paymentServiceImpl.writerOffOrder(req);
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	/**
	 * 可售份额排期发放;
	 */
	@RequestMapping(value = "scheduleSendProductMaxSaleVolume", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> scheduleSendProductMaxSaleVolume() {
		BaseResp rep = new BaseResp();
		try {
			productSchedService.scheduleSendProductMaxSaleVolume();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	/**
	 * 体验金到期自动赎
	 */
	@RequestMapping(value = "flatExpGold", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> flatExpGold() {
		BaseResp rep = new BaseResp();
		try {
			investorTradeOrderService.flatExpGold();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "taskUseCoupon", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> taskUseCoupon() {
		BaseResp rep = new BaseResp();
		try {
			taskCouponLogService.taskUseCoupon();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "ys", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> ys() {
		BaseResp rep = new BaseResp();
		try {
			this.ysService.ys();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "incomeScheduleDo", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> incomeScheduleDo() {
		BaseResp rep = new BaseResp();
		try {
			this.incomeScheduleService.incomeScheduleDo();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "noticeScheduleDo", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> noticeScheduleDo() {
		BaseResp rep = new BaseResp();
		try {
			this.incomeScheduleService.noticeScheduleDo();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "getLatestTen", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> getLatestTen() {
		BaseResp rep = new BaseResp();
		try {
			// this.productService.getLatestTen();
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "confirmDo", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> confirmDo(@RequestParam String offsetOid, @RequestParam String taskOid) {
		BaseResp rep = new BaseResp();
		try {
			this.publisherOffsetService.confirmDo(offsetOid, taskOid);
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "interestt0", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> interest(@RequestParam String incomeOid, @RequestParam String productOid) {
		BaseResp rep = new BaseResp();
		try {
			this.interestDistributionService.distributeInterestByProduct(incomeOid, productOid);
		} catch (Throwable e) {

			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "createCheckOrderBatch", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> createCheckOrderBatch() {
		BaseResp rep = new BaseResp();
		try {
			this.platformFinanceCheckService.createCheckOrderBatch();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "batchpay", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> batchpay() {
		BaseResp rep = new BaseResp();
		try {
			this.investorTradeOrderBatchPayService.batchPayDo();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "创建html", notes = "创建html ")
	@RequestMapping(value = "createHtml", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> createHtml() {
		BaseResp rep = new BaseResp();

		try {
			productAgreementService.createHtmlLog();
		} catch (Throwable e) {
			logger.error("<<----- fail----->>");
			e.printStackTrace();
		}

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 是否已经有申请过的
	 * 
	 * @param productOid
	 * @return
	 */
	@ApiOperation(value = "查找findSalePositionApply", notes = "查找findSalePositionApply ")
	@RequestMapping(value = "/findSalePositionApply", name="是否已经有申请过的", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<ProductSalePositionApplyResp> findSalePositionApply(
			@RequestParam(required = true) String productOid) {
		ProductSalePositionApplyResp psrr = salePositionService.findSalePositionApply(productOid);
		return new ResponseEntity<ProductSalePositionApplyResp>(psrr, HttpStatus.OK);
	}

}
