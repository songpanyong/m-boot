package com.guohuai.mmp.ope.schedule;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bank.BankEntity;
import com.guohuai.mmp.investor.bank.BankService;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderService;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.ope.api.OpeScheduleListRep;
import com.guohuai.mmp.ope.api.OpeScheduleRep;
import com.guohuai.mmp.ope.api.OpeSelectApiService;
import com.guohuai.mmp.ope.time.OpeTime;
import com.guohuai.mmp.ope.time.OpeTimeService;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;

@Service
public class OpeService {
	private Logger log = LoggerFactory.getLogger(OpeService.class);
	@Autowired
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private OpeSelectApiService opeSelectApiService;
	@Autowired
	private OpeBaseService opeBaseService;
//	@Autowired
//	private OpeUserCenterApi opeUserCenterApi;
	@Autowired
	private OpeService opeService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private OpeTimeService opeTimeService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private BankService bankService;
	
	// 扫描注册用户
	public void collectNoCardSchedule() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_opeschedule);
		try {
			long nocardtime = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_NOCARDTIME);
			
			OpeScheduleListRep rep = opeService.collectNoCardSchedule(nocardtime, null, null);
			if (rep != null && rep.getList() != null && !rep.getList().isEmpty()){
				Long startTime = nocardtime;
						
				for (OpeScheduleRep it : rep.getList()){
					if (it != null){
						try {
							opeSelectApiService.createNoCard(it.getUserOid(), it.getPhone(), it.getSource(), it.getCreateTime());
							
							if (startTime < it.getCreateTime()){
								startTime = it.getCreateTime();
							}
						} catch (Exception e) {
							log.info("运营查询：扫描注册用户单个处理出错，错误信息："+e.getMessage());
							jobLog.setJobMessage(AMPException.getStacktrace(e));
							jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
							break;
						}
					}
				}
				
				opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_NOCARDTIME, startTime);
			}
		}catch (Exception e) {
			log.info("运营查询：扫描注册用户出错，错误信息："+e.getMessage());
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
	}
	
	// 扫描绑卡用户
	public void collectBindCardSchedule() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_opeschedule);
		try {
			long bindtime = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_BINDTIME);
			
			OpeScheduleListRep rep = opeService.collectBindCardSchedule(bindtime, null, null);
			if (rep != null && rep.getList() != null && !rep.getList().isEmpty()){
				long startTime = bindtime;
				for (OpeScheduleRep it : rep.getList()){
					if (it != null){
						try {
							opeSelectApiService.createFullFailCard(it.getUserOid(), it.getPhone(), it.getSource(), it.getName(), it.getCreateTime());
							
							if (startTime < it.getCreateTime()){
								startTime = it.getCreateTime();
							}
						} catch (Exception e) {
							log.info("运营查询：扫描用户绑卡单个处理出错，错误信息："+e.getMessage());
							jobLog.setJobMessage(AMPException.getStacktrace(e));
							jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
							break;
						}
					}
				}
				
				opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_BINDTIME, startTime);
			}
		}catch (Exception e) {
			log.info("运营查询：扫描绑卡用户出错，错误信息："+e.getMessage());
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
	}
	
	// 充值扫描
	public void collectFailRechargeSchedule() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_opeschedule);
		try {
			long failrechargetime = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_FAILRECHARGETIME);
			long startTime = failrechargetime;
			List<PayLogEntity> list = opeBaseService.queryPayLogEntity(failrechargetime, null, null);
			
			if (list != null && !list.isEmpty()){
				for (PayLogEntity en : list){
					try {
						InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeNoExc(en.getOrderCode());
						try {
							if (bankOrder != null && bankOrder.getOrderCode()!=null && bankOrder.getOrderType().equals(InvestorBankOrderEntity.BANKORDER_orderType_deposit)){
								InvestorBaseAccountEntity user = opeBaseService.getUserByBankOrderCode(en.getOrderCode());
								if (en.getErrorCode()==0){
									// 充值成功
									opeSelectApiService.successRecharge(user.getOid(), user.getPhoneNum(), user.getRealName(), user.getChannelid(), bankOrder.getCreateTime().getTime(), en.getCreateTime().getTime());
								}else{
									// 充值失败
									opeSelectApiService.failRecharge(user.getOid(), user.getPhoneNum(), user.getRealName(), user.getChannelid(), en.getErrorMessage(), bankOrder.getCreateTime().getTime());
								}
							}
						}catch (Exception e) {
							log.info("运营查询：扫描充值日志单个处理访问uc或保存表出错，错误信息："+e.getMessage());
							jobLog.setJobMessage(AMPException.getStacktrace(e));
							jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
							break;
						}
						
						if (startTime < en.getCreateTime().getTime()){
							startTime = en.getCreateTime().getTime();
						}
					}catch (Exception e) {
						log.info("运营查询：扫描充值日志单个处理出错，错误信息："+e.getMessage());
						continue;
					}
				}
				
				opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_FAILRECHARGETIME, startTime);
			}
		}catch (Exception e) {
			log.info("运营查询：扫描充值日志出错，错误信息："+e.getMessage());
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
	}

	// 扫描已购买
	public void collectNoBuySchedule() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_opeschedule);
		try {
			long nobuytime = opeTimeService.getTimeByName(OpeTime.OPETIME_NAME_NOBUYTIME);
			long startTime = nobuytime;
			List<InvestorTradeOrderEntity> list = opeBaseService.queryInvestorTradeOrderEntity(nobuytime, null, null);
			
			if (list != null && !list.isEmpty()){
				for (InvestorTradeOrderEntity order : list){
					try {
						if (order != null && order.getOrderCode()!=null){
							String userOid = opeBaseService.getUserOidByTradeOrderCode(order.getOrderCode());
							
							if (userOid == null || userOid.isEmpty()){
								throw new Exception("InvestorBaseAccount错误：UserOid不存在，InvestorTradeOrderEntity的oid为："+order.getOid());
							}
							
							opeSelectApiService.successbuy(userOid, order.getCreateTime().getTime());
						}
						
						if (startTime < order.getCreateTime().getTime()){
							startTime = order.getCreateTime().getTime();
						}
					}catch (Exception e) {
						log.info("运营查询：扫描交易订单中单个处理出错，错误信息："+e.getMessage());
						jobLog.setJobMessage(AMPException.getStacktrace(e));
						jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
						break;
					}
				}
				
				opeTimeService.setOpeTime(OpeTime.OPETIME_NAME_NOBUYTIME, startTime);
			}
		}catch (Exception e) {
			log.info("运营查询：扫描交易订单出错，错误信息："+e.getMessage());
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
	}
	
	// 线程扫描注册用户，添加到未绑卡列表
	public OpeScheduleListRep collectNoCardSchedule(final long notime, Integer page, Integer rows) {
		List<OpeScheduleRep> repList = new ArrayList<>();
		try {
			Specification<InvestorBaseAccountEntity> spec = new Specification<InvestorBaseAccountEntity>() {
				@Override
				public Predicate toPredicate(Root<InvestorBaseAccountEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Predicate a = cb.greaterThan(root.get("createTime").as(Timestamp.class), new Timestamp(notime));
					query.where(cb.and(a));
					query.orderBy(cb.asc(root.get("createTime")));
					return query.getRestriction();
				}
			};
			
			
			List<InvestorBaseAccountEntity> list = new ArrayList<>();
			if (page == null || rows == null){
				list = investorBaseAccountService.findAll(spec);
			}else{
				Pageable pageable = new PageRequest(page - 1, rows);
				Page<InvestorBaseAccountEntity> enPage = investorBaseAccountService.findPage(spec, pageable);
				list = enPage.getContent();
			}
			
			if (list != null && !list.isEmpty()){
				for (InvestorBaseAccountEntity en : list){
					OpeScheduleRep resp = new OpeScheduleRep();
					resp.setUserOid(en.getOid());
					resp.setPhone(en.getPhoneNum());
					resp.setSource(en.getChannelid());
					resp.setCreateTime(en.getCreateTime().getTime());
					
					repList.add(resp);
				}
			}
		} catch (Exception e) {
			log.info("运营查询：扫描注册用户出错，错误信息："+e.getMessage());
		}
		OpeScheduleListRep rep = new OpeScheduleListRep();
		rep.setList(repList);
		return rep; 
	}

	// 线程扫描绑卡信息，添加到绑卡失败表为绑卡成功
	@Transactional
	public OpeScheduleListRep collectBindCardSchedule(final long bindtime, Integer page, Integer rows) {
		List<OpeScheduleRep> repList = new ArrayList<>();
		try {
			Specification<BankEntity> spec = new Specification<BankEntity>() {
				@Override
				public Predicate toPredicate(Root<BankEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Predicate a = cb.greaterThan(root.get("createTime").as(Timestamp.class), new Timestamp(bindtime));
					query.where(cb.and(a));
					query.orderBy(cb.asc(root.get("createTime")));
					return query.getRestriction();
				}
			};
			
			List<BankEntity> list = new ArrayList<>();
			if (page == null || rows == null){
				list = bankService.findAll(spec);
			}else{
				Pageable pageable = new PageRequest(page - 1, rows);
				Page<BankEntity> enPage = bankService.findPage(spec, pageable);
				list = enPage.getContent();
			}
			
			if (list != null && !list.isEmpty()){
				for (BankEntity en : list){
					OpeScheduleRep rep = new OpeScheduleRep();
					rep.setUserOid(en.getInvestorBaseAccount().getOid());
					rep.setPhone(en.getInvestorBaseAccount().getPhoneNum());
					rep.setSource(en.getInvestorBaseAccount().getChannelid());
					rep.setName(en.getName());
					rep.setCreateTime(en.getCreateTime().getTime());
					
					repList.add(rep);
				}
			}
		} catch (Exception e) {
			log.info("运营查询：扫描用户绑卡出错，错误信息："+e.getMessage());
		}
		OpeScheduleListRep rep = new OpeScheduleListRep();
		rep.setList(repList); 
		return rep;
	}
}
