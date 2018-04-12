package com.guohuai.mmp.platform.redis;


import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RedisSyncService {

	@Autowired
	private RedisSyncDao redisSyncDao;

	@Transactional(value = TxType.REQUIRES_NEW)
	public RedisSyncEntity saveEntityRefProductRequireNew(String investorOid, String productOid, String assetPoolOid) {
		RedisSyncEntity entity = new RedisSyncEntity();
		entity.setSyncOid(investorOid);
		entity.setProductOid(productOid);
		entity.setAssetPoolOid(assetPoolOid);
		entity.setSyncOidType(RedisSyncEntity.SYNC_syncOidType_investor);
		return this.saveEntity(entity);
	}
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public RedisSyncEntity saveEntityRefInvestorHoldRequireNew(String investorOid, String productOid) {
		RedisSyncEntity entity = new RedisSyncEntity();
		entity.setSyncOid(investorOid);
		entity.setProductOid(productOid);
		
		entity.setSyncOidType(RedisSyncEntity.SYNC_syncOidType_investor);
		return this.saveEntity(entity);
	}
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public RedisSyncEntity saveEntityRefSpvHoldRequireNew(String investorOid, String productOid, String assetPoolOid) {
		RedisSyncEntity entity = new RedisSyncEntity();
		entity.setSyncOid(investorOid);
		entity.setProductOid(productOid);
		entity.setAssetPoolOid(assetPoolOid);
		entity.setSyncOidType(RedisSyncEntity.SYNC_syncOidType_investor);
		return this.saveEntity(entity);
	}
	

	public RedisSyncEntity saveEntity(RedisSyncEntity entity) {
		return this.redisSyncDao.save(entity);
	}
	
}
