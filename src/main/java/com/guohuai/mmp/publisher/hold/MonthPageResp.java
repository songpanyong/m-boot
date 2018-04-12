package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
public class MonthPageResp<T> extends BaseResp{

	protected long total;
	protected List<T> rows = new ArrayList<T>();
	//计划名
	protected String planListName;
		
		//计划的创建时间
	protected Timestamp createTime;
		
		//投资期限
	protected int investDuration;
		//已执行月数
	protected int totalInvestCount;
		//当前累计投资今日
	protected BigDecimal totalInvestAmount;
		//每月转入金额
	protected BigDecimal monthAmount;
		//每月转入日
	protected  int monthInvestDay;
		//计划的状态
	protected String status;
		//状态的描述
	protected String statusDesc;

	
	public MonthPageResp(Page<T> page, String planListName, Timestamp createTime, int investDuration,
			int totalInvestCount, BigDecimal totalInvestAmount, BigDecimal monthAmount, int monthInvestDay,
			String status, String statusDesc) {
		
		this(page.getTotalElements(), page.getContent(), statusDesc, createTime, monthInvestDay, monthInvestDay, monthAmount, monthAmount, monthInvestDay, statusDesc, statusDesc);

	}
}
