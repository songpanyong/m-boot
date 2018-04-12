package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.ext.web.PageResp.PageRespBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlanPageResp<T> extends BaseResp{

	protected long total;
	protected List<T> rows = new ArrayList<T>();
	/** 筛选出的总收益 */
	protected String selectTotalIncome;
	

	public PlanPageResp(Page<T> page,String selectTotalIncome ) {
		this(page.getTotalElements(), page.getContent(),selectTotalIncome);
		
	}
}
