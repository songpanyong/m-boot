package com.guohuai.component.web.view;
import java.util.ArrayList;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RowsRep<T> extends BaseResp {
    
    List<T> rows = new ArrayList<T>();
    public void add(T e) {
        rows.add(e);
    }
}