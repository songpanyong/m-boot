package com.guohuai.ams.liquidAsset.yield;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.component.persist.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 收益采集表
 * @author zudafu
 *
 */
@Entity
@Table(name = "T_GAM_LIQUID_ASSET_YIELD")
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class LiquidAssetYield extends UUID{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 现金类资产Oid
	 */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "liquidAssetOid", referencedColumnName = "oid")
	@JsonBackReference
	private LiquidAsset liquidAsset;
	/**
	 * 收益日
	 */
	private Date profitDate;
	/**
	 * 万份收益
	 */
	private BigDecimal dailyProfit;
	/**
	 * 7日年化收益率
	 */
	private BigDecimal weeklyYield;
	/**
	 * creator
	 */
	private String creator;
	/**
	 * 收益采集日
	 */
	private Timestamp createTime;
}

