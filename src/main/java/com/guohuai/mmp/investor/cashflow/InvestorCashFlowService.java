package com.guohuai.mmp.investor.cashflow;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.operate.api.AdminSdk;

@Service
@Transactional
public class InvestorCashFlowService {
	
	Logger logger = LoggerFactory.getLogger(InvestorCashFlowService.class);
	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	
	@Autowired
	private InvestorCashFlowDao investorCashFlowDao;
	@Autowired
	private AdminSdk adminSdk;
	
	public InvestorCashFlowEntity saveEntity(InvestorCashFlowEntity cashFlow) {
		return investorCashFlowDao.save(cashFlow);
	}
	
	/**
	 * 充值提现
	 */
	public InvestorCashFlowEntity createCashFlow(InvestorBankOrderEntity bankOrder) {
		InvestorCashFlowEntity cashFlow = new InvestorCashFlowEntity();
		cashFlow.setOrderOid(bankOrder.getOid());
		cashFlow.setInvestorBaseAccount(bankOrder.getInvestorBaseAccount());
		cashFlow.setTradeAmount(bankOrder.getOrderAmount());
		cashFlow.setTradeType(getTradeTypeByOrderType(bankOrder.getOrderType()));
		
		return this.saveEntity(cashFlow);
	}
	
//	/**
//	 * 收益派发 
//	 */
//	public InvestorCashFlowEntity createCashFlow(InvestorIncomeEntity investorIncomeEntity) {
//		InvestorCashFlowEntity cashFlow = new InvestorCashFlowEntity();
//		cashFlow.setOrderOid(investorIncomeEntity.getOid());
//		cashFlow.setInvestorBaseAccount(investorIncomeEntity.getInvestorBaseAccount());
//		cashFlow.setTradeAmount(investorIncomeEntity.getIncomeAmount());
//		cashFlow.setTradeType(InvestorCashFlowEntity.CASHFLOW_tradeType_interest);
//		return this.saveEntity(cashFlow);
//	}

	/**
	 * 交易订单
	 */
	public InvestorCashFlowEntity createCashFlow(InvestorTradeOrderEntity orderEntity) {
		InvestorCashFlowEntity cashFlow = new InvestorCashFlowEntity();
		cashFlow.setOrderOid(orderEntity.getOid());
		cashFlow.setInvestorBaseAccount(orderEntity.getInvestorBaseAccount());
		cashFlow.setTradeAmount(orderEntity.getOrderAmount());
		cashFlow.setTradeType(getTradeTypeByOrderType(orderEntity.getOrderType()));
		return this.saveEntity(cashFlow);
	}


