package com.guohuai.mmp.investor.baseaccount.statistics;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.cache.entity.HoldCacheEntity;
import com.guohuai.cache.entity.InvestorBaseAccountCacheEntity;
import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheInvestorService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.sonaccount.SonAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanFormVO;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProfitListForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanBaseService;
import com.guohuai.mmp.publisher.hold.MyHoldT0QueryRep;
import com.guohuai.mmp.publisher.hold.MyHoldTnQueryRep;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;

@Service
@Transactional
public class InvestorStatisticsService {
	
	@Autowired
	private InvestorStatisticsDao investorStatisticsDao;
	@Autowired
	private CacheHoldService cacheHoldService;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private CacheInvestorService cacheInvestorService;
	@Autowired
	private  PublisherHoldService publisherHoldService;
	@Autowired
	private PlanBaseService planService;
	@Autowired
	private SonAccountService sonAccountService;
	
	@Autowired
	private InvestorBaseAccountService  investorBaseAccountService;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	
	/**
	 * 充值 
	 * 更新<<投资人-基本账户-统计>>.<<累计充值总额>><<累计充值次数>><<当日充值次数>> 
	 * @param {@link InvestorBankOrderEntity}
	 * @return int
	 */
	public int updateStatistics4Deposit(InvestorBankOrderEntity bankOrder) {
		return investorStatisticsDao.updateStatistics4Deposit(bankOrder.getInvestorBaseAccount(), bankOrder.getOrderAmount());
	}
	
	public int updateStatistics4DepositOk2Fail(InvestorBankOrderEntity bankOrder) {
		return investorStatisticsDao.updateStatistics4DepositOk2Fail(bankOrder.getInvestorBaseAccount(), bankOrder.getOrderAmount());
	}
	/**
	 * 投资人提现回调
	 * 更新<<投资人-基本账户-统计>>.<<累计提现总额>><<累计提现次数>><<当日提现次数>><<月提现次数>>
	 */
	public int updateStatistics4Withdraw(InvestorBankOrderEntity bankOrder) {
		return investorStatisticsDao.updateStatistics4Withdraw(bankOrder.getInvestorBaseAccount(), bankOrder.getOrderAmount());
	}
	
	public int updateStatistics4WithdrawOK2Fail(InvestorBankOrderEntity bankOrder) {
		return investorStatisticsDao.updateStatistics4WithdrawOK2Fail(bankOrder.getInvestorBaseAccount(), bankOrder.getOrderAmount());
	}
	
	public int increaseMonthWithdrawCount(InvestorBankOrderEntity bankOrder) {
		return investorStatisticsDao.increaseMonthWithdrawCount(bankOrder.getInvestorBaseAccount());
	}
	
	public int decreaseMonthWithdrawCount(InvestorBankOrderEntity bankOrder) {
		return investorStatisticsDao.decreaseMonthWithdrawCount(bankOrder.getInvestorBaseAccount());
		
	}
	
	public InvestorStatisticsEntity findByInvestorBaseAccount(InvestorBaseAccountEntity baseAccount) {
		InvestorStatisticsEntity entity = investorStatisticsDao.findByInvestorBaseAccount(baseAccount);
		if (null == entity) {
			// error.define[30058]=投资人统计账户不存在(CODE:30058)
			throw new AMPException(30058);
		}
		return entity;
	}
	
	public InvestorStatisticsEntity getByInvestorBaseAccount(InvestorBaseAccountEntity baseAccount) {	
		return investorStatisticsDao.findByInvestorBaseAccount(baseAccount);
	}

	/**
	 * 新增
	 * @param en
	 * @return
	 */
	public InvestorStatisticsEntity saveEntity(InvestorStatisticsEntity entity){
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}
	
	private InvestorStatisticsEntity updateEntity(InvestorStatisticsEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.investorStatisticsDao.save(entity);
	}

