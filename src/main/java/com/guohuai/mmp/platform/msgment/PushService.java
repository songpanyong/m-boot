package com.guohuai.mmp.platform.msgment;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.api.cms.CmsApi;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.platform.msgment.log.MsgLogReq;
import com.guohuai.mmp.platform.msgment.log.MsgLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PushService {
	
	@Autowired
	private MsgLogService msgLogService;
	@Autowired
	private MsgUtil msgUtil;
	@Autowired
	private CmsApi cmsApi; 
	
	
	
	/**
	 * 产品成立进入存续期	计息提醒	interest	["产品名称"，"收益位置链接"]	计息提醒	您投资的{1}理财产品开始计息！详情请查看{2}。
	 */
	public void interest(InterestPushReq ireq) {
		this.interest(ireq, true);
	}
	
	public void interest(InterestPushReq ireq, boolean isLog) {
		BaseResp irep = new BaseResp();
		try {
			
			irep = this.cmsApi.sendPush(ireq.getUserOid(), MsgParam.pushInterest.toString(), msgUtil.assembleMsgParams(ireq.getProductName()));
		} catch (Exception e) {
			log.error("interest interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.pushInterest.getInnerFaceName());
		}
	}
	
	
	private void setIrepException(BaseResp irep, Exception e) {
		irep.setErrorCode(-1);
		irep.setErrorMessage(AMPException.getStacktrace(e));
	}

	private void setIrep(BaseResp orep, BaseResp irep) {
		if (null == orep) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("返回为空");
		} else if (orep.getErrorCode() != 0) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(orep.getErrorMessage());
		}
	}

	private <T> void writeLog(BaseResp irep, T ireq, String interfaceName) {
		MsgLogReq req = new MsgLogReq();
		
		req.setInterfaceName(interfaceName);
		req.setSendedTimes(1);
		req.setSendObj(JSONObject.toJSONString(ireq));
		req.setErrorCode(irep.getErrorCode());
		req.setErrorMessage(irep.getErrorMessage());
		msgLogService.createEntity(req);
	}
	
	
	
	
}
