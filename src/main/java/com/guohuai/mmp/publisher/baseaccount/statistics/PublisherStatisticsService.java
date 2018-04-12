package com.guohuai.mmp.publisher.baseaccount.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDecimalFormat;
import com.guohuai.ams.product.ProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.publisher.bankorder.PublisherBankOrderEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.product.statistics.PublisherProductStatisticsEntity;
import com.guohuai.mmp.publisher.product.statistics.PublisherProductStatisticsService;

@Service
@Transactional
public class PublisherStatisticsService {
	
	@Autowired
	PublisherStatisticsDao publisherStatisticsDao;
	@Autowired
	ProductService productService;
	@Autowired
	PublisherHoldService publisherHoldService;
	@Autowired
	PublisherProductStatisticsService publisherProductStatisticsService;
	@Autowired
	PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	PublisherLoginAccService publisherLoginAccService;
	
	private Logger logger = LoggerFactory.getLogger(PublisherStatisticsService.class);

	/**
	 * 充值
	 */
	public int updateStatistics4Deposit(PublisherBankOrderEntity bankOrder) {
		
		return publisherStatisticsDao.updateStatistics4Deposit(bankOrder.getPublisherBaseAccount(), bankOrder.getOrderAmount());
	}
	
	/**
	 * 提现
	 */
	public int updateStatistics4Withdraw(PublisherBankOrderEntity bankOrder) {
		return publisherStatisticsDao.updateStatistics4Withdraw(bankOrder.getPublisherBaseAccount(), bankOrder.getOrderAmount());
	}
	
	/**
	 * 还款
	 */
	public int increaseTotalReturnAmount(InvestorTradeOrderEntity orderEntity) {
		return publisherStatisticsDao.increaseTotalReturnAmount(orderEntity.getPublisherBaseAccount(), orderEntity.getOrderAmount());
	}
	
	/**
	 * 借款
	 */
	public int increaseTotalLoanAmount(InvestorTradeOrderEntity orderEntity) {
//		if (Product.TYPE_Producttype_02.equals(orderEntity.getProduct().getType().getOid())) {
			return publisherStatisticsDao.increaseTotalLoanAmount(orderEntity.getPublisherBaseAccount(), orderEntity.getOrderAmount());
//		} else {
//			return publisherStatisticsDao.increaseTotalLoanTnAmount(orderEntity.getPublisherBaseAccount(), orderEntity.getOrderAmount());
//		}
	}
	
	public PublisherStatisticsEntity findByPublisherBaseAccount(PublisherBaseAccountEntity baseAccount) {
		PublisherStatisticsEntity entity = publisherStatisticsDao.findByPublisherBaseAccount(baseAccount);
		if (null == entity) {
			throw new AMPException("发行人统计不存在");
		}
		return entity;
	}
	
	/**
	 * 重置相关今日统计数据
	 */
	public int resetToday() {
		int i = this.publisherStatisticsDao.resetToday();
		return i;
	}
	
//	/**
//	 * 定期产品进入募集期时，增加产品发行数量
//	 * 活期产品进入存续期时
//	 */
//	public int increaseReleasedProductAmount(PublisherBaseAccountEntity baseAccount) {
//		int i = this.publisherStatisticsDao.increaseReleasedProductAmount(baseAccount);
//		return i;
//	}
//	
//	/**
//	 * 定期产品进入募集期时，增加在售产品数量
//	 * 活期产品进入存续期时
//	 */
//	public int increaseOnSaleProductAmount(PublisherBaseAccountEntity baseAccount) {
//		int i = this.publisherStatisticsDao.increaseOnSaleProductAmount(baseAccount);
//		return i;
//	}
//	
//	/**
//	 * 定期产品存续期结束之后，增加待结算产品数量
//	 * 活期产品发起清盘操作
//	 */
//	public int increaseToCloseProductAmount(PublisherBaseAccountEntity baseAccount) {
//		int i = this.publisherStatisticsDao.increaseToCloseProductAmount(baseAccount);
//		return i;
//	}
//	
//	/**
//	 * 定期产品发起还本付息之后/或定期产品募集失败,增加已结算产品数量
//	 */
//	public int increaseClosedProductAmount(PublisherBaseAccountEntity baseAccount) {
//		int i = this.publisherStatisticsDao.increaseClosedProductAmount(baseAccount);
//		return i;
//	}

