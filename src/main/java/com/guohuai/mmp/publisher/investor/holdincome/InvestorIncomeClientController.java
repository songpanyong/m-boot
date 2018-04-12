package com.guohuai.mmp.publisher.investor.holdincome;

import java.sql.Date;
import java.text.ParseException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/client/investor/holdincome", produces = "application/json")
public class InvestorIncomeClientController extends BaseController {

	@Autowired
	private InvestorIncomeService serviceInvestorIncome;

	/** 我的收益明细(总收益和收益明细) */
	@RequestMapping(value = "qryincome2", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MyInvestorIncomeRep> qryincome2(@RequestParam final String productOid, @RequestParam int page,
			@RequestParam int rows) {
		
		final String uid = this.getLoginUser();
		page = page < 1 ? 1 : page;
		rows = rows < 1 ? 1 : rows;

		Specification<InvestorIncomeEntity> spec = new Specification<InvestorIncomeEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorIncomeEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				return cb.and(cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid), // 投资者ID
//						cb.equal(root.get("product").get("oid").as(String.class), productOid) // 产品ID
//				);
				Predicate accountProcut = cb.and(cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid), // 投资者ID
						cb.equal(root.get("product").get("oid").as(String.class), productOid)); // 产品ID;
				Predicate plan = cb.isNull(root.get("wishplanOid").as(String.class));
				return cb.and(accountProcut, plan);
			}
		};

		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "confirmDate")));

		MyInvestorIncomeRep rep = this.serviceInvestorIncome.findAllByPages2(spec, pageable, page == 1 ? true : false);

		return new ResponseEntity<MyInvestorIncomeRep>(rep, HttpStatus.OK);
	}

	/**
	 * 投资者合仓收益
	 */
	@RequestMapping(value = "qryincome", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<InvestorIncomeRep>> queryInvestorIncome(HttpServletRequest request,
			@And({@Spec(params = "productOid", path = "product.oid", spec = Equal.class),
				  @Spec(params = "confirmDateBegin", path = "confirmDate", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
				  @Spec(params = "confirmDateEnd", path = "confirmDate", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern),
				}) Specification<InvestorIncomeEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows) {

		final String uid = this.getLoginUser();

		Specification<InvestorIncomeEntity> specUID = new Specification<InvestorIncomeEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorIncomeEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p1 = cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid);
				return cb.and(p1);
			}
		};
		spec = Specifications.where(spec).and(specUID);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "confirmDate")));
		PageResp<InvestorIncomeRep> pages = this.serviceInvestorIncome.queryInvestorIncome(spec, pageable);

		return new ResponseEntity<PageResp<InvestorIncomeRep>>(pages, HttpStatus.OK);
	}

	/** 我的收益明细 */
	@RequestMapping(value = "mydetail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<MyInvestorIncomeDetailsRep>> queryMyIncome(@RequestParam int page,
			@RequestParam int rows, @RequestParam int year, @RequestParam int month) {

		if (month < 1 || month > 12) {
			// error.define[10001]=非法的请求参数(CODE:10001)
			throw new AMPException(10001);
		}
		final Date sDate;
		final Date eDate;
		try {
			sDate = DateUtil.fetchSqlDate(DateUtil.getFirstDayZeroTimeOfMonth(year, month, "yyyy-MM-dd"));
			eDate = DateUtil.fetchSqlDate(DateUtil.getLastDayLastTimeOfMonth(year, month, "yyyy-MM-dd"));
		} catch (ParseException e) {
			// error.define[10001]=非法的请求参数(CODE:10001)
			throw new AMPException(10001);
		}
		final String uid = this.getLoginUser();

		page = page < 1 ? 1 : page;
		rows = rows < 1 ? 1 : rows;

		// 查询收益明细
		Specification<InvestorIncomeEntity> spec = new Specification<InvestorIncomeEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorIncomeEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid), // 投资者ID
						cb.between(root.get("confirmDate").as(Date.class), sDate, eDate)// 收益确认日
				);
			}
		};
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "confirmDate")));
		PageResp<MyInvestorIncomeDetailsRep> pages = this.serviceInvestorIncome.findMyIncomeByPages(spec, pageable);

		return new ResponseEntity<PageResp<MyInvestorIncomeDetailsRep>>(pages, HttpStatus.OK);
	}

	/**
	 * 累计 收益 页面 
	 */
	@RequestMapping(value = "mydatedetail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<IncomeRep> queryMyTotalIncomeByDate(@RequestParam int year,
			@RequestParam int month) {

		if (month < 1 || month > 12) {
			// error.define[10001]=非法的请求参数(CODE:10001)
			throw new AMPException(10001);
		}
		String investorOid = this.getLoginUser();
		
		String yMonth = year + (month < 10 ? "0" + month : "" + month);
		IncomeRep rep = this.serviceInvestorIncome.queryMyTotalIncomeByDate(investorOid, yMonth);

		return new ResponseEntity<IncomeRep>(rep, HttpStatus.OK);
	}
}
