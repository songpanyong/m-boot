package com.guohuai.ams.portfolio.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.guohuai.ams.acct.books.AccountBook;
import com.guohuai.ams.acct.books.AccountBookService;
import com.guohuai.ams.dict.Dict;
import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.dao.PortfolioDao;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.form.PortfolioForm;
import com.guohuai.ams.portfolio.form.PortfolioListResp;
import com.guohuai.ams.portfolio.form.PortfolioResp;
import com.guohuai.ams.portfolio.form.PortfolioStatisticsResp;
import com.guohuai.ams.portfolio.scopes.ScopesEntity;
import com.guohuai.ams.portfolio.scopes.ScopesService;
import com.guohuai.ams.portfolio20.net.correct.PortfolioNetCorrectEntity;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;

/**
 * 投资组合业务逻辑
 * 
 * @author star.zhu 2016年12月26日
 */
@Service
public class PortfolioService {
	@Autowired
	private PortfolioDao portfolioDao;

	@Autowired
	private ScopesService scopeService;
	@Autowired
	private PublisherBaseAccountService spvService;
	@Autowired
	private AccountBookService accountBookService;
	@Autowired
	private ProductDao productDao;
	

	/**
	 * 新建投资组合
	 * 
	 * @param form
	 * @param uid
	 */
	@Transactional
	public void createPortfolio(PortfolioForm form, String operator) {
		PortfolioEntity entity = new PortfolioEntity();
		entity.setOid(StringUtil.uuid());
		entity.setName(form.getName());
		PublisherBaseAccountEntity spv = spvService.findByOid(form.getSpvOid());
		entity.setSpvEntity(spv);

		entity.setCashRate(BigDecimalUtil.formatForDivide100(form.getCashRate()));
		entity.setLiquidRate(BigDecimalUtil.formatForDivide100(form.getLiquidRate()));
		entity.setIlliquidRate(BigDecimalUtil.formatForDivide100(form.getIlliquidRate()));

		entity.setCashFactRate(BigDecimal.ZERO);
		entity.setLiquidFactRate(BigDecimal.ZERO);
		entity.setIlliquidFactRate(BigDecimal.ZERO);

		entity.setManageRate(BigDecimalUtil.formatForDivide100(form.getManageRate()));
		entity.setTrusteeRate(BigDecimalUtil.formatForDivide100(form.getTrusteeRate()));
		entity.setCalcBasis(form.getCalcBasis());

		entity.setOrganization(form.getOrganization());
		entity.setPlanName(form.getPlanName());
		entity.setBank(form.getBank());
		entity.setAccount(form.getAccount());
		entity.setContact(form.getContact());
		entity.setTelephone(form.getTelephone());

		entity.setNav(BigDecimal.ZERO);
		entity.setShares(BigDecimal.ZERO);
		entity.setNetValue(BigDecimal.ZERO);

		entity.setDimensions(BigDecimal.ZERO);
		entity.setCashPosition(BigDecimal.ZERO);
		entity.setLiquidDimensions(BigDecimal.ZERO);
		entity.setIlliquidDimensions(BigDecimal.ZERO);
		entity.setDeviationValue(BigDecimal.ZERO);
		entity.setFreezeCash(BigDecimal.ZERO);
		entity.setDrawedChargefee(BigDecimal.ZERO);
		entity.setCountintChargefee(BigDecimal.ZERO);

		entity.setCreater(operator);
		entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
		entity.setOperator(operator);
		entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		entity.setState(ConstantUtil.state_create);

		this.portfolioDao.save(entity);

		if (null != form.getScopes() && form.getScopes().length > 0) {
			for (String s : form.getScopes()) {
				this.scopeService.save(entity.getOid(), s);
			}
		}
	}

	/**
	 * 根据资产池id获取对应的资产池详情
	 * 
	 * @param pid
	 * @return
	 */
	@Transactional
	public PortfolioEntity getByOid(String pid) {
		PortfolioEntity entity = portfolioDao.findOne(pid);

		return entity;
	}

