package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;



@lombok.Data
public class NickNameAndPhoneAndTimeAndAmount {

	private String phone;
	private String nickname;
	private Timestamp time;
	private BigDecimal realAmount;
}
