package com.guohuai.mmp.platform.tulip;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.platform.tulip.log.TulipLogDao;
import com.guohuai.mmp.platform.tulip.log.TulipLogEntity;
import com.guohuai.mmp.platform.tulip.log.TulipLogService;

/**
 * 推广平台-重新发送失败的请求
 * 
 */
@Service
public class TulipResendRequireNewService {

	Logger logger = LoggerFactory.getLogger(TulipResendRequireNewService.class);
	@Autowired
	private TulipService tulipService;
	@Autowired
	private TulipLogService tulipLogService;
	@Autowired
	private TulipLogDao tulipLogDao;
	
	
	
	
	
	
	/** 将发送推广平台失败的请求重新发送 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void reSendTulipMessageDo(String resendOid) {
		TulipLogEntity en = this.tulipLogService.findByOid(resendOid);
		BaseResp rep = new BaseResp();
		for (TulipLogEntity.TULIP_TYPE type : TulipLogEntity.TULIP_TYPE.values()) {
            if (type.getInterfaceCode().equals(en.getInterfaceCode())) {
                try {
                    Object obj = this.tulipService.getClass().getMethod(type.getInterfaceCode(), Class.forName(type.getIfaceReq()))
                            .invoke(this.tulipService,JSONObject.parseObject(en.getSendObj(), Class.forName(type.getIfaceReq()).newInstance().getClass()));
                } catch (Exception e) {
                    e.printStackTrace();
                } 
            }
        }
		en.setErrorCode(rep.getErrorCode());
		en.setErrorMessage(rep.getErrorMessage());
		en.setSendedTimes(en.getSendedTimes() + 1);
		en.setNextNotifyTime(this.tulipLogService.getNextNotifyTime(en));
		this.tulipLogDao.save(en);
	}

	


}
