package com.guohuai.mmp.ope.distribution;


import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class DistributionResp{
	
	/** 注册渠道 */
	private String source;
	/** 注册数量 */
	private Integer registerNum;
	/** 首次绑定数量 */
	private Integer bindNum;
	/** 首次充值数量 */
	private Integer rechargeNum;
	/** 首次购买数量 */
	private Integer buyNum;
	
	private Date time;
}
