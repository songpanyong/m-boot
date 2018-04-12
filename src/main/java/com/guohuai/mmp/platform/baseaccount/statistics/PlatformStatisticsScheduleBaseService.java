package com.guohuai.mmp.platform.baseaccount.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guohuai.ams.product.Product;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderService;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.ope.time.OpeTime;
import com.guohuai.mmp.ope.time.OpeTimeService;
import com.guohuai.mmp.publisher.bankorder.PublisherBankOrderEntity;
import com.guohuai.mmp.publisher.bankorder.PublisherBankOrderService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsEntity;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsService;
import com.guohuai.mmp.publisher.investor.interest.result.InterestResultEntity;
import com.guohuai.mmp.publisher.investor.interest.result.InterestResultService;
@Service
@Transactional
public class PlatformStatisticsScheduleBaseService {

	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private OpeTimeService opeTimeService;
	@Autowired
	private InterestResultService interestResultService;
	@Autowired
	private PublisherBankOrderService publisherBankOrderService;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	
	// 累计借款总额统计
	public void totalLoanAmountStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_TOTALLOAN);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "completeTime")));		
		Specification<InvestorTradeOrderEntity> spec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("completeTime").as(Timestamp.class), new Timestamp(time));
				Predicate b = cb.equal(root.get("orderStatus").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed);
				Predicate c = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_invest);
				Predicate d = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest);
				Predicate e = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff);
				Predicate f = cb.or(c, d, e);
				return cb.and(a, b, f);
			}
		};
		
		Page<InvestorTradeOrderEntity> orderPage = investorTradeOrderService.findPage(spec, pageable);
		List<InvestorTradeOrderEntity> orders = orderPage.getContent();
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		if (orders != null && !orders.isEmpty()){
			for (InvestorTradeOrderEntity order : orders){
				BigDecimal amount = order.getOrderAmount() == null ? BigDecimal.ZERO : order.getOrderAmount();
				totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
				
				this.publisherStatisticsService.increaseTotalLoanAmount(order);	// 发行人统计--借款 
				
				if (lastTime < order.getCompleteTime().getTime()){
					lastTime = order.getCompleteTime().getTime();
				}
//				if (lastTime < order.getUpdateTime().getTime()){
//				lastTime = order.getUpdateTime().getTime();
//			}
			}
		}
		
		//平台统计--投资单份额确认:累计交易额、累计借款额
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setTotalLoanAmount(en.getTotalLoanAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		platformStatisticsService.updateEntity(en);
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_TOTALLOAN, lastTime);
	}
	
	// 累计还款总额统计
	public void totalReturnAmountStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_TOTALRETURN);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "completeTime")));		
		Specification<InvestorTradeOrderEntity> spec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("completeTime").as(Timestamp.class), new Timestamp(time));
				Predicate b = cb.equal(root.get("orderStatus").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed);
				Predicate c = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem);
				Predicate d = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem);
				Predicate e = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_cash);
				Predicate f = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed);
				Predicate g = cb.equal(root.get("orderType").as(String.class), InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem);
				Predicate h = cb.or(c, d, e, f, g);
				
				return cb.and(a, b, h);
			}
		};
		
		Page<InvestorTradeOrderEntity> orderPage = investorTradeOrderService.findPage(spec, pageable);
		List<InvestorTradeOrderEntity> orders = orderPage.getContent();
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		if (orders != null && !orders.isEmpty()){
			for (InvestorTradeOrderEntity order : orders){
				BigDecimal amount = order.getOrderAmount() == null ? BigDecimal.ZERO : order.getOrderAmount();
				totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
				
				this.publisherStatisticsService.increaseTotalReturnAmount(order); // 发行人统计--还款
				
//				if (lastTime < order.getUpdateTime().getTime()){
//					lastTime = order.getUpdateTime().getTime();
//				}
				if (lastTime < order.getCompleteTime().getTime()){
					lastTime = order.getCompleteTime().getTime();
				}
			}
		}
		
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setTotalReturnAmount(en.getTotalReturnAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		platformStatisticsService.updateEntity(en);
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_TOTALRETURN, lastTime);
	}
	
	// 累计付息总额统计
	public void totalInterestAmountStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_TOTALINTEREST);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "createTime")));		
		Specification<InterestResultEntity> spec = new Specification<InterestResultEntity>() {
			@Override
			public Predicate toPredicate(Root<InterestResultEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("createTime").as(Timestamp.class), new Timestamp(time));
				Predicate b = cb.equal(root.get("status").as(String.class), InterestResultEntity.RESULT_status_ALLOCATED);
				
				return cb.and(a, b);
			}
		};
		
		Page<InterestResultEntity> orderPage = interestResultService.findPage(spec, pageable);
		List<InterestResultEntity> orders = orderPage.getContent();
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		if (orders != null && !orders.isEmpty()){
			for (InterestResultEntity order : orders){
				BigDecimal amount = order.getSuccessAllocateIncome() == null ? BigDecimal.ZERO : order.getSuccessAllocateIncome();
				totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
				
				// 发行人统计--付息
				publisherStatisticsService.increaseTotalInterestAmount(order.getProduct().getPublisherBaseAccount(),
						order.getSuccessAllocateIncome());
				
//				if (lastTime < order.getUpdateTime().getTime()){
//					lastTime = order.getUpdateTime().getTime();
//				}
				if (lastTime < order.getCreateTime().getTime()){
					lastTime = order.getCreateTime().getTime();
				}
			}
		}
		
		// 平台统计-付息
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setTotalInterestAmount(en.getTotalInterestAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		platformStatisticsService.updateEntity(en);
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_TOTALINTEREST, lastTime);
	}
	
	// 发行人充值总额统计
	public void publisherDepositAmountStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_PUBLISHERDEPOSIT);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "completeTime")));		
		Specification<PublisherBankOrderEntity> spec = new Specification<PublisherBankOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<PublisherBankOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("completeTime").as(Timestamp.class), new Timestamp(time));
				Predicate b = cb.equal(root.get("orderStatus").as(String.class), PublisherBankOrderEntity.BANKORDER_orderStatus_done);
				Predicate c = cb.equal(root.get("orderType").as(String.class), PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_deposit);
				Predicate d = cb.equal(root.get("orderType").as(String.class), PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_depositLong);
				Predicate e = cb.or(c ,d);
						
				return cb.and(a, b, e);
			}
		};
		
		Page<PublisherBankOrderEntity> orderPage = publisherBankOrderService.findPage(spec, pageable);
		List<PublisherBankOrderEntity> orders = orderPage.getContent();
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		if (orders != null && !orders.isEmpty()){
			for (PublisherBankOrderEntity order : orders){
				BigDecimal amount = order.getOrderAmount() == null ? BigDecimal.ZERO : order.getOrderAmount();
				totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
				
				publisherStatisticsService.updateStatistics4Deposit(order);	// 更新<<发行人-统计>>.<<累计充值总额>>

//				if (lastTime < order.getUpdateTime().getTime()){
//					lastTime = order.getUpdateTime().getTime();
//				}
				if (lastTime < order.getCompleteTime().getTime()){
					lastTime = order.getCompleteTime().getTime();
				}
			}
		}
		
		// 更新<<平台-统计>>.<<累计交易总额>><<发行人充值总额>>
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setPublisherTotalDepositAmount(en.getPublisherTotalDepositAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		platformStatisticsService.updateEntity(en);
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_PUBLISHERDEPOSIT, lastTime);
	}
	
	// 发行人提现总额统计
	public void publisherWithdrawAmountStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_PUBLISHERWITHDRAW);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "completeTime")));		
		Specification<PublisherBankOrderEntity> spec = new Specification<PublisherBankOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<PublisherBankOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("completeTime").as(Timestamp.class), new Timestamp(time));
				Predicate b = cb.equal(root.get("orderStatus").as(String.class), PublisherBankOrderEntity.BANKORDER_orderStatus_done);
				Predicate c = cb.equal(root.get("orderType").as(String.class), PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_withdraw);
				Predicate d = cb.equal(root.get("orderType").as(String.class), PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_withdrawLong);
				Predicate e = cb.or(c ,d);
				
				return cb.and(a, b, e);
			}
		};
		
		Page<PublisherBankOrderEntity> orderPage = publisherBankOrderService.findPage(spec, pageable);
		List<PublisherBankOrderEntity> orders = orderPage.getContent();
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		if (orders != null && !orders.isEmpty()){
			for (PublisherBankOrderEntity order : orders){
				BigDecimal amount = order.getOrderAmount() == null ? BigDecimal.ZERO : order.getOrderAmount();
				totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
				
				this.publisherStatisticsService.updateStatistics4Withdraw(order);	// 更新<<发行人-统计>>.<<累计提现总额>>

//				if (lastTime < order.getUpdateTime().getTime()){
//					lastTime = order.getUpdateTime().getTime();
//				}
				if (lastTime < order.getCompleteTime().getTime()){
					lastTime = order.getCompleteTime().getTime();
				}
			}
		}
		
		// 更新<<平台-统计>>.<<累计交易总额>><<发行人提现总额>>
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setPublisherTotalWithdrawAmount(en.getPublisherTotalWithdrawAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		platformStatisticsService.updateEntity(en);
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_PUBLISHERWITHDRAW, lastTime);
	}
	
	// 投资人充值总额统计
	public void investorDepositStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_INVESTORDEPOSIT);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "completeTime")));		
		Specification<InvestorBankOrderEntity> spec = new Specification<InvestorBankOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorBankOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("completeTime").as(Timestamp.class), new Timestamp(time));
				Predicate b = cb.equal(root.get("orderStatus").as(String.class), InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
				Predicate c = cb.equal(root.get("orderType").as(String.class), InvestorBankOrderEntity.BANKORDER_orderType_deposit);
				Predicate d = cb.equal(root.get("orderType").as(String.class), InvestorBankOrderEntity.BANKORDER_orderType_depositLong);
				Predicate e = cb.or(c ,d);
				
				return cb.and(a, b, e);
			}
		};
		
		Page<InvestorBankOrderEntity> orderPage = investorBankOrderService.findPage(spec, pageable);
		List<InvestorBankOrderEntity> orders = orderPage.getContent();
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		if (orders != null && !orders.isEmpty()){
			for (InvestorBankOrderEntity order : orders){
				BigDecimal amount = order.getOrderAmount() == null ? BigDecimal.ZERO : order.getOrderAmount();
				totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
				
//				if (lastTime < order.getUpdateTime().getTime()){
//					lastTime = order.getUpdateTime().getTime();
//				}
				if (lastTime < order.getCompleteTime().getTime()){
					lastTime = order.getCompleteTime().getTime();
				}
			}
		}
		
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setInvestorTotalDepositAmount(en.getInvestorTotalDepositAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		platformStatisticsService.updateEntity(en);
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_INVESTORDEPOSIT, lastTime);
	}
	
	// 投资人提现总额统计
	public void investorWithdrawStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_INVESTORWITHDRAW);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "completeTime")));		
		Specification<InvestorBankOrderEntity> spec = new Specification<InvestorBankOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorBankOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("completeTime").as(Timestamp.class), new Timestamp(time));
				Predicate b = cb.equal(root.get("orderStatus").as(String.class), InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
				Predicate c = cb.equal(root.get("orderType").as(String.class), InvestorBankOrderEntity.BANKORDER_orderType_withdraw);
				Predicate d = cb.equal(root.get("orderType").as(String.class), InvestorBankOrderEntity.BANKORDER_orderType_withdrawLong);
				Predicate e = cb.or(c ,d);
				
				return cb.and(a, b, e);
			}
		};
		
		Page<InvestorBankOrderEntity> orderPage = investorBankOrderService.findPage(spec, pageable);
		List<InvestorBankOrderEntity> orders = orderPage.getContent();
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		if (orders != null && !orders.isEmpty()){
			for (InvestorBankOrderEntity order : orders){
				BigDecimal amount = order.getOrderAmount() == null ? BigDecimal.ZERO : order.getOrderAmount();
				totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
				
//				if (lastTime < order.getUpdateTime().getTime()){
//					lastTime = order.getUpdateTime().getTime();
//				}
				if (lastTime < order.getCompleteTime().getTime()){
					lastTime = order.getCompleteTime().getTime();
				}
			}
		}
		
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setInvestorTotalWithdrawAmount(en.getInvestorTotalWithdrawAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
		platformStatisticsService.updateEntity(en);
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_INVESTORWITHDRAW, lastTime);
	}
	
	// 卡券统计
	public void couponStatisticsDo() {
		final long time = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_COUPON);
		long lastTime = time;
		
		Pageable pageable = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "createTime")));		
		Specification<TradeOrderCouponEntity> spec = new Specification<TradeOrderCouponEntity>() {
			@Override
			public Predicate toPredicate(Root<TradeOrderCouponEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("createTime").as(Timestamp.class), new Timestamp(time));
				return a;
			}
		};
		
		Page<TradeOrderCouponEntity> orderPage = tradeOrderCouponService.findPage(spec, pageable);
		List<TradeOrderCouponEntity> orders = orderPage.getContent();
		
		BigDecimal couponAmount = BigDecimal.ZERO;	// 代金券
		BigDecimal tasteCouponAmount = BigDecimal.ZERO;	// 体验金
		Long coupon = 0l;	// 代金券数量
		Long tasteCoupon = 0l;	// 体验金数量
		Long rateCoupon = 0l;	// 加息券数量
		
		if (orders != null && !orders.isEmpty()){
			for (TradeOrderCouponEntity order : orders){
				if (order.getCouponType().equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_coupon)){
					coupon++;
					couponAmount = couponAmount.add(order.getCouponAmount()).setScale(2, RoundingMode.HALF_UP);
				}else if (order.getCouponType().equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_tasteCoupon)){
					tasteCoupon++;
					tasteCouponAmount = tasteCouponAmount.add(order.getCouponAmount()).setScale(2, RoundingMode.HALF_UP);
				}else if (order.getCouponType().equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_rateCoupon)){
					rateCoupon++;
				}
				
