package com.guohuai.mmp.ope.distribution;


import java.util.List;

import org.springframework.data.domain.Page;
import com.guohuai.basic.component.ext.web.PageResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DistributionListResp extends PageResp<DistributionResp> {
	public DistributionListResp() {
		super();
	}

	public DistributionListResp(Page<DistributionResp> liquidAsset, List<DistributionResp> totalList) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements(), totalList);
	}

	public DistributionListResp(List<DistributionResp> liquidAsset, List<DistributionResp> totalList) {
		this(liquidAsset, liquidAsset.size(), totalList);
	}

	public DistributionListResp(List<DistributionResp> liquidAsset, long total, List<DistributionResp> totalList) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
		this.setTotalList(totalList);
	}
	
	private List<DistributionResp> totalList;
}
