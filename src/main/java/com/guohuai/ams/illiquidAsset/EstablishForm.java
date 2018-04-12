/*   
 * Copyright © 2015 guohuaigroup All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.guohuai.ams.illiquidAsset;

import java.io.Serializable;
import java.sql.Date;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标的成立表单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstablishForm implements Serializable {

	/**
	 * @Fields serialVersionUID :
	 */

	private static final long serialVersionUID = 2246250729662434487L;
	@NotNull(message = "投资标的id不能为空")
	private String oid;

	private String risk;

	@NotNull(message = "投资标的成立日期不能为空")
	private Date setDate;
	@NotNull(message = "收益起始日不能为空")
	private Date incomeStartDate;
	@NotNull(message = "收益截止日不能为空")
	private Date incomeEndDate;

	/**
	 * 付息日
	 */
	@NotNull(message = "投资标的付息日不能为空")
	private Integer accrualDate;
	private String operator;
}
