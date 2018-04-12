package com.guohuai.ams.portfolio20.net.correct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午12:23:05
 */
public interface PortfolioNetCorrectDao extends JpaRepository<PortfolioNetCorrectEntity, String>, JpaSpecificationExecutor<PortfolioNetCorrectEntity> {

}
