package com.guohuai.mmp.platform.finance.modifyorder;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;

@Service
@Transactional
public class ModifyOrderNewService {
	@Autowired
	ModifyOrderDao modifyOrderDao;

	@Transactional(value = TxType.REQUIRES_NEW)
	public void deleteByCheckOid(String checkOid) {
		modifyOrderDao.deleteByCheckOid(checkOid);
	}
	/**
	 * 修改补帐单的处理状态为已处理
	 * 处理中改成已处理
	 * @param orderCode
	 * @return
	 */
	public int updateDealStatusDealtByOrderCode(String orderCode) {
		int i = this.modifyOrderDao.updateDealStatusDealtByOrderCode(orderCode);
		if (i < 1) {
			throw new AMPException("对账状态非处理中");
		}
		return i;
	}
	/**
	 * 修改补帐单的处理状态为处理中
	 * 待处理改成处理中
	 * @param orderCode
	 * @return
	 */
	public int updateDealStatusDealingByOrderCode(String orderCode) {
		int i = this.modifyOrderDao.updateDealStatusDealingByOrderCode(orderCode);
		if (i < 1) {
			throw new AMPException("对账状态非待处理");
		}
		return i;
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public int updateDealStatusDealingByOrderCodeNewTrans(String orderCode) {
		return updateDealStatusDealingByOrderCode(orderCode);
	}
	// @Transactional(value=TxType.REQUIRES_NEW)
	// public void updateDealStatus(String oid,String dealStatus) {
	// this.modifyOrderDao.updateDealStatus(oid,dealStatus);
	// }

}
