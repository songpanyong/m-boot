package com.guohuai.mmp.jiajiacai.productAsset;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetLevelThird {
	private BigDecimal amount;
	private String AssetName;
	private BigDecimal AssetRate;
	private String oid;

}