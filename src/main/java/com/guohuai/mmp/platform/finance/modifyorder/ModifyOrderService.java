package com.guohuai.mmp.platform.finance.modifyorder;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.channel.ChannelService;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderDao;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorInvestTradeOrderExtService;
import com.guohuai.mmp.investor.tradeorder.InvestorRedeemTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.check.AbandonReq;
import com.guohuai.mmp.investor.tradeorder.check.CheckOrderReq;
import com.guohuai.mmp.investor.tradeorder.check.InvestorAbandonTradeOrderService;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckDao;
import com.guohuai.mmp.platform.finance.data.PlatformFinanceCompareDataDao;
import com.guohuai.mmp.platform.finance.data.PlatformFinanceCompareDataEntity;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultEntity;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;

@Service
@Transactional
public class ModifyOrderService {
	Logger logger = LoggerFactory.getLogger(ModifyOrderService.class);
	@Autowired
	ModifyOrderDao modifyOrderDao;
	@Autowired
	ModifyOrderNewService modifyOrderNewService;
	@Autowired
	PlatformFinanceCheckDao platformFinanceCheckDao;
	@Autowired
	InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	InvestorBankOrderDao investorBankOrderDao;
	@Autowired
	ProductService productService;
	@Autowired
	InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	ChannelService channelService;
	@Autowired
	InvestorAbandonTradeOrderService investorAbandonTradeOrderService;
	@Autowired
	InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;
	@Autowired
	InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	@Autowired
	private PlatformFinanceCompareDataDao platformFinanceCompareDataDao;
	/**
	 * 查询补帐结果
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public PageResp<ModifyOrderRep> modifyOrderList(Specification<ModifyOrderEntity> spec, Pageable pageable) {
		Page<ModifyOrderEntity> list = this.modifyOrderDao.findAll(spec, pageable);
		PageResp<ModifyOrderRep> pagesRep = new PageResp<ModifyOrderRep>();
		for (ModifyOrderEntity entity : list) {
			ModifyOrderRep rep = new ModifyOrderRep();
			rep.setOid(entity.getOid());
			rep.setCheckCode(entity.getFinanceCheck().getCheckCode());
			rep.setApproveStatus(entity.getApproveStatus());
			rep.setInvestorOid(entity.getInvestorOid());
			rep.setOpType(entity.getOpType());
			rep.setOrderAmount(entity.getOrderAmount());
			rep.setOrderCode(entity.getOrderCode());
			rep.setPostmodifyStatus(entity.getPostmodifyStatus());
			rep.setPremodifyStatus(entity.getPremodifyStatus());
			rep.setDealStatus(entity.getDealStatus());
			rep.setResultOid(entity.getResultOid());
			rep.setProductOid(entity.getProductOid());
			rep.setTradeType(entity.getTradeType());
			rep.setReason(entity.getReason());
			rep.setOperator(entity.getOperator());
			rep.setCreateTime(entity.getCreateTime());
			rep.setUpdateTime(entity.getUpdateTime());
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(list.getTotalElements());
		return pagesRep;
	}

	/**
	 * 废单录入
	 * 
	 * @param req
	 * @return
	 */
	public BaseResp saveModifyOrder(ModifyOrderReq req) {
		BaseResp rep = new BaseResp();
		try {
			
			ModifyOrderEntity entity =modifyOrderDao.findByOrderCode(req.getOrderCode());
			if(null != entity){
				throw new AMPException("补帐订单重复!");
			}else{
				entity=new ModifyOrderEntity();
			}
//			OrderQueryResponse oqr=investorBaseAccountService.getAccountOrderByOrderCode(req.getOrderCode());
//			entity.setProductOid(oqr.getRelationProductNo());
//			entity.setOperator(req.getOperator());
//			entity.setOrderAmount(req.getOrderAmount());
//			entity.setFinanceCheck(platformFinanceCheckDao.findOne(req.getCheckOid()));
//			entity.setInvestorOid(req.getInvestorOid());
//			entity.setOpType(req.getOpType());
//			entity.setTradeType(req.getTradeType());
//			entity.setOrderCode(req.getOrderCode());
//			entity.setOrderTime(req.getOrderTime());
//			entity.setApproveStatus(ModifyOrderEntity.APPROVESTATUS_TOAPPROVE);
//			entity.setDealStatus(ModifyOrderEntity.DEALSTATUS_TODEAL);
//			entity.setResultOid(req.getResultOid());
//			entity.setPremodifyStatus(req.getPremodifyStatus());
//			entity.setCreateTime(DateUtil.getSqlCurrentDate());
//			entity.setUpdateTime(DateUtil.getSqlCurrentDate());
////			modifyOrderDao.save(entity);
//			platformFinanceCompareDataResultNewService.updateDealStatusByOid(req.getResultOid(),
//					PlatformFinanceCompareDataResultEntity.DEALSTATUS_DEALING);
		} catch (AMPException e) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("新增失败！");
		}
		return rep;
	}

	/**
	 * 通过审核
	 * 
	 * @param oid
	 * @param resultOid
	 * @param approveStatus
	 * @param operator
	 * @return
	 */
	public BaseResp modifyOrderApprove(String oid, String resultOid, String approveStatus, String operator) {
		BaseResp rep = new BaseResp();
		try {
			int num= 0;
			if(ModifyOrderEntity.APPROVESTATUS_PASS.equals(approveStatus)){
				num=modifyOrderDao.modifyOrderPassApprove(oid, approveStatus, operator);
			}else{
				num=modifyOrderDao.modifyOrderRefusedApprove(oid, approveStatus, operator);
			}
			if(num < 1){
				throw new AMPException("审核状态异常!");
			}
			ModifyOrderEntity entity = modifyOrderDao.findOne(oid);
			if (null != entity && ModifyOrderEntity.APPROVESTATUS_PASS.equals(approveStatus)) {
				/*** 1.设置对账审核为正在处理 ***/
				int result = modifyOrderNewService.updateDealStatusDealingByOrderCode(entity.getOrderCode());
				if (result > 0) {// 如果没有更新，说明数据已经更新完成,或者正在处理
					// 充值或者提现
					if (InvestorBankOrderEntity.BANKORDER_orderType_deposit.equals(entity.getTradeType())
							|| InvestorBankOrderEntity.BANKORDER_orderType_withdraw.equals(entity.getTradeType())) {
						//多帐废单
						if (ModifyOrderEntity.OPTYPE_REMOVEORDER.equals(entity.getOpType())) {
							/** 银行委托单作废 */
							investorBankOrderDao.updateBankOrderToAbandonedByOrderCode(entity.getOrderCode());
						//少账补单
						}else if(ModifyOrderEntity.OPTYPE_FIXORDER.equals(entity.getOpType())){
							createInvestorBankOrder(entity);
						}
					}
					if (entity.getTradeType().equals("redeem") || entity.getTradeType().equals("normalRedeem")) {// 赎回
						//多帐废单,重新调用结算
						if (ModifyOrderEntity.OPTYPE_REMOVEORDER.equals(entity.getOpType())) {
							InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(entity.getOrderCode());
							
						//少账补单
						}else if(ModifyOrderEntity.OPTYPE_FIXORDER.equals(entity.getOpType())){
							CheckOrderReq req=new CheckOrderReq();
		                    req.setMemberId(entity.getInvestorOid());
		                    req.setProductOid(entity.getProductOid());
		                    req.setOrderCode(entity.getOrderCode());
		                    req.setMoneyVolume(entity.getOrderAmount());
		                    req.setOrderTime(entity.getOrderTime());
		                    investorInvestTradeOrderExtService.resumitRedeemOrder(req);
						}
					}
					if (entity.getTradeType().equals("invest")) {// 申购
						//多帐废单
						if (ModifyOrderEntity.OPTYPE_REMOVEORDER.equals(entity.getOpType())) {
							 AbandonReq req =new AbandonReq();
//			                req.setOrderAmount(entity.getOrderAmount());
			                req.setOrderCode(entity.getOrderCode());
			                investorAbandonTradeOrderService.abandon(req);
						//少账补单
						}else if(ModifyOrderEntity.OPTYPE_FIXORDER.equals(entity.getOpType())){
							CheckOrderReq req=new CheckOrderReq();
		                    req.setProductOid(entity.getProductOid());
		                    req.setMoneyVolume(entity.getOrderAmount());
		                    req.setOrderCode(entity.getOrderCode());
		                    req.setMemberId(entity.getInvestorOid());
		                    req.setOrderTime(entity.getOrderTime());
		                    investorInvestTradeOrderExtService.resumitInvestOrder(req);
							
						}
					}
					modifyOrderNewService.updateDealStatusDealtByOrderCode(entity.getOrderCode());
				}
			}
			/** 设置当前对接结果为已处理 */
//			platformFinanceCompareDataResultNewService.updateDealStatusByOid(resultOid,
//					PlatformFinanceCompareDataResultEntity.DEALSTATUS_DEALT);
		} catch (AMPException e) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("审核操作失败！");
		}
		return rep;
	}

	/**
	 * 批量审核
	 */
	public BaseResp modifyOrderBatchApprove(List<String> oids, String operator) {
		String approveStatus = "pass";
		BaseResp rep = new BaseResp();
		try {
			if (null != oids && oids.size() > 0) {
				for (int i = 0; i < oids.size(); i++) {
					String oid = oids.get(i);
					int num= 0;
					if(ModifyOrderEntity.APPROVESTATUS_PASS.equals(approveStatus)){
						num=modifyOrderDao.modifyOrderPassApprove(oid, approveStatus, operator);
					}else{
						num=modifyOrderDao.modifyOrderRefusedApprove(oid, approveStatus, operator);
					}
					if(num < 1){
						throw new AMPException("审核状态异常!");
					}
					ModifyOrderEntity entity = modifyOrderDao.findOne(oid);
					if (null != entity) {
						/*** 1.设置对账审核为正在处理 ***/
						int result = modifyOrderNewService.updateDealStatusDealingByOrderCode(entity.getOrderCode());
						if (result > 0) {// 如果没有更新，说明数据已经更新完成,或者正在处理
							// 充值或者提现
							if (InvestorBankOrderEntity.BANKORDER_orderType_deposit.equals(entity.getTradeType())
									|| InvestorBankOrderEntity.BANKORDER_orderType_withdraw.equals(entity.getTradeType())) {
								//多帐废单
								if (ModifyOrderEntity.OPTYPE_REMOVEORDER.equals(entity.getOpType())) {
									/** 银行委托单作废 */
									investorBankOrderDao.updateBankOrderToAbandonedByOrderCode(entity.getOrderCode());
								//少账补单
								}else if(ModifyOrderEntity.OPTYPE_FIXORDER.equals(entity.getOpType())){
									createInvestorBankOrder(entity);
								}
							}
							if (entity.getTradeType().equals("redeem") || entity.getTradeType().equals("normalRedeem")) {// 赎回
								//多帐废单,重新调用结算
								if (ModifyOrderEntity.OPTYPE_REMOVEORDER.equals(entity.getOpType())) {
									InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(entity.getOrderCode());
									
								//少账补单
								}else if(ModifyOrderEntity.OPTYPE_FIXORDER.equals(entity.getOpType())){
									CheckOrderReq req=new CheckOrderReq();
				                    req.setMemberId(entity.getInvestorOid());
				                    req.setProductOid(entity.getProductOid());
				                    req.setOrderCode(entity.getOrderCode());
				                    req.setMoneyVolume(entity.getOrderAmount());
				                    req.setOrderTime(entity.getOrderTime());
				                    investorInvestTradeOrderExtService.resumitRedeemOrder(req);
								}
							}
							if (entity.getTradeType().equals("invest")) {// 申购
								// 接口 大超
								//多帐废单
								if (ModifyOrderEntity.OPTYPE_REMOVEORDER.equals(entity.getOpType())) {
									AbandonReq req =new AbandonReq();
//					                req.setOrderAmount(entity.getOrderAmount());
					                req.setOrderCode(entity.getOrderCode());
					                investorAbandonTradeOrderService.abandon(req);
								//少账补单
								}else if(ModifyOrderEntity.OPTYPE_FIXORDER.equals(entity.getOpType())){
									CheckOrderReq req=new CheckOrderReq();
				                    req.setProductOid(entity.getProductOid());
				                    req.setMoneyVolume(entity.getOrderAmount());
				                    req.setOrderCode(entity.getOrderCode());
				                    req.setMemberId(entity.getInvestorOid());
				                    req.setOrderTime(entity.getOrderTime());
				                    investorInvestTradeOrderExtService.resumitInvestOrder(req);
								}
							}
							modifyOrderNewService.updateDealStatusDealtByOrderCode(entity.getOrderCode());
							/** 设置当前对接结果为已处理 */
//							platformFinanceCompareDataResultNewService.updateDealStatusByOid(entity.getResultOid(),
//									PlatformFinanceCompareDataResultEntity.DEALSTATUS_DEALT);
						}
					}
				}
			}
		} catch (AMPException e) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("审核操作失败！");
		}
		return rep;
	}
	private InvestorBankOrderEntity createInvestorBankOrder(ModifyOrderEntity modifyOrderEntity) {
		PlatformFinanceCompareDataEntity dataEntity=platformFinanceCompareDataDao.findByOrderCode(modifyOrderEntity.getOrderCode());
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(modifyOrderEntity.getOrderCode());
		bankOrder.setInvestorBaseAccount(investorBaseAccountDao.findByMemberId(modifyOrderEntity.getInvestorOid()));
		bankOrder.setOrderAmount(modifyOrderEntity.getOrderAmount());
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
		if(!"1".equals(dataEntity.getOrderStatus())){
			bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		}
		bankOrder.setOrderTime(modifyOrderEntity.getOrderTime());
		bankOrder.setOrderType(modifyOrderEntity.getTradeType());
		return  this.investorBankOrderDao.save(bankOrder);
	}
}
