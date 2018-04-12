package com.guohuai.mmp.platform.publisher.offset;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.calendar.TradeCalendarService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.StaticProperties;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.CloseRequest;
import com.guohuai.mmp.platform.accountingnotify.AccountingNotifyService;
import com.guohuai.mmp.platform.publisher.offsetlog.PublisherOffsetLogEntity;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetEntity;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetService;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetServiceRequiresNew;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.serialtask.PublisherClearTaskParams;
import com.guohuai.mmp.serialtask.PublisherConfirmTaskParams;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskRequireNewService;
import com.guohuai.mmp.serialtask.SerialTaskService;
import com.guohuai.mmp.sys.CodeConstants;

@Service
@Transactional
public class PublisherOffsetService {

	Logger logger = LoggerFactory.getLogger(PublisherOffsetService.class);

	@Autowired
	private PublisherOffsetDao publisherOffsetDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private AccountingNotifyService accountingNotifyService;
	@Autowired
	private ProductOffsetService productOffsetService;
	@Autowired
	private ProductService productService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private TradeCalendarService tradeCalendarService;
	@Autowired
	private PublisherOffsetServiceRequiresNew requiresNewService;
	@Autowired
	private ProductOffsetServiceRequiresNew productOffsetServiceRequiresNew;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private Accment accmentService;
	@Autowired
	private SerialTaskRequireNewService serialTaskRequireNewService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	
	
	public PublisherOffsetEntity updateEntity(PublisherOffsetEntity offset) {
		return this.publisherOffsetDao.save(offset);
	}
	