	/**
	 * 删除投资人统计表
	 * @param baseAccount
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void delInvestorStatistics(InvestorBaseAccountEntity baseAccount) {
		InvestorStatisticsEntity statistisc = this.getByInvestorBaseAccount(baseAccount);
		if (null != statistisc) {
			this.investorStatisticsDao.delete(statistisc);
		}
	}
	
	/**
	 * 我的
	 */
	public MyHomeQueryRep myHome(String investorOid) {
		MyHomeQueryRep rep = new MyHomeQueryRep();
		
		//持有的HoldCacheEntity
		List<HoldCacheEntity> holds = this.cacheHoldService.findByInvestorOid(investorOid);
		Date incomeDate = DateUtil.getBeforeDate();
		for (HoldCacheEntity hold : holds) {

			ProductCacheEntity product = this.cacheProductService.getProductCacheEntityById(hold.getProductOid());
			if (Product.TYPE_Producttype_02.equals(product.getType())) {
				rep.setT0CapitalAmount(rep.getT0CapitalAmount().add(hold.getTotalVolume())); // 活期总资产
				rep.setCapitalAmount(rep.getCapitalAmount().add(hold.getTotalVolume())); // 总资产
				//Comment out by chenxian
//				rep.setTotalIncomeAmount(rep.getTotalIncomeAmount().add(hold.getHoldTotalIncome())); // 累计收益
				if (null != hold.getIncomeDate() && DateUtil.daysBetween(incomeDate, hold.getIncomeDate()) == 0) {
					rep.setT0YesterdayIncome(rep.getT0YesterdayIncome().add(hold.getHoldYesterdayIncome())); // 昨日收益\
				}
			} else {
				rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(hold.getTotalVolume())); // 定期总资产
				rep.setCapitalAmount(rep.getCapitalAmount().add(hold.getTotalVolume())); // 总资产
				//Comment out by chenxian
//				rep.setTotalIncomeAmount(rep.getTotalIncomeAmount().add(hold.getHoldTotalIncome())); // 累计收益
				if (null != hold.getIncomeDate() && DateUtil.daysBetween(incomeDate, hold.getIncomeDate()) == 0) {
					rep.setT0YesterdayIncome(rep.getT0YesterdayIncome().add(hold.getHoldYesterdayIncome())); // 昨日收益
				}
			}
		}
		
		InvestorBaseAccountCacheEntity cache = cacheInvestorService.getInvestorByInvestorOid(investorOid);
		rep.setBalance(cache.getBalance());
		rep.setWithdrawAvailableBalance(cache.getWithdrawAvailableBalance());
		rep.setApplyAvailableBalance(cache.getApplyAvailableBalance());
		rep.setRechargeFrozenBalance(cache.getRechargeFrozenBalance());
		rep.setWithdrawFrozenBalance(cache.getWithdrawFrozenBalance());
		
		rep.setCapitalAmount(rep.getBalance().add(rep.getCapitalAmount()));
		
		//获取活期产品中持有和申请中的数量
		MyHoldT0QueryRep repT0 = this.publisherHoldService.queryMyT0HoldProList(investorOid);
		rep.setHoldT0Size(repT0.getHoldingDetails().getRows().size()+repT0.getToConfirmDetails().getRows().size());
		//获得定期产品中申请和持有中的数量
		MyHoldTnQueryRep repTn = this.publisherHoldService.queryMyTnHoldProList(investorOid);
		rep.setHoldTnSize(repTn.getHoldingTnDetails().getRows().size()+repTn.getToConfirmTnDetails().getRows().size());
		//获取我的心愿计划里的进行中和停止的数量
		int a = getPlanMount(investorOid);
		rep.setHoldPlanSize(a);
		
		
		/** 心愿计划投资成本 */
		PlanProfitListForm planInvest = this.planService.satisticsPlanList(investorOid);
		rep.setPlanInvestAmount(new BigDecimal(planInvest.getTotalDepositAmount()));
			
		/** 心愿计划的总资产  */
		rep.setPlancapitalAmount(new BigDecimal(planInvest.getTotalExpectedAmount()));
		
		//心愿计划和非心愿计划的总资产总和
		rep.setAllCapitalAmount(rep.getCapitalAmount().add(rep.getPlancapitalAmount()));
		//心愿计划和非心愿计划的总收益
		//Comment out by chenxian
//		rep.setAllTotalIncomeAmount(rep.getTotalIncomeAmount().add(rep.getPlanTotalIncomeAmount()));
		rep.setAllTotalIncomeAmount(getStatisticsIncome(investorOid));
		
