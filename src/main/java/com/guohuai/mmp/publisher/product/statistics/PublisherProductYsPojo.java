package com.guohuai.mmp.publisher.product.statistics;

import java.math.BigDecimal;

import com.guohuai.mmp.sys.SysConstant;

import lombok.Data;

@Data
public class PublisherProductYsPojo {
	private String productOid;
	
	private BigDecimal todayInvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	
	
}
