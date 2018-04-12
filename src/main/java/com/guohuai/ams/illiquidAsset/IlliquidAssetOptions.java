package com.guohuai.ams.illiquidAsset;

import java.io.Serializable;


import lombok.Data;

@Data
public class IlliquidAssetOptions implements Serializable {

	private static final long serialVersionUID = -4352690573472560544L;

	public IlliquidAssetOptions(IlliquidAsset asset) {
		this.oid = asset.getOid();
		this.sn = asset.getSn();
		this.name = asset.getName();
		this.type = asset.getType();
	}

	private String oid;
	private String sn; 
	private String name;
	private String type;
}
