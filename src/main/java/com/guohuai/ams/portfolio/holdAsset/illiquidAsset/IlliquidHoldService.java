package com.guohuai.ams.portfolio.holdAsset.illiquidAsset;

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

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.illiquidAsset.IlliquidAssetService;
import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.holdAsset.valuations.illiquidValuations.IlliquidValuationsService;
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
public class IlliquidHoldService {

	@Autowired
	private IlliquidHoldDao illiquidHoldDao;
	
	@Autowired
	private IlliquidValuationsService illiquidValuationsService;
	@Autowired
	private IlliquidAssetService illiquidAssetService;
	
	/**
	 * 获取持仓列表
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<IlliquidHoldEntity> getIlliquidHoldList(Specification<IlliquidHoldEntity> spec, Pageable pageable) {
		Page<IlliquidHoldEntity> page = illiquidHoldDao.findAll(spec, pageable);
		PageResp<IlliquidHoldEntity> resp = new PageResp<IlliquidHoldEntity>();
		
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
	public List<IlliquidHoldEntity> getIlliquidHoldListByOid(String portfolioOid) {
		
		Specification<IlliquidHoldEntity> spec = new Specification<IlliquidHoldEntity>() {
			
			@Override
			public Predicate toPredicate(Root<IlliquidHoldEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return cb.equal(root.get("portfolio.oid").as(String.class), portfolioOid);
			}
		};
		
		return illiquidHoldDao.findAll(spec);
	}
	
	/**
	 * 获取持仓标的详情
	 * @param portfolioOid
	 * @param illiquidAssetOid
	 * @return
	 */
	@Transactional
	public IlliquidHoldEntity getIlliquidHoldInfo(String portfolioOid, String illiquidAssetOid) {
		IlliquidHoldEntity entity = illiquidHoldDao.findIlliquidHoldInfo(illiquidAssetOid, portfolioOid);
		
		return entity;
	}
	
	/**
	 * 更新持仓信息
	 * @param trade
	 */
	@Transactional
	public void saveOrUpdate(AssetTradeEntity trade) {
		IlliquidAsset illiquid = illiquidAssetService.findByOid(trade.getAssetOid());
		if (null == illiquid) {
			throw new AMPException("未知的错误！");
		}
		IlliquidHoldEntity entity = this.getIlliquidHoldInfo(trade.getPortfolio().getOid(), illiquid.getOid());
		if (null == entity) {
			if (ConstantUtil.trade_redeem.equals(trade.getTradeType())
					|| ConstantUtil.trade_transOut.equals(trade.getTradeType())
					|| ConstantUtil.trade_calibration.equals(trade.getTradeType())) {
				throw new AMPException("持仓信息有误，请联系管理员！");
			}
			entity = new IlliquidHoldEntity();
			entity.setOid(StringUtil.uuid());
			entity.setIlliquid(illiquidAssetService.findByOid(trade.getAssetOid()));
			entity.setPortfolio(trade.getPortfolio());
			entity.setInvestDate(DateUtil.getSqlDate());
			entity.setHoldShare(trade.getConfirmVolume());
			entity.setValuations(trade.getConfirmCapital());
			entity.setLastValueDate(DateUtil.getSqlDate());
			entity.setCreater(trade.getConfirmer());
			entity.setCreateTime(DateUtil.getSqlCurrentDate());
		} else {
			if (ConstantUtil.trade_calibration.equals(trade.getTradeType())) {
				entity.setHoldShare(trade.getConfirmVolume());
				entity.setValuations(trade.getConfirmCapital());
			} else if (ConstantUtil.trade_purchase.equals(trade.getTradeType())
					|| ConstantUtil.trade_transIn.equals(trade.getTradeType())
					|| ConstantUtil.trade_subscribe.equals(trade.getTradeType())) {
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
		
		illiquidHoldDao.save(entity);
	}
	
	/**
	 * 定时任务自动计算每日估值
	 */
	@Transactional
	public void sysValuations() {
		// 获取持仓列表
		List<IlliquidHoldEntity> list = illiquidHoldDao.findAll();
		if (!list.isEmpty()) {
			for (IlliquidHoldEntity entity : list) {
				illiquidValuationsService.insert(entity, ConstantUtil.calc_system);
			}
		}
	}
}
