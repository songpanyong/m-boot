package com.guohuai.mmp.jiajiacai.common.constant;

public enum UserRiskLevelEnum {

	LEVEL_1("温和型", 1), LEVEL_2("稳健型", 2), LEVEL_3("平衡型", 3), LEVEL_4("积极型", 4), LEVEL_5("冒险型", 5),;

	private String name;
	private int code;

	private UserRiskLevelEnum(String name, int code) {
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

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
