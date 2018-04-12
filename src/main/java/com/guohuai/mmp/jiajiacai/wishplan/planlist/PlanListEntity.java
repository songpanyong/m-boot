package com.guohuai.mmp.jiajiacai.wishplan.planlist;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 所有的计划
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_PLANLIST_ENTITY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class PlanListEntity implements Serializable {
	
	private static final long serialVersionUID = 1028814416903831639L;
	
	@Id
	private String oid;
	@NotNull
	@Length(max=20)
	private String name;
	@NotNull
	@Length(max=500)
	private String desc;
	@NotNull
	private int orders;
	private String planType;
}
