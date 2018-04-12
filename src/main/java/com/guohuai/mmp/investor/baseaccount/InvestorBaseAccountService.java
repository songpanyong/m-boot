package com.guohuai.mmp.investor.baseaccount;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.switchcraft.white.SwitchWhiteEntity;
import com.guohuai.ams.switchcraft.white.SwitchWhiteService;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.proactive.ProActive.Execution;
import com.guohuai.basic.component.proactive.ProActiveAware;
import com.guohuai.basic.config.TerminalConfig;
import com.guohuai.cache.CacheKeyConstants;
import com.guohuai.cache.entity.InvestorBaseAccountCacheEntity;
import com.guohuai.cache.service.CacheInvestorService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.Collections3;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.Digests;
import com.guohuai.component.util.HashRedisUtil;
import com.guohuai.component.util.PwdUtil;
import com.guohuai.component.util.StrRedisUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.ext.investorBaseAccount.InvestorQueryDataExt;
import com.guohuai.mmp.investor.bank.BankEntity;
import com.guohuai.mmp.investor.bank.BankService;
import com.guohuai.mmp.investor.bank.bankhis.BankHisEntity;
import com.guohuai.mmp.investor.bank.bankhis.BankHisService;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountQueryRep.InvestorBaseAccountQueryRepBuilder;
import com.guohuai.mmp.investor.baseaccount.log.CouponLogEntity;
import com.guohuai.mmp.investor.baseaccount.log.CouponLogReq;
import com.guohuai.mmp.investor.baseaccount.log.CouponLogService;
import com.guohuai.mmp.investor.baseaccount.refer.details.InvestoRefErDetailsService;
import com.guohuai.mmp.investor.baseaccount.referee.InvestorRefEreeEntity;
import com.guohuai.mmp.investor.baseaccount.referee.InvestorRefEreeService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsEntity;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.sonaccount.SonAccountDao;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;
import com.guohuai.mmp.investor.sonaccount.SonAccountInfoRep;
import com.guohuai.mmp.investor.sonaccount.SonAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.TradeOrderReq;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProfitListForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanBaseService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanTradeOrderService;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.UserBalanceRep;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;
import com.guohuai.mmp.platform.tulip.TulipConstants;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.sms.SMSTypeEnum;
import com.guohuai.mmp.sms.SMSUtils;
import com.guohuai.tulip.platform.facade.obj.MyCouponRep;

@Service
@Transactional
public class InvestorBaseAccountService {
	
	private Logger logger = LoggerFactory.getLogger(InvestorBaseAccountService.class);
	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	
	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	@Autowired
	private InvestorRefEreeService investorRefEreeService;
	@Autowired
	private InvestoRefErDetailsService investoRefErDetailsService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private ProductService productService;
	@Autowired
	private InvestorBaseAccountTwoService investorBaseAccountTwoService;
	@Autowired
	private CouponLogService couponLogService;
	@Autowired
	private CacheInvestorService cacheInvestorService;
	@Autowired
	private Accment accmentService;
	@Autowired
	private SMSUtils sMSUtils;
	@Autowired
	private BankService bankService;
	@Autowired
	private BankHisService bankHisService;
	@Autowired
	private TerminalConfig terminalConfig;
	@Autowired
	private ProActiveAware proActiveAware;
	@Autowired
	private SonAccountDao sonAccountDao;
	@Autowired
	private InvestorBankOrderDao investorBankOrderDao;
	@Autowired
	private PlanBaseService planBaseService;	
	@Autowired
	private PlanInvestDao planInvestDao;
	@Autowired
	private PlanMonthDao planMonthDao;
	
	@Autowired
	private PlanTradeOrderService tradeOrderService;
	@Autowired
	private SonAccountService sonAccountService;
	@Autowired
	private SwitchWhiteService switchWhiteService;
	
	public List<InvestorBaseAccountEntity> findAll(Specification<InvestorBaseAccountEntity> spec){
		List<InvestorBaseAccountEntity> users = this.investorBaseAccountDao.findAll(spec);	
		return users;
	}
	
	public Page<InvestorBaseAccountEntity> findPage(Specification<InvestorBaseAccountEntity> spec, Pageable pageable){
		Page<InvestorBaseAccountEntity> page = this.investorBaseAccountDao.findAll(spec, pageable);	
		return page;
	}
	
	/**
	 * 更新用户手机号，仅用于测试人员使用
	 * @param phoneNum 手机号
	 */
	public void changeAccPhoneNum(String phoneNum) {
		InvestorBaseAccountEntity account = this.investorBaseAccountDao.findByPhoneNum(phoneNum);
		if (null != account) {
			account.setPhoneNum(StringUtil.uuid());
			this.updateEntity(account);
		} else {
			//error.define[80000]=账户不存在!(CODE:80000)
			throw new AMPException(80000);
		}
	}
	
