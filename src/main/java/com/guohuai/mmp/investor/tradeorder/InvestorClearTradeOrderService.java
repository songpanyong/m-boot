package com.guohuai.mmp.investor.tradeorder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.common.SeqGenerator;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.investor.offset.InvestorOffsetService;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.sys.CodeConstants;
@Service
@Transactional
public class InvestorClearTradeOrderService {
	
	Logger logger = LoggerFactory.getLogger(InvestorClearTradeOrderService.class);
	@Autowired
	private ProductService productService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	@Autowired
	private InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	@Autowired
	private SeqGenerator seqGenerator;
	@Autowired
	private InvestorOffsetService investorOffsetService;
	@Autowired
	private PublisherOffsetService publisherOffsetService;
	@Autowired
	private ProductOffsetService productOffsetService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;
	
	
	public void clear() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_clear)) {
			clearLog();
		}
	}
	
	private void clearLog() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_clear);
		try {
			
			clearDo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_clear);
	}

	/**
	 *清盘 
	 */
	public void clearDo() {
		List<Product> pList = this.productService.findByState(Product.STATE_Clearing);
		for (Product product : pList) {
			String lastOid = "0";
			while (true) {
				List<PublisherHoldEntity> hList = this.publisherHoldService.clearingHold(product.getOid(), PublisherHoldEntity.PUBLISHER_accountType_INVESTOR, lastOid);
				if (hList.isEmpty()) {
					break;
				}
				for (PublisherHoldEntity hold : hList) {
					RedeemTradeOrderReq redeemTradeOrderReq = new RedeemTradeOrderReq();
					redeemTradeOrderReq.setOrderAmount(hold.getRedeemableHoldVolume());
					redeemTradeOrderReq.setProductOid(hold.getProduct().getOid());
					redeemTradeOrderReq.setUid(hold.getInvestorBaseAccount().getOid());
					redeemTradeOrderReq.setPlanRedeemOid(hold.getWishplanOid());
					investorInvestTradeOrderExtService.clearRedeem(redeemTradeOrderReq);
				
					lastOid = hold.getOid();
				}
			}
		}
	}
	
	

}
