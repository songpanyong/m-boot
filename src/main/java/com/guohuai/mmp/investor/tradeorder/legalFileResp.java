package com.guohuai.mmp.investor.tradeorder;


import java.util.ArrayList;
import java.util.List;


import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.file.legal.file.LegalFileResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
public class legalFileResp extends BaseResp{


	
	private List<LegalFileResp> File = new ArrayList<LegalFileResp>();// 协议
	
	private Object data ;
}
