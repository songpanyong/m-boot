package com.guohuai.mmp.ope.distribution;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.ope.failrecharge.FailRecharge;
import com.guohuai.mmp.ope.failrecharge.FailRechargeService;
import com.guohuai.mmp.ope.nobuy.NoBuy;
import com.guohuai.mmp.ope.nobuy.NoBuyService;
import com.guohuai.mmp.ope.nocard.NoCard;
import com.guohuai.mmp.ope.nocard.NoCardService;
import com.guohuai.mmp.ope.norecharge.NoRecharge;

@Service
@Transactional
public class DistributionService {

	@Autowired
	private NoCardService noCardService;
	
//	@Autowired
//	private NoRechargeService noRechargeService;
	
	@Autowired
	private FailRechargeService failRechargeService;
	
	@Autowired
	private NoBuyService noBuyService;

	// 来源分布列表
	public DistributionListResp groupList(String startTime, String endTime, int page, int rows) {
		List<NoCard> noCardSources = querySources(startTime, endTime);
		int start = (page-1)*rows;
		int endNum = page*rows;
		if (noCardSources.size() < endNum) endNum = noCardSources.size();
		
		List<DistributionResp> list = new ArrayList<>();
		for (NoCard item : noCardSources){
			DistributionResp en = new DistributionResp();
			en.setSource(item.getSource());
			en.setRegisterNum(getRigisterNum(startTime, endTime, item.getSource()));
			en.setBindNum(getBindNum(startTime, endTime, item.getSource()));
			en.setRechargeNum(getRechargeNum(startTime, endTime, item.getSource()));
			en.setBuyNum(getBuyNum(startTime, endTime, item.getSource()));
			
			list.add(en);
		}
		
		list.sort(new Comparator<DistributionResp>() {
			@Override
			public int compare(DistributionResp o1, DistributionResp o2) {
				return o1.getRegisterNum().intValue() < o2.getRegisterNum().intValue()? 1:-1;
			}
		});
		
		List<DistributionResp> sources = new ArrayList<>();
		for(int i = start; i < endNum; i++){
			sources.add(list.get(i));
		}
		
		DistributionListResp resp = new DistributionListResp(sources, noCardSources.size(), null);
		
		return resp;
	}
	
	// 用户分渠道每日统计列表
	public DistributionListResp sourcelist(String startTime, String endTime, String source, int page, int rows) {
		int btwnDays = DateUtil.daysBetween(DateUtil.parse(endTime, DateUtil.datePattern), DateUtil.parse(startTime, DateUtil.datePattern));
		List<DistributionResp> list = new ArrayList<>();
		List<DistributionResp> totalList = new ArrayList<>();
		
		if (btwnDays < 0){
			btwnDays = 0;
		}else{
			int startNum = (page-1)*rows;
			int endNum = page*rows;
			if (btwnDays < endNum) endNum = btwnDays;
			
//			java.util.Date startDate = DateUtil.addDay(DateUtil.parse(startTime, DateUtil.datePattern), startNum);
			java.util.Date startDate = DateUtil.parse(startTime, DateUtil.datePattern);
			for(int i = 0; i < startNum; i++){
				java.util.Date endDate = DateUtil.addDay(startDate, 1);
				
				String sTime = DateUtil.format(startDate, DateUtil.datePattern);
				String eTime = DateUtil.format(endDate, DateUtil.datePattern);
				
				DistributionResp en = new DistributionResp();
				en.setSource(source);
				en.setRegisterNum(getRigisterNum(sTime, eTime, source));
				en.setBindNum(getBindNum(sTime, eTime, source));
				en.setRechargeNum(getRechargeNum(sTime, eTime, source));
				en.setBuyNum(getBuyNum(sTime, eTime, source));
				en.setTime(startDate);
				
				totalList.add(en);
				
				startDate = DateUtil.addDay(startDate, 1);
			}
			for(int i = startNum; i < endNum; i++){
				java.util.Date endDate = DateUtil.addDay(startDate, 1);
				
				String sTime = DateUtil.format(startDate, DateUtil.datePattern);
				String eTime = DateUtil.format(endDate, DateUtil.datePattern);
				
				DistributionResp en = new DistributionResp();
				en.setSource(source);
				en.setRegisterNum(getRigisterNum(sTime, eTime, source));
				en.setBindNum(getBindNum(sTime, eTime, source));
				en.setRechargeNum(getRechargeNum(sTime, eTime, source));
				en.setBuyNum(getBuyNum(sTime, eTime, source));
				en.setTime(startDate);
				
				list.add(en);
				totalList.add(en);
				
				startDate = DateUtil.addDay(startDate, 1);
			}
			for(int i = endNum; i < btwnDays; i++){
				java.util.Date endDate = DateUtil.addDay(startDate, 1);
				
				String sTime = DateUtil.format(startDate, DateUtil.datePattern);
				String eTime = DateUtil.format(endDate, DateUtil.datePattern);
				
				DistributionResp en = new DistributionResp();
				en.setSource(source);
				en.setRegisterNum(getRigisterNum(sTime, eTime, source));
				en.setBindNum(getBindNum(sTime, eTime, source));
				en.setRechargeNum(getRechargeNum(sTime, eTime, source));
				en.setBuyNum(getBuyNum(sTime, eTime, source));
				en.setTime(startDate);
				
				totalList.add(en);
				
				startDate = DateUtil.addDay(startDate, 1);
			}
		}
		
		DistributionListResp resp = new DistributionListResp(list, btwnDays, totalList);
		
		return resp;
	}

