package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;

@lombok.Data
public class AmountAndIdAndNickNameAndTime {
	private String nickname;
	private String userOid;
	private BigDecimal orderAmount;
	private Timestamp time;
	private BigDecimal fee;
	private BigDecimal preAmount;
	
}
