package com.guohuai.ams.illiquidAsset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.repaymentPlan.IlliquidAssetRepaymentPlan;
import com.guohuai.ams.illiquidAsset.repaymentPlan.IlliquidAssetRepaymentPlanDao;
import com.guohuai.ams.illiquidAsset.repaymentPlan.IlliquidAssetRepaymentPlanService;
import com.guohuai.ams.portfolio.scopes.ScopesDao;
import com.guohuai.ams.portfolio.scopes.ScopesEntity;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.proactive.ProActive.Execution;
import com.guohuai.basic.component.proactive.ProActive.Result;
import com.guohuai.basic.component.proactive.ProActiveAware;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.ext.illiquidAsset.IlliquidAssetAdd;
import com.guohuai.ext.illiquidAsset.IlliquidAssetAddIsSnRepeat;

@Service
@Transactional
public class IlliquidAssetService {

	@Autowired
	private IlliquidAssetDao illiquidAssetDao;

	@Autowired
	private ScopesDao scopesDao;

	@Autowired
	private IlliquidAssetRepaymentPlanService illiquidAssetRepaymentPlanService;
	@Autowired
	private IlliquidAssetRepaymentPlanDao illiquidAssetRepaymentPlanDao;
	@Autowired
	private ProActiveAware proActiveAware;

	/**
	 * 新增
	 * 
	 * @param en
	 * @return
	 */
	@Transactional
	public IlliquidAsset saveEntity(IlliquidAsset en) {
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}

	/**
	 * 修改
	 * 
	 * @param en
	 * @return
	 */
	@Transactional
	public IlliquidAsset updateEntity(IlliquidAsset en) {
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.illiquidAssetDao.save(en);
	}

	/**
	 * 根据OID查询
	 * 
	 * @param oid
	 * @return
	 */
	public IlliquidAsset findByOid(String oid) {
		IlliquidAsset entity = this.illiquidAssetDao.findOne(oid);
		if (null == entity) {
			throw GHException.getException(70000);
		}
		return entity;
	}

	public IlliquidAsset findIlliquidAssetByOid(String oid) {
		IlliquidAsset entity = this.illiquidAssetDao.findOne(oid);
		return entity;
	}

	/**
	 * 后台分页查询
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Page<IlliquidAsset> queryByPage(Specification<IlliquidAsset> spec, Pageable pageable) {
		Page<IlliquidAsset> enchs = this.illiquidAssetDao.findAll(spec, pageable);

		return enchs;
	}

	/**
	 * 后台分页查询
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public IlliquidAssetListResp queryPage(Specification<IlliquidAsset> spec, Pageable pageable) {
		Page<IlliquidAsset> enchs = this.illiquidAssetDao.findAll(spec, pageable);
		IlliquidAssetListResp pageResp = new IlliquidAssetListResp(enchs);

		return pageResp;
	}

	/**
	 * 新建投资标的
	 * 
	 * @param form
	 * @return
	 */
	public IlliquidAsset saveIlliquidAsset(IlliquidAssetForm form, String operator) {
		// 添加添加标的的扩展业务调用
		if(this.proActiveAware.achieved(IlliquidAssetAddIsSnRepeat.class)){
			List<Result<IlliquidAsset>> snRepeat = this.proActiveAware.invoke(new Execution<IlliquidAssetAddIsSnRepeat, IlliquidAsset>() {
				@Override
				public IlliquidAsset execute(IlliquidAssetAddIsSnRepeat arg0) {
					return arg0.isSnRepeat(form.getSn());
				}
			}, IlliquidAssetAddIsSnRepeat.class);
			if(snRepeat.get(0).getResult() != null){
				throw new AMPException("标的编号重复");
			}
			return snRepeat.get(0).getResult();
		} else if (this.proActiveAware.achieved(IlliquidAssetAdd.class)) {
			 List<Result<IlliquidAsset>> resultSet = this.proActiveAware.invoke(new Execution<IlliquidAssetAdd, IlliquidAsset>() {
				@Override
				public IlliquidAsset execute(IlliquidAssetAdd arg0) {
					return arg0.saveIlliquidAssetExt(form, operator);
				}
			}, IlliquidAssetAdd.class);
			
			return resultSet.get(0).getResult();
		} else {
			IlliquidAsset en = createIlliquidAsset(form, operator);

			// this.calculateRiskRate(en);

			/* 计算标的风险开始 */
			// this.calculateRiskRate(en);

			return en;
		}
	}

