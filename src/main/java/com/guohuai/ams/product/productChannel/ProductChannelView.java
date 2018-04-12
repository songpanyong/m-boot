package com.guohuai.ams.product.productChannel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.product.Product;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductChannelView {

	
	public ProductChannelView(ProductChannel c) {
		this.oid = c.getOid();
		this.channelOid = c.getChannel().getOid();
		this.cid = c.getChannel().getCid();
		this.ckey = c.getChannel().getCkey();
		this.channelName = c.getChannel().getChannelName();
		this.productOid = c.getProduct().getOid();
		this.productName = c.getProduct().getName();
		this.productCode = c.getProduct().getCode();
		this.productStatus = c.getProduct().getState();
		this.publishStatus = c.getStatus();
		this.productType = c.getProduct().getType().getOid();
		this.productTypeName = c.getProduct().getType().getName();
		this.marketState = c.getMarketState();
		this.rackTime = c.getRackTime()!=null?DateUtil.formatDatetime(c.getRackTime().getTime()):"";
		this.downTime = c.getDownTime()!=null?DateUtil.formatDatetime(c.getDownTime().getTime()):"";
		
		this.durationPeriodDays = c.getProduct().getDurationPeriodDays();
		this.expArorDisp = DecimalUtil.zoomOut(c.getProduct().getExpAror(), 100).toString() + "~" + DecimalUtil.zoomOut(c.getProduct().getExpArorSec(), 100).toString();
		this.raisedTotalNumber = c.getProduct().getRaisedTotalNumber();
		this.assetPoolName = c.getProduct().getPortfolio().getName();
		Product p = c.getProduct();
		if (null != p.getPortfolio()) {
			PortfolioEntity ap = p.getPortfolio();
			BigDecimal cashPosition = ap.getCashPosition() == null ? BigDecimal.ZERO : ap.getCashPosition();
			BigDecimal liquidDimensions = ap.getLiquidDimensions() == null ? BigDecimal.ZERO : ap.getLiquidDimensions();
			this.hqla = cashPosition.add(liquidDimensions).setScale(2, RoundingMode.HALF_UP);
		}
		this.isOpenPurchase = p.getIsOpenPurchase();
		this.isOpenRemeed = p.getIsOpenRemeed();
	}
	
	private String oid;
	private String channelOid;
	private String cid;
	private String ckey;
	private String channelName;
	private String productOid;
	private String productName;
	private String productCode;
	
	private String productType;
	private String productTypeName;
	private String productStatus;
	private String marketState;
	private String rackTime;// 上架时间
	private String downTime;// 下架时间
	
	private String publishStatus;
	
	/**
	 * 期限(存续期天数)
	 */
	private Integer durationPeriodDays;
	
	/**
	 * 预期年化
	 */
	private String expArorDisp;
	
	
	/**
	 * 募集总份额(产品可售头寸)
	 */
	private BigDecimal raisedTotalNumber;
	
	/**
	 * 资产池名称
	 */
	private String assetPoolName;
	
	/**
	 * 流动性资产
	 */
	private BigDecimal hqla;
	
	/**
	 * 开放申购期
	 */
	private String isOpenPurchase;
	/**
	 * 开放赎回期
	 */
	private String isOpenRemeed;
}
