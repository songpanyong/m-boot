package com.guohuai.mmp.jiajiacai.ebaoquan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.RegistFile;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.publisher.product.agreement.AgreeUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class Html2PdfService {

	public final static String COMPANY_NAME = "北京尚世同禾科技有限公司";

	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;

	@Autowired
	private AgreeUtil agreeUtil;

	@Autowired
	private ToContractPdf toPdf;

	@Autowired
	private JunziqianService junziqian;

	@Autowired
	private EbaoquanRecordService recordService;

	@Autowired
	private PlanInvestDao planInvestDao;

	@Autowired
	private InvestorBaseAccountDao baseAccountDao;

	@Autowired
	private PlanMonthDao planMonthDao;
	

	@Transactional(value = TxType.REQUIRES_NEW)
	public void processProduct(EbaoquanRecord baoquan, String agreementModel) {
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(baoquan.getRelatedKey());

		String agreeHtml = generateProcutHtmlAgreement(agreementModel, orderEntity, baoquan);
		orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_htmlOK);
		baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_HTMLOK);
		log.info("orderCode={} 生成HTML={}", baoquan.getRelatedKey(), agreeHtml);

		String agreePdf = agreeHtml.substring(0, agreeHtml.length() - 4) + "pdf";
		File oldFile = new File(agreePdf);
		if (oldFile.exists()) {
			FileUtils.deleteQuietly(oldFile);
		}
		
		baoquan.setFileDir(agreePdf);

		try {
			toPdf.htmlToPdf(agreeHtml, agreePdf);
			baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_PDFOK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_PDFFAIL);
			e.printStackTrace();
		}

		orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_pdfOK);
		this.investorTradeOrderService.saveEntity(orderEntity);
		recordService.save(baoquan);
	}

	public String generateProcutHtmlAgreement(String model, InvestorTradeOrderEntity order, EbaoquanRecord baoquan) {

		BufferedWriter bw = null;
		String abName = null;
		try {
			String abDir = agreeUtil.abDirJJC(order.getCreateTime(), order.getInvestorBaseAccount().getOid());
			FileUtils.forceMkdir(new File(abDir));
			abName = agreeUtil.abInvestNameJJC(abDir, baoquan.getCodeId(), baoquan.getDtContractType());
			File oldFile = new File(abName);
			if (oldFile.exists()) {
				FileUtils.deleteQuietly(oldFile);
			}
			model = model.replace("#number", baoquan.getCodeId()); // 订单号

			if (!StringUtil.isEmpty(order.getInvestorBaseAccount().getRealName())) {
				model = model.replace("#realName", order.getInvestorBaseAccount().getRealName()); // 真实姓名
				model = model.replace("#party_a", order.getInvestorBaseAccount().getRealName()); // 真实姓名
				model = model.replace("#party_A", order.getInvestorBaseAccount().getRealName()); // 真实姓名
			}
			model = model.replace("#userName", order.getInvestorBaseAccount().getPhoneNum()); // 平台注册手机号

			if (!StringUtil.isEmpty(order.getInvestorBaseAccount().getIdNum())) {
				model = model.replace("#idCard", order.getInvestorBaseAccount().getIdNum()); // 身份证号
			}

			/*if (!StringUtil.isEmpty(order.getPublisherBaseAccount().getRealName())) {
				model = model.replace("#company", order.getPublisherBaseAccount().getRealName()); // 发行人名字
				model = model.replace("#party_b", order.getPublisherBaseAccount().getRealName()); // 发行人名字
				model = model.replace("#party_B", order.getPublisherBaseAccount().getRealName()); // 发行人名字
			}*/
			//乙方名字固定为公司名字
			model = model.replace("#company", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_b", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_B", COMPANY_NAME); // 发行人名字
			
			model = model.replace("#date", DateUtil.format(order.getCreateTime())); // 生成时间

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(abName)));
			bw.write(model);
			bw.flush();
			
