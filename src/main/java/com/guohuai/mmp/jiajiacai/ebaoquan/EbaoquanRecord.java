package com.guohuai.mmp.jiajiacai.ebaoquan;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_ebaoquan_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class EbaoquanRecord implements Serializable {

	private static final long serialVersionUID = 7644481711548905139L;
	/**
	 * code
	 */
	/** 家加财平台注册服务协议 */
	public static final String EBAOQUAN_CODE_REGIST = "10000";
	/** 家加财活期产品转入协议 */
	public static final String EBAOQUAN_CODE_INVEST_OPEN = "10001";
	/** 家加财定期期产品转入协议 */
	public static final String EBAOQUAN_CODE_INVEST_CLOSE = "10002";
	/** 家加财定向委托投资协议 */
	public static final String EBAOQUAN_CODE_AGREE = "10003";
	/** 心愿计划转入协议 */
	public static final String EBAOQUAN_CODE_INVEST_WISHPLAN = "10006";
	/** 代扣服务协议 */
	public static final String EBAOQUAN_CODE_WITHHOLD = "10007";
	/**
	 * type
	 */
	/** 家加财平台注册服务协议 */
	public static final int EBAOQUAN_TYPE_REGIST = 1;

	/** 家加财活期产品转入协议 */
	public static final int EBAOQUAN_TYPE_INVEST_OPEN = 2;
	/** 家加财定期期产品转入协议 */
	public static final int EBAOQUAN_TYPE_INVEST_CLOSE = 3;

	/** 心愿计划转入协议一次性 */
	public static final int EBAOQUAN_TYPE_WISHPLAN_ONCE = 5;
	/** 心愿计划转入协议月定投 */
	public static final int EBAOQUAN_TYPE_WISHPLAN_MONTH = 6;

	/** 代扣服务协议 */
	public static final int EBAOQUAN_TYPE_WITHHOLD = 7;

	/** 家加财定向委托投资协议活期 */
	public static final int EBAOQUAN_TYPE_AGREE_OPEN = 8;
	/** 家加财定向委托投资协议定期 */
	public static final int EBAOQUAN_TYPE_AGREE_CLOSE = 9;
	/** 家加财定向委托投资协议心愿一次性 */
	public static final int EBAOQUAN_TYPE_AGREE_WISHPLAN_ONCE = 10;
	/** 家加财定向委托投资协议月定投 */
	public static final int EBAOQUAN_TYPE_AGREE_WISHPLAN_MONTH = 11;
	/**
	 * status
	 */
	/** 合同生成状态--toRigist */
	public static final String EBAOQUAN_CONTRACTSTATUS_TOREALNAME = "toRealname";
	/** 合同生成状态--等待生成html */
	public static final String EBAOQUAN_CONTRACTSTATUS_TOHTML = "toHtml";
	/** 合同生成状态--生成html成功 */
	public static final String EBAOQUAN_CONTRACTSTATUS_HTMLOK = "htmlOK";
	/** 合同生成状态--生成html失败 */
	public static final String EBAOQUAN_CONTRACTSTATUS_HTMLFAIL = "htmlFail";
	/** 合同生成状态--生成PDF成功 */
	public static final String EBAOQUAN_CONTRACTSTATUS_PDFOK = "pdfOK";
	/** 合同生成状态--生成PDF失败 */
	public static final String EBAOQUAN_CONTRACTSTATUS_PDFFAIL = "pdfFail";
	/** 合同生成状态--上传PDF成功 */
	public static final String EBAOQUAN_CONTRACTSTATUS_UPPDFOK = "upPdfOK";
	/** 合同生成状态--上传PDF失败 */
	public static final String EBAOQUAN_CONTRACTSTATUS_UPPDFFAIL = "upPdfFail";

	private static final Map<String, String> codeMap = createMap();

	private static Map<String, String> createMap() {
		Map<String, String> myMap = new HashMap<String, String>();
		myMap.put("1", EBAOQUAN_CODE_REGIST);
		myMap.put("2", EBAOQUAN_CODE_INVEST_OPEN);
		myMap.put("3", EBAOQUAN_CODE_INVEST_CLOSE);

		myMap.put("5", EBAOQUAN_CODE_INVEST_WISHPLAN);
		myMap.put("6", EBAOQUAN_CODE_INVEST_WISHPLAN);

		myMap.put("7", EBAOQUAN_CODE_WITHHOLD);

		myMap.put("8", EBAOQUAN_CODE_AGREE);
		myMap.put("9", EBAOQUAN_CODE_AGREE);
		myMap.put("10", EBAOQUAN_CODE_AGREE);
		myMap.put("11", EBAOQUAN_CODE_AGREE);
		return myMap;
	}

	/**
	 * getCode
	 * 
	 * @param type
	 * @return
	 */
	public static String getCode(int type) {
		return codeMap.get(String.valueOf(type));
	}

	
	@Id
	private String oid;
	
//	@Id
//	private int id;

//	// 债权转让交易ID
//	private int dtContractId;
//	// 债权转让协议ID
//	private int dtId;
	// 保全Hash值
	private String docHash;
//	// 保全时间戳
//	private String preservationTime;

	// pdf文件下载URL
	private String downloadUrl;
	// 创建日期
	private Timestamp createAt;
	// 更新日期;
	private Timestamp updateAt;

	// 保全类型
	private int dtContractType;
	// 保全ID
	private String preservationId;
	// 保全状态
	private String status;
	// 保全关联外键
	private String relatedKey;
	// 保全编号
	private String codeId;
	// 保全文件路径
	private String fileDir;
	// 上次获取url日期;
	private Timestamp lastAccessTime;
	
	private String fileParam;


}
