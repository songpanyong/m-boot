package com.guohuai.mmp.platform.msgment;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.api.cms.CmsApi;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.sonaccount.SonAccountDao;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;
import com.guohuai.mmp.jiajiacai.common.service.ThirdNotifyService;
import com.guohuai.mmp.platform.msgment.log.MsgLogReq;
import com.guohuai.mmp.platform.msgment.log.MsgLogService;

import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@Slf4j
public class MailService {
	
	@Autowired
	private MsgLogService msgLogService;
	@Autowired
	private MsgUtil msgUtil;
	@Autowired
	private CmsApi cmsApi;
	@Autowired
	private ThirdNotifyService thirdNotifyService;
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private SonAccountDao sonAccountDao;
	
	static final boolean IS_WRITE_LOG = true;
	/**
	 * 充值成功
	 * 充值成功提醒	rechargesuccess	["金额"]	充值成功提醒	恭喜您成功充值{1}元！
	 */
	public void rechargesuccess(RechargeSuccessMailReq ireq) {
		Boolean flag = this.thirdNotifyService.judgeByOid(ireq.getUserOid());
		if(flag){
			this.rechargesuccess(ireq, true);
		}else{
			this.sonRechargesuccess(ireq, true);
		}
		
	}
	/**主账户充值成功
	 * 
	 * */
	public void rechargesuccess(RechargeSuccessMailReq ireq, boolean isLog) {
		BaseResp rep = new BaseResp();
		try {
			
			rep = this.cmsApi.sendMail(ireq.getUserOid(), MsgParam.mailRechargeSuccess.toString(), msgUtil.assembleMsgParams(ireq.getOrderAmount()));
		} catch (Exception e) {
			log.error("mailRechargeSuccess interface exception", e);
		}
		if (isLog) {
			writeLog(rep, ireq, MsgParam.mailRechargeSuccess.getInnerFaceName());
		}
	}
	
	/**子账户充值成功
	 * 恭喜您子账号{1}成功充值{2}元！请前往子账号个人中心查看详情。
	 * */
	public void sonRechargesuccess(RechargeSuccessMailReq ireq, boolean isLog) {
		BaseResp rep = new BaseResp();
		IdAndNicknameReq req = gueByOid(ireq.getUserOid());
		AmountAndIdAndNickName ain = new AmountAndIdAndNickName();
		ain.setUserOid(req.getUserOid());
		ain.setNickname(req.getNickname());
		ain.setOrderAmount(ireq.getOrderAmount());
		
		try {
			
			rep = this.cmsApi.sendMail(ain.getUserOid(), MsgParam.mailSonRechargeSuccess.toString(), msgUtil.assembleMsgParams(ain.getNickname(),ireq.getOrderAmount()));
		} catch (Exception e) {
			log.error("mailSonRechargeSuccess interface exception", e);
		}
		if (isLog) {
			writeLog(rep, ain, MsgParam.mailSonRechargeSuccess.getInnerFaceName());
		}
	}
	
