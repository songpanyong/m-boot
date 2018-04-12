package com.guohuai.mmp.publisher.product.agreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordDao;
import com.guohuai.mmp.jiajiacai.ebaoquan.Html2PdfService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductAgreementPDFService {
	@Autowired
	private ProductAgreementDao productAgreementDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService; 
	@Autowired
	private ProductService productService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private ProductAgreementRequireNewService productAgreementRequireNewService;
	@Autowired
	private EbaoquanRecordDao recordDao;
	@Autowired
	private Html2PdfService html2PdfService;
	public void uploadPDFDo() {
		List<Product> productList = productService.findByProduct4Contract();
		if (productList.isEmpty()) {
			return;
		}
		for (Product product : productList) {
			try {
				processPDF4Product(product);
			} catch (Exception e) {
				log.error("productOid:{},协议生成异常", product.getOid(), e);
			}
		}
	}
	
	private void processPDF4Product(Product product) {
		String lastOid = "0";
		
		while (true) {

			List<InvestorTradeOrderEntity> orderList = this.investorTradeOrderService
					.findByProductOid4PDF(product.getOid(), lastOid);
			if (orderList.isEmpty()) {
				break;
			}
			List<ProductAgreementEntity> agreeList = new ArrayList<ProductAgreementEntity>();
			for (InvestorTradeOrderEntity order : orderList) {
				productAgreementRequireNewService.processOneItem(order.getOrderCode());
				lastOid = order.getOid();
			}
			this.investorTradeOrderService.batchUpdate(orderList);
			productAgreementDao.save(agreeList);
		}
	}




	public void uploadPDF() {
		
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_uploadPDF)) {
			this.uploadPDFLog();
		}
	}

	public void uploadPDFLog() {
//		JobLogEntity jobLog =  JobLogFactory.getInstance(JobLockEntity.JOB_jobId_uploadPDF);
		
		try {
//			uploadPDFDo();
/**			
 * Modified by chenxian 20180206
 */
			uploadPDFDoJJC();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
//			jobLog.setJobMessage(AMPException.getStacktrace(e));
//			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
//		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
//		this.jobLogService.saveEntity(jobLog);
//		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_uploadPDF);
		
		
	}
	
	public void uploadPDFDoJJC() {
//		List<Product> productList = productService.findByProduct4Contract();
//		if (productList.isEmpty()) {
//			return;
//		}
//		List<String> statusList = Arrays.asList(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_PDFOK, EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_UPPDFFAIL);
		List<String> statusList = Arrays.asList(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_PDFOK);
		List<EbaoquanRecord> recordList = recordDao.findByStatusList(statusList); 
		for (EbaoquanRecord baoquan : recordList) {
			try {
				html2PdfService.uploadOne(baoquan);
			} catch (Exception e) {
				log.error("productOid:{},协议生成异常", baoquan.getOid(), e);
			}
		}

	}
	
}
