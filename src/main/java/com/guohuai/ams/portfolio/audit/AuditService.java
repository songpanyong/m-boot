package com.guohuai.ams.portfolio.audit;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.PageResp;

/**
 * 审核记录
 * @author star.zhu
 * 2016年12月26日
 */
@Service
public class AuditService {

	@Autowired
	private AuditDao auditDao;
	
	/**
	 * 新增审核记录
	 * @param entity
	 */
	@Transactional
	public void create(AuditEntity entity) {
		auditDao.save(entity);
	}
	
	/**
	 * 获取查询列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public PageResp<AuditEntity> getListByParams(Specification<AuditEntity> spec, Pageable pageable) {
		PageResp<AuditEntity> resp = new PageResp<AuditEntity>();
		Page<AuditEntity> result = auditDao.findAll(spec, pageable);
		if (!result.getContent().isEmpty()) {
			resp.setRows(result.getContent());
			resp.setTotal(result.getTotalElements());
		}
		
		return resp;
	}

	/**
	 * 根据oid，获取审核记录详情
	 * @param oid
	 * @return
	 */
	public AuditEntity findByOid(String oid) {
		return this.auditDao.findOne(oid);
	}
}
