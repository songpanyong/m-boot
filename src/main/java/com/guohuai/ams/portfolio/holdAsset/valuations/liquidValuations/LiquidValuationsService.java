package com.guohuai.ams.portfolio.holdAsset.valuations.liquidValuations;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.holdAsset.liquidAsset.LiquidHoldEntity;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;

/**
 * 投资组合持仓现金类资产历史每日估值
 * @author star.zhu
 * 2016年12月28日
 */
@Service
public class LiquidValuationsService {

	@Autowired
	private LiquidValuationsDao liquidValuationDao;
	/**
	 * 新增估值明细
	 * @param hold
	 * @param operator
	 */
	@Transactional
	public void insert(LiquidHoldEntity hold, String operator) {
		LiquidValuationsEntity entity = new LiquidValuationsEntity();
		try {
			BeanUtils.copyProperties(entity, hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
		entity.setOid(StringUtil.uuid());
		entity.setLiquidHold(hold);
		entity.setPortfolioOid(hold.getPortfolio().getOid());
		entity.setDailyProfit(hold.getLiquid().getDailyProfit());
		entity.setWeeklyYield(hold.getLiquid().getWeeklyYield());
		entity.setCreater(operator);
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		
		/**
		 * 更新持仓信息
		 * 当日收益（T日收益 = T-1日份额 * T-1日万份收益 / 1000）
		 * 累计收益（T日累计收益 = T-1日累计收益 + 当日收益）
		 * 持有份额（T日份额 = T-1日份额 + 当日收益）
		 * 当前估值（T日估值 = T-1日估值 + 当日收益）
		 */
		hold.setDayProfit(BigDecimalUtil.formatForDivide10000(hold.getHoldShare().multiply(hold.getLiquid().getDailyProfit()).setScale(7, BigDecimal.ROUND_HALF_DOWN)));
		hold.setTotalProfit(hold.getTotalProfit().add(hold.getDayProfit()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		hold.setHoldShare(hold.getHoldShare().add(hold.getDayProfit()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		hold.setValuations(hold.getValuations().add(hold.getDayProfit()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		hold.setLastValueDate(DateUtil.getSqlDate());
		hold.setOperator(operator);
		hold.setUpdateTime(DateUtil.getSqlCurrentDate());
		
		liquidValuationDao.save(entity);
	}
	
	/**
	 * 获取持仓标的资产历史详情
	 * @param illiquidHoldOid
	 * 					持仓OID
	 * @param valueDate
	 * 					估值日
	 * @return
	 */
	@Transactional
	public LiquidValuationsEntity getValuationsByDate(String liquidHoldOid, Date valueDate) {
		return liquidValuationDao.findByLiquidHoldOidAndValueDate(liquidHoldOid, valueDate);
	}
	
	/**
	 * 获取投资组合历史当日持仓估值列表
	 * @param portfolioOid
	 * @param valueDate
	 * @return
	 */
	@Transactional
	public List<LiquidValuationsEntity> getLiquidValuationsListByDate(String portfolioOid, Date valueDate) {
		return liquidValuationDao.findByPortfolioOidAndValueDate(portfolioOid, valueDate);
	}
}
