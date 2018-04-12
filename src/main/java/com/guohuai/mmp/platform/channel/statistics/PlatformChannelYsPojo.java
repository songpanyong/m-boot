package com.guohuai.mmp.platform.channel.statistics;

import java.math.BigDecimal;

import com.guohuai.mmp.sys.SysConstant;

import lombok.Data;

@Data
public class PlatformChannelYsPojo {
	private String channelOid;
	
	private BigDecimal todayInvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	
	
}
