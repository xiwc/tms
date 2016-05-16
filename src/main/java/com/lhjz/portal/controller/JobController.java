/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.controller;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Job;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.JobForm;
import com.lhjz.portal.repository.JobRepository;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping("admin/job")
public class JobController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	JobRepository jobRepository;

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	public RespBody save(@Valid JobForm jobForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Job job = new Job();
		job.setCategory(jobForm.getCategory());
		job.setSize(jobForm.getSize());
		job.setDuty(jobForm.getDuty());
		job.setEducation(jobForm.getEducation());
		job.setExperience(jobForm.getExperience());
		job.setLabels(jobForm.getLabels());
		job.setSalary(jobForm.getSalary());


		Job job2 = jobRepository.saveAndFlush(job);

		log(Action.Create, Target.Job, job2);

		return RespBody.succeed(job2);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public RespBody delete(@RequestParam("id") Long id) {

		jobRepository.delete(id);

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public RespBody update(@RequestParam("id") Long id, @Valid JobForm jobForm,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Job job = jobRepository.findOne(id);

		if (job == null) {
			logger.error("更新职位信息不存在, ID: {}", id);
			return RespBody.failed("更新职位信息不存在!");
		}

		job.setCategory(jobForm.getCategory());
		job.setSize(jobForm.getSize());
		job.setDuty(jobForm.getDuty());
		job.setEducation(jobForm.getEducation());
		job.setExperience(jobForm.getExperience());
		job.setLabels(jobForm.getLabels());
		job.setSalary(jobForm.getSalary());

		Job job2 = jobRepository.saveAndFlush(job);

		log(Action.Update, Target.Job, job2);

		return RespBody.succeed(job2);
	}

	@RequestMapping(value = "get", method = RequestMethod.POST)
	@ResponseBody
	public RespBody get(@RequestParam("id") Long id) {
		return RespBody.succeed(jobRepository.findOne(id));
	}

}
