package com.guohuai.mmp.investor.baseaccount.log;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.PageResp;

@Service
@Transactional
public class CouponLogService {
	
	@Autowired
	CouponLogDao couponLogDao;
	

	/** 创建日志对象 */
	public CouponLogEntity createEntity(CouponLogReq req) {
		CouponLogEntity logEntity = new CouponLogEntity();
		logEntity.setSendedTimes(0);// 已发送次数
		logEntity.setType(req.getType()); //设置类型
		logEntity.setUserOid(req.getUserOid()); // 用户
		logEntity.setLimitSendTimes(3);//最多发送次数
		logEntity.setNextNotifyTime(getNextNotifyTime(logEntity));
		logEntity.setStatus(CouponLogEntity.STATUS_FAILED);
		return this.couponLogDao.save(logEntity);
	}
	
	public Timestamp getNextNotifyTime(CouponLogEntity entity) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, entity.getSendedTimes() * 2);
		return new Timestamp(cal.getTimeInMillis());
	}


	public List<String> getCouponLogEntity() {
		return this.couponLogDao.getCouponLogEntity();
	}
	
	public void batchUpdate(List<CouponLogEntity> entites) {
		couponLogDao.save(entites);
	}

	public PageResp<CouponLogPojo> findCouponLog(Specification<CouponLogEntity> spec, Pageable pageable) {
		Page<CouponLogEntity> list = this.couponLogDao.findAll(spec, pageable);
		PageResp<CouponLogPojo> pageResp = new PageResp<CouponLogPojo>();
		for (CouponLogEntity entity : list) {
			CouponLogPojo rep = new CouponLogPojo();
			rep.setCreateTime(entity.getCreateTime());
			rep.setLimitSendTimes(entity.getLimitSendTimes());
			rep.setNextNotifyTime(entity.getNextNotifyTime());
			rep.setOid(entity.getOid());
			rep.setSendedTimes(entity.getSendedTimes());
			rep.setStatus(entity.getStatus());
			rep.setType(entity.getType());
			rep.setUpdateTime(entity.getUpdateTime());
			rep.setUserOid(entity.getUserOid());
			pageResp.getRows().add(rep);
		}
		pageResp.setTotal(list.getTotalElements());
		return pageResp;
	}

	public CouponLogEntity findByOid(String oid) {
		return this.couponLogDao.findOne(oid);
	}

	public CouponLogEntity saveEntity(CouponLogEntity entity) {
		return this.couponLogDao.save(entity);
		
	}
}
