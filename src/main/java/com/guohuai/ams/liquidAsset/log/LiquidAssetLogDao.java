package com.guohuai.ams.liquidAsset.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LiquidAssetLogDao
		extends JpaRepository<LiquidAssetLog, String>, JpaSpecificationExecutor<LiquidAssetLog> {

}
