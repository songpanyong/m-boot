package com.guohuai.mmp.jiajiacai.ebaoquan;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.guohuai.component.util.DateUtil;
import com.junziqian.api.bean.Signatory;
import com.junziqian.api.common.DealType;
import com.junziqian.api.common.IdentityType;
import com.junziqian.api.request.ApplySignFileRequest;
import com.junziqian.api.request.DetailAnonyLinkRequest;
import com.junziqian.api.response.ApplySignResponse;
import com.junziqian.api.response.SignLinkResponse;
import com.junziqian.api.util.LogUtils;

import lombok.extern.slf4j.Slf4j;
//import com.lqb.dt.contract.model.EbaoquanRecord;
//
import rop.security.MainError;

/**
 * @author chenxian updated on 2018/02/01.
 */
@Slf4j
@Service
public class JunziqianService extends JunziqianClientInit {

//	static Logger logger = LoggerFactory.getLogger(JunziqianService.class);
	public final static String COMPANY_NAME = "北京尚世同禾科技有限公司";
	final static String COMPANY_IDNO = "911101085976789568";
	final static String COMPANY_EMAIL = "support@51lqb.com";

	@Autowired
	private EbaoquanRecordService recordService;

	@Transactional
	public boolean createContractFilePreservation(EbaoquanRecord baoquan, HashMap<String, String> map) {
		// boolean rst = false;
		log.debug("come into JunziqianPreservation");
		ApplySignFileRequest.Builder builder = new ApplySignFileRequest.Builder();
		try {
			builder.withFile(baoquan.getFileDir());
			builder.withContractName(map.get("contractNo"));
			builder.withContractAmount(new Double(map.get("amount")));
			// 签约人、签章位置、签章样式可对每个签约对象分别设置
			HashSet<Signatory> signatories = Sets.newHashSet();

			// 测试时请改为自己的个人信息进行测试（姓名、身份证号、手机号不能部分或全部隐藏）
			Signatory signatory = new Signatory();
			signatory.setFullName(map.get("buyerName")); // 姓名
			signatory.setSignatoryIdentityType(IdentityType.IDCARD); // 证件类型
			signatory.setIdentityCard(map.get("buyerIdno")); // 证件号码
			signatory.setMobile(map.get("buyerPhone"));
			signatories.add(signatory);

			signatory = new Signatory();
			signatory.setFullName(COMPANY_NAME); // 姓名
			signatory.setSignatoryIdentityType(IdentityType.BIZLIC); // 证件类型
			signatory.setIdentityCard(COMPANY_IDNO);// 证件号码（营业执照号或统一社会信用代码）
			signatory.setEmail(COMPANY_EMAIL);// 企业账户注册邮箱
			signatories.add(signatory);

			builder.withSignatories(signatories); // 添加签约人
			builder.withDealType(DealType.ONLY_PRES);// 保全

		} catch (Exception e) {
			log.debug("ebaoquan api builder error: " + e.getMessage());
		}

		log.debug("before request baoquan SERVICE_URL:{}", SERVICE_URL);
		
		ApplySignResponse response = null;
		try {
			response = getClient().applySignFile(builder.build());
		} catch (Exception e) {
			log.debug("ebaoquan api error: " + e.getMessage());
		}

		log.debug("after request baoquan");
		log.info("respose" + response);

//		EbaoquanRecord ebaoquanRecord = new EbaoquanRecord();
//		ebaoquanRecord.setDtId(Integer.parseInt(map.get("dtId")));
//		ebaoquanRecord.setDtContractId(Integer.parseInt(map.get("dtContractId")));
//		ebaoquanRecord.setDtContractType(Integer.parseInt(map.get("dtContractType")));
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//		ebaoquanRecord.setPreservationTime(df.format(new Date()));
//		ebaoquanRecord.setDownloadUrl("");

		if (null != response) {
			if (response.isSuccess()) {
				log.info("创建保全成功!");
				log.info("保全ID:" + response.getApplyNo());

				baoquan.setPreservationId(response.getApplyNo());
				baoquan.setDocHash("0000");
				baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_UPPDFOK);
//				ebaoquanRecord = recordDao.save(ebaoquanRecord);
				// List<EbaoquanRecord> ebaoquanList =
				// recordDao.findByDtContractIdAndDtContractType(Integer.parseInt(map.get("dtContractId")),
				// Integer.parseInt(map.get("dtContractType")));
				// if (ebaoquanList.isEmpty()) {
				// rst = recordDao.save(ebaoquanRecord);
				// } else {
				// rst = ebaoquanRecord.update();
				// }
			} else {
				MainError error = response.getError();
				String errorStr = "";
				if (error != null) {
					errorStr = "保全失败：" + error.getCode() + "|" + error.getMessage() + "|" + "Main Error Solution:"
							+ error.getSolution();
					log.error(errorStr);
				} else {
					errorStr = "保全失败：Error is null";
					log.error(errorStr);
				}

				baoquan.setPreservationId("4444");
				baoquan.setDocHash("4444");
				baoquan.setDownloadUrl(errorStr);
				baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_UPPDFFAIL);
				// ebaoquanRecord = recordDao.save(ebaoquanRecord);
				// List<EbaoquanRecord> ebaoquanList =
				// ebaoquanRecord.getByDtContractIdAndType(Integer.parseInt(map.get("dtContractId")),
				// Integer.parseInt(map.get("dtContractType")));
				// if (ebaoquanList.isEmpty()) {
				// rst = ebaoquanRecord.save();
				// } else {
				// rst = ebaoquanRecord.update();
				// }

			}
		} else {
			baoquan.setPreservationId("5555");
			baoquan.setDocHash("5555");
			baoquan.setDownloadUrl("调用易保全失败，返回response为空，请查看连接情况！");
			baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_UPPDFFAIL);
