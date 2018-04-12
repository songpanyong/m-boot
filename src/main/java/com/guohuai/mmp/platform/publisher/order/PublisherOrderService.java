package com.guohuai.mmp.platform.publisher.order;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class PublisherOrderService {


	@Autowired
	private PublisherOrderDao publisherOrderDao;
	
	

	public PublisherOrderEntity saveEntity(PublisherOrderEntity order) {
		order.setCreateTime(DateUtil.getSqlCurrentDate());
		return updateEntity(order);
	}

	public PublisherOrderEntity updateEntity(PublisherOrderEntity order) {
		order.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.publisherOrderDao.save(order);
	}

	public PublisherOrderEntity findByOrderCode(String orderCode) {
		PublisherOrderEntity order = this.publisherOrderDao.findByOrderCode(orderCode);
		if (null == order) {
			throw new AMPException("订单不存在");
		}
		return order;
	}
	
	public PublisherOrderEntity findByOid(String orderCode) {
		PublisherOrderEntity order = this.publisherOrderDao.findOne(orderCode);
		if (null == order) {
			throw new AMPException("订单不存在");
		}
		return order;
	} 

	

}
