package com.guohuai.ams.portfolio20.net.correct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.acct.books.document.SPVDocumentService;
import com.guohuai.ams.order.SPVOrderService;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.portfolio20.net.correct.order.PortfolioNetCorrectOrderEntity;
import com.guohuai.ams.portfolio20.net.correct.order.PortfolioNetCorrectOrderService;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午12:23:53
 */
@Service
public class PortfolioNetCorrectService {

	@Autowired
	private PortfolioNetCorrectDao portfolioNetCorrectDao;
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private PortfolioNetCorrectOrderService portfolioNetCorrectOrderService;
	@Autowired
	private SPVDocumentService spvDocumentService;
	@Autowired
	private SPVOrderService spvOrderService;

	public PortfolioNetCorrectPrepareResp prepare(String portfolioOid) {
		PortfolioNetCorrectPrepareResp p = new PortfolioNetCorrectPrepareResp();

		PortfolioEntity portfolio = this.portfolioService.getByOid(portfolioOid);
		if (null == portfolio) {
			throw new GHException("无效的投资组合ID: " + portfolioOid);
		}

		// 有未审核的净值校准订单
		List<PortfolioNetCorrectOrderEntity> hisOrders = this.portfolioNetCorrectOrderService.findByState(portfolio, PortfolioNetCorrectOrderEntity.ORDER_STATE_SUBMIT);
		p.setCorrecting(null != hisOrders && hisOrders.size() > 0);

		Date maxCorrectDate = DateUtil.addDays(new java.sql.Date(System.currentTimeMillis()), -1);

		p.setMaxCorrectDate(maxCorrectDate);

		// 历史是否有执行过校准操作
		PortfolioNetCorrectEntity hisCorrect = this.getLastCorrect(portfolio);
		if (null != hisCorrect) {

			p.setCorrected(true);

			p.setLastNetDate(hisCorrect.getNetDate());

			Date currentCorrectDate = DateUtil.addDays(hisCorrect.getNetDate(), 1);
			p.setCurrentCorrectDate(currentCorrectDate);

			// 当前日期的所有数据已经校准完成
			p.setAllCorrected(DateUtil.eq(maxCorrectDate, hisCorrect.getNetDate()));

			p.setLastShare(hisCorrect.getShare());
			p.setLastNav(hisCorrect.getNav());
			p.setLastNet(hisCorrect.getNet());

			// TODO 需要通过 SPV 交易, 查询到对应的交易记录, 补充下面3个字段
			p.setChargeAmount(BigDecimal.ZERO);
			p.setWithdrawAmount(BigDecimal.ZERO);
			p.setTradeAmount(BigDecimal.ZERO);

		} else {
			p.setCorrected(false);
		}

		return p;
	}

	public PortfolioNetCorrectEntity getLastCorrect(PortfolioEntity portfolio) {

		Specification<PortfolioNetCorrectEntity> spec = new Specification<PortfolioNetCorrectEntity>() {

			@Override
			public Predicate toPredicate(Root<PortfolioNetCorrectEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.equal(root.get("portfolio").as(PortfolioEntity.class), portfolio);
			}
		};
		Pageable pageable = new PageRequest(0, 1, new Sort(new Order(Direction.DESC, "netDate")));
		Page<PortfolioNetCorrectEntity> hisCorrects = this.portfolioNetCorrectDao.findAll(spec, pageable);
		if (null != hisCorrects.getContent() & hisCorrects.getContent().size() > 0) {

			PortfolioNetCorrectEntity hisCorrect = hisCorrects.getContent().get(0);
			return hisCorrect;
		}
		return null;
	}

	@Transactional
	public void newCorrect(PortfolioNetCorrectOrderEntity order) {
		PortfolioNetCorrectEntity last = this.getLastCorrect(order.getPortfolio());

		PortfolioNetCorrectEntity c = new PortfolioNetCorrectEntity();
		c.setOid(StringUtil.uuid());
		c.setPortfolio(order.getPortfolio());
		c.setOrder(order);
		c.setNetDate(order.getNetDate());
		c.setShare(order.getShare());
		c.setNav(order.getNav());
		c.setNet(order.getNet());

		if (null != last) {
			c.setLastShare(last.getShare());
			c.setLastNav(last.getNav());
			c.setLastNet(last.getNet());
		} else {
			c.setLastShare(BigDecimal.ZERO);
			c.setLastNav(BigDecimal.ZERO);
			c.setLastNet(BigDecimal.ZERO);
		}

		BigDecimal[] tradeAmounts = this.spvOrderService.getAmounts(order.getNetDate(), order.getPortfolio().getOid());
		BigDecimal investAmount = tradeAmounts[0].setScale(2, RoundingMode.HALF_UP);
		BigDecimal redeemAmount = tradeAmounts[1].setScale(2, RoundingMode.HALF_UP);
		BigDecimal tradeAmount = investAmount.subtract(redeemAmount);

		c.setChargeAmount(investAmount);
		c.setWithdrawAmount(redeemAmount);
		c.setTradeAmount(tradeAmount);

		PortfolioNetCorrectEntity correct = this.getLastCorrect(order.getPortfolio());

		BigDecimal netYield = BigDecimal.ZERO;

		if (null == correct) {
			//  净值增长率
			if (tradeAmount.signum() != 0) {
				netYield = order.getShare().multiply(order.getNav()).setScale(6, RoundingMode.HALF_UP).subtract(tradeAmount).divide(tradeAmount, 6, RoundingMode.HALF_UP);
			} else {
				netYield = BigDecimal.ZERO;
			}
		} else {
			// 净值增长率
			if (correct.getNet().signum() == 0) {
				if (tradeAmount.signum() == 0) {
					netYield = BigDecimal.ZERO;
				} else {
					netYield = order.getShare().multiply(order.getNav()).setScale(6, RoundingMode.HALF_UP).subtract(tradeAmount).divide(tradeAmount, 6, RoundingMode.HALF_UP);
				}
			} else {
				netYield = order.getShare().multiply(order.getNav()).setScale(6, RoundingMode.HALF_UP).subtract(correct.getNet()).subtract(tradeAmount).divide(correct.getNet(), 6, RoundingMode.HALF_UP);
			}
		}

		c.setNetYield(netYield);

		this.portfolioNetCorrectDao.save(c);

		this.portfolioService.updateNet(c);

		this.spvOrderService.entry(order.getNetDate(), order.getPortfolio().getOid());

		// 净收益
		BigDecimal income = c.getNet().subtract(c.getLastNet()).subtract(c.getTradeAmount());
		if (income.signum() != 0) {
			this.spvDocumentService.incomeConfirm(c.getPortfolio().getOid(), c.getOid(), income);
		}

	}

	public Page<PortfolioNetCorrectEntity> getListByHistory(Specification<PortfolioNetCorrectEntity> spec, Pageable pageable) {
		return this.portfolioNetCorrectDao.findAll(spec, pageable);
	}

}
