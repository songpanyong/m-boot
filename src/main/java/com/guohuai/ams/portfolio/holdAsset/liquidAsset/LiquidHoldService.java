package com.guohuai.ams.portfolio.holdAsset.liquidAsset;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAssetService;
import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.holdAsset.valuations.liquidValuations.LiquidValuationsService;
import com.guohuai.ams.portfolio.trade.AssetTradeEntity;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;

/**
 * 投资组合持仓非现金类资产
 * @author star.zhu
 * 2016年12月28日
 */
@Service
public class LiquidHoldService {

	@Autowired
	private LiquidHoldDao liquidHoldDao;
	
	@Autowired
	private LiquidValuationsService liquidValuationsService;
	@Autowired
	private LiquidAssetService liquidAssetService;
	
	/**
	 * 获取持仓列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<LiquidHoldEntity> getLiquidHoldList(Specification<LiquidHoldEntity> spec, Pageable pageable) {
		Page<LiquidHoldEntity> page = liquidHoldDao.findAll(spec, pageable);
		PageResp<LiquidHoldEntity> resp = new PageResp<LiquidHoldEntity>();
		
		if (!page.getContent().isEmpty()) {
			resp.setRows(page.getContent());
			resp.setTotal(page.getTotalElements());
		}
		
		return resp;
	}
	
	/**
	 * 获取投资组合持仓列表
	 * @param portfolioOid
	 * @return
	 */
	public List<LiquidHoldEntity> getLiquidHoldListByOid(String portfolioOid) {
		
		Specification<LiquidHoldEntity> spec = new Specification<LiquidHoldEntity>() {
			
			@Override
			public Predicate toPredicate(Root<LiquidHoldEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return cb.equal(root.get("portfolio.oid").as(String.class), portfolioOid);
			}
		};
		
		return liquidHoldDao.findAll(spec);
	}
	
	/**
	 * 获取持仓标的详情
	 * @param portfolioOid
	 * @param LiquidAssetOid
	 * @return
	 */
	@Transactional
	public LiquidHoldEntity getLiquidHoldInfo(String portfolioOid, String liquidAssetOid) {
		LiquidHoldEntity entity = liquidHoldDao.findLiquidHoldInfo(liquidAssetOid, portfolioOid);
		
		return entity;
	}
	
	/**
	 * 更新持仓信息
	 * @param trade
	 */
	@Transactional
	public void saveOrUpdate(AssetTradeEntity trade) {
		LiquidAsset liquid = liquidAssetService.findByOid(trade.getAssetOid());
		if (null == liquid) {
			throw new AMPException("未知的错误！");
		}
		LiquidHoldEntity entity = this.getLiquidHoldInfo(trade.getPortfolio().getOid(), liquid.getOid());
		if (null == entity) {
			if (ConstantUtil.trade_redeem.equals(trade.getTradeType())) {
				throw new AMPException("持仓信息有误，请联系管理员！");
			}
			entity = new LiquidHoldEntity();
			entity.setOid(StringUtil.uuid());
			entity.setLiquid(liquid);
			entity.setPortfolio(trade.getPortfolio());
			entity.setInvestDate(DateUtil.getSqlDate());
			entity.setValueDate(liquid.getValueDate());
			entity.setHoldShare(trade.getConfirmVolume());
			entity.setValuations(trade.getConfirmCapital());
			entity.setLastValueDate(DateUtil.getSqlDate());
			entity.setCreater(trade.getConfirmer());
			entity.setCreateTime(DateUtil.getSqlCurrentDate());
		} else {
			if (ConstantUtil.trade_calibration.equals(trade.getTradeType())) {
				entity.setHoldShare(trade.getConfirmVolume());
				entity.setValuations(trade.getConfirmCapital());
			} else if (ConstantUtil.trade_purchase.equals(trade.getTradeType())) {
				entity.setHoldShare(entity.getHoldShare().add(trade.getConfirmVolume()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
				entity.setValuations(entity.getValuations().add(trade.getConfirmCapital()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
			} else {
				entity.setHoldShare(entity.getHoldShare().subtract(trade.getConfirmVolume()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
				entity.setValuations(entity.getValuations().subtract(trade.getConfirmCapital()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
			}
			entity.setLastValueDate(DateUtil.getSqlDate());
			entity.setOperator(trade.getConfirmer());
			entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		}
		
		liquidHoldDao.save(entity);
	}
	
	/**
	 * 每日估值
	 * 只计算当日收益采集数据录入的资产
	 */
	@Transactional
	public void calcValuations() {
		// 获取持仓列表
		List<LiquidHoldEntity> list = liquidHoldDao.findAll();
		if (!list.isEmpty()) {
			for (LiquidHoldEntity entity : list) {
				if (entity.getLiquid().getLastValueDate().equals(DateUtil.getBeforeDate())) {
					liquidValuationsService.insert(entity, ConstantUtil.calc_system);
				}
			}
		}
	}
}
