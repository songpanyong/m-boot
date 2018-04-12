package com.guohuai.mmp.sms;

import java.sql.Date;

@lombok.Data
public class InvestorSMSMessageRedisInfo {

	//短信发送次数
	private int sMsSendTimes;
	
	//最近一次短信的发送时间
	private Date lastestSendTime;
}
