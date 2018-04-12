package com.guohuai.ams.switchcraft;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SwitchDao extends JpaRepository<SwitchEntity, String>, JpaSpecificationExecutor<SwitchEntity>{

	SwitchEntity findByCode(String code);

}
