package com.guohuai.ams.portfolio.service;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.dao.IncomeDao;
import com.guohuai.ams.portfolio.entity.IncomeEntity;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.form.IncomeForm;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;

/**
 * 收益分配明细
 * @author star.zhu
 * 2016年12月26日
 */
@Service
public class IncomeService {

	@Autowired
	private IncomeDao incomeDao;
	
	/**
	 * 净值校准录入
	 * @param form
	 * @param portfolio
	 */
	@Transactional
	public void create(IncomeForm form, PortfolioEntity portfolio) {
		IncomeEntity entity = new IncomeEntity();
		try {
			BeanUtils.copyProperties(entity, form);
		} catch (Exception e) {
			e.printStackTrace();
		}
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolio);
		entity.setState(ConstantUtil.state_create);
		
		incomeDao.save(entity);
	}
	
	/**
	 * 获取投资组合收益分配列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<IncomeEntity> getListByParams(Specification<IncomeEntity> spec, Pageable pageable) {
		Page<IncomeEntity> page = incomeDao.findAll(spec, pageable);
		PageResp<IncomeEntity> resp = new PageResp<IncomeEntity>();
		if (!page.getContent().isEmpty()) {
			resp.setRows(page.getContent());
			resp.setTotal(page.getTotalElements());
		}
		
		return resp;
	}
	
	/**
	 * 净值校准审核
	 * @param oid
	 * @param operator
	 * @param operate
	 * 					YES:审核通过；
	 * 					NO:审核不通过；
	 * @param auditMark
	 * 					审核意见
	 */
	@Transactional
	public void auditIncome(String oid, String operator, String operate, String auditMark) {
		IncomeEntity entity = incomeDao.findOne(oid);
		entity.setAuditor(operator);
		if ("YES".equals(operate)) {
			entity.setAuditState(ConstantUtil.audit_pass);
		} else {
			entity.setAuditState(ConstantUtil.audit_reject);
		}
		entity.setAuditTime(DateUtil.getSqlCurrentDate());
		entity.setAuditMark(auditMark);
	}
}