//			FileParam fileParam = new FileParam();
//			fileParam.setConstractNum( baoquan.getCodeId());
//			fileParam.setFirstParty(order.getInvestorBaseAccount().getRealName());
//			fileParam.setFirstPartyIdNum(order.getInvestorBaseAccount().getIdNum());
//			fileParam.setSecondParty(COMPANY_NAME);
//			fileParam.setTradeTime( DateUtil.format(order.getCreateTime()));
//			
//			String jsonParam = JSON.toJSONString(fileParam);
//			baoquan.setFileParam(jsonParam);
//			recordService.save(baoquan);
			
		} catch (Exception e) {
			log.error(order.getOrderCode() + ":生成HTML异常", e);
		} finally {
			IOUtils.closeQuietly(bw);

			
		}
		return abName;
	}

	public String generatePlanMonthHtmlAgreement(EbaoquanRecord baoquan, String model) {
		PlanMonthEntity pm = planMonthDao.findByOid(baoquan.getRelatedKey());
		BufferedWriter bw = null;
		String abName = null;
		try {
			String abDir = agreeUtil.abDirJJC(pm.getCreateTime(), pm.getUid());
			FileUtils.forceMkdir(new File(abDir));
			abName = agreeUtil.abInvestNameJJC(abDir, baoquan.getCodeId(), baoquan.getDtContractType());
			
			File oldFile = new File(abName);
			if (oldFile.exists()) {
				FileUtils.deleteQuietly(oldFile);
			}
			model = model.replace("#number", baoquan.getCodeId()); // 订单号
			InvestorBaseAccountEntity account = baseAccountDao.findByOid(pm.getUid());
			if (!StringUtil.isEmpty(account.getRealName())) {
				model = model.replace("#realName", account.getRealName()); // 真实姓名
				model = model.replace("#party_a", account.getRealName()); // 真实姓名
				model = model.replace("#party_A", account.getRealName()); // 真实姓名
			}
			model = model.replace("#userName", account.getPhoneNum()); // 平台注册手机号

			if (!StringUtil.isEmpty(account.getIdNum())) {
				model = model.replace("#idCard", account.getIdNum()); // 身份证号
			}

			model = model.replace("#company", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_b", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_B", COMPANY_NAME); // 发行人名字
			
			RegistFile regist = JSON.parseObject(baoquan.getFileParam(), RegistFile.class);
			if(regist != null && regist.getTradeTime() != null){
				model = model.replace("#date", regist.getTradeTime()); // 生成时间
			} else {
				model = model.replace("#date", DateUtil.currentTime()); // 生成时间
			}
			

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(abName)));
			bw.write(model);
			bw.flush();
			
			//保存模板参数

//			FileParam fileParam = new FileParam();
//			fileParam.setConstractNum( baoquan.getCodeId());
//			fileParam.setFirstParty(account.getRealName());
//			fileParam.setFirstPartyIdNum(account.getIdNum());
//			fileParam.setSecondParty(COMPANY_NAME);
//			fileParam.setTradeTime(DateUtil.format(DateUtil.getSqlCurrentDate()));
//			
//			String jsonParam = JSON.toJSONString(fileParam);
//			baoquan.setFileParam(jsonParam);
//			recordService.save(baoquan);

		} catch (Exception e) {
			log.error(baoquan.getCodeId() + ":生成HTML异常", e);
		} finally {
			IOUtils.closeQuietly(bw);
		}
		return abName;
	}

	public String generatePlanOnceHtmlAgreement(EbaoquanRecord baoquan, String model) {
		PlanInvestEntity planInvest = planInvestDao.findByOid(baoquan.getRelatedKey());
		BufferedWriter bw = null;
		String abName = null;
		try {
			String abDir = agreeUtil.abDirJJC(planInvest.getCreateTime(), planInvest.getUid());
			FileUtils.forceMkdir(new File(abDir));
			abName = agreeUtil.abInvestNameJJC(abDir, baoquan.getCodeId(), baoquan.getDtContractType());
			File oldFile = new File(abName);
			if (oldFile.exists()) {
				FileUtils.deleteQuietly(oldFile);
			}
			model = model.replace("#number", baoquan.getCodeId()); // 订单号
			InvestorBaseAccountEntity account = baseAccountDao.findByOid(planInvest.getUid());
			if (!StringUtil.isEmpty(account.getRealName())) {
				model = model.replace("#realName", account.getRealName()); // 真实姓名
				model = model.replace("#party_a", account.getRealName()); // 真实姓名
				model = model.replace("#party_A", account.getRealName()); // 真实姓名
			}
			model = model.replace("#userName", account.getPhoneNum()); // 平台注册手机号

			if (!StringUtil.isEmpty(account.getIdNum())) {
				model = model.replace("#idCard", account.getIdNum()); // 身份证号
			}

			model = model.replace("#company", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_b", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_B", COMPANY_NAME); // 发行人名字

			model = model.replace("#date", DateUtil.format(planInvest.getCreateTime())); // 生成时间

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(abName)));
			bw.write(model);
			bw.flush();
			
			//保存模板参数

