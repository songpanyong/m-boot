package com.guohuai.mmp.jiajiacai.wishplan.planlist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PlanListService {
	
	@Autowired
	private PlanListDao planDao;
	
	/**
	 * 查询所有的心愿计划，按照数据库中的orders进行排序
	 * @return
	 */
	public List<PlanListEntity> findAll() {
		Sort sort = new Sort(Sort.Direction.ASC, "orders");
		return planDao.findAll(sort);
	}
	
	public PlanListEntity getEntityByName(String name) {
		return planDao.findByName(name);
	}
	
	public PlanListEntity getEntityByOid(String oid) {
		return planDao.findByOid(oid);
	}
	
	public PlanListEntity findByPlanType(String type) {
		return planDao.findByPlanType(type);
	}
	
}
