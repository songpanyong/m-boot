package com.guohuai.mmp.publisher.product.agreement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.StringUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.jiajiacai.ebaoquan.JunziqianService;
import com.guohuai.mmp.jiajiacai.ebaoquan.ToContractPdf;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductAgreementRequireNewService {

	@Autowired
	private ProductAgreementDao productAgreementDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	
	@Autowired
	private AgreeUtil agreeUtil;
	
	@Autowired
	private ToContractPdf toPdf;
	
	@Autowired
	private JunziqianService junziqian;
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void processOneItem(BufferedWriter bw, String serviceModel, String agreementModel, String orderCode)
			throws IOException {
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(orderCode);
		ProductAgreementEntity investAgreement = this.createInvestEntity(orderEntity);
		String agreeFullFile = generateHtmlAgreement(agreementModel, orderEntity);
		ProductAgreementEntity serviceAgreement = this.createServiceEntity(orderEntity);
		String serviceFullFile = generateServiceAgreement(serviceModel, orderEntity);

		String agreePdfFullPath = agreeFullFile.replace(".html", ".pdf");
		bw.append("/usr/bin/wkhtmltopdf ").append(agreeFullFile).append("  ").append(agreePdfFullPath)
				.append(System.getProperty("line.separator"));
		productAgreementDao.save(investAgreement);

		String servicePdfFullPath = serviceFullFile.replace(".html", ".pdf");
		bw.append("/usr/bin/wkhtmltopdf ").append(serviceFullFile).append("  ").append(servicePdfFullPath)
				.append(System.getProperty("line.separator"));
		productAgreementDao.save(serviceAgreement);

		orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_htmlOK);
		this.investorTradeOrderService.saveEntity(orderEntity);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void processOneJJCItem(String agreementModel, String orderCode)
			throws IOException {
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(orderCode);
		ProductAgreementEntity investAgreement = this.createInvestEntity(orderEntity);
		String agreeHtml = generateProcutHtmlAgreement(agreementModel, orderEntity);
		orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_htmlOK);
		log.info("orderCode={} 生成HTML={}", orderCode, agreeHtml);
//		ProductAgreementEntity serviceAgreement = this.createServiceEntity(orderEntity);
//		String serviceFullFile = generateServiceAgreement(serviceModel, orderEntity);

//		String agreePdfFullPath = agreeFullFile.replace(".html", ".pdf");
//		bw.append("/usr/bin/wkhtmltopdf ").append(agreeFullFile).append("  ").append(agreePdfFullPath)
//				.append(System.getProperty("line.separator"));
		String agreePdf = agreeUtil.abInvestPDFName(agreeUtil.abDir(orderEntity), orderEntity);
		
		toPdf.htmlToPdf(agreeHtml, agreePdf);
		productAgreementDao.save(investAgreement);

//		String servicePdfFullPath = serviceFullFile.replace(".html", ".pdf");
//		bw.append("/usr/bin/wkhtmltopdf ").append(serviceFullFile).append("  ").append(servicePdfFullPath)
//				.append(System.getProperty("line.separator"));
//		productAgreementDao.save(serviceAgreement);

		orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_pdfOK);
		this.investorTradeOrderService.saveEntity(orderEntity);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void processOneItem(String orderCode) {
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(orderCode);
		String service = agreeUtil.abServicePDFName(agreeUtil.abDir(orderEntity), orderEntity);
		String invest = agreeUtil.abInvestPDFName(agreeUtil.abDir(orderEntity), orderEntity);
		if (new File(service).exists() && new File(invest).exists()) {
			ProductAgreementEntity serviceEn = this.productAgreementDao.findByInvestorTradeOrderAndAgreementType(orderEntity, ProductAgreementEntity.Agreement_agreementType_service);
			ProductAgreementEntity investEn = this.productAgreementDao.findByInvestorTradeOrderAndAgreementType(orderEntity, ProductAgreementEntity.Agreement_agreementType_investing);
			serviceEn.setAgreementUrl(service);
			investEn.setAgreementUrl(invest);
			
			orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_pdfOK);
			this.investorTradeOrderService.saveEntity(orderEntity);
		}
	}

	public ProductAgreementEntity createInvestEntity(InvestorTradeOrderEntity order) {
		ProductAgreementEntity entity = new ProductAgreementEntity();
		entity.setProduct(order.getProduct());
		entity.setInvestorTradeOrder(order);
		entity.setAgreementCode(order.getOrderCode());
		entity.setAgreementName("invest protocol");
		entity.setAgreementType(ProductAgreementEntity.Agreement_agreementType_investing);
		return entity;
	}

	public ProductAgreementEntity createServiceEntity(InvestorTradeOrderEntity order) {
		ProductAgreementEntity entity = new ProductAgreementEntity();
		entity.setProduct(order.getProduct());
		entity.setInvestorTradeOrder(order);
		entity.setAgreementCode(order.getOrderCode());
		entity.setAgreementName("invest protocol");
		entity.setAgreementType(ProductAgreementEntity.Agreement_agreementType_service);
		return entity;
	}

	public String generateServiceAgreement(String model, InvestorTradeOrderEntity order) {

		BufferedWriter bw = null;
		String agreementFullPath = null;
		try {
			String abDir = agreeUtil.abDir(order);
			FileUtils.forceMkdir(new File(abDir));
			agreementFullPath = agreeUtil.abServiceName(abDir, order);

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(agreementFullPath)));
			bw.write(model);
			bw.flush();
		} catch (Exception e) {

		} finally {
			IOUtils.closeQuietly(bw);
		}
		return agreementFullPath;
	}

	public String generateHtmlAgreement(String model, InvestorTradeOrderEntity order) {

		BufferedWriter bw = null;
		String abName = null;
		try {
			String abDir = agreeUtil.abDir(order);
			FileUtils.forceMkdir(new File(abDir));
			abName = agreeUtil.abInvestName(abDir, order);
			
			if (!StringUtil.isEmpty(order.getInvestorBaseAccount().getRealName())) {
				model = model.replace("#username", order.getInvestorBaseAccount().getRealName()); // 真实姓名
			}
			model = model.replace("#userAcc", order.getInvestorBaseAccount().getPhoneNum()); // 平台注册手机号
			
			if (!StringUtil.isEmpty(order.getInvestorBaseAccount().getIdNum())) {
				model = model.replace("#idNumb", order.getInvestorBaseAccount().getIdNum()); // 身份证号
			}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(abName)));
			bw.write(model);
			bw.flush();
		} catch (Exception e) {
			log.error(order.getOrderCode() + ":生成HTML异常", e);
		} finally {
			IOUtils.closeQuietly(bw);
		}
		return abName;
	}
	
	public String generateProcutHtmlAgreement(String model, InvestorTradeOrderEntity order) {

		BufferedWriter bw = null;
		String abName = null;
		try {
			String abDir = agreeUtil.abDir(order);
			FileUtils.forceMkdir(new File(abDir));
			abName = agreeUtil.abInvestName(abDir, order);
			
			model = model.replace("&number",  order.getOrderCode()); // 订单号
			
			if (!StringUtil.isEmpty(order.getInvestorBaseAccount().getRealName())) {
				model = model.replace("&realName", order.getInvestorBaseAccount().getRealName()); // 真实姓名
				model = model.replace("&party_A", order.getInvestorBaseAccount().getRealName()); // 真实姓名
			}
			model = model.replace("&userName", order.getInvestorBaseAccount().getPhoneNum()); // 平台注册手机号
			
			if (!StringUtil.isEmpty(order.getInvestorBaseAccount().getIdNum())) {
				model = model.replace("&idCard", order.getInvestorBaseAccount().getIdNum()); // 身份证号
			}
			
			if (!StringUtil.isEmpty(order.getPublisherBaseAccount().getRealName())) {
				model = model.replace("&company", order.getPublisherBaseAccount().getRealName()); // 发行人名字
				model = model.replace("&party_B", order.getPublisherBaseAccount().getRealName()); // 发行人名字
			}
			model = model.replace("&date",  DateUtil.format(order.getCreateTime())); // 生成时间
			
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(abName)));
			bw.write(model);
			bw.flush();
		} catch (Exception e) {
			log.error(order.getOrderCode() + ":生成HTML异常", e);
		} finally {
			IOUtils.closeQuietly(bw);
		}
		return abName;
	}

	/**
	 * Added for jjc.
	 * @param orderCode
	 */
	/*
	@Transactional(value = TxType.REQUIRES_NEW)
	public void processJJCOneItem(String orderCode) {
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(orderCode);
//		String service = agreeUtil.abServicePDFName(agreeUtil.abDir(orderEntity), orderEntity);
		String invest = agreeUtil.abInvestPDFName(agreeUtil.abDir(orderEntity), orderEntity);
		if (new File(invest).exists()) {
			
			uploadPdf(invest, orderEntity);
//			ProductAgreementEntity serviceEn = this.productAgreementDao.findByInvestorTradeOrderAndAgreementType(orderEntity, ProductAgreementEntity.Agreement_agreementType_service);
			ProductAgreementEntity investEn = this.productAgreementDao.findByInvestorTradeOrderAndAgreementType(orderEntity, ProductAgreementEntity.Agreement_agreementType_investing);
//			serviceEn.setAgreementUrl(service);
			investEn.setAgreementUrl(invest);
			orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_upPdfOK);
			this.investorTradeOrderService.saveEntity(orderEntity);
		}
	}
	
	private void uploadPdf(String pdfFilePath, InvestorTradeOrderEntity orderEntity) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("contractNo", "02011816");
		map.put("amount", "1000");
		map.put("buyerName", "陈贤"); // 姓名
		
		map.put("buyerIdno", "102528199712121717"); // 证件号码
		map.put("buyerPhone", "13264391052");
		
		map.put("dtId", "02011816");
		map.put("dtContractId", "02011816");
        map.put("dtContractType", "135246");
		
		junziqian.createContractFilePreservation(pdfFilePath, map);
	}
	*/
}
