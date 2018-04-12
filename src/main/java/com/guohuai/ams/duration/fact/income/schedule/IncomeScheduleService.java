package com.guohuai.ams.duration.fact.income.schedule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.duration.fact.income.IncomeAllocateBaseResp;
import com.guohuai.ams.duration.fact.income.IncomeAllocateCalcResp;
import com.guohuai.ams.duration.fact.income.IncomeAllocateForm;
import com.guohuai.ams.duration.fact.income.IncomeDistributionService;
import com.guohuai.ams.duration.fact.income.schedule.IncomeScheduleResp.IncomeScheduleRespBuilder;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.switchcraft.SwitchEntity;
import com.guohuai.ams.switchcraft.SwitchQueryCTRep;
import com.guohuai.ams.switchcraft.SwitchService;
import com.guohuai.basic.common.Clock;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.api.cms.CmsApi;
import com.guohuai.component.api.cms.ElementCode;
import com.guohuai.component.api.cms.ElementResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.msgment.MsgParam;
import com.guohuai.mmp.sms.SMSUtils;

@Service
@Transactional
public class IncomeScheduleService {
	
	private Logger logger = LoggerFactory.getLogger(IncomeScheduleService.class);
	@Autowired
	private IncomeScheduleDao incomeScheduleDao;
	@Autowired
	private IncomeScheduleApplyDao incomeScheduleApplyDao;
	@Autowired
	private IncomeDistributionService incomeDistributionService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private ProductService productService;
	@Autowired
	private SwitchService switchService;
	@Autowired
	private CmsApi cmsApi;
	@Autowired
	private SMSUtils sMSUtils;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public IncomeSchedule saveEntity(IncomeSchedule en){
		en.setCreateTime(new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis()));
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public IncomeSchedule updateEntity(IncomeSchedule en){
		en.setUpdateTime(new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis()));
		return this.incomeScheduleDao.save(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public IncomeSchedule findByOid(String oid){
		IncomeSchedule entity = this.incomeScheduleDao.findOne(oid);
		if(null == entity){
			// 资产池收益分配排期不存在!(CODE:60013)
			throw AMPException.getException(60013);
		}
		return entity;
	}
	
	/**
	 * 后台分页查询
	 * @return
	 */
	public PageResp<IncomeScheduleResp> queryPage(Specification<IncomeSchedule> spec, Pageable pageable) {
		Page<IncomeSchedule> enchs = this.incomeScheduleDao.findAll(spec, pageable);
		
		PageResp<IncomeScheduleResp> pageResp = new PageResp<>();		
		
		List<IncomeScheduleResp> list = new ArrayList<IncomeScheduleResp>();
		for (IncomeSchedule en : enchs) {
			IncomeScheduleResp rep = new IncomeScheduleRespBuilder()
					.oid(en.getOid())
					.assetPoolOid(en.getAssetPool().getOid())
					.basicDate(en.getBasicDate())
					.annualizedRate(en.getAnnualizedRate())
					.errorMes(en.getErrorMes())
					.status(en.getStatus())
					.updateTime(en.getUpdateTime())
					.build();
			list.add(rep);
		}
		
		pageResp.setTotal(enchs.getTotalElements());
		pageResp.setRows(list);
		return pageResp;
	}
	

	/**
	 * 获取详细
	 * @param oid
	 * @return
	 */
	public IncomeScheduleResp detail(String oid) {
		IncomeSchedule en = this.findByOid(oid);
		
		IncomeScheduleResp rep = new IncomeScheduleRespBuilder()
				.oid(en.getOid())
				.assetPoolOid(en.getAssetPool().getOid())
				.basicDate(en.getBasicDate())
				.annualizedRate(en.getAnnualizedRate())
				.errorMes(en.getErrorMes())
				.status(en.getStatus())
				.updateTime(en.getUpdateTime())
				.build();
		return rep;
	}

	/**
	 * 删除
	 * @param oid
	 */
	public void delete(String oid) {
		IncomeSchedule en = this.findByOid(oid);
		
		this.incomeScheduleDao.delete(en);
	}
	
	/** 检测该资产池该时间是否存在排期*/
	public void checkExist(String assetpoolOid, Date basicDate) {
		IncomeSchedule en = this.incomeScheduleDao.findByAssetPoolOidAndBasicDate(assetpoolOid, basicDate);
		if (en != null){
			// 资产池该日期收益分配排期已存在!(CODE:60014)
			throw AMPException.getException(60014);
		}
	}
	
	/** 排期定时任务 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void incomeSchedule() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_incomeDistributionSchedule)) {
			JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_incomeDistributionSchedule);
			try {
				incomeScheduleDo();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			}
			jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
			this.jobLogService.saveEntity(jobLog);
			this.jobLockService.resetJob(JobLockEntity.JOB_jobId_incomeDistributionSchedule);
		}
	}
	
	/** 排期定时任务 */
	public void incomeScheduleDo() {
		Date baseDate = DateUtil.formatUtilToSql(DateUtil.addDays(DateUtil.getCurrDate(), -1)); // 执行前一天的收益分配
		this.incomeScheduleApplyDao.changeLose(baseDate);	// 将排期申请中未审核的置为失效
		this.incomeScheduleDao.changeLose(baseDate);		// 将排期中未审核的置为失效
		
		List<IncomeSchedule> allList = this.incomeScheduleDao.findToActive(baseDate);	// 获取待执行的排期
		
		for (IncomeSchedule income : allList){
			try {
				// 获取资产池相关数据
				IncomeAllocateCalcResp resp = this.incomeDistributionService.getIncomeAdjustData(income.getAssetPool().getOid());
				IncomeAllocateCalcResp calcResp = this.incomeDistributionService.getTotalScaleRewardBenefit(income.getAssetPool().getOid(), DateUtil.format(baseDate));
				
				if (Double.parseDouble(calcResp.getProductTotalScale()) == 0){
					//该资产池产品规模为0!(CODE:60016)
					throw AMPException.getException(60016);
				}
				
				IncomeAllocateForm form = new IncomeAllocateForm();
				form.setAssetpoolOid(income.getAssetPool().getOid());
				form.setIncomeDistrDate(DateUtil.format(baseDate));
				form.setProductAnnualYield(income.getAnnualizedRate().toString());
				form.setProductTotalScale(calcResp.getProductTotalScale());
				form.setProductRewardBenefit(calcResp.getProductRewardBenefit());
				form.setProductCouponBenefit(calcResp.getProductCouponBenefit());
				
				//计算分配收益(Math.pow((productAnnualYield / 100 + 1), (1 / incomeCalcBasis)) - 1) * productTotalScale
				Double calcA = Math.pow(income.getAnnualizedRate().divide(new BigDecimal(100)).add(new BigDecimal(1)).doubleValue(), new BigDecimal(1).divide(new BigDecimal(resp.getIncomeCalcBasis()), 15, RoundingMode.HALF_UP).doubleValue());
				Double calcB = (calcA-1)* Double.parseDouble(calcResp.getProductTotalScale());
				BigDecimal productDistributionIncome = new BigDecimal(calcB);
				
				form.setProductDistributionIncome(productDistributionIncome.toString());
				
				IncomeAllocateBaseResp baseResp = incomeDistributionService.saveIncomeAdjust(form, StringUtil.EMPTY);	// 保存收益分配仿正常流程
				if (baseResp.getErrorCode() != 0){
					throw new Exception(baseResp.getErrorMessage());
				}
				
				BaseResp repponse = this.incomeDistributionService.auditPassIncomeAdjust(baseResp.getOid(), StringUtil.EMPTY);	// 自动审核通过收益分配仿正常流程
				if (baseResp.getErrorCode() != 0){
					throw new Exception(repponse.getErrorMessage());
				}
				
				income.setStatus(IncomeSchedule.STATUS_finish);
			} catch (Exception e) {
				income.setStatus(IncomeSchedule.STATUS_fail);
				income.setErrorMes(e.getMessage());
			}
			
			this.updateEntity(income);
		}
		
	}

	/**
	 * 获取某资产池当前应排期日期
	 * @param oid 资产池oid
	 * @return
	 */
	public IncomeScheduleResp getBaseDate(String oid) {
		IncomeScheduleResp resp = null;
		// 获取当前最大日期+1，如果不存在则返回当前日期，如果当前最大日期+1<当前日期则返回当前日期
		Date date = this.incomeScheduleDao.getShouldBaseDate(oid);
		if (date == null || DateUtil.compare_current_(date)){
			resp = new IncomeScheduleRespBuilder().shouldDate(DateUtil.format(DateUtil.getCurrDate())).build();
		}else{
			resp = new IncomeScheduleRespBuilder().shouldDate(DateUtil.format(DateUtil.addDay(date, 1))).build();
		}
		
		return resp;
	}

	
	/** 未设置自动发放收益通知定时任务 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void noticeSchedule() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_incomeDistributionNotice)) {
			JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_incomeDistributionNotice);
			try {
				noticeScheduleDo();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			}
			jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
			this.jobLogService.saveEntity(jobLog);
			this.jobLockService.resetJob(JobLockEntity.JOB_jobId_incomeDistributionNotice);
		}
	}

	/** 未设置自动发放收益通知定时任务 */
	public void noticeScheduleDo() {
		SwitchQueryCTRep switchRep = switchService.findCode(SwitchEntity.SWITCH_code_IncomeDistriNotice, null, null);
		if (switchRep == null || switchRep.getStatus().equals(SwitchEntity.SWITCH_Status_disable)){
			logger.info("发放收益提醒未启用或设置错误！");
			return;
		}
		
		ElementResp eleResp = cmsApi.getElement(ElementCode.ELEMENT_code_incomeDistriNotice);
		if (eleResp == null || eleResp.getData() == null || eleResp.getData().getContent() == null || eleResp.getData().getContent().isEmpty()){
			logger.info("发放收益提醒未配置手机号，将不提醒！");
			return;
		}
		
		String[] phones = eleResp.getData().getContent().split(",");
		if (phones == null || phones.length == 0){
			logger.info("发放收益提醒未配置手机号，将不提醒！");
			return;
		}
		for (String phone : phones){
			if (!isMobile(phone)){
				logger.error("发放收益提醒手机号格式出错，请修改配置，例如：13245678999,13345678999");
				return;
			}
		}
		
		Date baseDate = DateUtil.formatUtilToSql(DateUtil.addDays(DateUtil.getCurrDate(), -1)); // 执行前一天的收益分配
		
		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("type").get("oid").as(String.class), Product.TYPE_Producttype_02);
				Predicate b = cb.equal(root.get("state").as(String.class), Product.STATE_Durationing);
				return cb.and(a,b);
			}
		};
		
		List<Product> productList = productService.findAll(spec);
		
		Specification<IncomeSchedule> spe = new Specification<IncomeSchedule>() {
			@Override
			public Predicate toPredicate(Root<IncomeSchedule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("basicDate").as(Date.class), baseDate);
				Predicate b = cb.equal(root.get("status").as(String.class), IncomeSchedule.STATUS_pass);
				Predicate c = cb.equal(root.get("status").as(String.class), IncomeSchedule.STATUS_finish);
				return cb.and(a,cb.or(b,c));
			}
		};
		List<IncomeSchedule> incomeList = this.incomeScheduleDao.findAll(spe);	// 获取待执行的排期
		Set<String> assetOids = new HashSet<>();	// 当日自动排期发放收益的资产池
		for(IncomeSchedule income:incomeList){
			assetOids.add(income.getAssetPool().getOid());
		}

		for(Product pro:productList){
			if (!assetOids.contains(pro.getPortfolio().getOid())){
				// 如果当日活期没有自动发放收益则通知
				String[] arrays = {pro.getPortfolio().getName(), pro.getFullName()};
				for (String phone : phones){
					this.sMSUtils.sendSMS(phone, MsgParam.msgIncomedistrinotice.toString(), arrays);
				}
			}
		}
	}
	
	public static boolean isMobile(String mobiles) {
	    Pattern p = Pattern.compile("^1\\d{10}");
	    Matcher m = p.matcher(mobiles);
	    return m.matches();
	}
}
