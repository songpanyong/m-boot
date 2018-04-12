package com.guohuai.mmp.investor.baseaccount.refer.details;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.referee.InvestorRefEreeEntity;

/**
 * 推荐人查询
 * @author wanglei
 *
 */
@Service
@Transactional
public class InvestoRefErDetailsService {

	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	
	@Autowired
	InvestoRefErDetailsDao investoRefErDetailsDao;

	/**我推荐的列表*/
	public PageResp<InvestoRefErDetailsRep> referlist(Specification<InvestoRefErDetailsEntity> spec,
			Pageable pageable) {

		PageResp<InvestoRefErDetailsRep> rep = new PageResp<InvestoRefErDetailsRep>();

		Page<InvestoRefErDetailsEntity> page = this.investoRefErDetailsDao.findAll(spec, pageable);
		if (page != null && page.getSize() > 0 && page.getTotalElements() > 0) {
			
			List<InvestoRefErDetailsRep> rows = new ArrayList<InvestoRefErDetailsRep>();
			
			for (InvestoRefErDetailsEntity entity : page) {
				InvestoRefErDetailsRep row = new InvestoRefErDetailsRep(entity, this.kickstar);
				rows.add(row);
			}
			rep.setRows(rows);
			rep.setTotal(page.getTotalElements());
		}

		return rep;
	}

	/**
	 * 推荐排名统计，前10名
	 * @return
	 */
	public PageResp<InvestoRefErDetailsRankRep> recommendRankTOP10() {
		PageResp<InvestoRefErDetailsRankRep> pages = new PageResp<InvestoRefErDetailsRankRep>();

		List<Object[]> tops = this.investoRefErDetailsDao.recommendRankTOP10();
		
		for (Object[] obj : tops) {
			if (null != obj) {
				InvestoRefErDetailsRankRep rep = new InvestoRefErDetailsRankRep();
				//　手机号
				rep.setPhoneNum(this.kickstar ? StringUtil.kickstarOnPhoneNum(null == obj[0] ? StringUtil.EMPTY : obj[0].toString()) : (null == obj[0] ? StringUtil.EMPTY : obj[0].toString()));
				//　实名认证
				rep.setRealName(this.kickstar ? StringUtil.kickstarOnRealname(null == obj[1] ? StringUtil.EMPTY : obj[1].toString()) : (null == obj[1] ? StringUtil.EMPTY : obj[1].toString()));
				// 推荐人数
				rep.setRecommendCount(obj[2].toString());
				pages.getRows().add(rep);
			}
		}
		pages.setTotal(tops.size());
		
		return pages;
	}
	
	/**
	 * 新增
	 * @param entity
	 * @return
	 */
	public InvestoRefErDetailsEntity saveEntity(InvestoRefErDetailsEntity entity){
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}
	
	/**
	 * 修改
	 * @param entity
	 * @return
	 */
	private InvestoRefErDetailsEntity updateEntity(InvestoRefErDetailsEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.investoRefErDetailsDao.save(entity);
	}
	
	/**
	 * 删除推荐明细关系
	 * @param account
	 * @param investorRefEreeEntity
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void delRefErDetails(InvestorBaseAccountEntity account, InvestorRefEreeEntity investorRefEreeEntity) {
		List<InvestoRefErDetailsEntity> list =  this.investoRefErDetailsDao.findByInvestorBaseAccount(account);
		
		List<InvestoRefErDetailsEntity> listDetails =  this.investoRefErDetailsDao.findByInvestorRefEree(investorRefEreeEntity);
		
		if (null != list && list.size() > 0) {
			this.investoRefErDetailsDao.delete(list);
		}
		
		if (null != listDetails && listDetails.size() > 0) {
			this.investoRefErDetailsDao.delete(listDetails);
		}
	}
}
