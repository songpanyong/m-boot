package com.guohuai.ams.portfolio20.invest.losses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午6:45:33
 */
public interface PortfolioInvestLossesDao extends JpaRepository<PortfolioInvestLossesEntity, String>, JpaSpecificationExecutor<PortfolioInvestLossesEntity> {

}
