package com.guohuai.ams.portfolio.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssetTradeDao
		extends JpaRepository<AssetTradeEntity, String>, JpaSpecificationExecutor<AssetTradeEntity> {

}