	public PublisherOffsetEntity getLatestOffset(InvestorTradeOrderEntity orderEntity, Date confirmDate) {
		return this.getLatestOffset(orderEntity, confirmDate, true);
	}
	/**
	 * 获取最新的轧差批次
	 */
	public PublisherOffsetEntity getLatestOffset(InvestorTradeOrderEntity tradeOrder, Date confirmDate, boolean isPositive) {
		BigDecimal orderAmount = tradeOrder.getOrderAmount();
		if (!isPositive) {
			orderAmount = orderAmount.negate();
		}
		PublisherOffsetEntity offset = this.getLatestOffset(tradeOrder.getPublisherBaseAccount(), confirmDate);
		if (tradeOrder.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_invest) ||
				tradeOrder.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_reInvest)) {
			this.increaseInvest(offset, orderAmount);
		} else {
			this.increaseRedeem(offset, orderAmount);
		}
		return offset;
	}
	
	public PublisherOffsetEntity getLatestOffset(PublisherBaseAccountEntity publisherBaseAccount, Date confirmDate) {
		if (publisherBaseAccount == null) {
			// error.define[20015]=发行人不存在(CODE:20015)
			throw AMPException.getException(20015);
		}
		PublisherOffsetEntity offset = this.publisherOffsetDao.getLatestOffset(publisherBaseAccount, DateUtil.defaultFormat(confirmDate));
		if (offset == null) {
			try {
				offset = requiresNewService.createEntity(publisherBaseAccount, DateUtil.defaultFormat(confirmDate));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				throw new AMPException("新建轧差批次异常");
				//return this.getLatestOffset(spv, confirmDate);
			}
		}
		if (!PublisherOffsetEntity.OFFSET_clearStatus_toClear.equals(offset.getClearStatus())) {
			// error.define[30025]=轧差批次状态异常，非待清算(CODE:30025)
			throw new AMPException(30025);
		}
		return offset;
	}
	
	/**
	 * 清算 
	 */
	public BaseResp clear(String offsetOid) {

		PublisherOffsetEntity offset = this.findByOid(offsetOid);

		if (!PublisherOffsetEntity.OFFSET_clearStatus_toClear.equals(offset.getClearStatus())) {
			// error.define[20021]=清算状态异常(CODE:20021)
			throw AMPException.getException(20021);
		}

//		if (DateUtil.daysBetween(DateUtil.getSqlDate(), offset.getOffsetDate()) < 0) {
		if (!DateUtil.isLessThanOrEqualToday(offset.getOffsetDate())) {
			// error.define[30062]=清算时间异常(CODE:30062)
			throw AMPException.getException(30062);
		}

		if (0 != this.publisherOffsetDao.beforeOffsetDate(offset.getOffsetDate(),
				offset.getPublisherBaseAccount().getOid())) {
			// error.define[30063]=请优先处理之前的轧差批次(CODE:30063)
			throw AMPException.getException(30063);
		}

		PublisherClearTaskParams param = new PublisherClearTaskParams();
		param.setOffsetOid(offsetOid);

		SerialTaskReq<PublisherClearTaskParams> req = new SerialTaskReq<PublisherClearTaskParams>();
		req.setTaskCode(SerialTaskEntity.TASK_taskCode_publisherClear);
		req.setTaskParams(param);
		/** 清算去重 */
		this.serialTaskService.findByTaskCodeAndTaskParam(req);

		serialTaskService.createSerialTask(req);
		return new BaseResp();
	}
	
	public void clearDo(String offsetOid, String taskOid) {
		

		int i = publisherOffsetDao.updateClearStatus(offsetOid, PublisherOffsetEntity.OFFSET_clearStatus_cleared);
		if (i < 1) {
			// error.define[20021]=清算状态异常(CODE:20021)
			throw new AMPException(20021);
		}
		productOffsetService.updateClearStatus(offsetOid, PublisherOffsetEntity.OFFSET_clearStatus_cleared);
		investorTradeOrderService.updatePublisherClearStatus(offsetOid,
				InvestorTradeOrderEntity.TRADEORDER_publisherClearStatus_cleared);
		serialTaskRequireNewService.updateTime(taskOid);
		
	}
	
	/**
	 * 结算
	 */
	public BaseResp close(String offsetOid) {
		
		PublisherOffsetEntity offset = this.findByOid(offsetOid);
		if (!PublisherOffsetEntity.OFFSET_clearStatus_cleared.equals(offset.getClearStatus())) {
			// error.define[20022]=结算状态异常(CODE:20022)
			throw AMPException.getException(20022);
		}
		
		if (PublisherOffsetEntity.OFFSET_closeStatus_closed.equals(offset.getCloseStatus())) {
			// error.define[20022]=结算状态异常(CODE:20022)
			throw AMPException.getException(20022);
		}
		
		PublisherBaseAccountEntity baseAccount = offset.getPublisherBaseAccount();
		// 如果净头寸小于0，赎回大于投资
		
		if (offset.getNetPosition().compareTo(BigDecimal.ZERO) < 0) {
			if (baseAccount.getAvailableAmountBalance().compareTo(offset.getNetPosition()) < 0) {
				throw new AMPException("可用金余额不足,请放款");
			}
		}
		BaseResp rep  = this.closeDo(offsetOid, null);
		
//		PublisherCloseTaskParams param = new PublisherCloseTaskParams();
//		param.setOffsetOid(offsetOid);
//		
//		SerialTaskReq<PublisherCloseTaskParams> req = new SerialTaskReq<PublisherCloseTaskParams>();
//		req.setTaskCode(SerialTaskEntity.TASK_taskCode_publisherClose);
//		req.setTaskParams(param);
//		/** 结算去重 */
//		this.serialTaskService.findByTaskCodeAndTaskParam(req);
//		serialTaskService.createSerialTask(req);
		return rep;
	}
	
	
	
	public BaseResp closeDo(String offsetOid, String taskOid) {
		PublisherOffsetEntity offset = this.findByOid(offsetOid);
		BaseResp baseResp = new BaseResp();
		if (offset.getRedeemAmount().compareTo(BigDecimal.ZERO) == 0) {
			String closeStatus4TradeOrder = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closed;
			String closeStatus4Publisher = PublisherOffsetEntity.OFFSET_closeStatus_closed;
			String closeStatus4Product = ProductOffsetEntity.OFFSET_closeStatus_closed;
			
			CloseRequest ireq = new CloseRequest();
			ireq.setPublisherUserOid(offset.getPublisherBaseAccount().getMemberId());
			ireq.setNettingBalance(offset.getNetPosition());
			ireq.setRequestNo(StringUtil.uuid());
			ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
			if (offset.getNetPosition().compareTo(BigDecimal.ZERO) > 0)  {
				ireq.setOrderCode(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_spvABCollectPayNo));
				ireq.setOrderType(AccParam.OrderType.ABCOLLECT.toString());
			} else {
				ireq.setOrderCode(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_spvABPayPayNo));
				ireq.setOrderType(AccParam.OrderType.ABPPAY.toString());
			}
			
			ireq.setOrderTime(DateUtil.format(DateUtil.getSqlCurrentDate(), DateUtil.fullDatePattern));
			ireq.setUserType(AccParam.UserType.SPV.toString());
			ireq.setRemark("remark");
			ireq.setOrderDesc("orderDesc");
			ireq.setInvestAmount(offset.getInvestAmount());
			ireq.setRedeemAmount(offset.getRedeemAmount());
			
			baseResp = this.accmentService.close(ireq);
			if (0 != baseResp.getErrorCode()) {
				closeStatus4TradeOrder = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeSubmitFailed;
				closeStatus4Publisher = PublisherOffsetEntity.OFFSET_closeStatus_closeSubmitFailed;
				closeStatus4Product = ProductOffsetEntity.OFFSET_closeStatus_closeSubmitFailed;
			} else {
				this.publisherBaseAccountService.updateBalance(offset.getPublisherBaseAccount());
			}
			updateCloseStatus(offset, closeStatus4TradeOrder, closeStatus4Publisher, closeStatus4Product);
		} else {
			String closeStatus4TradeOrder = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closing;
			String closeStatus4Publisher = PublisherOffsetEntity.OFFSET_closeStatus_closing;
			String closeStatus4Product = ProductOffsetEntity.OFFSET_closeStatus_closing;
			
			
			CloseRequest ireq = new CloseRequest();
			ireq.setPublisherUserOid(offset.getPublisherBaseAccount().getMemberId());
			ireq.setNettingBalance(offset.getNetPosition());
			ireq.setRequestNo(StringUtil.uuid());
			ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
			if (offset.getNetPosition().compareTo(BigDecimal.ZERO) > 0)  {
				ireq.setOrderCode(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_spvABCollectPayNo));
				ireq.setOrderType(AccParam.OrderType.ABCOLLECT.toString());
			} else {
				ireq.setOrderCode(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_spvABPayPayNo));
				ireq.setOrderType(AccParam.OrderType.ABPPAY.toString());
			}
			
			ireq.setOrderTime(DateUtil.format(DateUtil.getSqlCurrentDate(), DateUtil.fullDatePattern));
			ireq.setUserType(AccParam.UserType.SPV.toString());
			ireq.setRemark("remark");
			ireq.setOrderDesc("orderDesc");
			ireq.setInvestAmount(offset.getInvestAmount());
			ireq.setRedeemAmount(offset.getRedeemAmount());
			baseResp = this.accmentService.close(ireq);
			if (0 != baseResp.getErrorCode()) {
				closeStatus4TradeOrder = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeSubmitFailed;
				closeStatus4Publisher = PublisherOffsetEntity.OFFSET_closeStatus_closeSubmitFailed;
				closeStatus4Product = ProductOffsetEntity.OFFSET_closeStatus_closeSubmitFailed;
				
			} else {
				this.publisherBaseAccountService.updateBalance(offset.getPublisherBaseAccount());
			}
			updateCloseStatus(offset, closeStatus4TradeOrder, closeStatus4Publisher, closeStatus4Product);
			
		}
			
