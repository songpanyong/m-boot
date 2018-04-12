package com.guohuai.mmp.job.lock;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;

import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;


@RestController
@RequestMapping(value = "/mimosa/job/", produces = "application/json")
public class JobLockController {
		
		@Autowired
		JobLockService jobLockService;
	
		/**
		 * 查询定时任务列表
		 */
		@RequestMapping(value = "list", method = { RequestMethod.GET, RequestMethod.POST })
		@ResponseBody
		public ResponseEntity<PageResp<JobLockEntityResp>> getMoneyJobLockList(
				HttpServletRequest request,
				@And({@Spec(params = "jobId", path = "jobId", spec = Like.class)}) Specification<JobLockEntity> spec,
				@RequestParam int page, @RequestParam int rows){
			
			Pageable pageable = new PageRequest(page - 1, rows);
			PageResp<JobLockEntityResp> rep = this.jobLockService.findAll(spec, pageable);
			
			return new ResponseEntity<PageResp<JobLockEntityResp>>(rep, HttpStatus.OK);	 
		}
		
		/**
		 * 根据jobId执行定时任务
		 */
		@RequestMapping(value = "executetack", method = { RequestMethod.GET, RequestMethod.POST })
		public @ResponseBody ResponseEntity<BaseResp> executeTask(@RequestParam Date inDate,@RequestParam String jobId,@RequestParam String token,HttpServletRequest request) {
			BaseResp rep = new BaseResp();
			try {
				JobLockEntity jobLockEntity = jobLockService.findByJobId(jobId);
				if(JobLockEntity.JOB_jobStatus_toRun.equals(jobLockEntity.getJobStatus())){
					this.jobLockService.snapshot(inDate,jobId);
				}else if(JobLockEntity.JOB_jobStatus_processing.equals(jobLockEntity.getJobStatus())){
					//rep.setErrorCode(HttpStatus.FORBIDDEN.value());
					rep.setErrorMessage("任务执行中。。。");
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
		}
		

}
