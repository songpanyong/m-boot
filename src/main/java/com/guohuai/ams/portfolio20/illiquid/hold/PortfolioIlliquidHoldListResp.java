package com.guohuai.ams.portfolio20.illiquid.hold;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PortfolioIlliquidHoldListResp extends PageResp<PortfolioIlliquidHoldResp> {

	public PortfolioIlliquidHoldListResp() {
		super();
	}

	public PortfolioIlliquidHoldListResp(Page<PortfolioIlliquidHoldEntity> portfolioOrder) {

		if (null != portfolioOrder) {
			super.setTotal(portfolioOrder.getTotalElements());
			if (null != portfolioOrder.getContent() && portfolioOrder.getContent().size() > 0) {
				for (PortfolioIlliquidHoldEntity order : portfolioOrder.getContent()) {
					super.getRows().add(new PortfolioIlliquidHoldResp(order));
				}
			}
		}
	}

}
