package com.guohuai.mmp.ope.failcard;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class FailCardService {

	@Autowired
	private FailCardDao failCardDao;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public FailCard saveEntity(FailCard en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public FailCard updateEntity(FailCard en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.failCardDao.save(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public FailCard findByOid(String oid){
		FailCard entity = this.failCardDao.findOne(oid);
		return entity;
	}
	
	/**
	 * 根据userOid查询
	 * @param oid
	 * @return
	 */
	public FailCard findByUserOid(String userOid){
		FailCard entity = this.failCardDao.findByUserOid(userOid);
		return entity;
	}

	/**
	 * 分页查找
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Page<FailCard> queryPage(Specification<FailCard> spec, Pageable pageable) {
		Page<FailCard> enchs = this.failCardDao.findAll(spec, pageable);
		
		return enchs;
	}

	/**
	 * 删除
	 * @param oid
	 */
	public void delete(String oid) {
		FailCard entity = this.findByOid(oid);
		 
		failCardDao.delete(entity);
	}
	
	/**
	 * 反馈
	 * @param operator
	 * @param lastFeedBack
	 */
	public void feedback(String oid, String operator, String lastFeedback){
		FailCard en = this.findByOid(oid);
		en.setOperator(operator);
		en.setLastFeedback(lastFeedback);
		en.setIsFeedback(FailCard.FAILCARD_COMMON_IS);
		
		this.updateEntity(en);
	}

}