	//	private IlliquidAsset calculateRiskRate(IlliquidAsset entity) {
	//		String riskOption = entity.getRiskOption(); // 风险配置对象
	//		if (StringUtils.isNotBlank(riskOption)) {
	//			RiskIndicateCollectForm collForm = JSON.parseObject(riskOption, RiskIndicateCollectForm.class);
	//			collForm.setRelative(entity.getOid()); // 把投资标的的oid存入relative
	//			List<RiskIndicateCollectResp> list = riskIndicateCollectService.save(collForm);
	//			// 计算公式: sum(CollectScore)
	//			int sum = 0; // 标的信用等级评分总和
	//			BigDecimal weight = null; // 标的信用等级系数
	//			if (null != list) {
	//				for (RiskIndicateCollectResp rc : list) {
	//					sum += rc.getCollectScore();
	//				}
	//				log.debug("投资标的的风险总分: " + sum);
	//				entity.setCollectScore(sum);
	//				CCPWarrantor cCPWarrantor = cCPWarrantorService.getByScoreBetween(sum); // 根据标的风险总分获取[标的信用等级系数]
	//				if (null != cCPWarrantor) {
	//					weight = cCPWarrantor.getWeight();
	//					log.debug("投资标的id=" + entity.getOid() + "的信用等级对象为: " + JSON.toJSONString(cCPWarrantor));
	//				} else {
	//					log.warn("找不到风险总分:" + sum + "对应的信用等级系数区间");
	//				}
	//			}
	//			log.debug("投资标的id=" + entity.getOid() + "的信用等级系数为: " + weight);
	//			entity.setCollectScoreWeight(weight);
	//
	//			// 根据[标的信用等级系数]计算本标的下所有项目的[项目系数]
	//			BigDecimal riskRate = illiquidAssetProjectService.calculateInvestmentRisk(entity); // 返回标的下面所有项目的最大的风险系数
	//
	//			// 取max(各个项目的[项目系数])作为标的的[风险系数]
	//			// riskRate = projectService.getMaxRiskFactor(entity.getOid());
	//			entity.setRiskRate(riskRate);
	//			log.debug("投资标的id=" + entity.getOid() + "的风险系数为: " + riskRate);
	//		}
	//		return entity;
	//	}

