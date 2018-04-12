package com.guohuai.ams.portfolio.trade.liquidAsset;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.holdAsset.HoldDetService;
import com.guohuai.ams.portfolio.holdAsset.liquidAsset.LiquidHoldService;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.portfolio.trade.AssetTradeDao;
import com.guohuai.ams.portfolio.trade.AssetTradeEntity;
import com.guohuai.ams.portfolio.trade.AssetTradeForm;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;

/**
 * 投资组合--非现金类资产交易
 * @author star.zhu
 * 2016年12月27日
 */
@Service
public class LiquidTradeService {

	@Autowired
	private AssetTradeDao assetTradeDao;
	
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private LiquidHoldService liquidHoldService;
	@Autowired
	private HoldDetService holdDetService;
	
	/**
	 * 申购
	 * @param form
	 * @param operator
	 */
	@Transactional
	public void purchase(AssetTradeForm form, String operator) {
		AssetTradeEntity entity = new AssetTradeEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(form.getPortfolioOid()));
		entity.setAssetOid(form.getAssetOid());
		entity.setAssetName(form.getAssetName());
		entity.setAssetType(form.getAssetType());
		entity.setClassify(ConstantUtil.classify_liquid);
		entity.setTradeTime(DateUtil.getSqlCurrentDate());
		entity.setTradeCapital(BigDecimalUtil.formatForMul10000(form.getTradeCapital()));
		entity.setTradeType(ConstantUtil.trade_purchase);
		entity.setState(ConstantUtil.trade_create);
		entity.setAsker(operator);
		entity.setAskCapital(entity.getTradeCapital());
		entity.setAskVolume(entity.getTradeVolume());
		entity.setAskTime(entity.getTradeTime());
		
		assetTradeDao.save(entity);
	}
	
	/**
	 * 赎回
	 * @param form
	 * @param operator
	 */
	@Transactional
	public void redeem(AssetTradeForm form, String operator) {
		AssetTradeEntity entity = new AssetTradeEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(form.getPortfolioOid()));
		entity.setAssetOid(form.getAssetOid());
		entity.setAssetName(form.getAssetName());
		entity.setAssetType(form.getAssetType());
		entity.setClassify(ConstantUtil.classify_liquid);
		entity.setTradeTime(DateUtil.getSqlCurrentDate());
		entity.setTradeVolume(BigDecimalUtil.formatForMul10000(form.getTradeVolume()));
		entity.setTradeType(ConstantUtil.trade_redeem);
		entity.setState(ConstantUtil.trade_create);
		entity.setAsker(operator);
		entity.setAskCapital(entity.getTradeCapital());
		entity.setAskVolume(entity.getTradeVolume());
		entity.setAskTime(entity.getTradeTime());
		
		assetTradeDao.save(entity);
	}
	
	/**
	 * 净值校准
	 * @param form
	 * @param operator
	 */
	@Transactional
	public void calibration(AssetTradeForm form, String operator) {
		AssetTradeEntity entity = new AssetTradeEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(form.getPortfolioOid()));
		entity.setAssetOid(form.getAssetOid());
		entity.setAssetName(form.getAssetName());
		entity.setAssetType(form.getAssetType());
		entity.setClassify(ConstantUtil.classify_liquid);
		entity.setTradeTime(DateUtil.getSqlCurrentDate());
		entity.setTradeVolume(BigDecimalUtil.formatForMul10000(form.getTradeVolume()));
		entity.setTradeType(ConstantUtil.trade_calibration);
		entity.setState(ConstantUtil.trade_create);
		entity.setAsker(operator);
		entity.setAskCapital(entity.getTradeCapital());
		entity.setAskVolume(entity.getTradeVolume());
		entity.setAskTime(entity.getTradeTime());
		
		assetTradeDao.save(entity);
	}
	
	/**
	 * 审核
	 * @param form
	 * @param operator
	 * @param operate
	 */
	public void auditTrade(AssetTradeForm form, String operator, String operate) {
		AssetTradeEntity entity = assetTradeDao.findOne(form.getOid());
		if ("YES".equals(operate)) {
			entity.setState(ConstantUtil.trade_audit);
			entity.setAuditCapital(form.getAuditCapital());
			entity.setAuditVolume(form.getAuditVolume());
		} else {
			entity.setState(ConstantUtil.trade_audit_reject);
		}
		entity.setAuditor(operator);
		entity.setAuditMark(form.getAuditMark());
		entity.setAuditTime(DateUtil.getSqlCurrentDate());
	}
	
	/**
	 * 确认
	 * @param form
	 * @param operator
	 * @param operate
	 */
	public void confirmTrade(AssetTradeForm form, String operator, String operate) {
		AssetTradeEntity entity = assetTradeDao.findOne(form.getOid());
		if ("YES".equals(operate)) {
			entity.setState(ConstantUtil.trade_confirm);
			entity.setConfirmCapital(form.getConfirmCapital());
			entity.setConfirmVolume(form.getConfirmVolume());

			// 交易单价
			entity.setTradePrice(form.getConfirmCapital().divide(form.getConfirmVolume()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
			// 更新持仓数据
			liquidHoldService.saveOrUpdate(entity);
			// 更新分仓数据
			if (entity.getTradeType().equals(ConstantUtil.trade_purchase)) {
				if (ConstantUtil.classify_liquid.equals(form.getClassify())) {
					holdDetService.saveLiquid(entity);
				} else {
					holdDetService.saveIlliquid(entity);
				}
			}
			if (entity.getTradeType().equals(ConstantUtil.trade_redeem)) {
				if (ConstantUtil.classify_liquid.equals(form.getClassify())) {
					holdDetService.updateLiquid(entity);
				} else {
					holdDetService.updateIlliquid(entity);
				}
			}
		} else {
			entity.setState(ConstantUtil.trade_audit_reject);
		}
		entity.setConfirmer(operator);
		entity.setConfirmTime(DateUtil.getSqlCurrentDate());
	}
}
