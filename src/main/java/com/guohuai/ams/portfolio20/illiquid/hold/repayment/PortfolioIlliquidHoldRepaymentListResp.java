package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

import java.util.List;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PortfolioIlliquidHoldRepaymentListResp extends PageResp<PortfolioIlliquidHoldRepaymentResp> {

	public PortfolioIlliquidHoldRepaymentListResp(List<PortfolioIlliquidHoldRepaymentEntity> list) {
		super.setTotal(list.size());

		boolean paidable = true;

		PortfolioIlliquidHoldRepaymentResp paidableRepayment = null;

		for (PortfolioIlliquidHoldRepaymentEntity r : list) {
			PortfolioIlliquidHoldRepaymentResp repayment = new PortfolioIlliquidHoldRepaymentResp(r);
			if (PortfolioIlliquidHoldRepaymentEntity.STATE_AUDIT.equals(r.getState())) {
				paidable = false;
			}
			if (PortfolioIlliquidHoldRepaymentEntity.STATE_PAYING.equals(r.getState()) && null == paidableRepayment) {
				repayment.setPaidable(true);
				paidableRepayment = repayment;
			}
			super.getRows().add(repayment);
		}

		if (false == paidable && null != paidableRepayment) {
			paidableRepayment.setPaidable(false);
		}
	}

}
