package com.guohuai.ams.product;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductLogService {
	
	@Autowired
	private ProductLogDao productLogDao;

	public List<ProductLog> findByProduct(Product product) {
		return this.productLogDao.findByProduct(product);
	}

	
	public String auditTypeEn2Ch(String auditType) {
		if (ProductLog.AUDIT_TYPE_Auditing.equals(auditType)) {
			return "审核";
		}
		if (ProductLog.AUDIT_TYPE_Reviewing.equals(auditType)) {
			return "复核";
		}
		if (ProductLog.AUDIT_TYPE_Approving.equals(auditType)) {
			return "准入";
		}
		return auditType;
	}
	
	public String auditStateEn2Ch(String auditState) {
		if (ProductLog.AUDIT_STATE_Commited.equals(auditState)) {
			return "提交";
		}
		if (ProductLog.AUDIT_STATE_Approval.equals(auditState)) {
			return "批准";
		}
		if (ProductLog.AUDIT_STATE_Reject.equals(auditState)) {
			return "驳回";
		}
		if (ProductLog.AUDIT_STATE_Invalid.equals(auditState)) {
			return "失效";
		}
		return auditState;
	}



}
