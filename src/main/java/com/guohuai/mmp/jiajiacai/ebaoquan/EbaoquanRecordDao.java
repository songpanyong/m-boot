package com.guohuai.mmp.jiajiacai.ebaoquan;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface EbaoquanRecordDao
		extends JpaRepository<EbaoquanRecord, String>, JpaSpecificationExecutor<EbaoquanRecord> {

//	List<EbaoquanRecord> findByDtContractIdAndDtContractType(int dtContractId, int dtContractType);

	@Query(value = "SELECT * FROM t_ebaoquan_record r WHERE r.status IN (?1) ORDER BY r.createAt ASC LIMIT 20", nativeQuery = true)
	public List<EbaoquanRecord> findByStatusList(List<String> statusList);
	
	
	@Query(value = "SELECT COUNT(*) FROM t_ebaoquan_record r WHERE r.relatedKey=?1 AND r.dtContractType=?2", nativeQuery = true)
	public int baoquanCount(String key, int type);

	@Query(value="select * from t_ebaoquan_record t where t.relatedKey = ?1  and t.dtContractType in ?2",nativeQuery = true)
	public EbaoquanRecord findByRelateKey(String relateKey,List<Integer> typeList);


	@Modifying
	@Query(value = "UPDATE t_ebaoquan_record SET status = 'toHtml' WHERE relatedKey = ?1 AND status = 'toRealname'", nativeQuery = true)
	public int toHtml(String relatedKey);
	
	public EbaoquanRecord findByCodeId(String string);
	
}
