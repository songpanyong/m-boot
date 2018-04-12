package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;

@lombok.Data
public class AmountAndNicknameAndRealAmount {

	private String userOid;
	private String nickname;
	private Timestamp  time;
	private BigDecimal realAmount;
}
