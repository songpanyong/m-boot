package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanInvestService;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.BatchPayDto;
import com.guohuai.mmp.platform.accment.BatchPayRequest;
import com.guohuai.mmp.platform.accment.TransferRequest;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.publisher.dividend.offset.DividendOffsetService;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.sys.CodeConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvestorTradeOrderBatchPayService {

	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private Accment accmentService;
	@Autowired
	private PublisherOffsetService publisherOffsetService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Autowired
	private DividendOffsetService dividendOffsetService;
	
	@Autowired
	private PlanInvestService planInvestService;
	
	
	public void batchPay() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_batchPay.getJobId())) {
			
			this.batchPayLog();
		} else {
			System.out.println("FUC");
		}
	}
	
	public void batchPayLog() {
		JobLogEntity jobLog =  JobLogFactory.getInstance(JobEnum.JOB_jobId_batchPay.getJobId());
		try {
			batchPayDo();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_batchPay.getJobId());
	}

	public void batchPayDo() {
		List<PublisherBaseAccountEntity> publishers = publisherBaseAccountService.findAll();
		for (PublisherBaseAccountEntity entity : publishers) {
			List<InvestorTradeOrderEntity> list = investorTradeOrderDao.getToPayOrders(entity.getOid());
			if (list.isEmpty()) {
				continue;
			}
			
			transferAndBatchPay(entity, list);
		}
	}

	public void transferAndBatchPay(PublisherBaseAccountEntity entity, List<InvestorTradeOrderEntity> list) {
		BatchPayRequest ireq = new BatchPayRequest();
		ireq.setMemberId(entity.getMemberId());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
		List<BatchPayDto> orders = new ArrayList<BatchPayDto>();
		Map<String, BatchPayDto> ordersMap = new HashMap<>();
		
		for (InvestorTradeOrderEntity orderEntity : list) {
			if (orderEntity.getProduct().getRedeemConfirmDays() == 0) {
				TransferRequest req = new TransferRequest();
				req.setPublisherOid(orderEntity.getPublisherBaseAccount().getOid());
				req.setRequestNo(StringUtil.uuid());
				req.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
				req.setOrderCode(orderEntity.getOrderCode());
				req.setIPayNo(this.seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_redeemPayNo));
				req.setInvestorOid(orderEntity.getInvestorBaseAccount().getOid());
				req.setOrderType(AccParam.OrderType.REDEEM.toString());
				req.setOrderAmount(orderEntity.getPayAmount());
				req.setVoucher(BigDecimal.ZERO);
				req.setOrderTime(DateUtil.format(orderEntity.getOrderTime(), DateUtil.fullDatePattern));
				req.setUserType(AccParam.UserType.INVESTOR.toString());
				req.setRemark("remark:");
				req.setOrderDesc("orderDesc:");
				//Distinguish the wishplan and plain product
				if (null != orderEntity.getWishplanOid()) {
					req.setOriginBranch(InvestorTradeOrderEntity.TRADEORDER_originBranch_whishMiddle);
				}
				BaseResp rep = accmentService.transfer(req);
				if (0 != rep.getErrorCode()) {
					orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closePayFailed);
				} else {
					orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closed);
					this.investorBaseAccountService.updateBalance(orderEntity.getInvestorBaseAccount());
					publisherBaseAccountService.updateBalance(orderEntity.getPublisherBaseAccount());
					//TODO: 
					/**
					 *  增加活、定期产品赎回 到账时间（t+0）
					 *  
					 **/
					orderEntity.setRedeemToAccountTime(DateUtil.getSqlCurrentDate());
					investorTradeOrderDao.save(orderEntity);
					//Record wish redeem.
					if (orderEntity.getWishplanOid() != null) {
						updateWishplanRedeem(orderEntity);
					}
					
				}
			} else {
				BatchPayDto dto = new BatchPayDto();
				dto.setOrderCode(orderEntity.getOrderCode());
				dto.setIPayNo(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_redeemPayNo));
				dto.setMemberId(orderEntity.getInvestorBaseAccount().getMemberId());
				dto.setOrderType(AccParam.OrderType.REDEEM.toString());
				dto.setOrderAmount(orderEntity.getPayAmount());
				dto.setOrderDesc("orderDesc:赎回");
				dto.setOrderTime(DateUtil.format(orderEntity.getOrderTime(), DateUtil.fullDatePattern));
				dto.setRemark("remark:赎回");
				//Record wish redeem. T + 1
				if (null != orderEntity.getWishplanOid()) {
					dto.setOriginBranch(InvestorTradeOrderEntity.TRADEORDER_originBranch_whishMiddle);
				}
				orders.add(dto);//t+n产品的list集合
				ordersMap.put(orderEntity.getOrderCode(), dto);//t+n产品以map集合进行保存
			}
		}
		ireq.setOrders(orders);
		
		BaseResp irep = accmentService.batchPay(ireq);
		String closeStatus = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeToPay;
		if (irep.getErrorCode() == 0) {
			closeStatus = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeToPay;
		} else {
			closeStatus = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeSubmitFailed;
		}
		for (InvestorTradeOrderEntity orderEntity : list) {
			if (ordersMap.containsKey(orderEntity.getOrderCode())){
				orderEntity.setPublisherCloseStatus(closeStatus);
			}
		}
		this.investorTradeOrderDao.save(list);
	}

	public boolean notify(OrderNotifyReq ireq) {
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderDao.findByOrderCode(ireq.getOrderCode());
		if (PayParam.ReturnCode.RC0000.toString().equals(ireq.getReturnCode())) {
			orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closed);
			/**
			 *  增加活、定期产品赎回 到账时间（t+n）
			 *  
			 **/
			orderEntity.setRedeemToAccountTime(DateUtil.getSqlCurrentDate());
			
		} else {
			orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closePayFailed);
		}
		this.investorTradeOrderDao.save(orderEntity);
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderEntity.getOrderType())) {
			publisherOffsetService.decreaseToCloseRedeemAmount(orderEntity.getPublisherOffset().getOid());
		}
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_dividend.equals(orderEntity.getOrderType())) {
			dividendOffsetService.decreaseToCloseRedeemAmount(orderEntity.getDividendOffset().getOid());
		}
		
		//Record wish redeem. T + N
		if (orderEntity.getWishplanOid() != null) {
			updateWishplanRedeem(orderEntity);
		}
		/** 同步投资人余额 */
		investorBaseAccountService.updateBalance(orderEntity.getInvestorBaseAccount());
		/** 同步发行人余额 */
		this.publisherBaseAccountService.updateBalance(orderEntity.getPublisherBaseAccount());
		return true;
	}
	
	/**
	 * 
	 * @param orderEntity
	 */
	private void updateWishplanRedeem(InvestorTradeOrderEntity orderEntity) {
		if (orderEntity.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem)
			|| orderEntity.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem)
		    || orderEntity.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_cash)
		    || orderEntity.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed)) {
			planInvestService.incomeAndComplete(orderEntity.getPayAmount(), orderEntity.getWishplanOid());
		} else {
			planInvestService.addIncome(orderEntity.getPayAmount(), orderEntity.getWishplanOid());
		}
	 }
}
