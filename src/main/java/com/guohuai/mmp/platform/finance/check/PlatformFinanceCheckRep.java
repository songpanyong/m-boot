package com.guohuai.mmp.platform.finance.check;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 平台-财务-三方对账
 * 
 * @author suzhicheng
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class PlatformFinanceCheckRep {
	
	/**
	 * 对账批次OID
	 */
	private String checkOid;
	
	/** 对账批次号   */
	private String checkCode;
	/** 对账日期  */
	private Date checkDate;
	/** 对账状态 */
	private String checkStatus;
	private String  checkStatusDisp;
	
	/** 远程数据同步状态 */
	private String checkDataSyncStatus;
	private String checkDataSyncStatusDisp;
	
	/**
	 * 本地数据准备状态
	 */
	private String ldataStatus;
	private String ldataStatusDisp;
	
	/**
	 * 轧账状态
	 */
	private String gaStatus;
	private String gaStatusDisp;
	
	/** 错账笔数 */
	private Integer wrongCount;
	
	
	/**
	 * 待处理错账笔数
	 */
	private Integer wrongLeftCount;
	
	
	/**
	 * 对账开始时间
	 */
	private  Timestamp beginTime;
	/**
	 * 对账结束时间
	 */
	private Timestamp endTime;
	
	
	private Timestamp createTime;
	private Timestamp updateTime;


	
	
}
