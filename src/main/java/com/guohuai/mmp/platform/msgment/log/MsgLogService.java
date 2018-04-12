package com.guohuai.mmp.platform.msgment.log;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.mmp.platform.msgment.MsgParam;

/**
 *
 */
@Service
public class MsgLogService {

	@Autowired
	private MsgLogDao msgLogDao;
	
	

	/** 创建日志对象 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public MsgLogEntity createEntity(MsgLogReq req) {
		MsgLogEntity msgLogEntity = new MsgLogEntity();
		msgLogEntity.setInterfaceName(req.getInterfaceName());// 接口名称
		msgLogEntity.setErrorCode(req.getErrorCode());// 错误码
		msgLogEntity.setErrorMessage(req.getErrorMessage());// 错误消息
		msgLogEntity.setSendedTimes(req.getSendedTimes());// 已发送次数
		msgLogEntity.setSendObj(req.getSendObj()); // 发送内容
		msgLogEntity.setLimitSendTimes(MsgParam.getTimes(req.getInterfaceName()));
		msgLogEntity.setNextNotifyTime(getNextNotifyTime(msgLogEntity));
		//Record the object oid
		msgLogEntity.setObjectOid(req.getObjectOid());
		return this.save(msgLogEntity);
	}
	
	public Timestamp getNextNotifyTime(MsgLogEntity msgLogEntity) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, msgLogEntity.getSendedTimes() * msgLogEntity.getSendedTimes());
		return new Timestamp(cal.getTimeInMillis());
	}


	protected MsgLogEntity save(MsgLogEntity entity) {
		return this.msgLogDao.save(entity);
	}

	public List<MsgLogEntity> getResendEntities(String lastOid) {
		return this.msgLogDao.getResendEntities(lastOid);
	}

	public void batchUpdate(List<MsgLogEntity> entities) {
		this.msgLogDao.save(entities);
		
	}

	public PageResp<MsgLogQueryRep> mng(Specification<MsgLogEntity> spec, Pageable pageable) {
		Page<MsgLogEntity> cas = this.msgLogDao.findAll(spec, pageable);
		PageResp<MsgLogQueryRep> pagesRep = new PageResp<MsgLogQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (MsgLogEntity entity : cas) {
				MsgLogQueryRep queryRep = new MsgLogQueryRep();
				queryRep.setInterfaceName(entity.getInterfaceName());
				queryRep.setErrorCode(entity.getErrorCode());
				queryRep.setErrorMessage(entity.getErrorMessage());
				
				queryRep.setSendedTimes(entity.getSendedTimes());
				queryRep.setLimitSendTimes(entity.getLimitSendTimes());
				queryRep.setNextNotifyTime(entity.getNextNotifyTime());
				queryRep.setSendObj(entity.getSendObj());
				queryRep.setCreateTime(entity.getCreateTime());
				queryRep.setUpdateTime(entity.getUpdateTime());

				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	

	
}
