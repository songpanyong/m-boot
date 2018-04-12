package com.guohuai.mmp.investor.tradeorder;

import java.util.ArrayList;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

@lombok.Data
public class LegalTypeResp<T> extends BaseResp {


   private List<T> list = new ArrayList<T>();
}
