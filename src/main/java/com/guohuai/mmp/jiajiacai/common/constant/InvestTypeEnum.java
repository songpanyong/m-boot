package com.guohuai.mmp.jiajiacai.common.constant;

public enum InvestTypeEnum {
	FixedInvestment("一次性购买", "FIXED"), 
	MonthInvestment("按月投资", "MONTH"),
	OnceEduInvest("一次性教育投资", "ONCE_EDU"), 
	OnceTourInvest("一次性旅游投资", "ONCE_TOUR"),
	MonthEduInvest("按月教育投资", "MONTH_EDU"), 
	MonthTourInvest("按月旅游投资", "MONTH_TOUR"),
	MonthSalaryInvest("薪增长投资", "MONTH_SALARY");
    private String name;
    private String code;

    private InvestTypeEnum(String name, String code) {
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
