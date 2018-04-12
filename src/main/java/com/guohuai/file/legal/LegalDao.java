package com.guohuai.file.legal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LegalDao extends JpaRepository<LegalEntity, String>, JpaSpecificationExecutor<LegalEntity> {

	public LegalEntity findByCode(String code);

	public List<LegalEntity> findByName(String name);

	public List<LegalEntity> findByStatus(String status);

	@Query(value = "insert into T_GAM_LEGAL (oid, code ,name, status, operator) "
			+ "select replace(uuid(), '-', ''), case when max(code) is null then '10000' else MAX(CODE)+1+'' end,"
			+ " ?1, 'disabled',?2 from T_GAM_LEGAL", nativeQuery = true)
	@Modifying
	public int createEntity(String name, String operator);

	/**
	 * Added by chenxian 20180207
	 * @param code
	 * @return
	 */
	@Query(value = "SELECT status FROM T_GAM_LEGAL WHERE code=?1", nativeQuery = true)
	public String findStatusByCode(String code);
}