	/**
	 * form转entity
	 * 
	 * @param form
	 * @return
	 */
	public IlliquidAsset createIlliquidAsset(IlliquidAssetForm form, String operator) {
		IlliquidAsset entity = new IlliquidAsset();
		try {
			BeanUtils.copyProperties(form, entity);
			entity.setOid(form.getOid());
            entity.setHoldIncome(BigDecimal.ZERO);
            entity.setHoldShare(BigDecimal.ZERO);
            entity.setExpIncome(BigDecimal.ZERO);
            entity.setApplyAmount(BigDecimal.ZERO);
            entity.setLockupCapital(BigDecimal.ZERO);
            entity.setLockupIncome(BigDecimal.ZERO);
            
            
			// 资产规模 万转元
			if (form.getRaiseScope() != null) {
				BigDecimal yuan = form.getRaiseScope().multiply(new BigDecimal(10000));
				entity.setRaiseScope(yuan);
			}

			// 票面金额 万转元
			if (form.getTicketValue() != null) {
				BigDecimal yuan = form.getTicketValue().multiply(new BigDecimal(10000));
				entity.setTicketValue(yuan);
			}
			// 收购金额 万转元
			if (form.getPurchaseValue() != null) {
				BigDecimal yuan = form.getPurchaseValue().multiply(new BigDecimal(10000));
				entity.setPurchaseValue(yuan);
			}
			// 起购金额 万转元
			if (form.getStarValue() != null) {
				BigDecimal yuan = form.getStarValue().multiply(new BigDecimal(10000));
				entity.setStarValue(yuan);
			}

			// 预计年化收益 百分比转小数
			if (form.getExpAror() != null) {
				BigDecimal decimal = form.getExpAror().divide(new BigDecimal(100));
				entity.setExpAror(decimal);
			}
			// 逾期收益 百分比转小数
			if (form.getOverdueRate() != null) {
				BigDecimal decimal = form.getOverdueRate().divide(new BigDecimal(100));
				entity.setOverdueRate(decimal);
			}

			// 募集期收益 百分比转小数
			if (form.getCollectIncomeRate() != null) {
				BigDecimal decimal = form.getCollectIncomeRate().divide(new BigDecimal(100));
				entity.setCollectIncomeRate(decimal);
			}

			String lifeUnit = form.getLifeUnit();// day month year

			String accType = form.getType();
			// 票据类 要算出存续期 按天
			if (StringUtil.in(accType, "TARGETTYPE_16", "TARGETTYPE_15")) {
				entity.setLife(new Long(DateUtil.getDifferentSqlDays(form.getRestEndDate(), form.getRestStartDate())).intValue());
				entity.setLifeUnit("day");
			}
			// 供应链金融产品类 要算出存续期 按天
			if (StringUtil.in(accType, "TARGETTYPE_19")) {
				entity.setLife(new Long(DateUtil.getDifferentSqlDays(form.getRestEndDate(), form.getSetDate())).intValue() - 1);
				entity.setLifeUnit("day");
			}

			if (lifeUnit != null) {
				int life = form.getLife();
				int lifed = 0;
				if ("month".equalsIgnoreCase(lifeUnit))
					lifed = life * 30; // 一个月以30天算
				else if ("year".equalsIgnoreCase(lifeUnit))
					lifed = life * 360; // 一年以360天算
				else
					lifed = life;
				entity.setLifed(lifed);
			}

			java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

			// 是否生成还款计划
			boolean repayment = false;

			// 信托类
			if (StringUtil.in(accType, "TARGETTYPE_05", "TARGETTYPE_06", "TARGETTYPE_07", "TARGETTYPE_04", "TARGETTYPE_03", "TARGETTYPE_12", "TARGETTYPE_13", "TARGETTYPE_01", "TARGETTYPE_02", "TARGETTYPE_14")) {
				if (DateUtil.ge(now, form.getCollectEndDate())) {
					// 设置状态为募集结束
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVER_COLLECT);
				} else if (DateUtil.ge(now, form.getCollectStartDate())) {
					// 设置状态为募集中
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_COLLECTING);
				} else {
					// 设置状态为未开始募集
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_B4_COLLECT);
				}
				// 信托类的不生成还款计划， 在点击“成立”按钮时，才生成还款计划
			}
			// 票据类
			else if (StringUtil.in(accType, "TARGETTYPE_16", "TARGETTYPE_15")) {
				if (DateUtil.ge(now, form.getRestStartDate())) {
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE);
				} else {
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_UNSETUP);
				}

				entity.setSetDate(form.getRestStartDate());
				entity.setRaiseScope(entity.getPurchaseValue());
				entity.setAccrualDate(0);
				// 算年化收益率
				BigDecimal expAror = entity.getTicketValue().subtract(entity.getPurchaseValue()).divide(entity.getPurchaseValue(), 8, RoundingMode.HALF_UP).divide(new BigDecimal(entity.getLife()), 8, RoundingMode.HALF_UP).multiply(new BigDecimal(360)).setScale(6, RoundingMode.HALF_UP);
				entity.setExpAror(expAror);

				repayment = true;
			}
			// 消费金融类 及 债权及债权收益类
			else if (StringUtil.in(accType, "TARGETTYPE_17", "TARGETTYPE_18")) {
				int life = form.getLife();
				int lifed = 0;
				if (StringUtil.in(accType, "TARGETTYPE_17")){
					entity.setLifeUnit("day");
					lifed = life;
				}else if(StringUtil.in(accType, "TARGETTYPE_18")){
					entity.setLifeUnit("month");
					lifed = life * 30;
				}
				entity.setLifed(lifed);
				entity.setPurchaseValue(entity.getRaiseScope());
				if (form.getIsSetup().equals("YES")) {
					// 如果选择已成立，那么页面上输入的成立日期，只能是历史某一天
					entity.setRestStartDate(DateUtil.addSQLDays(form.getSetDate(), 1));

					Calendar c = Calendar.getInstance();
					c.clear();
					c.setTimeInMillis(entity.getRestStartDate().getTime());

					if (entity.getLifeUnit().equals("year")) {
						c.add(Calendar.YEAR, form.getLife());
					} else if (entity.getLifeUnit().equals("month")) {
						c.add(Calendar.MONTH, form.getLife());
					} else {
						c.add(Calendar.DATE, form.getLife());
					}

					entity.setRestEndDate(new java.sql.Date(c.getTimeInMillis()));

					if (DateUtil.ge(now, entity.getRestStartDate())) {
						entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE);

					} else {
						if (DateUtil.lt(now, entity.getSetDate())) {
							entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_UNSETUP);
						} else {
							entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_SETUP);
						}
					}

					repayment = true;
				} else {
					// 先清空历史的还款计划
					String isOid = entity.getOid();
					if(null != isOid){
						List<IlliquidAssetRepaymentPlan> hplan = this.illiquidAssetRepaymentPlanDao.findByIlliquidAsset(entity);
						if (null != hplan && hplan.size() > 0) {
							this.illiquidAssetRepaymentPlanDao.delete(hplan);
						}
					}
				
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_UNSETUP);
					// 未成立的标的， 页面要有“成立”按钮， 点击时，生成还款计划
				}
			}
			else if (StringUtil.in(accType, "TARGETTYPE_08")) {
				
				entity.setPurchaseValue(entity.getRaiseScope());
				if (form.getIsSetup().equals("YES")) {
					// 如果选择已成立，那么页面上输入的成立日期，只能是历史某一天
					entity.setRestStartDate(form.getSetDate());

					Calendar c = Calendar.getInstance();
					c.clear();
					c.setTimeInMillis(entity.getRestStartDate().getTime());

					if (entity.getLifeUnit().equals("year")) {
						c.add(Calendar.YEAR, form.getLife());
					} else if (entity.getLifeUnit().equals("month")) {
						c.add(Calendar.MONTH, form.getLife());
					} else {
						c.add(Calendar.DATE, form.getLife());
					}

					entity.setRestEndDate(new java.sql.Date(c.getTimeInMillis()));

					if (DateUtil.ge(now, entity.getRestStartDate())) {
						entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE);

					} else {
						
							entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_SETUP);
						
					}

					repayment = true;
				} else {
					// 先清空历史的还款计划
					String isOid = entity.getOid();
					if(null != isOid){
						List<IlliquidAssetRepaymentPlan> hplan = this.illiquidAssetRepaymentPlanDao.findByIlliquidAsset(entity);
						if (null != hplan && hplan.size() > 0) {
							this.illiquidAssetRepaymentPlanDao.delete(hplan);
						}
					}
				
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_UNSETUP);
					// 未成立的标的， 页面要有“成立”按钮， 点击时，生成还款计划
				}
			}
			// 供应链金融产品类
			else if (StringUtil.in(accType, "TARGETTYPE_19")) {
				entity.setPurchaseValue(entity.getRaiseScope());
				entity.setRestStartDate(DateUtil.addSQLDays(form.getSetDate(), 1));
				if (DateUtil.ge(now, entity.getRestStartDate())) {
					entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE);
				} else {
					if (DateUtil.lt(now, entity.getSetDate())) {
						entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_UNSETUP);
					} else {
						entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_SETUP);

					}
				}

				repayment = true;

			} else if (StringUtil.in(accType, "TARGETTYPE_20")) {
				entity.setLifeUnit("month");
				entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_UNSETUP);
				entity.setPurchaseValue(entity.getRaiseScope());
				repayment = false;

			}else {
				throw new GHException("未知的标的类型:" + accType);
			}
			Integer days = entity.getContractDays();
			if (days == null) {
				entity.setContractDays(360);
			}

			entity.setState(IlliquidAsset.ILLIQUIDASSET_STATE_CREATE);
			entity.setOperator(operator);
			entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
			entity.setCreator(operator);
			entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));

			entity = this.illiquidAssetDao.save(entity);

			if (repayment) {
				this.illiquidAssetRepaymentPlanService.repayMentPlanSchedule(entity, operator);
			}
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * 修改投资标的
	 * 
	 * @param entity
	 * @param operator
	 * @return
	 */
	public IlliquidAsset updateIlliquidAsset(IlliquidAssetForm form, String oprater) {
		IlliquidAsset investment = this.findByOid(form.getOid());
		if (!IlliquidAsset.ILLIQUIDASSET_STATE_CREATE.equals(investment.getState()) && !IlliquidAsset.ILLIQUIDASSET_STATE_REJECT.equals(investment.getState())) {
			// 标的状态不是待预审或驳回不能提交预审
			throw new RuntimeException();
		}
		IlliquidAsset temp = createIlliquidAsset(form, oprater);
		temp.setLifeState(investment.getLifeState());
		;
		temp.setState(investment.getState());
		temp.setCreateTime(investment.getCreateTime());
		temp.setCreator(investment.getCreator());

		IlliquidAsset entity = this.updateEntity(temp);

		// this.calculateRiskRate(entity);

		return entity;
	}

	/**
	 * 提交预审
	 * 
	 * @param oid
	 * @param operator
	 * @return
	 */
	public IlliquidAsset comitCheck(String oid, String operator) {
		IlliquidAsset investment = this.findByOid(oid);
		if (!IlliquidAsset.ILLIQUIDASSET_STATE_CREATE.equals(investment.getState()) && !IlliquidAsset.ILLIQUIDASSET_STATE_REJECT.equals(investment.getState())) {
			// 标的状态不是待预审或驳回不能提交预审
			throw new RuntimeException();
		}

		investment.setState(IlliquidAsset.ILLIQUIDASSET_STATE_AUDITING);
		investment.setOperator(operator);

		investment = this.updateEntity(investment);
		return investment;
	}

	/**
	 * 标的作废
	 * 
	 * @param oid
	 * @param operator
	 * @return
	 */
	public IlliquidAsset invalid(String oid, String operator) {
		IlliquidAsset investment = this.findByOid(oid);
		if (!IlliquidAsset.ILLIQUIDASSET_STATE_CREATE.equals(investment.getState()) && !IlliquidAsset.ILLIQUIDASSET_STATE_REJECT.equals(investment.getState())
				&& !IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVER_VALUEDATE.equals(investment.getLifeState()) && !IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVERDUE_REPAYMENTS.equals(investment.getLifeState())
				&& !IlliquidAsset.ILLIQUIDASSET_LIFESTATE_REPAYMENTS.equals(investment.getLifeState())) {
			// 标的状态不是待预审或驳回不能提交预审
			throw new RuntimeException();
		}

		investment.setState(IlliquidAsset.ILLIQUIDASSET_STATE_INCALID);
		investment.setOperator(operator);
		investment = this.updateEntity(investment);

		return investment;
	}

	/**
	 * 标的确认
	 * 
	 * @param oid
	 * @param operator
	 * @return
	 */
	public IlliquidAsset enter(String oid, String operator) {
		IlliquidAsset investment = this.findByOid(oid);
		if (!IlliquidAsset.ILLIQUIDASSET_STATE_PASS.equals(investment.getState()))
			throw new RuntimeException();

		investment.setState(IlliquidAsset.ILLIQUIDASSET_STATE_DURATION);
		investment.setOperator(operator);
		investment.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		investment = this.updateEntity(investment);

		//		 this.calculateRiskRate(investment);

		return investment;
	}

	/**
	 * 标的预审
	 * 
	 * @param oid
	 * @param state
	 * @param operator
	 * @param trustAmount
	 * @param suggest
	 */
	public IlliquidAsset precheck(String oid, String state, String operator, BigDecimal trustAmount, String suggest) {
		IlliquidAsset investment = this.findByOid(oid);
		investment.setState(state);
		if (IlliquidAsset.ILLIQUIDASSET_STATE_PASS.equals(state)) {
			if (null == trustAmount || trustAmount.doubleValue() <= 0)
				throw AMPException.getException("授信额度必须大于0");
			trustAmount = trustAmount.multiply(BigDecimal.valueOf(10000)); // 把万转成元
			investment.setPurchaseValue(trustAmount);
			//investment.setRestTrustAmount(trustAmount); // 初始化剩余授信额度和授信额度一样
		}
		if (!StringUtils.isEmpty(suggest)) {
			investment.setRejectDesc(suggest);
		}

		investment.setOperator(operator);
		this.updateEntity(investment);

		return investment;
	}

	/**
	 * 审核通过 非信托
	 * 
	 * @param oid
	 * @param state
	 * @param operator
	 * @return
	 */
	public IlliquidAsset precheckN(String oid, String state, String operator) {
		IlliquidAsset investment = this.findByOid(oid);
		investment.setState(state);

		investment.setOperator(operator);
		this.updateEntity(investment);

		return investment;
	}

	/**
	 * 标的成立
	 * 
	 * @Title: establish
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param form
	 * @return Investment 返回类型
	 */
	public IlliquidAsset establish(EstablishForm form) {
		String oid = form.getOid();
		IlliquidAsset it = this.findByOid(oid);
		BeanUtils.copyProperties(form, it);

		// it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_NORMAL); //
		// 标记为正常还款

		this.updateEntity(it);

		// targetService.repayMentSchedule(it); // 生成还款计划
		//repayMentPlanSchedule(it); // 生成预计还款计划

		return it;
	}

	/**
	 * 标的不成立
	 * 
	 * @Title: unEstablish
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param form
	 * @return Investment 返回类型
	 */
	public IlliquidAsset unEstablish(UnEstablishForm form) {
		String oid = form.getOid();
		IlliquidAsset it = this.findByOid(oid);
		it.setOperator(form.getOperator());
		it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_SETUP_FAIL); // 重置为成立失败

		this.updateEntity(it);

		return it;
	}

	/**
	 * 投资标的逾期
	 * 
	 * @param oid
	 * @param operator
	 */
	@Transactional
	public void overdue(String oid, String operator) {
		IlliquidAsset it = this.findByOid(oid);

		// 置为逾期
		it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVERDUE);
		//it.setOverdueDay(new java.sql.Date(System.currentTimeMillis()));
		it.setOperator(operator);

		this.updateEntity(it);

		//		// 添加逾期表
		//		IlliquidOverdue overdue = new IlliquidOverdue();
		//		overdue.setIlliquidAsset(it);
		//		overdue.setCreator(operator);
		//		overdue.setOverdueStartDate(new java.sql.Date(System.currentTimeMillis()));
		//		overdue.setOperator(operator);
		//		overdue.setOverdueDays(0);
		//
		//		illiquidOverdueService.saveEntity(overdue);
	}

	/**
	 * 逾期正常还款
	 * 
	 * @param oid
	 * @param operator
	 */
	@Transactional
	public void targetIncomeN(String oid, String operator) {
		IlliquidAsset it = this.findByOid(oid);

		// 置为本息兑付
		it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_REPAYMENTS);
		it.setOperator(operator);

		//		// 逾期天数
		//		int overdueDays = DateUtil.daysBetween(new Date(System.currentTimeMillis()), new Date(it.getOverdueDay().getTime()));
		//		it.setOverdueDays(overdueDays);
		//
		//		this.updateEntity(it);
		//
		//		// 生成逾期表
		//		IlliquidOverdue overdue = illiquidOverdueService.getLastOverdueByAssetOid(it.getOid());
		//		// 删除逾期表
		//		illiquidOverdueService.delEntity(overdue);
	}

	/**
	 * 逾期兑付
	 * 
	 * @param oid
	 * @param operator
	 */
	@Transactional
	public void targetIncomeD(String oid, String operator) {
		IlliquidAsset it = this.findByOid(oid);

		// 重置为逾期还款
		it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVERDUE_REPAYMENTS);
		// 逾期天数
		//int overdueDays = DateUtil.daysBetween(new Date(System.currentTimeMillis()), new Date(it.getOverdueDay().getTime()));
		// it.setOverdueDays(overdueDays);
		it.setOperator(operator);

		this.updateEntity(it);

		// 生成逾期表
		//		IlliquidOverdue overdue = illiquidOverdueService.getLastOverdueByAssetOid(it.getOid());
		//		if (overdue != null) {
		//			overdue.setOverdueEndDate(new java.sql.Date(System.currentTimeMillis()));
		//			overdue.setOverdueDays(overdueDays);
		//			overdue.setOperator(operator);
		//			illiquidOverdueService.updateEntity(overdue);
		//		}
	}
	/**
	 * 逾期转让
	 * 
	 * @param oid
	 * @param operator
	 */
	@Transactional
	public void overdueTransfer(String oid, String operator) {
		IlliquidAsset it = this.findByOid(oid);

		// 重置为逾期还款
		it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVERDUE_TRANSFER);
		// 逾期天数
		//int overdueDays = DateUtil.daysBetween(new Date(System.currentTimeMillis()), new Date(it.getOverdueDay().getTime()));
		// it.setOverdueDays(overdueDays);
		it.setOperator(operator);

		this.updateEntity(it);

		// 生成逾期表
		//		IlliquidOverdue overdue = illiquidOverdueService.getLastOverdueByAssetOid(it.getOid());
		//		if (overdue != null) {
		//			overdue.setOverdueEndDate(new java.sql.Date(System.currentTimeMillis()));
		//			overdue.setOverdueDays(overdueDays);
		//			overdue.setOperator(operator);
		//			illiquidOverdueService.updateEntity(overdue);
		//		}
	}
	/**
	 * 逾期转让
	 * 
	 * @param oid
	 * @param operator
	 */
	@Transactional
	public void transfer(String oid, String operator) {
		IlliquidAsset it = this.findByOid(oid);

		// 重置为逾期还款
		it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_TRANSFER);
		// 逾期天数
		//int overdueDays = DateUtil.daysBetween(new Date(System.currentTimeMillis()), new Date(it.getOverdueDay().getTime()));
		// it.setOverdueDays(overdueDays);
		it.setOperator(operator);

		this.updateEntity(it);

		// 生成逾期表
		//		IlliquidOverdue overdue = illiquidOverdueService.getLastOverdueByAssetOid(it.getOid());
		//		if (overdue != null) {
		//			overdue.setOverdueEndDate(new java.sql.Date(System.currentTimeMillis()));
		//			overdue.setOverdueDays(overdueDays);
		//			overdue.setOperator(operator);
		//			illiquidOverdueService.updateEntity(overdue);
		//		}
	}


	/**
	 * 逾期 - 坏账核销
	 * 
	 * @author
	 * @param oid
	 * @param operator
	 */
	@Transactional
	public void targetCancel(String oid, String operator) {
		IlliquidAsset it = this.findByOid(oid);

		// 置为坏账核销
		it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_CANCELLATION);
		it.setOperator(operator);
		// 逾期天数
		//		int overdueDays = DateUtil.daysBetween(new Date(System.currentTimeMillis()), new Date(it.getOverdueDay().getTime()));
		//		it.setOverdueDays(overdueDays);
		this.updateEntity(it);

		//		// 生成逾期表
		//		IlliquidOverdue overdue = illiquidOverdueService.getLastOverdueByAssetOid(it.getOid());
		//		if (overdue != null) {
		//			overdue.setOverdueEndDate(new java.sql.Date(System.currentTimeMillis()));
		//			overdue.setOverdueDays(overdueDays);
		//			overdue.setOperator(operator);
		//			illiquidOverdueService.updateEntity(overdue);
		//	}
	}

	/**
	 * 结束标的
	 * 
	 * @param oid
	 * @param operator
	 *            void 返回类型
	 */
	public void close(String oid, String operator) {
		IlliquidAsset it = this.findByOid(oid);
		it.setOperator(operator);
		// it.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_END); // 重置为结束
		this.updateEntity(it);
	}

	/**
	 * 认购下单成功, 增加申请份额
	 * 
	 * @param oid
	 * @param value
	 */
	@Transactional
	public void applyForPurchase(String oid, BigDecimal applyAmount) {
		this.illiquidAssetDao.incrApplyAmount(oid, applyAmount);
	}

	/**
	 * 认购订单审核成功, 增加持有份额, 减少申请份额
	 * 
	 * @param oid
	 * @param applyAmount
	 */
	@Transactional
	public void passForPurchase(String oid, BigDecimal applyAmount) {
		this.illiquidAssetDao.incrHoldShare(oid, applyAmount);
		this.illiquidAssetDao.decrApplyAmount(oid, applyAmount);
	}

	/**
	 * 认购审核失败, 减少申请份额
	 * 
	 * @param oid
	 * @param applyAmount
	 */
	@Transactional
	public void failForPurchase(String oid, BigDecimal applyAmount) {
		this.illiquidAssetDao.decrApplyAmount(oid, applyAmount);
	}

	/**
	 * 还款下单成功, 增加冻结本金, 增加冻结收益
	 * 
	 * @param oid
	 * @param applyAmount
	 */
	public void applyForRepayment(String oid, BigDecimal applyCapital, BigDecimal applyIncome) {
		this.illiquidAssetDao.incrLockupCapital(oid, applyCapital);
		this.illiquidAssetDao.incrLockupIncome(oid, applyIncome);
	}

	/**
	 * 还款审核失败, 减少冻结本金, 减少冻结收益
	 * 
	 * @param oid
	 * @param applyCapital
	 * @param applyIncome
	 */
	public void failForRepayment(String oid, BigDecimal applyCapital, BigDecimal applyIncome) {
		this.illiquidAssetDao.decrLockupCapital(oid, applyCapital);
		this.illiquidAssetDao.decrLockupIncome(oid, applyIncome);
	}

	/**
	 * 还款审核成功, 减少冻结本金, 减少冻结收益, 减少持有本金, 减少持有收益
	 * 
	 * @param oid
	 * @param applyCapital
	 * @param applyIncome
	 */
	public void passForRepayment(String oid, BigDecimal applyCapital, BigDecimal applyIncome) {
		this.illiquidAssetDao.decrLockupCapital(oid, applyCapital);
		this.illiquidAssetDao.decrLockupIncome(oid, applyIncome);
		this.illiquidAssetDao.decrHoldShare(oid, applyCapital);
		this.illiquidAssetDao.decrHoldIncome(oid, applyIncome);
	}

	/**
	 * 
	 * @param portfolioOid
	 *            投资组合id
	 * @param type
	 *            如果是空值， 表示要查全部的， 否则查指定的标的类型
	 * @return
	 */
	public List<IlliquidAssetOptions> subscripeQuery(String portfolioOid, final String type) {

		List<IlliquidAssetOptions> r = new ArrayList<IlliquidAssetOptions>();

		final List<ScopesEntity> list = this.scopesDao.findByRelationOid(portfolioOid);
		if (null == list || list.size() == 0) {
			return r;
		}

		Specification<IlliquidAsset> spec = new Specification<IlliquidAsset>() {

			@Override
			public Predicate toPredicate(Root<IlliquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate state = cb.equal(root.get("state").as(String.class), IlliquidAsset.ILLIQUIDASSET_STATE_DURATION);

				In<String> typeQuery = cb.in(root.get("type").as(String.class));
				for (int i = 0; i < list.size(); i++) {
					typeQuery.value(list.get(i).getAssetType().getOid());
				}

				In<String> lifeState = cb.in(root.get("lifeState").as(String.class));
				lifeState.value("B4_COLLECT").value("COLLECTING").value("OVER_COLLECT").value("UNSETUP");

				if (StringUtil.isEmpty(type)) {
					return cb.and(state, typeQuery, lifeState);
				}

				Predicate qtype = cb.equal(root.get("type").as(String.class), type);
				return cb.and(state, typeQuery, lifeState, qtype);
			}
		};

		List<IlliquidAsset> result = this.illiquidAssetDao.findAll(spec);
		for (IlliquidAsset a : result) {
			r.add(new IlliquidAssetOptions(a));
		}
		return r;
	}

	public List<IlliquidAssetOptions> purchaseQuery(String portfolioOid, final String type) {

		List<IlliquidAssetOptions> r = new ArrayList<IlliquidAssetOptions>();

		final List<ScopesEntity> list = this.scopesDao.findByRelationOid(portfolioOid);
		if (null == list || list.size() == 0) {
			return r;
		}

		Specification<IlliquidAsset> spec = new Specification<IlliquidAsset>() {

			@Override
			public Predicate toPredicate(Root<IlliquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate state = cb.equal(root.get("state").as(String.class), IlliquidAsset.ILLIQUIDASSET_STATE_DURATION);

				In<String> typeQuery = cb.in(root.get("type").as(String.class));
				for (int i = 0; i < list.size(); i++) {
					typeQuery.value(list.get(i).getAssetType().getOid());
				}

				Predicate lifeState = cb.equal(root.get("lifeState").as(String.class), IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE);

				if (StringUtil.isEmpty(type)) {
					return cb.and(state, typeQuery, lifeState);
				}
				Predicate qtype = cb.equal(root.get("type").as(String.class), type);
				return cb.and(state, typeQuery, lifeState, qtype);
			}
		};

		List<IlliquidAsset> result = this.illiquidAssetDao.findAll(spec);
		for (IlliquidAsset a : result) {
			r.add(new IlliquidAssetOptions(a));
		}
		return r;
	}

}
