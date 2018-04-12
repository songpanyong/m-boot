package com.guohuai.mmp.platform.accment;

import java.util.List;

@lombok.Data
public class BatchPayRequest {
//	publisherUserOid		String	Y	发行人ID
//	requestNo		String	Y	请求批次流水号
//	systemSource		String	Y	来源系统类型，传mimosa
//	orderList		List<AccountOrderDto>	Y	赎回申请单
	
	private String memberId;
	
	private String requestNo;
	
	private String systemSource;
	
	private List<BatchPayDto> orders;
	
	

}