	private String getTradeTypeByOrderType(String orderType) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_expGoldRedeem;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_invest;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_expGoldInvest;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_normalRedeem;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_dividend.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_dividend;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_clearRedeem;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_cash;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_cashFailed;
		}
		if (InvestorBankOrderEntity.BANKORDER_orderType_deposit.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_deposit;
		}
		if (InvestorBankOrderEntity.BANKORDER_orderType_withdraw.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_withdraw;
		}
		if (InvestorBankOrderEntity.BNAKORDER_orderType_redEnvelope.equals(orderType)) {
			return InvestorCashFlowEntity.CASHFLOW_tradeType_redEnvelope;
		}
		
		return orderType;
	}

	public PageResp<InvestorCashFlowQueryRep> query(Specification<InvestorCashFlowEntity> spec, Pageable pageable) {
		Page<InvestorCashFlowEntity> cas = this.investorCashFlowDao.findAll(spec, pageable);
		PageResp<InvestorCashFlowQueryRep> pagesRep = new PageResp<InvestorCashFlowQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (InvestorCashFlowEntity entity : cas) {
				InvestorCashFlowQueryRep queryRep = new InvestorCashFlowQueryRep();
				queryRep.setTradeAmount(entity.getTradeAmount());
				queryRep.setTradeType(entity.getTradeType());
				queryRep.setTradeTypeDisp(this.tradeTypeEn2Ch(entity.getTradeType()));
				queryRep.setUpdateTime(entity.getUpdateTime());
				queryRep.setCreateTime(entity.getCreateTime());
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}



	public PageResp<InvestorCashFlowQueryCRep> query4Client(Specification<InvestorCashFlowEntity> spec,
			Pageable pageable) {
		Page<InvestorCashFlowEntity> cas = this.investorCashFlowDao.findAll(spec, pageable);
		PageResp<InvestorCashFlowQueryCRep> pagesRep = new PageResp<InvestorCashFlowQueryCRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (InvestorCashFlowEntity entity : cas) {
				InvestorCashFlowQueryCRep queryRep = InvestorCashFlowQueryCRep.builder()
						.orderType(entity.getTradeType())
						.orderTypeDisp(tradeTypeEn2Ch(entity.getTradeType()))
						.tradeAmount(entity.getTradeAmount())
						.createTime(entity.getCreateTime())
						.build();
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	
	/**
	 * 对账查询
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public PageResp<InvestorCashFlow4IncorrectQueryRep> query4Incorrect(Specification<InvestorCashFlowEntity> spec, Pageable pageable) {
		Page<InvestorCashFlowEntity> cas = this.investorCashFlowDao.findAll(spec, pageable);
		PageResp<InvestorCashFlow4IncorrectQueryRep> pagesRep = new PageResp<InvestorCashFlow4IncorrectQueryRep>();
		
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (InvestorCashFlowEntity entity : cas) {
				InvestorCashFlow4IncorrectQueryRep queryRep = new InvestorCashFlow4IncorrectQueryRep();
				queryRep.setPhone(this.kickstar ? StringUtil.kickstarOnPhoneNum(entity.getInvestorBaseAccount().getPhoneNum()) : entity.getInvestorBaseAccount().getPhoneNum());
				queryRep.setDirection(this.tradeType2Direction(entity.getTradeType()));
				queryRep.setOrderType(this.tradeTypeEn2Ch(entity.getTradeType()));
				queryRep.setOrderTime(entity.getCreateTime());
				queryRep.setOrderAmt(entity.getTradeAmount());
				queryRep.setDoCheckType("系统创建");
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	

	private String tradeTypeEn2Ch(String tradeType) {
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_invest.equals(tradeType)) {
			return "投资";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_expGoldInvest.equals(tradeType)) {
			return "投资(体验金)";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_expGoldRedeem.equals(tradeType)) {
			return "赎回(体验金)";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_normalRedeem.equals(tradeType)) {
			return "赎回";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_dividend.equals(tradeType)) {
			return "现金分红";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_clearRedeem.equals(tradeType)) {
			return "赎回(清盘)";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_deposit.equals(tradeType)) {
			return "充值";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_withdraw.equals(tradeType)) {
			return "提现";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_fee.equals(tradeType)) {
			return "手续费";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_cash.equals(tradeType)) {
			return "还本付息";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_cashFailed.equals(tradeType)) {
			return "募集失败退款";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_redEnvelope.equals(tradeType)) {
			return "现金红包";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_rollIn.equals(tradeType)) {
			return "从主账户转账";
		}
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_rollOut.equals(tradeType)) {
			return "向子账号转账";
		}
		
		return tradeType;
	}
	
	/**
	 * 将交易类型转换为出入款
	 * @param tradeType
	 * @return
	 */
	public String tradeType2Direction(String tradeType) {
		
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_invest.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_expGoldInvest.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_reInvest.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_buy.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_withdraw.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_fee.equals(tradeType)
//				InvestorCashFlowEntity.CASHFLOW_tradeType_redPackets.equals(tradeType) || 
				) {
			return "出款";
		}
		
		if (InvestorCashFlowEntity.CASHFLOW_tradeType_expGoldRedeem.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_reRedeem.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_writeOff.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_normalRedeem.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_fastRedeem.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_clearRedeem.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_refund.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_deposit.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_repayLoan.equals(tradeType) ||
//				InvestorCashFlowEntity.CASHFLOW_tradeType_repayInterest.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_cash.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_cashFailed.equals(tradeType) || 
//				InvestorCashFlowEntity.CASHFLOW_tradeType_interest.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_dividend.equals(tradeType) || 
				InvestorCashFlowEntity.CASHFLOW_tradeType_redEnvelope.equals(tradeType)) {
			return "入款";
		}
		
		return tradeType;
	}

	public int createDividendCashFlow() {
	
		return this.investorCashFlowDao.createDividendCashFlow();
	}
	
	public void deleteByOrderCode(String orderOid) {
		logger.info("删除investorCashFlowDao信息orderOid为{}",orderOid);
		InvestorCashFlowEntity entity = this.investorCashFlowDao.findByOrderOid(orderOid);
		this.investorCashFlowDao.delete(entity);
	}
}
