package com.guohuai.mmp.jiajiacai.wishplan.risklevel;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.common.exception.ErrorMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "心愿计划 - 风险评测")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/mimosa/wishplan/risk", produces = "application/json")
public class RiskLevelController extends BaseController {

	@Autowired
	private RiskLevelService riskAssessmentService;

	@ApiOperation(value = "计算风险评测分数", notes = "风险评估用以确定投资者的风险接受程度 ")
	@RequestMapping(value = "calculateScore", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<RiskLevelForm> calculateScore(@NotNull @RequestParam("userOid") String userOid,
			@NotNull @RequestParam("answer1") String answer1, @NotNull @RequestParam("answer2") String answer2,
			@NotNull @RequestParam("answer3") String answer3, @NotNull @RequestParam("answer4") String answer4,
			@NotNull @RequestParam("answer5") String answer5, @NotNull @RequestParam("answer6") String answer6)
			 {

		Map<String, Float> map = new HashMap<String, Float>();
		map.put("A", 2.5F);
		map.put("B", 8.5F);
		map.put("C", 12F);
		map.put("D", 15F);
		map.put("E", 30F);
		Float score = 0F;

		if (map.containsKey(answer1) && map.containsKey(answer2) && map.containsKey(answer3) && map.containsKey(answer4)
				&& map.containsKey(answer5) && map.containsKey(answer6)) {
			score = map.get(answer1) + map.get(answer2) + map.get(answer3) + map.get(answer4) + map.get(answer5)
					+ map.get(answer6);
		} else {
			//throw new BaseException(ErrorMessage.PRAMETER_ERROR);
			throw new AMPException(ErrorMessage.PRAMETER_ERROR);
		}
		String level = caculateRiskLevel(score);
		RiskLevelForm form = null;
		if (userOid == null) {
			//form = new RiskLevelForm(userOid, "-1", "");
			throw new AMPException("账户不存在");
			//return new ResponseEntity<RiskLevelForm>(form, HttpStatus.OK);
		}

		RiskLevelEntity risklevel = riskAssessmentService.saveRiskLevel(userOid, level);
		if (risklevel == null) {
			//form = new RiskLevelForm(userOid, "-1", "");
			throw new AMPException("该账户未进行风险测评");
		} else {
			String levelName = setLevelName(risklevel.getRiskLevel());
			form = new RiskLevelForm(risklevel.getUserOid(), risklevel.getRiskLevel(), levelName);
		}
		return new ResponseEntity<RiskLevelForm>(form, HttpStatus.OK);
	}

	@ApiOperation(value = "是否做过风险评测", notes = "是否做过风险评测")
	@RequestMapping(value = "checkUserRiskLevel", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<RiskLevelForm> checkUserRiskLevel(@NotNull @RequestParam("userOid") String userOid) {
		RiskLevelForm form = null;
		if (userOid == null) {
//			form = new RiskLevelForm(userOid, "-1", "");
//			return new ResponseEntity<RiskLevelForm>(form, HttpStatus.OK);
			throw new AMPException("账户不存在");
		}
		RiskLevelEntity risklevel = riskAssessmentService.queryUserRiskLevel(userOid);

		if (risklevel == null) {
			//form = new RiskLevelForm(userOid, "-1", "");
			throw new AMPException("该账户未进行风险测评");
		} else {
			String levelName = setLevelName(risklevel.getRiskLevel());
			form = new RiskLevelForm(risklevel.getUserOid(), risklevel.getRiskLevel(), levelName);
		}
		return new ResponseEntity<RiskLevelForm>(form, HttpStatus.OK);
	}

	/**
	 * 根据level 设置 leveName <br>
	 * R1: 谨慎型 R2: 稳健型 R3: 平衡型 R4: 进取型 R5: 激进型
	 * 
	 * @param riskLevel
	 * @return
	 */
	private String setLevelName(String riskLevel) {
		String name = "";
		if (riskLevel.equals("R1")) {
			name = "温和型";
		} else if (riskLevel.equals("R2")) {
			name = "稳健型";
		} else if (riskLevel.equals("R3")) {
			name = "平衡型";
		} else if (riskLevel.equals("R4")) {
			name = "积极型";
		} else if (riskLevel.equals("R5")) {
			name = "冒险型";
		} else {

		}
		return name;
	}

	/**
	 * 根据评测的分数计算风险等级
	 * 
	 * @param score
	 * @return
	 * @throws BaseException
	 */
	private String caculateRiskLevel(Float score) {
		String level = "";
		if (score == null || score < 0) {
			//throw new BaseException(ErrorMessage.PRAMETER_ERROR);
			throw new AMPException(ErrorMessage.PRAMETER_ERROR);
		}

		if (score < 16) {
			level = "R1";
		} else if (score < 28) {
			level = "R2";
		} else if (score < 60) {
			level = "R3";
		} else if (score < 90) {
			level = "R4";
		} else if (score < 100) {
			level = "R5";
		}
		return level;
	}

}
