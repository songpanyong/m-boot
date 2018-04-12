/**
 * 
 */
package com.guohuai.mmp.investor.tradeorder;

/**
 * @author Administrator
 *
 */
@lombok.Data
public class FileParam {

	/**甲方 */
	private String firstParty = "";
	/** 乙方 */
	private String secondParty= "";
	/** 签署时间*/
	private String tradeTime= "";
	/** 合同编号 */
	private String constractNum= "";
	
	private  String firstPartyIdNum = ""; 
}
