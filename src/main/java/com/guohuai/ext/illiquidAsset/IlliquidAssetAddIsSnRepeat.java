package com.guohuai.ext.illiquidAsset;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.basic.component.proactive.ProActive;
import com.guohuai.basic.component.proactive.SingleProActive;

/**
 * 判断标的编号是否重复
 * 
 * @author pangbo
 *
 */
@SingleProActive
public interface IlliquidAssetAddIsSnRepeat extends ProActive{
	/**
	 * 判断标的编号是否重复
	 * @param sn
	 * @user pangbo
	 * @return
	 */
	public IlliquidAsset isSnRepeat(String sn);
	
}
