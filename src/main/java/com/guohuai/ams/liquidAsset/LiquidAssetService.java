package com.guohuai.ams.liquidAsset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.enums.CashToolEventType;
import com.guohuai.ams.liquidAsset.log.LiquidAssetLogService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class LiquidAssetService {

	@Autowired
	private LiquidAssetDao liquidAssetDao;
	@Autowired
	private LiquidAssetLogService liquidAssetLogService;

	public LiquidAsset createInvestment(LiquidAssetForm form) {
		LiquidAsset entity = new LiquidAsset();
		entity.setSn(form.getSn());
		entity.setName(form.getName());
		entity.setType(form.getType());
		if (null == form.getOperationMode()) {
			entity.setOperationMode("");
		} else {
			entity.setOperationMode(form.getOperationMode());
		}
		entity.setConfirmDays(form.getConfirmDays());
		entity.setContractDays(form.getContractDays());
		if (null == form.getPerfBenchmark()) {
			entity.setPerfBenchmark("");
		} else {
			entity.setPerfBenchmark(form.getPerfBenchmark());
		}
		entity.setIncomeSchedule(form.getIncomeSchedule());
		if (null == form.getExchangeCd()) {
			entity.setExchangeCd("");
		} else {
			entity.setExchangeCd(form.getExchangeCd());
		}
		if (null == form.getManagerName()) {
			entity.setManagerName("");
		} else {
			entity.setManagerName(form.getManagerName());
		}
		if (null == form.getManagementCompany()) {
			entity.setManagementCompany("");
		} else {
			entity.setManagementCompany(form.getManagementCompany());
		}
		if (null == form.getManagementFullName()) {
			entity.setManagementFullName("");
		} else {
			entity.setManagementFullName(form.getManagementFullName());
		}
		if (null == form.getCustodian()) {
			entity.setCustodian("");
		} else {
			entity.setCustodian(form.getCustodian());
		}
		if (null == form.getCustodianFullName()) {
			entity.setCustodianFullName("");
		} else {
			entity.setCustodianFullName(form.getCustodianFullName());
		}
		if (null == form.getInvestField()) {
			entity.setInvestField("");
		} else {
			entity.setInvestField(form.getInvestField());
		}
		if (null == form.getInvestTarget()) {
			entity.setInvestTarget("");
		} else {
			entity.setInvestTarget(form.getInvestTarget());
		}
		entity.setHoldPorpush(form.getHoldPorpush());
		entity.setRiskLevel(form.getRiskLevel());
		if ("CASHTOOLTYPE_01".equals(form.getType())) {
			entity.setWeeklyYield(null);
			entity.setDailyProfit(null);
		} else if ("CASHTOOLTYPE_02".equals(form.getType())){
				entity.setWeeklyYield(form.getYield().divide(new BigDecimal(100),10,RoundingMode.HALF_UP));
				BigDecimal dailyProfit = form.getYield().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP).divide(new BigDecimal(form.getContractDays()), 10, RoundingMode.HALF_UP).multiply(new BigDecimal(10000)).setScale(6, RoundingMode.HALF_UP);
				entity.setDailyProfit(dailyProfit);
				entity.setYield(form.getYield().divide(new BigDecimal(100),10,RoundingMode.HALF_UP));
				entity.setBaseAmount(form.getBaseAmount().multiply(new BigDecimal(10000)));
				entity.setBaseYield(form.getBaseYield().divide(new BigDecimal(100),10,RoundingMode.HALF_UP));
		}
		entity.setApplyAmount(new BigDecimal(0));
		entity.setHoldShare(new BigDecimal(0));
		entity.setLockupShare(new BigDecimal(0));
		entity.setPrice(new BigDecimal(0));
		entity.setDayProfit(new BigDecimal(0));
		entity.setTotalPfofit(new BigDecimal(0));
		entity.setValuations(new BigDecimal(0));
		entity.setNetValue(new BigDecimal(0));
		return entity;
	}

	public LiquidAsset save(LiquidAsset liquidAsset) {
		return this.liquidAssetDao.save(liquidAsset);
	}

	public Page<LiquidAsset> getLiquidAssetList(Specification<LiquidAsset> spec, Pageable pageable) {
		return this.liquidAssetDao.findAll(spec, pageable);
	}

	/**
	 * 查看标的详情
	 * 
	 * @param oid
	 * @return
	 */
	public LiquidAsset findByOid(String oid) {
		return this.liquidAssetDao.findOne(oid);
	}

	/**
	 * 标的审核通过
	 * 
	 * @param oid
	 * @param liquidStateCheckpass
	 * @param operator
	 */
	@SuppressWarnings("static-access")
	public void check(String oid, String liquidStateCheckpass, String operator) {
		LiquidAsset liquidAsset = this.findByOid(oid);
		if (null == liquidAsset || !liquidAsset.LIQUID_STATE_pretrial.equals(liquidAsset.getState())) {
			throw new RuntimeException();
		}
		liquidAsset.setState(liquidStateCheckpass);
		liquidAsset.setUpdateTime(DateUtil.getSqlCurrentDate());
		liquidAsset.setOperator(operator);
		this.save(liquidAsset);
	}

	/**
	 * 移出出库
	 * 
	 * @param oid
	 * @param loginId
	 */
	public void remove(String oid, String loginId) {
		LiquidAsset ct = this.liquidAssetDao.findOne(oid);
		ct.setState(LiquidAsset.LIQUID_STATE_invalid);
		ct.setOperator(loginId);
		ct.setUpdateTime(DateUtil.getSqlCurrentDate());
		liquidAssetLogService.saveLiquidAssetLog(oid, CashToolEventType.invalid, loginId);
		this.liquidAssetDao.save(ct);
	}

	public void findBySnAndState(LiquidAsset liquidAsset) {
		LiquidAsset liquid = this.liquidAssetDao.findBySnAndState(liquidAsset.getSn(), LiquidAsset.LIQUID_STATE_invalid);
		if (liquid != null) {
			throw AMPException.getException("标的编号[" + liquidAsset.getSn() + "]已存在");
		}
	}

	/**
	 * 根据标的类型和状态获取标的名称
	 * 
	 * @param type
	 * @return
	 */
	public List<LiquidAssetOptions> getOptions(String type) {
		List<LiquidAsset> assets = this.liquidAssetDao.findByStateAndType(LiquidAsset.LIQUID_STATE_collecting, type);
		List<LiquidAssetOptions> options = new ArrayList<LiquidAssetOptions>();
		for (LiquidAsset asset : assets) {
			options.add(new LiquidAssetOptions(asset));
		}
		return options;
	}

	/**
	 * 申购下单成功, 调整申请份额
	 * 
	 * @param oid
	 * @param value
	 */
	@Transactional
	public void applyForPurchase(String oid, BigDecimal applyAmount) {
		this.liquidAssetDao.incrApplyAmount(oid, applyAmount);
	}

	/**
	 * 申购订单审核通过, 调整持有份额
	 * 
	 * @param oid
	 * @param value
	 */
	@Transactional
	public void passForPurchase(String oid, BigDecimal applyAmount) {
		this.liquidAssetDao.decrApplyAmount(oid, applyAmount);
		this.liquidAssetDao.incrHoldShare(oid, applyAmount);
	}

	/**
	 * 申购订单审核失败, 调整申请份额
	 * 
	 * @param oid
	 * @param value
	 */
	@Transactional
	public void failForPurchase(String oid, BigDecimal applyAmount) {
		this.liquidAssetDao.decrApplyAmount(oid, applyAmount);
	}

	/**
	 * 赎回下单成功
	 * 
	 * @param oid
	 * @param value
	 *            赎回本金份额
	 */
	public void applyForRedeem(String oid, BigDecimal redeemShare) {
		this.liquidAssetDao.incrLockupShare(oid, redeemShare);
	}

	/**
	 * 赎回审核通过
	 * 
	 * @param oid
	 * @param redeemShare
	 *            下单冻结份额
	 * @param realShare
	 *            实际赎回份额
	 */
	public void passForRedeem(String oid, BigDecimal redeemShare) {
		this.liquidAssetDao.decrLockupShare(oid, redeemShare);
		this.liquidAssetDao.decrHoldShare(oid, redeemShare);
	}

	/**
	 * 赎回下单审核驳回
	 * 
	 * @param oid
	 * @param value
	 */
	public void failForRedeem(String oid, BigDecimal redeemShare) {
		this.liquidAssetDao.decrLockupShare(oid, redeemShare);
	}

	/**
	 * 编辑现金类标的
	 * @param form
	 * @return
	 */
	public LiquidAsset editInvestment(LiquidAssetForm form) {
		LiquidAsset entity = this.findByOid(form.getOid());
		if (null == entity) {
			throw new RuntimeException("未知的现金类标的oid:"+form.getOid());		
		}
		if (null != entity && !LiquidAsset.LIQUID_STATE_waitPretrial.equals(entity.getState())
				&& !LiquidAsset.LIQUID_STATE_reject.equals(entity.getState())) {
			throw new RuntimeException("该现金类标的不能编辑");
		}
		entity.setSn(form.getSn());
		entity.setName(form.getName());
		entity.setType(form.getType());
		entity.setOperationMode(form.getOperationMode());
		entity.setConfirmDays(form.getConfirmDays());
		entity.setContractDays(form.getContractDays());
		entity.setPerfBenchmark(form.getPerfBenchmark());
		entity.setIncomeSchedule(form.getIncomeSchedule());
		entity.setExchangeCd(form.getExchangeCd());
		entity.setManagerName(form.getManagerName());
		entity.setManagementCompany(form.getManagementCompany());
		entity.setManagementFullName(form.getManagementFullName());
		entity.setCustodian(form.getCustodian());
		entity.setCustodianFullName(form.getCustodianFullName());
		entity.setInvestField(form.getInvestField());
		entity.setInvestTarget(form.getInvestTarget());
		entity.setHoldPorpush(form.getHoldPorpush());
		entity.setRiskLevel(form.getRiskLevel());
		if ("CASHTOOLTYPE_02".equals(form.getType())) {
			entity.setWeeklyYield(form.getBaseYield().divide(new BigDecimal(100),10,RoundingMode.HALF_UP));
			BigDecimal dailyProfit = form.getYield().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP).divide(new BigDecimal(form.getContractDays()), 10, RoundingMode.HALF_UP).multiply(new BigDecimal(10000)).setScale(6, RoundingMode.HALF_UP);
			entity.setDailyProfit(dailyProfit);
			entity.setYield(form.getYield().divide(new BigDecimal(100),10,RoundingMode.HALF_UP));
			entity.setBaseAmount(form.getBaseAmount().multiply(new BigDecimal(10000)));
			entity.setBaseYield(form.getBaseYield().divide(new BigDecimal(100),10,RoundingMode.HALF_UP));
		}
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		
		return entity;
	}
}
