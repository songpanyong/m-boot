package com.guohuai.mmp.platform.publisher.dividend.offset;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.CloseRequest;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.sys.CodeConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class DividendOffsetService {

	@Autowired
	private DividendOffsetDao dividendOffsetDao;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private Accment accmentService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Value("${seq.env}")
	String seqEnv;

	public DividendOffsetEntity findByDividendDateAndProductOid(String productOid, Date incomeDate) {
		return this.dividendOffsetDao.findByDividendDateAndProductOid(productOid, incomeDate);
	}

	public boolean isDividend(String productOid, Date incomeDate) {
		DividendOffsetEntity entity = this.findByDividendDateAndProductOid(productOid, incomeDate);
		if (null == entity) {
			return false;
		}
		return true;
	}

	public DividendOffsetEntity findByDividendDate(Date incomeDate) {
		return this.dividendOffsetDao.findByDividendDate(incomeDate);
	}

	/**
	 * 创建现金分红轧差批次
	 */
	public int createDividendOffset(String dividendOffsetOid, String productOid, Date incomeDate, BigDecimal dividendAmount,
			int toCloseDividendNumber) {
		
		return this.dividendOffsetDao.createDividendOffset(dividendOffsetOid, productOid, incomeDate, dividendAmount,
				toCloseDividendNumber);
		
	}

	/**
	 * 创建现金分红订单
	 */
	public int createDividendOrders() {
		return this.dividendOffsetDao.createDividendOrders();
	}
	
	

	public int deleteOrdersMd() {
		return this.dividendOffsetDao.deleteOrdersMd();
	}
	
	public int insertIntoOrdersMd(String productOid, String publisherOid, String dividendOffsetOid, Date incomeDate) {
		return this.dividendOffsetDao.insertIntoOrdersMd(productOid, publisherOid, dividendOffsetOid, incomeDate, seqEnv);
	}

	public void dividend() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_dividendOffsetAutoClose.getJobId())) {
			this.dividendLog();
		}
	}

	public void dividendLog() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_dividendOffsetAutoClose.getJobId());
		try {
			dividendDo();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_dividendOffsetAutoClose.getJobId());
	}

	public void dividendDo() {
		List<DividendOffsetEntity> offsets = this.dividendOffsetDao
				.findByDividendCloseStatus(DividendOffsetEntity.OFFSET_closeStatus_toClose);
		for (DividendOffsetEntity offset : offsets) {
			close(offset);
		}
	}
	
	public BaseResp close(String dividendOffsetOid) {
		return this.close(this.findByOid(dividendOffsetOid));
		
	}

	private DividendOffsetEntity findByOid(String dividendOffsetOid) {
		DividendOffsetEntity entity = this.dividendOffsetDao.findOne(dividendOffsetOid);
		if (null == entity) {
			throw new AMPException("现金分红轧差不存在");
		}
		return entity;
	}

	private BaseResp close(DividendOffsetEntity offset) {
		BaseResp baseResp = new BaseResp();
		if (offset.getDividendAmount().compareTo(BigDecimal.ZERO) == 0) {
			this.dividendOffsetDao.updateCloseStatus4Close(offset.getOid(), DividendOffsetEntity.OFFSET_closeStatus_closed, null);
		} else {
			String closeStatus4TradeOrder = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closing;
			String closeStatus4Offset = ProductOffsetEntity.OFFSET_closeStatus_closing;

			CloseRequest ireq = new CloseRequest();
			ireq.setPublisherUserOid(offset.getProduct().getPublisherBaseAccount().getMemberId());
			ireq.setNettingBalance(offset.getDividendAmount().negate());
			ireq.setRequestNo(StringUtil.uuid());
			ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
			ireq.setOrderCode(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_spvABPay));
			ireq.setOrderType(AccParam.OrderType.ABPPAY.toString());
			ireq.setOrderTime(DateUtil.format(DateUtil.getSqlCurrentDate(), DateUtil.fullDatePattern));
			ireq.setUserType(AccParam.UserType.SPV.toString());
			ireq.setRemark("remark");
			ireq.setOrderDesc("orderDesc");
			ireq.setInvestAmount(BigDecimal.ZERO);
			ireq.setRedeemAmount(offset.getDividendAmount());
			baseResp = this.accmentService.close(ireq);
			if (0 != baseResp.getErrorCode()) {
				closeStatus4TradeOrder = InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeSubmitFailed;
				closeStatus4Offset = ProductOffsetEntity.OFFSET_closeStatus_closeSubmitFailed;

			} else {
				this.publisherBaseAccountService.updateBalance(offset.getProduct().getPublisherBaseAccount());
			}
			updateCloseStatus(offset, closeStatus4TradeOrder, closeStatus4Offset, baseResp.getErrorMessage());
		}

		return baseResp;

	}

	private void updateCloseStatus(DividendOffsetEntity offset, String closeStatus4TradeOrder,
			String dividendCloseStatus, String msg) {
		this.dividendOffsetDao.updateCloseStatus4Close(offset.getOid(), dividendCloseStatus, msg);

		this.investorTradeOrderService.updateCloseStatus4Dividend(offset, closeStatus4TradeOrder);
	}

	public PageResp<DividendOffsetQueryRep> mng(Specification<DividendOffsetEntity> spec, Pageable pageable) {
		Page<DividendOffsetEntity> cas = this.dividendOffsetDao.findAll(spec, pageable);
		PageResp<DividendOffsetQueryRep> pagesRep = new PageResp<DividendOffsetQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (DividendOffsetEntity offset : cas) {
				DividendOffsetQueryRep queryRep = new DividendOffsetQueryRep();
				queryRep.setDividendOffsetOid(offset.getOid());
				queryRep.setProductName(offset.getProduct().getName());
				queryRep.setDividendDate(offset.getDividendDate());
				queryRep.setDividendAmount(offset.getDividendAmount());
				queryRep.setToCloseDividendNumber(offset.getToCloseDividendNumber());
				queryRep.setMessage(offset.getMessage());
				queryRep.setDividendCloseStatus(offset.getDividendCloseStatus());
				queryRep.setDividendCloseStatusDisp(dividendCloseStatusEn2Ch(offset.getDividendCloseStatus()));
				
				queryRep.setUpdateTime(offset.getUpdateTime());
				queryRep.setCreateTime(offset.getCreateTime());
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}

	private String dividendCloseStatusEn2Ch(String dividendCloseStatus) {
		if (DividendOffsetEntity.OFFSET_closeStatus_toClose.equals(dividendCloseStatus)) {
			return "待结算";
		} else if (DividendOffsetEntity.OFFSET_closeStatus_closing.equals(dividendCloseStatus)) {
			return "结算中";
		} else if (DividendOffsetEntity.OFFSET_closeStatus_closed.equals(dividendCloseStatus)) {
			return "已结算";
		} else if (DividendOffsetEntity.OFFSET_closeStatus_closeSubmitFailed.equals(dividendCloseStatus)) {
			return "结算申请失败";
		} else if (DividendOffsetEntity.OFFSET_closeStatus_closePayFailed.equals(dividendCloseStatus)) {
			return "结算支付失败";
		}
		return dividendCloseStatus;
	}
	
	public int decreaseToCloseRedeemAmount(String dividendOffsetOid) {
		return this.dividendOffsetDao.decreaseToCloseDividendNumber(dividendOffsetOid);
	}


}
