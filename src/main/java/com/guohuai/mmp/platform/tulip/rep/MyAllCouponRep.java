package com.guohuai.mmp.platform.tulip.rep;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.guohuai.mmp.platform.tulip.TulipConstants;
import com.guohuai.mmp.sys.SysConstant;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 推广平台券信息
 * 
 * @author wanglei
 *
 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyAllCouponRep {

	/** 卡券编号 */
	private String oid = TulipConstants.BLANK;

	/**
	 * 卡券类型(参考InvestorCouponOrderEntity中的定义)
	 * 
	 * @see InvestorCouponOrderEntity
	 */
	private String type = TulipConstants.BLANK;

	/** 卡券名称 */
	private String name = TulipConstants.BLANK;

	/** 卡券内容描述 */
	private String description = TulipConstants.BLANK;

	/** 卡券金额 */
	private BigDecimal amount = SysConstant.BIGDECIMAL_defaultValue;

	/** 生效时间 */
	private Timestamp start;

	/** 失效时间 */
	private Timestamp finish;

	/** 状态(参考TulipConstants中的定义) */
	private String status = TulipConstants.BLANK;

	/** 使用规则 */
	private String rules = TulipConstants.BLANK;

	/** 使用产品,多个产品逗号分隔 */
	private String products = TulipConstants.BLANK;
	
	/** 最小投资额,空表示无限制 */
	private BigDecimal minAmt;

	/** 加息天数/体验天数 */
	private Integer rateDays;

	/** 使用时间 */
	private Timestamp useTime;

	/** 卡券领取时间 */
	private Timestamp leadTime;
	/** 活动ID */
	private String eventId;
	/** 活动标题 */
	private String eventTitle;
}
