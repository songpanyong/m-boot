package com.guohuai.component.api.cms;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ElementEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 编号 */
	private String code;
	
	/** 名称 */
	private String name;
	
	/** 内容 */
	private String content;
}
