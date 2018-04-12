package com.guohuai.file.legal.file;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.file.legal.LegalEntity;




public interface LegalFileDao extends JpaRepository<LegalFileEntity, String>, JpaSpecificationExecutor<LegalFileEntity> {

	public List<LegalFileEntity> findByTypeAndStatus(LegalEntity type, String status);
	
	public List<LegalFileEntity> findByName(String name);
	
	public List<LegalFileEntity> findByType(LegalEntity type);

	
	/**
	 * gam_legal added by chenxian
	 * @param code
	 * @return
	 */
	@Query(value = "SELECT f.fileUrl FROM t_gam_legal l, t_gam_legal_file f"
	        + " WHERE l.code = ?1 AND f.type = l.oid", nativeQuery = true)
    public String findFileByCode(String code);


	@Query(value="SELECT * FROM t_gam_legal_file t1,t_gam_legal t2 "
			+ " WHERE t2.oid=t1.type AND t2.status='enabled' AND t1.status='enabled' AND t2.code=?1 ",nativeQuery = true)
	public List<LegalFileEntity> findByTypeCode(String typeCode);

}
