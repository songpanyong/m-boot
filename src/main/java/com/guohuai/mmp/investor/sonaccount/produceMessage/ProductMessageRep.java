package com.guohuai.mmp.investor.sonaccount.produceMessage;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.guohuai.ams.dict.Dict;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.product.Product;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.sys.SysConstant;

@lombok.Data
public class ProductMessageRep extends BaseResp {

	
	/**
	 * 产品名称
	 */
	private String name;

	/**
	 * 产品全称
	 */
	private String fullName;


	/** 产品类型 */
	private String type;
	
	/**产品类型的描述*/
	private String typeDesc;

	/**
	 * 预期年化收益率
	 */
	private String expAror ;

	/**
	 * 募集开始日
	 */
	private Date raiseStartDate;
	
	/**
	 * 募集结束日期
	 */
	private Date raiseEndDate;

	/**
	 * 募集总份额(产品可售头寸)
	 */
	private BigDecimal raisedTotalNumber = new BigDecimal(0);

	/**
	 * 募集期
	 */
	private Integer raisePeriodDays = 0;
	
	/**
	 * 存续期
	 */
	private Integer durationPeriodDays = 0;

	/**
	 * 申购确认日
	 */
	private Integer purchaseConfirmDays = 0;

	/**
	 * 单位份额净值
	 */
	private BigDecimal netUnitShare = new BigDecimal(0);
	/**
	 * 单笔投资最低份额
	 */
	private BigDecimal investMin = new BigDecimal(0);
	
	/**
	 * 单笔投资追加份额
	 */
	private BigDecimal investAdditional = new BigDecimal(0);
	
	
	/**
	 * 产品说明
	 */
	private String instruction;

	/**
	 * 投资标的
	 */
	private String investComment;
	
	/**
	 * 风险等级
	 */
	private String riskLevel;
	
	/**
	 * 产品的创建时间
	 */
	private Timestamp createTime;

	/**
	 * 产品的更新时间
	 */
	private Timestamp updateTime;
	
	/**
	 * 收益处理方式
	 */
	private String incomeDealType = Product.PRODUCT_incomeDealType_reinvest;
	
	/**
	 * 单笔赎回追加份额
	 */
	private BigDecimal additionalRredeem = new BigDecimal(0);


}
