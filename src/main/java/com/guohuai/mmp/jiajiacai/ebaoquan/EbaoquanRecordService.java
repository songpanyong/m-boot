package com.guohuai.mmp.jiajiacai.ebaoquan;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.guohuai.component.util.DateUtil;
import com.guohuai.file.legal.LegalDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.FileParam;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.RegistFile;
import com.guohuai.mmp.jiajiacai.caculate.StringUtils;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenxian updated on 2018/02/01.
 */
@Slf4j
@Service
public class EbaoquanRecordService {

	final static String COMPANY_NAME = "北京尚世同禾科技有限公司";
	@Autowired
	private EbaoquanRecordDao recordDao;

	@Autowired
	private LegalDao legalDao;

	@Autowired
	private InvestorBaseAccountDao baseAccountDao;

	private static final Map<String, String> myMap = createMap();

	private static Map<String, String> createMap() {
		Map<String, String> myMap = new HashMap<String, String>();
		myMap.put("1", "GENREG");
		myMap.put("2", "GENHQBUY");
		myMap.put("3", "GENDQBUY");

		myMap.put("5", "GENXYPBUY");
		myMap.put("6", "GENXYPBUY");

		myMap.put("7", "GENDK");

		myMap.put("8", "GENDXWT");
		myMap.put("9", "GENDXWT");
		myMap.put("10", "GENDXWT");
		myMap.put("11", "GENDXWT");
		return myMap;
	}

	public <T> void eBaoquanRecord(int baoquanType, T object) {
		switch (baoquanType) {
		/** 家加财平台注册服务协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_REGIST:
			baoquanRegist((InvestorBaseAccountEntity) object);
			break;
		/** 家加财活期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN:
			/** 家加财定期期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_CLOSE:
			baoquanInvestProcut((InvestorTradeOrderEntity) object, baoquanType);
			break;
		/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE:
			baoquanInvestWishOnce((PlanInvestEntity) object);
			break;
		/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH:
			baoquanInvestWishMonth((PlanMonthEntity) object);
			break;

		default:
			break;
		}
	}

	@Transactional
	private <T> void createRecord(int baoquanType, String uid, String relatedKey, T obj) {
		if (!checkValid(baoquanType)) {
			return;
		}
		EbaoquanRecord record = new EbaoquanRecord();
		record.setOid(StringUtils.uuid());
		record.setCreateAt(DateUtil.getSqlCurrentDate());
		String preStr = myMap.get(String.valueOf(baoquanType));
//		record.setCodeId(preStr + uid.substring(uid.length() - 6) + DateUtil.currentTime());
		record.setCodeId(preStr + uid.substring(uid.length() - 6));
		record.setDtContractType(baoquanType);
		record.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_TOHTML);
		record.setRelatedKey(relatedKey);
		// Added parameters
		generateParameters(record, obj);
		recordDao.save(record);
	}

	private void baoquanRegist(InvestorBaseAccountEntity account) {
		createRecord(EbaoquanRecord.EBAOQUAN_TYPE_REGIST, account.getOid(), account.getOid(), account);
	}

	private void baoquanInvestProcut(InvestorTradeOrderEntity tradeOrder, int baoquanType) {
		if (baoquanType == EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN) {
			createRecord(EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN, tradeOrder.getInvestorBaseAccount().getOid(),
					tradeOrder.getOrderCode(), tradeOrder);
			createRecord(EbaoquanRecord.EBAOQUAN_TYPE_AGREE_OPEN, tradeOrder.getInvestorBaseAccount().getOid(),
					tradeOrder.getOrderCode(), tradeOrder);
		} else {
			createRecord(EbaoquanRecord.EBAOQUAN_TYPE_INVEST_CLOSE, tradeOrder.getInvestorBaseAccount().getOid(),
					tradeOrder.getOrderCode(), tradeOrder);
			createRecord(EbaoquanRecord.EBAOQUAN_TYPE_AGREE_CLOSE, tradeOrder.getInvestorBaseAccount().getOid(),
					tradeOrder.getOrderCode(), tradeOrder);
		}
	}

	private void baoquanInvestWishOnce(PlanInvestEntity pi) {
		createRecord(EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE, pi.getUid(), pi.getOid(), pi);
		createRecord(EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_ONCE, pi.getUid(), pi.getOid(), pi);
	}

	private void baoquanInvestWishMonth(PlanMonthEntity pm) {
		createRecord(EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH, pm.getUid(), pm.getOid(), pm);

		createRecord(EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_MONTH, pm.getUid(), pm.getOid(), pm);

		createRecord(EbaoquanRecord.EBAOQUAN_TYPE_WITHHOLD, pm.getUid(), pm.getOid(), pm);

	}

	@Transactional
	public EbaoquanRecord save(EbaoquanRecord record) {
		return recordDao.saveAndFlush(record);
	}

	final static String LEGAL_FILE_ENABLE = "enabled";

	private Boolean checkValid(int type) {
		String code = EbaoquanRecord.getCode(type);
		String status = legalDao.findStatusByCode(code);
		if (status.equals(LEGAL_FILE_ENABLE)) {
			return true;
		}
		return false;
	}

	public Boolean checkRecord(String relativeKey, int type) {
		int count = recordDao.baoquanCount(relativeKey, type);
		if (count > 0) {
			return false;
		}
		return true;
	}

	public <T> void generateParameters(EbaoquanRecord baoquan, T obj) {
		int baoquanType = baoquan.getDtContractType();
		switch (baoquanType) {
		/** 家加财平台注册服务协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_REGIST:
			generateRegistHtmlAgreement(baoquan, (InvestorBaseAccountEntity) obj);
			break;
		/** 家加财活期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN:
			/** 家加财定期期产品转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_INVEST_CLOSE:
			/** 家加财定向委托投资协议活期 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_OPEN:
			/** 家加财定向委托投资协议定期 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_CLOSE:
			processProduct(baoquan, (InvestorTradeOrderEntity) obj);
			break;
		/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE:
			/** 家加财定向委托投资协议心愿一次性 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_ONCE:
			generatePlanOnceHtmlAgreement(baoquan, (PlanInvestEntity) obj);
			break;
		/** 心愿计划转入协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH:
			/** 家加财定向委托投资协议月定投 */
		case EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_MONTH:
			/** 代扣服务协议 */
		case EbaoquanRecord.EBAOQUAN_TYPE_WITHHOLD:
			generatePlanMonthHtmlAgreement(baoquan, (PlanMonthEntity) obj);
			break;

		default:
			break;
		}

	}

