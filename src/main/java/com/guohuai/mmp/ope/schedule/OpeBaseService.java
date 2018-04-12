package com.guohuai.mmp.ope.schedule;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderService;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.platform.payment.log.PayInterface;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogService;

@Service
public class OpeBaseService {

	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private PayLogService payLogService;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;

	
	@Transactional(value = TxType.REQUIRES_NEW)
	public String getUserOidByTradeOrderCode(String orderCode){
		InvestorTradeOrderEntity order = investorTradeOrderService.findByOrderCode(orderCode);
		return order.getInvestorBaseAccount().getOid();
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorBaseAccountEntity getUserByBankOrderCode(String orderCode){
		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeNoExc(orderCode);
		InvestorBaseAccountEntity user = bankOrder.getInvestorBaseAccount();
		InvestorBaseAccountEntity en = new InvestorBaseAccountEntity();
		en.setOid(user.getOid());
		en.setPhoneNum(user.getPhoneNum());
		en.setChannelid(user.getChannelid());
		en.setRealName(user.getRealName());
		return en;
	}

	// 获取时间大于failrechargetime的并且interfaceName是tradeCallback
	public List<PayLogEntity> queryPayLogEntity(long failrechargetime, Integer page, Integer rows) {
		Specification<PayLogEntity> spec = new Specification<PayLogEntity>() {
			@Override
			public Predicate toPredicate(Root<PayLogEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("interfaceName"), PayInterface.tradeCallback.getInterfaceName());
				Predicate b = cb.greaterThan(root.get("createTime").as(Timestamp.class), new Timestamp(failrechargetime));
				query.where(cb.and(a, b));
				query.orderBy(cb.asc(root.get("createTime")));
				return query.getRestriction();
			}
		};
		
		List<PayLogEntity> list = new ArrayList<>();
		if (page == null || rows == null){
			list = payLogService.findAll(spec);
		}else{
			Pageable pageable = new PageRequest(page - 1, rows);
			Page<PayLogEntity> payLogPage = payLogService.findPage(spec, pageable);
			list = payLogPage.getContent();
		}
		
		return list;
	}

	public List<InvestorTradeOrderEntity> queryInvestorTradeOrderEntity(long nobuytime, Integer page, Integer rows) {
		Specification<InvestorTradeOrderEntity> spec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.greaterThan(root.get("createTime").as(Timestamp.class), new Timestamp(nobuytime));
				query.where(cb.and(a));
				query.orderBy(cb.asc(root.get("createTime")));
				return query.getRestriction();
			}
		};
		
		List<InvestorTradeOrderEntity> list = new ArrayList<>();
		if (page == null || rows == null){
			list = investorTradeOrderService.findAll(spec);
		}else{
			Pageable pageable = new PageRequest(page - 1, rows);
			Page<InvestorTradeOrderEntity> enPage = investorTradeOrderService.findPage(spec, pageable);
			list = enPage.getContent();
		}
		
		return list;
	}
}
