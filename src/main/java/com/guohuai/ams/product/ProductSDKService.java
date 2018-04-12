package com.guohuai.ams.product;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guohuai.ams.label.LabelDao;
import com.guohuai.ams.label.LabelEntity;
import com.guohuai.basic.component.ext.web.PageResp;

/**
 * SDK查询
 * 
 * @author wanglei
 *
 */
@Service
public class ProductSDKService {
	Logger logger = LoggerFactory.getLogger(ProductSDKService.class);
	@Autowired
	private ProductDao productDao;
	@Autowired
	private LabelDao labelDao;

	public PageResp<ProductLabelRep> queryProductLabelList() {

		List<ProductLabelRep> list = new ArrayList<ProductLabelRep>();
//		list.add(new ProductLabelSDKRep(Product.PRODUCT_productLabel_seckill, "秒杀"));
//		list.add(new ProductLabelSDKRep(Product.PRODUCT_productLabel_recom, "推荐"));
//		list.add(new ProductLabelSDKRep(Product.PRODUCT_productLabel_freshman, "新手"));
//		list.add(new ProductLabelSDKRep(Product.PRODUCT_productLabel_experienceFund, "体验"));

		PageResp<ProductLabelRep> page = new PageResp<ProductLabelRep>();
		page.setRows(list);
		page.setTotal(list.size());

		return page;
	}

	/**
	 * 查询产品列表
	 */
	public PageResp<ProductSDKRep> queryProductList(ProductSDKReq req) {

		Sort pSort = new Sort(new Order(Direction.ASC, "name"));

		PageResp<ProductSDKRep> pageResp = new PageResp<ProductSDKRep>();

		List<Product> list = this.productDao.findAll(getProQueryPredicate(req), pSort);

		if (list != null && list.size() > 0) {
			List<ProductSDKRep> rows = new ArrayList<ProductSDKRep>();
			for (Product product : list) {
				ProductSDKRep sdk = new ProductSDKRep(product.getOid(), product.getName(), product.getCode());
				rows.add(sdk);
			}
			pageResp.setRows(rows);
			pageResp.setTotal(list.size());
		}

		return pageResp;
	}

	private Specification<Product> getProQueryPredicate(final ProductSDKReq req) {

		return new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				Predicate p = null;

				// 产品oid
				if (!StringUtils.isEmpty(req.getOid())) {
					p = cb.and(cb.equal(root.get("oid").as(String.class), req.getOid()));
				}

				// 产品类型
				if (!StringUtils.isEmpty(req.getType())) {
					if (p == null) {
						p = cb.equal(root.get("type").get("oid").as(String.class),
								"0".equals(req.getType()) ? Product.TYPE_Producttype_02 : Product.TYPE_Producttype_01);
					} else {
						p = cb.and(cb.equal(root.get("type").get("oid").as(String.class),
								"0".equals(req.getType()) ? Product.TYPE_Producttype_02 : Product.TYPE_Producttype_01));
					}
				}

				// 产品标签
				if (!StringUtils.isEmpty(req.getLabel())) {
					if (p == null) {
						p = cb.equal(root.get("productLabel").as(String.class), req.getLabel());
					} else {
						p = cb.and(cb.equal(root.get("productLabel").as(String.class), req.getLabel()));
					}
				}

				return p;
			}
		};
	}
;
	public PageResp<ProductLabelRep> getProductLabelNames(ProductLabelReq param) {
		PageResp<ProductLabelRep> pageResp = new PageResp<ProductLabelRep>();
		Specification<LabelEntity> spec = new Specification<LabelEntity>() {
			@Override
			public Predicate toPredicate(Root<LabelEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = null;
				p = cb.equal(root.get("isOk").as(String.class), LabelEntity.isOk_yes);
				if(!StringUtils.isEmpty(param.getLabelType())){
					p=cb.and(cb.equal(root.get("labelType").as(String.class), param.getLabelType()));
				}
				return p;
			}
		};
		spec = Specifications.where(spec);

		List<LabelEntity> pls = this.labelDao.findAll(Specifications.where(spec));

		List<ProductLabelRep> productLabelNames = new ArrayList<ProductLabelRep>();
		if (pls != null && pls.size() > 0) {
			ProductLabelRep productLabelRep = null;
			for (LabelEntity pl : pls) {
				productLabelRep = new ProductLabelRep();
				productLabelRep.setOid(pl.getOid());
				productLabelRep.setName(pl.getLabelName());
				productLabelRep.setCode(pl.getLabelCode());
				productLabelNames.add(productLabelRep);
			}
		}
		pageResp.setRows(productLabelNames);
		pageResp.setTotal(productLabelNames.size());
		return pageResp;
	}
}
