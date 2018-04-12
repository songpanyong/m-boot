package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;

@lombok.Data
public class NicknameAndProductAndAmount {

	private String phone;
	private String nickname;
    private String productName;
	
	
	private BigDecimal orderAmount;
}
