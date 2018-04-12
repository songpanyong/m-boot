package com.guohuai.mmp.investor.baseaccount.check;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.switchcraft.SwitchEntity;
import com.guohuai.ams.switchcraft.SwitchQueryCTRep;
import com.guohuai.ams.switchcraft.SwitchService;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.baseaccount.check.CheckQueryRep.CheckQueryRepBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CheckService {

	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	@Value("${mimosa.account.lock:no}")
	private String accountLock;
	@Autowired
	private CheckDao checkDao;	
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private SwitchService switchService;
	
	/**
	 * 新增
	 * @param entity
	 * @return
	 */
	public CheckEntity saveEntity(CheckEntity entity){
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}
	
	public CheckEntity saveEntity(CheckEntity check, InvestorBaseAccountEntity account, CheckDataRep dataRep) {
		check.setInvestorBaseAccount(account);
		check.setMoneyAmount(dataRep.getMoneyAmount());
		check.setCapitalAmount(dataRep.getCapitalAmount());
		check.setRecharge(dataRep.getDepositOk());
		check.setWithdraw(dataRep.getWithdrawOk());
		check.setTnInterest(dataRep.getTnInterest());;
		check.setT0Interest(dataRep.getT0Interest());;
		check.setCouponAmt(dataRep.getCouponAmt());
		check.setBalance(dataRep.getWithdrawAvailableBalance());
		check.setApplyBalance(dataRep.getWithdrawFrozenBalance());
		check.setT0ApplyAmt(dataRep.getT0ToConfirm());
		check.setT0HoldAmt(dataRep.getT0Hold());
		check.setTnApplyAmt(dataRep.getTnToConfirm());
		check.setTnHoldlAmt(dataRep.getTnHold());
		check.setTnApplyAmt(dataRep.getTnToConfirm());
		return check;
	}
	
	/**
	 * 修改
	 * @param entity
	 * @return
	 */
	public CheckEntity updateEntity(CheckEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.checkDao.save(entity);
	}
	
	public CheckEntity getOne(String checkOid) {
		CheckEntity check = this.checkDao.findOne(checkOid);
		if (null == check) {
			// error.define[140002]=资金总额对账记录不存在！(CODE:140002)
			throw GHException.getException(140002);
		}
		return check;
	}
	
	/**
	 * 根据用户和对账日期获取对账信息
	 * @param account
	 * @param checkTime
	 * @return
	 */
	public CheckEntity findByAccAndChTime(InvestorBaseAccountEntity account, String checkTime) {
		return this.checkDao.findByAccAndChTime(account, checkTime);
	}
	
	/**
	 * 获取最新对账时间
	 * @return
	 */
	public String getMaxCheckTime() {
		return this.checkDao.getMaxCheckTime();
	}
	
	/**
	 * 获取当前用户最多对账日期
	 * @param investorOid
	 * @return
	 */
	public String getUserMaxCheckTime(String investorOid) {
		return this.checkDao.getUserMaxCheckTime(investorOid);
	}
	
	@Transactional
	public PageResp<CheckQueryRep> checkQuery(Specification<CheckEntity> spec, Pageable pageable) {		
		Page<CheckEntity> checks = this.checkDao.findAll(spec, pageable);
		PageResp<CheckQueryRep> pagesRep = new PageResp<CheckQueryRep>();		
		
		for (CheckEntity check : checks) {
			CheckQueryRep rep = new CheckQueryRepBuilder()
					.oid(check.getOid())
					.investOid(check.getInvestorBaseAccount().getOid())
					.checkTime(check.getCheckTime())
					.phone(this.kickstar ? StringUtil.kickstarOnPhoneNum(check.getInvestorBaseAccount().getPhoneNum()) : check.getInvestorBaseAccount().getPhoneNum())
					.checkStatus(this.checkStatus2Ch(check.getCheckStatus()))
					.userStatus(this.userStatus2Ch(check.getUserStatus()))
					.moneyAmount(check.getMoneyAmount())
					.capitalAmount(check.getCapitalAmount())
					.recharge(check.getRecharge())
					.withdraw(check.getWithdraw())
					.tnInterest(check.getTnInterest())
					.t0Interest(check.getT0Interest())
					.couponAmt(check.getCouponAmt())
					.yesterdayCapitalAmt(check.getYesterdayCapitalAmt())
					.balance(check.getBalance())
					.applyBalance(check.getApplyBalance())
					.t0ApplyAmt(check.getT0ApplyAmt())
					.t0HoldAmt(check.getT0HoldAmt())
					.tnApplyAmt(check.getTnApplyAmt())
					.tnHoldlAmt(check.getTnHoldlAmt())
					.build();
			pagesRep.getRows().add(rep);
			
		}
		pagesRep.setTotal(checks.getTotalElements());	
		return pagesRep;
	}
	
	/**
	 * 生成对账记录
	 * @param currentCheckTime 当前对账日期
	 * @return
	 */
	public BaseResp generateCheckOrders(String currentCheckTime) {
		BaseResp rep = new BaseResp();
		
		// 最近对账时间
		String preCheckDate = this.checkDao.getMaxCheckTime();
		if (currentCheckTime.equals(preCheckDate)) {
			// 当前对账日期已经对账过！(CODE:140000)
			throw GHException.getException(140000);
		}

		List<InvestorBaseAccountEntity> accounts = investorBaseAccountService.getInvestorsNotSuperaccount();
		
		for (InvestorBaseAccountEntity account : accounts) {
			try {
				this.generate(account, preCheckDate, currentCheckTime);
			} catch (Exception e) {
				log.info("账户总额对账，用户：{}，在：{}，对账失败。原因：{}", account.getOid(), currentCheckTime, AMPException.getStacktrace(e));
			}
		}
		return rep;
	}
	
	/**
	 * 单个重新对账
	 * @param currentCheckTime 当前对账日期
	 * @return
	 */
	public BaseResp singleGenerate(String checkOid) {
		BaseResp rep = new BaseResp();
		CheckEntity check = this.getOne(checkOid);
		
		String checkTime = check.getCheckTime();
		
		InvestorBaseAccountEntity account = check.getInvestorBaseAccount();
		
		try {
			this.generate(account, StringUtil.EMPTY, checkTime);
		} catch (Exception e) {
			log.info("账号总额重算，用户：{}，在：{}，重算失败。原因：{}", account.getOid(), checkTime, AMPException.getStacktrace(e));
		}
		
		return rep;
	}
	
	/**
	 * 对账
	 * @param account 投资者账户
	 * @param preCheckDate 前一次对账日期
	 * @param checkTime 当前对账日期
	 * @return
	 */
	public CheckEntity generate(InvestorBaseAccountEntity account, String preCheckDate, String checkTime) {
		
		CheckDataRep dataRep = new CheckDataRep();
		
		// 资金总额
		List<Object[]> moneyData = this.checkDao.getCheckMoneyData(account.getOid());
		
		// 资产总额
		List<Object[]> capitalData = this.checkDao.getCheckCapitalData(account.getOid());
		
		for (Object[] obj : moneyData) {
			if (null != obj) {
				dataRep.setDepositOk(BigDecimalUtil.parseFromObject(obj[0]));
				dataRep.setWithdrawOk(BigDecimalUtil.parseFromObject(obj[1]));
				dataRep.setTnInterest(BigDecimalUtil.parseFromObject(obj[2]));
				dataRep.setT0Interest(BigDecimalUtil.parseFromObject(obj[3]));
				dataRep.setCouponAmt(BigDecimalUtil.parseFromObject(obj[4]));
				dataRep.setMoneyAmount(BigDecimalUtil.parseFromObject(obj[5]));
			}
		}

		for (Object[] obj : capitalData) {
			if (null != obj) {
				dataRep.setWithdrawAvailableBalance(BigDecimalUtil.parseFromObject(obj[0]));
				dataRep.setWithdrawFrozenBalance(BigDecimalUtil.parseFromObject(obj[1]));
				dataRep.setT0Hold(BigDecimalUtil.parseFromObject(obj[2]));
				dataRep.setT0ToConfirm(BigDecimalUtil.parseFromObject(obj[3]));
				dataRep.setTnHold(BigDecimalUtil.parseFromObject(obj[4]));
				dataRep.setTnToConfirm(BigDecimalUtil.parseFromObject(obj[5]));
				dataRep.setCapitalAmount(BigDecimalUtil.parseFromObject(obj[6]));	
			}
		}
		
		CheckEntity preCheck = new CheckEntity();
		if (!StringUtil.isEmpty(preCheckDate)) {
			preCheck = this.checkDao.findByAccAndChTime(account, preCheckDate);
		} 

		// 对账日期
		CheckEntity check = this.checkDao.findByAccAndChTime(account, checkTime); 
		
		if (null == check) {
			check = new CheckEntity();
			check.setYesterdayCapitalAmt(preCheck == null ? BigDecimal.ZERO : preCheck.getCapitalAmount());
		} 
		
		check = this.saveEntity(check, account, dataRep);
		
		if (dataRep.getMoneyAmount().compareTo(dataRep.getCapitalAmount()) != 0) {
			// 资金明细重算
//			if (this.detailCheckService.isIgnore(account)) {
//				check.setCheckStatus(CheckEntity.Check_Status_Ignore);
//				check.setUserStatus(CheckEntity.Check_USER_Status_IsOk);
//			} else {
				check.setCheckStatus(CheckEntity.Check_Status_Failed);
				check.setUserStatus(CheckEntity.Check_USER_Status_IsLock);
				// 锁定开关打开则锁定用户
//				if ("yes".equals(this.accountLock)) {
//					terminalConfig.lockUser(account.getOid());
//				}
				SwitchQueryCTRep rep = this.switchService.findCode(SwitchEntity.SWITCH_code_PlatformCheckLock, null, null);
				if (null != rep && SwitchEntity.SWITCH_Status_enable.equals(rep.getStatus())) {
					this.investorBaseAccountService.forbiddenUser(check.getInvestorBaseAccount().getOid());
				}
//			}
		} else {
			check.setCheckStatus(CheckEntity.Check_Status_OK);
			check.setUserStatus(CheckEntity.Check_USER_Status_IsOk);
		}
		check.setCheckTime(checkTime);
		return this.saveEntity(check);
	}
	
	/**
	 * 平台总资产
	 * @param checkTime
	 * @return
	 */
	public CheckSumAmtRep getPlatformSumAmt(String checkTime) {
		CheckSumAmtRep rep = new CheckSumAmtRep();
		if (!StringUtil.isEmpty(checkTime)) {
			List<Object[]> sum = this.checkDao.getSumAmt(checkTime);
			if (null != sum && sum.size() > 0) {
				rep.setAllMoneyAmount((BigDecimal)sum.get(0)[0]);
				rep.setAllCapitalAmount((BigDecimal)sum.get(0)[1]);
			}
		}
		return rep;
	}
	
	/**
	 * 更新对账用户锁定状态
	 * @param checkOid
	 * @return
	 */
	public BaseResp uptCheckUserStatus(String checkOid) {
		BaseResp rep = new BaseResp();
		CheckEntity check = this.checkDao.findOne(checkOid);
		if (null != check) {
			check.setUserStatus(CheckEntity.Check_USER_Status_IsOk);
			this.updateEntity(check);
//			this.terminalConfig.unlockUser(check.getInvestorBaseAccount().getOid());
			this.investorBaseAccountService.normalUser(check.getInvestorBaseAccount().getOid());
		}
		return rep;
	}
	
	public String checkStatus2Ch(String checkStatus) {
		if (StringUtil.isEmpty(checkStatus)) {
			return StringUtil.EMPTY;
		}
		
		if (CheckEntity.Check_Status_OK.equals(checkStatus)) {
			return "对账成功";
		}
		if (CheckEntity.Check_Status_Failed.equals(checkStatus)) {
			return "对账失败";
		}
		
		if (CheckEntity.Check_Status_Ignore.equals(checkStatus)) {
			return "对账忽略";
		}
		return checkStatus;
	}
	
	public String userStatus2Ch(String userStatus) {
		if (StringUtil.isEmpty(userStatus)) {
			return StringUtil.EMPTY;
		}
		
		if (CheckEntity.Check_USER_Status_IsOk.equals(userStatus)) {
			return "未锁定";
		}
		if (CheckEntity.Check_USER_Status_IsLock.equals(userStatus)) {
			return "已锁定";
		}
		
		return userStatus;
	}
	
}
