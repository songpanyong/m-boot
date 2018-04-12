package com.guohuai.mmp.sms.notify;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class SMSNotifyService {

	@Autowired
	private SMSNotifyDao sMSNotifyDao;
	
	/**
	 * 新增
	 * @param entity
	 * @return
	 */
	public SMSNotifyEntity saveEntity(SMSNotifyEntity entity){
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}
	
	/**
	 * 修改
	 * @param entity
	 * @return
	 */
	public SMSNotifyEntity updateEntity(SMSNotifyEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.sMSNotifyDao.save(entity);
	}
	
	/**
	 * 重发
	 * @param entity
	 * @param resp
	 * @return
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public SMSNotifyEntity updateConfirm(SMSNotifyEntity entity, BaseResp resp) {
		Timestamp now = DateUtil.getSqlCurrentDate();
		if (0 == resp.getErrorCode()) {
			entity.setNotifyStatus(SMSNotifyEntity.NOTIFY_notifyStatus_confirmed);
			entity.setNotifyConfirmedTime(now);
			entity.setErrorCode(resp.getErrorCode());
			entity.setErrorMessage(resp.getErrorMessage());
		} else {
			entity.setErrorCode(resp.getErrorCode());
			entity.setErrorMessage(resp.getErrorMessage());
		}
		entity.setNotifyTime(now);
		entity.setNotifyTimes(entity.getNotifyTimes() + 1);
		
		return this.updateEntity(entity);
	}

	/**
	 * 新建
	 * @param smsSendTypes 短信发送类型
	 * @param notifyContent 短信内容
	 * @param resp
	 * @return
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public SMSNotifyEntity createLog(String smsSendTypes, String notifyContent) {
		SMSNotifyEntity notify = new SMSNotifyEntity();
		notify.setSmsSendTypes(smsSendTypes);
		notify.setNotifyContent(notifyContent);
		notify.setNotifyStatus(SMSNotifyEntity.NOTIFY_notifyStatus_toConfirm);
		notify.setErrorCode(-1);
		return this.saveEntity(notify);
	}
	
	public List<SMSNotifyEntity> getFailedNotify() {
		return this.sMSNotifyDao.getFailedNotify();
	}
}
