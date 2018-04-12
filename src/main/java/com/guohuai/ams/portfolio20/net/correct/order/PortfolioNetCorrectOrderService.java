package com.guohuai.ams.portfolio20.net.correct.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.order.SPVOrderService;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.portfolio20.net.correct.PortfolioNetCorrectEntity;
import com.guohuai.ams.portfolio20.net.correct.PortfolioNetCorrectService;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午12:18:08
 */
@Service
public class PortfolioNetCorrectOrderService {

	@Autowired
	private PortfolioNetCorrectOrderDao portfolioNetCorrectOrderDao;
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private PortfolioNetCorrectService portfolioNetCorrectService;
	@Autowired
	private SPVOrderService spvOrderService;

	public List<PortfolioNetCorrectOrderEntity> findByState(PortfolioEntity portfolio, String orderState) {
		return this.portfolioNetCorrectOrderDao.findByPortfolioAndOrderState(portfolio, orderState);
	}

	@Transactional
	public void createOrder(PortfolioNetCorrectOrderForm form, String operator) {
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID");
		}
		// 下订单, 做校验
		// 有未审核的净值校准订单
		List<PortfolioNetCorrectOrderEntity> hisOrders = this.findByState(portfolio, PortfolioNetCorrectOrderEntity.ORDER_STATE_SUBMIT);
		if (null != hisOrders && hisOrders.size() > 0) {
			throw new GHException("有未审核的净值校准订单, 请审核后再次操作.");
		}

		PortfolioNetCorrectOrderEntity o = new PortfolioNetCorrectOrderEntity();
		o.setOid(StringUtil.uuid());
		o.setPortfolio(portfolio);

		PortfolioNetCorrectEntity correct = this.portfolioNetCorrectService.getLastCorrect(portfolio);

		BigDecimal[] tradeAmounts = this.spvOrderService.getAmounts(form.getNetDate(), portfolio.getOid());
		BigDecimal investAmount = tradeAmounts[0].setScale(2, RoundingMode.HALF_UP);
		BigDecimal redeemAmount = tradeAmounts[1].setScale(2, RoundingMode.HALF_UP);
		BigDecimal tradeAmount = investAmount.subtract(redeemAmount);

		if (null == correct) {
			o.setNetDate(form.getNetDate());
			o.setShare(form.getShare());
			o.setNav(form.getNav());
			o.setNet(form.getShare().multiply(o.getNav()).setScale(6, RoundingMode.HALF_UP));

			// 和原有系统对接之后, 下面应该从 spv 交易表取
			o.setChargeAmount(investAmount);
			o.setWithdrawAmount(redeemAmount);
			o.setTradeAmount(tradeAmount);

			BigDecimal netYield = BigDecimal.ZERO;

			//  净值增长率
			if (tradeAmount.signum() != 0) {
				netYield = form.getShare().multiply(o.getNav()).setScale(6, RoundingMode.HALF_UP).subtract(tradeAmount).divide(tradeAmount, 6, RoundingMode.HALF_UP);
			} else {
				netYield = BigDecimal.ZERO;
			}
			o.setNetYield(netYield);

		} else {
			// 校准日期
			Date netDate = DateUtil.addDays(correct.getNetDate(), 1);
			if (!DateUtil.eq(netDate, form.getNetDate())) {
				throw new GHException("校准日期错误, 当前状态, 仅可校准 " + netDate.toString() + " 的净值数据.");
			}

			o.setNetDate(netDate);
			o.setShare(form.getShare());
			o.setNav(form.getNav());
			o.setNet(form.getShare().multiply(o.getNav()).setScale(6, RoundingMode.HALF_UP));

			//  和原有系统对接之后, 下面应该从 spv 交易表取
			o.setChargeAmount(investAmount);
			o.setWithdrawAmount(redeemAmount);
			o.setTradeAmount(tradeAmount);

			BigDecimal netYield = BigDecimal.ZERO;

			// 净值增长率
			if (correct.getNet().signum() == 0) {
				if (tradeAmount.signum() == 0) {
					netYield = BigDecimal.ZERO;
				} else {
					netYield = form.getShare().multiply(o.getNav()).setScale(6, RoundingMode.HALF_UP).subtract(tradeAmount).divide(tradeAmount, 6, RoundingMode.HALF_UP);
				}
			} else {
				netYield = form.getShare().multiply(o.getNav()).setScale(6, RoundingMode.HALF_UP).subtract(correct.getNet()).subtract(tradeAmount).divide(correct.getNet(), 6, RoundingMode.HALF_UP);
			}
			o.setNetYield(netYield);
		}

		o.setCreator(operator);
		o.setCreateTime(new Timestamp(System.currentTimeMillis()));
		o.setOrderState(PortfolioNetCorrectOrderEntity.ORDER_STATE_SUBMIT);
		this.portfolioNetCorrectOrderDao.save(o);
	}

	@Transactional
	public void passOrder(String oid, String auditMark, String operator) {

		PortfolioNetCorrectOrderEntity order = this.portfolioNetCorrectOrderDao.findOne(oid);
		if (null == order) {
			throw new GHException("未知的订单ID: " + oid);
		}

		order.setAuditor(operator);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setOrderState(PortfolioNetCorrectOrderEntity.ORDER_STATE_PASS);
		order.setAuditMark(auditMark);
		this.portfolioNetCorrectOrderDao.save(order);

		this.portfolioNetCorrectService.newCorrect(order);

	}

	@Transactional
	public void failOrder(String oid, String auditMark, String operator) {
		PortfolioNetCorrectOrderEntity order = this.portfolioNetCorrectOrderDao.findOne(oid);
		if (null == order) {
			throw new GHException("未知的订单ID: " + oid);
		}

		order.setAuditor(operator);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setOrderState(PortfolioNetCorrectOrderEntity.ORDER_STATE_FAIL);
		order.setAuditMark(auditMark);
		this.portfolioNetCorrectOrderDao.save(order);
	}

	@Transactional
	public void deleteOrder(String oid, String operator) {
		PortfolioNetCorrectOrderEntity order = this.portfolioNetCorrectOrderDao.findOne(oid);
		if (null == order) {
			throw new GHException("未知的订单ID: " + oid);
		}
		if (PortfolioNetCorrectOrderEntity.ORDER_STATE_PASS.equals(order.getOrderState())) {
			throw new GHException("已审核通过的订单, 不可删除.");
		}
		order.setOrderState(PortfolioNetCorrectOrderEntity.ORDER_STATE_DELETE);
		this.portfolioNetCorrectOrderDao.save(order);
	}

	public Page<PortfolioNetCorrectOrderEntity> getListByRecording(Specification<PortfolioNetCorrectOrderEntity> spec, Pageable pageable) {

		return this.portfolioNetCorrectOrderDao.findAll(spec, pageable);
	}

}
