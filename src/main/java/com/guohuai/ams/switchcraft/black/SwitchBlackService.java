package com.guohuai.ams.switchcraft.black;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.switchcraft.black.SwitchBlackQueryRep.SwitchBlackQueryRepBuilder;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;

@Service
@Transactional
public class SwitchBlackService {

	@Autowired
	private SwitchBlackDao switchBlackDao;
	
	/**
	 * 根据OID获取
	 * @param oid
	 * @return
	 */
	public SwitchBlackEntity getByOid(String oid){
		SwitchBlackEntity en = this.switchBlackDao.findOne(oid);
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
	public SwitchBlackEntity findBySwitchOidAndUserAcc(String switchOid, String userAcc){
		SwitchBlackEntity en = this.switchBlackDao.findBySwitchOidAndUserAcc(switchOid, userAcc);
		return en;
	}
	
	/**
	 * 根据开关oid和用户oid获取白名单
	 * @return
	 */
	public SwitchBlackEntity findBySwitchOidAndUserOid(String switchOid, String userOid){
		SwitchBlackEntity en = this.switchBlackDao.findBySwitchOidAndUserOid(switchOid, userOid);
		return en;
	}
	
	/**
	 * 列表查询
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<SwitchBlackQueryRep> query(Specification<SwitchBlackEntity> spec, Pageable pageable) {		
		Page<SwitchBlackEntity> ens = this.switchBlackDao.findAll(spec, pageable);

		PageResp<SwitchBlackQueryRep> pagesRep = new PageResp<SwitchBlackQueryRep>();

		for (SwitchBlackEntity en : ens) {
			SwitchBlackQueryRep rep = new SwitchBlackQueryRepBuilder()
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
		SwitchBlackEntity en = this.getByOid(oid);
		this.switchBlackDao.delete(en);
		return rep;
	}
	
	/**
	 * 根据配置oid删除
	 * @param oid
	 * @return
	 */
	public BaseResp delBySwitchOid(String switchOid){
		BaseResp rep = new BaseResp();
		this.switchBlackDao.deleteBySwitchOid(switchOid);
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
		SwitchBlackEntity en = new SwitchBlackEntity();
		en.setSwitchOid(switchOid);
		en.setUserOid(userOid);
		en.setUserAcc(userAcc);
		en.setNote(note);
		en.setOperator(operator);
		
		this.switchBlackDao.save(en);
	}
}
