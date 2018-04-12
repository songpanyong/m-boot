package com.guohuai.mmp.backstage.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.backstage.SonAccountRelateEntity;
import com.guohuai.mmp.backstage.dao.SonAccountRelateDao;
import com.guohuai.mmp.backstage.rep.SonAccountUserInfoRep;
import com.guohuai.mmp.backstage.rep.TransferMoneyRep;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderDao;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsEntity;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.TradeUtil;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProfitListForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanBaseService;
import com.guohuai.mmp.sys.CodeConstants;

@Service
public class BackStageRelateSonAccountService {

	@Autowired
	InvestorBankOrderDao investorBankOrderDao;
	@Autowired
	SonAccountRelateDao sonAccountRelateDao;
	@Autowired
	InvestorStatisticsService investorStatisticsService;
	@Autowired
	private PlanBaseService planBaseService;
	
	@Value("${seq.env}")
	private String pre;
	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	
	
	
	/**
	 * 后台查询主子账户的转账列表（通过字段rollOut来查询主账户的id）
	 * 
	 * */
	public PageResp<TransferMoneyRep> query(Specification<InvestorBankOrderEntity> spec, Pageable pages) {
		//从数据库中查出所有相关的操作
		Page<InvestorBankOrderEntity>  investorBankOrderEntity = this.investorBankOrderDao.findAll(spec,pages);
		PageResp<TransferMoneyRep> pageResp = new PageResp<TransferMoneyRep>();
		List<TransferMoneyRep> list = new ArrayList<TransferMoneyRep>();
		
		if(investorBankOrderEntity!=null&&investorBankOrderEntity.getSize()>0&&investorBankOrderEntity.getTotalElements()>0){
			//将对象进行循环遍历
			for(InvestorBankOrderEntity iboe:investorBankOrderEntity){
				TransferMoneyRep rep = new TransferMoneyRep();
				//获取主账户的对象
				InvestorBaseAccountEntity parentAccount =  iboe.getInvestorBaseAccount();
				rep.setPid(parentAccount.getOid());			
				rep.setParentPhone(this.kickstar ? StringUtil.kickstarOnPhoneNum(parentAccount.getPhoneNum()) : parentAccount.getPhoneNum());
				rep.setPRealname(parentAccount.getRealName());

				//通过主账户获取子账户的对象
				InvestorBaseAccountEntity sonAccount =  getParentAccountByRollOut(iboe.getOrderCode());

				rep.setSonPhone(sonAccount.getPhoneNum());
				rep.setSRealname(sonAccount.getRealName());
				rep.setSid(sonAccount.getOid());			
				rep.setTradeStatus(iboe.getOrderStatus());			
				rep.setTradeStatusDesc(TradeUtil.orderStatusEn2Ch(iboe.getOrderStatus()));
				rep.setTradeOrder(iboe.getOrderCode());
				rep.setFee(iboe.getFee());
				rep.setTransferAmount(iboe.getOrderAmount());
				rep.setTransferTime(iboe.getCreateTime());
							
				list.add(rep);
			}
			pageResp.setRows(list);
			pageResp.setTotal(investorBankOrderEntity.getTotalElements());					
		}
		return pageResp;	
	}
	
	/**
	 * 通过rollOut产生的订单号来获取rollIn的主账户的对象
	 * 
	 * */
	public InvestorBaseAccountEntity getParentAccountByRollOut(String orderCode) {

		String code = orderCode.substring(orderCode.length() - 16, orderCode.length());
		String code2 = StringUtils.substringBefore(orderCode, code);
		
		String code3 = code2.substring(0, code2.length() - 2);
		String rollInCode = code3 + "" + CodeConstants.PAYMENT_RollIn + "" + code;
		System.out.println(rollInCode);

		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(rollInCode);
		if (bankOrder != null) {
			InvestorBaseAccountEntity sonAccount = bankOrder.getInvestorBaseAccount();
			if (sonAccount != null) {
				return sonAccount;
			}
		}
		throw new AMPException("订单号不存在");
	}
			

	/**
	 * 所有主账户下的子账户   
	 * 
	 * */
	public PageResp<SonAccountUserInfoRep> querySonAccount(Specification<SonAccountRelateEntity> spec ,Pageable page) {
			Page<SonAccountRelateEntity> sonAccount =  this.sonAccountRelateDao.findAll(spec,page);
			
			
			PageResp<SonAccountUserInfoRep> rep = new PageResp<SonAccountUserInfoRep>();
			if(sonAccount!=null&&sonAccount.getSize()>0&&sonAccount.getTotalElements()>0){
				for(SonAccountRelateEntity son :sonAccount){
					/** 获取心愿计划的累计投资金额 */
					PlanProfitListForm  planProfitListForm = this.planBaseService.satisticsPlanList(son.getSonBaseAccount().getOid());
					SonAccountUserInfoRep queryInfo = new SonAccountUserInfoRep();
					InvestorStatisticsEntity staticsEntity = this.investorStatisticsService.findByInvestorBaseAccount(son.getSonBaseAccount());
					queryInfo.setSid(son.getSonBaseAccount().getOid());
					queryInfo.setPid(son.getInvestorBaseAccount().getOid());
					queryInfo.setNickName(son.getNickname());
					queryInfo.setRealName(son.getSonBaseAccount().getRealName());
					queryInfo.setPhoneNum(this.kickstar ? StringUtil.kickstarOnPhoneNum(son.getInvestorBaseAccount().getPhoneNum()) : son.getInvestorBaseAccount().getPhoneNum());//主账户 的注册的手机号
					queryInfo.setStatus(son.getSonBaseAccount().getStatus());
					if(queryInfo.getStatus().equals("normal")){
						queryInfo.setStatusDesc("正常");
					}else{
						queryInfo.setStatusDesc("锁定");
					}
					queryInfo.setRelationStatus(son.getStatus());
					queryInfo.setApplyBalance(son.getSonBaseAccount().getApplyAvailableBalance());//子账户的可用余额
					queryInfo.setTotalInvestAmount(staticsEntity.getTotalInvestAmount()
							.add(new BigDecimal(planProfitListForm.getTotalholdInvestAmount())));// 子账户的总投资
					queryInfo.setTotalIncomeAmount(staticsEntity.getTotalIncomeAmount().add(staticsEntity.getWishplanIncome()));//子账户的总收益	
					queryInfo.setCreateTime(son.getSonBaseAccount().getCreateTime());//子账户的创建时间
					queryInfo.setBalance(son.getSonBaseAccount().getBalance());//子账户余额
					rep.getRows().add(queryInfo);			
			}
			}
				rep.setTotal(sonAccount.getTotalElements());
		 return rep;
		
	}		
	
	
}
