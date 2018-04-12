package com.guohuai.mmp.ope.nocard;


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
public class NoCardService {

	@Autowired
	private NoCardDao noCardDao;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public NoCard saveEntity(NoCard en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public NoCard updateEntity(NoCard en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.noCardDao.save(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public NoCard findByOid(String oid){
		NoCard entity = this.noCardDao.findOne(oid);
		return entity;
	}
	
	/**
	 * 根据userOid查询
	 * @param oid
	 * @return
	 */
	public NoCard findByUserOid(String userOid){
		NoCard entity = this.noCardDao.findByUserOid(userOid);
		return entity;
	}

	/**
	 * 分页查找
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Page<NoCard> queryPage(Specification<NoCard> spec, Pageable pageable) {
		Page<NoCard> enchs = this.noCardDao.findAll(spec, pageable);
		
		return enchs;
	}

	/**
	 * 删除
	 * @param oid
	 */
	public void delete(String oid) {
		NoCard entity = this.findByOid(oid);
		 
		noCardDao.delete(entity);
	}
	
	/**
	 * 查询所有
	 * @param spec
	 * @return 
	 */
	public List<NoCard> findAll(Specification<NoCard> spec) {
		List<NoCard> list = this.noCardDao.findAll(spec);
		
		return list;
	}
	
	/**
	 * 反馈
	 * @param operator
	 * @param lastFeedBack
	 */
	public void feedback(String oid, String operator, String lastFeedback){
		NoCard en = this.findByOid(oid);
		en.setOperator(operator);
		en.setLastFeedback(lastFeedback);
		en.setIsFeedback(NoCard.NOCARD_COMMON_IS);
		
		this.updateEntity(en);
	}
}
