package com.guohuai.mmp.platform.publisher.product.offset;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FindsOidRep<T> extends BaseResp {
    
    List<T> rows = new ArrayList<T>();
    public void add(T e) {
        rows.add(e);
    }
    /**
     * 总可用金
     */
    private BigDecimal totalAvailableAmount;
    
    /**
     * 总现金类
     */
    private BigDecimal totalCashCategory;
    
    /**
     * 总非现金类
     */
    private BigDecimal totalNonCashCategory;
    
    /**
     * 总计
     */
    private BigDecimal totalSubtotal;
}