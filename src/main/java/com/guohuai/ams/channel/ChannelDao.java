package com.guohuai.ams.channel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ChannelDao extends JpaRepository<Channel, String>, JpaSpecificationExecutor<Channel> {

	public List<Channel> findByOidIn(List<String> oids);
	
	public List<Channel> findByApproveStatus(String approveStatus);
	
	public Channel findByCidAndCkey(String cid, String ckey);

	public Channel findByCid(String cid);
	
	public List<Channel> findByDeleteStatus(String deleteStatus);
	
	@Query(value = "select * from T_MONEY_PLATFORM_CHANNEL order by createTime desc limit 10", nativeQuery = true)
	public List<Channel> getLatestTen();
	
}