	/** 发行人首页 */
	public PublisherHomeQueryRep publisherHome(String loginAcc) {
		
		logger.info("loginAcc:" + loginAcc);
		
		PublisherHomeQueryRep rep = new PublisherHomeQueryRep();

		PublisherBaseAccountEntity ba = this.publisherLoginAccService.findByLoginAcc(loginAcc);
		PublisherStatisticsEntity st = this.publisherStatisticsDao.findByPublisherBaseAccount(ba);
		rep.setTotalDepositAmount(st.getTotalDepositAmount());
		rep.setTotalWithdrawAmount(st.getTotalWithdrawAmount());
		rep.setTotalLoanAmount(st.getTotalLoanAmount());
		rep.setTotalReturnAmount(st.getTotalReturnAmount());
		rep.setTotalInterestAmount(st.getTotalInterestAmount());
		rep.setTodayTnInvestAmount(st.getTodayTnInvestAmount());
		rep.setTodayRedeemAmount(st.getTodayRedeemAmount());
		rep.setTodayRepayInvestAmount(st.getTodayRepayInvestAmount());
		rep.setTodayRepayInterestAmount(st.getTodayRepayInterestAmount());
		rep.setOverdueTimes(st.getOverdueTimes());
		
		Map<String, Integer> map = this.productService.productAmount(ba);
		rep.setRaisingNum(map.get(Product.STATE_Raising));
		rep.setRaiseendNum(map.get(Product.STATE_Raiseend));
		rep.setDurationingNum(map.get(Product.STATE_Durationing));
		rep.setDurationendNum(map.get(Product.STATE_Durationend));
		rep.setClearingNum(map.get(Product.STATE_Clearing));
		rep.setClearedNum(this.productService.closedProductAmount(ba.getOid()));

		List<PublisherProductStatisticsEntity>  topFives = this.publisherProductStatisticsService.getTopFive(ba.getOid());
		for (PublisherProductStatisticsEntity en : topFives) {
			ChartPojo an = new ChartPojo();
			an.setXName(en.getProduct().getName());
			an.setYValue(en.getInvestAmount());
			rep.getTop5ProductList().add(an);
		}
		
		/** 定期产品募集中募集进度   */
		List<Product> proList = this.productService.getProductByPublisherOidAndState(ba.getOid(),
				Product.STATE_Raising);
		
		List<PublisherRaiseRateRep> raiseList = new ArrayList<PublisherRaiseRateRep>();
		if (proList != null && proList.size() > 0) {
			for (Product product : proList) {
				PublisherRaiseRateRep raise = new PublisherRaiseRateRep();
				raise.setProductName(product.getName());// 产品名称

				raise.setTotal(new BigDecimal("100.00"));// 总募集份额百分比
				raise.setRaised(ProductDecimalFormat.multiply(product.getCollectedVolume()
						.divide(product.getRaisedTotalNumber(), 2, BigDecimal.ROUND_HALF_UP)));// 已募集份额百分比
				raise.setToRaised(new BigDecimal("100.00").subtract(raise.getRaised()));// 待募集百分比

				raiseList.add(raise);
			}
		}
		rep.setRaiseRate(raiseList);

		
		List<Object[]> invstorList = this.publisherHoldService.analyseInvestor(ba.getOid());
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
	

	public PublisherStatisticsEntity saveEntity(PublisherStatisticsEntity st) {
		return this.publisherStatisticsDao.save(st);
	}

	public int increaseOverdueTimes(PublisherBaseAccountEntity publisherBaseAccount) {
		
		return this.publisherStatisticsDao.increaseOverdueTimes(publisherBaseAccount);
	}

	/**
	 * 累计付息总额
	 */
	public int increaseTotalInterestAmount(PublisherBaseAccountEntity publisherBaseAccount,
			BigDecimal successAllocateIncome) {
		return this.publisherStatisticsDao.increaseTotalInterestAmount(publisherBaseAccount, successAllocateIncome);
		
	}
	
	public PublisherStatisticsEntity findByPublisherBaseAccountEntity(PublisherBaseAccountEntity ba){
		return this.publisherStatisticsDao.findByPublisherBaseAccount(ba);
	}
}
