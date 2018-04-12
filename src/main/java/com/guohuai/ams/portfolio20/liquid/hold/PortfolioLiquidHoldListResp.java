package com.guohuai.ams.portfolio20.liquid.hold;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PortfolioLiquidHoldListResp extends PageResp<PortfolioLiquidHoldResp>{

	public PortfolioLiquidHoldListResp() {
		super();
	}

	public PortfolioLiquidHoldListResp(Page<PortfolioLiquidHoldEntity> portfolioOrder) {
		
		if (null != portfolioOrder ) {
			super.setTotal(portfolioOrder.getTotalElements());
			if (null != portfolioOrder.getContent() && portfolioOrder.getContent().size() > 0) {
				for (PortfolioLiquidHoldEntity order : portfolioOrder.getContent() ) {
					super.getRows().add(new PortfolioLiquidHoldResp(order));
				}
			}
		}
	}

}
