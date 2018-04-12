package com.guohuai.mmp.ope.nobuy;


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
public class NoBuyService {

	@Autowired
	private NoBuyDao noBuyDao;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public NoBuy saveEntity(NoBuy en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public NoBuy updateEntity(NoBuy en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.noBuyDao.save(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public NoBuy findByOid(String oid){
		NoBuy entity = this.noBuyDao.findOne(oid);
		return entity;
	}
	
	/**
	 * 根据userOid查询
	 * @param oid
	 * @return
	 */
	public NoBuy findByUserOid(String userOid){
		NoBuy entity = this.noBuyDao.findByUserOid(userOid);
		return entity;
	}

	/**
	 * 分页查找
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Page<NoBuy> queryPage(Specification<NoBuy> spec, Pageable pageable) {
		Page<NoBuy> enchs = this.noBuyDao.findAll(spec, pageable);
		
		return enchs;
	}

	/**
	 * 删除
	 * @param oid
	 */
	public void delete(String oid) {
		NoBuy entity = this.findByOid(oid);
		 
		noBuyDao.delete(entity);
	}
	
	/**
	 * 查询所有
	 * @param spec
	 * @return
	 */
	public List<NoBuy> findAll(Specification<NoBuy> spec) {
		List<NoBuy> list = noBuyDao.findAll(spec);
		return list;
	}
	
	/**
	 * 反馈
	 * @param operator
	 * @param lastFeedBack
	 */
	public void feedback(String oid, String operator, String lastFeedback){
		NoBuy en = this.findByOid(oid);
		en.setOperator(operator);
		en.setLastFeedback(lastFeedback);
		en.setIsFeedback(NoBuy.NOBUY_COMMON_IS);
		
		this.updateEntity(en);
	}

}
