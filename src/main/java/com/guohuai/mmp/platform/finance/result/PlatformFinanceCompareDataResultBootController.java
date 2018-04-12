package com.guohuai.mmp.platform.finance.result;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.product.Product;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.StringUtil;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/platform/finance/result", produces = "application/json")
public class PlatformFinanceCompareDataResultBootController {
	@Autowired
	private PlatformFinanceCompareDataResultService platformFinanceCompareDataResultService;
	
	/**
	 * 查询
	 */
	@RequestMapping(value = "crmng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PlatformFinanceCompareDataResultRep>> checkResultList(HttpServletRequest request,
			@RequestParam int page, @RequestParam int rows,
			@RequestParam String orderCode,
			@And({
				@Spec(params = "dealStatus", path = "dealStatus", spec = In.class),
				@Spec(params = "checkOid", path = "checkOid", spec = Equal.class),
				@Spec(params = "phoneNum", path = "phoneNum", spec = Equal.class),
				@Spec(params = "realName", path = "realName", spec = Equal.class),
				@Spec(params = "outerPhoneNum", path = "outerPhoneNum", spec = Equal.class),
				@Spec(params = "outerRealName", path = "outerRealName", spec = Equal.class)
				})
				Specification<PlatformFinanceCompareDataResultEntity> spec,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		Specification<PlatformFinanceCompareDataResultEntity> orderCodeSpec = null;
		if (!StringUtil.isEmpty(orderCode)) {
			orderCodeSpec = new Specification<PlatformFinanceCompareDataResultEntity>() {
				@Override
				public Predicate toPredicate(Root<PlatformFinanceCompareDataResultEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.or(cb.equal(root.get("orderCode").as(String.class), orderCode), cb.equal(root.get("outerOrderCode").as(String.class), orderCode));
				}
			};
			spec = Specifications.where(spec).and(orderCodeSpec);
		}
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<PlatformFinanceCompareDataResultRep> rep = this.platformFinanceCompareDataResultService.checkResultList(spec, pageable);

		return new ResponseEntity<PageResp<PlatformFinanceCompareDataResultRep>>(rep, HttpStatus.OK);
	}
}
