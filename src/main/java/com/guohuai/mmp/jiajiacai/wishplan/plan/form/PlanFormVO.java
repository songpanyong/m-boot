package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanFormVO extends BaseResp {
	public final static int TOUR_EDU_ONCE = 1;
	public final static int TOUR_EDU_MONTH = 2;
	public final static int SALARY_MONTH = 3;
	private String oid;
	private String planListOid;
	private String planListName;
	private String planType;
	private String status;
	private Timestamp startTime;
	private Timestamp endTime;
	private int amount; // 对于一次性投资 是 总投资， 对于按月定投的是 每月投资额
	private BigDecimal depositAmount;
	private String expectedAmount;
	private int category;
	private int investDuration;

	public void convertStatus(String status, String type) {
		if (type.equals(InvestTypeEnum.MonthSalaryInvest.getCode())
				|| type.equals(InvestTypeEnum.MonthEduInvest.getCode())
				|| type.equals(InvestTypeEnum.MonthTourInvest.getCode())) {
			if (PlanStatus.STOP.getCode().equals(status) || PlanStatus.REDEEMING.getCode().equals(status)) {
				this.status = PlanStatus.STOP.getCode();
			} else {
				this.status = status;
			}
		} else {
			// READY, SUCCESS, DEPOSITED, TODEPOSIT, REDEEMING, FAILURE,
			// COMPLETE
			if (PlanStatus.SUCCESS.getCode().equals(status) || PlanStatus.DEPOSITED.getCode().equals(status)
					|| PlanStatus.TODEPOSIT.getCode().equals(status)) {
				this.status = PlanStatus.READY.getCode();
			} else {
				this.status = status;
			}
		}
	}
}
