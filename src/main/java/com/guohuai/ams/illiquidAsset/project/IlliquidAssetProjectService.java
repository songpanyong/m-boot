package com.guohuai.ams.illiquidAsset.project;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.illiquidAsset.IlliquidAssetService;
import com.guohuai.ams.system.config.project.warrantyExpire.CCPWarrantyExpire;
import com.guohuai.ams.system.config.project.warrantyExpire.CCPWarrantyExpireDao;
import com.guohuai.ams.system.config.project.warrantyMode.CCPWarrantyMode;
import com.guohuai.ams.system.config.project.warrantyMode.CCPWarrantyModeDao;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class IlliquidAssetProjectService {

	@Autowired
	private IlliquidAssetProjectDao illiquidAssetProjectDao;
	@Autowired
	private IlliquidAssetService illiquidAssetService;
	@Autowired
	private CCPWarrantyModeDao cCPWarrantyModeDao;
	@Autowired
	private CCPWarrantyExpireDao cCPWarrantyExpireDao;

	@PersistenceContext
	private EntityManager em;//注入entitymanager

	/**
	 * 新增
	 * 
	 * @param en
	 * @return
	 */
	@Transactional
	public IlliquidAssetProject saveEntity(IlliquidAssetProject en) {
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
	public IlliquidAssetProject updateEntity(IlliquidAssetProject en) {
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.illiquidAssetProjectDao.save(en);
	}

	/**
	 * 根据OID查询
	 * 
	 * @param oid
	 * @return
	 */
	public IlliquidAssetProject findByOid(String oid) {
		IlliquidAssetProject entity = this.illiquidAssetProjectDao.findOne(oid);
		if (null == entity) {
			throw GHException.getException(70000);
		}
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
	public IlliquidAssetProjectListResp queryPage(Specification<IlliquidAssetProject> spec, Pageable pageable) {
		Page<IlliquidAssetProject> enchs = this.illiquidAssetProjectDao.findAll(spec, pageable);
		IlliquidAssetProjectListResp pageResp = new IlliquidAssetProjectListResp(enchs);

		return pageResp;
	}

	/**
	 * 保存底层项目
	 * 
	 * @Title: save
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param projectForm
	 * @return
	 * @return Project 返回类型
	 */
	public IlliquidAssetProject save(IlliquidAssetProjectForm projectForm) {
		if (null == projectForm)
			throw AMPException.getException("底层项目不能为空");
		String targetOid = projectForm.getIlliquidAssetOid();
		if (StringUtils.isBlank(targetOid))
			throw AMPException.getException("投资标的id不能为空");

		IlliquidAssetProject prj = new IlliquidAssetProject();
		BeanUtils.copyProperties(projectForm, prj);

		IlliquidAsset investment = this.illiquidAssetService.findByOid(targetOid);
		prj.setIlliquidAsset(investment);

		/* 计算项目风险系数开始 */
		// 计算公式 : Max(保证方式担保方式权数 * 保证方式担保期数权数, 抵押方式担保方式权数 * 抵押方式担保期数权数, 质押方式担保方式权数 * 质押方式担保期数权数) * 标的信用等级系数
		List<CCPWarrantyMode> modeList = cCPWarrantyModeDao.findAll(); // 查询担保方式权数配置		
		List<CCPWarrantyExpire> expireList = cCPWarrantyExpireDao.findAll(); // 查询担保期限权数配置

		prj = this.calculateProjectRiskFactor(modeList, expireList, investment, prj);
		/* 计算项目风险系数结束 */

		prj.setCreateTime(new Timestamp(System.currentTimeMillis()));
		prj.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		prj = this.saveEntity(prj);

		/* 重新计算标的的风险系数 */
		BigDecimal riskRate = this.getMaxRiskFactor(targetOid);
		investment.setRiskRate(riskRate);

		illiquidAssetService.updateEntity(investment);
		return prj;
	}

	/**
	 * 删除
	 * 
	 * @param oid
	 */
	public void deleteByOid(String oid) {
		IlliquidAssetProject en = findByOid(oid);
		illiquidAssetProjectDao.delete(en);
	}

	/**
	 * 根据标的id查询底层项目
	 * 
	 * @param targetOid
	 * @return
	 */
	public List<IlliquidAssetProject> findByIlliquidAssetOid(String illiquidAssetOid) {
		return this.illiquidAssetProjectDao.findByIlliquidAssetOid(illiquidAssetOid);
	}

	/**
	 * 求标的下面所有项目的最大的风险系数
	 * 
	 * @Title: getMaxRiskFactor
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param targetOid
	 * @return BigDecimal 返回类型
	 */
	public BigDecimal getMaxRiskFactor(String targetOid) {
		if (StringUtils.isBlank(targetOid))
			throw AMPException.getException("投资标的id不能为空");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
		Root<IlliquidAssetProject> root = query.from(IlliquidAssetProject.class);

		// Expression<BigDecimal> maxExp = root.get("").as(BigDecimal.class);
		// query.select(cb.max(maxExp));
		query.select(cb.max(root.get("riskFactor")));

		query.where(cb.equal(root.get("illiquidAsset").get("oid").as(String.class), targetOid));
		return em.createQuery(query).getSingleResult();
	}

	/**
	 * 计算项目风险系数
	 * 
	 * @Title: calculateProjectRiskFactor
	 * @version 1.0
	 * @param modeList
	 * @param expireList
	 * @param investment
	 * @param prj
	 * @return Project 返回类型
	 */
	public IlliquidAssetProject calculateProjectRiskFactor(List<CCPWarrantyMode> modeList, List<CCPWarrantyExpire> expireList, IlliquidAsset investment, IlliquidAssetProject prj) {
		if (null == modeList || null == expireList) {
			return prj;
		}
		/* 计算项目风险系数开始 */
		// 计算公式 : Max(保证方式担保方式权数 * 保证方式担保期数权数, 抵押方式担保方式权数 * 抵押方式担保期数权数, 质押方式担保方式权数 * 质押方式担保期数权数) * 标的信用等级系数

		BigDecimal guaranteeModeWeight = new BigDecimal(0), mortgageModeWeight = new BigDecimal(0), hypothecationModeWeight = new BigDecimal(0);
		String guaranteeModeOid = prj.getGuaranteeModeOid(), mortgageModeOid = prj.getMortgageModeOid(), hypothecationModeOid = prj.getHypothecationModeOid();
		for (CCPWarrantyMode mode : modeList) {
			String mOid = mode.getOid();
			if (StringUtils.equals(guaranteeModeOid, mOid)) {
				guaranteeModeWeight = mode.getWeight();
				prj.setGuaranteeModeWeight(guaranteeModeWeight);
				prj.setGuaranteeModeTitle(mode.getTitle());
			}
			if (StringUtils.equals(mortgageModeOid, mOid)) {
				mortgageModeWeight = mode.getWeight();
				prj.setMortgageModeWeight(mortgageModeWeight);
				prj.setMortgageModeTitle(mode.getTitle());
			}
			if (StringUtils.equals(hypothecationModeOid, mOid)) {
				hypothecationModeWeight = mode.getWeight();
				prj.setHypothecationModeWeight(mortgageModeWeight);
				prj.setHypothecationModeTitle(mode.getTitle());
			}
		}

		BigDecimal guaranteeModeExpireWeight = new BigDecimal(0), mortgageModeExpireWeight = new BigDecimal(0), hypothecationModeExpireWeight = new BigDecimal(0);
		String guaranteeModeExpireOid = prj.getGuaranteeModeExpireOid(), mortgageModeExpireOid = prj.getMortgageModeExpireOid(), hypothecationModeExpireOid = prj.getHypothecationModeExpireOid();
		for (CCPWarrantyExpire expire : expireList) {
			String eOid = expire.getOid();
			if (StringUtils.equals(guaranteeModeExpireOid, eOid)) {
				guaranteeModeExpireWeight = expire.getWeight();
				prj.setGuaranteeModeExpireWeight(guaranteeModeExpireWeight);
				prj.setGuaranteeModeExpireTitle(expire.getTitle());
			}
			if (StringUtils.equals(mortgageModeExpireOid, eOid)) {
				mortgageModeExpireWeight = expire.getWeight();
				prj.setMortgageModeExpireWeight(mortgageModeExpireWeight);
				prj.setMortgageModeExpireTitle(expire.getTitle());
			}
			if (StringUtils.equals(hypothecationModeExpireOid, eOid)) {
				hypothecationModeExpireWeight = expire.getWeight();
				prj.setHypothecationModeExpireWeight(hypothecationModeExpireWeight);
				prj.setHypothecationModeExpireTitle(expire.getTitle());
			}
		}

		BigDecimal guaranteeRato = guaranteeModeWeight.multiply(guaranteeModeExpireWeight); // 担保系数
		BigDecimal mortgageRato = mortgageModeWeight.multiply(mortgageModeExpireWeight); // 抵押系数
		BigDecimal hypothecation = hypothecationModeWeight.multiply(hypothecationModeExpireWeight); // 质押系数

		double maxRato = NumberUtils.max(new double[] { guaranteeRato.doubleValue(), mortgageRato.doubleValue(), hypothecation.doubleValue() });// 取最大值

		BigDecimal collectScoreWeight = investment.getCollectScoreWeight(); // 标的[信用等级系数]
		BigDecimal riskFactor = null;
		if (null != collectScoreWeight) {
			riskFactor = collectScoreWeight.multiply(new BigDecimal(maxRato)); // 计算出项目的风险系数
			prj.setRiskFactor(riskFactor);
		}
		return prj;
	}

	public BigDecimal calculateInvestmentRisk(IlliquidAsset entity) {
		BigDecimal max = null;
		String targetOid = entity.getOid();
		if (StringUtils.isBlank(targetOid))
			throw AMPException.getException("投资标的id不能为空");
		List<IlliquidAssetProject> list = this.findByIlliquidAssetOid(targetOid);
		int size = null == list ? 0 : list.size();
		if (0 == size) {
			log.debug("标的id=" + targetOid + "下暂无底层项目");
			return max;
		}
		List<CCPWarrantyMode> modeList = cCPWarrantyModeDao.findAll(); // 查询担保方式权数配置
		List<CCPWarrantyExpire> expireList = cCPWarrantyExpireDao.findAll(); // 查询担保期限权数配置
		double[] dbs = new double[size]; // 项目风险系数数组
		for (int i = 0; i < size; i++) {
			IlliquidAssetProject prj = list.get(i);
			this.calculateProjectRiskFactor(modeList, expireList, entity, prj); // 计算项目风险系数
			prj.setIlliquidAsset(entity);
			this.updateEntity(prj);
			dbs[i] = list.get(i).getRiskFactor().doubleValue(); // 获取每一个项目的项目风险系数
		}
		// max = getMaxRiskFactor(targetOid); // 通过数据库查询最大的项目风险系数
		max = new BigDecimal(NumberUtils.max(dbs));// 取最大的项目风险系数
		return max;
	}

}
