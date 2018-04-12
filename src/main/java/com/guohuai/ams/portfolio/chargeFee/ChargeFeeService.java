package com.guohuai.ams.portfolio.chargeFee;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;

/**
 * 费金管理
 * @author star.zhu
 * 2016年12月26日
 */
@Service
public class ChargeFeeService {

	@Autowired
	private ChargeFeeDao chargeFeeDao;
	
	/**
	 * 定时任务--费金累计
	 * @param entity
	 */
	@Transactional
	public void addFeeBySystem(ChargeFeeEntity entity) {
		chargeFeeDao.save(entity);
	}
	
	/**
	 * 获取费金列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<ChargeFeeForm> getListByParams(Specification<ChargeFeeEntity> spec, Pageable pageable) {
		Page<ChargeFeeEntity> result = chargeFeeDao.findAll(spec, pageable);
		PageResp<ChargeFeeForm> resPage = new PageResp<ChargeFeeForm>();
		List<ChargeFeeForm> list = Lists.newArrayList();
		if (!result.getContent().isEmpty()) {
			ChargeFeeForm form = null;
			for (ChargeFeeEntity entity : result.getContent()) {
				form = new ChargeFeeForm();
				try {
					BeanUtils.copyProperties(form, entity);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				list.add(form);
			}
			
			resPage.setRows(list);
			resPage.setTotal(result.getTotalElements());
		}
		
		return resPage;
	}
	
	/**
	 * 费金计提
	 * @param form
	 * @param operator
	 */
	@Transactional
	public void drawingFee(ChargeFeeForm form, String operator) {
		ChargeFeeEntity entity = new ChargeFeeEntity();
		
		try {
			BeanUtils.copyProperties(entity, form);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		entity.setOid(StringUtil.uuid());
		entity.setClassify(ConstantUtil.fee_draw);
		entity.setAsker(operator);
		entity.setAskDate(DateUtil.getSqlCurrentDate());
		
		chargeFeeDao.save(entity);
	}
	
	/**
	 * 费金累计
	 * @param entity
	 * @param operator
	 */
	@Transactional
	public void countingFee(ChargeFeeEntity entity, String operator) {
//		ChargeFeeEntity entity = new ChargeFeeEntity();
//		
//		entity.setOid(StringUtil.uuid());
//		entity.setPortfolioOid(portfolio.getOid());
//		entity.setPortfolioName(portfolio.getName());
//		entity.setClassify(ConstantUtil.COUNTFEE);
//		entity.setUpdateDate(DateUtil.getSqlDate());
//		entity.setAsker(operator);
//		entity.setAskDate(DateUtil.getSqlCurrentDate());
		
		chargeFeeDao.save(entity);
	}
	
	/**
	 * 费金计提审核
	 * @param oid
	 * @param operator
	 * @param operate
	 * 					YES:审核通过；
	 * 					NO:审核不通过；
	 * @param auditMark
	 * 					审核意见
	 */
	@Transactional
	public void auditDraw(String oid, String operator, String operate, String auditMark) {
		ChargeFeeEntity entity = chargeFeeDao.findOne(oid);
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
