package com.guohuai.ams.switchcraft.white;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.switchcraft.white.SwitchWhiteQueryRep.SwitchWhiteQueryRepBuilder;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;

@Service
@Transactional
public class SwitchWhiteService {

	@Autowired
	private SwitchWhiteDao switchWhiteDao;
	
	/**
	 * 根据OID获取
	 * @param oid
	 * @return
	 */
	public SwitchWhiteEntity getByOid(String oid){
		SwitchWhiteEntity en = this.switchWhiteDao.findOne(oid);
		if(en==null){
			//系统开关白名单不存在！(CODE:130004)
			throw GHException.getException(130004);
		}
		return en;
	}
	
	/**
	 * 根据开关oid和用户手机获取白名单
	 * @return
	 */
	public SwitchWhiteEntity findBySwitchOidAndUserAcc(String switchOid, String userAcc){
		SwitchWhiteEntity en = this.switchWhiteDao.findBySwitchOidAndUserAcc(switchOid, userAcc);
		return en;
	}
	
	/**
	 * 根据开关oid和用户oid获取白名单
	 * @return
	 */
	public SwitchWhiteEntity findBySwitchOidAndUserOid(String switchOid, String userOid){
		SwitchWhiteEntity en = this.switchWhiteDao.findBySwitchOidAndUserOid(switchOid, userOid);
		return en;
	}
	
	/**
	 * 列表查询
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<SwitchWhiteQueryRep> query(Specification<SwitchWhiteEntity> spec, Pageable pageable) {		
		Page<SwitchWhiteEntity> ens = this.switchWhiteDao.findAll(spec, pageable);

		PageResp<SwitchWhiteQueryRep> pagesRep = new PageResp<SwitchWhiteQueryRep>();

		for (SwitchWhiteEntity en : ens) {
			SwitchWhiteQueryRep rep = new SwitchWhiteQueryRepBuilder()
					.oid(en.getOid())
					.userAcc(en.getUserAcc())
					.operator(en.getOperator())
					.note(en.getNote())
					.createTime(en.getCreateTime())
					.updateTime(en.getUpdateTime())
					.build();
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(ens.getTotalElements());	
		return pagesRep;
	}
	
	/**
	 * 删除
	 * @param oid
	 * @return
	 */
	public BaseResp del(String oid){
		BaseResp rep = new BaseResp();
		SwitchWhiteEntity en = this.getByOid(oid);
		this.switchWhiteDao.delete(en);
		return rep;
	}
	
	/**
	 * 根据配置oid删除
	 * @param oid
	 * @return
	 */
	public BaseResp delBySwitchOid(String switchOid){
		BaseResp rep = new BaseResp();
		this.switchWhiteDao.deleteBySwitchOid(switchOid);
		return rep;
	}
	
	/**
	 * 添加
	 * @param switchOid
	 * @param userOid
	 * @param userAcc
	 * @param note
	 * @param operator
	 */
	public void add(String switchOid, String userOid, String userAcc, String note, String operator){
		SwitchWhiteEntity en = new SwitchWhiteEntity();
		en.setSwitchOid(switchOid);
		en.setUserOid(userOid);
		en.setUserAcc(userAcc);
		en.setNote(note);
		en.setOperator(operator);
		
		this.switchWhiteDao.save(en);
	}
}