	/**
	 * 根据id获取对应的投资组合详情
	 * 
	 * @param pid
	 * @return
	 */
	@Transactional
	public PortfolioResp getPortfolioByOid(String pid) {
		PortfolioResp resp = new PortfolioResp();
		PortfolioEntity entity = new PortfolioEntity();
		pid = this.getPid(pid);
		if (null == pid || "".equals(pid)) {
			return null;
		} else {
			entity = this.getByOid(pid);
		}
		List<Dict> scopes = scopeService.getScopes(pid);
		try {
			BeanUtils.copyProperties(resp, entity);
			resp.setScopes(scopes);
			resp.setSpvName(entity.getSpvEntity().getRealName());
			resp.setSpvOid(entity.getSpvEntity().getOid());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取收益数据

		Map<String, AccountBook> accountMap = accountBookService.find(pid, "2201", "2301", "300101", "300102");
		if (null != accountMap) {
			if (accountMap.containsKey("2201")) {
				// resp.setUnDistributeProfit(accountMap.get("2201").getBalance());
			}
			if (accountMap.containsKey("2301")) {
				// resp.setPayFeigin(accountMap.get("2301").getBalance());
			}
		}

		return resp;
	}

	/**
	 * 当pid为空的时候，默认获取排序的第一个投资组合
	 * 
	 * @param pid
	 * @return
	 */
	@Transactional
	public String getPid(String pid) {
		if (null == pid || "".equals(pid)) {
			PortfolioEntity entity = portfolioDao.getLimitOne();
			if (null != entity) {
				pid = entity.getOid();
			}
		}

		return pid;
	}

	/**
	 * 编辑资产池
	 * 
	 * @param form
	 * @param uid
	 */
	@Transactional
	public void editPortfolio(PortfolioForm form, String operator) {

		PortfolioEntity entity = portfolioDao.findOne(form.getOid());
		entity.setName(form.getName());
		PublisherBaseAccountEntity spv = spvService.findByOid(form.getSpvOid());
		entity.setSpvEntity(spv);

		entity.setCashRate(BigDecimalUtil.formatForDivide100(form.getCashRate()));
		entity.setLiquidRate(BigDecimalUtil.formatForDivide100(form.getLiquidRate()));
		entity.setIlliquidRate(BigDecimalUtil.formatForDivide100(form.getIlliquidRate()));

		entity.setCashFactRate(BigDecimal.ZERO);
		entity.setLiquidFactRate(BigDecimal.ZERO);
		entity.setIlliquidFactRate(BigDecimal.ZERO);

		entity.setManageRate(BigDecimalUtil.formatForDivide100(form.getManageRate()));
		entity.setTrusteeRate(BigDecimalUtil.formatForDivide100(form.getTrusteeRate()));
		entity.setCalcBasis(form.getCalcBasis());

		entity.setOrganization(form.getOrganization());
		entity.setPlanName(form.getPlanName());
		entity.setBank(form.getBank());
		entity.setAccount(form.getAccount());
		entity.setContact(form.getContact());
		entity.setTelephone(form.getTelephone());

		entity.setNav(BigDecimal.ZERO);
		entity.setShares(BigDecimal.ZERO);
		entity.setNetValue(BigDecimal.ZERO);

		entity.setDimensions(BigDecimal.ZERO);
		entity.setCashPosition(BigDecimal.ZERO);
		entity.setLiquidDimensions(BigDecimal.ZERO);
		entity.setIlliquidDimensions(BigDecimal.ZERO);
		entity.setDeviationValue(BigDecimal.ZERO);
		entity.setFreezeCash(BigDecimal.ZERO);
		entity.setDrawedChargefee(BigDecimal.ZERO);
		entity.setCountintChargefee(BigDecimal.ZERO);

		entity.setOperator(operator);
		entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		entity.setState(ConstantUtil.state_create);

		this.portfolioDao.save(entity);

		List<ScopesEntity> hisScopes = this.scopeService.list(entity.getOid());
		Map<String, ScopesEntity> hisScopesMap = new HashMap<String, ScopesEntity>();
		if (null != hisScopes && hisScopes.size() > 0) {
			for (ScopesEntity s : hisScopes) {
				hisScopesMap.put(s.getAssetType().getOid(), s);
			}
		}

		if (null != form.getScopes() && form.getScopes().length > 0) {
			for (String s : form.getScopes()) {
				if (hisScopesMap.containsKey(s)) {
					hisScopesMap.remove(s);
				} else {
					this.scopeService.save(entity.getOid(), s);
				}
			}
		}

		if (hisScopesMap.size() > 0) {
			for (Map.Entry<String, ScopesEntity> entry : hisScopesMap.entrySet()) {
				this.scopeService.delete(entry.getValue().getOid());
			}
		}

	}

	/**
	 * 新建审核
	 * 
	 * @param oid
	 * @param operator
	 * @param operate
	 *            YES:审核通过； NO:审核不通过；
	 * @param auditMark
	 *            审核意见
	 */
	@Transactional
	public void auditPortfolio(String oid, String operator, String operate, String auditMark) {
		PortfolioEntity entity = portfolioDao.findOne(oid);
		if ("YES".equals(operate)) {
			entity.setState(ConstantUtil.state_duration);
		} else {
			entity.setState(ConstantUtil.state_reject);
		}
		entity.setOperator(operator);
		entity.setAuditMark(auditMark);
		entity.setAuditTime(DateUtil.getSqlCurrentDate());
	}

	/**
	 * 获取查询列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<PortfolioEntity> getListByParams(Specification<PortfolioEntity> spec, Pageable pageable) {
		Page<PortfolioEntity> page = portfolioDao.findAll(spec, pageable);
		PageResp<PortfolioEntity> resp = new PageResp<PortfolioEntity>();
		if (!page.getContent().isEmpty()) {
			resp.setRows(page.getContent());
			resp.setTotal(page.getTotalElements());
		}
		return resp;
	}

	/**
	 * 获取查询列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PortfolioListResp getAuditListByParams(Specification<PortfolioEntity> spec, Pageable pageable) {
		Page<PortfolioEntity> rep = this.portfolioDao.findAll(spec, pageable);
		PortfolioListResp resp = new PortfolioListResp();
		if (null != rep) {
			resp.setTotal(rep.getTotalElements());
			if (null != rep.getContent() && rep.getContent().size() > 0) {
				for (PortfolioEntity entity : rep.getContent()) {
					PortfolioResp p = new PortfolioResp(entity);
					p.setSpvName(entity.getSpvEntity().getRealName());
					p.setSpvOid(entity.getSpvEntity().getOid());
					resp.getRows().add(p);
				}
			}
		}
		return resp;
	}

	/**
	 * 获取所有投资组合的名称列表，包含id
	 * 
	 * @return
	 */
	@Transactional
	public List<JSONObject> getAllNameList() {
		List<JSONObject> jsonObjList = Lists.newArrayList();
		List<Object> objList = portfolioDao.findAllNameList();
		if (!objList.isEmpty()) {
			Object[] obs = null;
			JSONObject jsonObj = null;
			for (Object obj : objList) {
				obs = (Object[]) obj;
				jsonObj = new JSONObject();
				jsonObj.put("oid", obs[0]);
				jsonObj.put("name", obs[1]);

				jsonObjList.add(jsonObj);
			}
		}

		return jsonObjList;
	}

	/**
	 * 逻辑删除资产池
	 * 
	 * @param pid
	 */
	@Transactional
	public void updatePortfolio(String pid) {
		portfolioDao.update(pid);
	}

	/**
	 * 物理删除投资组合
	 * 
	 * @param pid
	 */
	@Transactional
	public void deletePortfolio(String pid) {
		portfolioDao.delete(pid);
	}

	/**
	 * 保存
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional
	public PortfolioEntity save(PortfolioEntity entity) {
		return this.portfolioDao.save(entity);
	}

	@Transactional
	public PortfolioEntity updateNet(PortfolioNetCorrectEntity correct) {
		PortfolioEntity portfolio = correct.getPortfolio();
		portfolio.setShares(correct.getShare());
		portfolio.setNav(correct.getNav());
		portfolio.setNetValue(correct.getNet());
		portfolio.setBaseDate(correct.getNetDate());

		// 除了调整净值, 还要调整一下偏离损益
		// 净值 - 现金 - 现金类估值 - 非现金类估值
		// 偏离损益 = 市值 - 净值 = 市值 - ( 估值 - 计提 ) = 市值 - 估值 + 计提
		portfolio.setDeviationValue(portfolio.getNetValue().subtract(portfolio.getDimensions()).add(portfolio.getCountintChargefee()));

		this.portfolioDao.save(portfolio);
		return portfolio;
	}

	@Transactional
	public void freezeCash(String oid, BigDecimal value) {
		this.portfolioDao.incrFreezeCash(oid, value);
	}

	@Transactional
	public void relaxCash(String oid, BigDecimal value) {
		this.portfolioDao.decrFreezeCash(oid, value);
	}

	@Transactional
	public void liquidPurchase(String oid, BigDecimal cash, BigDecimal estimate) {
		this.portfolioDao.decrFreezeCash(oid, cash);
		this.portfolioDao.decrCashPosition(oid, cash);
		this.portfolioDao.incrLiquidDimensions(oid, estimate);
		BigDecimal deviation = cash.subtract(estimate);
		if (deviation.signum() != 0) {
			this.portfolioDao.incrDeviationValue(oid, deviation);
			this.portfolioDao.decrDimensions(oid, deviation);
		}
	}

	@Transactional
	public void liquidRedeem(String oid, BigDecimal cash, BigDecimal estimate) {
		this.portfolioDao.incrCashPosition(oid, cash);
		this.portfolioDao.decrLiquidDimensions(oid, estimate);
		BigDecimal deviation = cash.subtract(estimate);
		if (deviation.signum() != 0) {
			this.portfolioDao.incrDeviationValue(oid, deviation);
			this.portfolioDao.incrDimensions(oid, deviation);
		}
	}

	@Transactional
	public void illiquidPurchase(String oid, BigDecimal cash, BigDecimal estimate) {
		this.portfolioDao.decrFreezeCash(oid, cash);
		this.portfolioDao.decrCashPosition(oid, cash);
		this.portfolioDao.incrIlliquidDimensions(oid, estimate);
		BigDecimal deviation = cash.subtract(estimate);
		if (deviation.signum() != 0) {
			this.portfolioDao.incrDeviationValue(oid, deviation);
			this.portfolioDao.decrDimensions(oid, deviation);
		}
	}

	@Transactional
	public void illiquidRepayment(String oid, BigDecimal cash, BigDecimal estimate) {
		this.portfolioDao.incrCashPosition(oid, cash);
		this.portfolioDao.decrIlliquidDimensions(oid, estimate);
		BigDecimal deviation = cash.subtract(estimate);
		if (deviation.signum() != 0) {
			this.portfolioDao.incrDeviationValue(oid, deviation);
			this.portfolioDao.incrDimensions(oid, deviation);
		}
		
	}

	@Transactional
	public void cashTopup(String oid, BigDecimal cash) {
		this.portfolioDao.incrCashPosition(oid, cash);
		this.portfolioDao.decrDeviationValue(oid, cash);
		this.portfolioDao.incrDimensions(oid, cash);
	}

	@Transactional
	public void cashWithdraw(String oid, BigDecimal cash) {
		this.portfolioDao.decrCashPosition(oid, cash);
		this.portfolioDao.incrDeviationValue(oid, cash);
		this.portfolioDao.decrDimensions(oid, cash);
	}

	@Transactional
	public void estimate(String oid, BigDecimal chargefee, BigDecimal liquidDimensions, BigDecimal illiquidDimensions, Date dimensionsDate) {
		this.countingChargefee(oid, chargefee);
		this.portfolioDao.incrLiquidDimensions(oid, liquidDimensions);
		this.portfolioDao.incrIlliquidDimensions(oid, illiquidDimensions);
		this.portfolioDao.incrDimensions(oid, liquidDimensions.add(illiquidDimensions));
		this.portfolioDao.updateDimensionsDate(oid, dimensionsDate);
	}

	@Transactional
	private void countingChargefee(String oid, BigDecimal chargefee) {
		// this.portfolioDao.decrCashPosition(oid, chargefee);
		// this.portfolioDao.decrDimensions(oid, chargefee);
		this.portfolioDao.incrDeviationValue(oid, chargefee);
		this.portfolioDao.incrCountintChargefee(oid, chargefee);
		// this.portfolioDao.incrDrawedChargefee(oid, chargefee);
	}

	@Transactional
	public void drawingChargefee(String oid, BigDecimal chargefee) {
		this.portfolioDao.incrCountintChargefee(oid, chargefee);
	}

	@Transactional
	public void dualChargeFee(String oid, BigDecimal fee, String type) {
		// TODO spv 赎回业务触发, 需要确认后续操作
	}

	public List<PortfolioEntity> findActivePortfolio() {
		return this.portfolioDao.findByState(PortfolioEntity.PORTFOLIO_STATE_duration);
	}

	public PortfolioStatisticsResp statisticsQuery(String oid) {

		PortfolioEntity p = this.getByOid(oid);
		if (null == p) {
			throw new GHException("未知的投资组合ID");
		}
		Product product=productDao.findByPortfolio(p.getOid());
		PortfolioStatisticsResp r = new PortfolioStatisticsResp();
		r.setOid(oid);
		r.setBaseDate(p.getBaseDate());
		r.setShares(p.getShares());
		r.setNav(p.getNav());
		r.setNetValue(p.getNetValue());

		BigDecimal equity = BigDecimal.ZERO;
		BigDecimal payableIncome = BigDecimal.ZERO;
		BigDecimal receivableIncome = BigDecimal.ZERO;

		Map<String, AccountBook> accountMap = this.accountBookService.find(oid, "2201", "1201", "3001");
		if (null != accountMap) {
			if (accountMap.containsKey("3001")) {
				equity = accountMap.get("3001").getBalance();
			}
			if (accountMap.containsKey("2201")) {
				payableIncome = accountMap.get("2201").getBalance();
			}
			if (accountMap.containsKey("1201")) {
				receivableIncome = accountMap.get("1201").getBalance();
			}
		}

		r.setEquity(equity);
		r.setPayableIncome(payableIncome);
		r.setReceivableIncome(receivableIncome);

		// 所有者权益
		//		private BigDecimal equity;
		// 未分配收益
		//		private BigDecimal payableIncome;
		// 应收投资收益
		//		private BigDecimal receivableIncome;

		r.setDimensions(p.getDimensions());
		r.setEstimate(p.getDimensions().subtract(p.getCountintChargefee()));
		r.setDimensionsDate(p.getDimensionsDate());
		r.setCashPosition(p.getCashPosition());
		r.setLiquidDimensions(p.getLiquidDimensions());
		r.setIlliquidDimensions(p.getIlliquidDimensions());
		r.setDeviationValue(p.getDeviationValue());
		r.setCountintChargefee(p.getCountintChargefee());
		r.setDrawedChargefee(p.getDrawedChargefee());
		if(null==product){
			r.setProductType("PRODUCTTYPE_01");
		}else{
			r.setProductType(product.getType().getOid());
		}
		return r;
	}

}
