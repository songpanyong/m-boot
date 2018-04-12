package com.guohuai.ams.portfolio.service;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.dao.AdjustDao;
import com.guohuai.ams.portfolio.entity.AdjustEntity;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;

/**
 * 现金/损益 校准记录
 * @author star.zhu
 * 2016年12月27日
 */
@Service
public class AdjustService {
	
	@Autowired
	private AdjustDao adjustDao;
	
	@Autowired
	private PortfolioService portfolioService;
	
	/**
	 * 现金校准
	 * @param oid
	 * @param operator
	 * @param amount
	 * 				校准金额
	 */
	@Transactional
	public void adjustCash(String oid, String operator, BigDecimal amount) {
		AdjustEntity entity = new AdjustEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(oid));
		entity.setAmount(amount);
		entity.setAdjustDate(DateUtil.getSqlDate());
		entity.setAsker(operator);
		entity.setAskTime(DateUtil.getSqlCurrentDate());
		entity.setType(ConstantUtil.adjust_cash);
		entity.setState(ConstantUtil.state_create);
		
		adjustDao.save(entity);
	}
	
	/**
	 * 损益校准
	 * @param oid
	 * @param operator
	 * @param amount
	 * 				校准金额
	 */
	@Transactional
	public void adjustDeviation(String oid, String operator, BigDecimal amount) {
		AdjustEntity entity = new AdjustEntity();
		entity.setOid(StringUtil.uuid());
		entity.setPortfolio(portfolioService.getByOid(oid));
		entity.setAmount(amount);
		entity.setAdjustDate(DateUtil.getSqlDate());
		entity.setAsker(operator);
		entity.setAskTime(DateUtil.getSqlCurrentDate());
		entity.setType(ConstantUtil.adjust_deviation);
		entity.setState(ConstantUtil.state_create);
		
		adjustDao.save(entity);
	}
	
	/**
	 * 现金/损益 校准审核
	 */
	@Transactional
	public void auditAdjust(String oid, String operator, String operate, String auditMark) {
		AdjustEntity entity = adjustDao.findOne(oid);
		entity.setAuditor(operator);
		if ("YES".equals(operate)) {
			entity.setAuditState(ConstantUtil.audit_pass);
			if (ConstantUtil.adjust_cash.equals(entity.getType())) {
				entity.getPortfolio().setCashPosition(entity.getAmount());
			} else {
				entity.getPortfolio().setDeviationValue(entity.getAmount());
			}
		} else {
			entity.setAuditState(ConstantUtil.audit_reject);
		}
		entity.setAuditTime(DateUtil.getSqlCurrentDate());
		entity.setAuditMark(auditMark);
	}
	
	/**
	 * 获取列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<AdjustEntity> getListByParams(Specification<AdjustEntity> spec, Pageable pageable) {
		PageResp<AdjustEntity> resp = new PageResp<AdjustEntity>();
		Page<AdjustEntity> page = adjustDao.findAll(spec, pageable);
		if (!page.getContent().isEmpty()) {
			resp.setRows(page.getContent());
			resp.setTotal(page.getTotalElements());
		}
		
		return resp;
	}
}