//			rst = true;
			// List<EbaoquanRecord> ebaoquanList =
			// ebaoquanRecord.getByDtContractIdAndType(Integer.parseInt(map.get("dtContractId")),
			// Integer.parseInt(map.get("dtContractType")));
			// if (ebaoquanList.isEmpty()) {
			// rst = ebaoquanRecord.save();
			// } else {
			// rst = ebaoquanRecord.update();
			// }
			// ebaoquanRecord = recordDao.save(ebaoquanRecord);
			log.error("调用易保全失败，返回response为空，请查看连接情况！");
		}

		baoquan = recordService.save(baoquan);
		return (baoquan != null) ? true : false;
	}

	public void requestByNo() {
		DetailAnonyLinkRequest request = new DetailAnonyLinkRequest();
		request.setApplyNo("APL958994240522817536");
		SignLinkResponse response = getClient().detailAnonyLink(request);
		LogUtils.logResponse(response);
	}
	
	@Transactional
	public String requestByBaoquan(EbaoquanRecord baoquan) {
		int lastHour = -23;
		if (baoquan.getLastAccessTime() != null) {
			lastHour = DateUtil.getTimeRemainHours(baoquan.getLastAccessTime());
		}
		if (lastHour > -23) {
			return baoquan.getDownloadUrl();
		}
		DetailAnonyLinkRequest request = new DetailAnonyLinkRequest();
		request.setApplyNo(baoquan.getPreservationId());
		SignLinkResponse response = getClient().detailAnonyLink(request);
		LogUtils.logResponse(response);
		baoquan.setDownloadUrl(response.getLink());
		baoquan.setLastAccessTime(DateUtil.getSqlCurrentDate());
		recordService.save(baoquan);
		return baoquan.getDownloadUrl();
		
	}

	public boolean createAgreementPreservation(String pdfFilePath, HashMap<String, String> map) {
		// boolean rst = false;

		log.debug("come into JunziqianPreservation createAgreementPreservation");
		ApplySignFileRequest.Builder builder = new ApplySignFileRequest.Builder();
		try {
			builder.withFile(pdfFilePath);
			builder.withContractName(map.get("agreementNo"));
			builder.withContractAmount(0.0);

			// 签约人、签章位置、签章样式可对每个签约对象分别设置
			HashSet<Signatory> signatories = Sets.newHashSet();

			// 测试时请改为自己的个人信息进行测试（姓名、身份证号、手机号不能部分或全部隐藏）
			Signatory signatory = new Signatory();
			signatory.setFullName(map.get("realName")); // 姓名
			signatory.setSignatoryIdentityType(IdentityType.IDCARD); // 证件类型
			signatory.setIdentityCard(map.get("idCard")); // 证件号码
			signatory.setMobile(map.get("phone"));
			signatories.add(signatory);

			signatory = new Signatory();
			signatory.setFullName(COMPANY_NAME); // 姓名
			signatory.setSignatoryIdentityType(IdentityType.BIZLIC); // 证件类型
			signatory.setIdentityCard(COMPANY_IDNO);// 证件号码（营业执照号或统一社会信用代码）
			signatory.setEmail(COMPANY_EMAIL);// 企业账户注册邮箱
			signatories.add(signatory);

			builder.withSignatories(signatories); // 添加签约人
			builder.withDealType(DealType.ONLY_PRES);// 保全

		} catch (Exception e) {
			log.debug("ebaoquan api builder error: " + e.getMessage());
		}

		log.debug("before request baoquan");

		ApplySignResponse response = null;
		try {
			response = getClient().applySignFile(builder.build());
		} catch (Exception e) {
			log.debug("ebaoquan api error: " + e.getMessage());
		}

		log.debug("after request baoquan");
		log.info("respose" + response);

		EbaoquanRecord ebaoquanRecord = new EbaoquanRecord();
//		ebaoquanRecord.setDtId(0);
//		ebaoquanRecord.setDtContractId(Integer.parseInt(map.get("id")));
		ebaoquanRecord.setDtContractType(Integer.parseInt(map.get("type")));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//		ebaoquanRecord.setPreservationTime(df.format(new Date()));
		ebaoquanRecord.setDownloadUrl(map.get("title"));

		if (null != response) {
			if (response.isSuccess()) {
				log.info("创建保全成功!");
				log.info("保全ID:" + response.getApplyNo());

				ebaoquanRecord.setPreservationId(response.getApplyNo());
				ebaoquanRecord.setDocHash("0000");

				// List<EbaoquanRecord> ebaoquanList =
				// ebaoquanRecord.getByDtContractIdAndType(Integer.parseInt(map.get("id")),
				// Integer.parseInt(map.get("type")));
				// if (ebaoquanList.isEmpty()) {
				// rst = ebaoquanRecord.save();
				// } else {
				// rst = ebaoquanRecord.update();
				// }

			} else {
				// rst = false;
				MainError error = response.getError();
				String errorStr = "";
				if (error != null) {
					errorStr = "保全失败：" + error.getCode() + "|" + error.getMessage() + "|" + "Main Error Solution:"
							+ error.getSolution();
					log.error(errorStr);
				} else {
					errorStr = "保全失败：Error is null";
					log.error(errorStr);
				}

				ebaoquanRecord.setPreservationId("4444");
				ebaoquanRecord.setDocHash("4444");
				ebaoquanRecord.setDownloadUrl(errorStr);

				// List<EbaoquanRecord> ebaoquanList =
				// ebaoquanRecord.getByDtContractIdAndType(Integer.parseInt(map.get("id")),
				// Integer.parseInt(map.get("type")));
				// if (ebaoquanList.isEmpty()) {
				// rst = ebaoquanRecord.save();
				// } else {
				// rst = ebaoquanRecord.update();
				// }

			}
		} else {
			ebaoquanRecord.setPreservationId("5555");
			ebaoquanRecord.setDocHash("5555");
			ebaoquanRecord.setDownloadUrl("调用易保全失败，返回response为空，请查看连接情况！");

			// List<EbaoquanRecord> ebaoquanList =
			// ebaoquanRecord.getByDtContractIdAndType(Integer.parseInt(map.get("id")),
			// Integer.parseInt(map.get("type")));
			// if (ebaoquanList.isEmpty()) {
			// rst = ebaoquanRecord.save();
			// } else {
			// rst = ebaoquanRecord.update();
			// }
			log.error("调用易保全失败，返回response为空，请查看连接情况！");
		}
		ebaoquanRecord = recordService.save(ebaoquanRecord);
		return (ebaoquanRecord != null) ? true : false;
	}
}
