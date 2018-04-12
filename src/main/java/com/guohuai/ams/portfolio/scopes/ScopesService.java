package com.guohuai.ams.portfolio.scopes;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.dict.Dict;
import com.guohuai.ams.dict.DictService;
import com.guohuai.component.util.StringUtil;

@Service
public class ScopesService {

	@Autowired
	private ScopesDao scopeDao;

	@Autowired
	private DictService dictService;

	/**
	 * 投资范围
	 * 
	 * @param assetPool
	 * @param assetTypeOid
	 * @return
	 */
	@Transactional
	public ScopesEntity save(String relationOid, String assetTypeOid) {
		// 删除原先的范围数据

		ScopesEntity entity = new ScopesEntity();
		entity.setOid(StringUtil.uuid());
		entity.setRelationOid(relationOid);
		entity.setAssetType(dictService.get(assetTypeOid));

		return scopeDao.save(entity);
	}
	
	@Transactional
	public void delete(String oid) {
		this.scopeDao.delete(oid);
	}
	
	@Transactional
	public List<ScopesEntity> list(String relationOid) {
		return this.scopeDao.findByRelationOid(relationOid);
	}

	/**
	 * 获取投资范围
	 * 
	 * @param pid
	 * @return
	 */
	@Transactional
	public List<Dict> getScopes(String pid) {
		List<ScopesEntity> list = scopeDao.findByRelationOid(pid);
		List<Dict> dicts = new ArrayList<Dict>();
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				dicts.add(list.get(i).getAssetType());
			}
		}
		return dicts;
	}
}
