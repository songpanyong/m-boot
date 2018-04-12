package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;
@lombok.Data
public class NicknameAndTimeAndAmount {
	
	private String phone;
	private String nickname;

	private Timestamp orderTime;
	
	private BigDecimal fee;
	private BigDecimal orderAmount;
	private BigDecimal preAmount;
}
