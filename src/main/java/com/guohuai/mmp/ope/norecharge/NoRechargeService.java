package com.guohuai.mmp.ope.norecharge;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.exception.GHException;
import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class NoRechargeService {

	@Autowired
	private NoRechargeDao noRechargeDao;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public NoRecharge saveEntity(NoRecharge en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public NoRecharge updateEntity(NoRecharge en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.noRechargeDao.save(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public NoRecharge findByOid(String oid){
		NoRecharge entity = this.noRechargeDao.findOne(oid);
		if(null == entity){
			throw GHException.getException("元素不存在！");
		}
		return entity;
	}
	
	/**
	 * 根据userOid查询
	 * @param oid
	 * @return
	 */
	public NoRecharge findByUserOid(String userOid){
		NoRecharge entity = this.noRechargeDao.findByUserOid(userOid);
		return entity;
	}

	/**
	 * 分页查找
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Page<NoRecharge> queryPage(Specification<NoRecharge> spec, Pageable pageable) {
		Page<NoRecharge> enchs = this.noRechargeDao.findAll(spec, pageable);
		
		return enchs;
	}

	/**
	 * 删除
	 * @param oid
	 */
	public void delete(String oid) {
		NoRecharge entity = this.findByOid(oid);
		 
		noRechargeDao.delete(entity);
	}
	
	/**
	 * 查询所有
	 * @param spec
	 * @return
	 */
	public List<NoRecharge> findAll(Specification<NoRecharge> spec) {
		List<NoRecharge> list = this.noRechargeDao.findAll(spec);
		return list;
	}
	
	/**
	 * 反馈
	 * @param operator
	 * @param lastFeedBack
	 */
	public void feedback(String oid, String operator, String lastFeedback){
		NoRecharge en = this.findByOid(oid);
		en.setOperator(operator);
		en.setLastFeedback(lastFeedback);
		en.setIsFeedback(NoRecharge.NORECHARGE_COMMON_IS);
		
		this.updateEntity(en);
	}

	
}
