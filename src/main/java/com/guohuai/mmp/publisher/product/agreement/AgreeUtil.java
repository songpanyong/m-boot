package com.guohuai.mmp.publisher.product.agreement;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;

@Service
public class AgreeUtil {
	
	
	@Value(value = "${agreement.path}")
	String agreementPath;
	@Value(value = "${agreement.shell.path}")
	String agreementShellPath;
	@Value(value = "${agreement.log.path}")
	String agreementLogPath;
	
	
	public String abDir(InvestorTradeOrderEntity order) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(order.getOrderTime());
		StringBuilder sb = new StringBuilder();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);

		sb.append(this.agreementPath).append(order.getProduct().getOid()).append(File.separator);
		sb.append(year).append(File.separator).append(month < 10 ? "0" + month : month).append(File.separator)
				.append(day < 10 ? "0" + day : day).append(File.separator);

		return sb.toString();
	}
	
	public String abInvestName(String abDir, InvestorTradeOrderEntity order) {
		return abDir + order.getOrderCode() + "_" + ProductAgreementEntity.Agreement_agreementType_investing + ".html";
	}

	public String abServiceName(String abDir, InvestorTradeOrderEntity order) {
		return abDir + order.getOrderCode() + "_" + ProductAgreementEntity.Agreement_agreementType_service + ".html";
	}

	public String abInvestPDFName(String abDir, InvestorTradeOrderEntity order) {
		return abDir + order.getOrderCode() + "_" + ProductAgreementEntity.Agreement_agreementType_investing + ".pdf";
	}

	public String abServicePDFName(String abDir, InvestorTradeOrderEntity order) {
		return abDir + order.getOrderCode() + "_" + ProductAgreementEntity.Agreement_agreementType_service + ".pdf";
	}
	
	/**
	 * Below methods:
	 * Added & modified by chenxian 20180206
	 * @param orderTime
	 * @param oid
	 * @return
	 */
	public String abDirJJC(Date orderTime, String uid) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(orderTime);
		StringBuilder sb = new StringBuilder();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		sb.append(this.agreementPath).append(year).append(File.separator).append(month < 10 ? "0" + month : month).append(File.separator)
				.append(day < 10 ? "0" + day : day).append(File.separator);
		sb.append(uid).append(File.separator);
		return sb.toString();
	}
	
	public String abInvestNameJJC(String abDir, String codeId, int type) {
		return abDir + codeId + "_" + EbaoquanRecord.getCode(type) + ".html";
	}
	
	public String abInvestPDFNameJJC(String abDir, String codeId, int type) {
		return abDir + codeId + "_" + EbaoquanRecord.getCode(type) + ".pdf";
	}
	
}
