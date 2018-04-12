package com.guohuai.ams.portfolio.service;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.dao.NetValueDao;
import com.guohuai.ams.portfolio.entity.NetValueEntity;
import com.guohuai.ams.portfolio.form.NetValueForm;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

/**
 * 投资组合净值明细
 * @author star.zhu
 * 2016年12月26日
 */
@Service
public class NetValueService {

	@Autowired
	private NetValueDao netValueDao;
	
	@Autowired
	private PortfolioService portfolioService;
	
	/**
	 * 净值校准录入
	 * @param form
	 * @param portfolio
	 */
	@Transactional
	public void create(NetValueForm form, String operator) {
		NetValueEntity entity = new NetValueEntity();
		try {
			BeanUtils.copyProperties(entity, form);
		} catch (Exception e) {
			e.printStackTrace();
		}
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(form.getPortfolioName()));
		entity.setAuditState(ConstantUtil.state_create);
		
		netValueDao.save(entity);
	}
	
	/**
	 * 获取投资组合净值列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<NetValueEntity> getListByParams(Specification<NetValueEntity> spec, Pageable pageable) {
		Page<NetValueEntity> page = netValueDao.findAll(spec, pageable);
		PageResp<NetValueEntity> resp = new PageResp<NetValueEntity>();
		if (!page.getContent().isEmpty()) {
			resp.setRows(page.getContent());
			resp.setTotal(page.getTotalElements());
		}
		
		return resp;
	}
	
	/**
	 * 获取投资组合净值校准详情
	 * @param oid
	 * @return
	 */
	@Transactional
	public NetValueForm getNetValueInfo(String oid) {
		NetValueEntity entity = netValueDao.findOne(oid);
		if (null == entity) {
			throw new AMPException("未知错误！");
		}
		
		NetValueForm form = new NetValueForm();
		try {
			BeanUtils.copyProperties(form, entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return form;
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
	public void auditNetValue(String oid, String operator, String operate, String auditMark) {
		NetValueEntity entity = netValueDao.findOne(oid);
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
