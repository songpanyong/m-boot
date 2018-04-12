package com.guohuai.mmp.publisher.product.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.ProductService;
import com.guohuai.component.util.DateUtil;

@Service
public class PublisherProductStatisticsService {

	@Autowired
	private PublisherProductStatisticsDao publisherProductStatisticsDao;
	@Autowired
	private ProductService productService;
	
	
	public void ys() {
		List<Object[]> list = publisherProductStatisticsDao.ys();
		List<PublisherProductYsPojo> pojos = new ArrayList<PublisherProductYsPojo>();
		for (int i = 0; i < list.size(); i++) {
				Object[] objArr = list.get(i);
				PublisherProductYsPojo pojo = new PublisherProductYsPojo();
				pojo.setProductOid((String)objArr[0]);
				pojo.setTodayInvestAmount((BigDecimal)objArr[1]);
		}
		for (PublisherProductYsPojo pojo : pojos) {
			PublisherProductStatisticsEntity latestEn = this.publisherProductStatisticsDao.getLatest(pojo.getProductOid());
			PublisherProductStatisticsEntity entity = new PublisherProductStatisticsEntity();
			entity.setProduct(productService.findByOid(pojo.getProductOid()));
			entity.setPublisherBaseAccount(entity.getProduct().getPublisherBaseAccount());
			entity.setInvestAmount(pojo.getTodayInvestAmount());
			if (null == latestEn) {
				entity.setTotalInvestAmount(pojo.getTodayInvestAmount());
			} else {
				entity.setTotalInvestAmount(latestEn.getTotalInvestAmount().add(pojo.getTodayInvestAmount()));
			}
			entity.setInvestDate(DateUtil.getBeforeDate());
			saveEntity(entity);
		}
	}

	private PublisherProductStatisticsEntity saveEntity(PublisherProductStatisticsEntity entity) {
		return this.publisherProductStatisticsDao.save(entity);
	}
	
	public List<PublisherProductStatisticsEntity> getTopFive() {
		List<PublisherProductStatisticsEntity> topFiveList = this.publisherProductStatisticsDao.getTopFive();
		return topFiveList;
	}
	
	public List<PublisherProductStatisticsEntity> getTopFive(String publisherOid) {
		List<PublisherProductStatisticsEntity> topFiveList = this.publisherProductStatisticsDao.getTopFive(publisherOid);
		return topFiveList;
	}

	
	

}
