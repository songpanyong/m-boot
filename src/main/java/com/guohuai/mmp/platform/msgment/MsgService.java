package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.sonaccount.SonAccountDao;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;
import com.guohuai.mmp.jiajiacai.common.service.ThirdNotifyService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.platform.msgment.log.MsgLogReq;
import com.guohuai.mmp.platform.msgment.log.MsgLogService;
import com.guohuai.mmp.sms.SMSUtils;

import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@Slf4j
public class MsgService {
	
//	@Autowired
//	private UserCentreApi userCentreApi;
	@Autowired
	private MsgLogService msgLogService;
	@Autowired
	private MsgUtil msgUtil;
	@Autowired
	private SMSUtils sMSUtils;
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private PlanInvestDao planInvestDao;
	@Autowired
	private ThirdNotifyService thirdNotifyService;
	@Autowired
	private SonAccountDao sonAccountDao;
	
	static final boolean IS_WRITE_LOG = true;
	/**
	 * 充值成功
	 * 充值成功	rechargesuccess	["金额"]		【家加财】恭喜您成功充值{1}元！请前往个人中心查看详情。
	 * 			sonrechargesuccess			【家加财】恭喜您的子账户{1}成功充值{2}元！请前往个人中心查看详情。
	 */
	public void rechargesuccess(RechargeSuccessMsgReq ireq) {
		//增加主子账户的逻辑
		if(judgeByPhone(ireq.getPhone())){
			//主
			this.rechargesuccess(ireq, true);
		}else{
			//子
			this.sonRechargesuccess(ireq, true);
		}
		
	}
	/**主账户充值成功 */
	public void rechargesuccess(RechargeSuccessMsgReq ireq, boolean isLog) {
		BaseResp irep = new BaseResp();
		try {
			irep = this.sMSUtils.sendSMS(ireq.getPhone(), MsgParam.msgRechargeSuccess.toString(), JSON.parseObject(msgUtil.assembleMsgParams(ireq.getOrderAmount()), String[].class));
//			irep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgRechargeSuccess.toString(), msgUtil.assembleMsgParams(ireq.getOrderAmount()));
		} catch (Exception e) {
			log.error("msgRechargeSuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.msgRechargeSuccess.getInnerFaceName());
		}
	}
	
	/**子账户充值成功 */
	public void sonRechargesuccess(RechargeSuccessMsgReq ireq, boolean isLog) {
		BaseResp irep = new BaseResp();
		 PhoneAndNickName rep = getByPhone(ireq.getPhone());
		 MsgSonDepositRep mdrep = new MsgSonDepositRep();
		 mdrep.setPhone(rep.getPhone());
		 mdrep.setNickName(rep.getNickName());
		 mdrep.setAmount(ireq.getOrderAmount());
		 
		try {
			irep = this.sMSUtils.sendSMS(mdrep.getPhone(), MsgParam.msgSonRechargeSuccess.toString(), JSON.parseObject(msgUtil.assembleMsgParams(mdrep.getAmount(),ireq.getOrderAmount()), String[].class));
//			irep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgRechargeSuccess.toString(), msgUtil.assembleMsgParams(ireq.getOrderAmount()));
		} catch (Exception e) {
			log.error("msgSonRechargeSuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, mdrep, MsgParam.msgSonRechargeSuccess.getInnerFaceName());
		}
	}
	
	/**
	 * 投资成功
	 * 投资成功	buysuccess	["产品名称"]		家加财】恭喜您成功投资{1}理财产品，系统会在1-2个工作日确认，如有问题请致电客服热线。
	 */
	public void buysuccess(BuySuccessMsgReq ireq) {
		
		if(judgeByPhone(ireq.getPhone())){
			this.buysuccess(ireq, true);
		}else{
			this.sonBuysuccess(ireq, true);
		}
		
	}
	/**主账户购买成功*/
	public void buysuccess(BuySuccessMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		String type = MsgParam.msgBuySuccess.toString();
		try {
			orep = this.sMSUtils.sendSMS(ireq.getPhone(), type, JSON.parseObject(msgUtil.assembleMsgParams(ireq.getProductName()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgBuySuccess.toString(), msgUtil.assembleMsgParams(ireq.getProductName()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgBuySuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.msgBuySuccess.getInnerFaceName());
		}
	}
	
	/**
	 *子账户购买活期、定期产品成功
	 *【家加财】恭喜您为子账户{1}成功投资{2}理财产品，系统会在1-2个工作日确认，如有问题请致电客服热线。
	 * */
	public void sonBuysuccess(BuySuccessMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		PhoneAndNickName rep = getByPhone(ireq.getPhone());
		PhoneAndNickNameAndProduct pnp = new PhoneAndNickNameAndProduct();
		pnp.setPhone(rep.getPhone());
		pnp.setNickname(rep.getNickName());
		pnp.setProductName(ireq.getProductName());
		
		String type = MsgParam.msgSonBuySuccess.toString();
		
		try {
			orep = this.sMSUtils.sendSMS(pnp.getPhone(), type, JSON.parseObject(msgUtil.assembleMsgParams(pnp.getNickname(),ireq.getProductName()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgBuySuccess.toString(), msgUtil.assembleMsgParams(ireq.getProductName()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgSonBuySuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, pnp, MsgParam.msgSonBuySuccess.getInnerFaceName());
		}
	}

	/**
	 * 流标
	 * 流标	abortive	
	 */
	public void abortive(AbortiveMsgReq ireq) {
		/**  增加主子账户的逻辑  */
		if(ireq.getPhone().length()==11){
			//主
			this.abortive(ireq, true);
		}else{
			//子
			this.sonAbortive(ireq, true);
		}
		
	}
	/**
	 * 主账户流标
	 * */
	public void abortive(AbortiveMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			orep = this.sMSUtils.sendSMS(ireq.getPhone(), MsgParam.msgAbortive.toString(), JSON.parseObject(msgUtil.assembleMsgParams(ireq.getProductName(),ireq.getHotLine()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgAbortive.toString(), msgUtil.assembleMsgParams(ireq.getProductName(), ireq.getHotLine()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgAbortive interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.msgAbortive.getInnerFaceName());
		}
	}
	/**
	 * 子账户流标
	 * 【家加财】您为子账户{1}投资的{2}理财产品发生流标，如有疑问，请联系客服{3}。
	 * */
	public void sonAbortive(AbortiveMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		/**增加子账户的逻辑*/
		PhoneAndNickName rep = getByPhone(ireq.getPhone());
		MsgSonAbortiveRep marep = new MsgSonAbortiveRep();
		marep.setNickName(rep.getNickName());
		marep.setPhone(rep.getPhone());
		marep.setProduct(ireq.getProductName());
		marep.setHotLine(ireq.getHotLine());
		try {
			orep = this.sMSUtils.sendSMS(marep.getPhone(), MsgParam.msgSonAbortive.toString(), JSON.parseObject(msgUtil.assembleMsgParams(marep.getNickName(),marep.getProduct(),marep.getHotLine()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgAbortive.toString(), msgUtil.assembleMsgParams(ireq.getProductName(), ireq.getHotLine()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgSonAbortive interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, marep, MsgParam.msgSonAbortive.getInnerFaceName());
		}
	}
	
	/**
	 * 提现申请
	 * 提现申请	withdrawapply	
	 */
	public void withdrawapply(WithdrawApplyMsgReq ireq) {
		if(judgeByPhone(ireq.getPhone())){
			this.withdrawapply(ireq, true);
		}else{
			this.sonWithdrawapply(ireq, true);
		}
		
	}
	
	/**
	 * 主账户提现申请
	 * 
	 * ["时间","提现金额","手续费","预计到账金额"]      【家加财】您于{1}提交提现申请，提现金额{2}元，手续费{3}元，预计到账金额{4}元，我们会在1个工作日之内处理。
	 * */
	public void withdrawapply(WithdrawApplyMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			orep = this.sMSUtils.sendSMS(ireq.getPhone(), MsgParam.msgWithdrawApply.toString(), JSON.parseObject(msgUtil.assembleMsgParams(ireq.getOrderTime(), ireq.getOrderAmount(),ireq.getFee(),ireq.getPreAmount()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgWithdrawApply.toString(), msgUtil.assembleMsgParams(ireq.getOrderTime(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgWithdrawApply interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.msgWithdrawApply.getInnerFaceName());
		}
	}
	
	/**
	 * 子账户提现申请提醒
	 *  ["昵称","时间","提现金额","手续费","预计到账金额"]     
	 *  【家加财】您的子账号{1}于{2}提交提现申请，提现金额{3}元，手续费{4}元，预计到账金额{5}元，我们会在1个工作日之内处理。
	 * */
	public void sonWithdrawapply(WithdrawApplyMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		PhoneAndNickName rep= getByPhone(ireq.getPhone());
		NicknameAndTimeAndAmount mta = new NicknameAndTimeAndAmount();
		mta.setPhone(rep.getPhone());
		mta.setNickname(rep.getNickName());
		mta.setOrderTime(ireq.getOrderTime());
		mta.setOrderAmount(ireq.getOrderAmount());
		mta.setFee(ireq.getFee());
		mta.setPreAmount(ireq.getPreAmount());
		
		try {
			orep = this.sMSUtils.sendSMS(mta.getPhone(), MsgParam.msgSonWithdrawApply.toString(), JSON.parseObject(msgUtil.assembleMsgParams(mta.getNickname(),mta.getOrderTime(),mta.getOrderAmount(),mta.getFee(),mta.getPreAmount()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgWithdrawApply.toString(), msgUtil.assembleMsgParams(ireq.getOrderTime(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgSonWithdrawApply interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, mta, MsgParam.msgSonWithdrawApply.getInnerFaceName());
		}
	}
	
	/**
	 * 提现到账
	 * 提现到账	
	 */
	public void withdrawsuccess(WithdrawSuccessMsgReq ireq) {
		if(judgeByPhone(ireq.getPhone())){
			this.withdrawsuccess(ireq, true);
		}else{
			this.sonWithdrawsuccess(ireq, true);
		}
		
	}
	
	/**
	 * 主账户提现到账
	 * ["时间","实际到账金额"]		【家加财】您于{1}提现金额{2}元（已扣除手续费）已转入您指定的银行账号，具体到账时间请参照各银行规定。
	 * 
	 * */
	public void withdrawsuccess(WithdrawSuccessMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			orep = this.sMSUtils.sendSMS(ireq.getPhone(), MsgParam.msgWithdrawSuccess.toString(), JSON.parseObject(msgUtil.assembleMsgParams(ireq.getCompleteTime(), ireq.getRealAmount()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgWithdrawSuccess.toString(), msgUtil.assembleMsgParams(ireq.getCompleteTime(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgWithdrawSuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.msgWithdrawSuccess.getInnerFaceName());
		}
	}
	
	/**
	 * 子账户提现到账
	 * 
	 *  ["昵称","时间","实际到账金额"]   
	 *  【家加财】您的子账号{1}于{2}提现金额{3}元（已扣除手续费）已转入您指定的银行账号，具体到账时间请参照各银行规定。
	 * 
	 * */
	public void sonWithdrawsuccess(WithdrawSuccessMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		PhoneAndNickName rep= getByPhone(ireq.getPhone());
		NickNameAndPhoneAndTimeAndAmount mta = new NickNameAndPhoneAndTimeAndAmount();
		mta.setPhone(rep.getPhone());
		mta.setNickname(rep.getNickName());
		mta.setTime(ireq.getCompleteTime());
		mta.setRealAmount(ireq.getRealAmount());
		
		
		try {
			orep = this.sMSUtils.sendSMS(mta.getPhone(), MsgParam.msgSonWithdrawSuccess.toString(), JSON.parseObject(msgUtil.assembleMsgParams(mta.getNickname(),mta.getTime(),mta.getRealAmount()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgWithdrawSuccess.toString(), msgUtil.assembleMsgParams(ireq.getCompleteTime(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgSonWithdrawSuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, mta, MsgParam.msgSonWithdrawSuccess.getInnerFaceName());
		}
	}
	
	
	
	/**
	 * 回款
	 * 回款	receivedpayments	["产品名称","金额"]		【家加财】您投资的{1}理财产品本次回款{2}元，如需详情请查看资金记录。
	 * 
	 * 
	 */
	public void receivedpayments(ReceivedPaymentsMsgReq ireq) {
		if(judgeByPhone(ireq.getPhone())){
			this.receivedpayments(ireq, true);
		}
		else{
			this.sonReceivedpayments(ireq, true);
		}
	}
	/** 主账户回款  */
	public void receivedpayments(ReceivedPaymentsMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			orep = this.sMSUtils.sendSMS(ireq.getPhone(), MsgParam.msgReceivedpayments.toString(), JSON.parseObject(msgUtil.assembleMsgParams(ireq.getProductName(), ireq.getOrderAmount()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgReceivedpayments.toString(), msgUtil.assembleMsgParams(ireq.getProductName(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgReceivedpayments interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.msgReceivedpayments.getInnerFaceName());
		}
	}
	/**
	 * 子账户回款
	 * 
	 * 【家加财】您子账号{1}投资的{2}本次回款{3}元，如需详情请查看资金记录。
	 * */
	public void sonReceivedpayments(ReceivedPaymentsMsgReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		PhoneAndNickName rep= getByPhone(ireq.getPhone());
		NicknameAndProductAndAmount npa = new NicknameAndProductAndAmount();
		npa.setPhone(rep.getPhone());
		npa.setNickname(rep.getNickName());
		npa.setProductName(ireq.getProductName());
		npa.setOrderAmount(ireq.getOrderAmount());
		try {
			orep = this.sMSUtils.sendSMS(npa.getPhone(), MsgParam.msgSonReceivedpayments.toString(), JSON.parseObject(msgUtil.assembleMsgParams(npa.getNickname(),  ireq.getProductName(), ireq.getOrderAmount()), String[].class));
//			orep = this.userCentreApi.sendSms(ireq.getPhone(), MsgParam.msgReceivedpayments.toString(), msgUtil.assembleMsgParams(ireq.getProductName(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("msgSonReceivedpayments interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, npa, MsgParam.msgSonReceivedpayments.getInnerFaceName());
		}
	}

	private void setIrepException(BaseResp irep, Exception e) {
		irep.setErrorCode(-1);
		irep.setErrorMessage(AMPException.getStacktrace(e));
	}

	private void setIrep(BaseResp orep, BaseResp irep) {
		if (null == orep) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("返回为空");
		} else if (orep.getErrorCode() != 0) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(orep.getErrorMessage());
		}
	}

	private void writeLog(BaseResp irep, Object ireq, String interfaceName) {
		MsgLogReq req = new MsgLogReq();
		
		req.setInterfaceName(interfaceName);
		req.setSendedTimes(1);
		req.setSendObj(JSONObject.toJSONString(ireq));
		req.setErrorCode(irep.getErrorCode());
		req.setErrorMessage(irep.getErrorMessage());
		msgLogService.createEntity(req);
	}
	
	/**
	 * Added by chenxian
	 * @param irep
	 * @param params
	 * @param interfaceName
	 */
	private void writeLog(BaseResp irep, String params, String interfaceName, String wishplanOid) {
		MsgLogReq req = new MsgLogReq();

		req.setInterfaceName(interfaceName);
		req.setSendedTimes(1);
		req.setSendObj(JSONObject.toJSONString(params));
		req.setErrorCode(irep.getErrorCode());
		req.setErrorMessage(irep.getErrorMessage());
		//Record the wishplan
		req.setObjectOid(wishplanOid);
		msgLogService.createEntity(req);
	}

	/**
	 * 
	 * @param phone
	 * @param paramType
	 * @param paramsStr
	 */
	public void msgNotify(String phone, MsgParam paramType, String paramsStr, String wishplanOid) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		String type = paramType.toString();
		try {
			orep = this.sMSUtils.sendSMS(phone, type, JSON.parseObject(paramsStr, String[].class));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error(paramType.getInnerFaceName(), e);
			setIrepException(irep, e);
		}

		if (IS_WRITE_LOG) {
			writeLog(irep, paramsStr, paramType.getInnerFaceName(), wishplanOid);
		}
	}
	
	/** 通过子账户的手机号来获取主账户的手机号和子账户的昵称 
	 * 
	 * param：子账户的手机号
	 * return:主账户的手机号和子账户的昵称
	 * 
	 * */
	public PhoneAndNickName getByPhone(String phoneNum){
		PhoneAndNickName rep = new PhoneAndNickName();
		InvestorBaseAccountEntity sonAccount =  this.investorBaseAccountDao.findByPhoneNum(phoneNum);
		SonAccountEntity sonEntity =  this.sonAccountDao.findBySid(sonAccount.getOid());
		InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOid(sonEntity.getPid());
		rep.setPhone(baseAccount.getPhoneNum());
		
		rep.setNickName(sonEntity.getNickname());
		return rep;
	}
	/**
	 * 通过手机号判断是否为主子账户
	 * */
	public boolean judgeByPhone(String phoneNum){
		InvestorBaseAccountEntity baseAccount =  this.investorBaseAccountDao.findByPhoneNum(phoneNum);
		if(baseAccount.getPhoneNum().length()==11){
			return true;
		}return false;
		
	}
	
}
