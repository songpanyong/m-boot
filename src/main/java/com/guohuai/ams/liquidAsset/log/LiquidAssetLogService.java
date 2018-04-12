package com.guohuai.ams.liquidAsset.log;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.guohuai.ams.enums.CashToolEventType;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAssetDao;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class LiquidAssetLogService {

	@Autowired
	private LiquidAssetLogDao liquidAssetLogDao;
	@Autowired
	private LiquidAssetDao liquidAssetDao;


	/**
	 * 添加现金标的操作日志
	 * @param cashtoolOid
	 * @param eventType
	 * @param operator
	 * @return
	 */
	public LiquidAssetLog saveLiquidAssetLog(String liquidAssetOid, CashToolEventType eventType, String operator) {
		if (StringUtils.isBlank(liquidAssetOid))
			throw AMPException.getException("现金管理工具id不能为空");
		LiquidAsset liquidAsset = this.liquidAssetDao.findOne(liquidAssetOid);
		if (null == liquidAsset)
			throw AMPException.getException("找不到id为[" + liquidAssetOid + "]的现金管理工具");

		return this.saveLiquidAssetLog(liquidAsset, eventType, operator);
	}
	
	/**
	 * 添加现金管理工具操作日志
	 * @param cashTool
	 * @param eventType
	 * @param operator
	 * @return
	 */
	public LiquidAssetLog saveLiquidAssetLog(LiquidAsset liquidAsset, CashToolEventType eventType, String operator) {
		if(null == eventType)
			throw AMPException.getException("操作类型不能为空!");
		LiquidAssetLog entity = LiquidAssetLog.builder().liquidAsset(liquidAsset).eventTime(DateUtil.getSqlCurrentDate())
				.eventType(eventType.name()) // 用name吧 直观一点
//				.eventType(eventType.ordinal() + "")
//				.eventType(eventType.getCode())
				.operator(operator)
				.build();
		return liquidAssetLogDao.save(entity);
	}
	
	
}
