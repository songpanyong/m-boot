package com.guohuai.mmp.publisher.investor.interest.result;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.duration.fact.income.IncomeAllocate;
import com.guohuai.ams.duration.fact.income.IncomeDistributionService;
import com.guohuai.ams.product.Product;




@Service
@Transactional
public class InterestResultService {
	private static final Logger logger = LoggerFactory.getLogger(InterestResultService.class);
	@Autowired
	IncomeDistributionService incomeDistributionService;
	@Autowired
	InterestResultDao InterestResultDao;

	public InterestResultEntity createEntity(Product product, IncomeAllocate incomeAllocate, Date incomeDate) {
		InterestResultEntity entity = new InterestResultEntity();
		entity.setProduct(product);
		entity.setIncomeAllocate(incomeAllocate);
		entity.setAllocateDate(incomeDate);
		return entity;
	}

	public InterestResultEntity saveEntity(InterestResultEntity result) {
		return this.InterestResultDao.save(result);
	}
	
	public Page<InterestResultEntity>  findPage(Specification<InterestResultEntity> spec, Pageable pageable) {
		return this.InterestResultDao.findAll(spec, pageable);
	}

	public void send(InterestResultEntity result) {
		
		try {
			
			incomeDistributionService.allocateIncome(result);
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
		
	}

	/**
	 * 累计计息总额统计
	 */
	public List<Object[]> getTotalInterestAmount() {
		return this.InterestResultDao.getTotalInterestAmount();
	}
	
	


	

	
	
	
}
