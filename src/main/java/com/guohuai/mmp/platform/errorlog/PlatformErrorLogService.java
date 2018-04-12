package com.guohuai.mmp.platform.errorlog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;

/**
 * 推广平台-请求日志信息
 * 
 * @author wanglei
 *
 */
@Service
public class PlatformErrorLogService {

	@Autowired
	private PlatformErrorLogDao platformErrorLogDao;
	

	public PageResp<PlatformErrorLogQueryRep> mng(Specification<PlatformErrorLogEntity> spec, Pageable pageable) {
		Page<PlatformErrorLogEntity> cas = this.platformErrorLogDao.findAll(spec, pageable);
		PageResp<PlatformErrorLogQueryRep> pagesRep = new PageResp<PlatformErrorLogQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PlatformErrorLogEntity entity : cas) {
				PlatformErrorLogQueryRep queryRep = new PlatformErrorLogQueryRep();
				queryRep.setOid(entity.getOid());
				queryRep.setUid(entity.getUid());
				queryRep.setReqUri(entity.getReqUri());
				queryRep.setParams(entity.getParams());
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}


	public BaseResp saveErrorLog(ErrorLogReq req, String uid) {
		PlatformErrorLogEntity errorLog = new PlatformErrorLogEntity();
		errorLog.setUid(uid);
		errorLog.setReqUri(req.getReqUri());
		errorLog.setParams(req.getParams());
		this.saveEntity(errorLog);
		return new BaseResp();
	}
	
	public PlatformErrorLogEntity saveEntity(PlatformErrorLogEntity entity) {
		return this.platformErrorLogDao.save(entity);
	}
	

	
}
