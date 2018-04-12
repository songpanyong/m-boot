package com.guohuai.mmp.platform.finance.check;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class PlatformFinanceCheckNewService {
	@Autowired
	private PlatformFinanceCheckDao platformFinanceCheckDao;

	@Transactional(value = TxType.REQUIRES_NEW)
	public void save(PlatformFinanceCheckEntity entity) {
		this.platformFinanceCheckDao.save(entity);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public void checkDataConfirm(String oid, String operator) {
		this.platformFinanceCheckDao.checkDataConfirm(oid, operator);
	}

	public PlatformFinanceCheckEntity findByCheckDate(Date checkDate) {
		return this.platformFinanceCheckDao.findByCheckDate(checkDate);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public int checking(String pdcOid,String operator) {
		int i = this.platformFinanceCheckDao.checking(pdcOid,operator);
		if (i < 1) {
			throw new AMPException("对账数据正在对账中或已对账");
		}
		return i;
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public int syncing(String pdcOid) {
		int i = this.platformFinanceCheckDao.syncing(pdcOid);
		if (i < 1) {
			throw new AMPException("对账数据正在同步或已同步");
		}
		return i;
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public int syncFailed(String pdcOid) {
		int i = this.platformFinanceCheckDao.syncFailed(pdcOid);
		if (i < 1) {
			throw new AMPException("更新对账同步数据状态失败时异常");
		}
		return i;
	}

	public int syncOK(String pdcOid, int totalCount) {
		int i = this.platformFinanceCheckDao.syncOK(pdcOid, totalCount);
		if (i < 1) {
			throw new AMPException("更新对账同步数据状态成功时异常");
		}
		return i;
	}
	
	public void createEntity(Timestamp beginTime,Timestamp endTime,String checkCode) throws ParseException {
		PlatformFinanceCheckEntity entity = new PlatformFinanceCheckEntity();
		entity.setCheckCode(PlatformFinanceCheckEntity.PREFIX+checkCode);
		entity.setCheckDate(new java.sql.Date(DateUtil.parse(checkCode, "yyyyMMdd").getTime()));
		entity.setCheckStatus(PlatformFinanceCheckEntity.CHECKSTATUS_TOCHECK);
		entity.setLdataStatus(PlatformFinanceCheckEntity.CHECK_ldataStatus_toPrepare);
		entity.setCheckDataSyncStatus(PlatformFinanceCheckEntity.CHECKDATASYNCSTATUS_toSync);
		entity.setGaStatus(PlatformFinanceCheckEntity.CHECK_gaStatus_toGa);
		entity.setBeginTime(beginTime);
		entity.setEndTime(endTime);
		this.platformFinanceCheckDao.save(entity);
	}
//	
//	@Transactional(value = TxType.REQUIRES_NEW)
//	public void createBankEntity(String checkCode) throws ParseException {
//			PlatformFinanceCheckEntity entity = new PlatformFinanceCheckEntity();
//			entity.setCheckCode(PlatformFinanceCheckEntity.PREFIX+checkCode.replace("-", ""));
//			entity.setCheckDate(DateUtil.parseToSqlDate(checkCode));
//			entity.setCheckStatus(PlatformFinanceCheckEntity.CHECKSTATUS_TOCHECK);
//			entity.setCheckDataSyncStatus(PlatformFinanceCheckEntity.CHECKDATASYNCSTATUS_toSync);
//			entity.setConfirmStatus(PlatformFinanceCheckEntity.CONFIRMSTATUS_NO);
//			entity.setBeginTime(DateUtil.fetchTimestamp(checkCode+" 00:00:00"));
//			entity.setEndTime(DateUtil.fetchTimestamp(checkCode+" 23:59:59"));
//			this.platformFinanceCheckDao.save(entity);
//	}

}