//		serialTaskRequireNewService.updateTime(taskOid);
		return baseResp;
	}
	
	

	private void updateCloseStatus(PublisherOffsetEntity offset, String closeStatus4TradeOrder, String closeStatus4Publisher,
			String closeStatus4Product) {
		int i = this.publisherOffsetDao.updateCloseStatus4Close(offset.getOid(), closeStatus4Publisher,
				PublisherOffsetEntity.OFFSET_closeMan_publisher);
		if (i < 1) {
			// error.define[20022]=结算状态异常(CODE:20022)
			throw AMPException.getException(20022);
		}
		this.productOffsetService.updateCloseStatus4Close(offset.getOid(), closeStatus4Product);
		
		/** 投资单直接更新成已结算 */
		this.investorTradeOrderService.updateCloseStatus4Invest(offset);
		/** 赎回单根据入参更新 */
		this.investorTradeOrderService.updateCloseStatus4Redeem(offset, closeStatus4TradeOrder);
	}

	/**
	 * 生产所有发行人的轧差批次
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void createAllNew() {
		
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_createAllNew)) {
			this.createAllNewLog();
		}
		
	}
	
//	/**
//	 * 银行对账批次号
//	 */
//	@Transactional(value = TxType.REQUIRES_NEW)
//	public void createBankNew() {
//		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jonId_createBankNew)) {
//			this.createBankNLog();
//		}
//		
//	}
	

	
	public void createAllNewLog() {
		JobLogEntity jobLog =  JobLogFactory.getInstance(JobLockEntity.JOB_jobId_createAllNew);
		
		try {
			createAllNewDo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_createAllNew);
		
	}
	
