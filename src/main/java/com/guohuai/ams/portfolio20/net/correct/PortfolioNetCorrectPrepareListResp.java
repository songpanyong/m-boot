package com.guohuai.ams.portfolio20.net.correct;

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
public class PortfolioNetCorrectPrepareListResp extends PageResp<PortfolioNetCorrectPrepareResp>{

	public PortfolioNetCorrectPrepareListResp() {
		super();
	}

	public PortfolioNetCorrectPrepareListResp(Page<PortfolioNetCorrectEntity> Order) {
		
		if (null != Order ) {
			super.setTotal(Order.getTotalElements());
			if (null != Order.getContent() && Order.getContent().size() > 0) {
				for (PortfolioNetCorrectEntity order : Order.getContent() ) {
					super.getRows().add(new PortfolioNetCorrectPrepareResp(order));
				}
			}
		}
	}

}
