package com.guohuai.mmp.jiajiacai.wishplan.risklevel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_RISKLEVEL_ENTITY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class RiskLevelEntity implements Serializable {

	private static final long serialVersionUID = -6727999259658566877L;
	@Id
	private String oid;
	@NotNull
	private String userOid;
	// R1: 谨慎型\n            R2: 稳健型\n            R3: 平衡型\n            R4: 进取型\n            R5: 激进型\n
	@NotNull
	private String riskLevel;

}
