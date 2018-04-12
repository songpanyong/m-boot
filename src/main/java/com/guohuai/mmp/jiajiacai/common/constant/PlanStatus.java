package com.guohuai.mmp.jiajiacai.common.constant;
/**
 * SlaryMonth: READY, STOP   
 * @author Administrator
 *
 */
/**
 * t_plan_product_entity:FAILURE, SUCCESS, COMPLETE, TOREDEEM, REDEEMING
 * t_plan_invest:READY, SUCCESS, DEPOSITED, TODEPOSIT, REDEEMING, FAILURE, COMPLETE
 * t_month_plan: READY, STOP, REDEEMING, COMPLETE
 * @author Administrator
 *
 */
public enum PlanStatus {
	PROCEED("进行中", "PROCEED"), COMPLETE("已完成", "COMPLETE"), STOP("终止", "STOP"),  // plan status
	SUCCESS("转账", "SUCCESS"), READY("待转账", "READY"), FAILURE("失败", "FAILURE"),   // Account status
	TOREINVEST("待再投", "TOREINVEST"),
	TOREDEEM("待再赎", "TOREDEEM"),//PlanProductEntity 赎回失败
	OVERDUE("待下次划扣", "OVERDUE"),//t_plan_month_deposit 按月划扣
	DEPOSITED("转账成功", "DEPOSITED"),//一次性购买旅游、教育扣款成功
	TODEPOSIT("转账失败", "TODEPOSIT"),//一次性购买旅游、教育扣款失败
	REDEEMING("赎回中", "REDEEMING"),//赎回中
	;
	private String name;
    private String code;

    private PlanStatus(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code + "_" + this.name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
