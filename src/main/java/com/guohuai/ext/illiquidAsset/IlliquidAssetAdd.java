package com.guohuai.ext.illiquidAsset;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.illiquidAsset.IlliquidAssetForm;
import com.guohuai.basic.component.proactive.ProActive;
import com.guohuai.basic.component.proactive.SingleProActive;

/**
 * 添加标的的扩展 接口
 * 
 * @author gh
 *
 */
@SingleProActive
public interface IlliquidAssetAdd extends ProActive{
	/**
	 * 佳兆业 添加标的的扩展方法
	 * @param form
	 * @param operator
	 * @return
	 */
	public IlliquidAsset saveIlliquidAssetExt(IlliquidAssetForm form, String operator);
}
