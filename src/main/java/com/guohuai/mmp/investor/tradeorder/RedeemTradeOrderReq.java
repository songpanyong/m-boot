package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class RedeemTradeOrderReq {

	/**
	 * 所属理财产品
	 */
	@NotBlank(message = "所属理财产品OID不能为空 ")
	String productOid;

	/**
	 * 赎回金额
	 */
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	@NotNull(message = "金额不能为空")
	BigDecimal orderAmount;
	
	/**
	 * 实际到账金额
	 */
	BigDecimal payAmount;
	
	String cid;
	
	String ckey;

	String uid;
	
	/**
	 * 省份
	 */
	String province;
	/**
	 * 城市
	 */
	String city;
	/**
	 * 订单时间，补单用
	 * 为空取系统当前时间，不为空取orderTime
	 * */
	Timestamp orderTime;
	/**
	 * 订单编号，补单用
	 * 为空取系统自动生成，不为空取orderCode
	 */
	String orderCode;
	
	/** 
	 * The order type of origin branch, the default is plain
	  */
	String planRedeemOid;
}