	private void processProduct(EbaoquanRecord baoquan, InvestorTradeOrderEntity order) {
		FileParam fileParam = new FileParam();
		
		fileParam.setTradeTime(DateUtil.format(order.getCreateTime()));
		baoquan.setCodeId(baoquan.getCodeId() + DateUtil.timestamp2FullStr(order.getCreateTime()));
		
		fileParam.setConstractNum( baoquan.getCodeId());
		fileParam.setFirstParty(order.getInvestorBaseAccount().getRealName());
		fileParam.setFirstPartyIdNum(order.getInvestorBaseAccount().getIdNum());
		fileParam.setSecondParty(COMPANY_NAME);
		
		String jsonParam = JSON.toJSONString(fileParam);
		baoquan.setFileParam(jsonParam);

	}

	private void generatePlanMonthHtmlAgreement(EbaoquanRecord baoquan, PlanMonthEntity pm) {
		// 保存模板参数
		InvestorBaseAccountEntity account = baseAccountDao.findByOid(pm.getUid());
		FileParam fileParam = new FileParam();
		
		fileParam.setTradeTime(DateUtil.format(DateUtil.getSqlCurrentDate()));
		baoquan.setCodeId(baoquan.getCodeId() + DateUtil.timestamp2FullStr(DateUtil.getSqlCurrentDate()));
		
		fileParam.setConstractNum( baoquan.getCodeId());
		fileParam.setFirstParty(account.getRealName());
		fileParam.setFirstPartyIdNum(account.getIdNum());
		fileParam.setSecondParty(COMPANY_NAME);
		
		
		String jsonParam = JSON.toJSONString(fileParam);
		baoquan.setFileParam(jsonParam);
		
	}

	private void generatePlanOnceHtmlAgreement(EbaoquanRecord baoquan, PlanInvestEntity planInvest) {
		// 保存模板参数
		InvestorBaseAccountEntity account = baseAccountDao.findByOid(planInvest.getUid());
		FileParam fileParam = new FileParam();
		
		fileParam.setTradeTime(DateUtil.format(planInvest.getCreateTime()));
		baoquan.setCodeId(baoquan.getCodeId() + DateUtil.timestamp2FullStr(planInvest.getCreateTime()));
		
		log.info("计划购买开始 CodeId={}", baoquan.getCodeId());
		
		fileParam.setConstractNum( baoquan.getCodeId());
		fileParam.setFirstParty(account.getRealName());
		fileParam.setFirstPartyIdNum(account.getIdNum());
		fileParam.setSecondParty(COMPANY_NAME);
		
		String jsonParam = JSON.toJSONString(fileParam);
		baoquan.setFileParam(jsonParam);

	}

	private void generateRegistHtmlAgreement(EbaoquanRecord baoquan, InvestorBaseAccountEntity account) {
		RegistFile registFile = new RegistFile();
		
		registFile.setTradeTime(DateUtil.format(account.getCreateTime()));
		baoquan.setCodeId(baoquan.getCodeId() + DateUtil.timestamp2FullStr(account.getCreateTime()));
		
		registFile.setConstractNum(baoquan.getCodeId());
		registFile.setFirstParty(account.getPhoneNum());
		registFile.setSecondParty(COMPANY_NAME);
		
		String jsonParam = JSON.toJSONString(registFile);
		baoquan.setFileParam(jsonParam);

		baoquan.setStatus(EbaoquanRecord.EBAOQUAN_CONTRACTSTATUS_TOREALNAME);
	}
}
