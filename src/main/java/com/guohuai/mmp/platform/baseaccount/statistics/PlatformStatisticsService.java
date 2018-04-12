package com.guohuai.mmp.platform.baseaccount.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.platform.baseaccount.PlatformBaseAccountService;
import com.guohuai.mmp.platform.baseaccount.statistics.history.PlatformStatisticsHistoryEntity;
import com.guohuai.mmp.platform.baseaccount.statistics.history.PlatformStatisticsHistoryService;
import com.guohuai.mmp.platform.channel.statistics.PlatformChannelStatisticsEntity;
import com.guohuai.mmp.platform.channel.statistics.PlatformChannelStatisticsService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.product.statistics.PublisherProductStatisticsEntity;
import com.guohuai.mmp.publisher.product.statistics.PublisherProductStatisticsService;

@Service
@Transactional
public class PlatformStatisticsService {

	@Autowired
	private PlatformStatisticsDao platformStatisticsDao;
	@Autowired
	private PlatformBaseAccountService platformBaseAccountService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private PlatformChannelStatisticsService platformChannelStatisticsService;
	@Autowired
	private PublisherProductStatisticsService publisherProductStatisticsService;
	@Autowired
	private PlatformStatisticsHistoryService platformStatisticsHistoryService;
	@Autowired
	private ProductService productService;

	/**
	 * 新增
	 * @param en
	 * @return
	 */
	public PlatformStatisticsEntity saveEntity(PlatformStatisticsEntity en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	public PlatformStatisticsEntity updateEntity(PlatformStatisticsEntity en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.platformStatisticsDao.save(en);
	}



	public PlatformStatisticsEntity findByPlatformBaseAccount() {
		PlatformStatisticsEntity st = this.platformStatisticsDao.findByPlatformBaseAccount(platformBaseAccountService.getPlatfromBaseAccount());
		if (null == st) {
			throw new AMPException("平台统计不存在");
		}
		return st;
	}
	
	
	/**
	 * 付息
	 */
	public int updateStatistics4TotalInterestAmount(BigDecimal income) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4TotalInterestAmount(st.getOid(), income);
	}
	
