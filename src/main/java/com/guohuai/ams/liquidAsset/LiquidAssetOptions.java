package com.guohuai.ams.liquidAsset;

import java.io.Serializable;

import lombok.Data;

@Data
public class LiquidAssetOptions implements Serializable {

	private static final long serialVersionUID = -4352690573472560544L;

	public LiquidAssetOptions(LiquidAsset asset) {
		this.oid = asset.getOid();
		this.sn = asset.getSn();
		this.name = asset.getName();
	}

	private String oid;
	private String sn;
	private String name;
}
