package com.guohuai.mmp.platform.accment;

import lombok.Data;

@Data
public class QueryOrdersRequest {
//	date		String	N	查询日期，格式：yyyy-MM-dd
//	根据单日查询必传
//	countNum		long	Y	分页偏移量，默认0
//	beginTime		String	Y	开始时间，格式：yyyy-MM-dd HH:mm:ss
//	endTime		String	Y	结束时间，格式：yyyy-MM-dd HH:mm:ss
	
	private String Date;
	private long countNum;
	private String beginTime;
	private String endTime;
	
}
