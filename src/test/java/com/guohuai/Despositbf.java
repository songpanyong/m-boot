package com.guohuai;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.guohuai.mmp.investor.tradeorder.InvestorInvestTradeOrderExtService;
import com.guohuai.mmp.investor.tradeorder.InvestorRedeemTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderDao;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordDao;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ApplicationBootstrap.class)
public class Despositbf {
	
	@Autowired
	private InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;
	
	@Autowired
	private InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	
	@Autowired
	private EbaoquanRecordService baoquanService;
	
	@Autowired
	private EbaoquanRecordDao recordDao;
	
	@Autowired
	private PlanInvestDao planInvestDao;
	/*
	@Autowired
	InvestorBankOrderExtService investorBankOrderExtService;

	@Test
	public void test() {
		
		DepositBankOrderbfReq req = new DepositBankOrderbfReq();
		BigDecimal b = new BigDecimal("200");
		req.setOrderAmount(b);
		String investorOid="ffd3459e994c420d988ea32032e6d6d7";
		System.out.println(req);
		BankOrderRep rep = this.investorBankOrderExtService.depositbf(req, investorOid);
		System.out.println(rep);
		
	}
	
	@Autowired
	private ProductAssetService paService;
	
	@Autowired
	private JunziqianService junziqian;
	
	@Autowired
	private ToContractPdf toPdf;
	
	@Autowired
	private EbaoquanRecordDao recordDao;
	@Autowired
	private Html2PdfService html2PdfService;
	
	@Autowired
	private PublisherOffsetServiceRequiresNew requiresNewService;
	
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	
	
	@Autowired
	private PlanMonthScheduleService scheduleService;
	
	@Autowired
	private PlanInvestDao planInvestDao;
	
	@Autowired
	private PlanProductRedeemService redeemService;
	
	@Autowired
	private PlanProductDao planDao;
	
	@Autowired
	private OnePlanScheduleService oneScheduleService;
	
	@Autowired
	private  InterestDistributionService interestDistributionService;
	
	@Autowired
	private	IncomeBalanceService incomeBalanceService;

	@Autowired
	private PlanMonthDao planMonthDao;
	
	@Autowired
	private PlanBaseService planBaseService;
	*/
//	@Test
//	public void test(){
//		PlanInvestEntity planInvest = planInvestDao.findByOid("d408d8088989427f88f3f0649d02454a");
//		
//		try {
//			scheduleService.overdueMonthDeposit(planInvest);
//		} catch (BaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	/*
	@Test
	public void test(){
		PlanInvestEntity pp = planInvestDao.findByOid("ca9d003d9c6e486dbf823023df663eff");
		PlanProductEntity pi =   planDao.findByOid("81e68c3ea2544d58909cffd4ee033242");
		redeemService.createTradeOrder(pi, pp);
	}
	*/
	/*
	@Test
	public void test() throws BaseException{
		PlanInvestEntity pp = planInvestDao.findByOid("723d2fafa6a540df96c36b1ff9060e72");
		
		oneScheduleService.processOnePlan(pp);
	}
	*/
	
