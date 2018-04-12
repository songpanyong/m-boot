package com.guohuai.mmp.investor.baseaccount;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.exception.GHException;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.Digests;
import com.guohuai.component.util.PwdUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.baseaccount.refer.details.InvestoRefErDetailsEntity;
import com.guohuai.mmp.investor.baseaccount.refer.details.InvestoRefErDetailsService;
import com.guohuai.mmp.investor.baseaccount.referee.InvestorRefEreeEntity;
import com.guohuai.mmp.investor.baseaccount.referee.InvestorRefEreeService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsEntity;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.CreateUserReq;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.sms.SMSTypeEnum;
import com.guohuai.mmp.sms.SMSUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvestorBaseAccountTwoService {
	
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private InvestorRefEreeService investorRefEreeService;
	@Autowired
	private InvestoRefErDetailsService investoRefErDetailsService;
	@Autowired
	private TulipService tulipNewService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private Accment accmentService;
	@Autowired
	private SMSUtils sMSUtils;
	@Autowired
	private EbaoquanRecordService baoquanService;
	/**
	 * 注册(新)
	 * @param req
	 * @param isSmsCode 是否校验短信验证码 true:校验,false:不校验
	 * @return
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorBaseAccountEntity addBaseAccount(InvestorBaseAccountAddReq req, boolean isSmsCode) {		
		if (this.investorBaseAccountService.isPhoneExists(req.getUserAcc())) {
			throw new AMPException("该手机号已经注册过，无法再次注册!");
		}
		
		InvestorBaseAccountEntity recommender = null;
		if (req.getSceneId() != null && !req.getSceneId().isEmpty()){
			recommender = investorBaseAccountService.checkRecommend(req.getSceneId());
		}
		// 是否校验短信验证码
		if (isSmsCode) {
			this.sMSUtils.checkVeriCode(req.getUserAcc(), SMSTypeEnum.smstypeEnum.regist.toString(), req.getVericode());
		}
		
		InvestorBaseAccountEntity account = investorBaseAccountService.createEntity();
		try {
			String userOid = StringUtil.uuid();
			account.setOid(userOid);
			account.setMemberId(account.getOid());
			account.setPhoneNum(req.getUserAcc());
			if (!StringUtil.isEmpty(req.getUserPwd())) {
				account.setSalt(Digests.genSalt());
				account.setUserPwd(PwdUtil.encryptPassword(req.getUserPwd(), account.getSalt()));
			}
			/**  若为子账户，则设置子账户的交易密码，req带过来的 是主账户的交易密码 */
			account.setPayPwd(req.getPaypwd());
			account.setPaySalt(req.getPaySalt());
			account.setChannelid(req.getChannelid());
			account = this.investorBaseAccountService.saveEntity(account);
		
			// 投资人-统计
			InvestorStatisticsEntity en = new InvestorStatisticsEntity();
			en.setInvestorBaseAccount(account);
			this.investorStatisticsService.saveEntity(en);
	
			// 资金用户-推荐人统计
			InvestorRefEreeEntity investorRefEreeEntity = new InvestorRefEreeEntity();
			investorRefEreeEntity.setInvestorBaseAccount(account);
			this.investorRefEreeService.saveEntity(investorRefEreeEntity);				
	
			// 如果推荐人存在，则创建资金用户-推荐人明细
			if (null != recommender) {
				// 获取推荐人统计信息
				InvestorRefEreeEntity investorRefEree = this.investorRefEreeService
						.getInvestorRefEreeByAccount(recommender);
				InvestoRefErDetailsEntity refErDetails = new InvestoRefErDetailsEntity();
				refErDetails.setInvestorRefEree(investorRefEree);
				refErDetails.setInvestorBaseAccount(account);
				// 创建资金用户-推荐人明细
				this.investoRefErDetailsService.saveEntity(refErDetails);
	
				// 更新推荐人注册人数
				this.investorRefEreeService.updateRegister(investorRefEree.getOid());
			} 
			
			// 记录用户信息到redis，主要是登录信息/个推ID
			this.investorBaseAccountService.saveAccountRedis(account.getOid(), req.getClientId());
			
			
			
			// 投资人注册-增加平台注册人数
//			this.platformStatisticsService.increaseRegisterAmount();
	
			// (注册事件)推广平台注册事件
			this.tulipNewService.onRegister(account, recommender);
			
			
			CreateUserReq ireq = new CreateUserReq();
			ireq.setSystemUid(account.getOid());
			ireq.setPhone(account.getPhoneNum());
			ireq.setUserType(AccParam.UserType.INVESTOR.toString());
			ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
			ireq.setRemark("创建投资人:" + account.getOid());
			accmentService.addUser(ireq);
			
			
			
			req.setInvestorOid(account.getOid());
			
			//ebaoquan
			baoquanService.eBaoquanRecord(EbaoquanRecord.EBAOQUAN_TYPE_REGIST, account);
		} catch (Exception e) {
			
			Throwable cause = e.getCause();
		    if(cause instanceof ConstraintViolationException) {
		        String errMsg = ((ConstraintViolationException) cause).getSQLException().getMessage();
		        if(!StringUtil.isEmpty(errMsg) && errMsg.indexOf("phoneNum") != -1) {
					throw new AMPException("已经注册成功，请登录！");
		        }
		    } 
		    
		    if (e instanceof AMPException) {
	    		throw new AMPException(((AMPException) e).getMessage());
	    	}
			log.error("用户：{}注册失败，原因：{}", req.getUserAcc(), GHException.getStacktrace(e));
			throw new AMPException("注册失败！");
		}
		return account;
	}
	
}
