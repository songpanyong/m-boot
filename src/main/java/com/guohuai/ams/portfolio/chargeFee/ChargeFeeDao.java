package com.guohuai.ams.portfolio.chargeFee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeFeeDao
		extends JpaRepository<ChargeFeeEntity, String>, JpaSpecificationExecutor<ChargeFeeEntity> {

}
