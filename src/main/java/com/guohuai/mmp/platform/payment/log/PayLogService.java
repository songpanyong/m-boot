package com.guohuai.mmp.platform.payment.log;

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

/**
 * 推广平台-请求日志信息
 * 
 * @author wanglei
 *
 */
@Service
public class PayLogService {

	@Autowired
	private PayLogDao payLogDao;
	
	

	/** 创建日志对象 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public PayLogEntity createEntity(PayLogReq req) {
		PayLogEntity payLogEntity = new PayLogEntity();
		payLogEntity.setInterfaceName(req.getInterfaceName());// 接口名称
		payLogEntity.setErrorCode(req.getErrorCode());// 错误码
		payLogEntity.setErrorMessage(req.getErrorMessage());// 错误消息
		payLogEntity.setSendedTimes(req.getSendedTimes());// 已发送次数
		payLogEntity.setContent(req.getContent()); // 发送内容
		payLogEntity.setOrderCode(req.getOrderCode());
		payLogEntity.setIPayNo(req.getIPayNo());
		payLogEntity.setLimitSendTimes(PayInterface.getTimes(req.getInterfaceName()));
		payLogEntity.setNextNotifyTime(getNextNotifyTime(payLogEntity));
		payLogEntity.setHandleType(req.getHandleType());
		return this.save(payLogEntity);
	}
	
	public Timestamp getNextNotifyTime(PayLogEntity payLogEntity) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, payLogEntity.getSendedTimes() * payLogEntity.getSendedTimes());
		return new Timestamp(cal.getTimeInMillis());
	}


	protected PayLogEntity save(PayLogEntity entity) {
		return this.payLogDao.save(entity);
	}

	public List<PayLogEntity> getResendEntities(String lastOid) {
		return this.payLogDao.getResendEntities(lastOid);
	}

	public void batchUpdate(List<PayLogEntity> entities) {
		this.payLogDao.save(entities);
		
	}
	
	public List<PayLogEntity> findAll(Specification<PayLogEntity> spec){
		List<PayLogEntity> users = this.payLogDao.findAll(spec);	
		return users;
	}
	
	public Page<PayLogEntity> findPage(Specification<PayLogEntity> spec, Pageable pageable){
		Page<PayLogEntity> users = this.payLogDao.findAll(spec, pageable);	
		return users;
	}

	public PageResp<PayLogQueryRep> mng(Specification<PayLogEntity> spec, Pageable pageable) {
		Page<PayLogEntity> cas = this.payLogDao.findAll(spec, pageable);
		PageResp<PayLogQueryRep> pagesRep = new PageResp<PayLogQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PayLogEntity entity : cas) {
				PayLogQueryRep queryRep = new PayLogQueryRep();
				queryRep.setInterfaceName(entity.getInterfaceName());
				queryRep.setOrderCode(entity.getOrderCode());
				queryRep.setIPayNo(entity.getIPayNo());
				queryRep.setHandleType(entity.getHandleType());
				queryRep.setHandleTypeDisp(handleTypeEn2Ch(entity.getHandleType()));
				queryRep.setErrorCode(entity.getErrorCode());
				queryRep.setErrorMessage(entity.getErrorMessage());
				
				queryRep.setSendedTimes(entity.getSendedTimes());
				queryRep.setLimitSendTimes(entity.getLimitSendTimes());
				queryRep.setNextNotifyTime(entity.getNextNotifyTime());
				queryRep.setContent(entity.getContent());
				queryRep.setCreateTime(entity.getCreateTime());
				queryRep.setUpdateTime(entity.getUpdateTime());

				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	
	
	private String handleTypeEn2Ch(String handleType) {
		if (PayLogEntity.PAY_handleType_applyCall.equals(handleType)) {
			return "支付申请";
		}
		if (PayLogEntity.PAY_handleType_notify.equals(handleType)) {
			return "支付回调";
		}
		return handleType;
	}

	public PayLogEntity getSuccessPayAplly(String iPayNo) {
		return this.payLogDao.getSuccessPayAplly(iPayNo);
	}
	
	public PayLogEntity getSuccessPayApllyByOrderCode(String OrderCode) {
		return this.payLogDao.getSuccessPayApllyByOrderCode(OrderCode);
	}

	public PayLogEntity getPaySuccessNotify(String iPayNo) {
		return this.payLogDao.getPaySuccessNotify(iPayNo);
	}

	public PayLogEntity findByOrderCodeAndHandleType(String orderCode, String handleType) {
		return this.payLogDao.findByOrderCodeAndHandleType(orderCode, handleType);
	}
	

	
}
