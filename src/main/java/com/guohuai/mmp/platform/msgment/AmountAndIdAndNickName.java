package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;

@lombok.Data
public class AmountAndIdAndNickName {

	private String nickname;
	private String userOid;
	private BigDecimal orderAmount;
}
