package com.guohuai.mmp.jiajiacai.productAsset;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetLevelFirst {
	private BigDecimal amount;
	private String productName;
	private BigDecimal productRate;
	private String productOid;

}