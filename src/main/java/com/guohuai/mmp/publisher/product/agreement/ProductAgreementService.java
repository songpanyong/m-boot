package com.guohuai.mmp.publisher.product.agreement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.file.FileService;
import com.guohuai.file.legal.file.LegalFileDao;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordDao;
import com.guohuai.mmp.jiajiacai.ebaoquan.Html2PdfService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductAgreementService {

	@Autowired
	private FileService fileService;
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
	private ProductAgreementDao productAgreementDao;
	
	@Autowired
	private AgreeUtil agreeUtil;
	
	@Autowired
	private EbaoquanRecordDao recordDao;
	
	@Autowired
	private LegalFileDao legalDao;
	
	@Autowired
	Html2PdfService html2PdfService;
	
	public String getContractModel(Product product) {
		FileOutputStream fos = null;
		InputStream is = null;
		int length;
		byte[] buffer = new byte[2048];


		try {
			List<com.guohuai.file.File> files = this.fileService.list(product.getInvestFileKey(), com.guohuai.file.File.STATE_Valid);
			if (null == files || files.isEmpty()) {
				//error.define[30002]=产品投资协议不存在(CODE:30002)
				throw new AMPException(30002);
			}
			if (files.size() != 1) {
				//error.define[30003]=产品投资协议异常(CODE:30003)
				throw new AMPException(30003);
			}
			com.guohuai.file.File agreement = files.get(0);
			if (null == agreement.getFurl() || "".equals(agreement.getFurl())) {
				//error.define[30004]=产品投资协议地址不存在(CODE:30004)
				throw new AMPException(30004);
			}
//			URL url = new URL("http://localhost" + agreement.getFurl());
			URL url = new URL("file:///E:" + agreement.getFurl());
			is = url.openStream();
			StringBuilder model = new StringBuilder();
			
			while (-1 != (length = is.read(buffer, 0, buffer.length))) {
				model.append(new String(buffer, 0, length));
			}
			return model.toString();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
		}
		return null;
	}
	
	
	public String getServiceModel(Product product) {
		FileOutputStream fos = null;
		InputStream is = null;
		int length;
		byte[] buffer = new byte[2048];
		StringBuilder model = new StringBuilder();
		try {
			List<com.guohuai.file.File> files = this.fileService.list(product.getServiceFileKey(), com.guohuai.file.File.STATE_Valid);
			if (null == files || files.isEmpty()) {
				// error.define[30027]=产品服务协议不存在(CODE:30027)
				throw new AMPException(30027);
			}
			if (files.size() != 1) {
				// error.define[30028]=产品服务协议异常(CODE:30028)
				throw new AMPException(30028);
			}
			com.guohuai.file.File agreement = files.get(0);
			if (null == agreement.getFurl() || "".equals(agreement.getFurl())) {
				// error.define[30029]=产品服务协议地址不存在(CODE:30029)
				throw new AMPException(30029);
			}
//			URL url = new URL("http://localhost" + agreement.getFurl());
			URL url = new URL("file:///E:" + agreement.getFurl());
			is = url.openStream();
			
			
			while (-1 != (length = is.read(buffer, 0, buffer.length))) {
				model.append(new String(buffer, 0, length));
			}
			
		} catch (Exception e) {
			log.error("获取服务协议异常", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
		}
		return model.toString();
	}
	
	public void createHtml() {
		
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_createHtml)) {
			this.createHtmlLog();
		}
	}

	public void createHtmlLog() {
//		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_createHtml);
		try {
// Update by chenxian 20180206
//			createHtmlDo();
			createJJCHtmlDo();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
//			jobLog.setJobMessage(AMPException.getStacktrace(e));
//			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
//		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
//		this.jobLogService.saveEntity(jobLog);
//		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_createHtml);
	}
	
	public void createHtmlDo() {
		List<Product> productList = productService.findByProduct4Contract();
		if (productList.isEmpty()) {
			return;
		}
		for (Product product : productList) {
			try {
//				processHTML4Product(product);
				processJJCHTML4Product(product);
			} catch (Exception e) {
				log.error("productOid:{},协议生成异常", product.getOid(), e);
			}
		}

	}


	public void processHTML4Product(Product product) throws Exception {
		log.info("processHTML4Product:productOid={}, productCode={}", product.getOid(), product.getCode());
		System.out.println(product.getCode() + "==================");
		BufferedWriter bw = null;

		String serviceModel = this.getServiceModel(product);
		if (StringUtils.isEmpty(serviceModel)) {
			throw new AMPException("服务协议不存在");
		}
		String agreementModel = this.getContractModel(product);
		if (StringUtils.isEmpty(agreementModel)) {
			throw new AMPException("投资协议不存在");
		}

		try {
			String lastOid = "0";
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(agreeUtil.agreementShellPath + product.getOid() + ".first.sh")));
			while (true) {

				List<InvestorTradeOrderEntity> orderList = this.investorTradeOrderService
						.findByProductOid4Contract(product.getOid(), lastOid);
				if (orderList.isEmpty()) {
					break;
				}

				for (InvestorTradeOrderEntity order : orderList) {
					log.info("{} generate html start", order.getOrderCode());
					try {
						productAgreementRequireNewService.processOneItem(bw, serviceModel, agreementModel,
								order.getOrderCode());
					} catch (Exception e) {
						e.printStackTrace();
						log.error("{} generate html fail", order.getOrderCode(), e);
					}
					log.info("{} generate html end", order.getOrderCode());
					lastOid = order.getOid();
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(bw);
			File file = new File(agreeUtil.agreementShellPath + product.getOid() + ".first.sh");
			if (file.getTotalSpace() == 0) {
				file.delete();
			} else {
				File flagFile = new File(agreeUtil.agreementShellPath + product.getOid() + ".first.success");
				try {
					flagFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}


	public ProductAgreementEntity findByInvestorTradeOrderAndAgreementType(InvestorTradeOrderEntity tradeOrder,
			String type) {
		return this.productAgreementDao.findByInvestorTradeOrderAndAgreementType(tradeOrder, type);
	}
	
	/**
	 * process JJC HTML 4Product, added on 20180205
	 * @param product
	 * @throws Exception
	 */
	public void processJJCHTML4Product(Product product) throws Exception {
		log.info("processHTML4Product:productOid={}, productCode={}", product.getOid(), product.getCode());
		System.out.println(product.getCode() + "==================");

//		String serviceModel = this.getServiceModel(product);
//		if (StringUtils.isEmpty(serviceModel)) {
//			throw new AMPException("服务协议不存在");
//		}
		String agreementModel = this.getContractModel(product);
		if (StringUtils.isEmpty(agreementModel)) {
			throw new AMPException("投资协议不存在");
		}

		try {
			String lastOid = "0";
			while (true) {

				List<InvestorTradeOrderEntity> orderList = this.investorTradeOrderService
						.findByProductOid4Contract(product.getOid(), lastOid);
				if (orderList.isEmpty()) {
					break;
				}

				for (InvestorTradeOrderEntity order : orderList) {
					log.info("{} generate html start", order.getOrderCode());
					try {
						productAgreementRequireNewService.processOneJJCItem(agreementModel,
								order.getOrderCode());
					} catch (Exception e) {
						e.printStackTrace();
						log.error("{} generate html fail", order.getOrderCode(), e);
					}
					log.info("{} generate html end", order.getOrderCode());
					lastOid = order.getOid();
				}
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public void createJJCHtmlDo() {
//		List<Product> productList = productService.findByProduct4Contract();
//		if (productList.isEmpty()) {
//			return;
//		}
		List<String> statusList = Arrays.asList(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_TOHTML, EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_HTMLFAIL);
		List<EbaoquanRecord> recordList = recordDao.findByStatusList(statusList); 
		for (EbaoquanRecord baoquan : recordList) {
			try {
//				processHTML4Product(product);
				processJJCHTML4Baoquan(baoquan);
			} catch (Exception e) {
				log.error("productOid:{},协议生成异常", baoquan.getOid(), e);
			}
		}

	}
	
	/**
	 * process JJC HTML 4Product, added on 20180205
	 * @param product
	 * @throws Exception
	 */
	public void processJJCHTML4Baoquan(EbaoquanRecord baoquan) throws Exception {
		log.info("processHTML4Product:productOid={}, productCode={}", baoquan.getOid(), baoquan.getCodeId());


		String agreementModel = getContractBaoquanModel(baoquan);
		if (StringUtils.isEmpty(agreementModel)) {
			throw new AMPException("投资协议不存在");
		}
		
//		int baoquanType = baoquan.getDtContractType();
		
		html2PdfService.processOne(baoquan, agreementModel);
		
	}
	
	public String getContractBaoquanModel(EbaoquanRecord baoquan) {
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			String code = EbaoquanRecord.getCode(baoquan.getDtContractType());
			String fUrl = legalDao.findFileByCode(code);
			URL hh = new URL("http://vip.gh.com" + fUrl);
			URLConnection connection = hh.openConnection();
			String redirect = connection.getHeaderField("Location");
			if (redirect != null) {
				connection = new URL(redirect).openConnection();
			}
			
			log.info("HTML_URL:hh={}, redirect={}", hh, redirect);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder model = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				model.append(inputLine);
			}
			return model.toString();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
		}
		return null;
	}
	
	public String getContractBaoquanModelOld(EbaoquanRecord baoquan) {
		FileOutputStream fos = null;
		InputStream is = null;
		int length;
		byte[] buffer = new byte[2048];
		try {
			String code = EbaoquanRecord.getCode(baoquan.getDtContractType());
			String fUrl = legalDao.findFileByCode(code);
//			URL url = new URL("http://localhost" + fUrl);
			URL url = new URL("http://vip.gh.com" + fUrl);
//TODO:debug			
//			URL url = new URL("file:///E:" + fUrl);
			is = url.openStream();
			StringBuilder model = new StringBuilder();
			
			while (-1 != (length = is.read(buffer, 0, buffer.length))) {
				model.append(new String(buffer, 0, length));
			}
			return model.toString();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
		}
		return null;
	}
	
}
