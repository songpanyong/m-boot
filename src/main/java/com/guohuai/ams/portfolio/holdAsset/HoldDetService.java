package com.guohuai.ams.portfolio.holdAsset;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.illiquidAsset.IlliquidAssetService;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAssetService;
import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.trade.AssetTradeEntity;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

/**
 * 投资组合分仓记录
 * @author star.zhu
 * 2016年12月28日
 */
@Service
public class HoldDetService {

	@Autowired
	private HoldDetDao holdDetDao;
	@Autowired
	private LiquidAssetService liquidAssetService;
	@Autowired
	private IlliquidAssetService illiquidAssetService;
	
	/**
	 * 新增现金类资产持仓记录
	 * @param trade
	 */
	@Transactional
	public void saveLiquid(AssetTradeEntity trade) {
		LiquidAsset liquid = liquidAssetService.findByOid(trade.getAssetOid());
		if (null == liquid) {
			throw new AMPException("未知的错误！");
		}
		HoldDetEntity entity = new HoldDetEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(trade.getPortfolio());
		entity.setAssetOid(liquid.getOid());
		entity.setAssetName(liquid.getName());
		entity.setHoldAmount(trade.getConfirmVolume());
		entity.setBuyValue(trade.getConfirmCapital());
		entity.setBuyPrice(trade.getTradePrice());
		entity.setState(ConstantUtil.hold_hold);
		entity.setCreater(trade.getConfirmer());
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		
		holdDetDao.save(entity);
	}
	
	/**
	 * 新增非现金类资产持仓记录
	 * @param trade
	 */
	@Transactional
	public void saveIlliquid(AssetTradeEntity trade) {
		IlliquidAsset illiquid = illiquidAssetService.findByOid(trade.getAssetOid());
		if (null == illiquid) {
			throw new AMPException("未知的错误！");
		}
		HoldDetEntity entity = new HoldDetEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(trade.getPortfolio());
		entity.setAssetOid(illiquid.getOid());
		entity.setAssetName(illiquid.getName());
		entity.setHoldAmount(trade.getConfirmVolume());
		entity.setBuyValue(trade.getConfirmCapital());
		entity.setBuyPrice(trade.getTradePrice());
		entity.setState(ConstantUtil.hold_hold);
		entity.setCreater(trade.getConfirmer());
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		
		holdDetDao.save(entity);
	}
	
	/**
	 * 更新持有现金类资产分仓记录
	 * @param trade
	 */
	@Transactional
	public void updateLiquid(AssetTradeEntity trade) {
		LiquidAsset liquid = liquidAssetService.findByOid(trade.getAssetOid());
		if (null == liquid) {
			throw new AMPException("未知的错误！");
		}
		// 交易余额
		BigDecimal lastVolume = trade.getConfirmVolume();
		// 获取持仓列表
		List<HoldDetEntity> list = holdDetDao.getHoldList(trade.getPortfolio().getOid(), liquid.getOid());
		
		if (!list.isEmpty()) {
			for (HoldDetEntity entity : list) {
				if (lastVolume.compareTo(entity.getHoldAmount()) > 0) {
					entity.setState(ConstantUtil.hold_fire);
					entity.setTradeAmount(entity.getHoldAmount());
					
					lastVolume = lastVolume.subtract(entity.getHoldAmount()).setScale(3, BigDecimal.ROUND_HALF_DOWN);
				} else {
					entity.setState(ConstantUtil.hold_part_fire);
					entity.setTradeAmount(lastVolume);
					
					lastVolume = BigDecimal.ZERO;
				}
				entity.setSellPrice(trade.getTradePrice());
				entity.setSellValue(entity.getTradeAmount().multiply(trade.getTradePrice()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
				entity.setDeviation(entity.getSellValue().subtract(entity.getBuyValue()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
				entity.setOperator(trade.getConfirmer());
				entity.setUpdateTime(DateUtil.getSqlCurrentDate());
				
				if (lastVolume.equals(BigDecimal.ZERO)) {
					break;
				}
			}
		}
	}
	
	/**
	 * 更新持有非现金类资产持仓记录
	 * @param trade
	 */
	@Transactional
	public void updateIlliquid(AssetTradeEntity trade) {
		IlliquidAsset illiquid = illiquidAssetService.findByOid(trade.getAssetOid());
		if (null == illiquid) {
			throw new AMPException("未知的错误！");
		}
		// 交易余额
		BigDecimal lastVolume = trade.getConfirmVolume();
		// 获取持仓列表
		List<HoldDetEntity> list = holdDetDao.getHoldList(trade.getPortfolio().getOid(), illiquid.getOid());
		
		if (!list.isEmpty()) {
			for (HoldDetEntity entity : list) {
				if (lastVolume.compareTo(entity.getHoldAmount()) > 0) {
					entity.setState(ConstantUtil.hold_fire);
					entity.setTradeAmount(entity.getHoldAmount());
					
					lastVolume = lastVolume.subtract(entity.getHoldAmount()).setScale(3, BigDecimal.ROUND_HALF_DOWN);
				} else {
					entity.setState(ConstantUtil.hold_part_fire);
					entity.setTradeAmount(lastVolume);
					
					lastVolume = BigDecimal.ZERO;
				}
				entity.setSellPrice(trade.getTradePrice());
				entity.setSellValue(entity.getTradeAmount().multiply(trade.getTradePrice()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
				entity.setDeviation(entity.getSellValue().subtract(entity.getBuyValue()).setScale(2, BigDecimal.ROUND_HALF_DOWN));
				entity.setOperator(trade.getConfirmer());
				entity.setUpdateTime(DateUtil.getSqlCurrentDate());
				
				if (lastVolume.equals(BigDecimal.ZERO)) {
					break;
				}
			}
		}
	}
}
