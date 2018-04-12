package com.guohuai.mmp.publisher.holdapart.snapshot;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.sys.SysConstant;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class SnapshotServiceRequiresNew {
	@Autowired
	private SnapshotDao snapshotDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	
	/**
	 * 重新同步在派发收益日期之后已经拍过快照的数据
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public int reupdateAfterIncomeDateSnapshot(String productOid, Date incomeDate, Date afterIncomeDate){
		int result=this.snapshotDao.reupdateAfterIncomeDateSnapshot(productOid, incomeDate, afterIncomeDate);
		return result;
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public int reupdateAfterIncomeDateCashSnapshot(String productOid, Date incomeDate, Date afterIncomeDate){
		int result=this.snapshotDao.reupdateAfterIncomeDateCashSnapshot(productOid, incomeDate, afterIncomeDate);
		return result;
	}
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void flatWare(String orderOid, Date snapShotDate) {
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findOne(orderOid);
		BigDecimal volume = orderEntity.getOrderVolume();

		List<SnapshotEntity> shotList = this.findByInvestorBaseAccountAndProduct(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct(), snapShotDate);
		
		if (null == shotList || shotList.size() < 1) {
			return;
		}
		
		for (SnapshotEntity entity : shotList) {
			
			if (entity.getSnapshotVolume().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}
			
			if (entity.getSnapshotVolume().compareTo(volume) < 0) {
				BigDecimal shotv = entity.getSnapshotVolume();
				volume = volume.subtract(shotv);
				entity.setSnapshotVolume(SysConstant.BIGDECIMAL_defaultValue);
				continue;
			}

			if (entity.getSnapshotVolume().compareTo(volume) == 0) {
				entity.setSnapshotVolume(SysConstant.BIGDECIMAL_defaultValue);
				volume = SysConstant.BIGDECIMAL_defaultValue;
				break;
			}

			if (entity.getSnapshotVolume().compareTo(volume) > 0) {
				entity.setSnapshotVolume(entity.getSnapshotVolume().subtract(volume));
				volume = SysConstant.BIGDECIMAL_defaultValue;
				break;
			}
		}
		
		this.batchUpdate(shotList);
	}

	private void batchUpdate(List<SnapshotEntity> shotList) {
		this.snapshotDao.save(shotList);
	}

	private List<SnapshotEntity> findByInvestorBaseAccountAndProduct(InvestorBaseAccountEntity investorBaseAccount,
			Product product, Date snapShotDate) {
		return this.snapshotDao.findByInvestorBaseAccountAndProductAndSnapShotDate(investorBaseAccount, product, snapShotDate);
	}
}