	@Test
	public void test() throws BaseException {
		
		for (int i = 0; i < 5; i++) {
			EbaoquanRecord baoquan = recordDao.findByCodeId("GENREG83b6b120180312133449");
			
			PlanInvestEntity invest = planInvestDao.findOne("c0beaa61cc4148588c0868a0986241c0");
			baoquanService.generateParameters(baoquan, invest);
		}
		/*
		InvestorTradeOrderEntity orderEntity = investorTradeOrderDao.findOne("2c92808761d551250161d6770c8610a9");
		
		requiresNewService.processOneItem(orderEntity);
		*/
		
//		investorInvestTradeOrderExtService.redeemDo("246132018030200000001", "");
		
//		InvestorTradeOrderEntity orderEntity = investorTradeOrderDao.findOne("2c92808761e4b44c0161e5b9a5d70d0a");
//		
//		investorRedeemTradeOrderService.verification(orderEntity);
		
//		EbaoquanRecord baoquan =  recordDao.findByCodeId("GENREG4e33ce20180303172758");
//			
//		html2PdfService.uploadOne(baoquan);
		
//		String htmlFilePath = "E:\\_contract_0_tpl.html";
//		String pdfFilePath = "/ghorder/agreements/_contract_0_tpl.pdf";
//		
////		toPdf.htmlToPdf(htmlFilePath, pdfFilePath);
//		
//		
//		htmlFilePath = "E:\\withhold.html";
//		pdfFilePath = "/ghorder/agreements/withhold2.pdf";
//		
//		toPdf.htmlToPdf(htmlFilePath, pdfFilePath);
		/*
		String pdfFilePath = "E:\\jjcai0201.pdf";
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("contractNo", "02011816");
		map.put("amount", "1000");
		map.put("buyerName", "陈贤"); // 姓名
		
		map.put("buyerIdno", "102528199712121717"); // 证件号码
		map.put("buyerPhone", "13264391052");
		
		map.put("dtId", "02011816");
		map.put("dtContractId", "02011816");
        map.put("dtContractType", "135246");
		
		junziqian.createContractFilePreservation(pdfFilePath, map);
		
		
		for (int i = 0; i < 10; i++) {
		String incomeOid = "2ec95c9909ba4a38aa01da9fcb2fb666";
		String productOid = "94a86d790f8e4d659de56eaff814a88e";
		interestDistributionService.distributeInterestByProduct(incomeOid, productOid);
		}
		
		
		String uid = "50a8370eb59f4581a46c07134cd9bb73";
		String productType = "PRODUCTTYPE_02";
		paService.levelFirst(uid, productType);
		
		String productOid = "1bd8779eabc848c7b687b6c586bbf88e";
		BigDecimal amount = new   BigDecimal(300);
		RowsRep<AssetLevelSecond> levelSecond = paService.levelSecond(productOid, amount);
		System.out.println(levelSecond);
		*/
		
		
	}
    
//	@Test
//	public void test() throws BaseException{
		/*
		for (int i = 0; i < 10; i++) {
//			PlanInvestEntity pmd = planInvestDao.findByOid("00c986e239c9493e9070425f1d99fd2f");
//			incomeBalanceService.staticticsOnceIncome(pmd);
			int today = DateUtil.getTodayOnMonth();
			int yesterday = DateUtil.getYesterdayOnMonth();
			int thisMonth = DateUtil.getYearMonthFromDate();
			List<Integer> dueDays = new ArrayList<Integer>();
			dueDays.add(today);
			dueDays.add(yesterday);
			
			for (int j = 0; j < 30; j++) {
				dueDays.add(j);
			}
			
			Timestamp end = DateUtil.firstDayOfNextMonth();
//			List<PlanMonthEntity> list = planMonthDao.findByMonthInvestDateStatusMonth(dueDay, PlanStatus.READY.getCode(), thisMonth);
			List<PlanMonthEntity> list = planMonthDao.findMonthPlanByDateStatusEnd(dueDays, PlanStatus.READY.getCode(), thisMonth, end);
			System.out.print(list);
			list = planMonthDao.findMonthPlanByDateStatusEnd(dueDays, PlanStatus.COMPLETE.getCode(), thisMonth, end);
			
			System.out.print(list);
			
           list = planMonthDao.findMonthPlanByDateStatusEnd(dueDays, PlanStatus.REDEEMING.getCode(), thisMonth,end);
			
			System.out.print(list);
			
		}
		*/
		/*
		String oid = "8a50de85f1c648d5ab48b01e86441334";
		MonthPlanByOidRep rep = planBaseService.getMonthPlanDetail(oid);
		System.out.print(rep);
		*/
//		List<String> errorStrs =  Arrays.asList("账户余额不足(CODE:10010)", "未开通无卡支付(CODE:10011)", 
//				"渠道暂不可用(CODE:10016)", "暂不支持该银行卡(CODE:10040)", "支付失败");
//	
//		List<String> errorStrs =  Arrays.asList("未开通无卡支付(CODE:10011)", 
//				"渠道暂不可用(CODE:10016)", "暂不支持该银行卡(CODE:10040)", "支付失败");
		/*
		 * 
		 event.setReturnCode(Constant.FAIL);
                	if(resp.getResCode().equals("10010")){
                        event.setErrorDesc("账户余额不足(CODE:10010)");
                	} else if (resp.getResCode().equals("10011")){
                        event.setErrorDesc("未开通无卡支付(CODE:10011)");
                	} else if (resp.getResCode().equals("10016")){
                        event.setErrorDesc("渠道暂不可用(CODE:10016)");
                	} else if (resp.getResCode().equals("10040")){
                        event.setErrorDesc("暂不支持该银行卡(CODE:10040)");
                	}else{
                        event.setErrorDesc("支付失败");
                	}
		 */
//		for (String str : errorStrs) {
//			String planMonthOid = "7aa6eb32a7d74907aaeb6374616e82e2";
//			PlanInvestEntity pie = planInvestDao.findByOid(planMonthOid);
////			String str = "暂不支持该银行卡(CODE:10040)";
//			scheduleService.parseErrorCode(str, pie);
//		}
	
//	}
	
}
