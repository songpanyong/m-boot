package com.guohuai.mmp.publisher.baseaccount;

import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PublisherDetailRep extends BaseResp {
	
	public PublisherDetailRep(PublisherBaseAccountEntity entity) {
		this.oid = entity.getOid();
		
		this.name = entity.getRealName();
		
	}
	
	private String oid;
	// 公司简称
	private String name;
	// 公司名称
	private String companyName;
	// 账户类型
	private String accountType;
	// 账户账号
	private String accountNo;
	// 企业网址
	private String website;
	// 企业地址
	private String address;
	// 联系人
	private String contact;
	// 联系电话
	private String telephone;
	// 联系Email
	private String email;
	// 发行人状态(normal:正常,disabled:禁用)
	private String status;
	// 银行名称
	private String bankName;
	// 银行账户
	private String bankAccount;
	// 营业执照
	private String licenceNo;
	// 更新时间
	private Timestamp updateTime;
	// 创建时间
	private Timestamp createTime;

}
