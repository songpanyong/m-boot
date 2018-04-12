package com.guohuai.mmp.investor.sonaccount;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SonAccountDao extends JpaRepository<SonAccountEntity, Long>, JpaSpecificationExecutor<SonAccountEntity> {


	@Query(value = "select * from t_money_investor_sonaccount t WHERE t.status = ?1", nativeQuery = true)
	public List<SonAccountEntity> gStatus(Integer status);

	public SonAccountEntity findByPidAndNickname(String nickname, String pid);
	
	@Modifying
	@Query(value="update t_money_investor_sonaccount t set t.status = -1 where t.sid = ?1",nativeQuery = true)
	public void modifyBySid(String sMemberId);
	/**  查询子账户的信息     */
	public SonAccountEntity findBySid(String sMemberId);
	/**  创建子账户  */
	public SonAccountEntity findByNickname(String nickName);
	/**   根据pid和sid来查询      
	 * @return */
	public SonAccountEntity findByPidAndSid(String id, String userId);
	/**   查询主账户关联下的所有子账号信息   */
	public List findByPid(String oid);
	/**    通过主id和主子的状态来查询出所有的子账户的集合     */
	@Query(value="select * from t_money_investor_sonaccount s where s.pid = ?1 and s.status <>-1",nativeQuery =true)
	public List<SonAccountEntity> findByPidAndStatus(String oid);

	@Query(value="select * from t_money_investor_sonaccount s where s.nickname = ?1 and s.status <>-1 limit 1",nativeQuery =true)
	public SonAccountEntity findByNicknameAndStatus(String nickname);
	
	@Query(value="select nickname from t_money_investor_sonaccount where oid = ?1 limit 1",nativeQuery =true)
	public String queryNicknameByOid(String oid);
	
	/** 查询主账户下的子账户数量  */
	@Query(value ="select count(*) from t_money_investor_sonaccount t where t.pid = ?1 and t.status <>-1",nativeQuery =true)
	public Integer querySonAccountCount(String pid);


	
	
	
	

	
	
	

	

}
