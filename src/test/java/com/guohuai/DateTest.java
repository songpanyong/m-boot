package com.guohuai;

import java.io.IOException;
import java.sql.Timestamp;

import com.guohuai.component.util.DateUtil;

public class DateTest {

	public static void main(String[] args) throws IOException {
//		String aa = "20171223";
//		String bb = DateUtil.beforeDate(aa); 
//		System.out.print(bb);
		//System.out.print(DateUtil.parseToSqlDateTime(DateUtil.getDaySysBeginTime(new Timestamp(System.currentTimeMillis()))));
		//System.out.println(DateUtil.getSqlCurrentDate());
			System.out.println(DateUtil.currentTime());		   
	}
}
