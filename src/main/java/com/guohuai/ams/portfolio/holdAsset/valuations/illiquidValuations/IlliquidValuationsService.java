package com.guohuai.ams.portfolio.holdAsset.valuations.illiquidValuations;

import java.math.BigDecimal;
import java.sql.Date;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.holdAsset.illiquidAsset.IlliquidHoldEntity;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;

/**
 * 投资组合持仓现金类资产历史每日估值
 * @author star.zhu
 * 2016年12月28日
 */
@Service
public class IlliquidValuationsService {

	@Autowired
	private IlliquidValuationsDao illiquidValuationDao;
	/**
	 * 新增估值明细
	 * @param hold
	 * @param operator
	 */
	@Transactional
	public void insert(IlliquidHoldEntity hold, String operator) {
		IlliquidValuationsEntity entity = new IlliquidValuationsEntity();
		try {
			BeanUtils.copyProperties(entity, hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
		entity.setOid(StringUtil.uuid());
		entity.setIlliquidHold(hold);
		entity.setPortfolioOid(hold.getPortfolio().getOid());
		entity.setCreater(operator);
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		
		/**
		 * 更新持仓信息
		 * 当日收益（T日收益 = T-1日份额 * 募集期/存续期/逾期收益率）
		 * 累计收益（T日累计收益 = T-1日累计收益 + 当日收益）
		 * 持有份额（T日份额 = T-1日份额 + 当日收益），非现金类资产持有份额不变
		 * 当前估值（T日估值 = T-1日估值 + 当日收益）
		 */
		// 收益率
		BigDecimal yield = BigDecimal.ZERO;
		// 募集期
		if (ConstantUtil.lifeState_collect.equals(hold.getIlliquid().getLifeState())) {
			yield = hold.getIlliquid().getCollectIncomeRate();
		}
		// 存续期
		if (ConstantUtil.lifeState_normal.equals(hold.getIlliquid().getLifeState())) {
			yield = hold.getIlliquid().getExpAror();
		}
		// 逾期
		if (ConstantUtil.lifeState_overTime.equals(hold.getIlliquid().getLifeState())) {
			yield = hold.getIlliquid().getOverdueRate();
		}
		hold.setDayProfit(hold.getHoldShare().multiply(yield).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		hold.setTotalProfit(hold.getTotalProfit().add(hold.getDayProfit()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		hold.setValuations(hold.getValuations().add(hold.getDayProfit()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		hold.setLastValueDate(DateUtil.getSqlDate());
		hold.setOperator(operator);
		hold.setUpdateTime(DateUtil.getSqlCurrentDate());
		
		illiquidValuationDao.save(entity);
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
	public IlliquidValuationsEntity getValuationsByDate(String illiquidHoldOid, Date valueDate) {
		return illiquidValuationDao.findByIlliquidHoldOidAndValueDate(illiquidHoldOid, valueDate);
	}
}