	private Integer getBuyNum(String startTime, String endTime, String source) {
		Specification<NoBuy> spec = new Specification<NoBuy>() {
			@Override
			public Predicate toPredicate(Root<NoBuy> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("source").as(String.class), source);
				Predicate b = cb.equal(root.get("isBuy").as(String.class), NoBuy.NOBUY_COMMON_IS);
				return cb.and(a,b);
			}
		};
		if (startTime!=null&&!startTime.isEmpty()){
			Specification<NoBuy> sa = new Specification<NoBuy>() {
				@Override
				public Predicate toPredicate(Root<NoBuy> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.greaterThanOrEqualTo(root.get("buyTime").as(Timestamp.class), new Date(DateUtil.parseDate(startTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		if (endTime!=null&&!endTime.isEmpty()){
			Specification<NoBuy> sa = new Specification<NoBuy>() {
				@Override
				public Predicate toPredicate(Root<NoBuy> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.lessThanOrEqualTo(root.get("buyTime").as(Timestamp.class), new Date(DateUtil.parseDate(endTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		
		List<NoBuy> list =  noBuyService.findAll(spec);
		return list.size();
	}

	// 获取首次充值数量
	private Integer getRechargeNum(String startTime, String endTime, String source) {
//		Specification<NoRecharge> spec = new Specification<NoRecharge>() {
//			@Override
//			public Predicate toPredicate(Root<NoRecharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Predicate a = cb.equal(root.get("source").as(String.class), source);
//				Predicate b = cb.equal(root.get("isCharge").as(String.class), NoRecharge.NORECHARGE_COMMON_IS);
//				return cb.and(a, b);
//			}
//		};
//		if (startTime!=null&&!startTime.isEmpty()){
//			Specification<NoRecharge> sa = new Specification<NoRecharge>() {
//				@Override
//				public Predicate toPredicate(Root<NoRecharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//					return cb.greaterThanOrEqualTo(root.get("rechargeSuccessTime").as(Timestamp.class), new Date(DateUtil.parseDate(startTime, DateUtil.datePattern).getTime()));
//				}
//			};
//			spec = Specifications.where(spec).and(sa);
//		}
//		if (endTime!=null&&!endTime.isEmpty()){
//			Specification<NoRecharge> sa = new Specification<NoRecharge>() {
//				@Override
//				public Predicate toPredicate(Root<NoRecharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//					return cb.lessThanOrEqualTo(root.get("rechargeSuccessTime").as(Timestamp.class), new Date(DateUtil.parseDate(endTime, DateUtil.datePattern).getTime()));
//				}
//			};
//			spec = Specifications.where(spec).and(sa);
//		}
//		
//		List<NoRecharge> list =  noRechargeService.findAll(spec);
		
		Specification<FailRecharge> spec = new Specification<FailRecharge>() {
			@Override
			public Predicate toPredicate(Root<FailRecharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("source").as(String.class), source);
				Predicate b = cb.equal(root.get("isCharge").as(String.class), NoRecharge.NORECHARGE_COMMON_IS);
				return cb.and(a, b);
			}
		};
		if (startTime!=null&&!startTime.isEmpty()){
			Specification<FailRecharge> sa = new Specification<FailRecharge>() {
				@Override
				public Predicate toPredicate(Root<FailRecharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.greaterThanOrEqualTo(root.get("rechargeSuccessTime").as(Timestamp.class), new Date(DateUtil.parseDate(startTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		if (endTime!=null&&!endTime.isEmpty()){
			Specification<FailRecharge> sa = new Specification<FailRecharge>() {
				@Override
				public Predicate toPredicate(Root<FailRecharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.lessThanOrEqualTo(root.get("rechargeSuccessTime").as(Timestamp.class), new Date(DateUtil.parseDate(endTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		
		List<FailRecharge> list =  failRechargeService.findAll(spec);
		return list.size();
	}

	// 获取绑定数量
	private Integer getBindNum(String startTime, String endTime, String source) {
		Specification<NoCard> spec = new Specification<NoCard>() {
			@Override
			public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("source").as(String.class), source);
				Predicate b = cb.equal(root.get("isBind").as(String.class), NoCard.NOCARD_COMMON_IS);
				return cb.and(a, b);
			}
		};
		if (startTime!=null&&!startTime.isEmpty()){
			Specification<NoCard> sa = new Specification<NoCard>() {
				@Override
				public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.greaterThanOrEqualTo(root.get("bindSuccessTime").as(Timestamp.class), new Date(DateUtil.parseDate(startTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		if (endTime!=null&&!endTime.isEmpty()){
			Specification<NoCard> sa = new Specification<NoCard>() {
				@Override
				public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.lessThanOrEqualTo(root.get("bindSuccessTime").as(Timestamp.class), new Date(DateUtil.parseDate(endTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		
		List<NoCard> list =  noCardService.findAll(spec);
		return list.size();
	}

	// 获取注册数量
	private Integer getRigisterNum(String startTime, String endTime, String source) {
		
		Specification<NoCard> spec = new Specification<NoCard>() {
			@Override
			public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("source").as(String.class), source);
				return cb.and(a);
			}
		};
		if (startTime!=null&&!startTime.isEmpty()){
			Specification<NoCard> sa = new Specification<NoCard>() {
				@Override
				public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.greaterThanOrEqualTo(root.get("registerTime").as(Timestamp.class), new Date(DateUtil.parseDate(startTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		if (endTime!=null&&!endTime.isEmpty()){
			Specification<NoCard> sa = new Specification<NoCard>() {
				@Override
				public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.lessThanOrEqualTo(root.get("registerTime").as(Timestamp.class), new Date(DateUtil.parseDate(endTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		
		List<NoCard> list =  noCardService.findAll(spec);
		return list.size();
	}

	// 获取时间段内多少种来源
	private List<NoCard> querySources(String startTime, String endTime) {
		Specification<NoCard> spec = new Specification<NoCard>() {
			@Override
			public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.orderBy(cb.desc(root.get("registerTime")));
				query.groupBy(root.get("source"));
				return query.getRestriction();
			}
		};
		
		if (startTime!=null&&!startTime.isEmpty()){
			Specification<NoCard> sa = new Specification<NoCard>() {
				@Override
				public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.greaterThanOrEqualTo(root.get("registerTime").as(Timestamp.class), new Date(DateUtil.parseDate(startTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		if (endTime!=null&&!endTime.isEmpty()){
			Specification<NoCard> sa = new Specification<NoCard>() {
				@Override
				public Predicate toPredicate(Root<NoCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.lessThanOrEqualTo(root.get("registerTime").as(Timestamp.class), new Date(DateUtil.parseDate(endTime, DateUtil.datePattern).getTime()));
				}
			};
			spec = Specifications.where(spec).and(sa);
		}
		
		List<NoCard> list = noCardService.findAll(spec);
		return list;
	}

}