	/**
	 * 投资单份额确认:累计交易额、累计借款额
	 */
	public int updateStatistics4InvestConfirm(BigDecimal orderAmount) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4InvestConfirm(st.getOid(), orderAmount);
	}
	
	/**
	 * 赎回单份额确认:累计交易额、累计还款额
	 */
	public int updateStatistics4RedeemConfirm(BigDecimal orderAmount) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4RedeemConfirm(st.getOid(), orderAmount);
	}

	/**
	 * 投资人充值回调
	 * 更新<<平台-统计>>.<<累计交易总额>><<投资人充值总额>> 
	 */
	public int updateStatistics4InvestorDeposit(BigDecimal orderAmount) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4InvestorDeposit(st.getOid(), orderAmount);
	}

	/**
	 * 发行人充值回调
	 * 更新<<平台-统计>>.<<累计交易总额>><<发行人充值总额>>
	 * @param orderAmount
	 */
	public int updateStatistics4PublisherDeposit(BigDecimal orderAmount) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4PublisherDeposit(st.getOid(), orderAmount);
	}

	/**
	 * 投资人提现回调
	 * 更新<<平台-统计>>.<<累计交易总额>><<投资人提现总额>>
	 * @param orderAmount
	 */
	public int updateStatistics4InvestorWithdraw(BigDecimal orderAmount) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4InvestorWithdraw(st.getOid(), orderAmount);
	}
	
	public int updateStatistics4InvestorWithdrawWriteOff(BigDecimal orderAmount) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4InvestorWithdrawWriteOff(st.getOid(), orderAmount);
	}

	/**
	 * 发行人提现回调
	 * 更新<<平台-统计>>.<<累计交易总额>><<发行人提现总额>>
	 * @param orderAmount
	 * @return
	 */
	public int updateStatistics4PublisherWithdraw(BigDecimal orderAmount) {
		PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
		return this.platformStatisticsDao.updateStatistics4PublisherWithdraw(st.getOid(), orderAmount);
	}

	/**
	 * 投资人注册
	 * 增加平台注册人数
	 */
	public int increaseRegisterAmount() {
		return this.platformStatisticsDao.increaseRegisterAmount(this.findByPlatformBaseAccount().getOid());
	}
	
	/**
	 * 投资人实名认证
	 * 增加平台实名人数
	 */
	public int increaseVerifiedInvestorAmount() {
		return this.platformStatisticsDao.increaseVerifiedInvestorAmount(this.findByPlatformBaseAccount().getOid());
	}
	

	/** 平台首页查询 */
	public BaseResp qryHome() {
		
		PlatformHomeQueryRep rep = new PlatformHomeQueryRep();
		
		PlatformStatisticsEntity ps = this.findByPlatformBaseAccount();
		rep.setTotalTradeAmount(ps.getTotalTradeAmount());
		rep.setTotalLoanAmount(ps.getTotalLoanAmount()); // 累计借款总额
		rep.setTotalReturnAmount(ps.getTotalReturnAmount()); // 累计还款总额
		rep.setTotalInterestAmount(ps.getTotalInterestAmount()); // 累计付息总额
		rep.setPublisherTotalDepositAmount(ps.getPublisherTotalDepositAmount());  // 发行人充值总额
		rep.setPublisherTotalWithdrawAmount(ps.getPublisherTotalWithdrawAmount()); 
		rep.setInvestorTotalDepositAmount(ps.getInvestorTotalDepositAmount());
		rep.setInvestorTotalWithdrawAmount(ps.getInvestorTotalWithdrawAmount());
		rep.setRegisterAmount(ps.getRegisterAmount()); // 注册人数
		rep.setOverdueTimes(ps.getOverdueTimes()); // 逾期次数
		rep.setTotalCoupon(ps.getTotalCoupon());	// 代金券数量
		rep.setTotalCouponAmount(ps.getTotalCouponAmount());	// 代金券金额
		rep.setTotalRateCoupon(ps.getTotalRateCoupon());	// 加息券数量
		rep.setTotalTasteCoupon(ps.getTotalTasteCoupon());	// 体验金数量
		rep.setTotalTasteCouponAmount(ps.getTotalTasteCouponAmount());	// 体验金金额
		
		Map<String, Integer> map = this.productService.productAmount();
		rep.setRaisingNum(map.get(Product.STATE_Raising));
		rep.setRaiseendNum(map.get(Product.STATE_Raiseend));
		rep.setDurationingNum(map.get(Product.STATE_Durationing));
		rep.setDurationendNum(map.get(Product.STATE_Durationend));
		rep.setClearingNum(map.get(Product.STATE_Clearing));
		rep.setClearedNum(this.productService.closedProductAmount());
		
		rep.setPublisherAmount(ps.getPublisherAmount()); // 发行人数
		rep.setVerifiedInvestorAmount(ps.getVerifiedInvestorAmount()); // 实名认证人数
		
		
		/** 平台用户数变化 */
		List<PlatformStatisticsHistoryEntity> pshList = this.platformStatisticsHistoryService.getLatest30UserCurve();
		for (PlatformStatisticsHistoryEntity en : pshList) {
			rep.getPeopleCurve().add(new CurvePojo(en.getConfirmDate(), en.getRegisterAmount().longValue()));
		}
		
		
		// 昨日各渠道投资额排序前5位
		List<PlatformChannelStatisticsEntity> pcTopFive = this.platformChannelStatisticsService.getTopFive();
		for (PlatformChannelStatisticsEntity en : pcTopFive) {
			ChartPojo an = new ChartPojo();
			an.setXName(en.getChannel().getChannelName());
			an.setYValue(en.getTodayInvestAmount());
			rep.getChannelRank().add(an);
		}

		// 昨日产品新增投资额TOP5
		List<PublisherProductStatisticsEntity> ppTopFive = this.publisherProductStatisticsService.getTopFive();
		for (PublisherProductStatisticsEntity en : ppTopFive) {
			ChartPojo an = new ChartPojo();
			an.setXName(en.getProduct().getName());
			an.setYValue(en.getInvestAmount());
			rep.getProInvestorRank().add(an);
		}
		
		// 平台交易额占比分析
		rep.getTradeAmountAnalyse().add(new ChartPojo("投资", ps.getTotalLoanAmount()));
		rep.getTradeAmountAnalyse().add(new ChartPojo("赎回", ps.getTotalReturnAmount()));
		rep.getTradeAmountAnalyse().add(new ChartPojo("充值", ps.getInvestorTotalDepositAmount()));
		rep.getTradeAmountAnalyse().add(new ChartPojo("提现", ps.getInvestorTotalWithdrawAmount()));

		
		// 投资人质量分析
		List<Object[]> invstorList = this.publisherHoldService.analysePlatformInvestor();
		if (invstorList != null && invstorList.size() > 0) {
			for (Object[] objects : invstorList) {
				ChartPojo pojo = new ChartPojo(getLevelName(objects[0]), new BigDecimal(((BigInteger)objects[1]).intValue()));
				rep.getInvestorAnalyse().add(pojo);
				
			}
		}

		return rep;
	}

	private String getLevelName(Object level) {
		if (StringUtils.isEmpty(level)) {
			return "";
		}
		String levelStr = level.toString();
		switch (levelStr) {
		case "1":
			return "5万以下";
		case "2":
			return "5-10万";
		case "3":
			return "10-20万";
		case "4":
			return "20万以上";
		default:
			return "";
		}
	}
	
	/**
	 * 逾期次数
	 */
	public void increaseOverdueTimes(int overdueTimes) {
		if (0 == overdueTimes) {
			
		} else {
			PlatformStatisticsEntity st = this.findByPlatformBaseAccount();
			this.platformStatisticsDao.increaseOverdueTimes(st.getOid(), overdueTimes);
		}
		
	}

	/**
	 * 发行人数
	 */
	public int increasePublisherAmount() {
		return this.platformStatisticsDao.increasePublisherAmount(findByPlatformBaseAccount().getOid());
	}
	

	public List<Object[]> getPlatformStatisticsByBatch(String lastOid) {
		return this.platformStatisticsDao.getPlatformStatisticsByBatch(lastOid);
	}
	
}
