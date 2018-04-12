package com.guohuai.ams.portfolio.trade;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.trade.illiquidAsset.IlliquidTradeService;
import com.guohuai.ams.portfolio.trade.liquidAsset.LiquidTradeService;
import com.guohuai.basic.component.ext.web.PageResp;

/**
 * 投资组合--资产交易
 * @author star.zhu
 * 2016年12月27日
 */
@Service
public class AssetTradeService {
	@Autowired
	private AssetTradeDao assetTradeDao;
	
	@Autowired
	private LiquidTradeService liquidTradeService;
	@Autowired
	private IlliquidTradeService illiquidTradeService;
	
	/**
	 * 获取交易列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<AssetTradeEntity> getConfrimList(Specification<AssetTradeEntity> spec, Pageable pageable) {
		Page<AssetTradeEntity> page = assetTradeDao.findAll(spec, pageable);
		PageResp<AssetTradeEntity> resp = new PageResp<AssetTradeEntity>();
		if (!page.getContent().isEmpty()) {
			resp.setRows(page.getContent());
			resp.setTotal(page.getTotalElements());
		}
		
		return resp;
	}
	
	/**
	 * 资产交易
	 * @param form
	 * @param operator
	 */
	public void assetTrade(AssetTradeForm form, String operator) {
		// 申购
		if (ConstantUtil.trade_purchase.equals(form.getTradeType())) {
			if (ConstantUtil.classify_liquid.equals(form.getClassify())) {
				liquidTradeService.purchase(form, operator);
			} else {
				illiquidTradeService.purchase(form, operator);
			}
		} else if (ConstantUtil.trade_redeem.equals(form.getTradeType())) {
			// 赎回
			if (ConstantUtil.classify_liquid.equals(form.getClassify())) {
				liquidTradeService.redeem(form, operator);
			} else {
				illiquidTradeService.redeem(form, operator);
			}
		} else if (ConstantUtil.trade_calibration.equals(form.getTradeType())) {
			// 认购
			illiquidTradeService.calibration(form, operator);
		} else if (ConstantUtil.trade_transIn.equals(form.getTradeType())) {
			// 转入
			illiquidTradeService.transIn(form, operator);
		} else if (ConstantUtil.trade_transOut.equals(form.getTradeType())) {
			// 转出
			illiquidTradeService.transOut(form, operator);
		}
	}
}
