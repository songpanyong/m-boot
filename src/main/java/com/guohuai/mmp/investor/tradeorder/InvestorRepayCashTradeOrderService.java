package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.duration.fact.income.IncomeAllocate;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.mmp.platform.msgment.InterestMailReq;
import com.guohuai.mmp.platform.msgment.InterestPushReq;
import com.guohuai.mmp.platform.msgment.MailService;
import com.guohuai.mmp.platform.msgment.PushService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.investor.InterestRateMethodService;
import com.guohuai.mmp.publisher.investor.InterestReq;
import com.guohuai.mmp.publisher.investor.InterestRequireNew;
import com.guohuai.mmp.publisher.product.rewardincomepractice.PracticeService;
import com.guohuai.mmp.publisher.product.rewardincomepractice.RewardIsNullRep;
import com.guohuai.tulip.platform.facade.obj.InvalidBidsReq;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvestorRepayCashTradeOrderService {

	@Autowired
	private ProductService productService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private InterestRequireNew interestRequireNew;
	@Autowired
	private InvestorRepayCashTradeOrderRequireNewService investorRepayCashTradeOrderRequireNewService;
	@Autowired
	private InterestRateMethodService interestRateMethodService;
	@Autowired
	private PracticeService practiceService;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private MailService mailService;
	@Autowired
	private PushService pushService;
	

	
	/**
	 * isEstablish true 产品募集成立，进入存续期
	 * false 产品募集失败，还本付募集期利息
	 * @param productOid
	 * @param isEstablish
	 */
	public boolean isEstablish(String productOid, boolean isEstablish) {
		Product product = this.productService.findByOid(productOid);
		boolean okFlag = true;
		
		
		if (isEstablish) {
			this.productService.lockProduct(productOid);
			updateExpectedRevenue(product);
			this.productService.unLockProduct(productOid);
		} else {
			/** 还本付息锁 */
			this.productService.repayLock(productOid);
			
			String lastOid = "0";
			while (true) {
				List<PublisherHoldEntity> holds = this.publisherHoldService.findByProduct(
						product, lastOid);
				if (holds.isEmpty()) {
					break;
				}
				for (PublisherHoldEntity hold : holds) {
					
					try {
						
						this.investorRepayCashTradeOrderRequireNewService.processItem(hold.getOid());
						
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						okFlag = false;
						
					}
					lastOid = hold.getOid();
				}
			}
			if (okFlag) {
				this.productService.updateRepayStatus(product, Product.PRODUCT_repayLoanStatus_repayed, Product.PRODUCT_repayInterestStatus_repayed);
				InvalidBidsReq ireq = new InvalidBidsReq();
				ireq.setProductId(productOid);
				tulipService.onInvalidBids(ireq);;
			} else {
				this.productService.updateRepayStatus(product, Product.PRODUCT_repayLoanStatus_repayFailed, Product.PRODUCT_repayInterestStatus_repayFailed);
			}
		}
		return okFlag;
	}
	
	public void updateExpectedRevenue(Product product) {
		String lastOid = "0";
		
		while (true) {
			List<PublisherHoldEntity> holds = this.publisherHoldService.findByProduct(
					product, lastOid);
			
			if (holds.isEmpty()) {
				break;
			}
			for (PublisherHoldEntity hold : holds) {
				
				try {
					
					this.investorRepayCashTradeOrderRequireNewService.updateExpectedRevenue(hold.getOid());
					//Filter out the wish plan
					if (hold.getWishplanOid() == null) {
						
						InterestMailReq mailReq = new InterestMailReq();
						mailReq.setUserOid(hold.getInvestorBaseAccount().getOid());
						mailReq.setProductName(hold.getProduct().getName());
						
						mailService.interest(mailReq);

						InterestPushReq pushReq = new InterestPushReq();
						pushReq.setUserOid(hold.getInvestorBaseAccount().getOid());
						pushReq.setProductName(hold.getProduct().getName());
						pushService.interest(pushReq);
					}
					
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					
				}
				lastOid = hold.getOid();
			}
		}
		
	}

	/**
	 *付息 
	 */
	public void allocateIncome(String productOid, BigDecimal incomeAmount, BigDecimal couponAmount, BigDecimal fpRate) {
		log.info("fpRate : {}, incomeAmount : {}", fpRate, incomeAmount);
		Product product = this.productService.findByOid(productOid);
		if (!Product.STATE_Durationend.equals(product.getState())) {
			// error.define[30054]=募集期尚未结束(CODE:30054)
			throw AMPException.getException(30054);
		}
		
		/** 派息锁 */
		this.productService.repayInterestLock(productOid);
		
		RewardIsNullRep rep = this.practiceService.rewardIsNullRep(product, null);
		BigDecimal totalInterestedVolume = rep.getTotalHoldVolume();
		Date incomeDate = rep.getTDate();
		
		BigDecimal ratio = DecimalUtil.zoomIn(fpRate, 100);
		incomeAmount = incomeAmount.multiply(new BigDecimal(product.getDurationPeriodDays()));
		InterestReq ireq = new InterestReq();
		ireq.setProduct(product);
		ireq.setTotalInterestedVolume(totalInterestedVolume);
		ireq.setIncomeAmount(incomeAmount);
		ireq.setIncomeCouponAmount(couponAmount);
		ireq.setRatio(ratio);
		ireq.setIncomeDate(incomeDate);
		ireq.setIncomeType(IncomeAllocate.ALLOCATE_INCOME_TYPE_durationIncome);
		
		IncomeAllocate incomeAllocate = this.interestRequireNew.newAllocate(ireq);
		
		this.interestRateMethodService.interest(incomeAllocate.getOid(), incomeAllocate.getProduct().getOid());
		
	}
	

	@Transactional(value = TxType.REQUIRES_NEW)
	public void repayCash(String productOid) {
		/** 还本付息锁 */
		this.productService.repayLoanLock(productOid);

		Product product = productService.findByOid(productOid);
		if (Product.TYPE_Producttype_01.equals(product.getType().getOid()) 
				&& Product.STATE_Durationend.equals(product.getState())) {
			String lastOid = "0";
			boolean okFlag = true;
			while (true) {
				List<PublisherHoldEntity> holds = this.publisherHoldService.findByProduct(product, lastOid);
				if (holds.isEmpty()) {
					break;
				}
				for (PublisherHoldEntity hold : holds) {
					
					try {
						
						investorRepayCashTradeOrderRequireNewService.processCashItem(hold.getOid());

					} catch (Exception e) {
						log.error(e.getMessage(), e);
						okFlag = false;
						
					}
					lastOid = hold.getOid();
				}
			}
			
			if (okFlag) {

				this.productService.repayLoanEnd(product.getOid(), Product.PRODUCT_repayLoanStatus_repayed);
			} else {
				this.productService.repayLoanEnd(product.getOid(), Product.PRODUCT_repayLoanStatus_repayFailed);
			}
			
			
		} else {
			throw new AMPException("产品非存续期结束 ");
		}
	}
	
	
	

}
