package com.guohuai.mmp.investor.baseaccount.detailcheck;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.baseaccount.check.CheckService;
import com.guohuai.mmp.investor.baseaccount.detailcheck.DetailCheckQueryRep.DetailCheckQueryRepBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class DetailCheckService {

	
	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	@Autowired
	private DetailCheckDao detailCheckDao;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private CheckService checkService;
	
	/**
	 * 新增
	 * @param entity
	 * @return
	 */
	public DetailCheckEntity saveEntity(DetailCheckEntity entity) {
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}
	
	/**
	 * 修改
	 * @param entity
	 * @return
	 */
	public DetailCheckEntity updateEntity(DetailCheckEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.detailCheckDao.save(entity);
	}
	
	public DetailCheckEntity getOne(String detailOid) {
		DetailCheckEntity detailCheck = this.detailCheckDao.findOne(detailOid);
		if (null == detailCheck) {
			// 账户明细对账记录不存在！(CODE:140001)
			throw GHException.getException(140001);
		}
		return detailCheck;
	}
	
	/**
	 * 获取最新对账时间
	 * @return
	 */
	public String getMaxCheckTime() {
		return this.detailCheckDao.getMaxCheckTime();
	}
	
	@Transactional
	public PageResp<DetailCheckQueryRep> detailCheckQuery(Specification<DetailCheckEntity> spec, Pageable pageable) {		
		Page<DetailCheckEntity> checks = this.detailCheckDao.findAll(spec, pageable);
		PageResp<DetailCheckQueryRep> pagesRep = new PageResp<DetailCheckQueryRep>();		
		
		for (DetailCheckEntity detailCheck : checks) {
			
			DetailCheckQueryRep rep = new DetailCheckQueryRepBuilder()
					.oid(detailCheck.getOid())
					.investOid(detailCheck.getInvestorBaseAccount().getOid())
					.checkTime(detailCheck.getCheckTime())
					.phone(this.kickstar ? StringUtil.kickstarOnPhoneNum(detailCheck.getInvestorBaseAccount().getPhoneNum()) : detailCheck.getInvestorBaseAccount().getPhoneNum())
					.checkStatus(this.checkStatus2Ch(detailCheck.getCheckStatus()))
					.balance(detailCheck.getBalance())
					.recorrectBalance(detailCheck.getRecorrectBalance())
					.build();
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(checks.getTotalElements());	
		return pagesRep;
	}
	
	/**
	 * 资金明细变化对账
	 * @param checkTime 对账日期
	 * @return
	 */
	public BaseResp generateDetailCheck(String checkTime) {
		BaseResp rep = new BaseResp();
		
		List<InvestorBaseAccountEntity> accounts = investorBaseAccountService.getInvestorsNotSuperaccount();
		
		for (InvestorBaseAccountEntity account : accounts) {			
			try {
				this.generateDetailCheck(account, checkTime);
			} catch (Exception e) {
				log.info("明细对账，用户：{}，在：{}，对账失败。原因：{}", account.getOid(), checkTime, AMPException.getStacktrace(e));
			}
		}
		
		return rep;
	}
	
	/**
	 * 单个重算对账
	 * @param detailOid
	 * @return
	 */
	public BaseResp singleCheck(String detailOid) {
		BaseResp rep = new BaseResp();
		DetailCheckEntity detailCheck = this.getOne(detailOid);
		
		String checkTime = detailCheck.getCheckTime();
		
		InvestorBaseAccountEntity account = detailCheck.getInvestorBaseAccount();
		
		try {
			this.generateDetailCheck(account, checkTime);
		} catch (Exception e) {
			log.info("明细重算，用户：{}，在：{}，重算失败。原因：{}", account.getOid(), checkTime, AMPException.getStacktrace(e));
		}
		return rep;
	}
	
	/**
	 * 对账
	 * 累计充值 - 累计提现 - 提现在途中 - 累计申购(不包含卡券) - 申购在途中(不包含卡券) + 赎回 + 卡券  + 现金红包
	 * 累计充值：SELECT SUM(orderamount) AS depositamount FROM t_money_investor_bankorder WHERE ordertype = 'deposit' AND orderstatus = 'paySuccess' AND investoroid = 'ffd3459e994c420d988ea32032e6d6d7'
	 * 累计提现：SELECT SUM(orderamount) AS withdrawAmount FROM t_money_investor_bankorder WHERE ordertype = 'withdraw' AND orderstatus = 'paySuccess' AND investoroid = 'ffd3459e994c420d988ea32032e6d6d7' 
	 * 提现在途：SELECT SUM(orderamount) AS withdrawOnWayAmount FROM t_money_investor_bankorder WHERE ordertype = 'withdraw' AND orderstatus = 'toPay' AND investoroid = 'ffd3459e994c420d988ea32032e6d6d7'
	 * 累计申购：SELECT SUM(orderamount) AS totalInvestAmount FROM T_MONEY_INVESTOR_TRADEORDER WHERE ordertype = 'invest' AND orderstatus = 'confirmed' AND investoroid = 'ffd3459e994c420d988ea32032e6d6d7' 
	 * 申购在途：SELECT SUM(orderamount) AS totalOnWayInvest FROM T_MONEY_INVESTOR_TRADEORDER WHERE ordertype = 'invest' AND orderstatus = 'accepted' AND investoroid = 'ffd3459e994c420d988ea32032e6d6d7'
	 * 赎回：SELECT SUM(payAmount) AS totalRedeemAmount FROM T_MONEY_INVESTOR_TRADEORDER WHERE ordertype IN ('normalRedeem','cash','cashFailed','clearRedeem','dividend') AND publisherCloseStatus = 'closed' AND investoroid = 'ffd3459e994c420d988ea32032e6d6d7'
	 * 卡券：SELECT SUM(a.couponAmount) AS couponAmount FROM T_MONEY_INVESTOR_TRADEORDER_COUPON as a,T_MONEY_INVESTOR_TRADEORDER as b WHERE a.orderOid = b.oid AND a.couponType = 'coupon' AND b.orderStatus IN ('accepted', 'confirmed') and a.investorOid = 'ffd3459e994c420d988ea32032e6d6d7'
	 * 现金红包：SELECT SUM(orderAmount) AS redEnvelope FROM T_MONEY_INVESTOR_BANKORDER WHERE orderType = 'redEnvelope' AND orderStatus = 'paySuccess' AND investoroid = 'ffd3459e994c420d988ea32032e6d6d7' 
	 * 可用资产：累计充值 - 累计提现 - 提现在途 - 累计申购 - 申购在途 + 现金红包 + 卡券 + 现金红包
	 */
	public void generateDetailCheck(InvestorBaseAccountEntity account, String checkTime) {
		DetailDataRep rep = this.getDetailData(account);
		
		// 资金明细变动重算金额：已充值-提现金额-申购申请中+资产已结算+优惠券+补登金额(入)-补登金额(出)
		BigDecimal balance = rep.getBalance();
		
		DetailCheckEntity detailCheck = this.detailCheckDao.findByCheckTimeAndInvestorBaseAccount(checkTime, account);
		
		if (null == detailCheck) {
			detailCheck = new DetailCheckEntity();
			detailCheck.setInvestorBaseAccount(account);
		}
		
		detailCheck.setBalance(account.getApplyAvailableBalance());
		detailCheck.setRecorrectBalance(balance);

		if (detailCheck.getBalance().compareTo(detailCheck.getRecorrectBalance()) != 0) {
			detailCheck.setCheckStatus(DetailCheckEntity.Detail_Check_Status_Failed);
		} else {
			detailCheck.setCheckStatus(DetailCheckEntity.Detail_Check_Status_OK);
		}
		detailCheck.setCheckTime(checkTime);
		this.saveEntity(detailCheck);
	}
	
//	/**
//	 * 判断资金总额对账是否可忽略
//	 * @param account
//	 * @return
//	 */
//	public boolean isIgnore(InvestorBaseAccountEntity account) {
//		DetailDataRep rep = this.getDetailData(account);
//		
//		// 资金明细变动重算金额：已充值-提现金额-申购申请中+资产已结算+优惠券+补登金额(入)-补登金额(出)
//		BigDecimal recorrectBalance = rep.getDepositOk().subtract(rep.getWithdrawAmt()).subtract(rep.getApplyingAmt())
//				.add(rep.getMoneyDone()).add(rep.getCoupon())
//				.add(rep.getAllRecorrectAmtAdd()).subtract(rep.getAllRecorrectAmtAReduce());					
//		
//		if (account.getBalance().compareTo(recorrectBalance) != 0) {
//			return false;
//		}
//		return true;
//	}
	
	/**
	 * 累计充值 - 累计提现 - 提现在途中 - 累计申购(不包含卡券) - 申购在途中(不包含卡券) + 赎回 + 卡券  + 现金红包
	 * @param account
	 * @return
	 */
	public DetailDataRep getDetailData(InvestorBaseAccountEntity account) {
		
		List<Object[]> data = this.detailCheckDao.getMoneyDetailData(account.getOid());
		
		DetailDataRep rep = new DetailDataRep();
		
		for (Object[] obj : data) {
			if (null != obj) {
				rep.setDepositAmount(BigDecimalUtil.parseFromObject(obj[0]));
				rep.setWithdrawAmount(BigDecimalUtil.parseFromObject(obj[1]));
				rep.setWithdrawOnWayAmount(BigDecimalUtil.parseFromObject(obj[2]));
				rep.setTotalInvestAmount(BigDecimalUtil.parseFromObject(obj[3]));
				rep.setTotalOnWayInvest(BigDecimalUtil.parseFromObject(obj[4]));
				rep.setTotalRedeemAmount(BigDecimalUtil.parseFromObject(obj[5]));
				rep.setCouponAmount(BigDecimalUtil.parseFromObject(obj[6]));
				rep.setRedEnvelope(BigDecimalUtil.parseFromObject(obj[7]));
				rep.setBalance(BigDecimalUtil.parseFromObject(obj[8]));
			}
		}	
		return rep;
	}
	
	public String checkStatus2Ch(String checkStatus) {
		if (StringUtil.isEmpty(checkStatus)) {
			return StringUtil.EMPTY;
		}
		if (DetailCheckEntity.Detail_Check_Status_OK.equals(checkStatus)) {
			return "对账成功";
		}
		if (DetailCheckEntity.Detail_Check_Status_Failed.equals(checkStatus)) {
			return "对账失败";
		}
		return checkStatus;
	}
}