//			FileParam fileParam = new FileParam();
//			fileParam.setConstractNum( baoquan.getCodeId());
//			fileParam.setFirstParty(account.getRealName());
//			fileParam.setFirstPartyIdNum(account.getIdNum());
//			fileParam.setSecondParty(COMPANY_NAME);
//			fileParam.setTradeTime(DateUtil.format(planInvest.getCreateTime()));
//			
//			String jsonParam = JSON.toJSONString(fileParam);
//			baoquan.setFileParam(jsonParam);
//			recordService.save(baoquan);

		} catch (Exception e) {
			log.error(baoquan.getCodeId() + ":生成HTML异常", e);
		} finally {
			IOUtils.closeQuietly(bw);		
		}	
		return abName;
	}

	public String generateRegistHtmlAgreement(EbaoquanRecord baoquan, String model) {
		InvestorBaseAccountEntity account = baseAccountDao.findByOid(baoquan.getRelatedKey());
		
//		RegistFile registFile = new RegistFile();
//		registFile.setConstractNum(baoquan.getCodeId());
//		registFile.setFirstParty(account.getPhoneNum());
//		registFile.setSecondParty(COMPANY_NAME);
//		registFile.setTradeTime(DateUtil.format(DateUtil.getSqlCurrentDate()));
//		String jsonParam = JSON.toJSONString(registFile);
//		baoquan.setFileParam(jsonParam);
//		recordService.save(baoquan);

		
		BufferedWriter bw = null;
		String abName = null;
		try {
			String abDir = agreeUtil.abDirJJC(account.getCreateTime(), account.getOid());
			FileUtils.forceMkdir(new File(abDir));
			abName = agreeUtil.abInvestNameJJC(abDir, baoquan.getCodeId(), baoquan.getDtContractType());
			File oldFile = new File(abName);
			if (oldFile.exists()) {
				FileUtils.deleteQuietly(oldFile);
			}
			model = model.replace("#number", baoquan.getCodeId()); // 订单号
			
			if (!StringUtil.isEmpty(account.getPhoneNum())) {
				model = model.replace("#realName", account.getPhoneNum()); // 手机号码
				model = model.replace("#party_a", account.getPhoneNum()); // 手机号码
				model = model.replace("#party_A", account.getPhoneNum()); // 手机号码
			}
			/*
			if (!StringUtil.isEmpty(account.getRealName())) {
				model = model.replace("#realName", account.getRealName()); // 真实姓名
				model = model.replace("#party_a", account.getRealName()); // 真实姓名
				model = model.replace("#party_A", account.getRealName()); // 真实姓名
			}
			*/
			model = model.replace("#userName", account.getPhoneNum()); // 平台注册手机号

			if (!StringUtil.isEmpty(account.getIdNum())) {
				model = model.replace("#idCard", account.getIdNum()); // 身份证号
			}

			model = model.replace("#company", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_b", COMPANY_NAME); // 发行人名字
			model = model.replace("#party_B", COMPANY_NAME); // 发行人名字

			model = model.replace("#date", DateUtil.format(account.getCreateTime())); // 生成时间
			
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(abName)));
			bw.write(model);
			bw.flush();
		} catch (Exception e) {
			log.error(baoquan.getCodeId() + ":生成HTML异常", e);
		} finally {
			IOUtils.closeQuietly(bw);
			
		}
		
		return abName;
	}

	public void processOne(EbaoquanRecord baoquan, String agreementModel) {
		int baoquanType = baoquan.getDtContractType();
		String agreeHtml = null;
		switch (baoquanType) {
			/** 家加财平台注册服务协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_REGIST:
			agreeHtml = generateRegistHtmlAgreement(baoquan, agreementModel);
			html2pdf(baoquan, agreeHtml);
			break;
			/** 家加财活期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN:
			/** 家加财定期期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_CLOSE:
			/** 家加财定向委托投资协议活期 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_OPEN:
			/** 家加财定向委托投资协议定期 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_CLOSE:
			processProduct(baoquan, agreementModel);
			break;
			/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE:
			/** 家加财定向委托投资协议心愿一次性 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_ONCE:
			agreeHtml = generatePlanOnceHtmlAgreement(baoquan, agreementModel);
			html2pdf(baoquan, agreeHtml);
			break;
			/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH:
			/** 家加财定向委托投资协议月定投 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_MONTH:
			/** 代扣服务协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WITHHOLD:
			agreeHtml = generatePlanMonthHtmlAgreement(baoquan, agreementModel);
			html2pdf(baoquan, agreeHtml);
			break;

		default:
			break;
		}

	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	private void html2pdf(EbaoquanRecord baoquan, String agreeHtml) {
		baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_HTMLOK);
		log.info("orderCode={} 生成HTML={}", baoquan.getRelatedKey(), agreeHtml);

		String agreePdf = agreeHtml.substring(0, agreeHtml.length() - 4) + "pdf";
		baoquan.setFileDir(agreePdf);
		
		File oldFile = new File(agreePdf);
		if (oldFile.exists()) {
			FileUtils.deleteQuietly(oldFile);
		}

		try {
			toPdf.htmlToPdf(agreeHtml, agreePdf);
			baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_PDFOK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_PDFFAIL);
			e.printStackTrace();
		}
		recordService.save(baoquan);

	}
	
	/**
	 *****************************************************************************
	 upload
	 *****************************************************************************
	 */
	
	public void uploadOne(EbaoquanRecord baoquan) {
		int baoquanType = baoquan.getDtContractType();
		HashMap<String, String> map = null;
		switch (baoquanType) {
			/** 家加财平台注册服务协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_REGIST:
			map = uploadRegistPdf(baoquan);
			break;
			/** 家加财活期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN:
			/** 家加财定期期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_CLOSE:
			/** 家加财定向委托投资协议活期 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_OPEN:
			/** 家加财定向委托投资协议定期 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_CLOSE:
			map = uploaProcutPdf(baoquan);
			break;
			/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE:
			/** 家加财定向委托投资协议心愿一次性 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_ONCE:
			map = uploadPlanOncePdf(baoquan);
			break;
			/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH:
			/** 家加财定向委托投资协议月定投 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_MONTH:
			/** 代扣服务协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WITHHOLD:
			map = uploadPlanMonthPdf(baoquan);
			break;

		default:
			break;
		}
		
		junziqian.createContractFilePreservation(baoquan, map);

	}
	
	private HashMap<String, String> uploaProcutPdf(EbaoquanRecord baoquan) {
		HashMap<String, String> map = new HashMap<String, String>();
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(baoquan.getRelatedKey());
		map.put("contractNo", baoquan.getCodeId());
		map.put("amount", orderEntity.getOrderVolume().toString());
		
		
		map.put("buyerName", orderEntity.getInvestorBaseAccount().getRealName()); // 姓名

		map.put("buyerIdno", orderEntity.getInvestorBaseAccount().getIdNum()); // 证件号码
		map.put("buyerPhone", orderEntity.getInvestorBaseAccount().getPhoneNum());//手机号码

		return map;
	}

	private HashMap<String, String> uploadPlanMonthPdf(EbaoquanRecord baoquan) {
		PlanMonthEntity pm = planMonthDao.findByOid(baoquan.getRelatedKey());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("contractNo", baoquan.getCodeId());
		map.put("amount", "0.0");
		
		InvestorBaseAccountEntity account = baseAccountDao.findByOid(pm.getUid());
		map.put("buyerName", account.getRealName()); // 姓名

		map.put("buyerIdno", account.getIdNum()); // 证件号码
		map.put("buyerPhone", account.getPhoneNum());//手机号码
		return map;
	}

	private HashMap<String, String> uploadPlanOncePdf(EbaoquanRecord baoquan) {
		PlanInvestEntity planInvest = planInvestDao.findByOid(baoquan.getRelatedKey());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("contractNo", baoquan.getCodeId());
		map.put("amount", "0.0");
		
		InvestorBaseAccountEntity account = baseAccountDao.findByOid(planInvest.getUid());
		map.put("buyerName", account.getRealName()); // 姓名

		map.put("buyerIdno", account.getIdNum()); // 证件号码
		map.put("buyerPhone", account.getPhoneNum());//手机号码
		return map;
	}

	private HashMap<String, String> uploadRegistPdf(EbaoquanRecord baoquan) {
		InvestorBaseAccountEntity account = baseAccountDao.findByOid(baoquan.getRelatedKey());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("contractNo", baoquan.getCodeId());
		map.put("amount", "0.0");
		map.put("buyerName", account.getRealName()); // 姓名

		map.put("buyerIdno", account.getIdNum()); // 证件号码
		map.put("buyerPhone", account.getPhoneNum());//手机号码
		return map;
	}

}
