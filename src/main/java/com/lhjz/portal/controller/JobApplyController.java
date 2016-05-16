/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.JobApply;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.repository.JobApplyRepository;
import com.lhjz.portal.util.EnumUtil;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping("admin/jobApply")
public class JobApplyController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(JobApplyController.class);

	@Autowired
	JobApplyRepository jobApplyRepository;

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public RespBody delete(@RequestParam("id") Long id) {

		JobApply jobApply = jobApplyRepository.findOne(id);

		if (jobApply == null) {
			logger.error("删除职位申请信息不存在! ID: {}", id);
			return RespBody.failed("删除职位申请信息不存在!");
		}

		if (jobApply.getStatus() == Status.Bultin) {
			logger.error("内置职位申请信息, 删除失败! ID: {}", id);
			return RespBody.failed("内置职位申请信息, 删除失败!");
		}

		jobApplyRepository.delete(id);

		// TODO 是否要删除上传的简历文件 路径: upload/job/

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public RespBody update(@RequestParam("id") Long id,
			@RequestParam("status") String status) {

		JobApply jobApply = jobApplyRepository.findOne(id);

		if (jobApply == null) {
			logger.error("更新职位申请信息不存在, ID: {}", id);
			return RespBody.failed("更新职位申请信息不存在!");
		}

		Status status2 = EnumUtil.status(status);

		if (status2 == Status.Unknow) {
			logger.error("更新职位申请状态不存在, ID: {}", id);
			return RespBody.failed("更新职位申请状态不存在!");
		}

		jobApply.setStatus(status2);

		JobApply jobApply2 = jobApplyRepository.saveAndFlush(jobApply);

		log(Action.Update, Target.JobApply, jobApply2);

		return RespBody.succeed(jobApply2);
	}

	@RequestMapping(value = "get", method = RequestMethod.POST)
	@ResponseBody
	public RespBody get(@RequestParam("id") Long id) {
		return RespBody.succeed(jobApplyRepository.findOne(id));
	}

}
