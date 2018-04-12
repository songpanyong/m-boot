package com.guohuai.mmp.platform.payment;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DepositOrderQueryResponse extends BaseResp {
//	userOid		String	用户ID
//	orderNo		String	订单号
//	status			String	三方返回交易状态，S成功，F失败;returnCode返回为失败时，status为空
//	returnCode		String	结果码0000调用成功 其他为失败
//	errorMessage		String	返回描述
	
	private String orderStatus;
}
