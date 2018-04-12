package com.guohuai.mmp.jiajiacai.wishplan.plan.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.WishplanProduct;

public interface WishplanProductDao extends JpaRepository<WishplanProduct, String>, JpaSpecificationExecutor<WishplanProduct> {

	/**家加财新增代码---通过id查询产品实体*/
	public WishplanProduct findByOid(String Oid);
}	

