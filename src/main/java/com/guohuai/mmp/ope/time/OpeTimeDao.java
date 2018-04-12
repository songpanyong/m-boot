package com.guohuai.mmp.ope.time;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OpeTimeDao extends JpaRepository<OpeTime, String>, JpaSpecificationExecutor<OpeTime> {

	OpeTime findByName(String name);
}
