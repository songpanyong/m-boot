package com.guohuai.mmp.serialtask;


import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 串行任务执行
 * 
 * @author yuechao
 *
 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class SerialTaskRep  extends BaseResp {
	
	SerialTaskEntity taskEn;
	
	

}
