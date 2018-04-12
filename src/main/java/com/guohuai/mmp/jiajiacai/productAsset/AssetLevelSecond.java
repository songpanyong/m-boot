package com.guohuai.mmp.jiajiacai.productAsset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetLevelSecond {
	private BigDecimal amount = BigDecimal.ZERO;
	private String assetType;
	private BigDecimal assetRate;
	private String typeName;
	
	private List<AssetLevelThird> listThird = new ArrayList<AssetLevelThird>();

}