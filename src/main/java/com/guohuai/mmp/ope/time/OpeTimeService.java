package com.guohuai.mmp.ope.time;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DateUtil;

@Service
@Transactional
public class OpeTimeService {

	@Autowired
	private OpeTimeDao opeTimeDao;
	
	/**
	 * 新增
	 * @param en
	 * @return
	 */
	@Transactional
	public OpeTime saveEntity(OpeTime en){
		en.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(en);
	}
	
	/**
	 * 修改
	 * @param en
	 * @return
	 */
	@Transactional
	public OpeTime updateEntity(OpeTime en){
		en.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.opeTimeDao.save(en);
	}
	
	/**
	 * 根据OID查询
	 * @param oid
	 * @return
	 */
	public OpeTime findByOid(String oid){
		OpeTime entity = this.opeTimeDao.findOne(oid);
		return entity;
	}
	
	/**
	 * 根据name查询
	 * @param name
	 * @return
	 */
	public OpeTime findByName(String name){
		OpeTime entity = this.opeTimeDao.findByName(name);
		return entity;
	}
	
	/**
	 * 根据name查询时间
	 * @param name
	 * @return
	 */
	public long getTimeByName(String name){
		OpeTime en = this.opeTimeDao.findByName(name);
		
		if (en == null){
			en = new OpeTime();
			en.setName(name);
			en.setTime(0L);
			
			en = this.saveEntity(en);
		}
		
		if (en.getTime() == null){
			en.setTime(0L);
			
			this.updateEntity(en);
		}
		
		return en.getTime();
	}
	
	/**
	 * 保存时间
	 * @param name
	 * @param time
	 */
	public void setOpeTime(String name, Long time){
		OpeTime en = findByName(name);
		
		if (en == null){
			en = new OpeTime();
			en.setName(name);
			en.setTime(time);
			
			this.saveEntity(en);
		}else{
			en.setTime(time);
			
			this.updateEntity(en);
		}
	}
}