//				if (lastTime < order.getUpdateTime().getTime()){
//					lastTime = order.getUpdateTime().getTime();
//				}
				if (lastTime < order.getCreateTime().getTime()){
					lastTime = order.getCreateTime().getTime();
				}
			}
			
			//平台统计--代金券数量，体验金数量，加息券数量，代金券总额，体验金总额
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			
			BigDecimal totalCouponAmount = en.getTotalCouponAmount() == null ? BigDecimal.ZERO : en.getTotalCouponAmount();	// 代金券
			BigDecimal totalTasteCouponAmount = en.getTotalTasteCouponAmount() == null ? BigDecimal.ZERO : en.getTotalTasteCouponAmount();	// 体验金
			Long totalCoupon = en.getTotalCoupon() == null ? 0l : en.getTotalCoupon();	// 代金券数量
			Long totalTasteCoupon = en.getTotalTasteCoupon() == null ? 0l : en.getTotalTasteCoupon();	// 体验金数量
			Long totalRateCoupon = en.getTotalRateCoupon() == null ? 0l : en.getTotalRateCoupon();	// 加息券数量
			
			
			en.setTotalCoupon(totalCoupon+coupon);
			en.setTotalTasteCoupon(totalTasteCoupon+tasteCoupon);
			en.setTotalRateCoupon(totalRateCoupon+rateCoupon);
			en.setTotalCouponAmount(totalCouponAmount.add(couponAmount).setScale(2, RoundingMode.HALF_UP));
			en.setTotalTasteCouponAmount(totalTasteCouponAmount.add(tasteCouponAmount).setScale(2, RoundingMode.HALF_UP));
			
			platformStatisticsService.updateEntity(en);
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_COUPON, lastTime);
		}
	}


	// 更新注册人数
	public void registerStatisticsDo() {
		long registerNum = investorBaseAccountService.getAllCount();
		
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setRegisterAmount(Integer.valueOf(registerNum+""));
		
		platformStatisticsService.updateEntity(en);
	}

	// 更新实名人数
	public void verifiedInvestorStatisticsDo() {
		long verifiedNum = investorBaseAccountService.getAllVerifiedCount();
		
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setVerifiedInvestorAmount(Integer.valueOf(verifiedNum+""));
		
		platformStatisticsService.updateEntity(en);
	}

	// 更新发行人数量
	public void publisherStatisticsDo() {
		long publisherNum = publisherBaseAccountService.getAllCount();
		
		PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
		
		en.setPublisherAmount(Integer.valueOf(publisherNum+""));
		
		platformStatisticsService.updateEntity(en);
	}
	
	//发行人今日借款统计
	public void publisherTotalTLoanStatisticsDo() {
		String date = DateUtil.getToday();
		String startTime = date+" 00:00:00";
		String endTime = date+" 23:59:59";
		
		// 金额，发行人id,产品类型
		List<Object[]> totalObjList = investorTradeOrderService.getTotalTLoanAmount(startTime, endTime);
		if (totalObjList != null && !totalObjList.isEmpty()){
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					String publisherOid = obj[1] == null ? "0" : obj[1]+"";
					String productType = obj[2] == null ? "" : obj[2]+"";
					
					PublisherBaseAccountEntity pubAccount = publisherBaseAccountService.findOneNoEx(publisherOid);
				
					if (pubAccount != null){
						PublisherStatisticsEntity pubStatistics = publisherStatisticsService.findByPublisherBaseAccountEntity(pubAccount);
						if (productType.equals(Product.TYPE_Producttype_01)){
							// 定期
							pubStatistics.setTodayTnInvestAmount(amount);
						}else if (productType.equals(Product.TYPE_Producttype_02)){
							// 活期  暂没用
//							pubStatistics.setTodayT0InvestAmount(amount);
						}
						publisherStatisticsService.saveEntity(pubStatistics);
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	// 累计借款总额统计
	public void handTotalLoanAmountStatisticsDo() throws ParseException {
		long lastTime = 0l;
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		// 金额，更新时间，发行人id
		List<Object[]> totalObjList = investorTradeOrderService.getTotalLoanAmount();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			Map<String, BigDecimal> map = new HashMap<>();
			
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					String publisherOid = obj[2] == null ? "0" : obj[2]+"";
					
					totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
					
					if (lastTime < time){
						lastTime = time;
					}
					
					if (map.containsKey(publisherOid)){
						BigDecimal pubAmount = map.get(publisherOid);
						map.put(publisherOid, pubAmount.add(amount).setScale(2, RoundingMode.HALF_UP));
					}else{
						map.put(publisherOid, amount);
					}
				}
			}
			
			//平台统计--投资单份额确认:累计交易额、累计借款额
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			
			en.setTotalLoanAmount(totalAmount);
			en.setTotalTradeAmount(totalAmount);	// 重置平台累计交易额
			platformStatisticsService.updateEntity(en);
			
			// 发行人统计--借款 
			for (String pubOid : map.keySet()){
				PublisherBaseAccountEntity pubAccount = publisherBaseAccountService.findOneNoEx(pubOid);
				if (pubAccount != null){
					PublisherStatisticsEntity pubStatistics = publisherStatisticsService.findByPublisherBaseAccountEntity(pubAccount);
					pubStatistics.setTotalLoanAmount(map.get(pubOid));
					
					publisherStatisticsService.saveEntity(pubStatistics);
				}
			}
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_TOTALLOAN, lastTime);
		}
	}
	
	// 累计还款总额统计
	public void handTotalReturnAmountStatisticsDo() throws ParseException {
		long lastTime = 0l;
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		// 金额，更新时间，发行人id
		List<Object[]> totalObjList = investorTradeOrderService.getTotalReturnAmount();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			Map<String, BigDecimal> map = new HashMap<>();
			
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					String publisherOid = obj[2] == null ? "0" : obj[2]+"";
					
					totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
					
					if (lastTime < time){
						lastTime = time;
					}
					
					if (map.containsKey(publisherOid)){
						BigDecimal pubAmount = map.get(publisherOid);
						map.put(publisherOid, pubAmount.add(amount).setScale(2, RoundingMode.HALF_UP));
					}else{
						map.put(publisherOid, amount);
					}
				}
			}
			
			//平台统计--还款单份额确认:累计交易额、累计还款额
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			
			en.setTotalReturnAmount(totalAmount);
			en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));	// 累计借款已重置过，此处不再重置
			platformStatisticsService.updateEntity(en);
			
			// 发行人统计--还款
			for (String pubOid : map.keySet()){
				PublisherBaseAccountEntity pubAccount = publisherBaseAccountService.findOneNoEx(pubOid);
				if (pubAccount != null){
					PublisherStatisticsEntity pubStatistics = publisherStatisticsService.findByPublisherBaseAccountEntity(pubAccount);
					pubStatistics.setTotalReturnAmount(map.get(pubOid));
					
					publisherStatisticsService.saveEntity(pubStatistics);
				}
			}
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_TOTALRETURN, lastTime);
		}
	}
	
	// 累计付息总额统计
	public void handTotalInterestAmountStatisticsDo() throws ParseException {
		long lastTime = 0l;
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		// 金额，创建时间，发行人id
		List<Object[]> totalObjList = interestResultService.getTotalInterestAmount();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			Map<String, BigDecimal> map = new HashMap<>();
			
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					String publisherOid = obj[2] == null ? "0" : obj[2]+"";
					
					totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
					
					if (lastTime < time){
						lastTime = time;
					}
					
					if (map.containsKey(publisherOid)){
						BigDecimal pubAmount = map.get(publisherOid);
						map.put(publisherOid, pubAmount.add(amount).setScale(2, RoundingMode.HALF_UP));
					}else{
						map.put(publisherOid, amount);
					}
				}
			}
			
			//平台统计--还款单份额确认:累计交易额、累计还款额
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			
			en.setTotalInterestAmount(totalAmount);
			platformStatisticsService.updateEntity(en);
			
			// 发行人统计--计息
			for (String pubOid : map.keySet()){
				PublisherBaseAccountEntity pubAccount = publisherBaseAccountService.findOneNoEx(pubOid);
				if (pubAccount != null){
					PublisherStatisticsEntity pubStatistics = publisherStatisticsService.findByPublisherBaseAccountEntity(pubAccount);
					pubStatistics.setTotalInterestAmount(map.get(pubOid));
					
					publisherStatisticsService.saveEntity(pubStatistics);
				}
			}
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_TOTALINTEREST, lastTime);
		}
	}
	
	// 发行人充值总额统计
	public void handPublisherDepositAmountStatisticsDo() throws ParseException {
		long lastTime = 0l;
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		// 金额，更新时间，发行人id
		List<Object[]> totalObjList = publisherBankOrderService.getPublisherDepositAmount();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			Map<String, BigDecimal> map = new HashMap<>();
			
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					String publisherOid = obj[2] == null ? "0" : obj[2]+"";
					
					totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
					
					if (lastTime < time){
						lastTime = time;
					}
					
					if (map.containsKey(publisherOid)){
						BigDecimal pubAmount = map.get(publisherOid);
						map.put(publisherOid, pubAmount.add(amount).setScale(2, RoundingMode.HALF_UP));
					}else{
						map.put(publisherOid, amount);
					}
				}
			}
			
			// 更新<<平台-统计>>.<<累计交易总额>><<发行人充值总额>>
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			en.setPublisherTotalDepositAmount(totalAmount);
			en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
			platformStatisticsService.updateEntity(en);
			
			// 发行人统计--累计充值总额
			for (String pubOid : map.keySet()){
				PublisherBaseAccountEntity pubAccount = publisherBaseAccountService.findOneNoEx(pubOid);
				if (pubAccount != null){
					PublisherStatisticsEntity pubStatistics = publisherStatisticsService.findByPublisherBaseAccountEntity(pubAccount);
					pubStatistics.setTotalDepositAmount(map.get(pubOid));
					
					publisherStatisticsService.saveEntity(pubStatistics);
				}
			}
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_PUBLISHERDEPOSIT, lastTime);
		}
	}
	
	// 发行人提现总额统计
	public void handPublisherWithdrawAmountStatisticsDo() throws ParseException {
		long lastTime = 0l;
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		// 金额，更新时间，发行人id
		List<Object[]> totalObjList = publisherBankOrderService.getPublisherWithdrawAmount();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			Map<String, BigDecimal> map = new HashMap<>();
			
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					String publisherOid = obj[2] == null ? "0" : obj[2]+"";
					
					totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
					
					if (lastTime < time){
						lastTime = time;
					}
					
					if (map.containsKey(publisherOid)){
						BigDecimal pubAmount = map.get(publisherOid);
						map.put(publisherOid, pubAmount.add(amount).setScale(2, RoundingMode.HALF_UP));
					}else{
						map.put(publisherOid, amount);
					}
				}
			}
			
			// 更新<<平台-统计>>.<<累计交易总额>><<发行人提现总额>>
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			en.setPublisherTotalWithdrawAmount(totalAmount);
			en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
			platformStatisticsService.updateEntity(en);
			
			// 发行人统计--累计充值总额
			for (String pubOid : map.keySet()){
				PublisherBaseAccountEntity pubAccount = publisherBaseAccountService.findOneNoEx(pubOid);
				if (pubAccount != null){
					PublisherStatisticsEntity pubStatistics = publisherStatisticsService.findByPublisherBaseAccountEntity(pubAccount);
					pubStatistics.setTotalWithdrawAmount(map.get(pubOid));
					
					publisherStatisticsService.saveEntity(pubStatistics);
				}
			}
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_PUBLISHERWITHDRAW, lastTime);
		}
	}
	
	// 投资人充值总额统计
	public void handInvestorDepositStatisticsDo() throws ParseException {
		long lastTime = 0l;
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		// 金额，更新时间
		List<Object[]> totalObjList = investorBankOrderService.getInvestorDepositAmount();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					
					totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
					
					if (lastTime < time){
						lastTime = time;
					}
				}
			}
			
			// 更新<<平台-统计>>.<<累计交易总额>><<投资人充值总额>>
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			en.setInvestorTotalDepositAmount(totalAmount);
			en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
			platformStatisticsService.updateEntity(en);
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_INVESTORDEPOSIT, lastTime);
		}
	}
	
	// 投资人提现总额统计
	public void handInvestorWithdrawStatisticsDo() throws ParseException {
		long lastTime = 0l;
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		// 金额，更新时间
		List<Object[]> totalObjList = investorBankOrderService.getInvestorWithdrawAmount();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"");
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					
					totalAmount = totalAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
					
					if (lastTime < time){
						lastTime = time;
					}
				}
			}
			
			// 更新<<平台-统计>>.<<累计交易总额>><<投资人提现总额>>
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			en.setInvestorTotalWithdrawAmount(totalAmount);
			en.setTotalTradeAmount(en.getTotalTradeAmount().add(totalAmount).setScale(2, RoundingMode.HALF_UP));
			platformStatisticsService.updateEntity(en);
			
			opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_INVESTORWITHDRAW, lastTime);
		}
	}
	
	// 卡券统计
	public void handCouponStatisticsDo() throws ParseException {
		long lastTime = 0l;
		
		// 金额，创建时间，数量，类型
		List<Object[]> totalObjList = tradeOrderCouponService.getCouponStatistics();
		
		if (totalObjList != null && !totalObjList.isEmpty()){
			//平台统计--代金券数量，体验金数量，加息券数量，代金券总额，体验金总额
			PlatformStatisticsEntity en = platformStatisticsService.findByPlatformBaseAccount();
			
			for (Object[] obj : totalObjList){
				if (obj != null){
					BigDecimal amount = obj[0] == null ? BigDecimal.ZERO : new BigDecimal(obj[0]+"").setScale(2, RoundingMode.HALF_UP);
					long time = obj[1] == null ? 0l : DateUtil.fetchTimestamp(obj[1]+"").getTime();
					long num = obj[2] == null ? 0l : Long.parseLong(obj[2]+"");
					String type = obj[3] == null ? null : obj[2]+"";
					
					if (type.equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_coupon)){
						en.setTotalCoupon(num);
						en.setTotalCouponAmount(amount);
					}else if (type.equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_tasteCoupon)){
						en.setTotalTasteCoupon(num);
						en.setTotalTasteCouponAmount(amount);
					}else if (type.equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_rateCoupon)){
						en.setTotalRateCoupon(num);
					}
					
					if (lastTime < time){
						lastTime = time;
					}
				}
			}
			
			platformStatisticsService.updateEntity(en);
		}
		
		opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_COUPON, lastTime);
	}
}
