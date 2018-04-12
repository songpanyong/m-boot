package com.guohuai.mmp.publisher.hold;

import java.sql.Date;
import java.sql.Timestamp;

@lombok.Data
public class PlanRelateProductInfo {

	/** 产品id */
	private String productOid;
	/** 产品名称 */
	private String productName;
	/** 产品编号 */
	private String productCode;
	/** 产品类型 */
	private String productType;
	/** 产品类型描述 */
	private String productTypeDesc;
	/** 购买时间 */
	private Timestamp investTime;
	
	/** 定期产品成立时间 */
	private String setupTime;
	/** 份额确认时间 */
	private String confirmTime;
	/** 赎回时间 */
	private String redeemTime;
	/** 赎回到账时间 */
	private String redeemToAccountTime;
	
}
