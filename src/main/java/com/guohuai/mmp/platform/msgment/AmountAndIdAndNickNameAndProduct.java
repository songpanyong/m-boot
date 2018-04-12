package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;

@lombok.Data
public class AmountAndIdAndNickNameAndProduct {

	private String nickname;
	private String userOid;
	private BigDecimal orderAmount;
	private String productName;
}
