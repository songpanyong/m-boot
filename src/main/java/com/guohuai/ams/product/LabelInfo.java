package com.guohuai.ams.product;

import com.guohuai.ams.label.LabelEntity;

@lombok.Data
public class LabelInfo {

	public LabelInfo(LabelEntity l) {
		this.labelName = l.getLabelName();
		this.labelType = l.getLabelType();
		this.isOk = l.getIsOk();
		this.labelCode = l.getLabelCode();
	}

	private String labelName;//标签名称
	private String labelType;//标签类型
	private String isOk;//是否可用
	private String labelCode;//标签码
}
