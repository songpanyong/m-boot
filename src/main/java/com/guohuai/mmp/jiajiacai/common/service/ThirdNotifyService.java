package com.guohuai.mmp.jiajiacai.common.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bank.BankDao;
import com.guohuai.mmp.investor.bank.BankEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.sonaccount.SonAccountDao;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListDao;
import com.guohuai.mmp.platform.msgment.AbortiveRep;
import com.guohuai.mmp.platform.msgment.MailService;
import com.guohuai.mmp.platform.msgment.MsgParam;
import com.guohuai.mmp.platform.msgment.MsgService;
import com.guohuai.mmp.platform.msgment.MsgUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ThirdNotifyService {

	static final String KEY_PHONE = "phone";
	static final String KEY_UID = "uid";
	static final String KEY_PARAM = "param";
	static final String KEY_PLANOID = "planOid";

	@Autowired
	private MsgService msgService;

	@Autowired
	private MailService mailService;

	@Autowired
	private MsgUtil msgUtil;

	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private PlanInvestDao planInvestDao;

	@Autowired
	private BankDao bankDao;
	
	@Autowired
	private SonAccountDao sonAccountDao;

	@Autowired
	private PlanListDao planListDao;
	/**
	 * Check the user type
	 * 
	 * @return
	 */
	private boolean isChildAccount(String uid) {
		InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOid(uid);
		if(baseAccount!=null){
			 return  baseAccount.getPhoneNum().length()!=11;
		}else{
			throw new AMPException("账户不存在");
		}
	}
	
	/**
	 * getMasterPhone
	 * @param uid
	 * @param isChild
	 * @return
	 */
	/*private String getMasterPhone(String uid, boolean isChild) {
		String phone = null;
		if (isChild) {
			SonAccountEntity sonAccount = this.sonAccountDao.findBySid(uid);			
			InvestorBaseAccountEntity  pBasicAccount  =  this.investorBaseAccountDao.findByOid(sonAccount.getPid());
			
			System.out.println(pBasicAccount);
			if(pBasicAccount!=null){
				phone = pBasicAccount.getPhoneNum();
			}
		} else {
			phone = investorBaseAccountDao.findPhoneByOid(uid);
		}
		
		return phone;
	}*/

	/**
	 * 通过主账户的id来获取主账户的手机号
	 * @param  id
	 * 
	 * */
	public String getMasterPhone(String id){
		InvestorBaseAccountEntity baseAccount =  this.investorBaseAccountDao.findByOid(id);
		return baseAccount.getPhoneNum();
	}

	/**
	 * Get the child name
	 * 
	 * @return
	 */
	private String getChildAccount(String childUid) {
		return sonAccountDao.queryNicknameByOid(childUid);
	}
	
	/**
	 * 
	 * 获取子账户的昵称和主账户的id
	 * 
	 * */
	private SonAccountEntity getBySid(String sid){
		return sonAccountDao.findBySid(sid);
	}

	/**
	 * Convert the msg parameters to mail type
	 * 
	 * @param msgType
	 * @return
	 */
	private MsgParam msg2mail(MsgParam msgType) {
		String type = msgType.name();
		String mailStr = "mail" + type.substring(3);
		MsgParam mailType = MsgParam.valueOf(mailStr);
		return mailType;
	}

	/**
	 * Convert the msg parameters to son's type
	 * 
	 * @param msgType
	 * @return
	 */
	private MsgParam msg2sonMsg(MsgParam msgType) {
		String type = msgType.name();
		String sonStr = "msgson" + type.substring(3);
		MsgParam sonType = MsgParam.valueOf(sonStr);
		return sonType;
	}

	public <T> void callMsgMail(MsgParam msgType, T object, String userid) {
		Boolean flag = true;
		if (this.isChildAccount(userid)) {
			msgType = msg2sonMsg(msgType);
		}

		Map<String, String> map = new HashMap<String, String>();

		switch (msgType) {

		case msgRechargeSuccess:
			break;
		case msgBuySuccess:
			break;
		case msgAbortive:
			break;
		case msgWithdrawApply:
			break;
		case msgWithdrawSuccess:
			break;
		case msgReceivedpayments:
			break;
		case msgIncomedistrinotice:
			break;

		case msgdepositbindcardremind:
			break;

		case msgsonBuySuccess:
			break;
		case msgsuccessjoinplanonetime:
			wishplanOneInvest((PlanInvestEntity) object, false, map);
			break;
		case msgsonsuccessjoinplanonetime:
			wishplanOneInvest((PlanInvestEntity) object, true, map);
			break;
		case msgsuccessjoinplanbymonth:
		case msgsetwageincreasesuccess:
		case msgmodifyplanbyonemonthsuccess:
		case msgmodifywageincreasesuccess:
			wishplanMonthInvest((PlanMonthEntity) object, false, map);
			break;
		case msgsonsuccessjoinplanbymonth:
		case msgsonsetwageincreasesuccess:
		case msgsonmodifyplanbyonemonthsuccess:
		case msgsonmodifywageincreasesuccess:
			wishplanMonthInvest((PlanMonthEntity) object, true, map);
			break;
		case msgbymonthbuysuccess:
			withholdResult((PlanInvestEntity) object, false, map, true);
			break;
		case msgsonbymonthbuysuccess:
			withholdResult((PlanInvestEntity) object, true, map, true);
			break;
		case msgfirstbymonthbuyfail:
		case msgDeductFailForSystemNoSupportCard:
		case msgDeductFailForChannelNoSupportCard:
		case msgDeductFailForOnlineBanking:
		case msgDeductFailForOtherReason:
			withholdResult((PlanInvestEntity) object, false, map, false);
			flag = false;
			break;
		case msgsonfirstbymonthbuyfail:
		case msgsonDeductFailForSystemNoSupportCard:
		case msgsonDeductFailForChannelNoSupportCard:
		case msgsonDeductFailForOnlineBanking:
		case msgsonDeductFailForOtherReason:
			withholdResult((PlanInvestEntity) object, true, map, false);
			flag = false;
			break;
		case msgstopbymonth:
			stopMonthInvest((PlanMonthEntity) object, false, map);
			break;
		case msgsonstopbymonth:
			stopMonthInvest((PlanMonthEntity) object, true, map);
			break;
		case msgwishplanreceivedpayments:
			receiveOneInvest((PlanInvestEntity) object, false, map);
			break;
		case msgsonwishplanreceivedpayments:
			receiveOneInvest((PlanInvestEntity) object, true, map);
			break;

		case msgmonthplanreceivedpayments:
			receiveMonthInvest((PlanMonthEntity) object, false, map);
			msgType = MsgParam.msgwishplanreceivedpayments;
			break;
		case msgsonmonthplanreceivedpayments:
			receiveMonthInvest((PlanMonthEntity) object, true, map);
			msgType = MsgParam.msgsonwishplanreceivedpayments;
			break;

		case msgbalancemonthbuysuccess:
			balanceSuccess((PlanInvestEntity) object, false, map);
			msgType = MsgParam.msgbymonthbuysuccess;
			break;
		case msgsonbalancemonthbuysuccess:
			balanceSuccess((PlanInvestEntity) object, true, map);
			msgType = MsgParam.msgsonbymonthbuysuccess;
			break;
		default:
			break;
		}

		String phone = map.get(KEY_PHONE);
		String uid = map.get(KEY_UID);
		String params = map.get(KEY_PARAM);
		String wishplanOid = map.get(KEY_PLANOID);

		if(flag){
			if (phone != null && params != null) {
				msgService.msgNotify(phone, msgType, params, wishplanOid);
				
			} else {
				log.error(msgType.getInnerFaceName(), "params error");
			}
		}

		MsgParam mailType = msg2mail(msgType);
		if (uid != null && params != null) {
			mailService.mailNotify(uid, mailType, params, wishplanOid);
		} else {
			log.error(mailType.getInnerFaceName(), "params error");
		}

	}

	/**
	 * Investor invest one plan
	 * 
	 * @param planInvest
	 * @param isChild
	 * @param map
	 */
	private void wishplanOneInvest(PlanInvestEntity planInvest, boolean isChild, Map<String, String> map) {
		String msgParams;
		String mailOid;
//		String planName = planListDao.findPlanNameByType(planInvest.getPlanType());
		String planName = JJCUtility.plantype2Str(planInvest.getPlanType());
		BigDecimal money = planInvest.getDepositAmount();
//		String duration = String.format("%d", planInvest.getInvestDuration());
//		/** 将站内信中的日期由日变为月 */
//		String investDuration = String.valueOf(Integer.parseInt(duration)/30);
		String investDuration = String.format("%d",DateUtil.getMonthSpace(planInvest.getEndTime(), planInvest.getCreateTime()));
		if (isChild) {
			//String childAccount = getChildAccount(planInvest.getUid());
			SonAccountEntity son =  this.getBySid(planInvest.getUid());
			String childAccount = son.getNickname();
			mailOid = son.getPid();
			msgParams = msgUtil.assembleMsgParams(childAccount, planName, money, investDuration);
		} else {
			mailOid = planInvest.getUid();
			msgParams = msgUtil.assembleMsgParams(planName, money, investDuration);
		}
		//String phone = getMasterPhone(planInvest.getUid(), isChild);
		String phone = getMasterPhone(mailOid);
		
		map.put(KEY_PHONE, phone);
		//map.put(KEY_UID, planInvest.getUid());
		map.put(KEY_UID,mailOid);
		map.put(KEY_PARAM, msgParams);
		map.put(KEY_PLANOID, planInvest.getOid());
	}

	/**
	 * Investor invest month plan
	 * 
	 * @param planInvest
	 * @param isChild
	 * @param map
	 */
	private void wishplanMonthInvest(PlanMonthEntity planInvest, boolean isChild, Map<String, String> map) {
		String msgParams;
		String mailOid;
		//String planName = planListDao.findPlanNameByType(planInvest.getPlanType());
		String planName = JJCUtility.plantype2Str(planInvest.getPlanType());
		String duration = null;
		if (!planInvest.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
			duration = String.format("%d", planInvest.getPlanMonthCount());
		}
		String dayOnMonth = String.format("%d", planInvest.getMonthInvestDate());
		BigDecimal money = planInvest.getMonthAmount();

		if (isChild) {
			//String childAccount = getChildAccount(planInvest.getUid());
			SonAccountEntity son = this.getBySid(planInvest.getUid());	
				mailOid = son.getPid();
				String childAccount = son.getNickname();
			
			if (duration != null) {
				msgParams = msgUtil.assembleMsgParams(childAccount, planName, duration, dayOnMonth, money);
			} else {
				msgParams = msgUtil.assembleMsgParams(childAccount, planName, dayOnMonth, money);
			}
		} else {
			mailOid = planInvest.getUid();
			if (duration != null) {
				msgParams = msgUtil.assembleMsgParams(planName, duration, dayOnMonth, money);
			} else {
				msgParams = msgUtil.assembleMsgParams(planName, dayOnMonth, money);
			}
		}
//		String phone = investorBaseAccountDao.findPhoneByOid(planInvest.getUid());
		String phone = getMasterPhone(mailOid);
		map.put(KEY_PHONE, phone);
		//map.put(KEY_UID, planInvest.getUid());
		map.put(KEY_UID, mailOid);
		map.put(KEY_PARAM, msgParams);
		map.put(KEY_PLANOID, planInvest.getOid());
	}

	/**
	 * Buy plan from withhold
	 * 
	 * @param planInvest
	 * @param isChild
	 * @param map
	 * @param isSuccess
	 */
	private void withholdResult(PlanInvestEntity planInvest, boolean isChild, Map<String, String> map,
			boolean isSuccess) {
		String msgParams;
		String mailOid;
		//String planName = planListDao.findPlanNameByType(planInvest.getPlanType());
		String planName = JJCUtility.plantype2Str(planInvest.getPlanType());
		BankEntity be = bankDao.getOKBankByInvestorOid(planInvest.getUid());
		//银行4位尾号
		/*
		String dc = be.getDebitCard();
		String bankInfo = be.getBankName() + " (尾号" + dc.substring(dc.length() - 4) + ")";
		*/
		//避免解除绑卡后异常
		String bankInfo = null;
		if (be != null && be.getDebitCard() != null && be.getDebitCard().length() > 4) {
			int cardLen = be.getDebitCard().length();
			bankInfo = be.getBankName() + "(尾号" + be.getDebitCard().substring(cardLen - 4, cardLen) + ")";
		} else {
			bankInfo = "无银行卡绑定";
		}
		BigDecimal money = planInvest.getDepositAmount();
		if (isChild) {
			//tring childAccount = getChildAccount(planInvest.getUid());;
			SonAccountEntity son =  this.getBySid(planInvest.getUid());
			String childAccount = son.getNickname();
			mailOid = son.getPid();
			msgParams = msgUtil.assembleMsgParams(childAccount, planName, bankInfo, money);
		} else {
			mailOid = planInvest.getUid();
			msgParams = msgUtil.assembleMsgParams(planName, bankInfo, money);
		}
//		String phone = investorBaseAccountDao.findPhoneByOid(planInvest.getUid());
		//String phone = getMasterPhone(planInvest.getUid(), isChild);
		String phone = getMasterPhone(mailOid);
		map.put(KEY_PHONE, phone);
		//map.put(KEY_UID, planInvest.getUid());
		map.put(KEY_UID, mailOid);
		map.put(KEY_PARAM, msgParams);
		map.put(KEY_PLANOID, planInvest.getOid());
	}

	/**
	 * Auto buy plan from balance firstly.
	 * 
	 * @param planInvest
	 * @param isChild
	 * @param map
	 */
	private void balanceSuccess(PlanInvestEntity planInvest, boolean isChild, Map<String, String> map) {
		String msgParams;
		String mailOid;
		//String planName = planListDao.findPlanNameByType(planInvest.getPlanType());
		String planName = JJCUtility.plantype2Str(planInvest.getPlanType());
		String bankInfo = "账户余额";
		BigDecimal money = planInvest.getDepositAmount();
		if (isChild) {
			//String childAccount = getChildAccount(planInvest.getUid());;
			SonAccountEntity son =  this.getBySid(planInvest.getUid());
			String childAccount = son.getNickname();
			mailOid = son.getPid();
			msgParams = msgUtil.assembleMsgParams(childAccount, planName, bankInfo, money);
		} else {
			mailOid = planInvest.getUid();
			msgParams = msgUtil.assembleMsgParams(planName, bankInfo, money);
		}
//		String phone = investorBaseAccountDao.findPhoneByOid(planInvest.getUid());
		//String phone = getMasterPhone(planInvest.getUid(), isChild);
		String phone = getMasterPhone(mailOid);
		map.put(KEY_PHONE, phone);
		//map.put(KEY_UID, planInvest.getUid());
		map.put(KEY_UID, mailOid);
		map.put(KEY_PARAM, msgParams);
		map.put(KEY_PLANOID, planInvest.getOid());
	}

	/**
	 * Stop month plan
	 * 
	 * @param planInvest
	 * @param isChild
	 * @param map
	 */
	private void stopMonthInvest(PlanMonthEntity planInvest, boolean isChild, Map<String, String> map) {
		String msgParams;
		String mailOid;
	//	String planName = planListDao.findPlanNameByType(planInvest.getPlanType());
		String planName = JJCUtility.plantype2Str(planInvest.getPlanType());
		if (isChild) {
			
			//String childAccount = getChildAccount(planInvest.getUid());;
			SonAccountEntity son =  this.getBySid(planInvest.getUid());
			String childAccount = son.getNickname();
			mailOid = son.getPid();
			msgParams = msgUtil.assembleMsgParams(childAccount, planName);
		} else {
			mailOid = planInvest.getUid();
			msgParams = msgUtil.assembleMsgParams(planName);
		}
//		String phone = investorBaseAccountDao.findPhoneByOid(planInvest.getUid());
		//String phone = getMasterPhone(planInvest.getUid(), isChild);
		String phone = getMasterPhone(mailOid);
		map.put(KEY_PHONE, phone);
		//map.put(KEY_UID, planInvest.getUid());
		map.put(KEY_UID, mailOid);
		map.put(KEY_PARAM, msgParams);
		map.put(KEY_PLANOID, planInvest.getOid());
	}

	/**
	 * Month plan education and tour
	 * 
	 * @param planInvest
	 * @param isChild
	 * @param map
	 */
	private void receiveMonthInvest(PlanMonthEntity planInvest, boolean isChild, Map<String, String> map) {
		String msgParams;
		String mailOid;
	//	String planName = planListDao.findPlanNameByType(planInvest.getPlanType());
		String planName = JJCUtility.plantype2Str(planInvest.getPlanType());
		BigDecimal money = planInvest.getIncome();
		if (isChild) {
			//String childAccount = getChildAccount(planInvest.getUid());;
			SonAccountEntity son =  this.getBySid(planInvest.getUid());
			String childAccount = son.getNickname();
			mailOid = son.getPid();
			msgParams = msgUtil.assembleMsgParams(childAccount, planName, money);
		} else {
			mailOid = planInvest.getUid();
			msgParams = msgUtil.assembleMsgParams(planName, money);
		}
//		String phone = investorBaseAccountDao.findPhoneByOid(planInvest.getUid());
		//String phone = getMasterPhone(planInvest.getUid(), isChild);
		String phone = getMasterPhone(mailOid);
		map.put(KEY_PHONE, phone);
		//map.put(KEY_UID, planInvest.getUid());
		map.put(KEY_UID, mailOid);
		map.put(KEY_PARAM, msgParams);
		map.put(KEY_PLANOID, planInvest.getOid());
	}

	/**
	 * Tour and education plan
	 * 
	 * @param planInvest
	 * @param isChild
	 * @param map
	 */
	private void receiveOneInvest(PlanInvestEntity planInvest, boolean isChild, Map<String, String> map) {
		String msgParams;
		String mailOid;
	//	String planName = planListDao.findPlanNameByType(planInvest.getPlanType());
		String planName = JJCUtility.plantype2Str(planInvest.getPlanType());
		BigDecimal money = planInvest.getBalance();
		if (isChild) {
			//String childAccount = getChildAccount(planInvest.getUid());;
			SonAccountEntity son =  this.getBySid(planInvest.getUid());
			String childAccount = son.getNickname();
			mailOid = son.getPid();
			msgParams = msgUtil.assembleMsgParams(childAccount, planName, money);
		} else {
			mailOid = planInvest.getUid();
			msgParams = msgUtil.assembleMsgParams(planName, money);
		}
//		String phone = investorBaseAccountDao.findPhoneByOid(planInvest.getUid());
		//String phone = getMasterPhone(planInvest.getUid(), isChild);
		String phone = getMasterPhone(mailOid);
		map.put(KEY_PHONE, phone);
		//map.put(KEY_UID, planInvest.getUid());
		map.put(KEY_UID, mailOid);
		map.put(KEY_PARAM, msgParams);
		map.put(KEY_PLANOID, planInvest.getOid());
	}
	
	
	
	/**
	 * 通过用户的id判断是否为主、子账户
	 *
	 *return boolean
	 * */
	public boolean judgeByOid(String investorOid){
		//String markId = this.investorBaseAccountDao.findMarkIdByOid(investorOid);
		InvestorBaseAccountEntity baseAccount =  this.investorBaseAccountDao.findByOid(investorOid);
		if(baseAccount!=null&&baseAccount.getPhoneNum().length()==11){
			return true;
		}return false;
	}
	
}
