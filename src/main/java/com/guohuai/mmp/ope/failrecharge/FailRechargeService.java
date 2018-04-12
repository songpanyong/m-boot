package com.guohuai.mmp.ope.failrecharge;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class FailRechargeService {

	@Autowired
	private FailRechargeDao failRechargeDao;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public FailRecharge saveEntity(FailRecharge en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public FailRecharge updateEntity(FailRecharge en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.failRechargeDao.save(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public FailRecharge findByOid(String oid){
		FailRecharge entity = this.failRechargeDao.findOne(oid);
		return entity;
	}
	
	/**
	 * 根据userOid查询
	 * @param oid
	 * @return
	 */
	public FailRecharge findByUserOid(String userOid){
		FailRecharge entity = this.failRechargeDao.findByUserOid(userOid);
		return entity;
	}

	/**
	 * 分页查找
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Page<FailRecharge> queryPage(Specification<FailRecharge> spec, Pageable pageable) {
		Page<FailRecharge> enchs = this.failRechargeDao.findAll(spec, pageable);
		
		return enchs;
	}

	/**
	 * 删除
	 * @param oid
	 */
	public void delete(String oid) {
		FailRecharge entity = this.findByOid(oid);
		 
		failRechargeDao.delete(entity);
	}
	
	/**
	 * 反馈
	 * @param operator
	 * @param lastFeedBack
	 */
	public void feedback(String oid, String operator, String lastFeedback){
		FailRecharge en = this.findByOid(oid);
		en.setOperator(operator);
		en.setLastFeedback(lastFeedback);
		en.setIsFeedback(FailRecharge.FAILRECHARGE_COMMON_IS);
		
		this.updateEntity(en);
	}

	public List<FailRecharge> findAll(Specification<FailRecharge> spec) {
		return this.failRechargeDao.findAll(spec);
	}
}
