package com.guohuai.ams.liquidAsset.yield;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.enums.CashToolEventType;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAssetDao;
import com.guohuai.ams.liquidAsset.log.LiquidAssetLogService;
import com.guohuai.ams.liquidAsset.yield.LiquidAssetYieldForm.Profit;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.component.exception.AMPException;

@Service
@Transactional
public class LiquidAssetYieldService {

	@Autowired
	private LiquidAssetYieldDao liquidAssetYieldDao;
	@Autowired
	private LiquidAssetDao liquidAssetDao;
	@Autowired
	private LiquidAssetLogService liquidAssetLogService;
	@Autowired
	private LiquidAssetYieldService liquidAssetYieldService;

	/**
	 * 收益采集查询
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<LiquidAssetYield> getLiquidAssetList(Specification<LiquidAssetYield> spec, Pageable pageable) {
		return this.liquidAssetYieldDao.findAll(spec, pageable);
	}

	/**
	 * 分页查询现金收益
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<LiquidAssetYield> getLiquidAssetYieldList(Specification<LiquidAssetYield> spec, Pageable pageable) {
		return this.liquidAssetYieldDao.findAll(spec, pageable);
	}

	/**
	 * 查询某个货币基金收益的最近收益日
	 * 
	 * @param oid
	 * @return
	 */
	public Date findmaxProfitDateByOid(String oid) {
		return this.liquidAssetYieldDao.findmaxProfitDateByOid(oid);
	}

	/**
	 * 现金标的收益采集
	 * 
	 * @param form
	 */
	public JSONObject liquidAssetYield(LiquidAssetYieldForm form, String operator) {
		String liquidAssetOid = form.getOid();
		if (StringUtils.isBlank(liquidAssetOid))
			throw AMPException.getException("现金类标的id不能为空");
		// 获取现金类标的
		LiquidAsset liquidAsset = this.liquidAssetDao.findOne(liquidAssetOid);
		if (null == liquidAsset)
			throw AMPException.getException("找不到id为[" + liquidAssetOid + "]的现金类标的");

		this.liquidAssetLogService.saveLiquidAssetLog(liquidAsset, CashToolEventType.revenue, operator); // 现金管理工具收益采集

		Iterator<Profit> profit = form.getProfits().iterator();
		while (profit.hasNext()) {
			LiquidAssetYield cr = new LiquidAssetYield();
			BeanUtils.copyProperties(form, cr);

			cr.setLiquidAsset(liquidAsset);
			cr.setCreateTime(new Timestamp(System.currentTimeMillis()));

			Profit next = profit.next();
			cr.setDailyProfit(next.getDailyProfit());
			cr.setWeeklyYield(next.getWeeklyYield().divide(new BigDecimal(100), 6, BigDecimal.ROUND_DOWN));
			cr.setProfitDate(next.getProfitDate());

			//保存收益采集
			this.liquidAssetYieldDao.save(cr);
			if (!profit.hasNext()) {
				//更新最新收益
				this.liquidAssetDao.liquidAssetYield(liquidAsset.getOid(), cr.getDailyProfit(), cr.getWeeklyYield(), cr.getProfitDate());
			}
		}
		return this.dateVerify(form.getOid());
	}

	/**
	 * 收益采集日期校验
	 * 
	 * @param oid
	 * @return
	 */
	public JSONObject dateVerify(String oid) {

		JSONObject object = new JSONObject();
		// 收益起始日
		Date profitStartDate = null;
		// 最后一次采集日期
		Date maxProfitStartDate = null;
		// 当前可采集的最大日期
		Date maxProfitDeadlineDate = com.guohuai.component.util.DateUtil.getMaxProfitDeadlineDate();
		// 历史是否有收益采集
		boolean isProfited = false;
		// 最大可采集日期是否已采集
		boolean maxDateIsProfited = false;

		maxProfitStartDate = this.liquidAssetYieldService.findmaxProfitDateByOid(oid);

		isProfited = null != maxProfitStartDate;
		if (null == maxProfitStartDate) {
			maxDateIsProfited = false;
		} else {
			maxDateIsProfited = DateUtil.le(new java.sql.Date(maxProfitDeadlineDate.getTime()), new java.sql.Date(maxProfitStartDate.getTime()));
		}

		if (null == maxProfitStartDate) {
			profitStartDate = maxProfitDeadlineDate;
		} else {
			profitStartDate = DateUtil.addDays(maxProfitStartDate, 1);
			if (DateUtil.gt(new java.sql.Date(profitStartDate.getTime()), new java.sql.Date(maxProfitDeadlineDate.getTime()))) {
				profitStartDate = maxProfitDeadlineDate;
			}
		}

		object.put("profitStartDate", profitStartDate);
		object.put("maxProfitStartDate", maxProfitStartDate);
		object.put("maxProfitDeadlineDate", maxProfitDeadlineDate);
		object.put("isProfited", isProfited);
		object.put("maxDateIsProfited", maxDateIsProfited);

		return object;
	}

	public LiquidAssetYield findByDate(LiquidAsset asset, Date date) {
		return this.liquidAssetYieldDao.findByLiquidAssetAndProfitDate(asset, date);
	}
}
