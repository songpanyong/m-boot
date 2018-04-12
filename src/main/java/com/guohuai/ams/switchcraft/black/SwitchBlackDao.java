package com.guohuai.ams.switchcraft.black;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SwitchBlackDao extends JpaRepository<SwitchBlackEntity, String>, JpaSpecificationExecutor<SwitchBlackEntity>{

	public SwitchBlackEntity findBySwitchOidAndUserAcc(String switchOid, String userAcc);

	public SwitchBlackEntity findBySwitchOidAndUserOid(String switchOid, String userOid);

	public void deleteBySwitchOid(String switchOid);
}
