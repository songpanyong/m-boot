package com.guohuai.ams.portfolio20.net.correct.order;

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
public class PortfolioNetCorrectOrderListResp extends PageResp<PortfolioNetCorrectOrderResp>{

	public PortfolioNetCorrectOrderListResp() {
		super();
	}

	public PortfolioNetCorrectOrderListResp(Page<PortfolioNetCorrectOrderEntity> Order) {
		
		if (null != Order ) {
			super.setTotal(Order.getTotalElements());
			if (null != Order.getContent() && Order.getContent().size() > 0) {
				for (PortfolioNetCorrectOrderEntity order : Order.getContent() ) {
					super.getRows().add(new PortfolioNetCorrectOrderResp(order));
				}
			}
		}
	}

}
