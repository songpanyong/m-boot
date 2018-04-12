package com.guohuai.mmp.publisher.investor.holdincome;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsEntity;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanProductDao;
import com.guohuai.mmp.publisher.hold.PlanIncomeInfo;
import com.guohuai.mmp.publisher.investor.holdapartincome.PartIncomeDao;
import com.guohuai.mmp.publisher.investor.holdapartincome.PartIncomeEntity;
import com.guohuai.mmp.publisher.investor.holdincome.InvestorIncomeQueryRep.InvestorIncomeQueryRepBuilder;

/**
 * 发行人-投资人-合仓收益明细
 * @author xjj
 *
 */
@Service
@Transactional
public class InvestorIncomeService {

	@Autowired
	private InvestorIncomeDao investorIncomeDao;
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private InvestorBaseAccountService  investorBaseAccountService;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	
	@Autowired
	private PlanProductDao planProductDao;
	@Autowired
	private PartIncomeDao partIncomeDao;
	
	
	/*@Transactional
	public PageResp<InvestorIncomeQueryRep> investorIncomeQuery(Specification<InvestorIncomeEntity> spec, Pageable pageable) {		
		Page<InvestorIncomeEntity> incomes = this.investorIncomeDao.findAll(spec, pageable);
		PageResp<InvestorIncomeQueryRep> pagesRep = new PageResp<InvestorIncomeQueryRep>();

		for (InvestorIncomeEntity income : incomes) {
			InvestorIncomeQueryRep rep = new InvestorIncomeQueryRepBuilder()
					.oid(income.getOid())
					.productCode(income.getProduct().getCode())
					.productName(income.getProduct().getName())
					.incomeAmount(income.getIncomeAmount())
					.baseAmount(income.getBaseAmount())
					.rewardAmount(income.getRewardAmount())
					.accureVolume(income.getAccureVolume())
					.confirmDate(income.getConfirmDate())
					.createTime(income.getCreateTime())
					.build();
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(incomes.getTotalElements());	
		return pagesRep;
	}*/
	@Transactional
	public PageResp<InvestorIncomeQueryRep> investorIncomeQuery(String investorOid,int page,int rows) {		
	
		PageResp<InvestorIncomeQueryRep> rep = new PageResp<InvestorIncomeQueryRep>();
		List<Object[]> list = this.partIncomeDao.findInvestorIncome(investorOid);
		
		for(Object[] income : list){
			InvestorIncomeQueryRep incomeRep = new InvestorIncomeQueryRepBuilder()
					.oid((String)income[0])
					.productCode((String)income[1])
					.productName((String)income[2])
					.incomeAmount((BigDecimal)income[3])
					.baseAmount((BigDecimal)income[4])
					.accureVolume((BigDecimal)income[5])
					.rewardAmount((BigDecimal)income[6])	
					.confirmDate((Date)income[7])
					.createTime((Timestamp)income[8])
					.build();
			rep.getRows().add(incomeRep);
		}
		
		List<InvestorIncomeQueryRep> newList = this.getPage(rep.getRows(),page,rows);
		rep.setTotal(rep.getRows().size());
		rep.setRows(newList);		
		
		return rep;
	}
	
	public List<InvestorIncomeQueryRep> getPage(List<InvestorIncomeQueryRep> list, int page, int rows) {
		
		List<InvestorIncomeQueryRep> newList = new ArrayList<InvestorIncomeQueryRep>();
		int currIdx = page > 1 ? (page - 1) * rows : 0 ;
		for(int j = 0; j < rows && j < list.size() - currIdx; j++){
			InvestorIncomeQueryRep PlanIncomeInfo = list.get(currIdx + j);
			newList.add(PlanIncomeInfo);
		}
		return newList;
	}

	public InvestorIncomeEntity saveEntity(InvestorIncomeEntity holdIncome) {
		holdIncome.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(holdIncome);
	}

	public InvestorIncomeEntity updateEntity(InvestorIncomeEntity holdIncome) {
		holdIncome.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.investorIncomeDao.save(holdIncome);
	}
	
	public List<InvestorIncomeEntity> findAll(Specification<InvestorIncomeEntity> spec) {
		List<InvestorIncomeEntity> list = this.investorIncomeDao.findAll(spec);
		return list;
	}
	
	

