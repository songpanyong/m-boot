package com.guohuai.mmp.jiajiacai.productAsset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor

public class FirstLevelRep<T> extends BaseResp {

	List<T> rows = new ArrayList<T>();

	public void add(T e) {
		rows.add(e);
	}
	private BigDecimal totalVolume;

}