		return rep;
	}
	
	/**
	 * 我的资产
	 */
	public MyCaptialQueryRep myCaptial(String investorOid) {
		
		MyCaptialQueryRep rep = new MyCaptialQueryRep();
		
		List<HoldCacheEntity> holds = cacheHoldService.findByInvestorOid(investorOid);
		
		List<CapitalDetail> t0CapitalDetails = new ArrayList<CapitalDetail>();
		List<CapitalDetail> tnCapitalDetails = new ArrayList<CapitalDetail>();
		List<CapitalDetail> applyCapitalDetails = new ArrayList<CapitalDetail>();
		rep.setT0CapitalDetails(t0CapitalDetails);
		rep.setTnCapitalDetails(tnCapitalDetails);
		rep.setApplyCapitalDetails(applyCapitalDetails);
		
		InvestorBaseAccountCacheEntity cache = this.cacheInvestorService.getInvestorByInvestorOid(investorOid);
		rep.setBalance(cache.getBalance());
		rep.setWithdrawAvailableBalance(cache.getWithdrawAvailableBalance());
		rep.setApplyAvailableBalance(cache.getApplyAvailableBalance());
		rep.setRechargeFrozenBalance(cache.getRechargeFrozenBalance());
		rep.setWithdrawFrozenBalance(cache.getWithdrawFrozenBalance());
		
		
		rep.setCapitalAmount(rep.getBalance()); // 资产总额
		for (HoldCacheEntity hold : holds) {
			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closed.equals(hold.getHoldStatus()) ||
					PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunded.equals(hold.getHoldStatus())) {
				continue;
			}
			ProductCacheEntity product = cacheProductService.getProductCacheEntityById(hold.getProductOid());
			rep.setCapitalAmount(rep.getCapitalAmount().add(hold.getTotalVolume()));
			if(BigDecimal.ZERO.compareTo(hold.getHoldVolume()) < 0){
				if (Product.TYPE_Producttype_02.equals(product.getType())) {
					CapitalDetail detail = new CapitalDetail();
					detail.setProductOid(hold.getProductOid());
					detail.setProductName(product.getName());
//					detail.setAmount(hold.getHoldVolume().subtract(hold.getExpGoldVolume()));
					detail.setAmount(hold.getHoldVolume());
					t0CapitalDetails.add(detail);
					rep.setT0CapitalAmount(rep.getT0CapitalAmount().add(hold.getHoldVolume())); // 活期总资产
					rep.setExperienceCouponAmount(rep.getExperienceCouponAmount().add(hold.getExpGoldVolume())); // 体验金总资产
				}
				if (Product.TYPE_Producttype_01.equals(product.getType())) {
					CapitalDetail detail = new CapitalDetail();
					detail.setProductOid(hold.getProductOid());
					detail.setProductName(product.getName());
					detail.setAmount(hold.getHoldVolume().subtract(hold.getExpGoldVolume()));
					tnCapitalDetails.add(detail);
					rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(hold.getHoldVolume())); // 定期总资产
					rep.setExperienceCouponAmount(rep.getExperienceCouponAmount().add(hold.getExpGoldVolume())); // 体验金总资产
				}
			}
			if (BigDecimal.ZERO.compareTo(hold.getToConfirmInvestVolume()) != 0) {
				CapitalDetail applyDetail = new CapitalDetail();
				applyDetail.setProductOid(hold.getProductOid());
				applyDetail.setProductName(product.getName());
				applyDetail.setAmount(hold.getToConfirmInvestVolume());
				applyCapitalDetails.add(applyDetail);
				rep.setApplyAmt(rep.getApplyAmt().add(hold.getToConfirmInvestVolume()));
			}
		}
		/**  增加代码，增加心愿计划的资产和收益  */
		/** 心愿计划投资成本 */
		PlanProfitListForm planInvest = this.planService.satisticsPlanList(investorOid);
		rep.setPlanInvestAmount(new BigDecimal(planInvest.getTotalDepositAmount()));
		
		
		
		/** 心愿计划的总资产  */
		//rep.setPlancapitalAmount(rep.getPlanInvestAmount().add(rep.getPlanTotalIncomeAmount()));
		rep.setPlancapitalAmount(new BigDecimal(planInvest.getTotalExpectedAmount()));
		
		/**  心愿计划的收益   */
		//rep.setPlanTotalIncomeAmount(new BigDecimal(planInvest.getTotalholdAmountIncome()).subtract(new BigDecimal(planInvest.getTotalExpectedAmount())));
		
		//心愿计划和非心愿计划的总资产总和
		rep.setAllCapitalAmount(rep.getCapitalAmount().add(rep.getPlancapitalAmount()));
		
		return rep;
	}
	
	public boolean isFree(InvestorBaseAccountEntity baseAccount, int freeTimes) {
		int i = this.investorStatisticsDao.isFree(baseAccount, freeTimes);
		if (i < 1) {
			return false;
		}
		return true;
	}
	
	public int interestStatistics(InvestorBaseAccountEntity investorBaseAccount, BigDecimal holdIncomeAmount, BigDecimal holdLockIncomeAmount, Date incomeDate) {
		int i = this.investorStatisticsDao.interestStatistics(investorBaseAccount, holdIncomeAmount, holdLockIncomeAmount, incomeDate);
		return i;
		
	}
	
	public int interestStatisticsTn(InvestorBaseAccountEntity investorBaseAccount, BigDecimal holdIncomeAmount, BigDecimal holdLockIncomeAmount, Date incomeDate) {
		int i = this.investorStatisticsDao.interestStatisticsTn(investorBaseAccount, holdIncomeAmount, holdLockIncomeAmount, incomeDate);
		return i;
		
	}
	
	public int  resetToday() {
		int i = this.investorStatisticsDao.resetToday();
		return i;
	}
	
	/**
	 * 更新<<投资人-基本账户-统计>>.<<累计赎回总额>><<累计赎回次数>><<当日赎回次数>> <<活期资产总额>>
	 */
	public int redeemStatistics(InvestorTradeOrderEntity order) {
		int i = this.investorStatisticsDao.updateStatistics4T0Redeem(order.getInvestorBaseAccount(), order.getOrderAmount());
		return i;
	}
	
	public int repayStatistics(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount) {
		int i= this.investorStatisticsDao.repayStatistics(baseAccount, orderAmount);
		return i;
	}
	
	
	public int investStatistics(InvestorTradeOrderEntity tradeOrder, InvestorBaseAccountEntity baseAccount) {
		int i;
		if (Product.TYPE_Producttype_02.equals(tradeOrder.getProduct().getType().getOid())) {
			i = investorStatisticsDao.updateStatistics4T0Invest(baseAccount, tradeOrder.getOrderAmount());
		} else {
			i = investorStatisticsDao.updateStatistics4TnInvest(baseAccount, tradeOrder.getOrderAmount());
		}
		return i; 
	}

	public int resetMonth() {
		return this.investorStatisticsDao.resetMonth();
		
	}
	
	/**
	 * 获取我的计划中所有进行中的个数
	 * 
	 * */

	public int getPlanMount(String investorOid){
		List<PlanFormVO> list =  this.planService.getOwnerPlanList(investorOid);
		int mount = 0;
		
		for(PlanFormVO p:list){
			if(!p.getStatus().equals(PlanStatus.COMPLETE.getCode())){
				mount++;
			}
		}
		return mount;
	}
	
	/**
	 * 
	 * @param investorOid
	 * @return
	 */
	private BigDecimal getStatisticsIncome(String investorOid) {
		InvestorBaseAccountEntity baseAccount = investorBaseAccountService.findOne(investorOid);
		InvestorStatisticsEntity st = investorStatisticsService.findByInvestorBaseAccount(baseAccount);
		return st.getTotalIncomeAmount().add(st.getWishplanIncome());
	}
	
	/**
	 * 我的资产
	 */
	public MyCaptialQueryRep myCaptialByType(String investorOid, String productType) {
		
		MyCaptialQueryRep rep = new MyCaptialQueryRep();
		
		List<HoldCacheEntity> holds = cacheHoldService.findByInvestorOid(investorOid);
		
		List<CapitalDetail> t0CapitalDetails = new ArrayList<CapitalDetail>();
		List<CapitalDetail> tnCapitalDetails = new ArrayList<CapitalDetail>();
		List<CapitalDetail> applyCapitalDetails = new ArrayList<CapitalDetail>();
		rep.setT0CapitalDetails(t0CapitalDetails);
		rep.setTnCapitalDetails(tnCapitalDetails);
		rep.setApplyCapitalDetails(applyCapitalDetails);
		
		InvestorBaseAccountCacheEntity cache = this.cacheInvestorService.getInvestorByInvestorOid(investorOid);
		rep.setBalance(cache.getBalance());
		rep.setWithdrawAvailableBalance(cache.getWithdrawAvailableBalance());
		rep.setApplyAvailableBalance(cache.getApplyAvailableBalance());
		rep.setRechargeFrozenBalance(cache.getRechargeFrozenBalance());
		rep.setWithdrawFrozenBalance(cache.getWithdrawFrozenBalance());
		
		
		rep.setCapitalAmount(rep.getBalance()); // 资产总额
		for (HoldCacheEntity hold : holds) {
			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closed.equals(hold.getHoldStatus()) ||
					PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunded.equals(hold.getHoldStatus())) {
				continue;
			}
			ProductCacheEntity product = cacheProductService.getProductCacheEntityById(hold.getProductOid());
			rep.setCapitalAmount(rep.getCapitalAmount().add(hold.getTotalVolume()));
			if (Product.TYPE_Producttype_02.equals(product.getType())) {
				CapitalDetail detail = new CapitalDetail();
				detail.setProductName(product.getName());
//				detail.setAmount(hold.getHoldVolume().subtract(hold.getExpGoldVolume()));
				detail.setAmount(hold.getHoldVolume());
				t0CapitalDetails.add(detail);
				rep.setT0CapitalAmount(rep.getT0CapitalAmount().add(hold.getHoldVolume())); // 活期总资产
				rep.setExperienceCouponAmount(rep.getExperienceCouponAmount().add(hold.getExpGoldVolume())); // 体验金总资产
			}
			if (Product.TYPE_Producttype_01.equals(product.getType())) {
				CapitalDetail detail = new CapitalDetail();
				detail.setProductName(product.getName());
				detail.setAmount(hold.getHoldVolume().subtract(hold.getExpGoldVolume()));
				tnCapitalDetails.add(detail);
				rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(hold.getHoldVolume())); // 定期总资产
				rep.setExperienceCouponAmount(rep.getExperienceCouponAmount().add(hold.getExpGoldVolume())); // 体验金总资产
			}
			if (BigDecimal.ZERO.compareTo(hold.getToConfirmInvestVolume()) != 0) {
				CapitalDetail applyDetail = new CapitalDetail();
				applyDetail.setProductName(product.getName());
				applyDetail.setAmount(hold.getToConfirmInvestVolume());
				applyCapitalDetails.add(applyDetail);
				rep.setApplyAmt(rep.getApplyAmt().add(hold.getToConfirmInvestVolume()));
			}
		}
		/**  增加代码，增加心愿计划的资产和收益  */
		/** 心愿计划投资成本 */
		PlanProfitListForm planInvest = this.planService.satisticsPlanList(investorOid);
		rep.setPlanInvestAmount(new BigDecimal(planInvest.getTotalDepositAmount()));
		
		
		
		/** 心愿计划的总资产  */
		//rep.setPlancapitalAmount(rep.getPlanInvestAmount().add(rep.getPlanTotalIncomeAmount()));
		rep.setPlancapitalAmount(new BigDecimal(planInvest.getTotalExpectedAmount()));
		
		/**  心愿计划的收益   */
		//rep.setPlanTotalIncomeAmount(new BigDecimal(planInvest.getTotalholdAmountIncome()).subtract(new BigDecimal(planInvest.getTotalExpectedAmount())));
		
		//心愿计划和非心愿计划的总资产总和
		rep.setAllCapitalAmount(rep.getCapitalAmount().add(rep.getPlancapitalAmount()));
		
		return rep;
	}
	
}
