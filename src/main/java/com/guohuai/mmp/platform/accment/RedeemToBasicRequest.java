package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 账户交易请求参数
* @ClassName: RedeemToBasicRequest 
* @Description: 
* @author longyunbo
* @date 2016年11月8日 上午10:10:41 
*
 */
@Data
public class RedeemToBasicRequest {
	
	private String userOid;
	
	private BigDecimal balance;	
}
