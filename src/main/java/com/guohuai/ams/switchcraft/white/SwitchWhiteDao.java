package com.guohuai.ams.switchcraft.white;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SwitchWhiteDao extends JpaRepository<SwitchWhiteEntity, String>, JpaSpecificationExecutor<SwitchWhiteEntity>{

	public SwitchWhiteEntity findBySwitchOidAndUserAcc(String switchOid, String userAcc);

	public SwitchWhiteEntity findBySwitchOidAndUserOid(String switchOid, String userOid);

	public void deleteBySwitchOid(String switchOid);
}
