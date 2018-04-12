package com.guohuai.mmp.platform.finance.check;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.guohuai.basic.component.ext.hibernate.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 平台-财务-三方对账
 * 
 * @author suzhicheng
 */
@Entity
@Table(name = "T_MONEY_PLATFORM_FINANCE_CHECK")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class PlatformFinanceCheckEntity extends UUID {
	/**
	* 
	*/
	private static final long serialVersionUID = 1646108955968857279L;
	public static final String PREFIX="CHEKDATA-";

	/** 对账状态--待对账 */
	public static final String  CHECKSTATUS_TOCHECK = "toCheck";
	/** 对账状态--对账中 */
	public static final String  CHECKSTATUS_CHECKING = "checking";
	/** 对账状态--对账成功 */
	public static final String  CHECKSTATUS_CHECKSUCCESS = "checkSuccess";
	/** 对账状态--对账失败 */
	public static final String  CHECKSTATUS_CHECKFAILED = "checkFailed";
	
	
	/** 轧账状态--待轧账 */
	public static final String  CHECK_gaStatus_toGa = "toGa";
	/** 轧账状态--轧账中 */
	public static final String  CHECK_gaStatus_gaing = "gaing";
	/** 轧账状态--轧账成功 */
	public static final String  CHECK_gaStatus_gaOk = "gaOk";
	/** 轧账状态--轧账失败 */
	public static final String  CHECK_gaStatus_gaFailed = "gaFailed";
	
	
	/** 对账数据同步状态--待同步 */
	public static final String  CHECKDATASYNCSTATUS_toSync = "toSync";
	/** 对账数据同步状态--同步失败 */
	public static final String  CHECKDATASYNCSTATUS_syncFailed = "syncFailed";
	/** 对账数据同步状态--同步成功 */
	public static final String  CHECKDATASYNCSTATUS_syncOK = "syncOK";
	/** 对账数据同步状态--导入中 */
	public static final String  CHECKDATASYNCSTATUS_syncing = "syncing";
	
	/** 本地对账数据--待准备 */
	public static final String  CHECK_ldataStatus_toPrepare = "toPrepare";
	/** 本地对账数据--ing */
	public static final String  CHECK_ldataStatus_prepareing = "prepareing";
	/** 本地对账数据--已准备 */
	public static final String  CHECK_ldataStatus_prepared = "prepared";
	/** 本地对账数据--准备失败 */
	public static final String  CHECK_ldataStatus_prepareFailed = "prepareFailed";
	
	
	/** 对账批次号   */
	private String checkCode;
	/** 对账日期  */
	private Date checkDate;
	
	
	/** 远程数据同步状态 */
	private String checkDataSyncStatus;
	
	/**
	 * 本地数据准备状态
	 */
	private String ldataStatus;
	
	
	/**
	 * 轧账状态
	 */
	private String gaStatus;
	
	
	/** 对账状态 */
	private String checkStatus;
	
	/**
	 * 远程账单总笔数
	 */
	private Integer totalCount;
	
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
