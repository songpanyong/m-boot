package com.guohuai.ams.illiquidAsset;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.DateUtil;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IlliquidAssetScheduleService {

	@Autowired
	private IlliquidAssetDao illiquidAssetDao;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;

	/**
	 * 调整资产状态
	 * 
	 * @param basicDate
	 *            基准日
	 */
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void updateState(Date basicDate) {

		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_updateIlliquidState.getJobId())) {
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_updateIlliquidState.getJobId());
			try {
				List<IlliquidAsset> assets = this.illiquidAssetDao.findByLifeStateIn(new String[] { "B4_COLLECT", "COLLECTING", "SETUP", "VALUEDATE", "UNSETUP" });

				if (null != assets && assets.size() > 0) {
					for (Iterator<IlliquidAsset> iterator = assets.iterator(); iterator.hasNext();) {
						IlliquidAsset asset = iterator.next();
						try {
							this.updateState(asset, basicDate);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			}
			jobLog.setBatchEndTime(new Timestamp(System.currentTimeMillis()));
			this.jobLogService.saveEntity(jobLog);
			this.jobLockService.resetJob(JobEnum.JOB_jobId_updateIlliquidState.getJobId());
		}

	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public int updateState(IlliquidAsset asset, Date basicDate) {
		switch (asset.getLifeState()) {
		case "B4_COLLECT":
			return this.updateStateOf_B4_COLLECT(asset, basicDate);
		case "COLLECTING":
			return this.updateStateOf_COLLECTING(asset, basicDate);
		case "SETUP":
			return this.updateStateOf_SETUP(asset, basicDate);
		case "VALUEDATE":
			return this.updateStateOf_VALUEDATE(asset, basicDate);
		case "UNSETUP":
			return this.updateStateOf_UNSETUP(asset, basicDate);
		default:
			return 0;
		}
	}

	// 未开始募集的, 判断系统日期和募集开始日期, 如果系统日期 >= 募集开始日期, 则调整标的状态为募集期
	@Transactional
	private int updateStateOf_B4_COLLECT(IlliquidAsset asset, Date basicDate) {
		if (null == asset.getCollectStartDate()) {
			throw new GHException("未开始募集的标的, 募集开始日不可为空.");
		} else {
			if (DateUtil.ge(basicDate, asset.getCollectStartDate())) {
				return this.illiquidAssetDao.updateLifeState(asset.getOid(), IlliquidAsset.ILLIQUIDASSET_LIFESTATE_COLLECTING);
			} else {
				return 0;
			}
		}
	}

	// 募集期的, 判断系统日期和募集结束日期, 如果系统日期 > 募集结束日期, 则调整标的状态为募集结束
	@Transactional
	private int updateStateOf_COLLECTING(IlliquidAsset asset, Date basicDate) {
		if (null == asset.getCollectEndDate()) {
			throw new GHException("已开始募集的标的, 募集截止日不可为空.");
		} else {
			if (DateUtil.gt(basicDate, asset.getCollectEndDate())) {
				return this.illiquidAssetDao.updateLifeState(asset.getOid(), IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVER_COLLECT);
			} else {
				return 0;
			}
		}
	}

	// 已成立的, 判断系统日期和起息日, 如果系统日期 >= 起息日, 则调整标的状态为已起息
	@Transactional
	private int updateStateOf_SETUP(IlliquidAsset asset, Date basicDate) {
		if (null == asset.getRestStartDate()) {
			throw new GHException("已成立的标的, 收益起始日不可为空.");
		} else {
			if (DateUtil.ge(basicDate, asset.getRestStartDate())) {
				return this.illiquidAssetDao.updateLifeState(asset.getOid(), IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE);
			} else {
				return 0;
			}
		}
	}

	// 已起息的, 判断系统日期和止息日, 如果系统日期 > 止息日, 则调整标的状态为已到期
	@Transactional
	private int updateStateOf_VALUEDATE(IlliquidAsset asset, Date basicDate) {
		if (null == asset.getRestEndDate()) {
			throw new GHException("已成立的标的, 收益截止日不可为空.");
		} else {
			if (DateUtil.ge(basicDate, asset.getRestEndDate())) {
				return this.illiquidAssetDao.updateLifeState(asset.getOid(), IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVER_VALUEDATE);
			} else {
				return 0;
			}
		}
	}

	@Transactional
	private int updateStateOf_UNSETUP(IlliquidAsset asset, Date basicDate) {
		if (null == asset.getSetDate()) {
			return 0;
		} else {
			if (DateUtil.gt(basicDate, asset.getSetDate())) {
				return this.illiquidAssetDao.updateLifeState(asset.getOid(), IlliquidAsset.ILLIQUIDASSET_LIFESTATE_SETUP);
			}
		}

		return 0;
	}

}
