package com.guohuai.ams.illiquidAsset.repaymentPlan;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IlliquidAssetRepaymentPlanResp  extends BaseResp {

	public IlliquidAssetRepaymentPlanResp(IlliquidAssetRepaymentPlan liquidAsset){
		super();
		this.data = liquidAsset;
	}
	private IlliquidAssetRepaymentPlan data;
}
