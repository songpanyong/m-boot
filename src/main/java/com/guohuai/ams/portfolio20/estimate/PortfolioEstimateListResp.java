package com.guohuai.ams.portfolio20.estimate;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PortfolioEstimateListResp extends PageResp<PortfolioEstimateResp>{

	public PortfolioEstimateListResp() {
		super();
	}

	public PortfolioEstimateListResp(Page<PortfolioEstimateEntity> entity) {
		
		if (null != entity ) {
			super.setTotal(entity.getTotalElements());
			if (null != entity.getContent() && entity.getContent().size() > 0) {
				for (PortfolioEstimateEntity estimate : entity.getContent() ) {
					super.getRows().add(new PortfolioEstimateResp(estimate));
				}
			}
		}
	}

}