//	public void createBankNLog() {
//		JobLogEntity jobLog =  JobLogFactory.getInstance(JobLockEntity.JOB_jonId_createBankNew);
//		try {
//			createBankNewDo();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			jobLog.setJobMessage(AMPException.getStacktrace(e));
//			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
//		}
//		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
//		this.jobLogService.saveEntity(jobLog);
//		this.jobLockService.resetJob(JobLockEntity.JOB_jonId_createBankNew);
//		
//	}

	private void createAllNewDo() {
		Date curDate = StaticProperties.isIs24() ? DateUtil.getSqlDate() : DateUtil.getAfterDate();
		Date beforeDate = DateUtil.addSQLDays(curDate, -1);
		
		boolean isTradeDate = tradeCalendarService.isTrade(curDate);
		List<PublisherBaseAccountEntity> publishers = this.publisherBaseAccountService.findAll();
		
		for (PublisherBaseAccountEntity spv : publishers) {
			
			Map<String, Product> productMap = new HashMap<String, Product>();
			logger.info("spv.oid:{}", spv.getOid());
			List<Product> t0s = this.productService.findProductT04NewOffset(spv);
			List<Product> tns = this.productService.findProductTn4NewOffset(spv);
			List<Product> productList = new ArrayList<Product>();
			productList.addAll(t0s);
			productList.addAll(tns);
			for (Product product : productList) {
				
				//申购确认日
				if (0 != product.getPurchaseConfirmDays()) {
					String offsetCode = DateUtil.defaultFormat(DateUtil.addSQLDays(curDate, product.getPurchaseConfirmDays()));
					productMap.put(offsetCode, product);
				} else {
					// 当是交易日的情况下，创建轧差批次，但前一日必须不是非交易日
					if (isTradeDate) {
						boolean beforeIsTrade = tradeCalendarService.isTrade(beforeDate);
						if (beforeIsTrade) {
							String offsetCode = DateUtil.defaultFormat(tradeCalendarService.nextTrade(curDate, product.getPurchaseConfirmDays()));
							productMap.put(offsetCode, product);
						}
					} else {
						// 当非交易日情况下，判断前一日是否是交易日，如果是交易日，则创建轧差批次。
						// 订单接收日，为接下来一个交易日，下一个交易日为确认日
						boolean beforeIsTrade = tradeCalendarService.isTrade(beforeDate);
						if (beforeIsTrade) {
							String offsetCode = DateUtil.defaultFormat(tradeCalendarService.nextTrade(curDate, product.getPurchaseConfirmDays() + 1));
							productMap.put(offsetCode, product);
						}
					}
				}
				
				int redeemConfirmDays = null == product.getRedeemConfirmDays() ? 1 : product.getRedeemConfirmDays();
				// 赎回确认日
				if (0 != product.getRedeemConfirmDays()) {
					String offsetCode = DateUtil.defaultFormat(DateUtil.addSQLDays(curDate, redeemConfirmDays));
					productMap.put(offsetCode, product);
				} else {
					// 当是交易日的情况下，创建轧差批次
					if (isTradeDate) {
						boolean beforeIsTrade = tradeCalendarService.isTrade(beforeDate);
						if (beforeIsTrade) {
							String offsetCode = DateUtil.defaultFormat(tradeCalendarService.nextTrade(curDate, redeemConfirmDays));
							productMap.put(offsetCode, product);
						}
					} else {
						// 当非交易日情况下，判断前一日是否是交易日，如果是交易日，则创建轧差批次。
						// 订单接收日，为接下来一个交易日，下一个交易日为确认日
						boolean beforeIsTrade = tradeCalendarService.isTrade(beforeDate);
						if (beforeIsTrade) {
							String offsetCode = DateUtil.defaultFormat(tradeCalendarService.nextTrade(curDate, redeemConfirmDays + 1));
							productMap.put(offsetCode, product);
						}
					}
				}
			}
			Set<String> set = productMap.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String offsetCode = it.next();
				PublisherOffsetEntity offset = this.publisherOffsetDao.findByPublisherBaseAccountAndOffsetCode(spv, offsetCode);
				if (null == offset) {
					try {
						offset = this.requiresNewService.createEntity(spv, offsetCode);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage(), e);
						continue;
					}
				}
				
				ProductOffsetEntity pOffset = this.productOffsetService.findByProductAndOffsetCode(productMap.get(offsetCode), offsetCode);
				if (null == pOffset) {
					try {
						pOffset = this.productOffsetServiceRequiresNew.createEntity(productMap.get(offsetCode), offset);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}
	
//	/**
//	 * 银行定时器
//	 */
//	private void createBankNewDo() {
//		java.util.Date utilDate=new java.util.Date();
//		String date=DateUtil.convertDate2String("yyyy-MM-dd ",DateUtil.lastDate(utilDate));
//		PlatformFinanceCheckEntity checkEntity = this.platformFinanceCheckService.findByCheckCode(PlatformFinanceCheckEntity.PREFIX+date);
//		if (null == checkEntity) {
//			try {
//				platformFinanceCheckNewService.createBankEntity(date);
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.error(e.getMessage(), e);
//			}
//			
//		}
//	}
	


	/**
	 * 增加待清算投资
	 * @param offset
	 * @param investAmount
	 */
	public void increaseInvest(PublisherOffsetEntity offset, BigDecimal investAmount){
		this.publisherOffsetDao.increaseInvest(offset.getOid(), investAmount);
	}
	
	/**
	 * 增加待清算赎回
	 * @param offset
	 * @param investAmount
	 */
	public void increaseRedeem(PublisherOffsetEntity offset,BigDecimal investAmount){
		this.publisherOffsetDao.increaseRedeem(offset.getOid(), investAmount);
	}

	/**
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public PageResp<PublisherOffsetQueryRep> mng(Specification<PublisherOffsetEntity> spec, Pageable pageable) {
		Page<PublisherOffsetEntity> cas = this.publisherOffsetDao.findAll(spec, pageable);
		PageResp<PublisherOffsetQueryRep> pagesRep = new PageResp<PublisherOffsetQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PublisherOffsetEntity offset : cas) {
				PublisherOffsetQueryRep queryRep = new PublisherOffsetQueryRep();
				
				queryRep.setSpvOid(offset.getPublisherBaseAccount().getPhone());
				queryRep.setSpvName(offset.getPublisherBaseAccount().getRealName());
				queryRep.setOffsetOid(offset.getOid()); // 轧差OID
				queryRep.setOffsetDate(offset.getOffsetDate()); // 轧差日期
				queryRep.setOffsetCode(offset.getOffsetCode()); // 轧差批次
				queryRep.setNetPosition(offset.getNetPosition()); // 净头寸
				if (!DateUtil.isLessThanOrEqualToday(offset.getOffsetDate())) {
					queryRep.setClearTimeArr(false);
				} else {
					queryRep.setClearTimeArr(true);
				}
//				if (DateUtil.daysBetween(DateUtil.getSqlDate(), offset.getOffsetDate()) < 0) {
//					queryRep.setClearTimeArr(false);
//				} else {
//					queryRep.setClearTimeArr(true);
//				}
				queryRep.setClearStatus(offset.getClearStatus()); // 轧差状态
				queryRep.setClearStatusDisp(clearStatusEn2Ch(offset.getClearStatus())); // 
				queryRep.setConfirmStatus(offset.getConfirmStatus());
				queryRep.setConfirmStatusDisp(confirmStatusEn2Ch(offset.getConfirmStatus()));
				queryRep.setCloseStatus(offset.getCloseStatus());
				queryRep.setCloseStatusDisp(closeStatusEn2Ch(offset.getCloseStatus()));
				queryRep.setBuyAmount(offset.getInvestAmount()); // 申购金额
				queryRep.setRedeemAmount(offset.getRedeemAmount()); // 赎回金额
				queryRep.setToCloseRedeemAmount(offset.getToCloseRedeemAmount()); // 待结算赎回订单笔数
				queryRep.setCloseMan(offset.getCloseMan()); // 结算人
				queryRep.setUpdateTime(offset.getUpdateTime());
				queryRep.setCreateTime(offset.getCreateTime());
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}

	
	
	
	private String closeStatusEn2Ch(String closeStatus) {

		if (PublisherOffsetEntity.OFFSET_closeStatus_toClose.equals(closeStatus)) {
			return "待结算";
		} else if (PublisherOffsetEntity.OFFSET_closeStatus_closing.equals(closeStatus)) {
			return "结算中";
		} else if (PublisherOffsetEntity.OFFSET_closeStatus_closed.equals(closeStatus)) {
			return "已结算";
		} else if (PublisherOffsetEntity.OFFSET_closeStatus_closeSubmitFailed.equals(closeStatus)) {
			return "结算申请失败";
		} else if (PublisherOffsetEntity.OFFSET_closeStatus_closePayFailed.equals(closeStatus)) {
			return "结算支付失败";
		}
		return closeStatus;
	}
	
	/**
	 * public static final String OFFSET_confirmStatus_toConfirm = "cleared";
	public static final String OFFSET_confirmStatus_confirming = "confirming";
	public static final String OFFSET_confirmStatus_confirmed = "confirmed";
	public static final String OFFSET_confirmStatus_confirmFailed = "confirmFailed";
	 * @param confirmStatus
	 * @return
	 */
	private String confirmStatusEn2Ch(String confirmStatus) {
		if (PublisherOffsetEntity.OFFSET_confirmStatus_toConfirm.equals(confirmStatus)) {
			return "待确认";
		} else if (PublisherOffsetEntity.OFFSET_confirmStatus_confirming.equals(confirmStatus)) {
			return "确认中";
		} else if (PublisherOffsetEntity.OFFSET_confirmStatus_confirmed.equals(confirmStatus)) {
			return "已确认";
		} else if (PublisherOffsetEntity.OFFSET_confirmStatus_confirmFailed.equals(confirmStatus)) {
			return "确认失败";
		}
		return confirmStatus;
	}
	
	private String clearStatusEn2Ch(String clearStatus) {
		if (PublisherOffsetEntity.OFFSET_clearStatus_toClear.equals(clearStatus)) {
			return "待清算";
		} else if (PublisherOffsetEntity.OFFSET_clearStatus_clearing.equals(clearStatus)) {
			return "清算中";
		} else if (PublisherOffsetEntity.OFFSET_clearStatus_cleared.equals(clearStatus)) {
			return "已清算";
		}
		return clearStatus;
	}

	public PublisherOffsetDetailRep detail(String offsetOid) {
		PublisherOffsetEntity offset = this.findByOid(offsetOid);
		PublisherOffsetDetailRep rep = new PublisherOffsetDetailRep();
		rep.setSpvOid(offset.getPublisherBaseAccount().getOid());
		rep.setOffsetDate(offset.getOffsetDate()); // 轧差日期
		rep.setOffsetCode(offset.getOffsetCode()); // 轧差批次
		rep.setNetPosition(offset.getNetPosition()); // 净头寸
		rep.setClearStatus(offset.getClearStatus()); // 轧差状态
		rep.setClearStatusDisp(clearStatusEn2Ch(offset.getClearStatus())); // 
		rep.setConfirmStatus(offset.getConfirmStatus());
		rep.setConfirmStatusDisp(confirmStatusEn2Ch(offset.getConfirmStatus()));
		rep.setCloseStatus(offset.getCloseStatus());
		rep.setCloseStatusDisp(closeStatusEn2Ch(offset.getCloseStatus()));
		rep.setBuyAmount(offset.getInvestAmount()); // 申购金额
		rep.setRedeemAmount(offset.getRedeemAmount()); // 赎回金额
		rep.setCloseMan(offset.getCloseMan()); // 结算人
		rep.setUpdateTime(offset.getUpdateTime());
		rep.setCreateTime(offset.getCreateTime());
		return rep;
	}

	public PublisherOffsetEntity findByOid(String offsetOid) {
		PublisherOffsetEntity offset = this.publisherOffsetDao.findOne(offsetOid);
		if (null == offset) {
			// error.define[30022]=轧差不存在(CODE:30022)
			throw new AMPException(30022);
		}

		return offset;
	}
	
	/**
	 * 份额确认
	 */
	public BaseResp confirm(String offsetOid) {
		
		PublisherConfirmTaskParams params = new PublisherConfirmTaskParams();
		params.setOffsetOid(offsetOid);
		
		SerialTaskReq<PublisherConfirmTaskParams> req = new SerialTaskReq<PublisherConfirmTaskParams>();
		req.setTaskCode(SerialTaskEntity.TASK_taskCode_publisherConfirm);
		req.setTaskParams(params);
		this.serialTaskService.createSerialTask(req);
		return new BaseResp();
	}

	public BaseResp confirmDo(String offsetOid, String taskOid) {
		BaseResp rep = new BaseResp();

		PublisherOffsetEntity offsetEntity = this.findByOid(offsetOid);
		
		String spvConfirmStatus = PublisherOffsetEntity.OFFSET_confirmStatus_confirming;
		String proConfirmStatus = ProductOffsetEntity.OFFSET_confirmStatus_confirming;
		this.requiresNewService.updateSpvConfirmStatus4Lock(offsetEntity.getOid(), spvConfirmStatus,
				proConfirmStatus);
		
		String lastOid = "0";
		PublisherOffsetLogEntity offsetLog = new PublisherOffsetLogEntity();
		int arithmometer = 1;
		while (true) {
			List<InvestorTradeOrderEntity> orderList = investorTradeOrderService.findByOffsetOid(offsetEntity.getOid(),
					lastOid);
			if (orderList.isEmpty()) {
				break;
			}

			for (InvestorTradeOrderEntity orderEntity : orderList) {
				lastOid = orderEntity.getOid();

				VolumeConfirmRep iRep = null;
				iRep = requiresNewService.processOneItem(orderEntity);

				if (iRep.isSuccess()) {
					offsetLog.setSuccessPeopleNum(offsetLog.getSuccessPeopleNum() + 1);
					offsetLog.setInvestAmount(offsetLog.getInvestAmount().add(iRep.getInvestAmount()));
					offsetLog.setRedeemAmount(offsetLog.getRedeemAmount().add(iRep.getRedeemAmount()));
				} else {
					offsetLog.setFailurePeopleNum(offsetLog.getFailurePeopleNum() + 1);
				}
				arithmometer++;
				if (arithmometer > 100) {
					arithmometer = 1;
					serialTaskRequireNewService.updateTime(taskOid);
				}
			}
		}
		String spvOffsetStatus = PublisherOffsetEntity.OFFSET_confirmStatus_confirmed;
		String proOffsetStatus = ProductOffsetEntity.OFFSET_confirmStatus_confirmed;

		if (offsetLog.getFailurePeopleNum() != 0) {
			spvOffsetStatus = PublisherOffsetEntity.OFFSET_confirmStatus_confirmFailed;
			proOffsetStatus = ProductOffsetEntity.OFFSET_confirmStatus_confirmFailed;
		}

		logger.info("====份额确认结果||" + offsetLog + "||====");

		requiresNewService.processStatus(offsetEntity, spvOffsetStatus, proOffsetStatus);

		return rep;
	}

	
	public BaseResp offsetMoney(OffsetMoneyReq moneyReq) {
		BaseResp rep = new BaseResp();
		System.out.println(moneyReq);
		List<Money> list = moneyReq.getOffsetMoneyList();
		this.accountingNotifyService.offsetMoney(list);
		return rep;
	}
	
	public List<PublisherOffsetEntity> getOverdueOffset(Date curDate) {
		
		return this.publisherOffsetDao.getOverdueOffset(curDate);
	}

	public void batchUpdate(List<PublisherOffsetEntity> pOffsetList) {
		this.publisherOffsetDao.save(pOffsetList);
	}
	
	public int decreaseToCloseRedeemAmount(String offsetOid) {
		return this.publisherOffsetDao.decreaseToCloseRedeemAmount(offsetOid);
	}

}
