package com.guohuai.mmp.investor.sonoperate;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.guohuai.component.persist.UUID;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_money_investor_son_operate")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class SonOperateEntity extends UUID implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String action;
	
	private String sid;
	
	private String bankorderoid;
	
	private String pid;
}