	@Transactional
	public PageResp<InvestorBaseAccountQueryRep> accountQuery(Specification<InvestorBaseAccountEntity> spec, Pageable pageable) {
		// 添加添加标的的扩展业务调用

		Page<InvestorBaseAccountEntity> accounts = this.investorBaseAccountDao.findAll(spec, pageable);
		PageResp<InvestorBaseAccountQueryRep> pagesRep = new PageResp<InvestorBaseAccountQueryRep>();		
		
		for (InvestorBaseAccountEntity en : accounts) {
			InvestorStatisticsEntity st = investorStatisticsService.findByInvestorBaseAccount(en);
			PlanProfitListForm genericForm = planBaseService.satisticsPlanList(en.getOid());

			InvestorBaseAccountQueryRep rep = new InvestorBaseAccountQueryRepBuilder().investorOid(en.getOid())
					.phoneNum(this.kickstar ? StringUtil.kickstarOnPhoneNum(en.getPhoneNum()) : en.getPhoneNum())
					.realName(this.kickstar ? StringUtil.kickstarOnRealname(en.getRealName()) : en.getRealName())
					.status(en.getStatus()).statusDisp(statusEn2Ch(en.getStatus())).owner(en.getOwner())
					.ownerDisp(ownerEn2Ch(en.getOwner())).balance(en.getBalance())
					.totalInvestAmount(st.getTotalInvestAmount().add(new BigDecimal(genericForm.getTotalDepositAmount())))//增加心愿计划的投资金额
					.totalIncomeAmount(st.getTotalIncomeAmount().add(st.getWishplanIncome())) //增加心愿 计划的收益
					.createTime(en.getCreateTime()).build();
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(accounts.getTotalElements());
		
		if( this.proActiveAware.achieved(InvestorQueryDataExt.class)) {
			this.proActiveAware.invoke(new Execution<InvestorQueryDataExt, PageResp<InvestorBaseAccountQueryRep>>() {
				@Override
				public PageResp<InvestorBaseAccountQueryRep> execute(InvestorQueryDataExt arg0) {
					return arg0.investorBaseAccount(pagesRep);
				}
			}, InvestorQueryDataExt.class);
		}
		
		
		return pagesRep;
		
	}
	
	private String statusEn2Ch(String status) {
		if (InvestorBaseAccountEntity.BASEACCOUNT_status_normal.equals(status)) {
			return "正常";
		}
		if (InvestorBaseAccountEntity.BASEACCOUNT_status_forbidden.equals(status)) {
			return "禁用";
		}
		return status;
	}
	
	private String ownerEn2Ch(String owner) {
		if (InvestorBaseAccountEntity.BASEACCOUNT_owner_investor.equals(owner)) {
			return "投资者 ";
		}
		if (InvestorBaseAccountEntity.BASEACCOUNT_owner_platform.equals(owner)) {
			return "平台";
		}
		return owner;
	}
	
//	public InvestorBaseAccountEntity findByUid(String uid) {
//		InvestorBaseAccountEntity baseAccount = investorBaseAccountDao.findByUserOid(uid);
//		if (null == baseAccount) {
//			//error.define[80000]=账户不存在!(CODE:80000)
//			throw new AMPException(80000);
//		}
//		return baseAccount;
//	}
	
	public InvestorBaseAccountEntity findOne(String oid) {
		InvestorBaseAccountEntity baseAccount = investorBaseAccountDao.findOne(oid);
		if (null == baseAccount) {
			//error.define[80000]=账户不存在!(CODE:80000)
			throw new AMPException(80000);
		}
		return baseAccount;
	}

	
	public InvestorBaseAccountEntity findByMemberId(String mid) {
		InvestorBaseAccountEntity baseAccount = investorBaseAccountDao.findByMemberId(mid);
		if (null == baseAccount) {
			//error.define[80000]=账户不存在!(CODE:80000)
			throw new AMPException(80000);
		}
		return baseAccount;
	}

	public InvestorBaseAccountEntity findByPhoneNum(String phoneNum) {
		InvestorBaseAccountEntity baseAccount = investorBaseAccountDao.findByPhoneNum(phoneNum);
		if (null == baseAccount) {
			//error.define[80000]=账户不存在!(CODE:80000)
			//throw new AMPException(80000);
			throw new AMPException("该手机号未注册");
		}
		return baseAccount;
	}
	
	// 推荐邀请码
	public InvestorBaseAccountEntity findByRecommendId(String uid) {		
		return investorBaseAccountDao.findByUid(uid);
	} 
	
	/**
	 * 校验邀请码
	 * @param sceneId
	 */
	public InvestorBaseAccountEntity checkRecommend(String sceneId) {
		// 资金用户-推荐人
		InvestorBaseAccountEntity recommender = this.findByRecommendId(sceneId);
		
		if (null == recommender) {
			throw AMPException.getException("邀请码不存在！");
		}
		return recommender;
	}

	/**
	 * PC端
	 * @param uid
	 * @return
	 */
	public BaseAccountRep userInfoPc(String userOid) {
		
		return userInfo(userOid, false);
	}
	
	public void isPhoneNumExists(String phoneNum) {
		InvestorBaseAccountEntity account = this.investorBaseAccountDao.findByPhoneNum(phoneNum);
		if (null == account) {
			throw new AMPException("用户未注册，请先注册！");
		}
	}
	
	/**
	 * 判断手机号是否已注册
	 * @param phoneNum
	 * @return
	 */
	public boolean isPhoneExists(String phoneNum) {
		InvestorBaseAccountEntity account = this.investorBaseAccountDao.findByPhoneNum(phoneNum);
		if (null == account) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断手机号是否已经注册
	 * @param phoneNum
	 * @return
	 */
	public InvestorBaseAccountIsRegistRep isRegist(String phoneNum) {
		InvestorBaseAccountIsRegistRep rep = new InvestorBaseAccountIsRegistRep();
		//暂时加入白名单功能，只有白名单的用户才能进行注册
		SwitchWhiteEntity whiteEntity = this.switchWhiteService.findBySwitchOidAndUserAcc("4", phoneNum);
		if(whiteEntity == null){
			rep.setAuthority(false);
		}else{
			rep.setAuthority(true);
		}
		
		InvestorBaseAccountEntity account = this.investorBaseAccountDao.findByPhoneNum(phoneNum);
		if (null != account) {
			rep.setRegist(true);
			rep.setPhoneNum(account.getPhoneNum());
			rep.setInvestorOid(account.getOid());
		} else {
			rep.setRegist(false);
		}		
		return rep;
	}
	
	public InvestorBaseAccountEntity findByPhone(String phoneNum) {
		return this.investorBaseAccountDao.findByPhoneNum(phoneNum);
	}
	
	/**
	 * APP端
	 * @param investorOid
	 * @param isClient 是后台还是客户端
	 * @return
	 */
	/*public BaseAccountRep userInfo(String investorOid, boolean isClient) {
		
		InvestorBaseAccountEntity baseAccount = this.findOne(investorOid);
		//InvestorStatisticsEntity是投资人统计账户的基本信息。
		InvestorStatisticsEntity sta = investorStatisticsService.findByInvestorBaseAccount(baseAccount);
		BaseAccountRep rep = new BaseAccountRep();
		rep.setMemberId(baseAccount.getMemberId());
		// 客户端显示全手机号
		if (isClient) {
			rep.setPhone(baseAccount.getPhoneNum());
		}
		
		rep.setPhoneNum(this.kickstar ? StringUtil.kickstarOnPhoneNum(baseAccount.getPhoneNum()) : baseAccount.getPhoneNum());
		rep.setRealName(this.kickstar ? StringUtil.kickstarOnRealname(baseAccount.getRealName()) : baseAccount.getRealName());
		rep.setStatus(baseAccount.getStatus());
		rep.setStatusDisp(statusEn2Ch(baseAccount.getStatus()));
		
		
		InvestorBaseAccountCacheEntity cache = cacheInvestorService.getInvestorByInvestorOid(investorOid);//账户资金的实体
		rep.setBalance(cache.getBalance());
		rep.setWithdrawAvailableBalance(cache.getWithdrawAvailableBalance());
		rep.setApplyAvailableBalance(cache.getApplyAvailableBalance());
		rep.setRechargeFrozenBalance(cache.getRechargeFrozenBalance());
		rep.setWithdrawFrozenBalance(cache.getWithdrawFrozenBalance());
		
		rep.setOwner(baseAccount.getOwner());
		rep.setOwner(ownerEn2Ch(baseAccount.getOwner()));
		rep.setIsFreshman(baseAccount.getIsFreshMan());
		//调用判断是否是新手的方法。
		rep.setIsFreshmanDisp(isFreshmanEn2Ch(baseAccount.getIsFreshMan()));
		rep.setTotalDepositAmount(sta.getTotalDepositAmount());
		rep.setTotalWithdrawAmount(sta.getTotalWithdrawAmount());
		rep.setTotalInvestAmount(sta.getTotalInvestAmount());
		rep.setTotalRedeemAmount(sta.getTotalRedeemAmount());
		rep.setTotalIncomeAmount(sta.getTotalIncomeAmount());
		rep.setTotalRepayLoan(sta.getTotalRepayLoan());
		rep.setT0YesterdayIncome(sta.getT0YesterdayIncome());
		rep.setTnTotalIncome(sta.getTnTotalIncome());
		rep.setT0TotalIncome(sta.getT0TotalIncome());
		rep.setT0CapitalAmount(sta.getT0CapitalAmount());
		rep.setTnCapitalAmount(sta.getTnCapitalAmount());

		rep.setTotalDepositCount(sta.getTotalDepositCount());
		rep.setTotalWithdrawCount(sta.getTotalWithdrawCount());
		rep.setMonthWithdrawCount(sta.getMonthWithdrawCount());
		rep.setTotalInvestCount(sta.getTotalInvestCount());
		rep.setTotalRedeemCount(sta.getTotalRedeemCount());
		
		rep.setTodayDepositCount(sta.getTodayDepositCount());
		rep.setTodayWithdrawCount(sta.getTodayWithdrawCount());
		rep.setTodayInvestCount(sta.getTodayInvestCount());
		rep.setTodayRedeemCount(sta.getTodayRedeemCount());
		
		rep.setTodayDepositAmount(sta.getTodayDepositAmount());
		rep.setTodayWithdrawAmount(sta.getTodayWithdrawAmount());
		rep.setTodayInvestAmount(sta.getTodayInvestAmount());
		rep.setTodayRedeemAmount(sta.getTodayRedeemAmount());
		

		rep.setIncomeConfirmDate(sta.getIncomeConfirmDate());
		rep.setUpdateTime(sta.getUpdateTime());
		rep.setCreateTime(sta.getCreateTime());
		return rep;
	}*/


	public BaseAccountRep userInfo(String investorOid, boolean isClient) {

		InvestorBaseAccountEntity baseAccount = this.findOne(investorOid);
		// InvestorStatisticsEntity是投资人统计账户的基本信息。
		InvestorStatisticsEntity sta = investorStatisticsService.findByInvestorBaseAccount(baseAccount);

		WishplanInfo wishplanInfo = this.getWishPlanInfoByOid(investorOid);
		BaseAccountRep rep = new BaseAccountRep();

		// 心愿计划的总资产和总收益
		PlanProfitListForm planProfit = this.planBaseService.satisticsPlanList(baseAccount.getOid());
		rep.setWishPlanTotalCapitalAmount(new BigDecimal(planProfit.getTotalExpectedAmount()));//心愿计划的会随着用户的心愿计划的结清而减少
		rep.setWishPlanTotalIncomeAmount(sta.getWishplanIncome());//用户心愿计划的总收益不会随着计划的结清而减少

		rep.setMemberId(baseAccount.getMemberId());
		// 客户端显示全手机号
		if (isClient) {
			rep.setPhone(baseAccount.getPhoneNum());
		}

		rep.setPhoneNum(
				this.kickstar ? StringUtil.kickstarOnPhoneNum(baseAccount.getPhoneNum()) : baseAccount.getPhoneNum());
		rep.setRealName(
				this.kickstar ? StringUtil.kickstarOnRealname(baseAccount.getRealName()) : baseAccount.getRealName());
		rep.setStatus(baseAccount.getStatus());
		rep.setStatusDisp(statusEn2Ch(baseAccount.getStatus()));

		// 判断是否为主子账户
		if (baseAccount.getPhoneNum().length() != 11) {
			rep.setIsSonAccount(true);

			List<Object[]> list = this.investorBankOrderDao.queryTransferFromPAccount(baseAccount.getOid());
			if (list != null && list.size() > 0) {
				for (Object[] li : list) {
					// 子账户从主账户中转入金额的累计次数
					rep.setSonTransferCount(((BigInteger) li[0]).intValue());
					// 当前子账户从主账户中转入累计金额
					rep.setTransferFromPAccount((BigDecimal) li[1]);
				}
			}

		} else {
			rep.setIsSonAccount(false);
			// 子账户数量
			rep.setSonAccountCount(this.sonAccountDao.querySonAccountCount(baseAccount.getOid()));
			List<Object[]> list = this.investorBankOrderDao.queryTransferToSonAccount(baseAccount.getOid());
			if (list != null && list.size() > 0) {
				for (Object[] li : list) {
					// 主账户向子账号转入的累计金额
					rep.setTransferToSonAccounts((BigDecimal) li[1]);
					// 主账户向子账号累计转入次数
					rep.setPTransferCount(((BigInteger) li[0]).intValue());
				}

			}
		}

		InvestorBaseAccountCacheEntity cache = cacheInvestorService.getInvestorByInvestorOid(investorOid);// 账户资金的实体
		rep.setBalance(cache.getBalance());
		rep.setWithdrawAvailableBalance(cache.getWithdrawAvailableBalance());
		rep.setApplyAvailableBalance(cache.getApplyAvailableBalance());
		rep.setRechargeFrozenBalance(cache.getRechargeFrozenBalance());
		rep.setWithdrawFrozenBalance(cache.getWithdrawFrozenBalance());

		rep.setOwner(baseAccount.getOwner());
		rep.setOwner(ownerEn2Ch(baseAccount.getOwner()));
		rep.setIsFreshman(baseAccount.getIsFreshMan());
		// 调用判断是否是新手的方法。
		rep.setIsFreshmanDisp(isFreshmanEn2Ch(baseAccount.getIsFreshMan()));
		rep.setTotalDepositAmount(sta.getTotalDepositAmount());
		rep.setTotalWithdrawAmount(sta.getTotalWithdrawAmount());

		rep.setTnTotalIncome(sta.getTnTotalIncome());
		rep.setT0TotalIncome(sta.getT0TotalIncome());
		rep.setT0CapitalAmount(sta.getT0CapitalAmount());
		rep.setTnCapitalAmount(sta.getTnCapitalAmount());
		rep.setT0YesterdayIncome(sta.getT0YesterdayIncome());

		rep.setTotalDepositCount(sta.getTotalDepositCount());
		rep.setTotalWithdrawCount(sta.getTotalWithdrawCount());
		rep.setMonthWithdrawCount(sta.getMonthWithdrawCount());

		rep.setTodayDepositCount(sta.getTodayDepositCount());
		rep.setTodayWithdrawCount(sta.getTodayWithdrawCount());

		rep.setTodayDepositAmount(sta.getTodayDepositAmount());
		rep.setTodayWithdrawAmount(sta.getTodayWithdrawAmount());

		rep.setIncomeConfirmDate(sta.getIncomeConfirmDate());
		rep.setUpdateTime(sta.getUpdateTime());
		rep.setCreateTime(sta.getCreateTime());

		// 活定期产品的投资金额加上心愿计划的投资金额---累计投资金额
		rep.setTotalInvestAmount(sta.getTotalInvestAmount().add(new BigDecimal(planProfit.getTotalholdInvestAmount())));
		// 活定期产品的收益中加入了心愿计划的总收益
		rep.setTotalIncomeAmount(sta.getTotalIncomeAmount().add(rep.getWishPlanTotalIncomeAmount()));
		rep.setTotalRedeemAmount(sta.getTotalRedeemAmount().add(wishplanInfo.getPlanTotalRedeemAmount()));
		rep.setTotalRepayLoan(sta.getTotalRepayLoan().add(wishplanInfo.getPlanTotalRepayLoan()));

		rep.setTotalInvestCount(sta.getTotalInvestCount() + wishplanInfo.getPlanTotalInvestCount());
		rep.setTotalRedeemCount(sta.getTotalRedeemCount() + wishplanInfo.getPlanTotalRedeemCount());

		rep.setTodayInvestCount(sta.getTodayInvestCount() + wishplanInfo.getPlanTodayInvestCount());
		rep.setTodayRedeemCount(sta.getTodayRedeemCount() + wishplanInfo.getPlanTodayRedeemCount());

		rep.setTodayInvestAmount(sta.getTodayInvestAmount().add(wishplanInfo.getPlanTodayInvestAmount()));
		rep.setTodayRedeemAmount(sta.getTodayRedeemAmount().add(wishplanInfo.getPlanTodayRedeemAmount()));

		return rep;
	}
	private String isFreshmanEn2Ch(String isFreshMan) {
		if (InvestorBaseAccountEntity.BASEACCOUNT_isFreshMan_yes.equals(isFreshMan)) {
			return "是新手";
		}
		if (InvestorBaseAccountEntity.BASEACCOUNT_isFreshMan_no.equals(isFreshMan)) {
			return "非新手";
		}
		
		return isFreshMan;
	}

	
	
	
	/**
	 * 提现回调
	 */
	public int update4WithdrawCBBalance(BigDecimal orderAmount, InvestorBaseAccountEntity investorBaseAccount) {
		int i = this.investorBaseAccountDao.update4WithdrawCBBalance(
				investorBaseAccount.getOid(), orderAmount);
		if (i < 1) {
			// error.define[30074]=余额不足(CODE:30074)
			throw new AMPException(30074);
		}
		return i;
	}
	
	/**
	 * 提现
	 */
	public int update4WithdrawBalance(BigDecimal orderAmount, InvestorBaseAccountEntity investorBaseAccount) {
		int i = this.investorBaseAccountDao.update4WithdrawBalance(investorBaseAccount.getOid(), orderAmount);
		if (i < 1) {
			// error.define[30074]=余额不足(CODE:30074)
			throw new AMPException(30074);
		}
		return i;

	}
	
	/**
	 * 冲销单
	 */
	public int update4WriteOffBalance(BigDecimal orderAmount, InvestorBaseAccountEntity investorBaseAccount) {
		int i = this.investorBaseAccountDao.update4WriteOffBalance(investorBaseAccount.getOid(), orderAmount);
		if (i < 1) {
			// error.define[30079]=在途中金额不足(CODE:30079)
			throw new AMPException(30079);
		}
		return i;

	}
	
	
	
	/**
	 * 获取超级投资用户
	 * @return
	 */
	public InvestorBaseAccountEntity getSuperInvestor(){
		InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOwner(InvestorBaseAccountEntity.BASEACCOUNT_owner_platform);
		if (null == baseAccount) {
			throw new AMPException("超级投资用户不存在!");
		}
		return baseAccount;
	}

	public BaseAccountRep supermanInfo() {
		InvestorBaseAccountEntity baseAccount = this.getSuperInvestor();
		if (null == baseAccount) {
			throw new AMPException("投资人-基本账户不存在!");
		}
		return this.userInfo(baseAccount.getOid(), false);
		
	}
	
	/**
	 * 删除账号，只删除注册时候产生的数据（充值，提现不删除，谨慎操作），仅测试使用
	 * @param phoneNum
	 * @return
	 */
	@Transactional
	public BaseResp delAccount(String phoneNum) {
		BaseResp rep = new BaseResp();
		InvestorBaseAccountEntity account = this.investorBaseAccountDao.findByPhoneNum(phoneNum);
		if (null != account) {
			try {
				InvestorRefEreeEntity investorRefEreeEntity = this.investorRefEreeService.getInvestorRefEreeByAccount(account);
				// 推荐明细关系
				this.investoRefErDetailsService.delRefErDetails(account, investorRefEreeEntity);
				// 推荐人记录数据表
				this.investorRefEreeService.delInvestorRefEree(account);
				// 投资人统计表
				this.investorStatisticsService.delInvestorStatistics(account);
				// 删除redis数据
				HashRedisUtil.del(redis, CacheKeyConstants.getInvestorHKey(account.getOid()));
				// 删除投资者数据
				this.investorBaseAccountDao.delete(account);
			} catch (Exception e) {
				logger.error("删除新注册用户失败，用户investorOid：{}，原因：{}", account.getOid(), e.getMessage());
			}
			
		} else {
			throw new AMPException("账号不存在！");
		}
		return rep;
	}
	
	/**
	 * 注册送体验金
	 * @param uid
	 * @return
	 */
	public TradeOrderReq useTastecoupon(String uid) {
		TradeOrderReq tradeOrderReq = null;
		// 查询当前用户未使用的体验金
		PageResp<MyCouponRep> myAllCoupons = this.tulipService.getMyAllCouponList(uid,
				TulipConstants.STATUS_COUPON_NOTUSED, TulipConstants.COUPON_TYPE_TASTECOUPON, 1, 10);
		// 如果用户没有体验金
		if (myAllCoupons.getTotal() <= 0) {
			logger.info("=======================uid:{},体验金未发放,等待下次发放", uid);
			return null;
		}
		List<MyCouponRep> couponList = myAllCoupons.getRows();
		MyCouponRep myCouponRep = couponList.get(0);

		
		Product usedProduct = this.productService.findOnSaleTyjProducts();
		if (usedProduct == null) {
			logger.info("=======================uid:{},体验金产品未创建。", uid);
			return null;
		}

		// 可用
		tradeOrderReq = new TradeOrderReq();
		// 产品ID
		tradeOrderReq.setProductOid(usedProduct.getOid());
		// 申购金额
		tradeOrderReq.setMoneyVolume(myCouponRep.getAmount());
		// 卡券ID
		tradeOrderReq.setCouponId(myCouponRep.getCouponId());
		// 卡券类型
		tradeOrderReq.setCouponType(myCouponRep.getType());
		// 卡券实际抵扣金额
		tradeOrderReq.setCouponDeductibleAmount(myCouponRep.getAmount());
		// 卡券金额
		tradeOrderReq.setCouponAmount(myCouponRep.getAmount());
		// 投资者实付金额
		tradeOrderReq.setPayAmouont(BigDecimal.ZERO);
		// 投资者用户Id
		tradeOrderReq.setUid(uid);
		return tradeOrderReq;
	}
//	/**
//	 * 用户绑卡成功之后推荐人投资产品
//	 * @param uid
//	 * @return
//	 */
//	public BaseResp addBank(String uid, String refereeId) {
//		// 投资人-基本账户
//		InvestorBaseAccountEntity account = investorBaseAccountDao.findByUserOid(uid);
//		InvestorBaseAccountEntity recommender = investorBaseAccountDao.findByUserOid(refereeId);
//		// (注册事件)推广平台注册事件
//		//this.tulipNewService.onReferee(account, recommender);
//		// 使用体验金投资活期产品
//		logger.info("=======================uid:{}", uid);
//
//		CouponLogReq entity = new CouponLogReq();
//		entity.setUserOid(recommender.getUserOid());
//		entity.setType(CouponLogEntity.TYPE_REFEREE);
//		couponLogService.createEntity(entity);
//
//		return new BaseResp();
//	}

	public InvestorBaseAccountEntity createEntity() {
		InvestorBaseAccountEntity account = new InvestorBaseAccountEntity();
		// 邀请码标识
		//account.setUid(StrRedisUtil.incr(redis, StrRedisUtil.USER_SCENEID_REDIS_KEY));
		// 前端注册-投资者
		account.setOwner(InvestorBaseAccountEntity.BASEACCOUNT_owner_investor);
		account.setStatus(InvestorBaseAccountEntity.BASEACCOUNT_status_normal);
		account.setSource(InvestorBaseAccountEntity.BASEACCOUNT_SOURCE_frontEnd);
		account.setIsFreshMan(InvestorBaseAccountEntity.BASEACCOUNT_isFreshMan_yes);
		return account;
	}
	
	public InvestorBaseAccountEntity saveEntity(InvestorBaseAccountEntity entity) {
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}

	public InvestorBaseAccountEntity updateEntity(InvestorBaseAccountEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.investorBaseAccountDao.save(entity);
	}

	
	public void updateAccountRealName(InvestorBaseAccountEntity entity, String name, String idNum) {
		entity.setRealName(name);
		entity.setIdNum(idNum);
		this.updateEntity(entity);
		
		if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(idNum)) {
			List<BankHisEntity> bankHisList = this.bankHisService.findByInvestorOid(entity.getOid());
			if (Collections3.isEmpty(bankHisList)) {
//				this.platformStatisticsService.increaseVerifiedInvestorAmount();
				
				this.tulipService.onSetRealName(entity);
			}
			
		}
	}
	
	/**
	 * 修改注册手机号
	 * @param investorOid
	 * @param oldpn
	 * @param newpn
	 * @return
	 */
	public BaseResp changeAcc(String investorOid, String oldpn, String newpn){
		InvestorBaseAccountEntity entity = this.findOne(investorOid);
		
		// 判断当前用户手机号是否正确
		if (!entity.getPhoneNum().equals(oldpn)) {
			throw AMPException.getException("您输入的原手机号与用户当前使用的手机号不一致！");
		}
		// 新手机号和旧手机号不能一致
		if (newpn.equals(oldpn)) {
			throw GHException.getException("新手机号与原手机号不能一致！");
		}
		// 校验新手机号是否已注册
		if (this.isPhoneExists(newpn)) {
			// error.define[80020]=该手机号已注册(CODE:80020)
			throw GHException.getException(80020);
		}
		
		entity.setPhoneNum(newpn);
		this.updateEntity(entity);
		
		return new BaseResp();
	}
	
	/**
	 * 用户基本信息
	 */
	public InvestorBaseAccountInfoRep getUserInfo(String oid){
		InvestorBaseAccountInfoRep rep = new InvestorBaseAccountInfoRep();
		InvestorBaseAccountEntity account = this.findOne(oid);
		
		rep.setPhoneNum(account.getPhoneNum());
		rep.setRealName(account.getRealName());
		rep.setStatus(account.getStatus());
		rep.setBalance(account.getBalance());
		rep.setCreateTime(account.getCreateTime());
		return rep;
	}
	
	/**
	 * 锁定与解锁用户
	 * @param uoid
	 * @param islock is/not
	 * @return
	 */
	public BaseResp lockUser(String oid, String islock){
		BaseResp rep = new BaseResp();
		InvestorBaseAccountEntity account = this.findOne(oid);
		String status = "";
		// 锁定
		if("is".equals(islock)){
			status = InvestorBaseAccountEntity.BASEACCOUNT_status_forbidden;
		}
		// 解锁
		if("not".equals(islock)){
			status = InvestorBaseAccountEntity.BASEACCOUNT_status_normal;
		}
		account.setStatus(status);
		this.updateEntity(account);
		return rep;
	}

//	public QueryAccountDetailsResp queryAccountDetails(QueryAccountDetailsReq req) {
//		req.setIdentityId(this.investorBaseAccountDao.findByPhoneNum(req.getPhoneNum()).getUserOid());
//		
//		return this.platformBalanceService.queryAccountDetails(req);
//	}

	public int borrowFromPlatform(BigDecimal amount) {
		int i = this.investorBaseAccountDao.borrowFromPlatform(amount);
		if (i < 1) {
			//error.define[30052]=借款失败(CODE:30051)
			throw new AMPException(30052);
		}
		return i;
		
	}

	public int payToPlatform(BigDecimal amount) {
		int i = this.investorBaseAccountDao.payToPlatform(amount);
		if (i < 1) {
			//error.define[30053]=还款失败(CODE:30053)
			throw new AMPException(30053);
		}
		return i;
		
	}

	public List<InvestorBaseAccountEntity> query4Bonus(String lastOid) {
		
		return this.investorBaseAccountDao.query4Bonus(lastOid);
	}


	public List<InvestorBaseAccountEntity> findAll(String lastOid) {
		
		return this.investorBaseAccountDao.findAll(lastOid);
	}

	public List<InvestorBaseAccountEntity> findAll() {
		
		return this.investorBaseAccountDao.findAll();
	}
	
	public List<InvestorBaseAccountEntity> getInvestorsNotSuperaccount() {
		return this.investorBaseAccountDao.getInvestorsNotSuperaccount();
	}
	
	public List<InvestorBaseAccountEntity> getAccByCreateTime(String createTime) {
		return this.investorBaseAccountDao.getAccByCreateTime(createTime);
	}
	
	/**
	 * 更新账户余额
	 */
	public UserBalanceRep updateBalance(InvestorBaseAccountEntity investorBaseAccount) {
		UserBalanceRep irep = accmentService.queryBalance(investorBaseAccount.getMemberId());
		logger.info("updateBalance={}", JSONObject.toJSONString(irep));
		if (0 == irep.getErrorCode()) {
			logger.info("syncUserBalance:investorOid={},memberId={},irep={}", investorBaseAccount.getOid(), 
					investorBaseAccount.getMemberId(), JSONObject.toJSONString(investorBaseAccount));
			this.investorBaseAccountDao.updateBalance(investorBaseAccount.getOid(), 
					irep.getBalance(), irep.getRechargeFrozenBalance(), 
					irep.getWithdrawFrozenBalance(), irep.getWithdrawAvailableBalance(), irep.getApplyAvailableBalance());
		}
		return irep;
	}
	
	/**
	 * 余额是否足够
	 */
	public void balanceEnoughWithdraw(InvestorBaseAccountEntity investorBaseAccount, BigDecimal orderAmount) {
		
		if (investorBaseAccount.getWithdrawAvailableBalance().compareTo(orderAmount) < 0) {
			//error.define[80002]=投资人-基本账户的余额不足!(CODE:80002)
			throw new AMPException(80002);
		}
	}
	
	public void balanceEnoughInvest(InvestorBaseAccountEntity investorBaseAccount, BigDecimal orderAmount) {
		
		if (investorBaseAccount.getApplyAvailableBalance().compareTo(orderAmount) < 0) {
			//error.define[80002]=投资人-基本账户的余额不足!(CODE:80002)
			throw new AMPException(80002);
		}
	}

	/**
	 * 注册(新)
	 * @param req
	 * @param isSmsCode 是否校验短信验证码 true:校验,false:不校验
	 * @return
	 */
	@Transactional
	public BaseResp addBaseAccount(InvestorBaseAccountAddReq req, boolean isSmsCode){
		
		InvestorBaseAccountEntity baseAccount = this.investorBaseAccountTwoService.addBaseAccount(req, isSmsCode);
		
		//使用体验金投资活期产品
		logger.info("=======================uid:" + baseAccount.getOid());
		CouponLogReq entity=new CouponLogReq();
		entity.setUserOid(baseAccount.getOid());
		entity.setType(CouponLogEntity.TYPE_REGISTER);
		couponLogService.createEntity(entity);

		return new BaseResp();
	}
	
	/**
	 * 登录
	 * @param req
	 * @return
	 */
	public String login(InvestorBaseAccountLoginReq req){
		InvestorBaseAccountEntity account = this.findByPhoneNum(req.getUserAcc());
		if (StringUtil.isEmpty(account.getUserPwd()) || StringUtil.isEmpty(account.getSalt())) {
			throw new AMPException("登录密码错误，请重新输入密码");
		}
		//登录前的校验
		InvestorBaseAccountRedisInfo accountRedis = this.saveAccountRedis(account.getOid(), req.getClientId());
		
		
		logger.info("用户：{}使用密码进行登录。", account.getPhoneNum());
		// 获取锁定状态
		this.getLockState(account, accountRedis, true);
		
		if (PwdUtil.checkPassword(req.getUserPwd(), account.getUserPwd(), account.getSalt())) {
			// 更新用户登录错误次数，清零
			accountRedis.setPwdErrorTimes(0);
			this.updateAccountRedis(account.getOid(), accountRedis);
			
			
			/** 将主账户对应的子账户下的markId都清零 */
			List<SonAccountEntity> list = this.clearMarkId(account.getOid());		
			if(list!=null){
				for(SonAccountEntity son:list){
					InvestorBaseAccountEntity baseAccount =  this.investorBaseAccountDao.findByOid(son.getSid());
					if(baseAccount!=null&&baseAccount.getMarkId()!=null){
						baseAccount.setMarkId(null);
						this.investorBaseAccountDao.save(baseAccount);
					}
					
					
				}
			}
			
			
			return account.getOid();
		} else {
			// 错误次数累计
			
			int pwdErrorTimes = accountRedis.getPwdErrorTimes() + 1;
			if(pwdErrorTimes == 5){
				// 设置锁定时间
				accountRedis.setLockTime(DateUtil.getSqlDate());
				
			}
			// 更新用户登录错误次数
			accountRedis.setPwdErrorTimes(pwdErrorTimes);
			this.updateAccountRedis(account.getOid(), accountRedis);
			if(pwdErrorTimes == 5){
				logger.info("用户：{}，密码输入错误：5次，时间：{}", account.getPhoneNum(), DateUtil.getSqlDate());
				throw GHException.getException("密码连续输入错误超过五次，账号今日被锁定");
			}
			logger.info("用户：{}，密码输入错误：{}次，时间：{}", account.getPhoneNum(), pwdErrorTimes, DateUtil.getSqlDate());
			throw new GHException("手机号和密码不匹配，您还有" + (5- pwdErrorTimes) + "次机会");
		}
	}
	
	/**
	 * 快速登录
	 * @param fu
	 * @return
	 */
	public String fastLogin(InvestorBaseAccountLoginReq req) {
		InvestorBaseAccountEntity account = this.findByPhoneNum(req.getUserAcc());
		this.sMSUtils.checkVeriCode(req.getUserAcc(), SMSTypeEnum.smstypeEnum.login.toString(), req.getVericode());
		this.saveAccountRedis(account.getOid(), req.getClientId());
		return account.getOid();
	}
	
	/**
	 * 初始化登录信息 redis
	 * @param accountOid
	 * @param clientId 可为空
	 * @return
	 */
	public InvestorBaseAccountRedisInfo saveAccountRedis(String investorOid, String clientId) {
		InvestorBaseAccountRedisInfo accountRedis = InvestorBaseAccountRedisUtil.get(redis, investorOid);
		
		if (null == accountRedis) {
			accountRedis = new InvestorBaseAccountRedisInfo();
		}
		if (!StringUtil.isEmpty(clientId)) {
			accountRedis.setClientId(clientId);
		}
		accountRedis = InvestorBaseAccountRedisUtil.set(redis, investorOid, accountRedis);
		return accountRedis;
	}
	
	/**
	 * 更新登录信息 redis
	 * @param investorOid
	 * @param accountRedis
	 * @return
	 */
	public InvestorBaseAccountRedisInfo updateAccountRedis(String investorOid, InvestorBaseAccountRedisInfo accountRedis) {
		return InvestorBaseAccountRedisUtil.set(redis, investorOid, accountRedis);
	}
	
	/**
	 * 判断用户的锁定状态
	 * @param userAcc
	 */
	public void checkLockState(String userAcc) {
		InvestorBaseAccountEntity account = this.findByPhoneNum(userAcc);
		this.checkAccount(account);
	}
	
	/**
	 * 校验手机号状态
	 * @param phone
	 */
	public InvestorBaseAccountEntity checkAccount(String phone) {
		InvestorBaseAccountEntity account = this.findByPhone(phone);
		if (null != account) {
			this.checkAccount(account);
		}
		return account;
	}
	
	/**
	 * 校验手机号状态
	 * @param account
	 */
	public void checkAccount(InvestorBaseAccountEntity account) {
		InvestorBaseAccountRedisInfo accountRedis = this.saveAccountRedis(account.getOid(), "");
		// 获取登录状态
		this.getLockState(account, accountRedis, false);
	}
	
	/**
	 * 获取当前用户锁定状态
	 * @param account
	 * @param accountRedis
	 * @param isLogin
	 */
	public void getLockState(InvestorBaseAccountEntity account, InvestorBaseAccountRedisInfo accountRedis, boolean isLogin){
		
//		if (InvestorBaseAccountEntity.BASEACCOUNT_status_forbidden.equals(account.getStatus())) {
//			throw new AMPException("此用户已被冻结!");
//		}
		 
		if (null != accountRedis.getLockTime()) {
			// 不是同一天，更新用户登录错误次数，清零
			if (!DateUtil.same(DateUtil.getSqlDate(), accountRedis.getLockTime())){
				accountRedis.setPwdErrorTimes(0);
				accountRedis.setLockTime(DateUtil.getSqlDate());
			  InvestorBaseAccountRedisUtil.set(redis, account.getOid(), accountRedis);
			}  
		}
		
		if (accountRedis.getPwdErrorTimes() > 4) {
			if (isLogin) {
				throw new AMPException("登录密码错误5次，退出登录状态，账户锁定至当天24点");
			} else {
				throw new AMPException("您的账号已被锁定，不能进行此操作");
			}
		}
		
	}
	
	/**
	 * 校验输入的原登录密码是否正确
	 * @param req
	 * @return
	 */
	public BaseResp checkLoginPassword(InvestorBaseAccountPasswordReq req) {
		InvestorBaseAccountEntity account = this.findOne(req.getInvestorOid());
		BaseResp rep = new BaseResp();
		if (!PwdUtil.checkPassword(req.getUserPwd(), account.getUserPwd(), account.getSalt())) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("原登录密码验证错误！");
		}
		return rep;
	}
	
