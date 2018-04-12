package com.guohuai.ams.portfolio20.invest.losses;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午4:16:30
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class PortfolioInvestLossesListResp extends PageResp<PortfolioInvestLossesResp>{

	public PortfolioInvestLossesListResp() {
		super();
	}

	public PortfolioInvestLossesListResp(Page<PortfolioInvestLossesEntity> Order) {
		
		if (null != Order ) {
			super.setTotal(Order.getTotalElements());
			if (null != Order.getContent() && Order.getContent().size() > 0) {
				for (PortfolioInvestLossesEntity order : Order.getContent() ) {
					super.getRows().add(new PortfolioInvestLossesResp(order));
				}
			}
		}
	}

}