	public List<InvestorIncomeEntity> findByInvestorOidAndConfirmDate(String investorOid, String incomeDate) {
		
		return this.investorIncomeDao.findByInvestorOidAndConfirmDate(investorOid, incomeDate);
	}

	public List<InvestorIncomeEntity> findByInvestorOidAndConfirmDateInHis(String investorOid, String incomeDate) {
		String tName = "select * from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME_" + incomeDate.replace("-", "");
		String where = " where investorOid = '" + investorOid + "' and confirmDate = '" + incomeDate +"'";
		@SuppressWarnings("unchecked")
		List<InvestorIncomeEntity> list = em.createNativeQuery(tName + where, InvestorIncomeEntity.class).getResultList();
		return list;
	}
	
	/**
	 * 我的活期明细-收益
	 */
	public MyInvestorIncomeRep findAllByPages2(Specification<InvestorIncomeEntity> spec ,Pageable pageable,boolean totalIncomeFlag) {
		MyInvestorIncomeRep rep = new MyInvestorIncomeRep();
		
		Page<InvestorIncomeEntity> page = this.investorIncomeDao.findAll(spec,pageable);
		PageResp<InvestorIncomeRep> pagesRep = new PageResp<InvestorIncomeRep>();
		if (page != null && page.getContent() != null && page.getTotalElements() > 0) {
			List<InvestorIncomeRep> rows = new ArrayList<InvestorIncomeRep>();
			for (InvestorIncomeEntity p : page) {
				InvestorIncomeRep queryRep = new InvestorIncomeRep();
				queryRep.setAmount(p.getIncomeAmount());
				queryRep.setTime(p.getConfirmDate());
				queryRep.setProductName(p.getProduct().getName());
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
			pagesRep.setTotal(page.getTotalElements());

			rep.setDetails(pagesRep);
			// 第一页的时候要返回总收益
			if (totalIncomeFlag) {
				// 总收益
				rep.setTotalIncome(page.getContent().get(0).getPublisherHold().getHoldTotalIncome());
			}
		}
		
		
		
		return rep;
	}
	
	/**
	 * 查询 用户收益 (可根据产品OID查询)
	 */
	public PageResp<InvestorIncomeRep> queryInvestorIncome(Specification<InvestorIncomeEntity> spec,
			Pageable pageable) {

		Page<InvestorIncomeEntity> page = this.investorIncomeDao.findAll(spec, pageable);
		PageResp<InvestorIncomeRep> pagesRep = new PageResp<InvestorIncomeRep>();
		if (page != null && page.getContent() != null && page.getTotalElements() > 0) {
			List<InvestorIncomeRep> rows = new ArrayList<InvestorIncomeRep>();
			for (InvestorIncomeEntity p : page) {
				InvestorIncomeRep queryRep = new InvestorIncomeRep();
				queryRep.setAmount(p.getIncomeAmount());
				queryRep.setTime(p.getConfirmDate());
				queryRep.setProductName(p.getProduct().getName());
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
		}
		pagesRep.setTotal(page.getTotalElements());

		return pagesRep;
	}
	
	/**
	 * 我的收益明细
	 */
	public PageResp<MyInvestorIncomeDetailsRep> findMyIncomeByPages(Specification<InvestorIncomeEntity> spec ,Pageable pageable) {

		Page<InvestorIncomeEntity> page = this.investorIncomeDao.findAll(spec,pageable);
		PageResp<MyInvestorIncomeDetailsRep> pagesRep = new PageResp<MyInvestorIncomeDetailsRep>();
		if (page != null && page.getContent() != null && page.getTotalElements() > 0) {
			List<MyInvestorIncomeDetailsRep> rows = new ArrayList<MyInvestorIncomeDetailsRep>();
			for (InvestorIncomeEntity p : page) {
				MyInvestorIncomeDetailsRep queryRep = new MyInvestorIncomeDetailsRep(p);
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
		}
		pagesRep.setTotal(page.getTotalElements());
		
		return pagesRep;
	}
	
	/** 
	 * 累计 收益 页面 
	 */
	public IncomeRep queryMyTotalIncomeByDate(String investorOid, String yearMonth) {
		
		
		IncomeRep incomeRep = new IncomeRep();
		RowsRep<MyInvestorIncomeOfDateRep> details = new RowsRep<MyInvestorIncomeOfDateRep>();
		incomeRep.setDetails(details);
		
		InvestorBaseAccountEntity baseAccount = this.investorBaseAccountService.findOne(investorOid);
		InvestorStatisticsEntity st = this.investorStatisticsService.findByInvestorBaseAccount(baseAccount);
		incomeRep.setConfirmDate(st.getIncomeConfirmDate());
		incomeRep.setTotalIncome(st.getTotalIncomeAmount().add(st.getWishplanIncome()));
		
		List<InvestorIncomeEntity> incomes = this.investorIncomeDao.queryIncomeByYearMonth(baseAccount.getOid(), yearMonth);
		Date confirmDate = new Date(0L);
		MyInvestorIncomeOfDateRep dateRep = null;
		for (InvestorIncomeEntity income : incomes) {
			if (income.getWishplanOid() != null) {
				continue;
			}
			if (confirmDate.compareTo(income.getConfirmDate()) != 0) {
				dateRep = new MyInvestorIncomeOfDateRep();
				details.add(dateRep);
				confirmDate = income.getConfirmDate();
				dateRep.setDate(confirmDate);
			}
			if (Product.TYPE_Producttype_02.equals(income.getProduct().getType().getOid())) {
				dateRep.setT0Income(dateRep.getT0Income().add(income.getIncomeAmount()));
			}
			if (Product.TYPE_Producttype_01.equals(income.getProduct().getType().getOid())) {
				dateRep.setTnIncome(dateRep.getTnIncome().add(income.getIncomeAmount()));
			}
		}
		//wish plan
		for (InvestorIncomeEntity income : incomes) {
			
			if (income.getWishplanOid() == null) { 
				continue;
			}
			//Salary plan income daily, the other plan income only one time.
			if (income.getIncomeAllocate() != null && !planProductDao.findPlanTypeByOid(income.getWishplanOid()).equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
				continue;
			}
			//TODO:
//			if (income.getWishplanOid() == null || 
//					(income.getPublisherHold() != null && !planProductDao.findPlanTypeByOid(income.getWishplanOid()).equals(InvestTypeEnum.MonthSalaryInvest.getCode()))) {
//				continue;
//			}

			MyInvestorIncomeOfDateRep currentDateRep = null;
			for (MyInvestorIncomeOfDateRep dr : details.getRows()) {
				if (dr.getDate().compareTo(income.getConfirmDate()) == 0) {
					currentDateRep = dr;
				}
			}
			if (currentDateRep == null) {
				currentDateRep = new MyInvestorIncomeOfDateRep();
				details.add(currentDateRep);
				currentDateRep.setDate(income.getConfirmDate());
			}
			currentDateRep.setWishplanIncome(currentDateRep.getWishplanIncome().add(income.getIncomeAmount()));
		}
		
		// Sort the rows
		if (details.getRows().size() > 1) {
			Collections.sort(details.getRows(), new Comparator<MyInvestorIncomeOfDateRep>() {
				@Override
				public int compare(MyInvestorIncomeOfDateRep o1, MyInvestorIncomeOfDateRep o2) {
					int flag = o1.getDate().compareTo(o2.getDate()) > 0 ? 1 : -1;
					return flag;
				}
			});
		}
		
		return incomeRep;
	}
	
	/**
	 * 获取用户收益金额
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param investorOid
	 * @param productType
	 * @return
	 */
	public BigDecimal getCheckIncomeOfAcc(String checkTimeStart, String checkTimeEnd, String investorOid, String productType) {
		return this.investorIncomeDao.getCheckIncomeOfAcc(checkTimeStart, checkTimeEnd, investorOid, productType);
	}

	public List<String> queryToCloseHoldIncome(String lastOid) {
		return this.investorIncomeDao.queryToCloseHoldIncome(lastOid);
	}

	public InvestorIncomeEntity findByOid(String holdIncomeOid) {
		return this.investorIncomeDao.findOne(holdIncomeOid);
	}
}

