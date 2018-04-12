package com.guohuai.ams.portfolio.form;

import org.springframework.data.domain.Page;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.basic.component.ext.web.PageResp;

public class PortfolioListResp extends PageResp<PortfolioResp>{

	public PortfolioListResp() {
		super();
	}

	public PortfolioListResp(Page<PortfolioEntity> entity) {
		if (null != entity ) {
			super.setTotal(entity.getTotalElements());
			if (null != entity.getContent() && entity.getContent().size() > 0) {
				for (PortfolioEntity order : entity.getContent() ) {
					super.getRows().add(new PortfolioResp(order));
				}
			}
		}
	}

}