	/**
	 * 设置/修改登录密码
	 * @param req
	 * @return
	 */
	public InvestorBaseAccountPasswordRep editLoginPassword(InvestorBaseAccountPasswordReq req) {
		InvestorBaseAccountEntity account = this.findOne(req.getInvestorOid());
		InvestorBaseAccountPasswordRep rep = new InvestorBaseAccountPasswordRep(account);
		if (PwdUtil.checkPassword(req.getUserPwd(), account.getPayPwd(), account.getPaySalt())) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("登录密码和交易密码不能一致！");
			return rep;
		}
		//更新主账户的登录密码和主账户下的所有子账户的密码
		this.updatePasswordEntity(account, req);
		return rep;
	}
	
	/**
	 * 修改登录密码，包括验证旧密码（新）
	 * @param req
	 * @return
	 */
	public InvestorBaseAccountPasswordRep modifyLoginPassword(InvestorBaseAccountPasswordReq req) {
		InvestorBaseAccountEntity account = this.findOne(req.getInvestorOid());
		InvestorBaseAccountPasswordRep rep = new InvestorBaseAccountPasswordRep();
		if (!PwdUtil.checkPassword(req.getOldUserPwd(), account.getUserPwd(), account.getSalt())) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("原登录密码输入不正确！");
			return rep;
		}
		
		return this.editLoginPassword(req);
	}
	
	/**
	 * 忘记登录密码
	 * @param req
	 * @return
	 */
	public InvestorBaseAccountPasswordRep forgetLoginPassword(InvestorBaseAccountPasswordReq req) {
		InvestorBaseAccountEntity account = this.findByPhoneNum(req.getUserAcc());
		this.sMSUtils.checkVeriCode(req.getUserAcc(), SMSTypeEnum.smstypeEnum.forgetlogin.toString(), req.getVericode());
		this.updatePasswordEntity(account, req);
		return new InvestorBaseAccountPasswordRep(account);
	}
	
	public void updatePasswordEntity(InvestorBaseAccountEntity account, InvestorBaseAccountPasswordReq req) {
		account.setSalt(Digests.genSalt());
		account.setUserPwd(PwdUtil.encryptPassword(req.getUserPwd(), account.getSalt()));
		this.updateEntity(account);
	}
	
	/**
	 * 校验交易密码是否正确
	 * @param req
	 * @return
	 * 
	 * 修改：将子账户的交易密码设置成主账户的交易密码，同时输入的交易密码的错误次数超过5次，则进行锁定
	 */
	
	public BaseResp checkPayPwd(InvestorBaseAccountPayPwdReq req) {
			
		InvestorBaseAccountEntity account = this.findOne(req.getInvestorOid());
		if(account!=null){			
		
			}else{
				throw new AMPException("基本账户不存在");
			}
		
		//查看redis中是否有交易密码输入的记录
		InvestorBaseAccountRedisInfo accountPayPwd = this.saveAccountPayPwdRedis(req.getInvestorOid());
		logger.info("redis中关于=账户输入交易密码的的信息：{}",accountPayPwd);
		
		//判断其锁定状态
		InvestorBaseAccountRedisInfo  accountPayPwdRedis = this.payPwdLockStatus(req.getInvestorOid());
		
		//进行交易密码的校验
		InvestorBaseAccountEntity basicAccount = this.findOne(req.getInvestorOid());
		BaseResp rep = new BaseResp();
		if (!PwdUtil.checkPassword(req.getPayPwd(), basicAccount.getPayPwd(), basicAccount.getPaySalt())) {
			//支付密码错误，错误次数加1
			accountPayPwdRedis.setPwdErrorTimes(accountPayPwdRedis.getPwdErrorTimes()+1);
			accountPayPwdRedis.setLockTime(DateUtil.getSqlDate());
			logger.info("redis中的交易密码的输入数据：{}",accountPayPwdRedis);
			
			//将新的信息保存到redis中
			InvestorBaseAccountPayPwdRedisUtil.set(redis, req.getInvestorOid(),accountPayPwdRedis );
				
			//判断已经输入交易密码的次数
			if(accountPayPwdRedis.getPwdErrorTimes() >4){				
				throw new AMPException("今日交易密码将锁定，请尝试找回交易密码");
			}else{
				throw new GHException("交易密码不正确，您还可以输入" + (5- accountPayPwdRedis.getPwdErrorTimes()) + "次");
			}
			
		}else{
			//密码正确，redis中的交易密码的输入记录清零
			accountPayPwdRedis.setLockTime(null);
			accountPayPwdRedis.setPwdErrorTimes(0);
			
			//将新的信息保存到redis中
			InvestorBaseAccountPayPwdRedisUtil.set(redis, req.getInvestorOid(),accountPayPwdRedis );			
		}	
	
		return rep;		
		
	}


	/** 保存交易密码的输入次数和锁定时间的redis记录  */
	private InvestorBaseAccountRedisInfo saveAccountPayPwdRedis(String investorOid) {
		InvestorBaseAccountRedisInfo accountPayPwdRedis =  InvestorBaseAccountPayPwdRedisUtil.get(redis,investorOid);
		if(accountPayPwdRedis==null){
			accountPayPwdRedis = new InvestorBaseAccountRedisInfo();
			accountPayPwdRedis.setPwdErrorTimes(0);
		} 
		//将其保存到redis中
		 accountPayPwdRedis = InvestorBaseAccountPayPwdRedisUtil.set(redis, investorOid, accountPayPwdRedis);
		return accountPayPwdRedis;
		
	}
	
	
	/**  判断交易密码的锁定状态   */
	private InvestorBaseAccountRedisInfo payPwdLockStatus(String investorOid) {
		//从redis中获取其交易密码的输入详情
		InvestorBaseAccountRedisInfo accountPayPwdRedis =  InvestorBaseAccountPayPwdRedisUtil.get(redis, investorOid);
		//判断锁定的时间是否大于当天
		if(accountPayPwdRedis.getLockTime()!=null){
			if(!DateUtil.same(DateUtil.getSqlDate(), accountPayPwdRedis.getLockTime())){
				//当锁定的时间大于当天，则进行清零
				accountPayPwdRedis.setPwdErrorTimes(0);
			}
		}
		
		//判断锁定的次数是否大于4
		if(accountPayPwdRedis.getPwdErrorTimes()>4){
			throw new AMPException("今日交易密码将锁定，请尝试找回交易密码");
		}
		
		//将交易密码的输入信息 更新后保存到redis中
		accountPayPwdRedis = InvestorBaseAccountPayPwdRedisUtil.set(redis,investorOid,accountPayPwdRedis);
		
		return accountPayPwdRedis;
	}
	
	
	

	/**
	 * 设置/修改交易密码
	 * @param req
	 * @return
	 */
	public BaseResp editPayPwd(InvestorBaseAccountPayPwdReq req) {
		InvestorBaseAccountEntity account = this.findOne(req.getInvestorOid());
		BaseResp rep = new BaseResp();
		//判断输入的交易密码是否太过简单
		//this.judgeEasyPayPwd(req.getPayPwd());
		//判断输入的交易密码是否太过简单(通过正则校验，目前用这个)
		this.judgeEasyPayPwd(req.getPayPwd());
		
		if (PwdUtil.checkPassword(req.getPayPwd(), account.getUserPwd(), account.getSalt())) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("交易密码和登录密码不能一致！");
			return rep;
		}
		this.updatePayPasswordEntity(account, req);
		
		/** 若因为交易密码输入5次而进行重置，则需要进行清零 */
		this.resetRedisZero(req.getInvestorOid());
		
		/** 更新主账户下所有子账户redis中的校验密码的信息*/
		List<SonAccountEntity> sonList = this.sonAccountDao.findByPidAndStatus(account.getOid());
		if(sonList !=null || sonList.size() > 0){			
			for(SonAccountEntity sonAccount : sonList){
				this.resetRedisZero(sonAccount.getSid());
			}
		}
		
		return rep;
	}
	
	/** 若因为交易密码输入5次而进行重置，则需要进行清零 */
	public void resetRedisZero(String investorOid) {
		InvestorBaseAccountRedisInfo accountPayPwdRedis = InvestorBaseAccountPayPwdRedisUtil.get(redis, investorOid);
		if (accountPayPwdRedis != null) {
			accountPayPwdRedis.setPwdErrorTimes(0);
			accountPayPwdRedis.setLockTime(null);
			InvestorBaseAccountPayPwdRedisUtil.set(redis, investorOid, accountPayPwdRedis);
			logger.info("redis中账户的交易密码的输入信息：{}", accountPayPwdRedis);
		}
	}
	
	
	/**
	 * 修改交易密码，包括验证旧密码（新）
	 * @param req
	 * @return
	 */
	public BaseResp modifyPayPwd(InvestorBaseAccountPayPwdReq req) {
		InvestorBaseAccountEntity account = this.findOne(req.getInvestorOid());
		BaseResp rep = new BaseResp();
		if (!PwdUtil.checkPassword(req.getOldPayPwd(), account.getPayPwd(), account.getPaySalt())) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("原交易密码输入不正确！");
			return rep;
		}
		
		return this.editPayPwd(req);
	}
	
	/**
	 * 重置交易密码
	 * @param req
	 * @return
	 */
	public BaseResp forgetPayPwd(InvestorBaseAccountPayPwdReq req) {
		this.sMSUtils.checkVeriCode(req.getUserAcc(), SMSTypeEnum.smstypeEnum.forgetpaypwd.toString(), req.getVericode());
		return this.editPayPwd(req);
	}
	
	public void updatePayPasswordEntity(InvestorBaseAccountEntity account, InvestorBaseAccountPayPwdReq req) {
		account.setPaySalt(Digests.genSalt());
		account.setPayPwd(PwdUtil.encryptPassword(req.getPayPwd(), account.getPaySalt()));
		this.updateEntity(account);
		
		//更新主账户对应的子账户下是所有的所有的交易密码
		this.updateSonAccountPaypwd(account);
		
				
	}
	
	/**
	 * 用户信息
	 * @param investorOid
	 * @return
	 */
	public BaseAccountInfoRep getAccountInfo(String investorOid) {
		InvestorBaseAccountEntity account = this.findOne(investorOid);
		
		BaseAccountInfoRep rep = new BaseAccountInfoRep();
		rep.setIslogin(true);
		rep.setInvestorOid(account.getOid());
		rep.setUserAcc(account.getPhoneNum());
		rep.setUserPwd(StringUtil.isEmpty(account.getUserPwd()) ? false : true);
		rep.setPaypwd(StringUtil.isEmpty(account.getPayPwd()) ? false : true);
		rep.setSceneid(account.getUid());
		rep.setStatus(account.getStatus()); // 冻结状态
		rep.setSource(account.getSource()); // 注册来源
		rep.setChannelid(account.getChannelid()); // 渠道来源
		rep.setCreateTime(DateUtil.formatFullPattern(account.getCreateTime())); // 注册时间
		BankEntity bank = this.bankService.getOKBankByInvestorOid(investorOid);
		
		if (null != bank) {
			rep.setName(StringUtil.kickstarOnRealname(bank.getName())); // 姓名
			rep.setFullName(bank.getName()); // 全姓名
			rep.setIdNumb(StringUtil.kickstarOnIdNum(bank.getIdCard())); // 身份证号
			rep.setFullIdNumb(bank.getIdCard()); // 全身份证号
			rep.setBankName(bank.getBankName()); // 银行名称
			rep.setBankCardNum(StringUtil.kickstarOnCardNum(bank.getDebitCard())); //　银行卡号
			rep.setFullBankCardNum(bank.getDebitCard()); // 全银行卡号
			rep.setBankPhone(StringUtil.kickstarOnPhoneNum(bank.getPhoneNo())); // 预留手机号
		}		
		return rep;
	}
	
	/**
	 * 解锁登录锁定
	 * @param investorOid
	 */
	public void cancelLoginLock(String investorOid) {
		this.findOne(investorOid);
		InvestorBaseAccountRedisInfo accountRedis = this.saveAccountRedis(investorOid, "");
		accountRedis.setPwdErrorTimes(0);
		this.updateAccountRedis(investorOid, accountRedis);
	}
	
	/**
	 * 剔除其他平台登录用户
	 * @param req
	 * @param rep
	 * @param sessionId
	 */
	public void logoutOtherPlatform(InvestorBaseAccountPasswordReq req, InvestorBaseAccountPasswordRep rep, String sessionId) {
		if (!StringUtil.isEmpty(rep.getInvestorOid())) {
			// 修改登录密码之后，剔除其他平台登录用户
			logger.info("当前平台：{}，用户：{}，修改登录密码，其他平台退出登录。", req.getPlatform(), rep.getInvestorOid());
			this.terminalConfig.logoutOthers(sessionId, rep.getInvestorOid(), req.getPlatform());
		}
	}
	
	/**
	 * 获取注册人数
	 * @return
	 */
	public long getAllCount() {
		return this.investorBaseAccountDao.count();
	}
	
	/**
	 * 获取实名人数，realName存在即实名
	 * @return
	 */
	public long getAllVerifiedCount() {
		return this.investorBaseAccountDao.getAllVerifiedCount();
	}
	
	public int forbiddenUser(String investorOid) {
		
		return this.investorBaseAccountDao.forbiddenUser(investorOid);
	}

	public int normalUser(String investorOid) {
		return this.investorBaseAccountDao.normalUser(investorOid);
		
	}
	
	//通过uid查找用户信息
	public InvestorBaseAccountEntity getBaseAccountEntity(String uid) {		
		return investorBaseAccountDao.findByMemberId(uid);
	} 
	

	/**当主账户的交易密码修改时，同步更新相关的所有子账号的交易密码
	 * 
	 * baseAccount  主账户
	 * */
	public void updateSonAccountPaypwd(InvestorBaseAccountEntity baseAccount){
		List<SonAccountEntity> sonAccount =  this.sonAccountDao.findByPid(baseAccount.getOid());

		if(sonAccount!=null&&sonAccount.size()>0){
			for(SonAccountEntity son:sonAccount){
				InvestorBaseAccountEntity sonBaseAccount =  this.investorBaseAccountDao.findByOid(son.getSid());
				if(sonBaseAccount!=null){
					sonBaseAccount.setPayPwd(baseAccount.getPayPwd());
					sonBaseAccount.setPaySalt(baseAccount.getPaySalt());
					sonBaseAccount.setUpdateTime(DateUtil.getSqlCurrentDate());
					this.investorBaseAccountDao.save(sonBaseAccount);
				}				
			}
		}
	}

	public InvestorBaseAccountEntity updatePayPwdEntity(InvestorBaseAccountEntity sonBaseAccount) {
		sonBaseAccount.setUpdateTime(DateUtil.getSqlCurrentDate());
		 return this.investorBaseAccountDao.save(sonBaseAccount);
	};

	/**  判断输入的交易密码太过简单   */
	/*public void judgeEasyPayPwd(String payPwd){
		//对枚举的进行遍历
		for(EasyPayPwd e : EasyPayPwd.values()){
			if(payPwd.equals(e.getNum())){
				throw new AMPException("您输入的交易密码过于简单，请重新输入");
			}
		}

	}*/
	
	/**  判断输入的交易密码太过简单   (通过正则校验，目前使用这个版本)*/
	public void judgeEasyPayPwd(String payPwd){
		
		//正则规则---不能包含3位及以上相同字符的重复
		String rule = "^.*(.)\\1{2,}+.*$";
		//正则规则--不能包含2位及以上字符组合的重复		
		String rule2 = "^.*(.{2})(.*)\\1+.*$";
		//正则规则---数字是两个以上重复的
		String rule3 = "^(?:(\\d)\\1)+$";
		//将正则规则进行编译
		Pattern pattern = Pattern.compile(rule);
		Matcher matcher = pattern.matcher(payPwd);
		boolean flag = !matcher.matches();
		
		Pattern pattern2 = Pattern.compile(rule2);
		Matcher matcher2 = pattern2.matcher(payPwd);
		boolean flag2 = !matcher2.matches();
		
		
		Pattern pattern3 = Pattern.compile(rule3);
		Matcher matcher3 = pattern3.matcher(payPwd);
		boolean flag3 = !matcher3.matches();
		
		if(flag && flag2 && flag3 && this.ruled(payPwd)){
			
		}else{
			throw new AMPException("您输入的交易密码过于简单，请重新输入");
		}
	}
	
	//正则规则--不能包含3位及以上的正序或者逆序的连续字符
	public boolean ruled(String s){
		char[] cc = s.toCharArray();
		for(int i=0;i<cc.length-2;i++){
			if(Math.abs(cc[i] - cc[i+1]) == 1 && (cc[i] - cc[i+1]) == (cc[i+1] - cc[i+2])){
				return false;
			}
	}
		return true;
	
	}

	
	
	/** 将主账户对应的子账户下的markId都清零 */
	public List<SonAccountEntity> clearMarkId(String investorOid){
		List<SonAccountEntity> list = this.sonAccountDao.findByPidAndStatus(investorOid);
		return list;
	}
	
	/** 获取账户中心愿计划的累计投资次数、赎回金额、赎回次数等*/
	public WishplanInfo getWishPlanInfoByOid(String investorOid) {

		WishplanInfo wishPlanInfo = new WishplanInfo();

/*		int oncePlanCount = this.planInvestDao.queryTotalInvestInfo(investorOid);
		int monthPlanCount = this.planMonthDao.queryTotalInvestCount(investorOid);
		// 获取心愿计划的累计投资次数
		wishPlanInfo.setPlanTotalInvestCount(oncePlanCount + monthPlanCount);*/
		/** 从订单表中查询心愿计划的累计投资次数和今日投资次数以及今日投资金额 */
		int totalPlanInvestCount = this.tradeOrderService.queryTotalInvestCount(investorOid);//心愿计划累计投资次数
		List<Object[]>  todayPlanInvestInfo = this.tradeOrderService.queryTodayInvestInfo(investorOid);//心愿计划当日投资次数
		wishPlanInfo.setPlanTotalInvestCount(totalPlanInvestCount);
		
		for(Object[]  todayInvest :todayPlanInvestInfo){
			wishPlanInfo.setPlanTodayInvestCount(((BigInteger)todayInvest[0]).intValue());
			wishPlanInfo.setPlanTodayInvestAmount((BigDecimal)todayInvest[1]);
		}

		List<Object[]> wishplanTotalRedeem = this.tradeOrderService.queryWishplanRedeem(investorOid);
		List<Object[]> wishplanTodayRedeem = this.tradeOrderService.queryWishplanTodayRedeem(investorOid);

		for (Object[] totalRedeem : wishplanTotalRedeem) {
			wishPlanInfo.setPlanTotalRedeemCount(((BigInteger) totalRedeem[0]).intValue());
			wishPlanInfo.setPlanTotalRedeemAmount((BigDecimal) totalRedeem[1]);
		}

		for (Object[] todayRedeem : wishplanTodayRedeem) {
			wishPlanInfo.setPlanTodayRedeemCount(((BigInteger) todayRedeem[0]).intValue());
			wishPlanInfo.setPlanTodayRedeemAmount((BigDecimal) todayRedeem[1]);
		}
		// 心愿计划累计还本金额
		List<InvestorTradeOrderEntity> tradeOrder = this.tradeOrderService.queryWishplanOid(investorOid);
		wishPlanInfo.setPlanTotalRepayLoan(new BigDecimal(0));
		if (tradeOrder != null && tradeOrder.size() > 0) {
			for (InvestorTradeOrderEntity trade : tradeOrder) {
				PlanInvestEntity planInvest = this.planInvestDao.findByOid(trade.getWishplanOid());
				if (planInvest == null) {
					// 月定投
					PlanMonthEntity planMonth = this.planMonthDao.findByOid(trade.getWishplanOid());
					wishPlanInfo.setPlanTotalRepayLoan(
							wishPlanInfo.getPlanTotalRepayLoan().add(planMonth.getTotalDepositAmount()));
				} else {
					// 一次性
					wishPlanInfo.setPlanTotalRepayLoan(
							wishPlanInfo.getPlanTotalRepayLoan().add(planInvest.getDepositAmount()));
				}
			}

		}

		return wishPlanInfo;
	}
	
	/**
	 * 重置交易密码前的校验
	 * 
	 * 	登录密码、登录密码和身份证号、登录密码和身份证号和银行卡号这三种
	 * 
	 * */

	public BaseResp checkBeforeresetPayPassword(CheckBeforeResetPassReq req) {
		BaseResp resp = new BaseResp();
		InvestorBaseAccountEntity baseAccount = this.getBaseAccountEntity(req.getInvestorOid());
		if (PwdUtil.checkPassword(req.getLoginPassword(), baseAccount.getUserPwd(), baseAccount.getSalt())) {
			// 登录密码校验成功后判断是否实名
			SonAccountInfoRep sonAccountInfoRep = this.sonAccountService.sonAccountInfo(req.getInvestorOid());
			if (sonAccountInfoRep.getFullIdNumb() == null) {
				// 未实名，则只通过登录密码进行重置交易密码前的校验
				return resp;
			} else {
				// 已经实名，判断身份证号是否相同
				if (req.getIdCardNum().equals(sonAccountInfoRep.getFullIdNumb())) {
					// 已实名，判断是否绑卡
					if (sonAccountInfoRep.getFullBankCardNum() == null) {
						// 未绑卡，则通过登录密码和身份证号进行重置交易密码前的校验
						return resp;
					} else {
						if (req.getBankCardNum().equals(sonAccountInfoRep.getFullBankCardNum())) {
							return resp;
						} else {
							throw new AMPException("您输入的银行卡号不正确");
						}
					}
				} else {
					// 身份证不符
					throw new AMPException("您输入的证件号码不正确");
				}
			}
		} else {
			logger.info("用户：{}，密码输入错误：时间：{}", baseAccount.getPhoneNum(), DateUtil.getSqlDate());
			throw new GHException("您输入的登录密码不正确");
		}

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
