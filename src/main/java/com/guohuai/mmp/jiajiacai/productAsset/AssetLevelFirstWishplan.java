package com.guohuai.mmp.jiajiacai.productAsset;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetLevelFirstWishplan {
	private BigDecimal amount;
	private String wishplanName;
	private BigDecimal wishplanRate;
	private String wishplanOid;
	private String wishplanType;

}