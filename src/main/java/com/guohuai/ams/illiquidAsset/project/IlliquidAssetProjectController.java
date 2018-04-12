
package com.guohuai.ams.illiquidAsset.project;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/illiquidAsset/project", produces = "application/json;charset=UTF-8")
public class IlliquidAssetProjectController extends BaseController {
	@Autowired
	private IlliquidAssetProjectService illiquidAssetProjectService;

	@RequestMapping(name = "查询底层项目", value = "projectlist", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<IlliquidAssetProjectListResp> projectlist(HttpServletRequest request, @And({ @Spec(params = "targetOid", path = "illiquidAsset.oid", spec = Equal.class), @Spec(params = "projectName", path = "projectName", spec = Like.class), @Spec(params = "projectManager", path = "projectManager", spec = Like.class), @Spec(path = "projectType", params = "projectType", spec = Equal.class) }) Specification<IlliquidAssetProject> spec,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int size, @RequestParam(defaultValue = "updateTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		if (page < 1) {
			page = 1;
		}
		if (size <= 0) {
			size = 50;
		}

		Pageable pageable = new PageRequest(page - 1, size, new Sort(new Order(sortDirection, sortField)));

		IlliquidAssetProjectListResp resp = illiquidAssetProjectService.queryPage(spec, pageable);

		return new ResponseEntity<IlliquidAssetProjectListResp>(resp, HttpStatus.OK);
	}

	/**
	 * 创建底层项目
	 * 
	 * @Title: save
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param projectForm
	 * @return CommonResp 返回类型
	 */
	@RequestMapping(name = "保存底层项目", value = "save", method = RequestMethod.POST)
	public BaseResp save(@Valid IlliquidAssetProjectForm projectForm) {

		projectForm.setCreator(super.getLoginUser());
		projectForm.setOperator(super.getLoginUser());
		this.illiquidAssetProjectService.save(projectForm);
		return new BaseResp();
	}

	/**
	 * 删除底层项目
	 * 
	 * @Title: deleteProject
	 * @version 1.0
	 * @see:
	 * @param approvalReq
	 * @return BaseResp 返回类型
	 */
	@RequestMapping(name = "删除底层项目", value = "deleteProject", method = { RequestMethod.POST, RequestMethod.GET })
	public BaseResp deleteProject(@RequestParam String oid) {
		illiquidAssetProjectService.deleteByOid(oid);
		return new BaseResp();
	}

	/**
	 * 根据标的id查询底层项目
	 * 
	 * @Title: getByTargetId
	 * @version 1.0
	 * @see:
	 * @param targetOid
	 * @return
	 * @return CommonResp 返回类型
	 */
	@RequestMapping(name = "根据标的id查询底层项目", value = "getByIlliquidAssetOid", method = { RequestMethod.POST, RequestMethod.GET })
	public PageResp<IlliquidAssetProject> getByTargetId(@RequestParam(required = true) String illiquidAssetOid) {
		List<IlliquidAssetProject> list = this.illiquidAssetProjectService.findByIlliquidAssetOid(illiquidAssetOid);
		PageResp<IlliquidAssetProject> pageResp = new PageResp<>(null == list ? 0 : list.size(), list);
		return pageResp;
	}

	/**
	 * 根据项目id查询底层项目
	 * 
	 * @Title: getByOid
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param oid
	 * @return
	 * @return CommonResp 返回类型
	 */
	@RequestMapping(name = "根据项目id查询底层项目", value = "detail", method = { RequestMethod.POST, RequestMethod.GET })
	public IlliquidAssetProjectResp getByOid(@RequestParam(required = true) String oid) {
		IlliquidAssetProject prj = this.illiquidAssetProjectService.findByOid(oid);
		IlliquidAssetProjectResp resp = new IlliquidAssetProjectResp(prj);
		return resp;
	}

}
