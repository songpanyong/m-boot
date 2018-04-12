package com.guohuai.ams.illiquidAsset.overdue;

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
public class IlliquidOverdueService {

	@Autowired
	private IlliquidOverdueDao illiquidOverdueDao;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public IlliquidOverdue saveEntity(IlliquidOverdue en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public IlliquidOverdue updateEntity(IlliquidOverdue en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.illiquidOverdueDao.save(en);
	}
	
	/**
	 * 删除
	 * @param en
	 * @return
	 */
	@Transactional
	public void delEntity(IlliquidOverdue en){
		this.illiquidOverdueDao.delete(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public IlliquidOverdue findByOid(String oid){
		IlliquidOverdue entity = this.illiquidOverdueDao.findOne(oid);
		if(null == entity){
			throw GHException.getException(70000);
		}
		return entity;
	}
	
	/**
	 * 后台分页查询
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public IlliquidOverdueListResp queryPage(Specification<IlliquidOverdue> spec, Pageable pageable) {
		Page<IlliquidOverdue> enchs = this.illiquidOverdueDao.findAll(spec, pageable);
		IlliquidOverdueListResp pageResp = new IlliquidOverdueListResp(enchs);
		
		return pageResp;
	}

	/**
	 * 根据非现金标的oid获取最后一条未完成的数据
	 * @param oid
	 * @return
	 */
	public IlliquidOverdue getLastOverdueByAssetOid(String oid) {
		List<IlliquidOverdue> list = illiquidOverdueDao.getLastOverdueByAssetOid(oid);
		if (list != null && !list.isEmpty()){
			return list.get(0);
		}

		return null;
	}
	
}
