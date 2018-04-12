package com.guohuai.ams.portfolio20.order;

import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MarketOrderListResp extends PageResp<MarketOrderResp>{

	public MarketOrderListResp() {
		super();
	}

	public MarketOrderListResp(Page<MarketOrderEntity> marketOrder) {
		
		if (null != marketOrder ) {
			super.setTotal(marketOrder.getTotalElements());
			if (null != marketOrder.getContent() && marketOrder.getContent().size() > 0) {
				for (MarketOrderEntity order : marketOrder.getContent() ) {
					super.getRows().add(new MarketOrderResp(order));
				}
			}
		}
	}
	public MarketOrderListResp(List<MarketOrderEntity> marketOrder,long total){
		super.total = total;
		if(null != marketOrder && marketOrder.size()>0){
			for(MarketOrderEntity m:marketOrder){
				super.rows.add(new MarketOrderResp(m));
			}
		}
	}

}
