package com.guohuai.mmp.platform.publisher.dividend.offset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.product.Product;
import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 平台-红利-轧差
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_PLATFORM_DIVIDEND_OFFSET")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class DividendOffsetEntity extends UUID implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7766004662127018449L;
	
	//'toClose:待结算，closing:结算中，closed:已结算，closeSubmitFailed:结算申请失败，closePayFailed:结算支付失败'
	public static final String OFFSET_closeStatus_toClose = "toClose";
	public static final String OFFSET_closeStatus_closing = "closing";
	public static final String OFFSET_closeStatus_closed = "closed";
	public static final String OFFSET_closeStatus_closeSubmitFailed = "closeSubmitFailed";
	public static final String OFFSET_closeStatus_closePayFailed = "closePayFailed";
	
	
	
	@JoinColumn(name = "productOid", referencedColumnName = "oid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;
	
	
	/**
	 * 红利日期
	 */
	private Date dividendDate;

	/**
	 * 红利金额
	 */
	private BigDecimal dividendAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 待结算红利订单笔数
	 */
	private Integer toCloseDividendNumber = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 结算响应信息
	 */
	private String message;
	
	/**
	 * 红利结算状态
	 */
	private String dividendCloseStatus;

	private Timestamp updateTime;
	private Timestamp createTime;
}
