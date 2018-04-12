package com.guohuai.mmp.investor.baseaccount.log;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorInvestTradeOrderExtService;
import com.guohuai.mmp.investor.tradeorder.TradeOrderRep;
import com.guohuai.mmp.investor.tradeorder.TradeOrderReq;

@Service
@Transactional
public class TaskCouponLogServiceRequireNew {
	@Autowired
	private CouponLogService couponLogService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void requireNew(String oid) {
		CouponLogEntity entity = this.couponLogService.findByOid(oid);

		String status = CouponLogEntity.STATUS_SUCCESS;
		try {
			TradeOrderReq tradeOrderReq = null;
			// 使用体验金投资

			tradeOrderReq = investorBaseAccountService.useTastecoupon(entity.getUserOid());

			if (null != tradeOrderReq) {
				TradeOrderRep tradeOrderRep = this.investorInvestTradeOrderExtService.expGoldInvest(tradeOrderReq);
				if (-1 == tradeOrderRep.getErrorCode()) {
					status = CouponLogEntity.STATUS_FAILED;
				}
			} else {
				status = CouponLogEntity.STATUS_FAILED;
			}

		} catch (Exception e) {
			status = CouponLogEntity.STATUS_FAILED;
			e.printStackTrace();
		}
		entity.setSendedTimes(entity.getSendedTimes() + 1);
		entity.setNextNotifyTime(this.couponLogService.getNextNotifyTime(entity));
		entity.setStatus(status);
		this.couponLogService.saveEntity(entity);
	}
}
