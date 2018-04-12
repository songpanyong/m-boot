package com.guohuai.mmp.investor.sonaccount;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class VadidateRep {
	
	/** 返回请求的状态码 */
	public String status;
	/** 返回请求的错误信息  */
	public String responseMsg ;
	
	public Boolean flag;
}
