package com.guohuai.mmp.platform.finance.result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderService;
import com.guohuai.mmp.platform.finance.orders.OrdersService;

@Service
public class PlatformFinanceCompareDataResultService {
	
	@Autowired
	private PlatformFinanceCompareDataResultDao platformFinanceCompareDataResultDao;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private OrdersService ordersService;
	
	/**
	 * 查询对账结果
	 */
	public PageResp<PlatformFinanceCompareDataResultRep> checkResultList(Specification<PlatformFinanceCompareDataResultEntity> spec,
			Pageable pageable) {
		Page<PlatformFinanceCompareDataResultEntity> list = this.platformFinanceCompareDataResultDao.findAll(spec, pageable);
		PageResp<PlatformFinanceCompareDataResultRep> pagesRep = new PageResp<PlatformFinanceCompareDataResultRep>();
		for (PlatformFinanceCompareDataResultEntity entity : list) {
			PlatformFinanceCompareDataResultRep rep = new PlatformFinanceCompareDataResultRep();
			
			rep.setCrOid(entity.getOid());
			rep.setCheckOid(entity.getCheckOid());
			rep.setOrderCode(entity.getOrderCode());
			rep.setIPayNo(entity.getIPayNo());
			rep.setUserType(entity.getUserType());
			rep.setUserTypeDisp(this.ordersService.userTypeEn2Ch(entity.getUserType()));
			rep.setOrderType(entity.getOrderType());
			rep.setOrderTypeDisp(this.investorBankOrderService.orderTypeEn2Ch(entity.getOrderType()));
			
			rep.setOrderAmount(entity.getOrderAmount());
			rep.setVoucher(entity.getVoucher());
			rep.setFee(entity.getFee());
			rep.setInvestorOid(entity.getInvestorOid());
			rep.setPhoneNum(entity.getPhoneNum());
			rep.setRealName(entity.getRealName());
			rep.setOrderStatus(entity.getOrderStatus());
			rep.setOrderStatusDisp(investorBankOrderService.orderStatusEn2Ch(entity.getOrderStatus()));
			rep.setOrderTime(entity.getOrderTime());
			rep.setCheckStatus(entity.getCheckStatus());
			rep.setCheckStatusDisp(ordersService.checkStatusEn2Ch(entity.getCheckStatus()));
			
			rep.setOuterOrderCode(entity.getOuterOrderCode());
			rep.setOuterOrderType(entity.getOuterOrderType());
			rep.setBuzzOrderType(entity.getBuzzOrderType());
			rep.setBuzzOrderTypeDisp(this.investorBankOrderService.orderTypeEn2Ch(entity.getBuzzOrderType()));
			
			rep.setOuterUserType(entity.getOuterUserType());
			rep.setBuzzUserType(entity.getBuzzUserType());
			rep.setBuzzUserTypeDisp(this.ordersService.userTypeEn2Ch(entity.getBuzzUserType()));
			rep.setTradeAmount(entity.getTradeAmount());
			rep.setOuterFee(entity.getOuterFee());
			rep.setOuterVoucher(entity.getOuterVoucher());
			rep.setOuterOrderStatus(entity.getOuterOrderStatus());
			rep.setBuzzOrderStatus(entity.getBuzzOrderStatus());
			rep.setBuzzOrderStatusDisp(investorBankOrderService.orderStatusEn2Ch(entity.getBuzzOrderStatus()));
			rep.setOuterOrderTime(entity.getOuterOrderTime());
			rep.setOuterInvestorOid(entity.getOuterInvestorOid());
			rep.setOuterPhoneNum(entity.getOuterPhoneNum());
			rep.setOuterRealName(entity.getOuterRealName());

			rep.setOuterCheckStatus(entity.getOuterCheckStatus());
			rep.setOuterCheckStatusDisp(ordersService.checkStatusEn2Ch(entity.getOuterCheckStatus()));
			rep.setDealStatus(entity.getDealStatus());
			rep.setDealStatusDisp(dealStatusEn2Ch(entity.getDealStatus()));
			
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(list.getTotalElements());
		return pagesRep;
	}
	
	

	private String dealStatusEn2Ch(String dealStatus) {
		if (PlatformFinanceCompareDataResultEntity.RESULT_dealStatus_toDeal.equals(dealStatus)) {
			return "待处理";
		}
		if (PlatformFinanceCompareDataResultEntity.RESULT_dealStatus_dealing.equals(dealStatus)) {
			return "处理中";
		}
		if (PlatformFinanceCompareDataResultEntity.RESULT_dealStatus_dealt.equals(dealStatus)) {
			return "已处理";
		}
		
		return dealStatus;
	}
	
	
	
}
