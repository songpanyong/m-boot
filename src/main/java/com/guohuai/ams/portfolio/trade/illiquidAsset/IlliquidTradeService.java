package com.guohuai.ams.portfolio.trade.illiquidAsset;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.holdAsset.HoldDetService;
import com.guohuai.ams.portfolio.holdAsset.illiquidAsset.IlliquidHoldService;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.portfolio.trade.AssetTradeDao;
import com.guohuai.ams.portfolio.trade.AssetTradeEntity;
import com.guohuai.ams.portfolio.trade.AssetTradeForm;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;

/**
 * 投资组合--非现金类资产交易
 * @author star.zhu
 * 2016年12月27日
 */
@Service
public class IlliquidTradeService {

	@Autowired
	private AssetTradeDao assetTradeDao;
	
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private IlliquidHoldService illiquidHoldService;
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
		entity.setClassify(ConstantUtil.classify_illiquid);
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
		entity.setClassify(ConstantUtil.classify_illiquid);
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
	 * 转入
	 * @param form
	 * @param operator
	 */
	@Transactional
	public void transIn(AssetTradeForm form, String operator) {
		AssetTradeEntity entity = new AssetTradeEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(form.getPortfolioOid()));
		entity.setAssetOid(form.getAssetOid());
		entity.setAssetName(form.getAssetName());
		entity.setAssetType(form.getAssetType());
		entity.setClassify(ConstantUtil.classify_illiquid);
		entity.setTradeTime(DateUtil.getSqlCurrentDate());
		entity.setTradeVolume(BigDecimalUtil.formatForMul10000(form.getTradeVolume()));
		entity.setTradeCapital(BigDecimalUtil.formatForMul10000(form.getTradeCapital()));
		entity.setTradeType(ConstantUtil.trade_transIn);
		entity.setState(ConstantUtil.trade_create);
		entity.setAsker(operator);
		entity.setAskCapital(entity.getTradeCapital());
		entity.setAskVolume(entity.getTradeVolume());
		entity.setAskTime(entity.getTradeTime());
		
		assetTradeDao.save(entity);
	}
	
	/**
	 * 转出
	 * @param form
	 * @param operator
	 */
	@Transactional
	public void transOut(AssetTradeForm form, String operator) {
		AssetTradeEntity entity = new AssetTradeEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(form.getPortfolioOid()));
		entity.setAssetOid(form.getAssetOid());
		entity.setAssetName(form.getAssetName());
		entity.setAssetType(form.getAssetType());
		entity.setClassify(ConstantUtil.classify_illiquid);
		entity.setTradeTime(DateUtil.getSqlCurrentDate());
		entity.setTradeVolume(BigDecimalUtil.formatForMul10000(form.getTradeVolume()));
		entity.setTradeCapital(BigDecimalUtil.formatForMul10000(form.getTradeCapital()));
		entity.setTradeType(ConstantUtil.trade_transOut);
		entity.setState(ConstantUtil.trade_create);
		entity.setAsker(operator);
		entity.setAskCapital(entity.getTradeCapital());
		entity.setAskVolume(entity.getTradeVolume());
		entity.setAskTime(entity.getTradeTime());
		
		assetTradeDao.save(entity);
	}
	
	/**
	 * 认购
	 * @param form
	 * @param operator
	 */
	@Transactional
	public void subscribe(AssetTradeForm form, String operator) {
		AssetTradeEntity entity = new AssetTradeEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(form.getPortfolioOid()));
		entity.setAssetOid(form.getAssetOid());
		entity.setAssetName(form.getAssetName());
		entity.setAssetType(form.getAssetType());
		entity.setClassify(ConstantUtil.classify_illiquid);
		entity.setTradeTime(DateUtil.getSqlCurrentDate());
		entity.setTradeCapital(BigDecimalUtil.formatForMul10000(form.getTradeCapital()));
		entity.setTradeType(ConstantUtil.trade_subscribe);
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
		entity.setClassify(ConstantUtil.classify_illiquid);
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
			entity.setAuditCapital(BigDecimalUtil.formatForDivide10000(form.getAuditCapital()));
			entity.setAuditVolume(BigDecimalUtil.formatForDivide10000(form.getAuditVolume()));
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
		if (form.getConfirmVolume().equals(BigDecimal.ZERO)) {
			throw new AMPException("数据错误！");
		}
		AssetTradeEntity entity = assetTradeDao.findOne(form.getOid());
		if ("YES".equals(operate)) {
			entity.setState(ConstantUtil.trade_confirm);
			entity.setConfirmCapital(form.getConfirmCapital());
			entity.setConfirmVolume(form.getConfirmVolume());
			
			// 交易单价
			entity.setTradePrice(form.getConfirmCapital().divide(form.getConfirmVolume()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
			// 更新持仓数据
			illiquidHoldService.saveOrUpdate(entity);
			// 更新分仓数据
			if (entity.getTradeType().equals(ConstantUtil.trade_purchase)
					|| entity.getTradeType().equals(ConstantUtil.trade_subscribe)
					|| entity.getTradeType().equals(ConstantUtil.trade_transIn)) {
				if (ConstantUtil.classify_liquid.equals(form.getClassify())) {
					holdDetService.saveLiquid(entity);
				} else {
					holdDetService.saveIlliquid(entity);
				}
			}
			if (entity.getTradeType().equals(ConstantUtil.trade_redeem)
					|| entity.getTradeType().equals(ConstantUtil.trade_transOut)) {
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
