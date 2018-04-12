package com.guohuai.cache.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.switchcraft.SwitchService;
import com.guohuai.cache.CacheKeyConstants;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DecimalUtil;

@Service
public class CachePlatformService {
	
	@Autowired
	private SwitchService switchService;
	@Autowired
	private RedisExecuteLogExtService redisExecuteLogExtService;
	
	/**
	 * 获取平台单日提现限额
	 */
	public void isWithdrawDayLimit(BigDecimal orderAmount) {
		BigDecimal withdrawDayLimit = switchService.getWithdrawDayLimit();
		if (DecimalUtil.isGoRules(withdrawDayLimit)) {
			String hkey = CacheKeyConstants.getPlatformCacheHkey();
			BigDecimal valOut = redisExecuteLogExtService.hincrByBigDecimal(hkey, "withdrawDayLimit", orderAmount);
			if (valOut.compareTo(withdrawDayLimit) > 0) {
				throw new AMPException("超过平台单日提现限额");
			}
		}
	}
	
	public void resetWithdrawDayLimit() {
		BigDecimal withdrawDayLimit = switchService.getWithdrawDayLimit();
		if (DecimalUtil.isGoRules(withdrawDayLimit)) {
			String hkey = CacheKeyConstants.getPlatformCacheHkey();
			redisExecuteLogExtService.redisExecuteHSET(hkey, "withdrawDayLimit", 0);
		}
	}
	
	
}
