package com.guohuai.mmp.publisher.investor.holdincome;

import java.math.BigDecimal;
import java.sql.Date;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.sys.SysConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的收益 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class IncomeRep extends BaseResp {

	/** 总收益 */
	private BigDecimal totalIncome = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 收益确认日期
	 */
	private Date confirmDate;

	/** 收益明细（第一页） */
	private RowsRep<MyInvestorIncomeOfDateRep> details;

}