	/**
	 * 投资成功
	 * 投资成功	buysuccess	["产品名称"]		【家加财】恭喜您成功投资{1}理财产品，请您耐心等待产品成立。
	 */
	public void buysuccess(BuySuccessMailReq ireq) {
		
		Boolean flag = this.thirdNotifyService.judgeByOid(ireq.getUserOid());
		if(flag){
			this.buysuccess(ireq, true);
		}else{
			this.sonBuysuccess(ireq, true);
		}
		
	}
	/**
	 * 主账户投资成功
	 * */
	public void buysuccess(BuySuccessMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		String type = MsgParam.mailBuySuccess.toString();
		
		try {
			
			orep = this.cmsApi.sendMail(ireq.getUserOid(), type, msgUtil.assembleMsgParams(ireq.getProductName()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailBuySuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.mailBuySuccess.getInnerFaceName());
		}
	}
	
	/**
	 * 子账户购买成功
	 * "恭喜您为子账户{1}成功投资{2}理财产品，系统会在1-2个工作日确认，如有问题请致电客服热线。
	 * */
	public void sonBuysuccess(BuySuccessMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		
		IdAndNicknameReq req = gueByOid(ireq.getUserOid());
		IdAndNicknameAndProduct inp = new IdAndNicknameAndProduct();
		inp.setUserOid(req.getUserOid());
		inp.setNickname(req.getNickname());
		inp.setProductName(ireq.getProductName());
		
		
		
		String type = MsgParam.mailSonBuySuccess.toString();
		
		try {
			
			orep = this.cmsApi.sendMail(inp.getUserOid(), type, msgUtil.assembleMsgParams(inp.getNickname(),ireq.getProductName()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailSonBuySuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, inp, MsgParam.mailSonBuySuccess.getInnerFaceName());
		}
	}
	
	/**
	 * 流标
	 * 流标	abortive	["产品名称"，"客服电话"]		【家加财】您投资的{1}理财产品发生流标，如有疑问请联系客服{2}。
	 */
	public void abortive(AbortiveMailReq ireq) {
		//判断主子账户
		Boolean flag = this.thirdNotifyService.judgeByOid(ireq.getUserOid());
		if(flag){
			this.abortive(ireq, true);
		}else{
			this.sonAbortive(ireq,true);
		}
		    
	}
	/**子账户流标
	 * 您的子账户{1}投资的{2}理财产品发生流标，如有疑问请联系客服{3}。
	 * 
	 * */
	public void sonAbortive(AbortiveMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();	
		//获取主账户的id和子账户的昵称
		IdAndNicknameReq req = gueByOid(ireq.getUserOid());
		IdAndNicknameAndProduct inp = new IdAndNicknameAndProduct();
		inp.setUserOid(req.getUserOid());
		inp.setNickname(req.getNickname());
		inp.setProductName(ireq.getProductName());
		inp.setHotLine(ireq.getHotLine());
		
		try {
			
			orep = this.cmsApi.sendMail(req.getUserOid(), MsgParam.mailSonAbortive.toString(), msgUtil.assembleMsgParams(req.getNickname(),ireq.getProductName(),ireq.getHotLine()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailSonAbortive interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, inp, MsgParam.mailAbortive.getInnerFaceName());
		}
	}
	/**
	 * 主账户流标
	 * */
	
	public void abortive(AbortiveMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();	
		try {
			
			orep = this.cmsApi.sendMail(ireq.getUserOid(), MsgParam.mailAbortive.toString(), msgUtil.assembleMsgParams(ireq.getProductName(), ireq.getHotLine()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailAbortive interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.mailSonAbortive.getInnerFaceName());
		}
	}
	
	/**
	 * 提现申请
	 * 提现申请	withdrawapply	
	 */
	public void withdrawapply(WithdrawApplyMailReq ireq) {

		Boolean flag = this.thirdNotifyService.judgeByOid(ireq.getUserOid());
		if(flag){
			this.withdrawapply(ireq, true);
		}else{
			this.sonWithdrawapply(ireq, true);
		}
		
	}
	/**
	 * 主账户提现申请
	 * 【家加财】您于{1}提交提现申请，提现金额{2}元，手续费{3}元，预计到账金额{4}元，我们会在1个工作日之内处理。
	 * 
	 * */
	public void withdrawapply(WithdrawApplyMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			
			orep = this.cmsApi.sendMail(ireq.getUserOid(), MsgParam.mailWithdrawApply.toString(), msgUtil.assembleMsgParams(ireq.getOrderTime(), ireq.getOrderAmount(),ireq.getFee(),ireq.getPreAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailWithdrawApply interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.mailWithdrawApply.getInnerFaceName());
		}
	}
	/**
	 * 子账户提现申请
	 * 您子账号{1}于{2}申请的{3}元提现已受理，我们会在1个工作日之内处理。
	 * */
	public void sonWithdrawapply(WithdrawApplyMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		
		IdAndNicknameReq req = gueByOid(ireq.getUserOid());
		AmountAndIdAndNickNameAndTime aint = new AmountAndIdAndNickNameAndTime();
		aint.setUserOid(req.getUserOid());
		aint.setNickname(req.getNickname());
		aint.setOrderAmount(ireq.getOrderAmount());
		aint.setTime(ireq.getOrderTime());
		aint.setFee(ireq.getFee());
		aint.setPreAmount(ireq.getPreAmount());
		
		try {
			
			orep = this.cmsApi.sendMail(aint.getUserOid(), MsgParam.mailSonWithdrawApply.toString(), msgUtil.assembleMsgParams(aint.getNickname(),aint.getTime(),aint.getOrderAmount(),aint.getFee(),aint.getPreAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailSonWithdrawApply interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, aint, MsgParam.mailSonWithdrawApply.getInnerFaceName());
		}
	}
	
	/**
	 * 提现到账
	 * 提现到账	withdrawsuccess	["时间","金额"]		【家加财】您于{1}申请的{2}元提现已转入您指定的银行帐号，具体到账时间请参照各银行规定。
	 */
	public void withdrawsuccess(WithdrawSuccessMailReq ireq) {

		//Boolean flag = this.thirdNotifyService.judgeByOid(ireq.getUserOid());
		InvestorBaseAccountEntity invest =  this.investorBaseAccountDao.findByOid(ireq.getUserOid());
		if(invest!=null&&invest.getPhoneNum()!=null){
			boolean flag = invest.getPhoneNum().length()==11;
			if(flag){
				this.withdrawsuccess(ireq, true);
			}else{
				this.sonWithdrawsuccess(ireq, true);
			}
			
		}
		
		
	}
	/**
	 * 主账户提现到账
	 * */
	public void withdrawsuccess(WithdrawSuccessMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			
			orep = this.cmsApi.sendMail(ireq.getUserOid(), MsgParam.mailWithdrawSuccess.toString(), msgUtil.assembleMsgParams(ireq.getCompleteTime(),ireq.getRealAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailWithdrawSuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.mailWithdrawSuccess.getInnerFaceName());
		}
	}
	/**
	 * 子账户提现到账
	 * 
	 * 您子账号{1}于{2}申请的{3}元提现已转入您指定的银行帐号，具体到账时间请参照各银行规定。
	 * */
	public void sonWithdrawsuccess(WithdrawSuccessMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		IdAndNicknameReq req = gueByOid(ireq.getUserOid());
		AmountAndNicknameAndRealAmount aint = new AmountAndNicknameAndRealAmount();
		aint.setUserOid(req.getUserOid());
		aint.setNickname(req.getNickname());
		aint.setTime(ireq.getCompleteTime());
		aint.setRealAmount(ireq.getRealAmount());
		
		try {
			orep = this.cmsApi.sendMail(aint.getUserOid(), MsgParam.mailSonWithdrawSuccess.toString(), msgUtil.assembleMsgParams(aint.getNickname(),aint.getTime(),aint.getRealAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailSonWithdrawSuccess interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, aint, MsgParam.mailSonWithdrawSuccess.getInnerFaceName());
		}
	}
	
	/**
	 * 回款
	 * 回款	receivedpayments	["产品名称","金额"]		【家加财】您投资的{1}理财产品本次回款{2}元，如需详情请查看资金记录。
	 */
	public void receivedpayments(ReceivedPaymentsMailReq ireq) {

		Boolean flag = this.thirdNotifyService.judgeByOid(ireq.getUserOid());
		if(flag){
			this.receivedpayments(ireq, true);
		}else{
			this.sonReceivedpayments(ireq, true);
		}
		
	}
	
	public void receivedpayments(ReceivedPaymentsMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			
			orep = this.cmsApi.sendMail(ireq.getUserOid(), MsgParam.mailReceivedPayments.toString(), msgUtil.assembleMsgParams(ireq.getProductName(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailReceivedPayments interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.mailReceivedPayments.getInnerFaceName());
		}
	}
	/**
	 * 子账户回款
	 * 您子账号{1}投资的{2}本次回款{3}元，如需详情请查看资金记录。
	 * */
	public void sonReceivedpayments(ReceivedPaymentsMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		
		IdAndNicknameReq req = gueByOid(ireq.getUserOid());
		AmountAndIdAndNickNameAndProduct ainp = new AmountAndIdAndNickNameAndProduct();
		ainp.setUserOid(req.getUserOid());
		ainp.setNickname(req.getNickname());
		ainp.setOrderAmount(ireq.getOrderAmount());
		ainp.setProductName(ireq.getProductName());
		
		try {
			
			orep = this.cmsApi.sendMail(ainp.getUserOid(), MsgParam.mailSonReceivedpayments.toString(), msgUtil.assembleMsgParams(ainp.getNickname(),ireq.getProductName(), ireq.getOrderAmount()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailSonReceivedPayments interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ainp, MsgParam.mailSonReceivedpayments.getInnerFaceName());
		}
	}
	
	/**
	 * 产品成立进入存续期	计息提醒	interest/soninterest	["产品名称"]	计息提醒	您投资的{1}理财产品开始计息！/您的子账户{1}投资的{2}开始计息。
	 */
	public void interest(InterestMailReq ireq) {
		/**判断主、子账户的逻辑  */
		Boolean flag = this.thirdNotifyService.judgeByOid(ireq.getUserOid());
		if(flag){
			this.interest(ireq, true);
		}else{
			this.sonInterest(ireq, true);
		}
		
	}
	/**主账户计息提醒*/
	public void interest(InterestMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			
			orep = this.cmsApi.sendMail(ireq.getUserOid(), MsgParam.mailInterest.toString(), msgUtil.assembleMsgParams(ireq.getProductName()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailInterest interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.mailInterest.getInnerFaceName());
		}
	}
	
	/**子账户计息提醒*/
	public void sonInterest(InterestMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		
		IdAndNicknameReq req = gueByOid(ireq.getUserOid());
		IdAndNicknameAndProduct inp = new IdAndNicknameAndProduct();
		inp.setUserOid(req.getUserOid());
		inp.setNickname(req.getNickname());
		inp.setProductName(ireq.getProductName());
		
		try {
			
			orep = this.cmsApi.sendMail(inp.getUserOid(), MsgParam.mailSonInterest.toString(), msgUtil.assembleMsgParams(inp.getNickname(),ireq.getProductName()));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailSonInterest interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, inp, MsgParam.mailSonInterest.getInnerFaceName());
		}
	}
	
	/**
	 * 提前还款提醒	prepayment	["产品名称","金额","金额","金额","金额"]	提前还款提醒	您投资的{1}发生提前还款,总额{2}元,其中提前还款本金{3}元,提前还款利息{4}元,提前还款补偿金{5}元，本次投资已回款结束。
	 */
	public void prepayment(InterestMailReq ireq) {
		this.interest(ireq, true);
	}
	
	public void prepayment(InterestMailReq ireq, boolean isLog) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		try {
			
			orep = this.cmsApi.sendMail(ireq.getUserOid(), MsgParam.mailPrepayment.toString(), msgUtil.assembleMsgParam(ireq));
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error("mailPrepayment interface exception", e);
			setIrepException(irep, e);
		}
		if (isLog) {
			writeLog(irep, ireq, MsgParam.mailPrepayment.getInnerFaceName());
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

	private <T> void writeLog(BaseResp irep, T ireq, String interfaceName) {
		MsgLogReq req = new MsgLogReq();
		
		req.setInterfaceName(interfaceName);
		req.setSendedTimes(1);
		req.setSendObj(JSONObject.toJSONString(ireq));
		req.setErrorCode(irep.getErrorCode());
		req.setErrorMessage(irep.getErrorMessage());
		msgLogService.createEntity(req);
	}
	
	/**
	 * mailNotify
	 * 
	 * @param userOid
	 * @param paramType
	 * @param paramsStr
	 */
	public void mailNotify(String userOid, MsgParam paramType, String paramsStr, String wishplanOid) {
		BaseResp orep = new BaseResp();
		BaseResp irep = new BaseResp();
		String type = paramType.toString();
		try {
			orep = this.cmsApi.sendMail(userOid, type, paramsStr);
			setIrep(orep, irep);
		} catch (Exception e) {
			log.error(paramType.getInnerFaceName(), e);
			setIrepException(irep, e);
		}
//		if (IS_WRITE_LOG) {
//			writeLog(irep, paramsStr, paramType.getInnerFaceName());
//		}
	}
	
	/** 通过子账户的id来获取主账户的id、昵称 
	 * 
	 * 参数：子用户id
	 * 返回：主账户的id、子账户的昵称
	 * */
	
	public IdAndNicknameReq gueByOid(String investorOid){
		//获取主账户的id和子账户的昵称
				IdAndNicknameReq req = new IdAndNicknameReq();
				SonAccountEntity sonAccount =  this.sonAccountDao.findBySid(investorOid);
				req.setUserOid(sonAccount.getPid());
				req.setNickname(sonAccount.getNickname());
				return req;
	}
	
	
}
