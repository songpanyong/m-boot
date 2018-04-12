package com.guohuai.mmp.jiajiacai.wishplan.risklevel;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.StringUtil;
import com.guohuai.mmp.investor.sonaccount.SonAccountDao;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;
import com.guohuai.mmp.jiajiacai.caculate.StringUtils;

@Service
public class RiskLevelService {
	
	@Autowired 
	private RiskLevelDao riskLevelDao;
	
	@Autowired
	SonAccountDao sonAccountDao;
	
	RiskLevelEntity saveRiskLevel(String userOid ,String level) {
		List<RiskLevelEntity> list = riskLevelDao.queryByUserOid(userOid);
		RiskLevelEntity result =null;
		if(list == null || list.size()==0) {
			RiskLevelEntity risklevel = new RiskLevelEntity(StringUtils.uuid(), userOid, level);
			result = riskLevelDao.save(risklevel);
		}else {
			int temp = riskLevelDao.updateEntityByOid(list.get(0).getOid(), level);
			if(temp ==1) {
				result = queryUserRiskLevel(list.get(0).getUserOid());
			}
		}
		//同时更新主账户所有名下的子账户
		this.updateSonRiskLevel(userOid,level);
		return result;
	}

	/**主账户名下的所有的子账户的风险评测都变为子账户的风险评测*/
	private void updateSonRiskLevel(String userOid,String level) {
		//获取所有的子账户
		List<SonAccountEntity> sonAccount = this.sonAccountDao.findByPid(userOid);
		if(sonAccount!=null&&sonAccount.size()>0){
			for(SonAccountEntity son:sonAccount){
				//从风险评测的表中查找是否有子账户的风险评测结果
				RiskLevelEntity  riskEntity = null;
				 riskEntity = this.riskLevelDao.findByUserOid(son.getSid());
				if(riskEntity!=null){
					riskEntity.setRiskLevel(level);					
				}else{
				 riskEntity = new RiskLevelEntity();
				 riskEntity.setOid(StringUtil.uuid());
				 riskEntity.setUserOid(son.getSid());
				 riskEntity.setRiskLevel(level);
				}
				//保存
				this.riskLevelDao.save(riskEntity);
			}
		}
		
	}

	RiskLevelEntity queryUserRiskLevel(String userOid) {
		List<RiskLevelEntity> list = riskLevelDao.queryByUserOid(userOid);
		if(list == null || list.size()==0) {
			return null;
		}
		return list.get(0);
	}

}
