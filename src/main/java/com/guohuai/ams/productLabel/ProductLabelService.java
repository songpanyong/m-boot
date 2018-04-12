package com.guohuai.ams.productLabel;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.guohuai.ams.label.LabelDao;
import com.guohuai.ams.label.LabelEntity;
import com.guohuai.ams.label.LabelRep;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;

@Service
@Transactional
public class ProductLabelService {
	
	Logger logger = LoggerFactory.getLogger(ProductLabelService.class);
	
	@Autowired
	private LabelDao labelDao;
	@Autowired
	private ProductLabelDao productLabelDao;
	@Autowired
	private ProductDao productDao;


	public List<ProductLabel> saveAndFlush(Product product, List<String> labelOids) {

		List<ProductLabel> oldProductLabels = this.findProductLabelsByProduct(product);
		
		productLabelDao.delete(oldProductLabels);

		List<ProductLabel> result = new ArrayList<ProductLabel>();
		
		for (String labelOid : labelOids) {
			// 存储新的
			LabelEntity label = labelDao.findOne(labelOid);
			if (null == label) {
				throw  new AMPException("标签不存在");
			}
			ProductLabel pl = new ProductLabel();
			pl.setOid(StringUtil.uuid());
			pl.setLabel(label);
			pl.setProduct(product);
			pl.setCreateTime(DateUtil.getSqlCurrentDate());
			pl.setUpdateTime(DateUtil.getSqlCurrentDate());
			productLabelDao.save(pl);
			result.add(pl);
		}
		return result;
	}
	
	public List<LabelRep> getLabelRepByProduct(String productOid) {
		
		List<LabelRep> repList = new ArrayList<LabelRep>();
		List<ProductLabel> list = this.findProductLabelsByProduct(productDao.findOne(productOid));
		for (ProductLabel label : list) {
			LabelRep rep = new LabelRep();
			rep.setLabelCode(label.getLabel().getLabelCode());
			rep.setLabelName(label.getLabel().getLabelName());
			rep.setLabelType(label.getLabel().getLabelType());
			repList.add(rep);
		}
		return repList;
	}
	
	public List<ProductLabel> findProductLabelsByProduct(final Product product) {
		Specification<ProductLabel> spec = new Specification<ProductLabel>() {
			@Override
			public Predicate toPredicate(Root<ProductLabel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("product").get("oid").as(String.class), product.getOid());
			}
		};
		spec = Specifications.where(spec);
		
		return productLabelDao.findAll(spec);
	}
	

	
	public String findLabelByProduct(Product product) {
		List<ProductLabel> list = productLabelDao.findByProduct(product);
		StringBuilder sb = new StringBuilder();
		for  (ProductLabel label : list) {
			sb.append(label.getLabel().getLabelCode()).append(",");
		}
		if (sb.length() != 0) {
			sb.substring(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}
	
}
