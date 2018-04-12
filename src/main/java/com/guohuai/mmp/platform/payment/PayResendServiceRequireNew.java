package com.guohuai.mmp.platform.payment;

import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.platform.payment.log.PayInterface;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PayResendServiceRequireNew {

	@Autowired
	private PayLogService payLogService;
	@Autowired
	private Payment paymentServiceImpl;
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public String requireNew(String lastOid, List<PayLogEntity> entities) throws Exception {
		for (PayLogEntity entity : entities) {
			
			try {
				
				BaseResp irep = (BaseResp)PaymentServiceImpl.class
						.getMethod(entity.getInterfaceName(),
								Class.forName(PayInterface.getIReq(entity.getInterfaceName())), boolean.class)
						.invoke(this.paymentServiceImpl,
								JSONObject.parseObject(entity.getContent(), Class.forName(PayInterface.getIReq(entity.getInterfaceName()))), false);
				entity.setErrorCode(irep.getErrorCode());
				entity.setErrorMessage(irep.getErrorMessage());
				entity.setSendedTimes(entity.getSendedTimes() + 1);
				entity.setNextNotifyTime(this.payLogService.getNextNotifyTime(entity));
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			} 
			lastOid = entity.getOid();
		}
		this.payLogService.batchUpdate(entities);
		return lastOid;
	}
}
